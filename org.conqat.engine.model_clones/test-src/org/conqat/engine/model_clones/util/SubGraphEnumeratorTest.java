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
package org.conqat.engine.model_clones.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.conqat.engine.model_clones.detection.util.GraphUtils;
import org.conqat.engine.model_clones.detection.util.SubgraphEnumerator;
import org.conqat.engine.model_clones.model.DirectedEdgeMock;
import org.conqat.engine.model_clones.model.IDirectedEdge;
import org.conqat.engine.model_clones.model.INode;
import org.conqat.engine.model_clones.model.TestGraphUtils;
import org.conqat.lib.commons.collections.PairList;
import org.conqat.lib.commons.math.MathUtils;
import org.conqat.lib.commons.test.CCSMTestCaseBase;

/**
 * Tests the {@link SubgraphEnumerator}.
 * 
 * @author $Author: deissenb $
 * @version $Rev: 36733 $
 * @ConQAT.Rating GREEN Hash: 0801FF43626BB5D082809BA3694090E3
 */
public class SubGraphEnumeratorTest extends CCSMTestCaseBase {

	/** Test subgraph enumeration on balanced binary tree. */
	public void testSubGraphEnumerationOnTree() {
		ArrayList<IDirectedEdge> edges = new ArrayList<IDirectedEdge>();
		int numberNodes = 15;
		List<INode> nodes = TestGraphUtils.createNodes(numberNodes, true);
		for (int j = 0; j <= 6; ++j) {
			edges.add(new DirectedEdgeMock(nodes.get(j), nodes.get(2 * j + 1),
					"x"));
			edges.add(new DirectedEdgeMock(nodes.get(j), nodes.get(2 * j + 2),
					"x"));
		}

		checkNumSubGraphs(nodes, edges, 3, 19);
		checkNumSubGraphs(nodes, edges, 2, 14);
	}

	/** Tests enumeration in clique graph. */
	public void testSubGraphEnumerationOnClique() {
		ArrayList<IDirectedEdge> edges = new ArrayList<IDirectedEdge>();

		int numberNodes = 6;
		List<INode> nodes = TestGraphUtils.createNodes(numberNodes, true);
		for (int j = 0; j < numberNodes; ++j) {
			for (int k = j + 1; k < numberNodes; k++) {
				edges.add(new DirectedEdgeMock(nodes.get(j), nodes.get(k), "x"));
			}
		}

		for (int i = 2; i <= 4; ++i) {
			checkNumSubGraphs(nodes, edges, i, MathUtils.choose(numberNodes, i));
		}
	}

	/** Test with random graph. */
	public void testRandomGraph() {
		int numberNodes = 50;
		int numberEdges = 2 * numberNodes;

		Random r = new Random(42);

		List<INode> nodes = TestGraphUtils.createNodes(numberNodes, true);
		List<IDirectedEdge> edges = new ArrayList<IDirectedEdge>();
		for (int i = 0; i < numberEdges; ++i) {
			edges.add(new DirectedEdgeMock(nodes.get(r.nextInt(numberNodes)),
					nodes.get(r.nextInt(numberNodes)), "x"));
		}

		long start = System.currentTimeMillis();
		checkNumSubGraphs(nodes, edges, 5, 7667);
		checkNumSubGraphs(nodes, edges, 4, 1620);
		checkNumSubGraphs(nodes, edges, 3, 358);
		System.err.println("Time required: "
				+ (System.currentTimeMillis() - start) / 1000.);
	}

	/** Checks the number of subgraphs of size k. */
	private void checkNumSubGraphs(List<INode> nodes,
			List<IDirectedEdge> edges, int k, int expected) {
		PairList<List<INode>, List<IDirectedEdge>> subGraphs = SubgraphEnumerator
				.getConnectedSubGraphs(nodes, edges, k);
		for (int i = 0; i < subGraphs.size(); ++i) {
			assertTrue(GraphUtils.isConnectedSmallGraph(subGraphs.getFirst(i),
					subGraphs.getSecond(i)));
			assertEquals(k, subGraphs.getFirst(i).size());
		}

		assertEquals("Wrong result for k = " + k, expected, subGraphs.size());
	}
}