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
package org.conqat.engine.code_clones.index.report;

import org.conqat.engine.code_clones.core.CloneClass;
import org.conqat.engine.code_clones.core.constraint.ConstraintList;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.assertion.CCSMPre;

/**
 * A {@link CollectingCloneClassReporter} that can deal with constraints.
 * 
 * @author hummelb
 * @author $Author: juergens $
 * @version $Rev: 34670 $
 * @ConQAT.Rating GREEN Hash: D0B97D9BFA23882A577347E11FFA608E
 */
public class ConstraintAwareCollectingCloneClassReporter extends
		CollectingCloneClassReporter {

	/** The constraints. */
	private final ConstraintList constraints;

	/** Required minimal length. */
	private final int minLength;

	/** Constructor. */
	public ConstraintAwareCollectingCloneClassReporter(int minLength,
			ConstraintList constraints) {
		CCSMPre.isNotNull(constraints);
		this.constraints = constraints;

		CCSMPre.isTrue(minLength > 0, "Minimal length must be positive!");
		this.minLength = minLength;
	}

	/** {@inheritDoc} */
	@Override
	public void report(CloneClass cloneClass) throws ConQATException {
		if (cloneClass.getNormalizedLength() >= minLength
				&& constraints.allSatisfied(cloneClass)) {
			super.report(cloneClass);
		}
	}
}