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

import org.conqat.lib.commons.clone.DeepCloneException;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.UnmodifiableCollection;
import org.conqat.engine.commons.node.ConQATNodeBase;
import org.conqat.engine.core.core.ConQATException;

/**
 * An inner node (hierarchy node) for the {@link ConQATGraph}. It has either
 * graph vertices or futher inner (hierarchy) node as children. Internally these
 * are managed separately.
 * 
 * @author Tilman Seifert
 * @author Benjamin Hummel
 * @author $Author: deissenb $
 * @version $Rev: 35147 $
 * @ConQAT.Rating GREEN Hash: 13A98543904EEEADC2EFCEBC373BC308
 */
public class ConQATGraphInnerNode extends ConQATNodeBase implements
		IConQATGraphInnerNode {

	/** The globally unique name of this node. */
	private final String id;

	/** The local name of this node. */
	private final String name;

	/** The parent node. */
	private ConQATGraphInnerNode parent;

	/** The children of this node. */
	private final HashMap<String, ConQATGraphInnerNode> children = new HashMap<String, ConQATGraphInnerNode>();

	/** The vertices for this node. */
	private final HashMap<String, ConQATVertex> vertices = new HashMap<String, ConQATVertex>();

	/**
	 * Creates a new inner node. Use the
	 * {@link #createChildNode(String, String)} method instead.
	 */
	/* package */ConQATGraphInnerNode(String id, String name) {
		this.id = id;
		this.name = name;
	}

	/**
	 * Copy constructor (without cloning of children). This is used from the
	 * {@link ConQATGraph} which handles the construction of the child nodes
	 * separately.
	 */
	protected ConQATGraphInnerNode(ConQATGraphInnerNode node)
			throws DeepCloneException {
		super(node);
		this.id = node.id;
		this.name = node.name;
	}

	/** Create a new node as a child of this node. */
	public ConQATGraphInnerNode createChildNode(String id, String name)
			throws ConQATException {
		ConQATGraphInnerNode node = new ConQATGraphInnerNode(id, name);
		addNode(node);
		return node;
	}

	/** Returns the description. */
	@Override
	public String getId() {
		return id;
	}

	/** Returns the description. */
	@Override
	public String getName() {
		return name;
	}

	/** {@inheritDoc} */
	@Override
	public boolean hasChildren() {
		return !children.isEmpty() || !vertices.isEmpty();
	}

	/** Returns an array with length 1, containing the root node. */
	@Override
	public IConQATGraphNode[] getChildren() {
		IConQATGraphNode[] result = new IConQATGraphNode[children.size()
				+ vertices.size()];
		int i = 0;
		for (ConQATGraphInnerNode c : children.values()) {
			result[i++] = c;
		}
		for (ConQATVertex v : vertices.values()) {
			result[i++] = v;
		}
		return result;
	}

	/** Add a vertex to this node. */
	/* package */void addVertex(ConQATVertex v) throws ConQATException {
		if (vertices.containsKey(v.getName())
				|| children.containsKey(v.getName())) {
			throw new ConQATException("A node with local name " + v.getName()
					+ " already exists!");
		}
		vertices.put(v.getName(), v);
		v.setParent(this);
	}

	/** Add a vertex to this node. */
	/* package */void addNode(ConQATGraphInnerNode n) throws ConQATException {
		if (vertices.containsKey(n.getName())
				|| children.containsKey(n.getName())) {
			throw new ConQATException("A node with local name " + n.getName()
					+ " already exists!");
		}
		children.put(n.getName(), n);
		n.setParent(this);
	}

	/** Sets the parent of this node. */
	/* package */void setParent(ConQATGraphInnerNode parent) {
		this.parent = parent;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws UnsupportedOperationException
	 *             may not clone this node (only the entire graph).
	 */
	@Override
	@SuppressWarnings("unused")
	public ConQATGraphInnerNode deepClone() throws DeepCloneException {
		throw new UnsupportedOperationException();
	}

	/** {@inheritDoc} */
	@Override
	public ConQATGraphInnerNode getParent() {
		return parent;
	}

	/** {@inheritDoc} */
	@Override
	public void remove() {
		/*
		 * We have to remove recursively, because vertices are also registered
		 * directly with the graph. So a simple "unlinking" from the tree is not
		 * sufficient.
		 */
		for (IConQATGraphNode n : getChildren()) {
			n.remove();
		}
		if (parent != null) {
			parent.removeChild(this);
			parent = null;
		}
	}

	/** Removes the given node from the list of children. */
	/* package */void removeChild(ConQATGraphInnerNode node) {
		children.remove(node.getName());
	}

	/** Removes the given node from the list of children. */
	/* package */void removeVertex(ConQATVertex vertex) {
		vertices.remove(vertex.getName());
	}

	/** Returns all inner nodes which are children of this node. */
	public UnmodifiableCollection<ConQATGraphInnerNode> getInnerNodes() {
		return CollectionUtils.asUnmodifiable(children.values());
	}

	/** Returns all vertices which are children of this node. */
	public UnmodifiableCollection<ConQATVertex> getChildVertices() {
		return CollectionUtils.asUnmodifiable(vertices.values());
	}

}