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

import org.conqat.lib.commons.test.CCSMTestCaseBase;
import org.conqat.engine.model_clones.detection.util.GraphUtils;
import org.conqat.engine.model_clones.model.DirectedEdgeMock;
import org.conqat.engine.model_clones.model.IDirectedEdge;
import org.conqat.engine.model_clones.model.INode;
import org.conqat.engine.model_clones.model.TestGraphUtils;

/**
 * Tests for the {@link GraphUtils} class.
 * 
 * @author $Author: deissenb $
 * @version $Rev: 36620 $
 * @ConQAT.Rating GREEN Hash: 9A56526260E6BB532749C1AE9ADFF4CC
 */
public class GraphUtilsTest extends CCSMTestCaseBase {

	/** Tests connectivity check. */
	public void testConnectivity() {
		int numberNodes = 6;

		List<INode> nodes = TestGraphUtils.createNodes(numberNodes, true);

		ArrayList<IDirectedEdge> edges = new ArrayList<IDirectedEdge>();
		for (int j = 0; j < numberNodes; ++j) {
			for (int k = j + 1; k < numberNodes; k++) {
				edges
						.add(new DirectedEdgeMock(nodes.get(j), nodes.get(k),
								"x"));
			}
		}
		assertEquals(true, GraphUtils.isConnectedSmallGraph(nodes, edges));

		edges = new ArrayList<IDirectedEdge>();
		for (int j = 1; j < numberNodes; ++j) {
			for (int k = j + 1; k < numberNodes; k++) {
				edges
						.add(new DirectedEdgeMock(nodes.get(j), nodes.get(k),
								"x"));
			}
		}
		assertEquals(false, GraphUtils.isConnectedSmallGraph(nodes, edges));

		edges = new ArrayList<IDirectedEdge>();
		for (int j = 2; j < numberNodes; ++j) {
			for (int k = j + 1; k < numberNodes; k++) {
				edges
						.add(new DirectedEdgeMock(nodes.get(j), nodes.get(k),
								"x"));
			}
		}
		edges.add(new DirectedEdgeMock(nodes.get(0), nodes.get(1), "x"));
		assertEquals(false, GraphUtils.isConnectedSmallGraph(nodes, edges));
	}
}