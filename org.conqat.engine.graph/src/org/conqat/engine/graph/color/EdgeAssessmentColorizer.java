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
package org.conqat.engine.graph.color;

import java.awt.Color;

import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.lib.commons.assessment.Assessment;
import org.conqat.lib.commons.assessment.AssessmentUtils;

/**
 * Determine edge colors based on assessment.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 38625 $
 * @ConQAT.Rating GREEN Hash: 5CD124B84A5153F76A9A76F4F164EAFE
 */
@AConQATProcessor(description = "Colors the edges of the provided graph based on the stored assessment.")
public class EdgeAssessmentColorizer extends EdgeColorizerBase<Assessment> {

	/** {@inheritDoc} */
	@Override
	protected Color determineColor(Assessment assessment) {
		return AssessmentUtils.getColor(assessment.getDominantColor());
	}
}