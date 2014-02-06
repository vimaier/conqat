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
package org.conqat.engine.model_clones.detection.pairs;

import java.util.Arrays;

import org.conqat.engine.model_clones.detection.ModelCloneReporterMock;
import org.conqat.engine.model_clones.detection.ModelCloneTestBase;
import org.conqat.engine.model_clones.model.DirectedEdgeMock;
import org.conqat.engine.model_clones.model.INode;
import org.conqat.engine.model_clones.model.ModelGraphMock;
import org.conqat.engine.model_clones.model.NodeMock;

/**
 * Base class for tests of detectors that can only find clone pairs.
 * 
 * @author hummelb
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 * @levd.rating GREEN Hash: 113314E8D35E8953FCE750357F855BD5
 */
public abstract class PairDetectorTestBase extends ModelCloneTestBase {

	/**
	 * Test with a graph which requires branching and some care to not report
	 * too many clones.
	 */
	public void testBranchingCase() throws Exception {
		INode[] n1 = new INode[7];
		INode[] n2 = new INode[7];

		ModelGraphMock graph = createEdgelessGraph(n1, n2);

		for (int i = 0; i < 4; ++i) {
			graph.edges.add(new DirectedEdgeMock(n1[i], n1[i + 1], "x"));
			graph.edges.add(new DirectedEdgeMock(n2[i], n2[i + 1], "x"));
		}
		graph.edges.add(new DirectedEdgeMock(n1[2], n1[5], "x"));
		graph.edges.add(new DirectedEdgeMock(n2[2], n2[5], "x"));
		graph.edges.add(new DirectedEdgeMock(n1[5], n1[6], "x"));
		graph.edges.add(new DirectedEdgeMock(n2[5], n2[6], "x"));

		ModelCloneReporterMock result = runDetection(graph, 5, 0);

		// there should be exactly one clone of this size
		assertSingleClone(result, 2, 5);
	}

	/**
	 * Test with a graph which requires backward branching and some care to not
	 * report too many clones.
	 */
	public void testBackwardBranchingCase() throws Exception {
		INode[] n1 = new INode[7];
		INode[] n2 = new INode[7];

		ModelGraphMock graph = createEdgelessGraph(n1, n2);

		for (int i = 0; i < 4; ++i) {
			graph.edges.add(new DirectedEdgeMock(n1[i + 1], n1[i], "x"));
			graph.edges.add(new DirectedEdgeMock(n2[i + 1], n2[i], "x"));
		}
		graph.edges.add(new DirectedEdgeMock(n1[5], n1[2], "x"));
		graph.edges.add(new DirectedEdgeMock(n2[5], n2[2], "x"));
		graph.edges.add(new DirectedEdgeMock(n1[6], n1[5], "x"));
		graph.edges.add(new DirectedEdgeMock(n2[6], n2[5], "x"));

		ModelCloneReporterMock result = runDetection(graph, 5, 0);

		// there should be exactly one clone of this size
		assertSingleClone(result, 2, 5);
	}

	/**
	 * Creates the graph used in {@link #testBranchingCase()} and
	 * {@link #testBackwardBranchingCase()}.
	 */
	private ModelGraphMock createEdgelessGraph(INode[] n1, INode[] n2) {
		ModelGraphMock graph = new ModelGraphMock();
		for (int i = 0; i < 7; ++i) {
			n1[i] = new NodeMock("eq1");
			n2[i] = new NodeMock("eq1");
		}
		n1[0] = new NodeMock("foo");
		n2[0] = new NodeMock("bar");
		n1[4] = new NodeMock("baz");
		n2[6] = new NodeMock("nop");

		graph.nodes.addAll(Arrays.asList(n1));
		graph.nodes.addAll(Arrays.asList(n2));
		return graph;
	}

	/** Test whether the weight is taken into consideration. */
	public void testWeight() throws Exception {
		INode[] n1 = new INode[7];
		INode[] n2 = new INode[7];

		ModelGraphMock graph = new ModelGraphMock();
		for (int i = 0; i < 7; ++i) {
			n1[i] = new NodeMock("eq" + i, i + 2);
			n2[i] = new NodeMock("eq" + i, i + 2);
		}
		n1[0] = new NodeMock("foo");
		n2[0] = new NodeMock("bar");
		n1[6] = new NodeMock("baz");
		n2[6] = new NodeMock("nop");

		graph.nodes.addAll(Arrays.asList(n1));
		graph.nodes.addAll(Arrays.asList(n2));

		for (int i = 0; i < 4; ++i) {
			graph.edges.add(new DirectedEdgeMock(n1[i], n1[i + 1], "x"));
			graph.edges.add(new DirectedEdgeMock(n2[i], n2[i + 1], "x"));
		}
		graph.edges.add(new DirectedEdgeMock(n1[2], n1[5], "x"));
		graph.edges.add(new DirectedEdgeMock(n2[2], n2[5], "x"));
		graph.edges.add(new DirectedEdgeMock(n1[5], n1[6], "x"));
		graph.edges.add(new DirectedEdgeMock(n2[5], n2[6], "x"));

		// We expect a clone of size 5 and weight 25
		ModelCloneReporterMock result = runDetection(graph, 0, 26);
		assertEquals(0, result.modelClones.size());

		result = runDetection(graph, 0, 25);
		assertSingleClone(result, 2, 5);
	}
}