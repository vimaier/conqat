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
package org.conqat.engine.commons.node;

import java.util.LinkedHashMap;

import org.conqat.lib.commons.clone.DeepCloneException;

/**
 * A simple ConQAT node with a hashed child list which is instantiated in a lazy
 * fashion.
 * 
 * @author Florian Deissenboeck
 * @author $Author: hummelb $
 * @version $Rev: 37013 $
 * @ConQAT.Rating GREEN Hash: FAEEF8B604EECF53077CBBF20BEEB13B
 */
public class SetNode<E> extends ConQATNodeBase implements
		IRemovableConQATNode {

	/** Children (lazy instantiation). */
	private LinkedHashMap<E, SetNode<E>> children;

	/** The parent of this node. */
	private SetNode<E> parent = null;

	/** Object stored by this node. */
	private final E object;

	/** Create new hashed node. */
	public SetNode(E object) {
		this.object = object;
	}

	/** Copy constructor. */
	protected SetNode(SetNode<E> node) throws DeepCloneException {
		super(node);
		this.object = node.object;
		if (node.hasChildren()) {
			for (SetNode<E> child : node.children.values()) {
				addChild(child.deepClone());
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	public String getId() {
		return object.toString();
	}

	/** Adds a new child node. */
	public void addChild(SetNode<E> node) {
		if (children == null) {
			children = new LinkedHashMap<E, SetNode<E>>();
		}
		children.put(node.object, node);
		node.setParent(this);
	}

	/** {@inheritDoc} */
	@Override
	public SetNode<E> deepClone() throws DeepCloneException {
		return new SetNode<E>(this);
	}

	/** Get child node by hash code. */
	public SetNode<E> getChild(E object) {
		if (children == null) {
			return null;
		}
		return children.get(object);
	}

	/** {@inheritDoc} */
	@Override
	public SetNode<E>[] getChildren() {
		if (children == null) {
			return null;
		}
		@SuppressWarnings("unchecked")
		SetNode<E>[] result = new SetNode[children.size()];
		children.values().toArray(result);
		return result;
	}

	/** Returns <code>toString()</code> of wrapped object. */
	@Override
	public String getName() {
		return object.toString();
	}

	/** {@inheritDoc} */
	@Override
	public boolean hasChildren() {
		if (children == null) {
			return false;
		}
		return !children.isEmpty();
	}

	/** {@inheritDoc} */
	@Override
	public void remove() {
		if (parent != null) {
			parent.removeChild(this);
		}
	}

	/** {@inheritDoc} */
	@Override
	public SetNode<E> getParent() {
		return parent;
	}

	/** Removes the child node given if it exists. */
	protected void removeChild(SetNode<E> childNode) {
		children.remove(childNode.object);
	}

	/** Sets the parent of this node. */
	protected void setParent(SetNode<E> parentNode) {
		this.parent = parentNode;
	}
}