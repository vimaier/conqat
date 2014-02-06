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
package org.conqat.engine.core.driver;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.conqat.engine.core.driver.declaration.BlockDeclaration;
import org.conqat.engine.core.driver.declaration.DeclarationAttribute;
import org.conqat.engine.core.driver.declaration.DeclarationParameter;
import org.conqat.engine.core.driver.declaration.IDeclaration;
import org.conqat.engine.core.driver.declaration.ProcessorDeclaration;
import org.conqat.engine.core.driver.error.BlockFileException;
import org.conqat.engine.core.driver.error.EDriverExceptionType;
import org.conqat.engine.core.driver.error.ErrorLocation;
import org.conqat.engine.core.driver.specification.BlockSpecification;
import org.conqat.engine.core.driver.specification.BlockSpecificationAttribute;
import org.conqat.engine.core.driver.specification.BlockSpecificationOutput;
import org.conqat.engine.core.driver.specification.BlockSpecificationParameter;
import org.conqat.engine.core.driver.specification.SpecificationLoader;
import org.conqat.engine.core.driver.util.IDocumentable;
import org.conqat.engine.core.driver.util.XmlToken;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.commons.xml.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This class is responsible for parsing the XML block file and creating the
 * initial configuration model.
 * <p>
 * This includes:
 * <ul>
 * <li>Reading block specifications (block-specs).</li>
 * <li>Reading blocks, parameters and attributes.</li>
 * <li>Reading processors, parameters and attributes.</li>
 * <li>Reading properties and applying.</li>
 * </ul>
 * The result is treated as another block specification and stored in the
 * {@link SpecificationLoader}.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 41766 $
 * @ConQAT.Rating GREEN Hash: 0850AC603E55F658454E76D5B4C8A3E0
 */
public class BlockFileReader {

	/**
	 * The specification loader used for adding block-specs. Additionally. it is
	 * provided to declarations.
	 */
	private final SpecificationLoader specLoader;

	/**
	 * Constructs a new config file reader.
	 * 
	 * @param specLoader
	 *            the specification loader used for adding block-specs and
	 *            provided to declarations.
	 */
	public BlockFileReader(SpecificationLoader specLoader) {
		this.specLoader = specLoader;
	}

	/**
	 * Reads the contents of the provided block file and returns the block
	 * specification.
	 */
	public BlockSpecification readBlockFile(URL blockURL)
			throws BlockFileException {
		try {
			return loadFromStream(blockURL.openStream(), null,
					new ErrorLocation(blockURL));
		} catch (IOException e) {
			throw new BlockFileException(EDriverExceptionType.IO_ERROR,
					"Could not read block from " + blockURL + ": "
							+ e.getMessage(), e, new ErrorLocation(blockURL));
		}
	}

	/**
	 * Reads the contents of the provided block file and returns the block
	 * specification.
	 */
	public BlockSpecification readBlockFile(File blockFile)
			throws BlockFileException {
		try {
			return loadFromStream(new FileInputStream(blockFile), blockFile,
					new ErrorLocation(blockFile));
		} catch (FileNotFoundException e) {
			throw new BlockFileException(EDriverExceptionType.IO_ERROR,
					"Could not read block from " + blockFile + ": "
							+ e.getMessage(), e, new ErrorLocation(blockFile));
		}
	}

	/**
	 * Reads the contents of the provided block and returns the block
	 * specification.
	 */
	public BlockSpecification readBlockFile(byte[] content)
			throws BlockFileException {
		return loadFromStream(new ByteArrayInputStream(content), null,
				ErrorLocation.UNKNOWN);
	}

