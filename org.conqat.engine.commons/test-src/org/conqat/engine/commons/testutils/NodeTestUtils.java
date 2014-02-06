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
package org.conqat.engine.commons.testutils;

import java.util.IdentityHashMap;

import org.conqat.lib.commons.clone.DeepCloneException;
import org.conqat.lib.commons.collections.IIdProvider;
import org.conqat.lib.commons.collections.IdentityHashSet;
import org.conqat.lib.commons.error.NeverThrownRuntimeException;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.commons.test.DeepCloneTestUtils;
import org.conqat.engine.commons.node.SetNode;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.traversal.INodeVisitor;
import org.conqat.engine.commons.traversal.TraversalUtils;

/**
 * Utility class for tests.
 * 
 * 
 * @author deissenb
 * @author $Author: hummelb $
 * @version $Rev: 43290 $
 * @ConQAT.Rating GREEN Hash: 55AD2B76E3D6B30338CE527A3CC02E4F
 */
public class NodeTestUtils {

	/** Create root node. */
	public static SetNode<String> createRootNode() {
		return new SetNode<String>("ROOT");
	}

	/**
	 * Create root node and attach nodes as described in
	 * {@link #addNodes(SetNode, String)}.
	 */
	public static SetNode<String> createNodes(String path) {
		SetNode<String> root = createRootNode();
		addNodes(root, path);
		return root;
	}

	/**
	 * Add new node to an existing node.
	 * <p>
	 * Example:
	 * 
	 * <pre>
	 * NodeTestUtils.addNodes(root, &quot;test1/test2/test3/test4/test5&quot;);
	 * </pre>
	 * 
	 * attaches the five nodes as children of each other to the root node. A
	 * subsequent call to
	 * 
	 * <pre>
	 * NodeTestUtils.addNodes(root, &quot;test1/test2/test3/test4/test6&quot;);
	 * </pre>
	 * 
	 * creates the new node 'test6' as child of node 'test4'.
	 */
	public static void addNodes(SetNode<String> node, String path) {
		for (String element : path.split("/")) {

			SetNode<String> child;

			SetNode<String> namedChild = node.getChild(element);
			if (namedChild != null) {
				child = namedChild;
			} else {
				child = new SetNode<String>(element);
				node.addChild(child);
			}

			node = child;
		}
	}

	/**
	 * Get a node denoted by the provided path expression.
	 * 
	 * @returns the node or <code>null</code> if no node of the given names
	 *          was found.
	 */
	public static SetNode<String> getNode(SetNode<String> node,
			String path) {
		for (String element : path.split("/")) {

			SetNode<String> child = node.getChild(element);
			if (child == null) {
				return null;
			}
			node = child;
		}

		return node;
	}

	/**
	 * Create a string representation of a node.
	 * 
	 * @param node
	 *            the root node
	 * @param keys
	 *            the keys to include in the string representation
	 */
	public static String toString(IConQATNode node, String... keys) {
		StringBuilder result = new StringBuilder();
		toString(node, keys, result, 0);
		return result.toString();
	}

	/**
	 * Attaches the string representation of the nodes to a
	 * {@link StringBuilder}.
	 */
	private static void toString(IConQATNode node, String[] keys,
			StringBuilder result, int depth) {
		result
				.append(StringUtils.fillString(depth * 2,
						StringUtils.SPACE_CHAR));
		result.append(node.getId());

		for (String key : keys) {
			result.append(" " + key + " = " + node.getValue(key) + " ");
		}

		result.append(StringUtils.CR);

		if (node.hasChildren()) {
			depth++;
			for (IConQATNode child : node.getChildren()) {
				toString(child, keys, result, depth);
			}
		}
	}

	/** Set a value for all nodes in a tree. */
	public static void setValue(IConQATNode root, String key, Object value) {
		TraversalUtils.visitAllDepthFirst(new ValueSetter(key, value), root);
	}

	/**
	 * Test deep cloning for a node.
	 * 
	 * @return a map that maps from object to its clones.
	 */
	public static IdentityHashMap<Object, Object> testDeepCloning(
			IConQATNode node) throws DeepCloneException {
		return testDeepCloning(node, node.getClass().getPackage().getName());
	}

	/**
	 * Test deep cloning for a node.
	 * 
	 * @param orig
	 *            the root node.
	 * @param packagePrefixes
	 *            prefix of the packages that child nodes belong to.
	 * @return a map that maps from object to its clones.
	 */
	public static IdentityHashMap<Object, Object> testDeepCloning(
			IConQATNode orig, String... packagePrefixes)
			throws DeepCloneException {
		IdentityHashSet<IConQATNode> nodes = DeepCloneTestUtils
				.getAllReferencedObjects(orig, IConQATNode.class,
						packagePrefixes);

		for (IConQATNode node : nodes) {
			node.setValue("testKey", "testValue");
		}

		IConQATNode clone = orig.deepClone();
		IdentityHashMap<Object, Object> result = DeepCloneTestUtils
				.testDeepCloning(orig, clone, new ConQATNodeIdProvider(),
						packagePrefixes);

		for (Object object : result.values()) {
			IConQATNode node = (IConQATNode) object;
			if (!"testValue".equals(node.getValue("testKey"))) {
				throw new RuntimeException(
						"Cloning of values is not implemented properly for "
								+ node.getClass());
			}
		}

		return result;
	}

	/** Simple id provider for ConQAT nodes. */
	public static class ConQATNodeIdProvider implements
			IIdProvider<String, Object> {
		/** Returns id for ConQAT nodes, throws an exception for other nodes. */
		@Override
		public String obtainId(Object node) {
			if (!(node instanceof IConQATNode)) {
				throw new RuntimeException(
						"ID provider works for ConQATNodes only.");
			}
			return ((IConQATNode) node).getId();
		}

	}

	/** Simple visitor for setting values. */
	private static class ValueSetter implements
			INodeVisitor<IConQATNode, NeverThrownRuntimeException> {

		/** Key to set. */
		private final String key;

		/** Value to set. */
		private final Object value;

		/** Create setter. */
		public ValueSetter(String key, Object value) {
			this.key = key;
			this.value = value;
		}

		/** Sets value. */
		@Override
		public void visit(IConQATNode node) throws NeverThrownRuntimeException {
			node.setValue(key, value);
		}

	}
}