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

import java.util.Map;

import org.conqat.lib.commons.clone.DeepCloneException;
import org.conqat.lib.commons.collections.MemoryEfficientStringMap;

/**
 * This is a base class for {@link IConQATNode}s with a hierarchy having only
 * one type.
 * 
 * @author Benjamin Hummel
 * @author $Author: hummelb $
 * @version $Rev: 36404 $
 * @ConQAT.Rating GREEN Hash: CE1831898B94B733CCD1E0F57EEF62E3
 * 
 * @param <E>
 *            the type of node handled.
 */
public abstract class ConQATGeneralNodeBase<E extends ConQATGeneralNodeBase<E>>
		extends ConQATNodeBase implements IRemovableConQATNode {

	/** The list of child nodes (initialized lazily). */
	private Map<String, E> children = null;

	/** The parent of this node. */
	private E parent = null;

	/** (Empty) default constructor. */
	protected ConQATGeneralNodeBase() {
		// do nothing
	}

	/** Copy constructor. Recursively copies entire tree. */
	@SuppressWarnings("unchecked")
	protected ConQATGeneralNodeBase(ConQATGeneralNodeBase<E> node)
			throws DeepCloneException {
		super(node);
		if (node.children != null) {
			for (E e : node.children.values()) {
				addChild((E) e.deepClone());
			}
		}
	}

	/** Adds a child node. */
	@SuppressWarnings("unchecked")
	public void addChild(E childNode) {
		if (children == null) {
			children = new MemoryEfficientStringMap<E>();
		}

		if (children.containsKey(childNode.getName())) {
			throw new IllegalArgumentException(
					"A node of this name already exists: "
							+ childNode.getName());
		}
		children.put(childNode.getName(), childNode);
		childNode.setParent((E) this);
	}

	/** {@inheritDoc} */
	@Override
	public boolean hasChildren() {
		return children != null && !children.isEmpty();
	}

	/** {@inheritDoc} */
	@Override
	public E[] getChildren() {
		if (!hasChildren()) {
			return null;
		}
		E[] result = allocateArray(children.size());
		children.values().toArray(result);
		return result;
	}

	/** Returns the number of children. */
	public int getNumberOfChildren() {
		if (!hasChildren()) {
			return 0;
		}
		return children.size();
	}

	/** Returns the child node of the given name or null if none exists. */
	public E getNamedChild(String name) {
		if (!hasChildren()) {
			return null;
		}
		return children.get(name);
	}

	/**
	 * Creates a new array of given size and type <code>E</code> (template
	 * method).
	 */
	protected abstract E[] allocateArray(int size);

	/** {@inheritDoc} */
	@Override
	public E getParent() {
		return parent;
	}

	/** Sets the parent node of this. */
	protected void setParent(E parentNode) {
		parent = parentNode;
	}

	/** {@inheritDoc} */
	@Override
	@SuppressWarnings("unchecked")
	public void remove() {
		if (parent != null) {
			parent.removeChild((E) this);
			setParent(null);
		}
	}

	/** Removes the child of the given name. */
	protected void removeChild(E childNode) {
		if (hasChildren()) {
			children.remove(childNode.getName());
		}
	}
}