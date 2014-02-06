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

import org.conqat.lib.commons.assertion.CCSMPre;

/**
 * A Stateflow data object.
 * 
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 2C5E844408CAEED5258372697757A9F1
 */
public class StateflowData extends StateflowDeclBase {

	/** Create new data object. */
	public StateflowData() {
		super();
	}

	/** Copy constructor for deep cloning. */
	private StateflowData(StateflowData stateflowData) {
		super(stateflowData);
	}

	/** Remove this data object from the model. */
	@Override
	public void remove() {
		CCSMPre.isFalse(getParent() == null,
				"Data object has no parent to be removed from.");
		getParent().removeData(this);
	}

	/** Deep clone this data object. */
	@Override
	public StateflowData deepClone() {
		return new StateflowData(this);
	}
}