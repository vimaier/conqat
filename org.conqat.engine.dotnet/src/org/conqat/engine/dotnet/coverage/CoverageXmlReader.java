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
package org.conqat.engine.dotnet.coverage;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.conqat.engine.commons.node.ListNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.text.TextElementUtils;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.string.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Reads coverage XML files as produced by Microsoft's coverage tools. These
 * tools record coverage information in a proprietary binary format, but they
 * can be converted to XML using Microsoft tools. This XML format is processed
 * here.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46896 $
 * @ConQAT.Rating GREEN Hash: 84F34FC422C630AD70F2B63ED0B569C7
 */

public class CoverageXmlReader extends DefaultHandler {

	/**
	 * Lookup map from element names to enum. This is used instead of
	 * EnumUtils.valueOf() for performance reasons. We have to do the lookup for
	 * each element, and experiments have shown this approach to be twice as
	 * fast.
	 */
	private static final Map<String, ECoverageXmlElement> NAME_TO_ELEMENT = new HashMap<String, ECoverageXmlElement>();

	static {
		for (ECoverageXmlElement element : ECoverageXmlElement.values()) {
			NAME_TO_ELEMENT.put(element.name(), element);
		}
	}

	/** The keys used for storing coverage information. */
	private static final List<String> COVERAGE_KEYS = Arrays.asList(
			ECoverageXmlElement.LinesCovered.name(),
			ECoverageXmlElement.LinesPartiallyCovered.name(),
			ECoverageXmlElement.LinesNotCovered.name(),
			ECoverageXmlElement.BlocksCovered.name(),
			ECoverageXmlElement.BlocksNotCovered.name());

	/** Element containing the coverage report */
	private final ITextElement inputElement;

	/** Whether to read method-level coverage information */
	private final boolean methodLevel;

	/** Buffer used for storing text content in elements. */
	private final StringBuilder textContent = new StringBuilder();

	/** Stack for storing the current receiver of coverage information. */
	private Stack<ListNode> coverageInfoStack = new Stack<ListNode>();

	/** Stack of open (recognized) elements. */
	private Stack<ECoverageXmlElement> elementStack = new Stack<ECoverageXmlElement>();

	/** Map from namespace keys to namespace names */
	private final Map<String, String> namespaceMapping = new HashMap<String, String>();

	/** Name of the current NamespaceKeyName element. */
	private String currentNamespaceKeyName;

	/**
	 * Constructor.
	 * 
	 * @param inputElement
	 *            element containing the coverage report.
	 * @param root
	 *            Root node to which results are appended. For each .NET Module
	 *            in the coverage reports, a single ListNode with the coverage
	 *            information is appended.
	 * @param methodLevel
	 *            whether to read method-level coverage information
	 */
	public CoverageXmlReader(ITextElement inputElement, ListNode root,
			boolean methodLevel) {
		this.inputElement = inputElement;
		coverageInfoStack.push(root);
		this.methodLevel = methodLevel;
	}

	/**
	 * Parse the coverage XML. If the input element corresponds to a plain file,
	 * we directly read the file to avoid loading the entire (large) report into
	 * memory first.
	 */
	public void parse() throws ConQATException {
		TextElementUtils.parseSAX(inputElement, this);
	}

	/** {@inheritDoc} */
	@Override
	public void error(SAXParseException e) throws SAXException {
		throw e;
	}