	/**
	 * Loads a block specification from a stream.
	 * 
	 * @param in
	 *            the stream to read the block from. This method will also close
	 *            this stream.
	 * @param blockFile
	 *            the file associated with the block. This may be null.
	 */
	private BlockSpecification loadFromStream(InputStream in, File blockFile,
			ErrorLocation errorLocation) throws BlockFileException,
			AssertionError {
		Element rootNode = loadConqatElement(in, errorLocation);

		BlockSpecification result = null;
		for (Element element : XMLUtils.elementNodes(rootNode.getChildNodes())) {
			String elementName = element.getLocalName();
			if (elementName.equals(XmlToken.XML_ELEMENT_BLOCK_SPECIFICATION)) {
				// we know from the schema, that this can be only one
				result = readBlockSpec(element, blockFile, errorLocation);
			} else if (elementName.equals(XmlToken.XML_ELEMENT_META)) {
				// Although the assertion would be sufficient, we keep the check
				// to make the compiler happy (avoids warning)
				if (result != null) {
					result.addMeta(
							element.getAttribute(XmlToken.XML_ATTRIBUTE_TYPE),
							(Element) element.cloneNode(true));
				} else {
					CCSMAssert
							.fail("The schama should ensure that a block has been read before meta data");
				}
			} else {
				throw new BlockFileException(
						EDriverExceptionType.ILLEGAL_TAG_IN_BLOCK_FILE,
						"Illegal tag '" + elementName + ".", errorLocation);
			}
		}

		if (result == null) {
			throw new BlockFileException(
					EDriverExceptionType.MISSING_BLOCK_SPECIFICATION,
					"The block specification is missing for the block file.",
					errorLocation);
		}
		return result;
	}

	/**
	 * Read a block specification (block-spec) from the given XML element and
	 * returns it.
	 */
	private BlockSpecification readBlockSpec(Element specElement,
			File currentFile, ErrorLocation errorLocation)
			throws BlockFileException {
		String specName = specElement.getAttribute(XmlToken.XML_ATTRIBUTE_NAME);
		return readBlockSpecification(specElement, specName, currentFile,
				errorLocation);
	}

	/**
	 * Reads a block specification from an XML element and and returns it.
	 * 
	 * @param specElement
	 *            the root element of the specification.
	 * @param name
	 *            the name used for the block specification. This is not taken
	 *            from the element to allow the usage of this method for reading
	 *            the entire configuration (in which case the specElement is a
	 *            conqat element without the name attribute.
	 */
	private BlockSpecification readBlockSpecification(Element specElement,
			String name, File currentFile, ErrorLocation errorLocation)
			throws BlockFileException {
		BlockSpecification blockSpec = new BlockSpecification(name,
				currentFile, errorLocation);

		List<Element> childElements = XMLUtils.elementNodes(specElement
				.getChildNodes());
		extractDoc(childElements, blockSpec);

		Set<String> names = new HashSet<String>();
		for (Element element : childElements) {
			checkNaming(names, element, name, errorLocation);
			handleBlockSpecInnerElement(blockSpec, element);
		}
		return blockSpec;
	}

	/**
	 * Handles an inner element of a block specification.
	 * 
	 * @param enclosingBlockSpec
	 *            the enclosing block specification.
	 * @param element
	 *            the element of the inner XML for the block specification
	 *            element.
	 */
	private void handleBlockSpecInnerElement(
			BlockSpecification enclosingBlockSpec, Element element)
			throws BlockFileException {
		String elementName = element.getLocalName();
		if (elementName.equals(XmlToken.XML_ELEMENT_BLOCK_SPECIFICATION)) {
			throw new BlockFileException(
					EDriverExceptionType.MULTIPLE_BLOCKSPEC,
					"Each block specification must be specified in a file of its own.",
					enclosingBlockSpec);
		} else if (elementName.equals(XmlToken.XML_ELEMENT_PARAM)) {
			readSpecificationParameter(element, enclosingBlockSpec);
		} else if (elementName.equals(XmlToken.XML_ELEMENT_OUTPUT)) {
			readSpecificationOutput(element, enclosingBlockSpec);
		} else if (elementName.equals(XmlToken.XML_ELEMENT_BLOCK)) {
			readBlockDeclaration(element, enclosingBlockSpec);
		} else if (elementName.equals(XmlToken.XML_ELEMENT_PROCESSOR)) {
			readProcessorDeclaration(element, enclosingBlockSpec);
		} else if (elementName.equals(XmlToken.XML_ELEMENT_META)) {
			return;
		} else if (elementName.equals(XmlToken.XML_ELEMENT_PROPERTY)) {
			throw new IllegalStateException(
					"Properties should have either been forbidden by the "
							+ "schema or already resolved at this point!");
		} else {
			throw new IllegalStateException(
					"Unsupported tag in block-spec should have been eliminated by the schema: "
							+ elementName);
		}
	}

