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
package org.conqat.engine.commons.traversal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.error.NeverThrownRuntimeException;
import org.conqat.lib.commons.predicate.IPredicate;
import org.conqat.lib.commons.predicate.PredicateUtils;

/**
 * This class offers utility methods for tree traversal.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 45908 $
 * @ConQAT.Rating YELLOW Hash: EA012E1034634CECD146051BC9C7D767
 * 
 */
public class TraversalUtils {

	/**
	 * Count number of node descendants.
	 * 
	 * @param node
	 *            parent node
	 * @return 0 if node has no children, number of descendants otherwise
	 */
	public static int countDescendants(IConQATNode node) {
		int result = 0;
		if (node.hasChildren()) {
			for (IConQATNode child : node.getChildren()) {
				result += 1 + countDescendants(child);
			}
		}
		return result;
	}

	/**
	 * Count number of nodes.
	 * 
	 * @param targetNodes
	 *            defines nodes to include in count
	 * @param startNode
	 *            that node to start at
	 */
	public static int countNodes(IConQATNode startNode, ETargetNodes targetNodes) {
		NodeCounter counter = new NodeCounter();
		visitDepthFirst(counter, startNode, targetNodes);
		return counter.nodeCount;
	}

	/**
	 * Count number of leaves
	 * 
	 * @param startNode
	 *            that node to start at
	 */
	public static int countLeaves(IConQATNode startNode) {
		return countNodes(startNode, ETargetNodes.LEAVES);
	}

	/**
	 * Does the same as
	 * {@link #visitDepthFirst(INodeVisitor, IConQATNode, ETargetNodes)} but
	 * visits all nodes.
	 */
	public static <E extends IConQATNode, X extends Exception> void visitAllDepthFirst(
			INodeVisitor<E, X> visitor, E startNode) throws X {
		visitDepthFirst(visitor, startNode, ETargetNodes.ALL);
	}

	/**
	 * Does the same as
	 * {@link #visitDepthFirst(INodeVisitor, IConQATNode, ETargetNodes)} but
	 * visits only leaves (sorting is respected).
	 */
	public static <E extends IConQATNode, X extends Exception> void visitLeavesDepthFirstSorted(
			INodeVisitor<E, X> visitor, E startNode) throws X {
		visitDepthFirstSortable(visitor, startNode, ETargetNodes.LEAVES, true);
	}

	/**
	 * Does the same as
	 * {@link #visitDepthFirst(INodeVisitor, IConQATNode, ETargetNodes)} but
	 * visits only leaves.
	 */
	public static <E extends IConQATNode, X extends Exception> void visitLeavesDepthFirst(
			INodeVisitor<E, X> visitor, E startNode) throws X {
		visitDepthFirst(visitor, startNode, ETargetNodes.LEAVES);
	}

	/**
	 * Same as
	 * {@link #visitDepthFirstSortable(INodeVisitor, IConQATNode, ETargetNodes, boolean)}
	 * without sorting.
	 */
	public static <E extends IConQATNode, X extends Exception> void visitDepthFirst(
			INodeVisitor<E, X> visitor, E startNode, ETargetNodes targetNodes)
			throws X {
		visitDepthFirstSortable(visitor, startNode, targetNodes, false);
	}

	/**
	 * Traverse tree in a depth first fashion. It is expected that method
	 * <code>getChildren()</code> of the class defined as as generic parameter
	 * returns objects of the same class (or a subclass). Otherwise a
	 * <code>ClassCastException</code> will be thrown.
	 * 
	 * @param <E>
	 *            the node type to visit
	 * @param <X>
	 *            exception type
	 * @param visitor
	 *            the visitor
	 * @param startNode
	 *            that node to start at
	 * @param targetNodes
	 *            defines nodes to visit
	 * @param sortIfPossible
	 *            if this is true and a comparator is defined, the children are
	 *            sorted. Otherwise they are not sorted.
	 * @throws X
	 *             might be thrown by the visitor
	 * @throws ClassCastException
	 *             if a node's <code>getChildren()</code>-method returns
	 *             children of different type (or subtype) than the node.
	 */
	public static <E extends IConQATNode, X extends Exception> void visitDepthFirstSortable(
			INodeVisitor<E, X> visitor, E startNode, ETargetNodes targetNodes,
			boolean sortIfPossible) throws X {

		if ((targetNodes != ETargetNodes.ROOT) && (startNode.hasChildren())) {

			for (IConQATNode rawChild : NodeUtils.getChildren(startNode,
					sortIfPossible)) {
				// this expects children of a node to be of the same type as the
				// node
				@SuppressWarnings("unchecked")
				E typedChild = (E) rawChild;
				visitDepthFirstSortable(visitor, typedChild, targetNodes,
						sortIfPossible);
			}
		}

		if (shouldVisitNode(startNode, targetNodes)) {
			visitor.visit(startNode);
		}
	}

