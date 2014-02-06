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
package org.conqat.engine.code_clones.core.constraint;

import org.conqat.engine.code_clones.core.Clone;
import org.conqat.engine.code_clones.core.CloneClass;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * {@ConQAT.Doc}
 * 
 * @author juergens
 * @author $Author: juergens $
 * @version $Rev: 34670 $
 * @ConQAT.Rating GREEN Hash: 29D379074144EC7A74B33ED4F3516287
 */
@AConQATProcessor(description = ""
		+ "Constraint that is satisfied if the ratio of gaps to units in the clone class are below a set threshold")
public class GapRatioConstraint extends ConstraintBase {

	/** Threshold used for filtering */
	private double maxGapRatio;

	/** ConQAT Parameter */
	@AConQATParameter(name = "max", minOccurrences = 1, maxOccurrences = 1, description = ""
			+ "Sets the max gap ratio above which clone classes are filtered out")
	public void setsetMaxGapRatio(
			@AConQATAttribute(name = "value", description = "Max gap ratio (inclusive)") double maxGapRatio) {
		this.maxGapRatio = maxGapRatio;
	}

	/** {@inheritDoc} */
	@Override
	public boolean satisfied(CloneClass cloneClass) {
		return gapRatioFor(cloneClass) <= maxGapRatio;
	}

	/** Returns the max gap ratio for the clones of a clone class */
	private double gapRatioFor(CloneClass cloneClass) {
		double gapRatio = 0;
		for (Clone clone : cloneClass.getClones()) {
			gapRatio = Math.max(gapRatio, gapRatioFor(clone));
		}
		return gapRatio;
	}

	/** Computes the gap-ratio for a clone */
	private double gapRatioFor(Clone clone) {
		return clone.getDeltaInUnits() / (double) clone.getLengthInUnits();
	}

}