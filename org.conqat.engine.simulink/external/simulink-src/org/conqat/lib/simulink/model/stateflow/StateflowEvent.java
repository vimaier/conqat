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
 * A Stateflow event object.
 * 
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 242C0C463CFD5B36A9F5749948BA662F
 */
public class StateflowEvent extends StateflowDeclBase {

	/** Create event. */
	public StateflowEvent() {
		super();
	}

	/** Copy constructor for deep cloning. */
	private StateflowEvent(StateflowEvent orig) {
		super(orig);
	}

	/** Remove this event from the model. */
	@Override
	public void remove() {
		CCSMPre.isFalse(getParent() == null,
				"Event has no parent to be removed from.");
		getParent().removeEvent(this);
	}

	/** Deep clone this event. */
	@Override
	public StateflowEvent deepClone() {
		return new StateflowEvent(this);
	}
}