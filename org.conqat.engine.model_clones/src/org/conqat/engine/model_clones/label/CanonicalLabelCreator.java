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
package org.conqat.engine.model_clones.label;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.conqat.engine.model_clones.detection.util.EDirection;
import org.conqat.engine.model_clones.model.IDirectedEdge;
import org.conqat.engine.model_clones.model.INode;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.ListMap;
import org.conqat.lib.commons.digest.Digester;
import org.conqat.lib.commons.digest.MD5Digest;
import org.conqat.lib.commons.math.MathUtils;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Computes a canonical label for a graph represented by a collection of nodes
 * and edges. The canonical label fulfills two properties:
 * <ul>
 * <li>1.) If the graphs are isomorphic, the labels are the same.</li>
 * <li>2.) If the labels are the same, the graphs are isomorphic.</li>
 * </ul>
 * <p>
 * As the computation of a canonical label takes exponential time in the worst
 * case, the running time is bounded and a simpler label is returned. This
 * simpler label still fulfills the first property, but errs on the second one
 * with a certain probability.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: D691A7F0F495DFDA3F703CD3E87E189A
 */
public class CanonicalLabelCreator {

	/** The maximum number of subsystem combinations */
	private static final int MAX_COMBINATIONS = 5040;

	/** The maximum size allowed for a single subset */
	private static final int MAX_SUBSET_SIZE = 7;

	/** The nodes we are working on. */
	private final Collection<? extends INode> nodes;

	/** The number of nodes. */
	private final int n;

	/** The edges we are working on. */
	private final Collection<? extends IDirectedEdge> edges;

	/** List of outgoing edges for each node. */
	private final ListMap<INode, IDirectedEdge> outgoingEdges;

	/** List of incoming edges for each node. */
	private final ListMap<INode, IDirectedEdge> incomingEdges;

	/**
	 * Variable for storing the best ordering found so far in
	 * {@link #computeCanonicalLabel(List)}.
	 */
	private List<INode> bestOrdering;

	/**
	 * Variable for storing the adjacency list of the best ordering found so far
	 * in {@link #computeCanonicalLabel(List)}.
	 */
	private BitSet bestAdjacency;

	/** Hidden constructor. */
	private CanonicalLabelCreator(Collection<? extends INode> nodes,
			Collection<? extends IDirectedEdge> edges) {
		this.nodes = nodes;
		n = nodes.size();
		this.edges = edges;

		this.outgoingEdges = buildEdgeLookup(edges, EDirection.FORWARD);
		this.incomingEdges = buildEdgeLookup(edges, EDirection.BACKWARD);
	}

	/** Creates the incoming / outgoing edge lookup maps. */
	private static ListMap<INode, IDirectedEdge> buildEdgeLookup(
			Collection<? extends IDirectedEdge> edges, EDirection dir) {
		ListMap<INode, IDirectedEdge> edgeLookup = new ListMap<INode, IDirectedEdge>();
		for (IDirectedEdge edge : edges) {
			if (dir == EDirection.FORWARD) {
				edgeLookup.add(edge.getSourceNode(), edge);
			} else {
				edgeLookup.add(edge.getTargetNode(), edge);
			}
		}
		return edgeLookup;
	}

	/**
	 * Returns a unique {@link GraphLabel} for the graph structure defined by
	 * the given collections of nodes and edges.
	 */
	public static GraphLabel getCanonicalLabel(
			Collection<? extends INode> nodes,
			Collection<? extends IDirectedEdge> edges) {
		CanonicalLabelCreator clc = new CanonicalLabelCreator(nodes, edges);
		return clc.computeCanonicalLabel(clc.partition());
	}

