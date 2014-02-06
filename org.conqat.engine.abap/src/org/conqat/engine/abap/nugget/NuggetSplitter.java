/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2011 The ConQAT Project                                   |
|                                                                          |
| Licensed under the Apache License, Version 2.0 (the "License");          |
| you may not use this file except in compliance with the License.         |
| You may obtain a copy of the License at                                  |
|                                                                          |
|    http://www.apache.org/licenses/LICENSE-2.0                            |
|                                                                          |
| Unless required by applicable law or agreed to in writing, software      |
| distributed under the License is distributed on an "AS IS" BASIS,        |
| WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. |
| See the License for the specific language governing permissions and      |
| limitations under the License.                                           |
+-------------------------------------------------------------------------*/
package org.conqat.engine.abap.nugget;

import static org.conqat.engine.abap.nugget.ENuggetElementType.CLAS;
import static org.conqat.engine.abap.nugget.ENuggetElementType.FUGR;
import static org.conqat.engine.abap.nugget.ENuggetElementType.MSAG;
import static org.conqat.engine.abap.nugget.ENuggetElementType.PROG;
import static org.conqat.engine.abap.nugget.ENuggetElementType.WDYN;
import static org.conqat.engine.abap.nugget.ENuggetElementType.valueOf;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.conqat.engine.commons.util.ConQATInputProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.IElement;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.text.ITextResource;
import org.conqat.engine.resource.util.ResourceTraversalUtils;
import org.conqat.engine.resource.util.UniformPathUtils;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.filesystem.CanonicalFile;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.commons.xml.XMLUtils;
import org.conqat.lib.commons.xml.XPathEvaluator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: poehlmann $
 * @version $Rev: 45197 $
 * @ConQAT.Rating GREEN Hash: 886BF8F0D7A3124E60FED6517A262D35
 */
@AConQATProcessor(description = "This processor extracts SAP source code files "
		+ "from nugget files, and writes them to a target directory. "
		+ "Nugget files can be exported from an SAP Application Server with "
		+ "the help of <a href=\"http://code.google.com/p/saplink/\">SAPLink</a>. "
		+ "The processor returns the target directory."
		+ "For each nugget file, a directory is created where the source files "
		+ "are written to. Sub-folders may be created to resemble a package "
		+ "hierarchy, depending on parameters from the name of the source artifact "
		+ "to be stored in a file. "
		+ "Futhermore the processer differentiates betweend ABAP and DynPro flow "
		+ "logic language. ABAP code is stored in *.abap files, where DynPro code "
		+ "is stored in *.dynpro files.")
public class NuggetSplitter extends ConQATInputProcessorBase<ITextResource> {

	/** Constant for the name attribute within function group elements */
	private static final String FUGR_NAME_ATTRIBUTE = "NAME";

	/** Target directory to which ABAP files are written. */
	private File targetDir;

