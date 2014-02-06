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
package org.conqat.lib.simulink.constraints;

import org.conqat.lib.commons.constraint.ConstraintViolationException;
import org.conqat.lib.commons.constraint.ILocalConstraint;
import org.conqat.lib.simulink.model.SimulinkLine;

/**
 * This constraint checks if a Simulink line crosses subsystem boundaries. This
 * is an optional constraint.
 * 
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 45279AD058138BE2917A44B4C06E7E43
 */
public class SimulinkLineConstraint implements ILocalConstraint<SimulinkLine> {

	/** See class comment. */
	@Override
	public void checkLocalConstraint(SimulinkLine element)
			throws ConstraintViolationException {
		if (element.getSrcPort().getBlock().getParent() != element.getDstPort()
				.getBlock().getParent()) {
			throw new ConstraintViolationException("Line " + element
					+ " crosses subsystem boundaries.", element);
		}
	}

}