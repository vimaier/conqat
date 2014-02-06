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
package org.conqat.engine.graph.algo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.conqat.engine.graph.nodes.ConQATGraph;
import org.conqat.engine.graph.nodes.ConQATVertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;

/**
 * Implementation of the standard algorithm for finding strongly connected
 * components as described in Cormen et al.: Introduction to Algorithms.
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 08214CA3501AC9C6D71654AB7AE07FD4
 */
public class StrongConnectivity {

	/** The number of nodes. */
	private final int n;

	/** The list of all nodes in the graph. */
	private final ArrayList<ConQATVertex> nodes;

	/** Reverse lookup from node to index. */
	private final Map<ConQATVertex, Integer> nodeLookup = new HashMap<ConQATVertex, Integer>();

	/** The number of components found. */
	private int numComponents = 0;

	/** The component index for each node. */
	private final int[] nodeComponent;

	/**
	 * For each node the "finish time" of the DFS as defined in Cormen et al. A
	 * value of 0 means this node has not yet been visited, -1 indicates that
	 * the node was visited but the finish time was not yet decided, and a
	 * positive value gives the finish time.
	 */
	private final int[] finishTime;

	/** "Time" used for calculating the finish time. */
	private int time = 0;

	/**
	 * Constructor. This will start the algorihm on the provided graph. The time
	 * and space complexity is O(n+m) for a graph with n nodes and m edges.
	 * 
	 * @param graph
	 *            the graph to find the components for.
	 */
	public StrongConnectivity(ConQATGraph graph) {
		nodes = new ArrayList<ConQATVertex>(graph.getVertices());
		n = nodes.size();
		for (int i = 0; i < n; ++i) {
			nodeLookup.put(nodes.get(i), i);
		}

		nodeComponent = new int[n];
		// use different names for the same array to make the actual purpose
		// more clear.
		finishTime = nodeComponent;

		calculateComponents();
	}

	/** Perform the calculation of the stringly connected components. */
	private void calculateComponents() {
		// run first DFS to calculate finish time
		for (int i = 0; i < n; ++i) {
			if (finishTime[i] == 0) {
				finishTime[i] = -1;
				calculateFinishTime(i);
			}
		}

		// sort nodes by finish time
		int[] orderedNodes = new int[n];
		for (int i = 0; i < n; ++i) {
			orderedNodes[finishTime[i] - 1] = i;
		}

		// This reset is required as it contained the finish times.
		Arrays.fill(nodeComponent, -1);

		// run second DFS for calculating component indices
		for (int i = n - 1; i >= 0; --i) {
			int v = orderedNodes[i];
			if (nodeComponent[v] < 0) {
				nodeComponent[v] = numComponents;
				labelComponents(v);
				++numComponents;
			}
		}
	}

	/**
	 * The first DFS for calculating finish times, i.e. the relative time when
	 * the vertex was finally left during backtracking.
	 * 
	 * @param node
	 *            the node currently visited.
	 */
	private void calculateFinishTime(int node) {
		for (Object o : nodes.get(node).getOutEdges()) {
			DirectedSparseEdge edge = (DirectedSparseEdge) o;
			ConQATVertex succNode = (ConQATVertex) edge.getDest();
			int succ = nodeLookup.get(succNode);

			if (finishTime[succ] == 0) {
				finishTime[succ] = -1;
				calculateFinishTime(succ);
			}
		}
		finishTime[node] = ++time;
	}

	/**
	 * The second DFS operating on the transposed graph (i.e. all edges are
	 * taken reversed) to label components.
	 * 
	 * @param node
	 *            the node currently visited.
	 */
	private void labelComponents(int node) {
		for (Object o : nodes.get(node).getInEdges()) {
			DirectedSparseEdge edge = (DirectedSparseEdge) o;
			ConQATVertex predNode = (ConQATVertex) edge.getSource();
			int pred = nodeLookup.get(predNode);

			if (nodeComponent[pred] < 0) {
				nodeComponent[pred] = numComponents;
				labelComponents(pred);
			}
		}
	}

	/** Returns the number of strongly connected components found. */
	public int getNumComponents() {
		return numComponents;
	}

	/**
	 * Returns an array containing the size of each strongly connected component
	 * (i.e. the number of nodes contained). As this required linear time the
	 * result should be reused if needed.
	 */
	public int[] getComponentSizes() {
		int[] componentSizes = new int[numComponents];
		for (int i = 0; i < n; ++i) {
			componentSizes[nodeComponent[i]] += 1;
		}
		return componentSizes;
	}

	/** Returns the component index for a given node. */
	public int getComponent(ConQATVertex node) {
		Integer index = nodeLookup.get(node);
		if (index == null) {
			throw new IllegalArgumentException("Unknown node!");
		}
		return nodeComponent[index];
	}

	/**
	 * Puts the component index of each node into its key value space using the
	 * provided key name.
	 */
	public void labelGraphNodes(String keyName) {
		for (int i = 0; i < n; ++i) {
			nodes.get(i).setValue(keyName, nodeComponent[i]);
		}
	}
}