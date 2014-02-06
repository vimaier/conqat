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
package org.conqat.lib.simulink.model.stateflow;

import org.conqat.lib.simulink.model.SimulinkConstants;

/**
 * Base class for Stateflow declarations {@link StateflowData} and
 * {@link StateflowEvent}.
 * 
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 71C84ED036CDDD8A8FDCB0376C93344E
 */
public abstract class StateflowDeclBase extends
		StateflowElementBase<StateflowDeclContainerBase<?>> {

	/** Create new data object. */
	public StateflowDeclBase() {
		super();
	}

	/** Copy constructor for deep cloning. */
	protected StateflowDeclBase(StateflowDeclBase orig) {
		super(orig);
	}

	/** Get name. */
	public String getName() {
		return getParameter(SimulinkConstants.PARAM_name);
	}

	/** Returns name */
	@Override
	public String toString() {
		return getName() + " [" + getStateflowId() + "]";
	}

}