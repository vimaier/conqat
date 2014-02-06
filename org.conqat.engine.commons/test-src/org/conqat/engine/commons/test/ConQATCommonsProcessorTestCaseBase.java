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
package org.conqat.engine.commons.test;

import static org.conqat.lib.commons.string.StringUtils.CR;

import java.util.HashSet;
import java.util.Set;

import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.reflect.EJavaPrimitive;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.engine.commons.node.ConQATNodeBase;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.core.logging.testutils.ConQATProcessorTestCaseBase;

/**
 * Base class for tests that are dealing with processors from ConQAT commons.
 * 
 * @author hummelb
 * @author $Author: hummelb $
 * @version $Rev: 43290 $
 * @ConQAT.Rating GREEN Hash: C6CE987555DE81ACBD3F43A63C57EB9E
 */
public abstract class ConQATCommonsProcessorTestCaseBase extends
		ConQATProcessorTestCaseBase {

	/**
	 * Special "key" used as a result in
	 * {@link #compareNodes(IConQATNode, IConQATNode, Set)}.
	 */
	protected static final String DIFFERENCE_IN_NODE = "Difference in node";

	/** Constructor (used to register CQDDL functions). */
	protected ConQATCommonsProcessorTestCaseBase() {
		parsingParameters.registerFunction("assessment",
				new AssessmentCQDDLFunction());
		parsingParameters.registerFunction("listNode",
				new ListNodeCQDDLFunction());
		parsingParameters.registerFunction("patList",
				new PatternListCQDDLFunction());
	}

	/** Asserts that two ConQAT node hierarchies are equal. */
	protected void assertNodesEqual(IConQATNode expected, IConQATNode received) {
		assertNodesEqual(expected, received, null);
	}

	/**
	 * Asserts that two ConQAT node hierarchies are equal when looking only at
	 * certain keys.
	 * 
	 * @param keys
	 *            the keys to use for comparison. If this is null, all keys are
	 *            used.
	 */
	protected void assertNodesEqual(IConQATNode expected, IConQATNode received,
			Set<String> keys) {
		String differingKey = compareNodes(expected, received, keys);
		if (differingKey == null) {
			return;
		}

		Set<String> outputKeys = new HashSet<String>();
		String message;
		if (DIFFERENCE_IN_NODE.equals(differingKey)) {
			message = "Had structural differences in nodes!";
		} else {
			outputKeys.add(differingKey);
			message = "Had differences in value of key " + differingKey + "!";
		}

		String expectedDump = dumpConQATNode(expected, outputKeys);
		String receivedDump = dumpConQATNode(received, outputKeys);

		if (expectedDump.equals(receivedDump)) {
			assertTrue(
					message
							+ " However, the string representation of both nodes is equal. "
							+ "Maybe it helps to override some methods of this test.",
					false);
		} else {
			assertEquals(
					message
							+ " A serialized version reduced to relevant parts is included.",
					expectedDump, receivedDump);
		}
	}

	/**
	 * Compares two ConQAT nodes with respect to certain keys.
	 * 
	 * @param keys
	 *            the keys to use for comparison. If this is null, all keys are
	 *            used.
	 * @return the name of the key for which values differ, or the value
	 *         {@link #DIFFERENCE_IN_NODE} if the difference is in the nodes
	 *         themselves, or null if both are equal.
	 */
	protected String compareNodes(IConQATNode node1, IConQATNode node2,
			Set<String> keys) {
		if (!equalNodes(node1, node2)) {
			return DIFFERENCE_IN_NODE;
		}

		for (String key : determineKeys(node1, node2, keys)) {
			if (!equalKeyValues(node1.getValue(key), node2.getValue(key))) {
				return key;
			}
		}

		if (node1.hasChildren() != node2.hasChildren()) {
			return DIFFERENCE_IN_NODE;
		}
		if (!node1.hasChildren()) {
			return null;
		}

		IConQATNode[] expectedChildren = node1.getChildren();
		IConQATNode[] receivedChildren = node2.getChildren();

		if (expectedChildren.length != receivedChildren.length) {
			return DIFFERENCE_IN_NODE;
		}

		for (int i = 0; i < expectedChildren.length; ++i) {
			String result = compareNodes(expectedChildren[i],
					receivedChildren[i], keys);
			if (result != null) {
				return result;
			}
		}

		return null;
	}

	/** Determines the keys being used when comparing two nodes. */
	private Set<String> determineKeys(IConQATNode node1, IConQATNode node2,
			Set<String> keys) {
		if (keys != null) {
			return keys;
		}
		keys = new HashSet<String>();
		if (node1 instanceof ConQATNodeBase) {
			keys.addAll(((ConQATNodeBase) node1).getKeys());
		}
		if (node2 instanceof ConQATNodeBase) {
			keys.addAll(((ConQATNodeBase) node2).getKeys());
		}
		return keys;
	}

	/**
	 * Returns whether two nodes equal when not considering their keys or
	 * children. Subclasses may override. The default implementation compares
	 * the class, the name and the ID.
	 */
	protected boolean equalNodes(IConQATNode node1, IConQATNode node2) {
		return node1.getClass().equals(node2.getClass())
				&& node1.getName().equals(node2.getName())
				&& node1.getId().equals(node2.getId());
	}

	/**
	 * Returns whether two values accessed via a node's keys are equal.
	 * Subclasses may override. The default implementation does null comparison
	 * and is based on equals().
	 */
	protected boolean equalKeyValues(Object value1, Object value2) {
		if (value1 == null) {
			return value2 == null;
		}
		if (value2 == null) {
			return false;
		}

		return value1 == value2 || value1.equals(value2);
	}

	/**
	 * Method for converting a ConQAT node to a string. This can be useful
	 * during debugging and for error messages in assertions.
	 * 
	 * @param keys
	 *            the keys to be respected. If this is null, all keys will be
	 *            used.
	 */
	protected String dumpConQATNode(IConQATNode node, Set<String> keys) {
		StringBuilder sb = new StringBuilder();
		dumpConQATNode(node, keys, 0, sb);
		return sb.toString();
	}

	/**
	 * Helper method for {@link #dumpConQATNode(IConQATNode, Set)}. We keep this
	 * protected, so subclasses may extend it for specific node types (if
	 * required).
	 */
	protected void dumpConQATNode(IConQATNode node, Set<String> keys,
			int indentLevel, StringBuilder sb) {
		String indent = StringUtils.fillString(2 * indentLevel, ' ');

		sb.append(indent + node.getName() + " ["
				+ node.getClass().getSimpleName() + "] {" + CR);
		sb.append(indent + "  id = " + node.getId() + CR);

		if (node instanceof ConQATNodeBase) {
			for (String key : CollectionUtils.sort(((ConQATNodeBase) node)
					.getKeys())) {
				if (keys == null || keys.contains(key)) {
					sb.append(indent + "  [" + key + " = "
							+ dumpKeyValue(node.getValue(key)) + "]" + CR);
				}
			}
		} else {
			sb.append(indent + "  <no key information>" + CR);
		}

		if (node.hasChildren()) {
			for (IConQATNode child : node.getChildren()) {
				dumpConQATNode(child, keys, indentLevel + 1, sb);
			}
		}

		sb.append(indent + "}" + CR);
	}

	/**
	 * Returns a string representation of a key value. This is made protected,
	 * so subclasses can add support for specific types.
	 * <p>
	 * The base implementation only supports primitives (and their wrappers),
	 * enums, and strings.
	 */
	@SuppressWarnings("rawtypes")
	protected String dumpKeyValue(Object value) {
		if (value == null) {
			return "<null>";
		}

		if (value instanceof String) {
			return (String) value;
		}

		if (EJavaPrimitive.isWrapperType(value.getClass())) {
			return value.toString();
		}

		if (value instanceof Enum<?>) {
			return ((Enum) value).name();
		}

		return "<object>";
	}
}