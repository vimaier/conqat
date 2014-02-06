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
package org.conqat.engine.resource.base;

import java.util.Map;

import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.resource.IContainer;
import org.conqat.engine.resource.IElement;
import org.conqat.engine.resource.IResource;
import org.conqat.engine.resource.util.UniformPathUtils;
import org.conqat.lib.commons.assertion.CCSMPre;
import org.conqat.lib.commons.assertion.PreconditionException;
import org.conqat.lib.commons.clone.DeepCloneException;
import org.conqat.lib.commons.collections.MemoryEfficientStringMap;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Base class for containers.
 * 
 * @param <E>
 *            children of the container
 * 
 * @author $Author: juergens $
 * @version $Rev: 35198 $
 * @ConQAT.Rating GREEN Hash: 02B29015CCE4354B2ECE89DEB85E7A21
 */
public abstract class ContainerBase<E extends IResource> extends ResourceBase
		implements IContainer {

	/**
	 * The list of child nodes. The key is determined by
	 * {@link IConQATNode#getName()} of the value.
	 */
	private final Map<String, E> children = new MemoryEfficientStringMap<E>();

	/** Container name. */
	private final String name;

	/** Constructor. */
	protected ContainerBase(String name) {
		this.name = name;
	}

	/** Copy constructor. */
	@SuppressWarnings("unchecked")
	protected ContainerBase(ContainerBase<E> other) throws DeepCloneException {
		super(other);
		name = other.name;
		for (E child : other.children.values()) {
			addChild((E) child.deepClone());
		}
	}

	/** Get child with provided name or null. */
	public E getNamedChild(String name) {
		return children.get(name);
	}

	/** {@inheritDoc} */
	@Override
	public void removeChild(IResource resource) {
		children.remove(resource.getName());
	}

	/** {@inheritDoc} */
	@Override
	public boolean hasChildren() {
		return !children.isEmpty();
	}

	/** {@inheritDoc} */
	@Override
	public E[] getChildren() {
		if (children.isEmpty()) {
			return null;
		}
		return children.values().toArray(allocateArray(children.size()));
	}

	/**
	 * Creates a new array of given size and type <code>E</code> (template
	 * method).
	 */
	protected abstract E[] allocateArray(int size);

	/** {@inheritDoc} */
	@Override
	public final String getName() {
		return name;
	}

	/** {@inheritDoc} */
	@Override
	public String getId() {
		if (getParent() == null) {
			return name;
		}

		String parentId = getParent().getId();
		if (StringUtils.isEmpty(parentId)) {
			return name;
		}

		return parentId + getPathSeparator() + name;
	}

	/**
	 * Returns the separator used when constructing the id from the hierarchical
	 * names. This can be changed by subclasses. The default implementation
	 * returns the slash.
	 */
	protected String getPathSeparator() {
		return "/";
	}

	/**
	 * Adds a child node.
	 * 
	 * @throws PreconditionException
	 *             is a child with the same name already exists.
	 */
	public void addChild(E childNode) {
		String childName;
		if (childNode instanceof IElement) {
			childName = getElementName((IElement) childNode);
		} else {
			childName = childNode.getName();
		}

		CCSMPre.isFalse(children.containsKey(childName),
				"A node of this name already exists: " + childName);
		children.put(childName, childNode);
		childNode.setParent(this);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Constructs the id from the id of the container, the
	 * {@link #getPathSeparator()} and the {@link #getElementName(IElement)}.
	 */
	@Override
	public String getElementId(IElement element) {
		String id = getId();
		if (StringUtils.isEmpty(id)) {
			return getElementName(element);
		}
		return id + getPathSeparator() + getElementName(element);
	}

	/** {@inheritDoc} */
	@Override
	public String getElementName(IElement element) {
		return UniformPathUtils.getElementName(element.getUniformPath());
	}
}