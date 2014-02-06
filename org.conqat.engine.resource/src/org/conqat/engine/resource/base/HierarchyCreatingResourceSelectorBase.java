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

import org.conqat.engine.resource.IResource;

/**
 * Base class for resource selectors that create the hierarchy based on
 * container paths.
 * 
 * @param <R>
 *            the kind of resource handled.
 * 
 * @param <C>
 *            the container implementation which has to match R.
 * 
 * @author $Author: juergens $
 * @version $Rev: 35198 $
 * @ConQAT.Rating GREEN Hash: A840FD13CFD221523D9E4101C78EE9D1
 */
public abstract class HierarchyCreatingResourceSelectorBase<R extends IResource, C extends ContainerBase<R>>
		extends ResourceSelectorBase<R, C> {

	/** {@inheritDoc}. */
	@SuppressWarnings("unchecked")
	@Override
	protected void insertElement(C container, R element) {
		String[] containerPath = getContainerPath(element);

		for (int i = 0; i < containerPath.length - 1; ++i) {
			R child = container.getNamedChild(containerPath[i]);
			if (!(child instanceof ContainerBase<?>)) {
				child = (R) createContainer(containerPath[i]);
				container.addChild(child);
			}
			container = (C) child;
		}
		container.addChild(element);
	}

	/**
	 * Returns the container path for the element, i.e. the names of the
	 * containers this should reside in, starting from the root. The name of the
	 * root container itself should not be part of the returned array.
	 */
	protected abstract String[] getContainerPath(R element);

}