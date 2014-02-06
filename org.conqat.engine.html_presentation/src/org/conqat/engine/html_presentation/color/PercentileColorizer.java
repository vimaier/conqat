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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math.stat.descriptive.rank.Percentile;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * This colorizer allows to color values above a specified percentile
 * differently from the values below the percentile.
 * 
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 19BD49571C021133723808B1DF445699
 */
@AConQATProcessor(description = "This colorizer allows to color values above "
		+ "a specified percentile differently from the values below the percentile.")
public class PercentileColorizer extends ColorizerBase<Number, Double> {

	/** Percentile instance used to calculate percentiles. */
	private final Percentile percentile = new Percentile();

	/** All values. */
	private final ArrayList<Double> values = new ArrayList<Double>();

	/** Defines the percentile. */
	private double p;

	/** The bound is the pth percentile. */
	private double bound;

	/** Color for values above and equal to the percentile. */
	private Color aboveColor;

	/** Color for values above and equal to the percentile. */
	private Color belowColor;

	/** ConQAT Parameter. */
	@AConQATParameter(name = "percentile", minOccurrences = 1, maxOccurrences = 1, description = "Specify percentile.")
	public void setPercentile(
			@AConQATAttribute(name = "value", description = "percentile must be > 0 and <=100")
			double p) {
		this.p = p;
	}

	/** ConQAT Parameter. */
	@AConQATParameter(name = "above", minOccurrences = 1, maxOccurrences = 1, description = "Color above and equal percentile.")
	public void setAboveColor(
			@AConQATAttribute(name = ConQATParamDoc.HTML_COLOR_NAME, description = ConQATParamDoc.HTML_COLOR_DESC)
			Color color) {
		aboveColor = color;
	}

	/** ConQAT Parameter. */
	@AConQATParameter(name = "below", minOccurrences = 1, maxOccurrences = 1, description = "Color below percentile.")
	public void setBelowColor(
			@AConQATAttribute(name = ConQATParamDoc.HTML_COLOR_NAME, description = ConQATParamDoc.HTML_COLOR_DESC)
			Color color) {
		belowColor = color;
	}

	/** Store value. */
	@Override
	protected void visitValue(Number value) {
		values.add(value.doubleValue());
	}

	/** Determine percentile. */
	@Override
	protected void calculateColorTable() {
		double[] valueArray = new double[values.size()];
		for (int i = 0; i < values.size(); i++) {
			valueArray[i] = values.get(i);
		}
		Arrays.sort(valueArray);
		bound = percentile.evaluate(valueArray, p);
	}

	/** {@inheritDoc} */
	@Override
	protected Map<Double, Color> getLegend() {
		if (values.isEmpty()) {
			return null;
		}
		Collections.sort(values);
		HashMap<Double, Color> legend = new HashMap<Double, Color>();
		legend.put(values.get(0), belowColor);
		legend.put(bound, aboveColor);
		legend.put(values.get(values.size() - 1), aboveColor);
		return legend;
	}

	/** {@inheritDoc} */
	@Override
	protected Color determineColor(Number valueObject) {
		double value = valueObject.doubleValue();

		if (value >= bound) {
			return aboveColor;
		}

		return belowColor;
	}

}