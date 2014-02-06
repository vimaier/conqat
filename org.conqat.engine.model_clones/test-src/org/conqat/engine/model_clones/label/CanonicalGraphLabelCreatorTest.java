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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import junit.framework.TestCase;
import org.conqat.lib.commons.assertion.CCSMPre;
import org.conqat.lib.commons.collections.Pair;
import org.conqat.engine.model_clones.model.DirectedEdgeMock;
import org.conqat.engine.model_clones.model.IDirectedEdge;
import org.conqat.engine.model_clones.model.INode;
import org.conqat.engine.model_clones.model.NodeMock;
import org.conqat.engine.model_clones.model.TestGraphUtils;

/**
 * Tests for the {@link CanonicalLabelCreator}.
 * 
 * @author hummelb
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 * @levd.rating GREEN Hash: D4931CB319D6CC84F666BD9A9347FD35
 */
public class CanonicalGraphLabelCreatorTest extends TestCase {

	/** Perform tests with cliques to check whether trivial labels are created. */
	public void testLabelTriviality() {
		GraphLabel label = getLabelForClique(3, false, false);
		assertFalse("Small clique should not result in trivial label.", label
				.isTrivial());

		label = getLabelForClique(30, false, false);
		assertTrue(
				"Large clique takes too much calculation overhead -> trivial label.",
				label.isTrivial());

		label = getLabelForClique(30, true, false);
		assertFalse(
				"Large clique with node labels should not result in trivial label.",
				label.isTrivial());

		label = getLabelForClique(30, false, true);
		assertFalse(
				"Large clique with edge labels should not result in trivial label.",
				label.isTrivial());
	}

	/**
	 * Creates a clique graph (all nodes connected to all others) and
	 * calculates/returns the canonical label for it.
	 * 
	 * @param numNodes
	 *            the size of the graph (number of nodes).
	 * @param uniqueNodeLabels
	 *            whether the labels used for the nodes should be unique
	 *            (default is to use same labels for all).
	 * @param uniqueEdgeLabels
	 *            whether the labels used for the edges should be unique
	 *            (default is to use same labels for all).
	 */
	private GraphLabel getLabelForClique(int numNodes,
			boolean uniqueNodeLabels, boolean uniqueEdgeLabels) {
		List<INode> nodes = TestGraphUtils.createNodes(numNodes, uniqueNodeLabels);
		List<IDirectedEdge> edges = new ArrayList<IDirectedEdge>();
		for (int i = 0; i < numNodes; ++i) {
			for (int j = 0; j < numNodes; ++j) {
				if (i != j) {
					String edgeLabel = "e";
					if (uniqueEdgeLabels) {
						edgeLabel = i + " -> " + j;
					}
					edges.add(new DirectedEdgeMock(nodes.get(i), nodes.get(j),
							edgeLabel));
				}
			}
		}
		GraphLabel label = CanonicalLabelCreator
				.getCanonicalLabel(nodes, edges);
		return label;
	}

	/** Tests whether labels can be used for equivalence testing. */
	public void testEquivalence() {
		GraphLabel label1 = getLabelForRandomGraph(30, 100, false, 42);

		GraphLabel label2 = getLabelForRandomGraph(30, 100, false, 42);
		assertEquals("Same parameters should lead to same labels", label1,
				label2);

		GraphLabel label3 = getLabelForRandomGraph(30, 99, false, 42);
		assertFalse("Different number of edges leads to different labels",
				label1.equals(label3));

		GraphLabel label4 = getLabelForRandomGraph(30, 100, true, 42);
		assertFalse("Different node labels lead to different graph labels",
				label1.equals(label4));

		GraphLabel label5 = getLabelForRandomGraph(30, 100, false, 77);
		assertFalse("Different random seed leads to different labels", label1
				.equals(label5));
	}

	/**
	 * Test whether label creation works in the case of different node order.
	 * This case belongs to CR#3041.
	 */
	public void testSortingIssue() {
		INode n1 = new NodeMock(1);
		INode n2 = new NodeMock(2);
		INode n3 = new NodeMock(3);

		IDirectedEdge edge1 = new DirectedEdgeMock(n2, n3, "edge1");
		IDirectedEdge edge2 = new DirectedEdgeMock(n3, n1, "edge2");

		List<INode> nodesSorted = Arrays.asList(n1, n2, n3);
		List<IDirectedEdge> edgesSorted = Arrays.asList(edge1, edge2);
		GraphLabel labelSorted = CanonicalLabelCreator.getCanonicalLabel(
				nodesSorted, edgesSorted);

		List<INode> nodesUnsorted = Arrays.asList(n3, n1, n2);
		List<IDirectedEdge> edgesUnsorted = Arrays.asList(edge2, edge1);
		GraphLabel labelUnsorted = CanonicalLabelCreator.getCanonicalLabel(
				nodesUnsorted, edgesUnsorted);

		assertEquals(labelSorted, labelUnsorted);
		assertEquals(labelSorted.getTextualHash(), labelUnsorted
				.getTextualHash());
	}

	/**
	 * Test with large random graphs (performance check). Only assertion is for
	 * non-trivial labels.
	 */
	public void testLargeGraphs() {
		recordCalculationTime(100, 1000);
		recordCalculationTime(1000, 10000);
		recordCalculationTime(1000, 100000);
	}

	/** Records calculation time for canonical labels and prints it to console. */
	private void recordCalculationTime(int numNodes, int numEdges) {
		long start = System.currentTimeMillis();
		getLabelForRandomGraph(numNodes, numEdges, false, 16);
		System.err.println("Canonical label for graph with " + numNodes
				+ " nodes and " + numEdges + " edges calculated in "
				+ (System.currentTimeMillis() - start) / 1000. + " seconds");
	}

	/**
	 * Creates a random graph with desired number of nodes and edges and returns
	 * the canonical label of it.
	 */
	private GraphLabel getLabelForRandomGraph(int numNodes, int numEdges,
			boolean uniqueNodeLabels, long randomSeed) {
		List<INode> nodes = TestGraphUtils.createNodes(numNodes, uniqueNodeLabels);
		List<IDirectedEdge> edges = createRandomEdges(nodes, numEdges,
				randomSeed);
		GraphLabel label = CanonicalLabelCreator
				.getCanonicalLabel(nodes, edges);

		assertFalse("The labels for the random graph should not be trivial!",
				label.isTrivial());

		return label;
	}

	/** Creates a list of random edges for the nodes. */
	private List<IDirectedEdge> createRandomEdges(List<INode> nodes,
			int numEdges, long randomSeed) {
		CCSMPre.isTrue(nodes.size() * (nodes.size() - 1) >= numEdges,
				"Too many edges!");

		Random rand = new Random(randomSeed);

		Set<Pair<Integer, Integer>> existingEdges = new HashSet<Pair<Integer, Integer>>();
		List<IDirectedEdge> edges = new ArrayList<IDirectedEdge>();
		while (edges.size() < numEdges) {
			int i = rand.nextInt(nodes.size());
			int j = rand.nextInt(nodes.size());
			Pair<Integer, Integer> pair = new Pair<Integer, Integer>(i, j);

			if (i != j && existingEdges.add(pair)) {
				edges.add(new DirectedEdgeMock(nodes.get(i), nodes.get(j),
						"foo"));
			}
		}
		return edges;
	}

}