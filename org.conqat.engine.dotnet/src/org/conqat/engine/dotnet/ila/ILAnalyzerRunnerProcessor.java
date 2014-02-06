/*--------------------------------------------------------------------------+
$Id: ILAnalyzerRunnerProcessor.java 47119 2013-12-05 14:22:17Z juergens $
|                                                                          |
| Copyright 2005-2010 Technische Universitaet Muenchen                     |
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
+--------------------------------------------------------------------------*/
package org.conqat.engine.dotnet.ila;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.dotnet.BundleContext;
import org.conqat.engine.dotnet.DotnetExecutorBase;
import org.conqat.engine.resource.IElement;
import org.conqat.engine.resource.IResource;
import org.conqat.engine.resource.util.ResourceTraversalUtils;
import org.conqat.engine.resource.util.ResourceUtils;
import org.conqat.lib.commons.date.DateUtils;
import org.conqat.lib.commons.filesystem.FileExtensionFilter;
import org.conqat.lib.commons.filesystem.FileSystemUtils;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: juergens $
 * @version $Revision: 47119 $
 * @ConQAT.Rating GREEN Hash: AD85FBD8EE7ACBDB5DAF8B2B82EDCBC0
 */
@AConQATProcessor(description = ""
		+ "Runs the Intermediate Language Analyzer (ILA) on a bunch of assemblies. The "
		+ "ILA extracts the dependencies between the types in the assemblies and writes "
		+ "them to XML files, one per analyzed assembly. The name of the resulting XML "
		+ "file <assembly-name>.xml: Analysis of the file application.dll creates and "
		+ "XML file application.dll.xml. "
		+ "This processor is typically executed before a processor that performs an "
		+ "architectural analysis. "
		+ "The assemblies that are to be analyzed can be specified individually. "
		+ "Alternatively, the processor can be given a resource hierarchy and then "
		+ "simply processes all contained files. This only works for files that are stored"
		+ "on the local disk."
		+ "By default, the processor only executes the ILA, if no up-to-date XML file for "
		+ "an assembly exists. It can be forced to recompute all assemblies via the parameter"
		+ "'enforce.recompute-all', however.")
public class ILAnalyzerRunnerProcessor extends DotnetExecutorBase {

	/** List of all the assemblies that are to be analyzed */
	private final List<File> assemblies = new ArrayList<File>();

	/** Folder into which XML gets written */
	private File xmlTargetFolder;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "enforce", attribute = "recompute-all", optional = true, description = ""
			+ "If set, ILA is executed on all assemblies, independent of whether up-to-date XML files for them exist")
	public boolean enforceRecomputeAll = false;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "exclude", attribute = "members", optional = true, description = ""
			+ "If set, ILA does not include member body information into the XML, which is not required for architecture analysis. Default is true.")
	public boolean excludeMembers = true;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "plain-method-names", attribute = "use", optional = true, description = ""
			+ "If set, ILA does not normalize method names, which is not required for architecture analysis. Default is false.")
	public boolean usePlainMethodNames = false;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "assembly", description = "Fully qualified name of the assembly that gets analysed", minOccurrences = 0, maxOccurrences = -1)
	public void addAssembly(
			@AConQATAttribute(name = "name", description = "Fully qualified name of the assembly that gets analysed") String filename) {
		assemblies.add(new File(filename));
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = ConQATParamDoc.INPUT_NAME, description = "File system scope that contains assemblies that get analyzed", minOccurrences = 0, maxOccurrences = 1)
	public void setInput(
			@AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = "All files that are contained in the scope are analyzed. Make sure it only contains assemblies.") IResource root)
			throws ConQATException {
		for (IElement element : ResourceTraversalUtils.listElements(root,
				IElement.class)) {
			assemblies.add(ResourceUtils
					.ensureFile(element, getProcessorInfo()));
		}
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "xml", description = "Folder the analysis result xml files are written to", minOccurrences = 1, maxOccurrences = 1)
	public void setXmlTargetFolder(
			@AConQATAttribute(name = "folder", description = "Folder the analysis result xml files are written to") String targetFolder) {
		xmlTargetFolder = new File(targetFolder);
	}

	/** Run assembly on input. */
	@Override
	public String process() throws ConQATException {
		setExecutable(BundleContext.getInstance().getResourceManager()
				.getAbsoluteResourcePath("ILAnalyzer.exe"));
		// abort if no assembly can be found
		if (assemblies.size() == 0) {
			throw new ConQATException(
					"No assembly found. Cannot run on empty input.");
		}
		doExecute();
		return xmlTargetFolder.getAbsolutePath();
	}

	/** Creates the command line arguments for analyzing the given file. */
	private List<String> createCommandLineArguments(File file) {
		List<String> arguments = new ArrayList<String>();
		arguments.add("-in");
		String inPath = file.getAbsolutePath();
		arguments.add(inPath);

		arguments.add("-out");
		outputFile = xmlFileFor(file).getAbsolutePath();
		arguments.add(outputFile);

		if (excludeMembers) {
			arguments.add("-excludeMembers");
		}
		if (usePlainMethodNames) {
			arguments.add("-usePlainMethodNames");
		}
		return arguments;
	}

	/** Construct path of XML file for assembly */
	private File xmlFileFor(File assembly) {
		return new File(xmlTargetFolder, xmlNameFor(assembly.getName()));
	}

	/**
	 * Construct name of XML file for assembly. This method is package-visible,
	 * to make it accessible from test code.
	 */
	/* package */static String xmlNameFor(String assemblyName) {
		return assemblyName + ".xml";
	}

	/** Execute the analysis of the assemblies. */
	private void doExecute() throws ConQATException {
		Set<File> superfluousXmlFiles = existingXmlFiles();

		for (File assembly : assemblies) {
			if (enforceRecomputeAll || !uptodateXmlFileExists(assembly)) {
				execute(createCommandLineArguments(assembly));
			}
			superfluousXmlFiles.remove(xmlFileFor(assembly));
		}

		deleteFiles(superfluousXmlFiles);
	}

	/** Returns set of all XML files in target directory */
	private Set<File> existingXmlFiles() {
		return new HashSet<File>(FileSystemUtils.listFilesRecursively(
				xmlTargetFolder, new FileExtensionFilter("xml")));
	}

	/**
	 * Checks whether an up-to-date XML file for the assembly already exists in
	 * the output directory
	 */
	private boolean uptodateXmlFileExists(File assembly) {
		warnIfLastModifiedInTheFuture(assembly);

		File xmlFile = xmlFileFor(assembly);
		if (xmlFile.canRead()
				&& xmlFile.lastModified() > assembly.lastModified()) {
			return true;
		}
		return false;
	}

	/**
	 * Creates a warning, if the last modification time of a file is in the
	 * future.
	 */
	private void warnIfLastModifiedInTheFuture(File file) {
		if (file.lastModified() > DateUtils.getNow().getTime()) {
			getLogger()
					.warn("Modification time of file "
							+ file
							+ " is in the future. This can disable incremental execution of the IL Analyzer");
		}
	}

	/** Delete set of files */
	private void deleteFiles(Set<File> superfluousXmlFiles)
			throws ConQATException {
		for (File xmlFile : superfluousXmlFiles) {
			try {
				FileSystemUtils.deleteFile(xmlFile);
			} catch (IOException e) {
				throw new ConQATException("Could not delete file: " + xmlFile,
						e);
			}
		}
	}

}