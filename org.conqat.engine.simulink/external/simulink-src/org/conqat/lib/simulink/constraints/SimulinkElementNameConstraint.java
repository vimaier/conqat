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
import org.conqat.lib.simulink.model.SimulinkElementBase;

/**
 * This constraint checks if a Simulink element has a defined name and ensures
 * that the name does not start or end with a slash. This is a mandatory
 * constraint.
 * 
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: B52BA5781399B8AE5405190E89F4A1A3
 */
public class SimulinkElementNameConstraint implements
		ILocalConstraint<SimulinkElementBase> {

	/** See class comment. */
	@Override
	public void checkLocalConstraint(SimulinkElementBase element)
			throws ConstraintViolationException {
		String name = element.getName();
		if (name == null || name.length() == 0) {
			throw new ConstraintViolationException("Element " + element
					+ " has no name.", element);
		}
		if (name.startsWith("/") || name.endsWith("/")) {
			throw new ConstraintViolationException("Name of element " + element
					+ " starts or ends with a slash.", element);
		}
	}
}