	/** Decides for a node and the targets, whether the node should be visited. */
	private static boolean shouldVisitNode(IConQATNode node,
			ETargetNodes targetNodes) {
		switch (targetNodes) {
		case ALL:
		case ROOT:
			return true;
		case INNER:
			return node.hasChildren();
		case LEAVES:
			return !node.hasChildren();
		}
		throw new IllegalStateException("Unknown enum constant: " + targetNodes);
	}

	/** Returns a list that contains the leaf nodes in a tree. */
	public static <E extends IConQATNode> List<E> listLeavesDepthFirst(E node) {
		return listDepthFirst(node, ETargetNodes.LEAVES);
	}

	/**
	 * Returns a list that contains targeted and included nodes in a tree.
	 * 
	 * @param inclusionPredicate
	 *            optional predicate that all returned nodes must fulfill. May
	 *            be null to indicate all nodes.
	 */
	public static <E extends IConQATNode> List<E> listDepthFirst(E node,
			ETargetNodes targetNodes, IPredicate<? super E> inclusionPredicate) {
		LeafListBuilder<E> visitor = new LeafListBuilder<E>();
		visitDepthFirst(visitor, node, targetNodes);
		List<E> result = visitor.leavesList;
		if (inclusionPredicate != null) {
			result = PredicateUtils.obtainContained(result, inclusionPredicate);
		}
		return result;
	}

	/** Returns a list that contains targeted nodes in a tree. */
	public static <E extends IConQATNode> List<E> listDepthFirst(E node,
			ETargetNodes targetNodes) {
		return listDepthFirst(node, targetNodes, null);
	}

	/** Returns a list that contains all nodes in a tree. */
	public static <E extends IConQATNode> List<E> listAllDepthFirst(E node) {
		return listDepthFirst(node, ETargetNodes.ALL);
	}

	/**
	 * Get array of numeric values stored at the leaves. The order is given by a
	 * depth-first search.
	 * 
	 * @param node
	 *            node to start from
	 * @param key
	 *            the key the values are stored at
	 * @throws ConQATException
	 *             if one of the values is <code>null</code> or not a numeric
	 *             value
	 */
	public static double[] getLeaveValues(IConQATNode node, String key)
			throws ConQATException {
		List<IConQATNode> nodes = TraversalUtils.listLeavesDepthFirst(node);
		double[] values = new double[nodes.size()];
		for (int i = 0; i < nodes.size(); i++) {
			values[i] = NodeUtils.getDoubleValue(nodes.get(i), key);
		}
		return values;
	}

	/** Creates a list of the leaf nodes under an {@link IConQATNode}. */
	private static class LeafListBuilder<E extends IConQATNode> implements
			INodeVisitor<E, NeverThrownRuntimeException> {

		/** List that stores leafs during traversal */
		private final List<E> leavesList = new ArrayList<E>();

		/** Adds visited leaf to list */
		@Override
		public void visit(E node) throws NeverThrownRuntimeException {
			leavesList.add(node);
		}
	}

	/** Counts visited nodes. */
	private static class NodeCounter implements
			INodeVisitor<IConQATNode, NeverThrownRuntimeException> {

		/** Node count. */
		private int nodeCount = 0;

		/** Increase node count. */
		@Override
		public void visit(IConQATNode node) throws NeverThrownRuntimeException {
			nodeCount++;
		}
	}

	/** Returns a mapping from node id to node for all nodes in the given tree. */
	public static <E extends IConQATNode> Map<String, E> createIdToNodeMap(
			E node) {
		IdToNodeMapBuilder<E> mapBuilder = new IdToNodeMapBuilder<E>();
		visitAllDepthFirst(mapBuilder, node);
		return mapBuilder.map;
	}

	/**
	 * Returns a mapping from node id to node for all leaf nodes in the given
	 * tree.
	 */
	public static <E extends IConQATNode> Map<String, E> createIdToLeafNodeMap(
			E node) {
		IdToNodeMapBuilder<E> mapBuilder = new IdToNodeMapBuilder<E>();
		visitLeavesDepthFirst(mapBuilder, node);
		return mapBuilder.map;
	}

	/** Creates a mapping from node ids to nodes. */
	private static class IdToNodeMapBuilder<T extends IConQATNode> implements
			INodeVisitor<T, NeverThrownRuntimeException> {

		/** The map being constructed. */
		private final Map<String, T> map = new HashMap<String, T>();

		/** {@inheritDoc} */
		@Override
		public void visit(T element) throws NeverThrownRuntimeException {
			String id = element.getId();
			map.put(id, element);
		}
	}
}