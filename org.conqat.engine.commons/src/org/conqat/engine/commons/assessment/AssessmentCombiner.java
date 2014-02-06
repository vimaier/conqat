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
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * A processor for combining multiple assessments for a single node into one.
 * 
 * @author Michael Aichner
 * @author Benjamin Hummel
 * @author $Author: hummelb $
 * @version $Rev: 37013 $
 * @ConQAT.Rating GREEN Hash: 70285628E620D8D49EDA2C5D5BC85B7D
 */
@AConQATProcessor(description = "This processor combines the assessments of each node visited by simply adding them.")
public class AssessmentCombiner extends AssessmentCombinerBase {

	/** Combine assessments using the default {@link Assessment#add(Assessment)}-method. */
	@Override
	protected Assessment combineAssessments(List<Assessment> assessments) {
		Assessment combined = new Assessment();
		for (Assessment assessment : assessments) {
			combined.add(assessment);
		}
		return combined;
	}
}