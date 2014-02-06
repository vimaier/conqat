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

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.lib.commons.assessment.Assessment;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 64C0008003099E13CD4F3CC11DED45EC
 */
@AConQATProcessor(description = "This processor creates an assessment based on a "
		+ "numeric value stored in a key. For this value ranges with assigned "
		+ "colors can be specified. The resulting assessment for a node is the color "
		+ "of the first range containing the value or a default color if no range "
		+ "contained the value. Default is to assess all nodes.")
public class AsessmentDefinitionBasedDoubleAssessor extends
		LocalAssessorBase<Number> {

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "assessment-definition", attribute = ConQATParamDoc.INPUT_REF_NAME, description = "Asessment ranges definition.")
	public TrafficLightRangesDefinition assessmentDefinition;

	/** {@inheritDoc} */
	@Override
	protected Assessment assessValue(Number value) {
		TrafficLightRangeDefinition rangeDefinition = assessmentDefinition
				.obtainRangeDefinition(value.doubleValue());
		return new Assessment(rangeDefinition.getTrafficLightColor());
	}
}