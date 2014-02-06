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

import org.conqat.lib.commons.constraint.ConstraintValidator;
import org.conqat.lib.commons.constraint.ConstraintViolationException;
import org.conqat.lib.simulink.model.SimulinkBlock;
import org.conqat.lib.simulink.model.SimulinkElementBase;
import org.conqat.lib.simulink.model.SimulinkLine;
import org.conqat.lib.simulink.model.SimulinkModel;
import org.conqat.lib.simulink.util.SimulinkModelWalker;

/**
 * Utility methods for validating the Simulink model. See package comment for
 * further details.
 * 
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 47430DF9D2DB542B36FF73ABDB80C437
 */
public class SimulinkConstraints {

	/** Check mandatory and optional constraints. */
	public static void checkAllConstraints(SimulinkModel model)
			throws ConstraintViolationException {
		checkMandatoryConstraints(model);
		checkOptionalConstraints(model);
	}

	/** Check mandatory constraints. */
	public static void checkMandatoryConstraints(SimulinkModel model)
			throws ConstraintViolationException {
		ConstraintValidator validator = new ConstraintValidator();
		addMandatoryConstraints(validator);
		validator.validateMesh(model, new SimulinkModelWalker());
	}

	/** Check optional constraints. */
	public static void checkOptionalConstraints(SimulinkModel model)
			throws ConstraintViolationException {
		ConstraintValidator validator = new ConstraintValidator();
		addOptionalConstraints(validator);
		validator.validateMesh(model, new SimulinkModelWalker());
	}

	/** Add optional mandatory to validator. */
	public static void addMandatoryConstraints(ConstraintValidator validator) {
		validator.addConstraint(SimulinkElementBase.class,
				new SimulinkElementNameConstraint());
		validator.addConstraint(SimulinkBlock.class,
				new SimulinkBlockTypeConstraint());
	}

	/** Add optional constraints to validator. */
	public static void addOptionalConstraints(ConstraintValidator validator) {
		validator.addConstraint(SimulinkLine.class,
				new SimulinkLineConstraint());
	}

}