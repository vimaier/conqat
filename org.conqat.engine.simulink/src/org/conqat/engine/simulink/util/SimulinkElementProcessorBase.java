/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2011 the ConQAT Project                                   |
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
package org.conqat.engine.simulink.util;

import org.conqat.engine.resource.base.ElementTraversingProcessorBase;
import org.conqat.engine.simulink.scope.ISimulinkElement;
import org.conqat.engine.simulink.scope.ISimulinkResource;

/**
 * Base class for pipeline processors that traverse {@link ISimulinkElement}s. *
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 2A8D7F26B3EB1BD5350FF235CE69F58A
 */
public abstract class SimulinkElementProcessorBase extends
		ElementTraversingProcessorBase<ISimulinkResource, ISimulinkElement> {

	/** {@inheritDoc} */
	@Override
	protected Class<ISimulinkElement> getElementClass() {
		return ISimulinkElement.class;
	}

}
