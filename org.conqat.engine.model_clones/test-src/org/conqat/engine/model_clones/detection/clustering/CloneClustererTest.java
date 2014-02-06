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
package org.conqat.engine.model_clones.detection.clustering;

import org.conqat.engine.model_clones.detection.ModelCloneReporterMock;
import org.conqat.engine.model_clones.detection.ModelCloneReporterMock.ModelClone;
import org.conqat.engine.model_clones.detection.pairs.PairDetector;
import org.conqat.engine.model_clones.detection.util.AugmentedModelGraph;
import org.conqat.engine.model_clones.model.DirectedEdgeMock;
import org.conqat.engine.model_clones.model.IModelGraph;
import org.conqat.engine.model_clones.model.NodeMock;
import org.conqat.engine.core.logging.testutils.LoggerMock;

/**
 * Tests for the {@link CloneClusterer}.
 * 
 * @author hummelb
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 * @levd.rating GREEN Hash: 4EC4703FBA64D90C0314C5D715BA3C43
 */
public class CloneClustererTest extends FullDetectorTestBase {

	/**
	 * This test case documents the limits of our heuristic clone detection
	 * approach. Although the clone should be found by a "perfect" solution,
	 * ours does not currently detect it.
	 * <p>
	 * Run with a clone which is completely hidden by 3 other clones. This is
	 * different from {@link #failingTestCompletelyHiddenClone4()}, as here we
	 * find no X clones.
	 */
	@SuppressWarnings("null")
	public void failingTestCompletelyHiddenClone3() throws Exception {
		make2TailedLine("A", "X", "B");
		make2TailedLine("B", "X", "C");
		make2TailedLine("C", "X", "A");

		// With this configuration we should find AX, BX, CX all twice.
		// Do we also find X three times?
		ModelCloneReporterMock result = runDetection(graph, SMALL_SIZE, 0);

		// there should be 4 clones: 3 large ones and a smaller one with 3 parts
		assertEquals(4, result.modelClones.size());

		ModelClone smallClone = null;
		for (ModelClone clone : result.modelClones) {
			if (clone.nodes.get(0).size() == SMALL_SIZE) {
				assertNull("There must be only one small clone", smallClone);
				smallClone = clone;
			} else {
				assertEquals(2, clone.nodes.size());
				assertEquals(BIG_SIZE, clone.nodes.get(0).size());
			}
			assertIsClone(clone);
		}

		assertNotNull("There must be a small clone!", smallClone);
		assertEquals("The small clone should be found 3 times!", 3,
				smallClone.nodes.size());
	}

	/**
	 * Helper method for creating lines of the kind (A-A- | B-B-)-X-X-X, where
	 * A, X, B are the three given classes.
	 */
	private void make2TailedLine(String class1, String class2, String class3) {

		NodeMock base = new NodeMock(class2);
		graph.nodes.add(base);
		NodeMock prev = base;
		for (int i = 1; i < BIG_SIZE - SIZE_DIFF; ++i) {
			NodeMock node = new NodeMock(class2);
			graph.nodes.add(node);
			graph.edges.add(new DirectedEdgeMock(prev, node, "x"));
			prev = node;
		}

		prev = null;
		for (int i = 0; i < SIZE_DIFF; ++i) {
			NodeMock node = new NodeMock(class1);
			graph.nodes.add(node);
			if (prev != null) {
				graph.edges.add(new DirectedEdgeMock(prev, node, "x"));
			}
			prev = node;
		}
		graph.edges.add(new DirectedEdgeMock(prev, base, "x"));

		prev = null;
		for (int i = 0; i < SIZE_DIFF; ++i) {
			NodeMock node = new NodeMock(class3);
			graph.nodes.add(node);
			if (prev != null) {
				graph.edges.add(new DirectedEdgeMock(prev, node, "x"));
			}
			prev = node;
		}
		graph.edges.add(new DirectedEdgeMock(prev, base, "x"));
	}

	/**
	 * This test case documents the limits of our heuristic clone detection
	 * approach. Although the clone should be found by a "perfect" solution,
	 * ours does not currently detect it.
	 * <p>
	 * Run with a clone which is completely hidden by four other clones. This is
	 * different from {@link #failingTestCompletelyHiddenClone3()}, as here we
	 * find two clone pairs.
	 */
	@SuppressWarnings("null")
	public void failingTestCompletelyHiddenClone4() throws Exception {

		makeClassedLine("A", "X", "B");
		makeClassedLine("A", "X", "D");
		makeClassedLine("C", "X", "D");
		makeClassedLine("C", "X", "B");

		// With this configuration we should find AX, XB, CX, XD all twice.
		// Do we also find X four times?
		ModelCloneReporterMock result = runDetection(graph, SMALL_SIZE, 0);

		// there should be 5 clones: 4 large ones and a smaller with 3 parts
		assertEquals(5, result.modelClones.size());

		ModelClone smallClone = null;
		for (ModelClone clone : result.modelClones) {
			if (clone.nodes.get(0).size() == SMALL_SIZE) {
				assertNull("There must be only one small clone", smallClone);
				smallClone = clone;
			} else {
				assertEquals(2, clone.nodes.size());
				assertEquals(BIG_SIZE, clone.nodes.get(0).size());
			}
			assertIsClone(clone);
		}

		assertNotNull("There must be a small clone!", smallClone);
		assertEquals("The small clone should be found 4 times!", 4,
				smallClone.nodes.size());
	}

	/**
	 * Helper method for creating lines of the kind A-A-X-X-X-B-B, where A, X, B
	 * are the three given classes.
	 */
	private void makeClassedLine(String class1, String class2, String class3) {
		NodeMock prev = null;
		for (int i = 0; i < BIG_SIZE + SIZE_DIFF; ++i) {
			String eq;
			if (i < SIZE_DIFF) {
				eq = class1;
			} else if (i < BIG_SIZE) {
				eq = class2;
			} else {
				eq = class3;
			}
			NodeMock node = new NodeMock(eq);
			graph.nodes.add(node);
			if (prev != null) {
				graph.edges.add(new DirectedEdgeMock(prev, node, "x"));
			}
			prev = node;
		}
	}

	/** {@inheritDoc} */
	@Override
	protected ModelCloneReporterMock runDetection(IModelGraph graph,
			int minCloneSize, int minCloneWeight) throws Exception {
		AugmentedModelGraph mag = new AugmentedModelGraph(graph);
		ModelCloneReporterMock result = new ModelCloneReporterMock();
		CloneClusterer clusterer = new CloneClusterer(mag, result,
				new LoggerMock(), true);
		new PairDetector(mag, minCloneSize, minCloneWeight, true, clusterer,
				new LoggerMock()).execute();
		clusterer.performInclusionAnalysis();
		clusterer.performClustering();
		return result;
	}
}