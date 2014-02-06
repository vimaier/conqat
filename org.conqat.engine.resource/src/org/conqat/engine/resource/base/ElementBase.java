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

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.IContentAccessor;
import org.conqat.engine.resource.IElement;
import org.conqat.engine.resource.IResource;
import org.conqat.lib.commons.clone.DeepCloneException;
import org.conqat.lib.commons.test.ADeepCloneTestExclude;

/**
 * Base class for elements.
 * <p>
 * Equality of elements ({@link #hashCode()} and {@link #equals(Object)}
 * methods) is solely based on the uniform path.
 * 
 * @author $Author: deissenb $
 * @version $Rev: 44767 $
 * @ConQAT.Rating GREEN Hash: ABB88C20C494B8137A575D2E66096D76
 */
public abstract class ElementBase extends ResourceBase implements IElement {

	/** The content accessor. */
	private final IContentAccessor accessor;

	/** Constructor */
	protected ElementBase(IContentAccessor accessor) {
		this.accessor = accessor;
	}

	/**
	 * Copy constructor.
	 */
	protected ElementBase(ElementBase other) throws DeepCloneException {
		super(other);
		accessor = other.accessor;
	}

	/**
	 * Retrieves the underlying {@link IContentAccessor} of this element. As the
	 * accessor may be shared between elements, this is not checked during deep
	 * clone testing.
	 */
	@ADeepCloneTestExclude
	public IContentAccessor getAccessor() {
		return accessor;
	}

	/**
	 * {@inheritDoc}.
	 * <p>
	 * The name of a parentless element is the uniform path.
	 */
	@Override
	public String getName() {
		if (getParent() == null) {
			return accessor.getUniformPath();
		}
		return getParent().getElementName(this);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * The id of a parentless element is the uniform path.
	 */
	@Override
	public String getId() {
		if (getParent() == null) {
			return accessor.getUniformPath();
		}
		return getParent().getElementId(this);
	}

	/** Returns <code>false</code>. */
	@Override
	public boolean hasChildren() {
		return false;
	}

	/** Returns <code>null</code>. */
	@Override
	public IResource[] getChildren() {
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public byte[] getContent() throws ConQATException {
		return accessor.getContent();
	}

	/** {@inheritDoc} */
	@Override
	public String getLocation() {
		return accessor.getLocation();
	}

	/** {@inheritDoc} */
	@Override
	public String getUniformPath() {
		return accessor.getUniformPath();
	}

	/** Includes only the hash code of the uniform path. */
	@Override
	public int hashCode() {
		return getUniformPath().hashCode();
	}

	/** Compares the uniform paths of both elements. */
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof IElement)) {
			return false;
		}
		return ((IElement) other).getUniformPath().equals(getUniformPath());
	}

	/** {@inheritDoc} */
	@Override
	public IContentAccessor createRelativeAccessor(String relativePath)
			throws ConQATException {
		return accessor.createRelative(relativePath);
	}

	/** {@inheritDoc} */
	@Override
	public String createRelativeUniformPath(String relativePath)
			throws ConQATException {
		return accessor.createRelativeUniformPath(relativePath);
	}
}