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

import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.resource.IElement;
import org.conqat.engine.resource.IResource;
import org.conqat.engine.resource.util.UniformPathUtils;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Base class for resource selectors that create the hierarchy based on the
 * uniform path of an element.
 * 
 * @param <R>
 *            the kind of resource handled.
 * 
 * @param <C>
 *            the container implementation which has to match R.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 45848 $
 * @ConQAT.Rating GREEN Hash: 00F732F31B88A3CF16F64AEDAF5CFE11
 */
public abstract class UniformPathHierarchyResourceSelectorBase<R extends IResource, C extends ContainerBase<R>>
		extends HierarchyCreatingResourceSelectorBase<R, C> {

	/** {@ConQATDoc} */
	@AConQATFieldParameter(parameter = "strip-project", attribute = "enable", optional = true, description = ""
			+ "If enabled project name is stripped from the uniform path. Default is false.")
	public boolean stripProject = false;

	/** {@inheritDoc} */
	@Override
	protected String[] getContainerPath(R element) {
		String path = ((IElement) element).getUniformPath();
		if (stripProject) {
			path = UniformPathUtils.stripProject(path);
		}
		return UniformPathUtils.splitPath(path);
	}

	/** {@inheritDoc} */
	@Override
	protected C createRootContainer() {
		return createRawContainer(StringUtils.EMPTY_STRING);
	}
}