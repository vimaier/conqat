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
package org.conqat.engine.model_clones.detection;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;
import org.conqat.lib.commons.collections.IdentityHashSet;
import org.conqat.engine.model_clones.detection.ModelCloneReporterMock.ModelClone;
import org.conqat.engine.model_clones.model.DirectedEdgeMock;
import org.conqat.engine.model_clones.model.IDirectedEdge;
import org.conqat.engine.model_clones.model.IModelGraph;
import org.conqat.engine.model_clones.model.INode;
import org.conqat.engine.model_clones.model.ModelGraphMock;
import org.conqat.engine.model_clones.model.NodeMock;

/**
 * Base class for tests of model clone detectors which only can detect clone
 * pairs.
 * 
 * @author hummelb
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 * @levd.rating GREEN Hash: 7A50A3B41E276BFE451559C8D2D125B5
 */
public abstract class ModelCloneTestBase extends TestCase {

	/** Run on a graph without any clones. */
	public void testNoClones() throws Exception {
		ModelGraphMock graph = new ModelGraphMock();
		for (int i = 0; i < 100; ++i) {
			graph.nodes.add(new NodeMock(i));
		}
		for (int j = 0; j < 100; ++j) {
			for (int k = j + 1; k < 100; k += 3) {
				graph.edges.add(new DirectedEdgeMock(graph.nodes.get(j),
						graph.nodes.get(k), "x"));
			}
		}
		ModelCloneReporterMock result = runDetection(graph, 2, 0);
		assertEquals(0, result.modelClones.size());
	}

	/** Run with a simple "obvious" clone which does not require any branching. */
	public void testSimpleClone() throws Exception {
		int numNodes = 2;

		ModelGraphMock graph = new ModelGraphMock();
		for (int i = 0; i < numNodes; ++i) {
			graph.nodes.add(new NodeMock(i));
			graph.nodes.add(new NodeMock(i));
		}
		// add unconnected nodes
		for (int i = 0; i < numNodes; ++i) {
			graph.nodes.add(new NodeMock(i));
		}
		for (int j = 0; j < numNodes; ++j) {
			for (int k = j + 1; k < numNodes; k += 3) {
				graph.edges.add(new DirectedEdgeMock(graph.nodes.get(2 * j),
						graph.nodes.get(2 * k), "x"));
				graph.edges.add(new DirectedEdgeMock(
						graph.nodes.get(2 * j + 1), graph.nodes.get(2 * k + 1),
						"x"));
			}
		}

		ModelCloneReporterMock result = runDetection(graph, 2, 0);

		// there should be exactly one large clone
		assertEquals(1, result.modelClones.size());
		ModelClone clone = result.modelClones.get(0);
		assertEquals(2, clone.nodes.size());
		assertEquals(numNodes, clone.nodes.get(0).size());
		assertIsClone(clone);
	}

	/** Ensures that the given clone is actually a clone. */
	protected static void assertIsClone(ModelClone clone) {
		checkCloneButNotSame(clone);

		// nodes may not occur multiple times
		Set<INode> all = new IdentityHashSet<INode>();
		for (int j = 0; j < clone.nodes.size(); ++j) {
			all.addAll(clone.nodes.get(j));
		}
		assertEquals(clone.nodes.get(0).size() * clone.nodes.size(), all.size());

		checkCloneEdges(clone);

		for (int j = 0; j < clone.nodes.size(); ++j) {
			assertConnected("Clone " + j + " must be connected!", clone.nodes
					.get(j), clone.edges.get(j));
		}
	}

	/**
	 * Checks that all nodes and edges are actually valid clones (i.e. have
	 * equal representatives) but are not the same objects.
	 */
	private static void checkCloneButNotSame(ModelClone clone) {
		for (int cloneIndex1 = 0; cloneIndex1 < clone.nodes.size(); ++cloneIndex1) {
			for (int cloneIndex2 = cloneIndex1 + 1; cloneIndex2 < clone.nodes
					.size(); ++cloneIndex2) {

				// corresponding nodes must have equal representative but must
				// not be same
				for (int i = 0; i < clone.nodes.get(0).size(); ++i) {
					assertEquals("Pair " + i + " (in " + cloneIndex1 + "/"
							+ cloneIndex2 + ")", clone.nodes.get(cloneIndex1)
							.get(i).getEquivalenceClassLabel(), clone.nodes
							.get(cloneIndex2).get(i).getEquivalenceClassLabel());
					assertNotSame("Pair " + i + " (in " + cloneIndex1 + "/"
							+ cloneIndex2 + ")", clone.nodes.get(cloneIndex1)
							.get(i), clone.nodes.get(cloneIndex2).get(i));
				}

				// corresponding edges must have equal representative but must
				// not be same
				for (int i = 0; i < clone.edges.get(0).size(); ++i) {
					assertEquals("Pair " + i + " (in " + cloneIndex1 + "/"
							+ cloneIndex2 + ")", clone.edges.get(cloneIndex1)
							.get(i).getEquivalenceClassLabel(), clone.edges
							.get(cloneIndex2).get(i).getEquivalenceClassLabel());
					assertNotSame("Pair " + i + " (in " + cloneIndex1 + "/"
							+ cloneIndex2 + ")", clone.edges.get(cloneIndex1)
							.get(i), clone.edges.get(cloneIndex2).get(i));
				}
			}
		}
	}

