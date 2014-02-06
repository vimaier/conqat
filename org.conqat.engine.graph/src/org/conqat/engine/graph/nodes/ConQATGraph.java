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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.conqat.lib.commons.assertion.CCSMPre;
import org.conqat.lib.commons.clone.DeepCloneException;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.UnmodifiableSet;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.core.core.ConQATException;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;

/**
 * The ConQATGraph is a hierarchic graph (also known as a cluster graph). It
 * consists of a tree (the node hierarchy) whose leaves are the vertices of a
 * directed graph (the base graph) which are realized as
 * {@link IConQATGraphVertex} objects. The inner nodes of this tree are
 * {@link IConQATGraphInnerNode}s.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: E92A5C41CA2E2DAD9DA37FECE4535245
 */
public class ConQATGraph extends ConQATGraphInnerNode {

	/** Underlying JUNG graph. */
	protected final DirectedSparseGraph graph;

	/** Lookup table for finding vertices by their global id. */
	private final Map<String, ConQATVertex> vertexLookup = new HashMap<String, ConQATVertex>();

	/** Creates a new graph. */
	public ConQATGraph() {
		super("ConQATGraph", "<graph-root>");
		graph = new DirectedSparseGraph();
	}

	/** Creates a new graph with given name- */
	public ConQATGraph(String id, String name) {
		super(id, name);
		graph = new DirectedSparseGraph();
	}

	/** Copy constructor. */
	public ConQATGraph(ConQATGraph source) throws DeepCloneException {
		super(source);
		graph = (DirectedSparseGraph) source.graph.copy();

		for (Object o : graph.getVertices()) {
			ConQATVertex v = (ConQATVertex) o;
			// make sure this belongs to the right graph
			v.setConQATGraph(this);
			vertexLookup.put(v.getId(), v);
		}

		cloneHierarchy(this, source);
	}

	/**
	 * Duplicates the children lists of the source node and appends them to the
	 * target node.
	 */
	private void cloneHierarchy(ConQATGraphInnerNode target,
			ConQATGraphInnerNode source) throws DeepCloneException {
		try {
			for (ConQATVertex v : source.getChildVertices()) {
				target.addVertex(vertexLookup.get(v.getId()));
			}
			for (ConQATGraphInnerNode n : source.getInnerNodes()) {
				ConQATGraphInnerNode clone = new ConQATGraphInnerNode(n);
				cloneHierarchy(clone, n);
				target.addNode(clone);
			}
		} catch (ConQATException e) {
			throw new IllegalStateException(
					"This should not happen during cloning as the cloned "
							+ "graph is consistent!", e);
		}
	}

	/** Clone this graph. */
	@Override
	public ConQATGraph deepClone() throws DeepCloneException {
		return new ConQATGraph(this);
	}

	/**
	 * Returns a collection of all vertices for this graph. This is a collection
	 * of all leaf nodes for the whole graph, not only it's direct children.
	 */
	@SuppressWarnings("unchecked")
	public UnmodifiableSet<ConQATVertex> getVertices() {
		return CollectionUtils.asUnmodifiable(graph.getVertices());
	}

	/** Returns the set of edges for this graph. */
	@SuppressWarnings("unchecked")
	public Set<DirectedSparseEdge> getEdges() {
		return graph.getEdges();
	}

	/**
	 * Returns the underlying JUNG graph. It is not save to add or remove
	 * vertices using the returned graph, as those vertices will not be mirrored
	 * in the hierarchy!
	 */
	public DirectedSparseGraph getGraph() {
		return graph;
	}

	/** Returns the vertex for a given id, or null if no such node exists. */
	public ConQATVertex getVertexByID(String id) {
		return vertexLookup.get(id);
	}

	/**
	 * Creates a new vertex and appends it to the given parent.
	 * 
	 * @throws IllegalArgumentException
	 *             if a node which is disconnected (e.g. by a remove) is used as
	 *             the parent.
	 * @throws ConQATException
	 *             if a vertex of the given id already exists for this graph.
	 */
	public ConQATVertex createVertex(String id, String name,
			ConQATGraphInnerNode parent) throws ConQATException {
		if (vertexLookup.containsKey(id)) {
			throw new ConQATException("A vertex of this id already exists: "
					+ id);
		}
		if (parent != this && parent.getParent() == null) {
			throw new IllegalArgumentException(
					"May not used disconnected node as parent: "
							+ parent.getId());
		}

		ConQATVertex v = new ConQATVertex(id, name, this);
		vertexLookup.put(id, v);
		parent.addVertex(v);
		graph.addVertex(v);
		return v;
	}

	/** Inserts an edge between the two given nodes. */
	public DirectedSparseEdge addEdge(ConQATVertex from, ConQATVertex to) {
		DirectedSparseEdge edge = new DirectedSparseEdge(from, to);
		graph.addEdge(edge);
		return edge;
	}

	/** Removes the given vertex from the graph. */
	/* package */void removeVertexFromGraph(ConQATVertex vertex) {
		if (vertexLookup.containsKey(vertex.getId())) {
			vertexLookup.remove(vertex.getId());
			graph.removeVertex(vertex);
		}
	}

	/** Converts a {@link Vertex} into an {@link IConQATGraphNode} */
	public IConQATGraphNode asConQATNode(Vertex vertex) {
		CCSMPre.isInstanceOf(vertex, IConQATGraphNode.class);
		return (IConQATGraphNode) vertex;
	}

	/**
	 * Converts a {@link Vertex} into an {@link IConQATGraphNode} and returns
	 * its {@link IConQATNode#getId()}
	 */
	public String getVertexId(Vertex vertex) {
		return asConQATNode(vertex).getId();
	}
}