	/**
	 * Partitions the {@link #nodes} using iterative label calculation. The
	 * initial label is the node's equivalence label. The next label is the node
	 * label combined with the current labels of its adjacent nodes (combined
	 * with the labels of the edges leading there). This is repeated until no
	 * more separation can be achieved. To avoid too long strings, MD5 hashing
	 * is used.
	 */
	private List<List<INode>> partition() {
		Map<INode, String> nodeLabel = new IdentityHashMap<INode, String>();
		Set<String> usedLabels = new HashSet<String>();

		for (INode node : nodes) {
			String label = compactLabel(node.getEquivalenceClassLabel());
			nodeLabel.put(node, label);
			usedLabels.add(label);
		}

		int oldLabelSize = 0;
		while (usedLabels.size() > oldLabelSize) {
			oldLabelSize = usedLabels.size();
			usedLabels.clear();
			List<String> sublabels = new ArrayList<String>();
			Map<INode, String> newLabel = new IdentityHashMap<INode, String>();

			for (INode node : nodes) {
				StringBuilder labelBuilder = new StringBuilder();
				labelBuilder.append(nodeLabel.get(node));

				extendLabelViaEdges(labelBuilder, EDirection.FORWARD,
						nodeLabel, outgoingEdges.getCollection(node), sublabels);
				extendLabelViaEdges(labelBuilder, EDirection.BACKWARD,
						nodeLabel, incomingEdges.getCollection(node), sublabels);

				String label = compactLabel(labelBuilder.toString());
				newLabel.put(node, label);
				usedLabels.add(label);
			}

			nodeLabel = newLabel;
		}

		Map<String, List<INode>> partitions = new HashMap<String, List<INode>>();
		List<List<INode>> result = new ArrayList<List<INode>>();
		for (String label : CollectionUtils.sort(usedLabels)) {
			List<INode> list = new ArrayList<INode>();
			partitions.put(label, list);
			result.add(list);
		}

		for (INode node : nodes) {
			partitions.get(nodeLabel.get(node)).add(node);
		}
		return result;
	}

	/** Compacts a label using MD5 if this will make it shorter. */
	private String compactLabel(String label) {
		if (label.length() < 2 * MD5Digest.MD5_BYTES) {
			return label;
		}
		return Digester.createMD5Digest(label);
	}

	/**
	 * Extends the label by traversing the given list of edges in the direction
	 * requested and adding labels of edges and nodes.
	 */
	private void extendLabelViaEdges(StringBuilder label, EDirection direction,
			Map<INode, String> nodeLabel, List<IDirectedEdge> edgeList,
			List<String> sublabels) {
		sublabels.clear();
		label.append("$");
		if (edgeList != null) {
			for (IDirectedEdge edge : edgeList) {
				INode otherNode;
				if (direction == EDirection.FORWARD) {
					otherNode = edge.getTargetNode();
				} else {
					otherNode = edge.getSourceNode();
				}
				sublabels.add(edge.getEquivalenceClassLabel() + ": "
						+ nodeLabel.get(otherNode));
			}
		}
		// sorting is crucial here to make the label independent of edge order
		Collections.sort(sublabels);
		label.append(StringUtils.concat(sublabels, "|"));
	}

	/**
	 * Computes the canonical label by testing all possible permutations within
	 * each cluster of the partition and returning the one with the
	 * (lexicographically) smallest adjacency matrix.
	 */
	private GraphLabel computeCanonicalLabel(List<List<INode>> partition) {

		// return label without adjacency matrix is too many permutations would
		// need to be tested.
		if (getCombinations(partition) > MAX_COMBINATIONS) {
			List<INode> flat = new ArrayList<INode>();
			for (List<INode> list : partition) {
				flat.addAll(list);
			}
			return new GraphLabel(flat, outgoingEdges.getValues().size(), null);
		}

		Map<INode, Integer> nodeIndexMap = new IdentityHashMap<INode, Integer>();
		int i = 0;
		for (List<INode> list : partition) {
			for (INode node : list) {
				nodeIndexMap.put(node, i++);
			}
		}

		traversePermutations(new ArrayList<INode>(), partition, 0, 0,
				nodeIndexMap);

		return new GraphLabel(bestOrdering, edges.size(), bestAdjacency);
	}

