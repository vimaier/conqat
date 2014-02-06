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
package org.conqat.engine.code_clones.core.constraint;

import org.conqat.engine.code_clones.core.CloneClass;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.logging.ELogLevel;
import org.conqat.lib.commons.collections.CounterSet;
import org.conqat.lib.commons.collections.PairList;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: goede $
 * @version $Rev: 40387 $
 * @ConQAT.Rating GREEN Hash: 3E9D07F6CFD02D057C9227B7BC1409B0
 */
@AConQATProcessor(description = "This constraint is fulfilled if multiple subconstraints are all fulfilled. "
		+ "The benefit of this processor over directly attaching multiple constraints to the clone detector is "
		+ "that this constraint supports inversion of individual constraints and a lenient mode.")
public class AndConstraint extends ConstraintBase {

	/**
	 * Maximum number of equal error messages produced in lenient mode (to avoid
	 * flooding the log).
	 */
	private static final int MAX_MESSAGES = 10;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "lenient-mode", attribute = "log-level", optional = true, description = "If this parameter is set, "
			+ "exception from subconstraints are not thrown but rather logged at the given level.")
	public ELogLevel lenientLogLevel = null;

	/** List of constraints and inversion flags. */
	private final PairList<ICloneClassConstraint, Boolean> constraints = new PairList<ICloneClassConstraint, Boolean>();

	/** Used to avoid logging certain messages over and over again. */
	private final CounterSet<String> messageCount = new CounterSet<String>();

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "constraint", description = ""
			+ "Adds a constraint that must be satisfied.")
	public void addConstraint(
			@AConQATAttribute(name = "ref", description = "Reference to the contraint.") ICloneClassConstraint constraint,
			@AConQATAttribute(name = "inverted", defaultValue = "false", description = ""
					+ "If this is true, the result of the constraint is inverted.") boolean inverted) {
		constraints.add(constraint, inverted);
	}

	/** {@inheritDoc} */
	@Override
	public boolean satisfied(CloneClass cloneClass) throws ConQATException {
		for (int i = 0; i < constraints.size(); ++i) {
			try {
				boolean result = constraints.getFirst(i).satisfied(cloneClass);
				if (constraints.getSecond(i)) {
					result = !result;
				}
				if (!result) {
					return false;
				}
			} catch (ConQATException e) {
				if (lenientLogLevel == null) {
					throw e;
				}

				logMessage("Problems in "
						+ constraints.getFirst(i).getClass().getSimpleName()
						+ ": " + e.getMessage());
			}
		}
		return true;
	}

	/**
	 * Logs the given error message respecting the {@link #MAX_MESSAGES} and
	 * {@link #lenientLogLevel}
	 */
	private void logMessage(String message) {
		messageCount.inc(message);
		int count = messageCount.getValue(message);
		if (count < MAX_MESSAGES) {
			getLogger().log(lenientLogLevel, message);
		} else if (count == MAX_MESSAGES) {
			getLogger().log(
					lenientLogLevel,
					"Message occurred more than " + MAX_MESSAGES + " times: "
							+ message);
		}
	}

}