	/** {@inheritDoc} */
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) {
		// always reset current text content as we are not interested in text
		// over element boundaries
		textContent.setLength(0);

		ECoverageXmlElement element = NAME_TO_ELEMENT.get(localName);
		if (element != null) {
			elementStack.push(element);
		}
	}

	/**
	 * Completes a class node by adding namespace information an attaching the
	 * temporary node to the tree.
	 */
	private void completeClassNode() throws SAXException {
		String namespaceName = namespaceMapping.get(currentNamespaceKeyName
				.trim());
		if (namespaceName == null) {
			throw new SAXException("Invalid format: No namespace for key "
					+ currentNamespaceKeyName + " found!");
		}

		ListNode oldClassNode = coverageInfoStack.pop();
		String fullName = oldClassNode.getName();
		if (!StringUtils.isEmpty(namespaceName)) {
			fullName = namespaceName + "." + fullName;
		}

		ListNode newClassNode = insertNewNode(fullName);

		if (oldClassNode.hasChildren()) {
			for (ListNode child : oldClassNode.getChildren()) {
				newClassNode.addChild(child);
			}
		}

		try {
			NodeUtils.copyValues(COVERAGE_KEYS, oldClassNode, newClassNode,
					false);
		} catch (ConQATException e) {
			CCSMAssert.fail("Impossible, as we do not use deep cloning!");
		}
	}

	/** {@inheritDoc} */
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		ECoverageXmlElement element = NAME_TO_ELEMENT.get(localName);
		if (element == null) {
			return;
		}

		if (elementStack.pop() != element) {
			throw new SAXException("Nesting error!");
		}

		switch (element) {
		case Class:
			completeClassNode();
			// fall-through intended

		case Module:
		case Method:
			assertCoverageReceiverPresent();
			coverageInfoStack.pop();
			break;

		case LinesCovered:
		case LinesPartiallyCovered:
		case LinesNotCovered:
		case BlocksCovered:
		case BlocksNotCovered:
			parseAndStoreCoverageValue(element);
			break;

		case NamespaceKeyName:
			currentNamespaceKeyName = assertNotEmpty(textContent.toString());
			break;

		case NamespaceName:
			namespaceMapping.put(currentNamespaceKeyName.trim(), textContent
					.toString().trim());
			break;

		case ModuleName:
			if (elementStack.peek() == ECoverageXmlElement.Module) {
				insertNewNode(textContent.toString());
			}
			break;

		case ClassName:
			String className = textContent.toString();
			if (StringUtils.isEmpty(className)) {
				className = "<empty>";
			}
			// push an (unconnected) dummy node to receive coverage info
			coverageInfoStack.push(new ListNode(className));
			break;

		case MethodName:
			if (methodLevel) {
				insertNewNode(textContent.toString());
			} else {
				// need dummy node to accept the coverage info
				coverageInfoStack.push(new ListNode());
			}
			break;
		}
	}

	/**
	 * Inserts a new node to the top of the {@link #coverageInfoStack} stack and
	 * adds it to the previous entry there.
	 */
	private ListNode insertNewNode(String name) {
		assertCoverageReceiverPresent();
		ListNode node = new ListNode(assertNotEmpty(name));
		coverageInfoStack.peek().addChild(node);
		coverageInfoStack.push(node);
		return node;
	}

	/**
	 * Parses a coverage value and stores it to the top-node of
	 * {@link #coverageInfoStack}.
	 */
	private void parseAndStoreCoverageValue(ECoverageXmlElement coverageType)
			throws SAXException {
		// only store coverage value for modules, classes, methods
		switch (elementStack.peek()) {
		case Module:
		case Class:
		case Method:
			break;
		default:
			return;
		}

		assertCoverageReceiverPresent();

		try {
			int value = Integer.valueOf(assertNotEmpty(textContent.toString()));
			coverageInfoStack.peek().setValue(coverageType.name(), value);
		} catch (NumberFormatException e) {
			throw new SAXException("Invalid coverage report format:", e);
		}

	}

	/**
	 * This method is used to document our assumptions about the Coverage report
	 * format.
	 */
	private static String assertNotEmpty(String string) {
		CCSMAssert
				.isFalse(StringUtils.isEmpty(string),
						"Assumption about Coverage report format violated. String is empty!");
		return string;
	}

	/**
	 * This method is used to document our assumptions about the Coverage report
	 * format.
	 */
	private void assertCoverageReceiverPresent() {
		CCSMAssert.isFalse(coverageInfoStack.isEmpty(),
				"No coverage receiver present!");
	}

	/** {@inheritDoc} */
	@Override
	public void characters(char[] ch, int start, int length) {
		textContent.append(ch, start, length);
	}
}
