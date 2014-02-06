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
package org.conqat.engine.html_presentation.base;

import java.awt.Graphics2D;
import java.awt.RenderingHints;

import org.conqat.lib.commons.assessment.Assessment;
import org.conqat.lib.commons.assessment.AssessmentUtils;
import org.conqat.lib.commons.assessment.ETrafficLightColor;

/**
 * This class generates images representing assessments and writes them to disk
 * as PNGs.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating YELLOW Hash: F23AE135FEF0964A523573378848AF5B
 */
public class AssessmentDrawer extends SummaryDrawerBase<Assessment> {

	/** Constructor. */
	public AssessmentDrawer(int width, int height) {
		super(width, height);
	}

	/** {@inheritDoc} */
	@Override
	public void drawSummary(Assessment assessment, Graphics2D graphics) {
		if (assessment.getDominantColor() == ETrafficLightColor.UNKNOWN) {
			drawUndefined(graphics);
		} else {
			draw(graphics, assessment);
		}
	}

	/** Visualize the assessment. */
	private void draw(Graphics2D graphics, Assessment assessment) {

		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		ETrafficLightColor[] usedColors = { ETrafficLightColor.GREEN,
				ETrafficLightColor.YELLOW, ETrafficLightColor.RED };
		int[] freq = new int[usedColors.length];
		int total = 0;
		for (int i = 0; i < usedColors.length; ++i) {
			freq[i] = assessment.getColorFrequency(usedColors[i]);
			total += freq[i];
		}

		int x = 0;
		int count = 0;
		for (int i = 0; i < usedColors.length; ++i) {
			graphics.setColor(AssessmentUtils.getColor(usedColors[i]));
			count += freq[i];
			int nextX = (int) (((float) count / total) * width);
			graphics.fillRect(x, 0, nextX - x, height);
			x = nextX;
		}
	}
}
