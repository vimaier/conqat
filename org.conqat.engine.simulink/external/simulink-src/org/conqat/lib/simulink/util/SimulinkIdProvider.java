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
package org.conqat.lib.simulink.util;

import org.conqat.lib.commons.collections.IIdProvider;
import org.conqat.lib.commons.test.DeepCloneTestUtils;
import org.conqat.lib.simulink.model.SimulinkElementBase;
import org.conqat.lib.simulink.model.SimulinkLine;
import org.conqat.lib.simulink.model.SimulinkPortBase;
import org.conqat.lib.simulink.model.stateflow.IStateflowElement;
import org.conqat.lib.simulink.model.stateflow.StateflowElementBase;
import org.conqat.lib.simulink.model.stateflow.StateflowTransition;

/**
 * Id provider to be used for {@link DeepCloneTestUtils}.
 * 
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 4590F7F9C19BE4CAC40A2839002A906A
 */
public class SimulinkIdProvider implements IIdProvider<String, Object> {
	/**
	 * Obtain id for element.
	 * 
	 * @throws RuntimeException
	 *             if an unknown type was encountered.
	 */
	@Override
	public String obtainId(Object object) {
		if (object instanceof SimulinkElementBase) {
			return ((SimulinkElementBase) object).getName();
		}
		if (object instanceof StateflowElementBase<?>) {
			return ((IStateflowElement<?>) object).getStateflowId();
		}
		if (object instanceof SimulinkPortBase) {
			SimulinkPortBase port = (SimulinkPortBase) object;
			return port.getBlock().getId() + "-" + port.getIndex();
		}
		if (object instanceof SimulinkLine) {
			SimulinkLine line = (SimulinkLine) object;
			return obtainId(line.getSrcPort()) + "-"
					+ obtainId(line.getDstPort());
		}
		if (object instanceof StateflowTransition) {
			StateflowTransition transition = (StateflowTransition) object;

			if (transition.getSrc() == null) {
				return "null-" + obtainId(transition.getDst());
			}

			return obtainId(transition.getSrc()) + "-"
					+ obtainId(transition.getDst());
		}
		throw new RuntimeException("Unknown type " + object.getClass());
	}
}