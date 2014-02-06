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
import java.util.List;
import java.util.Set;

import org.conqat.engine.model_clones.model.IDirectedEdge;
import org.conqat.engine.model_clones.model.INode;
import org.conqat.lib.commons.assertion.CCSMPre;
import org.conqat.lib.commons.collections.IdentityHashSet;
import org.conqat.lib.commons.collections.ListMap;
import org.conqat.lib.commons.collections.PairList;

/**
 * Enumeration of all connected subgraphs of size k of a given graph.
 * 
 * @author $Author: deissenb $
 * @version $Rev: 36733 $
 * @ConQAT.Rating GREEN Hash: F06C3B75D9CDEA5B304BB26E829AF7AD
 */
public class SubgraphEnumerator {

	/** All nodes. */
	private final List<INode> nodes;

	/** Nodes that have been deleted. */
	private final Set<INode> deletedNodes = new IdentityHashSet<INode>();

	/** Preclustered outgoing edges. */
	private final ListMap<INode, IDirectedEdge> outgoingEdges = new ListMap<INode, IDirectedEdge>();

	/** Undirected adjacency information. */
	private final ListMap<INode, INode> adjacentNodes = new ListMap<INode, INode>();

	/** Size of the subgraphs (number of nodes) */
	private final int k;

	/**
	 * Constructor
	 * 
	 * @param k
	 *            the size of the generated subgraphs. Must be at least 2.
	 */
	private SubgraphEnumerator(Collection<INode> nodes,
			Collection<IDirectedEdge> edges, int k) {
		CCSMPre.isTrue(k >= 2, "k must be at least 2");

		this.nodes = new ArrayList<INode>(nodes);
		this.k = k;

		for (IDirectedEdge edge : edges) {
			outgoingEdges.add(edge.getSourceNode(), edge);

			adjacentNodes.add(edge.getSourceNode(), edge.getTargetNode());
			adjacentNodes.add(edge.getTargetNode(), edge.getSourceNode());
		}
	}

	/** Calculates all connected subgraphs consisting of exactly k nodes. */
	public static PairList<List<INode>, List<IDirectedEdge>> getConnectedSubGraphs(
			Collection<INode> nodes, Collection<IDirectedEdge> edges, int k) {
		return new SubgraphEnumerator(nodes, edges, k).getSubGraphs();
	}

	/** Returns a list of all subgraphs of size k. */
	private PairList<List<INode>, List<IDirectedEdge>> getSubGraphs() {
		PairList<List<INode>, List<IDirectedEdge>> subGraphs = new PairList<List<INode>, List<IDirectedEdge>>();
		for (INode node : nodes) {
			Set<INode> visitedNodes = new IdentityHashSet<INode>();
			visitedNodes.add(node);
			deletedNodes.add(node);

			enumerateConnectedSubGraphs(visitedNodes, subGraphs);
		}
		return subGraphs;
	}

	/** Returns the set of edges induced by the nodes. */
	private List<IDirectedEdge> getInducedEdges(List<INode> nodes) {
		List<IDirectedEdge> edgesInSubgraph = new ArrayList<IDirectedEdge>();
		for (INode node : nodes) {
			List<IDirectedEdge> edges = outgoingEdges.getCollection(node);
			if (edges != null) {
				for (IDirectedEdge edge : edges) {
					if (nodes.contains(edge.getTargetNode())) {
						edgesInSubgraph.add(edge);
					}
				}
			}
		}
		return edgesInSubgraph;
	}

	/**
	 * Enumerates all connected (sub)graphs of size {@link #k} from the
	 * <code>nodeSet</code> and add them to the <code>result</code>.
	 */
	private void enumerateConnectedSubGraphs(Set<INode> nodeSet,
			PairList<List<INode>, List<IDirectedEdge>> result) {

		Set<INode> neighbors = new IdentityHashSet<INode>();
		for (INode node : nodeSet) {
			List<INode> adj = adjacentNodes.getCollection(node);
			if (adj != null) {
				neighbors.addAll(adj);
			}
		}

		List<INode> neighborsList = new ArrayList<INode>();
		for (INode n : neighbors) {
			if (!deletedNodes.contains(n)) {
				neighborsList.add(n);
			}
		}

		int maxSize = k - nodeSet.size();
		deletedNodes.addAll(neighborsList);
		addNeighbors(nodeSet, neighborsList, 0, maxSize, result);
		deletedNodes.removeAll(neighborsList);
	}

	/**
	 * For a given set of nodes and their neighborhood, try all combinations of
	 * adding at most <code>maxSize</code> of them and recurse to
	 * {@link #enumerateConnectedSubGraphs(Set, PairList)}. If this way a graph
	 * of size {@link #k} is formed, it is added to the result.
	 */
	private void addNeighbors(Set<INode> nodes, List<INode> neighborsList,
			int index, int maxSize,
			PairList<List<INode>, List<IDirectedEdge>> result) {
		if (maxSize == 0) {
			List<INode> nodesList = new ArrayList<INode>(nodes);
			result.add(nodesList, getInducedEdges(nodesList));
			return;
		}

		if (neighborsList.isEmpty()) {
			return;
		}

		if (index >= neighborsList.size()) {
			enumerateConnectedSubGraphs(nodes, result);
			return;
		}

		addNeighbors(nodes, neighborsList, index + 1, maxSize, result);

		nodes.add(neighborsList.get(index));
		addNeighbors(nodes, neighborsList, index + 1, maxSize - 1, result);
		nodes.remove(neighborsList.get(index));
	}
}