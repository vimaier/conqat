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
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.simulink.model.SimulinkBlock;

/**
 * This constraint checks if a Simulink block has a defined type. This is a
 * mandatory constraint.
 * 
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 3AE2858E25094C348CA1568A8D3711E6
 */
public class SimulinkBlockTypeConstraint implements
		ILocalConstraint<SimulinkBlock> {

	/** See class comment. */
	@Override
	public void checkLocalConstraint(SimulinkBlock element)
			throws ConstraintViolationException {
		if (StringUtils.isEmpty(element.getType())) {
			throw new ConstraintViolationException("Block " + element
					+ " has no type.", element);
		}
	}
}