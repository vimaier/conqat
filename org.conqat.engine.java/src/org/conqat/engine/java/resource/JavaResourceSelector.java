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

import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.resource.IResource;
import org.conqat.engine.resource.base.HierarchyCreatingResourceSelectorBase;
import org.conqat.lib.commons.string.StringUtils;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: juergens $
 * @version $Rev: 35196 $
 * @ConQAT.Rating GREEN Hash: E1FA926861D348FDCE1E6F82A9D9080C
 */
@AConQATProcessor(description = "Creates a hierarchy of Java resources organized by packages.")
public class JavaResourceSelector extends
		HierarchyCreatingResourceSelectorBase<IJavaResource, JavaContainer> {

	/** {@inheritDoc} */
	@Override
	protected String[] getContainerPath(IJavaResource element) {
		return ((IJavaElement) element).getClassName().split("[.]");
	}

	/** {@inheritDoc} */
	@Override
	protected JavaContainer createRawContainer(String name) {
		return new JavaContainer(name);
	}

	/** {@inheritDoc} */
	@Override
	protected JavaContainer createRootContainer() {
		return createRawContainer(StringUtils.EMPTY_STRING);
	}

	/** {@inheritDoc} */
	@Override
	protected boolean keepElement(IResource element) {
		return element instanceof IJavaElement;
	}
}