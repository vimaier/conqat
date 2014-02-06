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
package org.conqat.engine.resource.filters;

import org.conqat.engine.commons.filter.FilterBase;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.resource.IContainer;
import org.conqat.engine.resource.IResource;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: juergens $
 * @version $Rev: 35198 $
 * @ConQAT.Rating GREEN Hash: 719DD83C0D5DD56F8F272314AE7C01C2
 */
@AConQATProcessor(description = "Removes all empty containers from a tree of resources.")
public class EmptyContainerPruner extends FilterBase<IResource> {

	/** Remove directories without children. */
	@Override
	protected boolean isFiltered(IResource node) {
		return node instanceof IContainer && !node.hasChildren();
	}
}