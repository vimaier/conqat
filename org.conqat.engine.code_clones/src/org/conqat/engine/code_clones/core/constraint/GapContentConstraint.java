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

import org.conqat.engine.code_clones.core.Clone;
import org.conqat.engine.code_clones.core.CloneClass;
import org.conqat.engine.code_clones.core.Unit;
import org.conqat.engine.code_clones.core.utils.CloneUtils;
import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.pattern.PatternList;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.lib.commons.algo.Diff;
import org.conqat.lib.commons.algo.Diff.Delta;
import org.conqat.lib.commons.assertion.CCSMAssert;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: juergens $
 * @version $Rev: 40742 $
 * @ConQAT.Rating GREEN Hash: 78C56D49DCC772CE9EDF87AB59B59836
 */
@AConQATProcessor(description = "Constraint that is satisfied if the content "
		+ "of at least one gap contains at least one of the patterns. The "
		+ "content of each gap is checked unit by unit, therefore the patterns "
		+ "can be specified only for single units. A potential use-case is "
		+ "searching gapped clones with things like '== null' in their gaps, "
		+ "which often indicates unwanted inconsistencies. Please note that "
		+ "the gaps are recalculated for each pair of clones in the clone "
		+ "class as storing this information would significantly increase the "
		+ "complexity elsewhere. Hence, the gaps may theoretically differ from "
		+ "those that have been identified in the actual clone detection "
		+ "process. I practice, however, this is very unlikely.")
public class GapContentConstraint extends ConstraintBase {

	/** Patterns against which clones are matched */
	protected PatternList patterns;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "patterns", description = "Patterns against which gaps of clones are matched.", minOccurrences = 1, maxOccurrences = 1)
	public void setPatterns(
			@AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC) PatternList patterns) {
		this.patterns = patterns;
	}

	/** {@inheritDoc} */
	@Override
	public boolean satisfied(CloneClass cloneClass) {
		Clone last = null;
		// Calculate the difference for each pair of clones
		for (Clone clone : cloneClass.getClones()) {
			CCSMAssert.isNotNull(CloneUtils.getUnits(clone),
					"No units found for clone. Is unit storing enabled?");
			if (last != null) {
				Delta<Unit> delta = Diff.computeDelta(
						CloneUtils.getUnits(last), CloneUtils.getUnits(clone));
				for (int i = 0; i < delta.getSize(); i++) {
					Unit unit = delta.getT(i);
					if (patterns.findsAnyIn(unit.getUnnormalizedContent())) {
						return true;
					}
				}

			}

			last = clone;
		}
		return false;
	}

}