	/** Element types. */
	private final Set<ENuggetElementType> elementTypes = CollectionUtils
			.asHashSet(ENuggetElementType.values());

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "packages-by", attribute = "seperator", optional = true, description = ""
			+ "Separator string for identifying packages hierarchy within file names, usually '_'. "
			+ "Sub-packages will only be created if this parameter is set.")
	public String packageSeperator = null;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "package-depth", attribute = "max-depth", optional = true, description = ""
			+ "Maximum depth of package hierarchy. If limit is reached, no further package strucutre is "
			+ "created, all remaining files will be stored in the directory at the diven depth-limit."
			+ "Set to -1 for no limit (default).")
	public int maxPackageDepth = -1;

	/**
	 * {@link Map} that stores the maximum package name length for package
	 * levels. The key is the package level, the value the maximum length for
	 * package name on this level.
	 */
	private final Map<Integer, Integer> packageIdentifierLengthMap = new HashMap<Integer, Integer>();

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "target", minOccurrences = 1, maxOccurrences = 1, description = "Target directory")
	public void setTargetDir(
			@AConQATAttribute(name = "dir", description = "Target directory to which ABAP files are written") String targetDirName) {
		targetDir = new File(targetDirName);
	}

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "recover-from-parsing-exceptions", attribute = "value", description = ""
			+ "Flag for recovering form expections which occur during nugget parsing. If true, only a warning will be logged.", optional = true)
	public boolean ignoreParsingException = false;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "delete-target-dir", attribute = "value", description = ""
			+ "Flag for indicating if the target directory should be deleted "
			+ "before extracting source files. ", optional = true)
	public boolean deleteTargetDirectory = false;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "override-encoding", attribute = "name", optional = true, description = ""
			+ "Set the correct encoding for the nugget file since nugget may state wrong encoding."
			+ "If not set, encoding stated in the nugget is used.")
	public String encodingFix = null;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "remove", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "Remove a certain type")
	public void setAddAll(
			@AConQATAttribute(name = "type", description = "Element type") ENuggetElementType type) {
		elementTypes.remove(type);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "package-seperation-by-prefix", minOccurrences = 0, maxOccurrences = -1, description = ""
			+ "This parameter allows to define packages not by a sperator char but by a prefix of a given length. "
			+ "It defines the maximum length of package names on a distinct package hierarchy level. "
			+ "On the specified level, a package name should be of maximum maxLength characters, "
			+ "also if package name is not seperated by a package sperator character. "
			+ "The remaining characters are used fo package structering / file naming on folowoing levels.")
	public void addPackageIdentifierLength(
			@AConQATAttribute(name = "level", description = "package hierarchie level") int level,
			@AConQATAttribute(name = "maxLength", description = "maximum package name length") int maxLength) {
		packageIdentifierLengthMap.put(level, maxLength);
	}

	/** {@inheritDoc} */
	@Override
	public String process() {
		for (ITextElement nuggetElement : ResourceTraversalUtils
				.listTextElements(input)) {
			try {
				processNuggetElement(nuggetElement);
			} catch (ConQATException e) {
				getLogger().error(
						"Error while splitting nugget "
								+ nuggetElement.getName(), e);
			}
		}

		return targetDir.getAbsolutePath();
	}

	/** Process single nugget element */
	private void processNuggetElement(ITextElement node) throws ConQATException {
		File nuggetTargetDir = getNuggetTargetDir(node);

		deleteTargetDirectory(nuggetTargetDir);
		checkTargetDirectoryEmpty(nuggetTargetDir);
		try {
			FileSystemUtils.ensureDirectoryExists(nuggetTargetDir);
		} catch (IOException e) {
			throw new ConQATException("Cannot create target directory "
					+ nuggetTargetDir, e);
		}

		Document document = parseNugget(node);
		write(nuggetTargetDir, document.getDocumentElement());
	}

	/**
	 * Deletes the target directory if {@link #deleteTargetDirectory} parameter
	 * is set.
	 */
	private void deleteTargetDirectory(File nuggetTargetDir) {
		if (deleteTargetDirectory && nuggetTargetDir.exists()) {
			FileSystemUtils.deleteRecursively(nuggetTargetDir);
		}
	}

	/**
	 * Checks if the target directory is empty from existing source files. If
	 * not empty a warning is logged.
	 */
	private void checkTargetDirectoryEmpty(File nuggetTargetDir) {
		List<File> existingFiles = FileSystemUtils.listFilesRecursively(
				nuggetTargetDir, NuggetSource.SOURCE_FILE_FILTER);

		if (!existingFiles.isEmpty()) {
			getLogger()
					.warn(existingFiles.size()
							+ "Source files already exist in target directory "
							+ nuggetTargetDir
							+ ".\n Analysis may include files which are not originating from the nugget. "
							+ "Set delete-target-dir parameter, if clean-up is required.");
		}
	}

	/** Parse the nugget. */
	private Document parseNugget(ITextElement node) throws ConQATException {
		Document document = null;
		try {
			// SAPLink does not escape line breaks in XML attribute values
			// according to the XML standard. The XML standard also defines
			// that parsers should replace line breaks by blanks. However,
			// this behavior is undesired, as we need the line breaks for the
			// code stored in attribute values. Therefore, this stream replaces
			// line breaks in attribute values by the intended escape character.
			InputStream byteInputStream = createFixedXmlInputStream(node);
			InputStream in = new SAPLinkXMLAttributeFixInputStream(
					byteInputStream);

			document = XMLUtils.parse(new InputSource(in));
		} catch (IOException e) {
			throw new ConQATException("Problems parsing nugget "
					+ node.getLocation() + " :", e);
		} catch (SAXException e) {
			throw new ConQATException("Problems parsing nugget "
					+ node.getLocation() + " :", e);
		}
		return document;
	}

	/**
	 * Creates a {@link InputStream} from the given nugget node where following
	 * XML violations in the nugget content are fixed:
	 * <ul>
	 * <li>Nuggets may state an incorrect encoding, set to encoding-fix
	 * parameter
	 * <li>Nuggets may contain <code>&#0;</code> escape sequence (ASCII 0) which
	 * is not permitted in XML. This escape sequence will be removed.
	 * </ul>
	 */
	private InputStream createFixedXmlInputStream(ITextElement node)
			throws ConQATException {
		String content = node.getTextContent();
		if (!StringUtils.isEmpty(encodingFix)) {
			content = content.replaceFirst(
					"^<\\?xml\\s([^>]*)encoding=\"[^\"]*\"([^>]*)\\?>",
					"<?xml $1encoding=\"" + encodingFix + "\"$2?>");
		}
		content = content.replaceAll("&#0;", "");
		content = stripNonValidXMLCharacters(content);
		ByteArrayInputStream byteInputStream = new ByteArrayInputStream(
				content.getBytes());
		return byteInputStream;

	}

	/**
	 * Ensures that the output String has only valid XML unicode characters as
	 * specified by the XML 1.0 standard. For reference, please see <a
	 * href="http://www.w3.org/TR/2000/REC-xml-20001006#NT-Char">the
	 * standard</a>. This method will return an empty String if the input is
	 * null or empty.
	 */
	private String stripNonValidXMLCharacters(String in) {
		StringBuffer out = new StringBuffer();

		if (in == null || ("".equals(in))) {
			return "";
		}

		char current;
		for (int i = 0; i < in.length(); i++) {
			current = in.charAt(i);
			if ((current == 0x9) || (current == 0xA) || (current == 0xD)
					|| ((current >= 0x20) && (current <= 0xD7FF))
					|| ((current >= 0xE000) && (current <= 0xFFFD))
					|| ((current >= 0x10000) && (current <= 0x10FFFF))) {
				out.append(current);
			}
		}

		return out.toString();
	}

	/**
	 * Handles exceptions occurring during nugget parsing. Depending on
	 * {@link #ignoreParsingException} a {@link ConQATException} is thrown or a
	 * warning is logged only.
	 */
	private void handleParsingException(String message, Exception e)
			throws ConQATException {
		if (ignoreParsingException) {
			getLogger().warn(message, e);
		} else {
			throw new ConQATException(message, e);
		}
	}

	/** Get the target directory for ABAP files contained in this nugget. */
	private File getNuggetTargetDir(IElement nuggetElement)
			throws ConQATException {
		String path = UniformPathUtils.stripProject(nuggetElement
				.getUniformPath());
		String extension = UniformPathUtils.getExtension(path);
		if (extension != null) {
			path = StringUtils.stripSuffix("." + extension, path);
		}

		try {
			return new CanonicalFile(targetDir, path);
		} catch (IOException e) {
			throw new ConQATException("Could not create canonical file", e);
		}
	}

	/** Write files for the root element to a target directory. */
	private void write(File nuggetTargetDir, Element element)
			throws ConQATException {
		List<Element> children = XMLUtils.elementNodes(element.getChildNodes());
		if (children.isEmpty()) {
			String nuggetName = getAttributeValue(element, "name");
			handleParsingException("Nugget '" + nuggetName + "' is empty.",
					null);
			return;
		}

		try {
			for (Element child : children) {
				if (ignore(child)) {
					continue;
				}

				// classes
				if (child.getNodeName().equals(CLAS.name())) {
					writeElement(nuggetTargetDir, child, CLAS.name(), "CLSNAME");
				}
				// message groups
				else if (child.getNodeName().equals(MSAG.name())) {
					writeElement(nuggetTargetDir, child, MSAG.name(), "ARBGB");
				}
				// programs
				else if (child.getNodeName().equals(PROG.name())) {
					writeElement(nuggetTargetDir, child, PROG.name(),
							FUGR_NAME_ATTRIBUTE);
				}
				// web DynPros
				else if (child.getNodeName().equals(WDYN.name())) {
					writeWebDynPro(nuggetTargetDir, child);
				}
				// function groups
				else if (child.getNodeName().equals(FUGR.name())) {
					writeFunctionGroup(nuggetTargetDir, child);
				}
			}
		} catch (IOException e) {
			throw new ConQATException("Error writing ABAP files:", e);
		}
	}

	/** Check whether an element should be ignored. */
	private boolean ignore(Element element) {
		String elementName = element.getNodeName();
		try {
			ENuggetElementType elementType = valueOf(elementName);
			if (elementTypes.contains(elementType)) {
				return false;
			}
		} catch (IllegalArgumentException e) {
			// ignore if element type is currently not supported
		}

		return true;
	}

	/**
	 * Write an element to an ABAP file.
	 * 
	 * @param nuggetTargetDir
	 *            The target directory to which the files of a nugget are
	 *            written
	 * @param element
	 *            The element
	 * @param subDirectory
	 *            The prefix of the filename
	 * @param nameAttribute
	 *            The attribute to access the name of the element
	 */
	private void writeElement(File nuggetTargetDir, Element element,
			String subDirectory, String nameAttribute) throws IOException {
		String fileName = getMandatoryAttributeValue(element, nameAttribute);
		writeFile(new File(nuggetTargetDir, subDirectory), fileName, retrieveSourceContent(element));
	}

	/**
	 * Retrieves the source code content from the given element.
	 */
	private NuggetSource retrieveSourceContent(Element element) {
		return new NuggetSource(element);
	}

	/** Write files for a WebDynPro. */
	private void writeWebDynPro(File nuggetTargetDir, Element element)
			throws IOException {
		List<Element> children = getElements(element,
				"controller_definition/wdy_ctlr_compo");
		for (Element child : children) {
			writeWebDynProControllerComponent(nuggetTargetDir, child);
		}
	}

	/** Get elements based on an XPath expression. */
	private List<Element> getElements(Element element, String xpath) {
		NodeList childrenList = new XPathEvaluator().selectNodeList(xpath,
				element);
		return XMLUtils.elementNodes(childrenList);
	}

	/** Write files for a WebDynPro controller component. */
	private void writeWebDynProControllerComponent(File nuggetTargetDir,
			Element element) throws IOException {
		String code = getAttributeValue(element, "CODE_BODY");
		if (code != null) {
			String fileName = WDYN.name() + "_"
					+ getMandatoryAttributeValue(element, "COMPONENT_NAME")
					+ "_"
					+ getMandatoryAttributeValue(element, "CONTROLLER_NAME")
					+ "_" + getMandatoryAttributeValue(element, "CMPNAME");
			writeFile(nuggetTargetDir, fileName, code.trim());
		}
	}

	/** Write files for a function group. */
	private void writeFunctionGroup(File nuggetTargetDir, Element element)
			throws IOException {

		List<Element> children = getElements(element, "mainprogram");
		for (Element child : children) {
			writeElement(nuggetTargetDir, child, "FUGR-PROG",
					FUGR_NAME_ATTRIBUTE);
		}

		children = getElements(element, "includeprograms/include");
		for (Element child : children) {
			writeElement(nuggetTargetDir, child, "FUGR-INCL",
					FUGR_NAME_ATTRIBUTE);
		}

		children = getElements(element, "functionmodules/functionmodule");
		for (Element child : children) {
			writeElement(nuggetTargetDir, child, "FUGR-FMOD",
					FUGR_NAME_ATTRIBUTE);
		}
	}

	/** Write contents to a file. */
	private void writeFile(File targetDir, String fileName,
			NuggetSource sourceContent) throws IOException {
		fileName = tidyFileName(fileName);
		sourceContent.writeContentToFiles(targetDir, fileName);
	}

	/**
	 * Replace characters which are not allowed in file names.
	 */
	private String tidyFileName(String fileName) {
		fileName = fileName.replace('\\', '_');
		fileName = fileName.replace('/', '!');
		fileName = fileName.replace("<", "#lt#");
		fileName = fileName.replace(">", "#gt#");
		fileName = fileName.replace(":", "#colon#");
		fileName = fileName.replace("|", "#bar#");
		fileName = fileName.replace("?", "#questmark#");
		fileName = fileName.replace("*", "#asterik#");
		fileName = fileName.replace("\"", "#doubquot#");

		fileName = addPackageHierarchieToFilename(fileName);
		return fileName;
	}

	/** Write contents to a file. */
	private void writeFile(File targetDir, String fileName, String content)
			throws IOException {
		fileName = tidyFileName(fileName);
		File targetFile = new CanonicalFile(targetDir, fileName + "."
				+ NuggetSource.ABAP_FILE_EXTENSION);
		FileSystemUtils.writeFile(targetFile, content);
	}

	/**
	 * Extracts the package hierarchy from the given file name and adds it in
	 * front of the file name. The hierarchy is identified by
	 * {@link #packageSeperator} symbol. Every separator symbol will lead to a
	 * new package sub-directory. If attributes for maximum length of package
	 * names are set with {@link #addPackageIdentifierLength(int, int)}, a
	 * package is also created of the substring with maximum name length.
	 * <p>
	 * For example: <code>PROG_YABCDEF.abab</code> will lead to
	 * <code>PROG/YABC/PROG_YABCDEF.abab</code> if for level 2 a maximum package
	 * name length of 4 is configured.
	 * 
	 * @return file name with path for package hierarchy. If
	 *         {@link #packageSeperator} is not configured, the file name will
	 *         be unchanged and no package structure created.
	 */
	private String addPackageHierarchieToFilename(String fileName) {

		if (StringUtils.isEmpty(packageSeperator)) {
			return fileName;
		}

		String[] parts = fileName.split(packageSeperator, maxPackageDepth);
		if (parts.length < 2) {
			return fileName;
		}

		StringBuffer hierarchicalFileName = new StringBuffer();
		for (int packageLevel = 0; packageLevel < parts.length; packageLevel++) {

			Integer maxPackageNameLengthOnCurrentLevel = packageIdentifierLengthMap
					.get(packageLevel);

			boolean createPackageFromPrefixOnCurrentLevel = maxPackageNameLengthOnCurrentLevel != null
					&& parts[packageLevel].length() > maxPackageNameLengthOnCurrentLevel;

			if (createPackageFromPrefixOnCurrentLevel) {
				@SuppressWarnings("null")
				String start = parts[packageLevel].substring(0,
						maxPackageNameLengthOnCurrentLevel);
				String rest = parts[packageLevel]
						.substring(maxPackageNameLengthOnCurrentLevel);
				hierarchicalFileName.append(start);
				hierarchicalFileName.append('/');
				parts[packageLevel] = rest;
			}
			if (packageLevel < parts.length - 1) {
				hierarchicalFileName.append(parts[packageLevel]);
				hierarchicalFileName.append('/');
			}

		}
		hierarchicalFileName.append(fileName);

		return hierarchicalFileName.toString();
	}

	/**
	 * Get the value of an attribute of a node. Asserts that the attribute is
	 * actually set.
	 */
	private String getMandatoryAttributeValue(Element element, String name) {
		String value = getAttributeValue(element, name);

		CCSMAssert.isNotNull(value, "The XML element \"" + element.getTagName()
				+ "\" is expected to define an attribute \"" + name + "\"");
		return value;
	}

	/**
	 * Get the value of an attribute of a node. Return null, if the attribute is
	 * not set.
	 */
	private String getAttributeValue(Element element, String name) {
		Node attribute = element.getAttributes().getNamedItem(name);
		if (attribute == null) {
			return null;
		}
		return attribute.getNodeValue();
	}

}