	/**
	 * Returns the number of possible combinations to permute the partition.
	 * This respects the {@link #MAX_SUBSET_SIZE} and {@link #MAX_COMBINATIONS}
	 * values.
	 */
	private static long getCombinations(List<List<INode>> partition) {
		long combinations = 1;
		for (List<INode> subset : partition) {
			if (subset.size() > MAX_SUBSET_SIZE) {
				return MAX_COMBINATIONS + 1;
			}
			combinations *= MathUtils.factorial(subset.size());
			if (combinations > MAX_COMBINATIONS) {
				// abort to prevent overflow
				break;
			}
		}
		return combinations;
	}

	/**
	 * This method traverses all permutations of the given partition
	 * recursively. Each permutation is processed by passing it to
	 * {@link #checkOrdering(List, Map)}.
	 * 
	 * @param currentNodes
	 *            the current permutation being constructed element-wise.
	 * @param partition
	 *            the partition from which the permutation is constructed.
	 * @param clusterIndex
	 *            the index of the cluster in the partition from which elements
	 *            are currently taken.
	 * @param nodeIndex
	 *            the index in the current cluster. All elements before this
	 *            index have already been used.
	 * @param nodeIndexMap
	 *            unique mapping from node to index used for construction of
	 *            adjacency matrix.
	 */
	private void traversePermutations(List<INode> currentNodes,
			List<List<INode>> partition, int clusterIndex, int nodeIndex,
			Map<INode, Integer> nodeIndexMap) {
		if (currentNodes.size() == n) {
			checkOrdering(currentNodes, nodeIndexMap);
			return;
		}

		int nextClusterIndex = clusterIndex;
		int nextNodeIndex = nodeIndex + 1;
		List<INode> cluster = partition.get(clusterIndex);

		if (nextNodeIndex >= cluster.size()) {
			nextNodeIndex = 0;
			nextClusterIndex += 1;
		}

		for (int i = nodeIndex; i < cluster.size(); ++i) {
			swapNodes(cluster, nodeIndex, i, nodeIndexMap);
			currentNodes.add(cluster.get(nodeIndex));

			traversePermutations(currentNodes, partition, nextClusterIndex,
					nextNodeIndex, nodeIndexMap);

			// restore state
			currentNodes.remove(currentNodes.size() - 1);
			swapNodes(cluster, nodeIndex, i, nodeIndexMap);
		}
	}

	/**
	 * Swaps the nodes at given indices in the node list. The nodeIndexMap,
	 * which contains the current indices of the nodes in the global list is
	 * updated as well.
	 */
	private void swapNodes(List<INode> nodeList, int i1, int i2,
			Map<INode, Integer> nodeIndexMap) {
		Collections.swap(nodeList, i1, i2);
		INode node1 = nodeList.get(i1);
		INode node2 = nodeList.get(i2);
		Integer index1 = nodeIndexMap.get(node1);
		Integer index2 = nodeIndexMap.get(node2);
		nodeIndexMap.put(node1, index2);
		nodeIndexMap.put(node2, index1);
	}

	/**
	 * Checks a node ordering by computing the adjacency matrix. If the
	 * adjacency matrix is the largest (lexicographically) one encountered so
	 * far, both the list and the matrix are stored in local attributes.
	 * 
	 * @param nodeIndexMap
	 *            unique mapping from node to index used for construction of
	 *            adjacency matrix.
	 */
	private void checkOrdering(List<INode> orderedNodes,
			Map<INode, Integer> nodeIndexMap) {
		BitSet adjacencyMatrix = new BitSet(n * n);
		for (IDirectedEdge edge : edges) {
			int i = nodeIndexMap.get(edge.getSourceNode());
			int j = nodeIndexMap.get(edge.getTargetNode());
			adjacencyMatrix.set(i * n + j);
		}

		// keep current bestList/bestAdjacency if it exists and is
		// lexicographically larger
		if (bestOrdering != null) {
			BitSet xor = (BitSet) adjacencyMatrix.clone();
			xor.xor(bestAdjacency);
			int highestBit = xor.length() - 1;
			if (highestBit < 0 || bestAdjacency.get(highestBit)) {
				return;
			}
		}

		bestOrdering = new ArrayList<INode>(orderedNodes);
		bestAdjacency = adjacencyMatrix;
	}
}