	/**
	 * Reads a (block) specification parameter from the given element.
	 * 
	 * @param blockSpec
	 *            the specification to add the read parameter to.
	 */
	private void readSpecificationParameter(Element paramElement,
			BlockSpecification blockSpec) throws BlockFileException {
		String name = paramElement.getAttribute(XmlToken.XML_ATTRIBUTE_NAME);
		BlockSpecificationParameter param = new BlockSpecificationParameter(
				name, blockSpec);

		List<Element> childElements = XMLUtils.elementNodes(paramElement
				.getChildNodes());
		extractDoc(childElements, param);

		// all remaining elements must be attr tags
		for (Element element : childElements) {
			String attrName = element.getAttribute(XmlToken.XML_ATTRIBUTE_NAME);
			BlockSpecificationAttribute attr = new BlockSpecificationAttribute(
					attrName, param);
			extractDoc(XMLUtils.elementNodes(element.getChildNodes()), attr);
			param.addAttribute(attr);
		}

		blockSpec.addParam(param);
	}

	/**
	 * Read parameters for a declaration. Comments and free text is skipped. The
	 * parameters are appended to the given declaration.
	 * 
	 * @param element
	 *            the DOM element defining the processor or block.
	 * @param blockSpec
	 *            the specification to add the output to.
	 */
	private void readSpecificationOutput(Element element,
			BlockSpecification blockSpec) throws BlockFileException {
		String name = element.getAttribute(XmlToken.XML_ATTRIBUTE_NAME);
		String ref = element.getAttribute(XmlToken.XML_ATTRIBUTE_REF);
		BlockSpecificationOutput output = new BlockSpecificationOutput(name,
				ref, blockSpec);
		extractDoc(XMLUtils.elementNodes(element.getChildNodes()), output);
		blockSpec.addOutput(output);
	}

	/**
	 * Check the name attribute of an XML element for unique naming. Therefore a
	 * set of known names is provided which is then completed using the name of
	 * this element.
	 * 
	 * @param names
	 *            the set of names used so far.
	 * @param element
	 *            the element to check the name of.
	 * @param scopeName
	 *            the name of the current scope (used for error messages).
	 * @throws BlockFileException
	 *             if a duplicate name was found.
	 */
	private void checkNaming(Set<String> names, Element element,
			String scopeName, ErrorLocation errorLocation)
			throws BlockFileException {
		String nameAttribute = element
				.getAttribute(XmlToken.XML_ATTRIBUTE_NAME);
		if (names.contains(nameAttribute)) {
			throw new BlockFileException(EDriverExceptionType.DUPLICATE_NAME,
					"Element '" + element.getLocalName() + "': name '"
							+ nameAttribute + "' in " + scopeName,
					errorLocation);
		}
		names.add(nameAttribute);
	}

	/** Reads a processor declaration from the given XML element. */
	private void readProcessorDeclaration(Element element,
			BlockSpecification enclosingSpecification)
			throws BlockFileException {
		String name = element.getAttribute(XmlToken.XML_ATTRIBUTE_NAME);
		String classname = element.getAttribute(XmlToken.XML_ATTRIBUTE_CLASS);

		ProcessorDeclaration declaration = new ProcessorDeclaration(name,
				classname, enclosingSpecification, specLoader);
		readDeclarationParameters(element, declaration);
		enclosingSpecification.addDeclaration(declaration);
	}

	/** Reads the block declaration from the given XML element. */
	private void readBlockDeclaration(Element element,
			BlockSpecification enclosingSpecification)
			throws BlockFileException {
		String name = element.getAttribute(XmlToken.XML_ATTRIBUTE_NAME);
		String spec = element.getAttribute(XmlToken.XML_ATTRIBUTE_SPEC);

		BlockDeclaration declaration = new BlockDeclaration(name, spec,
				enclosingSpecification, specLoader);
		readDeclarationParameters(element, declaration);
		enclosingSpecification.addDeclaration(declaration);
	}

	/**
	 * Read parameters for a declaration. Comments and free text is skipped. The
	 * parameters are appended to the given declaration.
	 * 
	 * @param element
	 *            the DOM element defining the processor or block.
	 * @param declaration
	 *            the declaration to read the parameters for.
	 */
	private void readDeclarationParameters(Element element,
			IDeclaration declaration) throws BlockFileException {

		List<DeclarationParameter> parameters = new ArrayList<DeclarationParameter>();
		parameters.add(createConditionParameter(element, declaration));

		for (Element param : XMLUtils.elementNodes(element.getChildNodes())) {
			String paramName = param.getLocalName();
			DeclarationParameter parameter = new DeclarationParameter(
					paramName, declaration);

			if (!XMLUtils.elementNodes(param.getChildNodes()).isEmpty()) {
				throw new BlockFileException(
						EDriverExceptionType.PARAMETER_HAS_CHILDELEMENTS,
						"The parameter '"
								+ paramName
								+ "' has child elements although they are not allowed there.",
						parameter);
			}

			if (containsText(param)) {
				throw new BlockFileException(
						EDriverExceptionType.PARAMETER_HAS_TEXT_CONTENT,
						"The parameter '" + paramName + "' has text contents",
						parameter);
			}

			NamedNodeMap attributes = param.getAttributes();
			for (int j = 0; j < attributes.getLength(); j++) {
				Node attr = attributes.item(j);
				String name = attr.getLocalName();
				String value = attr.getNodeValue();

				parameter.addAttribute(new DeclarationAttribute(name, value,
						parameter));
			}

			parameters.add(parameter);
		}
		declaration.setParameters(parameters);
	}

