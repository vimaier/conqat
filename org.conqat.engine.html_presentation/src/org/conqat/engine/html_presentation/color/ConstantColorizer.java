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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: poehlmann $
 * @version $Rev: 42194 $
 * @ConQAT.Rating GREEN Hash: 38DA9E0228076B93DC086EA9D612866F
 */
@AConQATProcessor(description = "Colors the provided nodes based on the stored value, where "
		+ "each value is assigned a different color. If required, some of the values can be "
		+ "assigned fixed colors.")
public class ConstantColorizer extends ColorizerBase<Object, String> {

	/** Colors already fixed. */
	private final Map<String, Color> fixedColors = new HashMap<String, Color>();

	/** The colors used. */
	private final Map<Object, Color> colors = new HashMap<Object, Color>();

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "fix", description = "Fixes the color for all values whose "
			+ "string representation matches the given value.")
	public void addFixedColor(
			@AConQATAttribute(name = "value", description = "The value to define the color for.") String value,
			@AConQATAttribute(name = ConQATParamDoc.HTML_COLOR_NAME, description = ConQATParamDoc.HTML_COLOR_DESC) Color color) {

		fixedColors.put(value, color);
	}

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "saturation", attribute = "value", optional = true, description = "The saturation value used for color generation. Default is 1.")
	public double saturation = 1;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "brightness", attribute = "value", optional = true, description = "The brightness value used for color generation. Default is 1.")
	public double brightness = 1;

	/** {@inheritDoc} */
	@Override
	protected Color determineColor(Object value) {
		return colors.get(value);
	}

	/** {@inheritDoc} */
	@Override
	public void visitValue(Object value) {
		colors.put(value, null);
	}

	/**
	 * This is called after all values have been visited, so we calculate the
	 * color assignment here. This is done by using saturated colors equally
	 * distributed around the color cycle (which is easy for the HSB color model
	 * as it uses colors (x,1,1) where x is distrubuted in [0,1]). Extra care is
	 * taken to avoid colliding with user defined (fixed) colors.
	 */
	@Override
	public void calculateColorTable() {
		// calculate number of colors needed
		int numColors = fixedColors.size();
		for (Object o : colors.keySet()) {
			if (!fixedColors.containsKey(o.toString())) {
				++numColors;
			}
		}

		// calculate which colors to avoid (used for fixed).
		Set<Integer> skip = new HashSet<Integer>();
		for (Color color : fixedColors.values()) {
			// hue is between 0 and 1
			double hue = Color.RGBtoHSB(color.getRed(), color.getGreen(),
					color.getBlue(), null)[0];
			skip.add((int) (Math.round(hue * numColors) + .1));
		}

		// assign colors
		int i = -1;
		for (Object key : colors.keySet()) {
			if (fixedColors.containsKey(key.toString())) {
				colors.put(key, fixedColors.get(key.toString()));
			} else {
				do {
					++i;
				} while (skip.contains(i));
				colors.put(key, Color.getHSBColor((float) i / numColors,
						(float) saturation, (float) brightness));
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	protected Map<String, Color> getLegend() {
		Map<String, Color> result = new HashMap<String, Color>();
		for (Entry<Object, Color> e : colors.entrySet()) {
			result.put(e.getKey().toString(), e.getValue());
		}
		for (Entry<String, Color> e : fixedColors.entrySet()) {
			result.put(e.getKey(), e.getValue());
		}
		return result;
	}
}