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
package org.conqat.engine.model_clones.detection.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.conqat.engine.model_clones.model.IDirectedEdge;
import org.conqat.engine.model_clones.model.IModelGraph;
import org.conqat.engine.model_clones.model.INode;
import org.conqat.lib.commons.assertion.CCSMPre;
import org.conqat.lib.commons.collections.IdManager;
import org.conqat.lib.commons.collections.ListMap;

/**
 * Wrapper class for a {@link IModelGraph} which augments it with additional
 * methods. This is introduced to have minimal requirements on the model graph
 * but make complex operations available, too.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 36637 $
 * @ConQAT.Rating GREEN Hash: 16BE45F1AF6D3B2C7DB3A99A53E4DE78
 */
public class AugmentedModelGraph {

	/** The underlying model graph. */
	private final IModelGraph graph;

	/** Mapping from nodes to their index. */
	private final Map<INode, Integer> nodeIndex = new IdentityHashMap<INode, Integer>();

	/** Mapping from nodes to equivalence class indices. */
	private final int[] nodeEq;

	/** Stores the number of edges for each node. */
	private final int[] numEdges;

	/** Mapping from edges to equivalence class indices. */
	private final Map<IDirectedEdge, Integer> edgeEq = new IdentityHashMap<IDirectedEdge, Integer>();

	/** Preclustered outgoing edges. */
	private final Map<INode, ListMap<Integer, IDirectedEdge>> outgoingEdgeClusters = new IdentityHashMap<INode, ListMap<Integer, IDirectedEdge>>();

	/** Preclustered incoming edges. */
	private final Map<INode, ListMap<Integer, IDirectedEdge>> incomingEdgeClusters = new IdentityHashMap<INode, ListMap<Integer, IDirectedEdge>>();

	/** Constructor. */
	public AugmentedModelGraph(IModelGraph graph) {
		this.graph = graph;
		int numNodes = graph.getNodes().size();
		nodeEq = new int[numNodes];
		numEdges = new int[numNodes];
		prepareNodeEq();
		prepareEdgeEq();
		prepareEdgeClusterings();
	}

	/** Setup the {@link #nodeEq} and {@link #nodeIndex} maps. */
	private void prepareNodeEq() {
		IdManager<Object> idManager = new IdManager<Object>();
		int counter = 0;
		for (INode node : graph.getNodes()) {
			nodeEq[counter] = idManager.obtainId(node
					.getEquivalenceClassLabel());
			nodeIndex.put(node, counter++);

			outgoingEdgeClusters.put(node,
					new ListMap<Integer, IDirectedEdge>());
			incomingEdgeClusters.put(node,
					new ListMap<Integer, IDirectedEdge>());
		}
	}

	/** Setup the {@link #edgeEq} map. */
	private void prepareEdgeEq() {
		IdManager<Object> idManager = new IdManager<Object>();
		for (IDirectedEdge edge : graph.getEdges()) {
			edgeEq.put(edge,
					idManager.obtainId(edge.getEquivalenceClassLabel()));
		}
	}

	/**
	 * Setup the {@link #outgoingEdgeClusters} and {@link #incomingEdgeClusters}
	 * .
	 */
	private void prepareEdgeClusterings() {
		IdManager<String> idManager = new IdManager<String>();
		for (IDirectedEdge edge : graph.getEdges()) {
			Integer edgeId = edgeEq.get(edge);
			Integer sourceIndex = nodeIndex.get(edge.getSourceNode());
			Integer targetIndex = nodeIndex.get(edge.getTargetNode());
			if (sourceIndex == null || targetIndex == null) {
				throw new IllegalStateException("Model not closed!");
			}

			numEdges[sourceIndex]++;
			numEdges[targetIndex]++;

			outgoingEdgeClusters.get(edge.getSourceNode()).add(
					idManager.obtainId(edgeId + "->" + nodeEq[targetIndex]),
					edge);
			incomingEdgeClusters.get(edge.getTargetNode()).add(
					idManager.obtainId(edgeId + "<-" + nodeEq[sourceIndex]),
					edge);
		}
	}

	/** Returns the edges for this graph. */
	public Collection<IDirectedEdge> getEdges() {
		return graph.getEdges();
	}

	/** Returns the nodes for this graph. */
	public Collection<INode> getNodes() {
		return graph.getNodes();
	}

	/** Returns the index of the given node. */
	public int getNodeIndex(INode node) {
		return nodeIndex.get(node);
	}

	/**
	 * Returns the edges of the given node clustered by equivalence of edge and
	 * target. The keys used to differentiate equivalence are unique for all
	 * nodes and edges and thus can be used to compare the results of several
	 * calls to this method. Only edges matching the direction as seen from the
	 * given node are returned.
	 */
	public ListMap<Integer, IDirectedEdge> getEdgeClusters(INode node,
			EDirection direction) {
		if (direction == EDirection.FORWARD) {
			return outgoingEdgeClusters.get(node);
		}
		return incomingEdgeClusters.get(node);
	}

