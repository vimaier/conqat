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
package org.conqat.engine.html_presentation.formatters;

import static org.conqat.lib.commons.html.ECSSProperty.BACKGROUND_COLOR;
import static org.conqat.lib.commons.html.ECSSProperty.BORDER_RADIUS;
import static org.conqat.lib.commons.html.ECSSProperty.COLOR;
import static org.conqat.lib.commons.html.ECSSProperty.FONT_SIZE;
import static org.conqat.lib.commons.html.ECSSProperty.HEIGHT;
import static org.conqat.lib.commons.html.ECSSProperty.MARGIN_LEFT;
import static org.conqat.lib.commons.html.ECSSProperty.MARGIN_RIGHT;
import static org.conqat.lib.commons.html.ECSSProperty.WIDTH;
import static org.conqat.lib.commons.html.EHTMLAttribute.CLASS;
import static org.conqat.lib.commons.html.EHTMLAttribute.STYLE;
import static org.conqat.lib.commons.html.EHTMLElement.DIV;

import org.conqat.lib.commons.assessment.Assessment;
import org.conqat.lib.commons.assessment.AssessmentUtils;
import org.conqat.lib.commons.assessment.ETrafficLightColor;
import org.conqat.lib.commons.color.ColorUtils;
import org.conqat.lib.commons.color.ECCSMColor;
import org.conqat.lib.commons.html.CSSDeclarationBlock;
import org.conqat.lib.commons.html.HTMLWriter;

/**
 * A HTML formatter for assessments.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating YELLOW Hash: 70F597AB4EFD9709941F7782BBFCFCCC
 */
public class AssessmentHTMLFormatter implements IHTMLFormatter<Assessment> {

	/** The base style used for the assessment markers. */
	private static final CSSDeclarationBlock BASE_STYLE = new CSSDeclarationBlock(
			WIDTH, "10px", HEIGHT, "10px", FONT_SIZE, "1px", BORDER_RADIUS,
			"4px", MARGIN_LEFT, "3px", MARGIN_RIGHT, "3px").setBorder("1px",
			"solid", ECCSMColor.DARK_GRAY.getHTMLColorCode());

	/** Inserts a traffic light image based on the dominant color. */
	@Override
	public void formatObject(Assessment assessment, HTMLWriter writer) {
		ETrafficLightColor dominantColor = assessment.getDominantColor();

		String color = ColorUtils.toHtmlString(AssessmentUtils.getColor(dominantColor));
		CSSDeclarationBlock colorStyle = new CSSDeclarationBlock(
				BACKGROUND_COLOR, color, COLOR, color);
		writer.openElement(DIV, CLASS, BASE_STYLE, STYLE, colorStyle);

		// add reverserd ordinal to allow sorting
		writer.addText(Integer.toString(9 - dominantColor.ordinal()));
		writer.closeElement(DIV);
	}
}