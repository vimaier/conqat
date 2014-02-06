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

import java.util.ArrayList;

import org.conqat.engine.core.core.ConQATException;

import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;

/**
 * Useful utility methods for performing operations on the {@link ConQATGraph}.
 * These were "externalized" here to reduce the size of the {@link ConQATGraph}
 * class.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 40415 $
 * @ConQAT.Rating GREEN Hash: 9493006A5664192E2E6B74ABBBA2B650
 */
public class ConQATGraphUtils {

	/**
	 * Adds all vertices to the graph itself and removes all hierarchy nodes.
	 * This is useful for rebuilding the hierarchy.
	 */
	public static void collapseHierarchy(ConQATGraph graph)
			throws ConQATException {
		for (ConQATVertex v : graph.getVertices()) {
			v.relocate(graph);
		}

		// we copy the child list before removal, because the list is implicitly
		// modified in the loop.
		for (ConQATGraphInnerNode node : new ArrayList<ConQATGraphInnerNode>(
				graph.getInnerNodes())) {
			node.remove();
		}
	}

	/**
	 * Returns the edge from the <code>source</code> to the <code>target</code>
	 * in the given graph, or null if it does not exist yet.
	 */
	public static DirectedSparseEdge findEdge(ConQATVertex source,
			ConQATVertex target) {
		return (DirectedSparseEdge) source.findEdge(target);
	}

	/**
	 * Returns the edge from the <code>source</code> to the <code>target</code>
	 * in the given graph. If none exists yet a new edge is created and
	 * returned.
	 */
	public static DirectedSparseEdge getOrCreateEdge(ConQATGraph graph,
			ConQATVertex source, ConQATVertex target) {
		DirectedSparseEdge result = findEdge(source, target);
		if (result != null) {
			return result;
		}
		return graph.addEdge(source, target);
	}

	/**
	 * Returns the vertex with the specified id. If the vertex does not exist
	 * yet, it is created.
	 */
	public static ConQATVertex getOrCreateVertex(ConQATGraph graph, String id)
			throws ConQATException {
		ConQATVertex conceptVertex = graph.getVertexByID(id);
		if (conceptVertex == null) {
			conceptVertex = graph.createVertex(id, id, graph);
		}
		return conceptVertex;
	}

}