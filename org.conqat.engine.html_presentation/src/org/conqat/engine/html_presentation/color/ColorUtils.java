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

import static org.conqat.lib.commons.html.ECSSProperty.BACKGROUND_COLOR;
import static org.conqat.lib.commons.html.EHTMLAttribute.CLASS;
import static org.conqat.lib.commons.html.EHTMLAttribute.STYLE;
import static org.conqat.lib.commons.html.EHTMLElement.BR;
import static org.conqat.lib.commons.html.EHTMLElement.TABLE;
import static org.conqat.lib.commons.html.EHTMLElement.TD;
import static org.conqat.lib.commons.html.EHTMLElement.TR;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import org.conqat.engine.html_presentation.treemap.TreeMapCreator;
import org.conqat.lib.commons.html.EHTMLElement;
import org.conqat.lib.commons.html.HTMLWriter;

/**
 * Utility code for dealing with colors.
 * 
 * @author hummelb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: AF4221EAE05A3C1635E7B69402051B4B
 */
public class ColorUtils {

	/**
	 * Create HTML code for a provided legend (i.e. a mapping from description
	 * objects to colors).
	 */
	public static void makeHtmlLegend(
			Map<Comparable<Comparable<?>>, Color> legend, HTMLWriter writer) {
		writer.addClosedElement(BR);
		writer.addClosedElement(BR);
		writer.openElement(TABLE, CLASS, TreeMapCreator.MAP_LEGEND);

		ArrayList<Comparable<Comparable<?>>> keys = new ArrayList<Comparable<Comparable<?>>>(
				legend.keySet());
		Collections.sort(keys);
		for (Comparable<?> key : keys) {
			Color c = legend.get(key);
			String htmlColor = String.format("#%06X", c.getRGB() & 0xffffff);
			writer.openElement(TR);
			writer.openElement(TD, STYLE, TreeMapCreator.COLOR_SQUARE_STYLE
					.setProperty(BACKGROUND_COLOR, htmlColor));
			writer.addRawString("&nbsp;");
			writer.closeElement(TD);
			writer.addClosedTextElement(TD, key.toString());
			writer.closeElement(TR);
		}
		writer.closeElement(EHTMLElement.TABLE);
	}

}