	/**
	 * Creates and returns edge lists containing those forward edges all nodes
	 * have in common and that have the same equivalence and have source and
	 * target nodes which are mapped to each other. The mapping is determined by
	 * the order of the nodes in the lists.
	 * <p>
	 * Intuitively this takes the node sets of many clones and returns the
	 * maximal set of edges for each node set, such that the edges are also
	 * cloned.
	 * 
	 * @param nodeLists
	 *            The lists containing the nodes. All must be of same size.
	 */
	@SuppressWarnings("unchecked")
	public List<IDirectedEdge>[] calculateEquivalentEdges(
			List<INode>... nodeLists) {

		int numLists = nodeLists.length;
		CCSMPre.isFalse(numLists < 2, "Expected at least 2 lists!");

		int numNodes = nodeLists[0].size();
		for (int i = 1; i < numLists; ++i) {
			CCSMPre.isTrue(numNodes == nodeLists[i].size(),
					"All given lists must have the same size!");
		}

		// this map stores the node index for all nodes in the first list
		Map<INode, Integer> nodeIndex = new IdentityHashMap<INode, Integer>();
		for (int i = 0; i < numNodes; ++i) {
			nodeIndex.put(nodeLists[0].get(i), i);
		}

		// prepare the result array
		List<IDirectedEdge>[] result = new List[numLists];
		for (int i = 0; i < numLists; ++i) {
			result[i] = new ArrayList<IDirectedEdge>();
		}

		// these arrays store temporary values of the following computation for
		// each input list
		ListMap<Integer, IDirectedEdge>[] eqClusteredEdges = new ListMap[numLists];
		ListMap<INode, IDirectedEdge>[] targetClusteredEdges = new ListMap[numLists];
		List<IDirectedEdge>[] eqEdges = new List[numLists];

		// we iterate over all nodes and check their outgoing edges (this way we
		// visit all relevant edges)
		for (int currentNode = 0; currentNode < numNodes; ++currentNode) {

			// for each list get the outgoing edges of the current node
			// (clustered by edge equivalence class index)
			for (int listIndex = 0; listIndex < numLists; ++listIndex) {
				eqClusteredEdges[listIndex] = outgoingEdgeClusters
						.get(nodeLists[listIndex].get(currentNode));
			}

			// iterate over these clusters, as we only may map edges of the same
			// equivalence class to each other
			EQ_EDGELOOP: for (Integer key : eqClusteredEdges[0].getKeys()) {

				// now partition those equivalent edges again by their target
				for (int listIndex = 0; listIndex < numLists; ++listIndex) {
					targetClusteredEdges[listIndex] = createTargetMap(eqClusteredEdges[listIndex]
							.getCollection(key));
					if (targetClusteredEdges[listIndex] == null) {
						// if any of the edges lists is empty, there can be no
						// edge mapping and we can continue with the outer loop
						continue EQ_EDGELOOP;
					}
				}

				// now iterate over target nodes (of first list)
				TARGET_LOOP: for (INode targetNode : targetClusteredEdges[0]
						.getKeys()) {

					// edge's target is not in node set
					if (!nodeIndex.containsKey(targetNode)) {
						continue;
					}

					// for each other list get all edges from our cluster which
					// point to the node corresponding to targetNode
					int targetIndex = nodeIndex.get(targetNode);
					int minListSize = Integer.MAX_VALUE;
					for (int listIndex = 0; listIndex < numLists; ++listIndex) {
						eqEdges[listIndex] = targetClusteredEdges[listIndex]
								.getCollection(nodeLists[listIndex]
										.get(targetIndex));

						if (eqEdges[listIndex] == null
								|| eqEdges[listIndex].isEmpty()) {
							// if any list is empty, again we are finished with
							// this iteration
							continue TARGET_LOOP;
						}
						minListSize = Math.min(minListSize,
								eqEdges[listIndex].size());
					}

					// as all edges are equivalent and have corresponding
					// targets, we may map any of them to each other. we choose
					// the first minListSize from each list.
					for (int listIndex = 0; listIndex < numLists; ++listIndex) {
						for (int k = 0; k < minListSize; ++k) {
							result[listIndex].add(eqEdges[listIndex].get(k));
						}
					}
				}
			}
		}

		return result;
	}

	/** Builds a mapping from edges targets to all edges. */
	private ListMap<INode, IDirectedEdge> createTargetMap(
			List<IDirectedEdge> edges) {
		if (edges == null || edges.isEmpty()) {
			return null;
		}

		ListMap<INode, IDirectedEdge> targetMap = new ListMap<INode, IDirectedEdge>(
				new IdentityHashMap<INode, List<IDirectedEdge>>());
		for (IDirectedEdge edge : edges) {
			targetMap.add(edge.getTargetNode(), edge);
		}
		return targetMap;
	}

	/** Returns the (unique) index of the equivalence class of the given node. */
	public int getNodeEq(INode node) {
		return nodeEq[nodeIndex.get(node)];
	}

	/** Returns the number of edges for the given node. */
	public int getNumEdges(INode node) {
		return numEdges[nodeIndex.get(node)];
	}

	/**
	 * Returns the number of edges leading to / originating from a node not
	 * within {@code nodes}. Therefore, it returns the interface size of the
	 * clone represented by {@code nodes}.
	 * */
	public int getInterfaceSize(Collection<INode> nodes) {
		int result = 0;
		for (INode node : nodes) {
			if (outgoingEdgeClusters.get(node) != null) {
				for (IDirectedEdge edge : outgoingEdgeClusters.get(node)
						.getValues()) {
					if (!nodes.contains(edge.getTargetNode())) {
						result++;
					}
				}
			}
			if (incomingEdgeClusters.get(node) != null) {
				for (IDirectedEdge edge : incomingEdgeClusters.get(node)
						.getValues()) {
					if (!nodes.contains(edge.getSourceNode())) {
						result++;
					}
				}
			}
		}
		return result;
	}

}