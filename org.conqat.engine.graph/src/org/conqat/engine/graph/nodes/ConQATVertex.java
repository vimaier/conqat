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

import org.conqat.engine.core.core.ConQATException;

import edu.uci.ics.jung.graph.ArchetypeGraph;
import edu.uci.ics.jung.graph.ArchetypeVertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;

/**
 * This is a child node for the ConQATGraphNode hierarchy and at the same time a
 * vertex for the base graph of the hierarchy.
 * 
 * @author Tilman Seifert
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 8640DE3CCF92C75B7E13F265A092874B
 */
public class ConQATVertex extends DirectedSparseVertex implements
		IConQATGraphVertex {

	/** The unique name. */
	private String id;

	/** The local name. */
	private String name;

	/** The parent node. */
	private ConQATGraphInnerNode parent;

	/** The ConQAT graph we belong to. */
	private ConQATGraph graph;

	/**
	 * Hidden constructor. Use the
	 * {@link ConQATGraph#createVertex(String, String, ConQATGraphInnerNode)}
	 * factory method instead.
	 * 
	 * @param id
	 *            the globally (in this graph) unique identifier.
	 * @param name
	 *            the local name of this vertex.
	 * @param graph
	 *            the ConQAT graph we belong to.
	 */
	/* package */ConQATVertex(String id, String name, ConQATGraph graph) {
		this.id = id;
		this.name = name;
		this.graph = graph;
	}

	/**g
	 * {@inheritDoc}
	 * <p>
	 * Augments copy by also duplicating id and name.
	 */
	@Override
	public ArchetypeVertex copy(ArchetypeGraph g) {
		ConQATVertex vertex = (ConQATVertex) super.copy(g);
		vertex.id = id;
		vertex.name = name;
		return vertex;
	}

	/**
	 * {@inheritDoc}}
	 * 
	 * @throws UnsupportedOperationException
	 *             don't try to clone a single vertex without the whole graph.
	 */
	@Override
	public IConQATGraphVertex deepClone() {
		throw new UnsupportedOperationException(
				"cloning is not supported by itself (only in conjunction "
						+ "with the whole graph)");
	}

	/** {@inheritDoc} */
	@Override
	public Object getValue(String key) {
		return getUserDatum(key);
	}

	/** {@inheritDoc} */
	@Override
	public void setValue(String key, Object value) {
		setUserDatum(key, value, DeepCloneCopyAction.getInstance());
	}

	/** {@inheritDoc} */
	@Override
	public String getId() {
		return id;
	}

	/** {@inheritDoc} */
	@Override
	public String getName() {
		return name;
	}

	/** Sets the local name of this node. */
	public void setName(String name) {
		this.name = name;
	}

	/** Sets the id of this node. */
	public void setId(String id) {
		this.id = id;
	}
	
	/** Returns <code>null</code>. */
	@Override
	public IConQATGraphNode[] getChildren() {
		return null;
	}

	/** Returns <code>false</code>. */
	@Override
	public boolean hasChildren() {
		return false;
	}

	/** Returns the parent node. */
	@Override
	public ConQATGraphInnerNode getParent() {
		return parent;
	}

	/** Set a the parent. */
	/* package */void setParent(ConQATGraphInnerNode parent) {
		this.parent = parent;
	}

	/** {@inheritDoc} */
	@Override
	public void remove() {
		if (parent != null) {
			parent.removeVertex(this);
		}
		graph.removeVertexFromGraph(this);
	}

	/** Attaches the vertex to another inner node. */
	public void relocate(ConQATGraphInnerNode newParent) throws ConQATException {
		if (parent != null) {
			parent.removeVertex(this);
		}
		newParent.addVertex(this);
	}

	/** This is used to fix the referenced ConQAT graph after cloning. */
	/* package */void setConQATGraph(ConQATGraph graph) {
		this.graph = graph;
	}
}