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
package org.conqat.engine.commons.input;

import java.io.File;
import java.io.IOException;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import org.conqat.lib.commons.xml.XMLUtils;
import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;

/**
 * This processor extracts a string specified by an XPath expression from an XML
 * file. This processor fails if the specified XML element was not found.
 * 
 * @author Florian Deissenboeck
 * @author $Author: hummelb $
 * @version $Rev: 37013 $
 * @ConQAT.Rating GREEN Hash: 520501BE6ED05EABC278835E2EE3E0B5
 */
@AConQATProcessor(description = "This processor extracts a string specified "
		+ "by an XPath expression from an XML file. This processor fails "
		+ "if the specified element was not found.")
public class XPathReader extends ConQATProcessorBase {

	/** XPath object used to evaluate XPath expression */
	private final XPath xPathEvaluator = XPathFactory.newInstance().newXPath();

	/** XML file */
	private File file;

	/** XPath expression */
	private String xPath;

	/** Set name of the XML file. */
	@AConQATParameter(name = "file", minOccurrences = 1, maxOccurrences = 1, description = "XML file to read value from")
	public void setFilename(
			@AConQATAttribute(name = "name", description = "Filename") String filename) {
		this.file = new File(filename);
	}

	/** Set the XPath expression */
	@AConQATParameter(name = "xpath", minOccurrences = 1, maxOccurrences = 1, description = "XPath expression")
	public void setXPath(
			@AConQATAttribute(name = "value", description = "Expression") String xPath) {
		this.xPath = xPath;
	}

	/**
	 * Reads XML file and extracts value.
	 * 
	 * @throws ConQATException
	 *             if the file could not be read or parsed or if the XPath
	 *             expression is invalid or points to an XML element that could
	 *             not be found.
	 */
	@Override
	public String process() throws ConQATException {
		Document doc = parseFile();

		// we do not use XPathEvaluator here as we want to check for invalid
		// XPath expressions.
		try {

			// we need to do this in two steps as the xPathEvaluator.evaluate
			// with return type String returns an empty string for non-existent
			// element as well as for elements with no text content.
			boolean exists = (Boolean) xPathEvaluator.evaluate(xPath, doc
					.getDocumentElement(), XPathConstants.BOOLEAN);

			if (!exists) {
				throw new ConQATException("Element specified by XPath " + xPath
						+ " not found.");
			}

			return (String) xPathEvaluator.evaluate(xPath, doc
					.getDocumentElement(), XPathConstants.STRING);

		} catch (XPathExpressionException e) {
			throw new ConQATException("Invalid XPath expresion: " + xPath, e);
		}

	}

	/** Parse XML file and handle exception. */
	private Document parseFile() throws ConQATException {
		try {
			return XMLUtils.parse(file);
		} catch (SAXException e) {
			throw new ConQATException("Could not parse file: " + file, e);
		} catch (IOException e) {
			throw new ConQATException("Could not read file: " + file, e);
		}
	}
}