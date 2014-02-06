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

import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.engine.model_clones.detection.ModelCloneReporterMock;
import org.conqat.engine.model_clones.detection.ModelCloneTestBase;
import org.conqat.engine.model_clones.detection.ModelCloneReporterMock.ModelClone;
import org.conqat.engine.model_clones.model.DirectedEdgeMock;
import org.conqat.engine.model_clones.model.ModelGraphMock;
import org.conqat.engine.model_clones.model.NodeMock;

/**
 * Base class for tests of "full" (unlimited) clone detectors.
 * 
 * @author hummelb
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 * @levd.rating GREEN Hash: D6AD1D016401E21C3981E6167CC83283
 */
public abstract class FullDetectorTestBase extends ModelCloneTestBase {

	/** Constant for controlling the size of the test case. */
	protected final static int BIG_SIZE = 3;

	/** Constant for controlling the size of the test case. */
	protected final static int SMALL_SIZE = 2;

	static {
		CCSMAssert.isTrue(SMALL_SIZE < BIG_SIZE, "must be less than BIG_SIZE!");
	}

	/** Constant for controlling the size of the test case. */
	protected final static int SIZE_DIFF = BIG_SIZE - SMALL_SIZE;

	/** The graph used for clone detection. */
	protected ModelGraphMock graph = null;

	/** {@inheritDoc} */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		graph = new ModelGraphMock();
	}

	/** Run with a simple "obvious" clone of size larger than 2. */
	public void testSimpleMultiClone() throws Exception {
		int size = 5;
		int numNodes = 100;

		for (int i = 0; i < numNodes; ++i) {
			for (int j = 0; j < size; ++j) {
				graph.nodes.add(new NodeMock(i));
			}
		}
		// add unconnected nodes
		for (int i = 0; i < numNodes; ++i) {
			graph.nodes.add(new NodeMock(i));
		}
		for (int j = 0; j < numNodes; ++j) {
			for (int k = j + 1; k < numNodes; k += 3) {
				for (int i = 0; i < size; ++i) {
					graph.edges.add(new DirectedEdgeMock(graph.nodes.get(size
							* j + i), graph.nodes.get(size * k + i), "x"));
				}
			}
		}

		ModelCloneReporterMock result = runDetection(graph, 2, 0);

		// there should be exactly one large clone
		assertSingleClone(result, size, numNodes);
	}

	/** Run with a clone which is partially hidden by other clones. */
	@SuppressWarnings("null")
	public void testPartiallyHiddenClone() throws Exception {
		NodeMock prev = null;
		for (int i = 0; i < BIG_SIZE; ++i) {
			NodeMock node = new NodeMock(i);
			graph.nodes.add(node);
			if (prev != null) {
				graph.edges.add(new DirectedEdgeMock(prev, node, "x"));
			}
			prev = node;
		}

		prev = null;
		for (int i = 0; i < BIG_SIZE; ++i) {
			NodeMock node = new NodeMock(SIZE_DIFF + i);
			graph.nodes.add(node);
			if (prev != null) {
				graph.edges.add(new DirectedEdgeMock(prev, node, "x"));
			}
			prev = node;
		}

		prev = null;
		for (int i = 0; i < BIG_SIZE + SIZE_DIFF; ++i) {
			NodeMock node = new NodeMock(i);
			graph.nodes.add(node);
			if (prev != null) {
				graph.edges.add(new DirectedEdgeMock(prev, node, "x"));
			}
			prev = node;
		}

		ModelCloneReporterMock result = runDetection(graph, SMALL_SIZE, 0);

		// there should be 3 clones: two large one and a smaller with 3 parts
		assertEquals(3, result.modelClones.size());

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
}