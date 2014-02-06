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
import org.conqat.lib.simulink.model.ParameterizedElement;
import org.conqat.lib.simulink.model.SimulinkConstants;

/**
 * A Stateflow transition.
 * 
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: A1A1BBCD64D344F714919442E2035E2F
 */
public class StateflowTransition extends ParameterizedElement {

	/** Source node. */
	private StateflowNodeBase src;

	/** Destination node. */
	private StateflowNodeBase dst;

	/** Create new default transition. */
	public StateflowTransition(StateflowNodeBase dst) {
		CCSMPre.isTrue(dst != null, "Destination may not be null.");
		this.dst = dst;
		src = null;
		this.dst.addInTransition(this);
	}

	/** Create new transition. */
	public StateflowTransition(StateflowNodeBase src, StateflowNodeBase dst) {
		CCSMPre.isTrue(src != null && dst != null,
				"Neither src nor dst may be null.");
		this.src = src;
		this.dst = dst;
		this.src.addOutTransition(this);
		this.dst.addInTransition(this);
	}

	/** Get destination node. */
	public StateflowNodeBase getDst() {
		return dst;
	}

	/** Get label. */
	public String getLabel() {
		return getParameter(SimulinkConstants.PARAM_labelString);
	}

	/** Get source node. This may be null to indicate default transitions. */
	public StateflowNodeBase getSrc() {
		return src;
	}

	/** Remove this transition from the model. */
	public void remove() {
		if (src != null) {
			src.removeOutTransition(this);
			src = null;
		}
		dst.removeInTransition(this);
		dst = null;
	}

	/** toString() includes source and destination. */
	@Override
	public String toString() {
		if (src == null) {
			return "-> " + dst;
		}
		return src + " -> " + dst;
	}
}