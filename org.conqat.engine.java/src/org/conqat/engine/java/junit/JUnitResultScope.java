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
package org.conqat.engine.java.junit;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.xml.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This processor defines a scope consisting of JUnit results. JUnit results are
 * read from the XML formatted JUnit report file. This scope currently lists
 * only the test suites of one result but not their test cases.
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 8D921194E24A42E47F9FA51E9335D17E
 */
@AConQATProcessor(description = "This processor defines a scope consisting "
		+ "of JUnit results. JUnit results are read from the XML formatted "
		+ "JUnit report files.")
public class JUnitResultScope extends ConQATProcessorBase {

	/** Key for test count */
	@AConQATKey(description = "The number of tests in the suite.", type = "java.lang.Integer")
	public final static String KEY_TEST_COUNT = "# Tests";

	/** Key for error count */
	@AConQATKey(description = "The number of errors for the suite.", type = "java.lang.Integer")
	public final static String KEY_ERROR_COUNT = "# Errors";

	/** Key for failure count */
	@AConQATKey(description = "The number of failures for the suite.", type = "java.lang.Integer")
	public final static String KEY_FAILURE_COUNT = "# Failures";

	/** Names of the JUnit report files. */
	private final HashSet<String> filenames = new HashSet<String>();

	/** The root node representing a single report file. */
	private JUnitResultNode root;

	/**
	 * Set the name of the JUnit report file, e.g.
	 * 'log/junit/TESTS-TestSuites.xml'
	 */
	@AConQATParameter(name = "file", minOccurrences = 1, description = "name of the XML-formatted "
			+ "JUnit report file, e.g. 'log/junit/TESTS-TestSuites.xml'")
	public void addFilename(
			@AConQATAttribute(name = "path", description = "path to file") String filename) {
		filenames.add(filename);
	}

	/** {@inheritDoc} */
	@Override
	public JUnitResultNode process() throws ConQATException {
		root = new JUnitResultNode();
		for (String filename : filenames) {
			parseResultFile(new File(filename));
		}
		if (!root.hasChildren()) {
			throw new ConQATException("No test cases found!");
		}
		return root;
	}

	/** Parse the JUnit report file and build scope. */
	private void parseResultFile(File file) {

		Document document;
		try {
			document = XMLUtils.parse(file);
		} catch (IOException e) {
			getLogger().warn("IO error while reading " + file);
			return;
		} catch (SAXException e) {
			getLogger().warn("XML parsing error in " + file);
			return;
		}

		NodeList nodes = document.getElementsByTagName("testsuite");
		if (nodes == null) {
			return;
		}
		for (int i = 0; i < nodes.getLength(); i++) {
			Element node = (Element) nodes.item(i);
			addSuite(node);
		}
	}

	/**
	 * Add child node that describes a JUnit test suite to the root node.
	 * 
	 * @param element
	 *            DOM element that describes a test suite.
	 */
	private void addSuite(Element element) {
		String className = element.getAttribute("name");
		String packageName = element.getAttribute("package");
		String fqName = packageName + "." + className;
		int testCount = Integer.valueOf(element.getAttribute("tests"));
		int errorCount = Integer.valueOf(element.getAttribute("errors"));
		int failureCount = Integer.valueOf(element.getAttribute("failures"));

		JUnitTestSuiteNode suiteNode = new JUnitTestSuiteNode(fqName,
				testCount, errorCount, failureCount);
		root.addChild(suiteNode);
	}

}