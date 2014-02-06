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
package org.conqat.engine.simulink.scope;

import org.conqat.engine.resource.IResource;
import org.conqat.engine.resource.base.UniformPathHierarchyResourceSelectorBase;

import org.conqat.engine.core.core.AConQATProcessor;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @levd.rating GREEN Hash: 4614E1BC2D9376B37E865B9CF4428E84
 */
@AConQATProcessor(description = "This processor filters all resources "
		+ "that are not ISimulinkResources.")
public class SimulinkResourceSelector extends
		UniformPathHierarchyResourceSelectorBase<ISimulinkResource, SimulinkContainer> {

	/** {@inheritDoc} */
	@Override
	protected SimulinkContainer createRawContainer(String name) {
		return new SimulinkContainer(name);
	}

	/** {@inheritDoc} */
	@Override
	protected boolean keepElement(IResource element) {
		return element instanceof ISimulinkElement;
	}

}