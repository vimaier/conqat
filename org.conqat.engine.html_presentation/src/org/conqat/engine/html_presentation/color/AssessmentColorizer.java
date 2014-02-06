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
package org.conqat.engine.html_presentation.color;

import java.awt.Color;

import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.lib.commons.assessment.Assessment;
import org.conqat.lib.commons.assessment.AssessmentUtils;
import org.conqat.lib.commons.assessment.ETrafficLightColor;
import org.conqat.lib.commons.collections.PairList;
import org.conqat.lib.commons.color.MultiColor;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: F70EE581DEF1B389EB04497EFF61DAD3
 */
@AConQATProcessor(description = "Colors the provided nodes based on the stored assessment.")
public class AssessmentColorizer extends ColorizerBase<Assessment, String> {

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "multi-color", attribute = "value", optional = true, description = ""
			+ "If this is set to true, multi-colors are created. Otherwise (default) only the dominant color is used for colorizing.")
	public boolean multiColor = false;

	/** {@inheritDoc} */
	@Override
	protected Color determineColor(Assessment assessment) {
		Color dominantColor = determineColor(assessment.getDominantColor());

		if (multiColor && assessment.getSize() > 0) {

			PairList<Color, Integer> colorDistribution = new PairList<Color, Integer>();
			for (ETrafficLightColor color : ETrafficLightColor.values()) {
				int frequency = assessment.getColorFrequency(color);
				if (frequency > 0) {
					colorDistribution.add(AssessmentUtils.getColor(color), frequency);
				}
			}

			return new MultiColor(dominantColor, colorDistribution);
		}

		return dominantColor;
	}

	/** Determines the color for a traffic light color. */
	public static Color determineColor(ETrafficLightColor color) {
		if (color == null) {
			return Color.GRAY;
		}
		return AssessmentUtils.getColor(color);
	}
}