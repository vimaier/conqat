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

import junit.framework.TestCase;
import org.conqat.engine.graph.algo.StrongConnectivity;
import org.conqat.engine.graph.nodes.ConQATGraph;
import org.conqat.engine.graph.nodes.ConQATVertex;

/**
 * Tests for the strong connectivity algorithm.
 * 
 * @author Benjamin Hummel
 * @author Tilman Seifert
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @levd.rating GREEN Hash: 14AC7D82EF83B7C12D1C21FBE3AA69D7
 */
public class StrongConnectivityTest extends TestCase {

	/** Test the result for a DAG. */
	public void testDAG() throws Exception {
		ConQATGraph graph = new ConQATGraph();
		ConQATVertex[] v = new ConQATVertex[8];
		for (int i = 0; i < v.length; ++i) {
			v[i] = graph.createVertex("v" + i, "v" + i, graph);
		}

		graph.addEdge(v[0], v[1]);
		graph.addEdge(v[2], v[1]);
		graph.addEdge(v[1], v[3]);
		graph.addEdge(v[1], v[4]);
		graph.addEdge(v[5], v[6]);
		graph.addEdge(v[6], v[7]);

		StrongConnectivity scc = new StrongConnectivity(graph);
		assertEquals(8, scc.getNumComponents());
	}

	/** Test the result for a circle. */
	public void testCircle() throws Exception {
		ConQATGraph graph = new ConQATGraph();
		ConQATVertex[] v = new ConQATVertex[8];
		for (int i = 0; i < v.length; ++i) {
			v[i] = graph.createVertex("v" + i, "v" + i, graph);
		}

		for (int i = 0; i < v.length; ++i) {
			graph.addEdge(v[i], v[(i + 1) % v.length]);
		}

		StrongConnectivity scc = new StrongConnectivity(graph);
		assertEquals(1, scc.getNumComponents());
	}

	/** Test the result for a "complex" graph. */
	public void testComplex() throws Exception {
		ConQATGraph graph = new ConQATGraph();
		ConQATVertex[] v = new ConQATVertex[8];
		for (int i = 0; i < v.length; ++i) {
			v[i] = graph.createVertex("v" + i, "v" + i, graph);
		}

		// two components
		graph.addEdge(v[0], v[1]);

		// one component
		graph.addEdge(v[2], v[3]);
		graph.addEdge(v[3], v[2]);

		// two more components
		graph.addEdge(v[4], v[5]);
		graph.addEdge(v[5], v[4]);
		graph.addEdge(v[5], v[6]);
		graph.addEdge(v[6], v[7]);
		graph.addEdge(v[7], v[6]);

		StrongConnectivity scc = new StrongConnectivity(graph);
		assertEquals(5, scc.getNumComponents());
	}
}