	/**
	 * This method checks that all edge lists are "closed" (i.e. only refer to
	 * nodes contained in the clone) and corresponding edges refer to
	 * corresponding nodes.
	 */
	private static void checkCloneEdges(ModelClone clone) {
		// prepare index lookup maps
		Map<INode, Integer> lookup = new IdentityHashMap<INode, Integer>();
		for (int j = 0; j < clone.nodes.size(); ++j) {
			for (int i = 0; i < clone.nodes.get(j).size(); ++i) {
				lookup.put(clone.nodes.get(j).get(i), i);
			}
		}

		for (int clone1 = 0; clone1 < clone.nodes.size(); ++clone1) {
			for (int clone2 = clone1 + 1; clone2 < clone.nodes.size(); ++clone2) {
				for (int i = 0; i < clone.edges.get(0).size(); ++i) {
					IDirectedEdge e1 = clone.edges.get(clone1).get(i);
					IDirectedEdge e2 = clone.edges.get(clone2).get(i);

					assertTrue("Pair " + i + " (in " + clone1 + "/" + clone2
							+ ")", lookup.containsKey(e1.getSourceNode()));
					assertTrue("Pair " + i + " (in " + clone1 + "/" + clone2
							+ ")", lookup.containsKey(e2.getSourceNode()));
					assertEquals("Pair " + i + " (in " + clone1 + "/" + clone2
							+ ")", lookup.get(e1.getSourceNode()), lookup
							.get(e2.getSourceNode()));

					assertTrue("Pair " + i + " (in " + clone1 + "/" + clone2
							+ ")", lookup.containsKey(e1.getTargetNode()));
					assertTrue("Pair " + i + " (in " + clone1 + "/" + clone2
							+ ")", lookup.containsKey(e2.getTargetNode()));
					assertEquals("Pair " + i + " (in " + clone1 + "/" + clone2
							+ ")", lookup.get(e1.getTargetNode()), lookup
							.get(e2.getTargetNode()));
				}
			}
		}
	}

	/** Assert that the graph given by the nodes and edges is connected. */
	private static void assertConnected(String message, List<INode> nodes,
			List<IDirectedEdge> edges) {
		Set<INode> seen = new IdentityHashSet<INode>();
		runDFS(nodes.get(0), seen, edges);
		assertEquals(message, nodes.size(), seen.size());
	}

	/** Run a DFS. */
	private static void runDFS(INode node, Set<INode> seen,
			List<IDirectedEdge> allowedEdges) {
		if (seen.contains(node)) {
			return;
		}
		seen.add(node);

		for (IDirectedEdge edge : allowedEdges) {
			if (edge.getSourceNode() == node) {
				runDFS(edge.getTargetNode(), seen, allowedEdges);
			} else if (edge.getTargetNode() == node) {
				runDFS(edge.getSourceNode(), seen, allowedEdges);
			}
		}
	}

	/**
	 * Asserts that the result contains exactly one clone with given number of
	 * occurrences and nodes.
	 */
	protected static void assertSingleClone(ModelCloneReporterMock result,
			int numOccurrences, int numNodes) {
		assertEquals(1, result.modelClones.size());
		ModelClone clone = result.modelClones.get(0);
		assertEquals(numOccurrences, clone.nodes.size());
		assertEquals(numNodes, clone.nodes.get(0).size());
		assertIsClone(clone);
	}

	/** Run the detection. */
	protected abstract ModelCloneReporterMock runDetection(IModelGraph graph,
			int minCloneSize, int minCloneWeight) throws Exception;
}