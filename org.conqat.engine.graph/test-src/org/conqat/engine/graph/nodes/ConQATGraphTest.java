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
package org.conqat.engine.graph.nodes;

import junit.framework.TestCase;
import org.conqat.lib.commons.clone.DeepCloneException;
import org.conqat.engine.core.core.ConQATException;

/**
 * Tests for the ConQATGraph class.
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @levd.rating GREEN Hash: 334EB2C76F01E0238C16D02B4BCCA650
 */
public class ConQATGraphTest extends TestCase {

	/** Test building a simple graph. */
	public void testSimple() throws ConQATException {
		ConQATGraph graph = new ConQATGraph();
		ConQATGraphInnerNode node = graph.createChildNode("x", "y");
		ConQATVertex a = graph.createVertex("A", "A", graph);
		ConQATVertex b = graph.createVertex("B", "B", node);
		graph.addEdge(a, b);

		assertEquals(1, graph.getEdges().size());
		assertEquals(2, graph.getChildren().length);
		assertEquals(2, graph.getVertices().size());
		assertSame(a, graph.getVertexByID("A"));
		assertSame(b, graph.getVertexByID("B"));
	}

	/** Tests whether duplicate vertex IDs cause exceptions. */
	public void testDuplicateID() throws ConQATException {
		ConQATGraph graph = new ConQATGraph();
		ConQATGraphInnerNode node = graph.createChildNode("x", "y");
		graph.createVertex("A", "A", graph);

		try {
			graph.createVertex("A", "B", node);
			fail("Expected exception!");
		} catch (ConQATException e) {
			// expected
		}
	}

	/** Test whether the removal of vertices works as expected. */
	public void testVertexRemoval() throws ConQATException {
		ConQATGraph graph = new ConQATGraph();
		ConQATGraphInnerNode node = graph.createChildNode("x", "y");
		ConQATVertex a = graph.createVertex("A", "A", graph);
		ConQATVertex b = graph.createVertex("B", "B", node);
		graph.addEdge(a, b);

		b.remove();
		assertEquals(0, graph.getEdges().size());
		assertEquals(2, graph.getChildren().length);
		assertEquals(1, graph.getVertices().size());

		// double removal should not harm
		b.remove();
		assertEquals(0, graph.getEdges().size());
		assertEquals(2, graph.getChildren().length);
		assertEquals(1, graph.getVertices().size());

		// check that the ID was really freed
		b = graph.createVertex("B", "B", node);
		assertEquals(0, graph.getEdges().size());
		assertEquals(2, graph.getChildren().length);
		assertEquals(2, graph.getVertices().size());
	}

	/** Test whether removal of inner nodes works as expected. */
	public void testInnerNodeRemoval() throws ConQATException {
		ConQATGraph graph = new ConQATGraph();
		ConQATGraphInnerNode node = graph.createChildNode("x", "y");
		ConQATVertex a = graph.createVertex("A", "A", graph);
		ConQATVertex b = graph.createVertex("B", "B", node);
		graph.addEdge(a, b);

		node.remove();
		assertEquals(0, graph.getEdges().size());
		assertEquals(1, graph.getChildren().length);
		assertEquals(1, graph.getVertices().size());

		// double removal should not harm
		node.remove();
		assertEquals(0, graph.getEdges().size());
		assertEquals(1, graph.getChildren().length);
		assertEquals(1, graph.getVertices().size());

		// this should not work
		try {
			b = graph.createVertex("B", "B", node);
			fail("Expected exception!");
		} catch (IllegalArgumentException e) {
			// expected
		}

		// check that the ID was really freed
		node = graph.createChildNode("x", "y");
		b = graph.createVertex("B", "B", node);
		assertEquals(0, graph.getEdges().size());
		assertEquals(2, graph.getChildren().length);
		assertEquals(2, graph.getVertices().size());
	}

	/** Test whether cloning works as it should */
	public void testCloning() throws ConQATException, DeepCloneException {
		ConQATGraph graph = new ConQATGraph();
		ConQATGraphInnerNode node = graph.createChildNode("x", "y");
		ConQATVertex a = graph.createVertex("A", "A", graph);
		ConQATVertex b = graph.createVertex("B", "B", node);
		graph.addEdge(a, b);

		ConQATGraph clone = graph.deepClone();

		assertEquals(1, clone.getEdges().size());
		assertEquals(2, clone.getChildren().length);
		assertEquals(2, clone.getVertices().size());
		assertNotSame(a, clone.getVertexByID("A"));
		assertNotSame(b, clone.getVertexByID("B"));

		// test whether this is ok
		clone.getVertexByID("A").remove();
	}
}