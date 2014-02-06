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
package org.conqat.engine.commons.assessment;

import java.util.List;

import org.conqat.lib.commons.assessment.Assessment;
import org.conqat.lib.commons.assessment.ETrafficLightColor;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * This processor relates multiple assessments. If all input assessments are
 * green the result assessment is green. If all input assessemnts are non-green,
 * the result assessement is red. Otherwise it is yellow.
 * 
 * @author Michael Aichner
 * @author Benjamin Hummel
 * @author $Author: hummelb $
 * @version $Rev: 37013 $
 * @ConQAT.Rating GREEN Hash: 3274A9DF12F2E3A2C7F4E118ADF09F58
 */
@AConQATProcessor(description = "This processor relates multiple assessments. "
		+ "If all input assessments are green, the result assessment is green. "
		+ "If all input assessemnts are non-green, the result assessement is red. "
		+ "Otherwise it is yellow.")
public class AssessmentRelater extends AssessmentCombinerBase {

	/** {@inheritDoc} */
	@Override
	protected Assessment combineAssessments(List<Assessment> assessments) {
		int notGreenCount = 0;

		for (Assessment assessment : assessments) {
			if (assessment.getDominantColor() != ETrafficLightColor.GREEN) {
				notGreenCount++;
			}
		}

		if (notGreenCount == 0) {
			return new Assessment(ETrafficLightColor.GREEN);
		}

		if (notGreenCount == assessments.size()) {
			return new Assessment(ETrafficLightColor.RED);
		}

		return new Assessment(ETrafficLightColor.YELLOW);
	}
}