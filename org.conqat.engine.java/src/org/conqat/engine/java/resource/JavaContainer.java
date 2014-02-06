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
package org.conqat.engine.java.resource;

import org.conqat.engine.resource.IElement;
import org.conqat.engine.resource.base.ContainerBase;
import org.conqat.lib.commons.clone.DeepCloneException;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Container implementation for Java.
 * 
 * @author $Author: juergens $
 * @version $Rev: 35196 $
 * @ConQAT.Rating GREEN Hash: 2AE9B3DCD551EA161987BB807C959237
 */
public class JavaContainer extends ContainerBase<IJavaResource> implements
		IJavaResource {

	/** Constructor. */
	public JavaContainer(String name) {
		super(name);
	}

	/** Copy constructor. */
	protected JavaContainer(JavaContainer other) throws DeepCloneException {
		super(other);
	}

	/** {@inheritDoc} */
	@Override
	protected IJavaResource[] allocateArray(int size) {
		return new IJavaResource[size];
	}

	/** {@inheritDoc} */
	@Override
	public JavaContainer deepClone() throws DeepCloneException {
		return new JavaContainer(this);
	}

	/** {@inheritDoc} */
	@Override
	protected String getPathSeparator() {
		return ".";
	}

	/** {@inheritDoc} */
	@Override
	public String getElementName(IElement element) {
		if (element instanceof IJavaElement) {
			return StringUtils.getLastPart(
					((IJavaElement) element).getClassName(), '.');
		}
		return super.getElementName(element);
	}
}