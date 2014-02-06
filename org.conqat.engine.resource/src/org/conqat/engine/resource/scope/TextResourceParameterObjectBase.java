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
package org.conqat.engine.resource.scope;

import org.conqat.engine.resource.IContainer;
import org.conqat.engine.resource.text.ITextResource;
import org.conqat.lib.commons.assertion.CCSMPre;

/**
 * Base class for parameter objects that allows a processor to bundle several
 * outputs into a single object.
 * <p>
 * In order to allow standard ConQAT processors to work on parameter objects,
 * this base class simply implements {@link ITextResource}. This implementation
 * of {@link ITextResource} delegates to the root of the resource system tree.
 * <p>
 * Deriving classes must implement {@link ITextResource#deepClone()}
 * <p>
 * For an example, look at the class ClassifiedCloneDetectionResult in the
 * bundle org.conqat.engine.code_clones.
 * 
 * @author $Author: juergens $
 * @version $Rev: 35198 $
 * @ConQAT.Rating GREEN Hash: D59F72CBD48FF6E1BAC182B3363F083C
 */
public abstract class TextResourceParameterObjectBase implements ITextResource {

	/** Resource tree root parameter. */
	private final ITextResource root;

	/** Constructor. */
	public TextResourceParameterObjectBase(ITextResource root) {
		CCSMPre.isNotNull(root);
		this.root = root;
	}

	/** Returns the root element. */
	public ITextResource getRoot() {
		return root;
	}

	/** Delegates call to root parameter */
	@Override
	public ITextResource[] getChildren() {
		return root.getChildren();
	}

	/** Delegates call to root parameter */
	@Override
	public String getId() {
		return root.getId();
	}

	/** Delegates call to root parameter */
	@Override
	public String getName() {
		return root.getName();
	}

	/** Delegates call to root parameter */
	@Override
	public IContainer getParent() {
		return root.getParent();
	}

	/** {@inheritDoc} */
	@Override
	public void setParent(IContainer parent) {
		root.setParent(parent);
	}

	/** Delegates call to root parameter */
	@Override
	public Object getValue(String key) {
		return root.getValue(key);
	}

	/** Delegates call to root parameter */
	@Override
	public boolean hasChildren() {
		return root.hasChildren();
	}

	/** Delegates call to root parameter */
	@Override
	public void remove() {
		root.remove();
	}

	/** Delegates call to root parameter */
	@Override
	public void setValue(String key, Object value) {
		root.setValue(key, value);
	}
}