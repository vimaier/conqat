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
 * @ConQAT.Rating GREEN Hash: 647D3D14C0F5B9374FD2B865FA2CA8C3
 */
@AConQATProcessor(description = ""
		+ "Constraint that is satisfied if clone class contains specified number of gaps")
public class GapConstraint extends ConstraintBase {

	/** Unbounded maximal value */
	public static final int UNBOUNDED = -1;

	/** Minimal number of gaps */
	private int min = 0;

	/** Maximal number of gaps */
	private int max = 0;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "gaps", minOccurrences = 1, maxOccurrences = 1, description = ""
			+ "Set number of gaps required to satisfy constraint. "
			+ "Number of gaps of a clone class is the number of gaps in the clone with "
			+ "the highest gap count.")
	public void setGapBoundaries(
			@AConQATAttribute(name = "min", description = "Minimal number of gaps (inclusive)") int min,
			@AConQATAttribute(name = "max", description = "Maximal number of gaps (inclusive). Use "
					+ UNBOUNDED + " for unbounded") int max) {
		this.min = min;
		this.max = max;
	}

	/** {@inheritDoc} */
	@Override
	public boolean satisfied(CloneClass cloneClass) {
		int gaps = cloneClass.getMaxGapNumber();
		return gaps >= min && (gaps <= max || max == UNBOUNDED);
	}

}