	/**
	 * Creates the synthetic parameter for conditional execution based on the
	 * {@link XmlToken#XML_ATTRIBUTE_CONDITION} attribute.
	 */
	private DeclarationParameter createConditionParameter(Element element,
			IDeclaration declaration) {
		// XML schema has default value set, so the attribute is always valid
		String condition = element
				.getAttribute(XmlToken.XML_ATTRIBUTE_CONDITION);
		return BlockDeclaration
				.createConditionParameter(declaration, condition);
	}

	/**
	 * Checks whether the given element contains text. This method does not only
	 * check the text content, but also the text content of children. This is
	 * documented/explained in CR#3225.
	 */
	private static boolean containsText(Element param) {
		if (!StringUtils.isEmpty(param.getTextContent())) {
			return true;
		}
		NodeList childs = param.getChildNodes();
		for (int i = 0; i < childs.getLength(); ++i) {
			if (!StringUtils.isEmpty(childs.item(i).getTextContent())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Extracts the documentation from a list of XML elements and stores it in
	 * the given documented object. Documentation is expected to be the first
	 * element and be a doc tag. If documentation is found it is removed from
	 * the list, otherwise nothing happens.
	 * 
	 * @param elements
	 *            the list of elements possibly containing the documentation.
	 * @param documentable
	 *            the object to store the documentation into.
	 */
	private void extractDoc(List<Element> elements, IDocumentable documentable) {
		if (elements == null || elements.isEmpty()) {
			return;
		}
		Element first = elements.get(0);
		if (XmlToken.XML_ELEMENT_DOC.equals(first.getLocalName())) {
			elements.remove(0);
			documentable.setDoc(first.getTextContent());
		}
	}

	/**
	 * Read the config from a stream file and return the contained conqat
	 * element.
	 */
	public static Element loadConqatElement(InputStream in,
			ErrorLocation errorLocation) throws BlockFileException {
		Document document = parseDom(in, errorLocation);
		Element rootNode = (Element) document.getElementsByTagName(
				XmlToken.XML_ELEMENT_CONQAT).item(0);
		return rootNode;
	}

	/**
	 * Read the provided config file and return the contained conqat element.
	 * This is a convenience method, used e.g. from the IDE integration.
	 */
	public static Element loadConqatElement(File xmlFile)
			throws BlockFileException {
		try {
			// no need for closing, as the called method does that
			return loadConqatElement(new FileInputStream(xmlFile),
					new ErrorLocation(xmlFile));
		} catch (FileNotFoundException e) {
			throw new BlockFileException(EDriverExceptionType.IO_ERROR,
					"Could not read " + xmlFile + ": " + e.getMessage(), e,
					new ErrorLocation(xmlFile));
		}
	}

	/**
	 * Read the provided stream and return a DOM tree of its contents. The
	 * stream is closed by this method.
	 */
	private static Document parseDom(InputStream in, ErrorLocation errorLocation)
			throws BlockFileException {
		try {

			URL schemaURL = BlockFileReader.class
					.getResource(XmlToken.SCHEMA_NAME);

			Document document = org.conqat.lib.commons.xml.XMLUtils.parse(
					new InputSource(in), schemaURL);

			return document;

		} catch (SAXException e) {
			throw new BlockFileException(
					EDriverExceptionType.XML_PARSING_EXCEPTION,
					"XML parsing exception", e, errorLocation);
		} catch (IOException e) {
			throw new BlockFileException(EDriverExceptionType.IO_ERROR,
					"File not readable: " + e.getMessage(), e, errorLocation);
		} finally {
			FileSystemUtils.close(in);
		}
	}

}