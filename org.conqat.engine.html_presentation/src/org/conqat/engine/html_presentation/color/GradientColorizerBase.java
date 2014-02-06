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
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.ConQATException;

/**
 * Base class for processors assigning colors in the continuous spectrum, i.e.
 * it does not work with a fixed number of colors but picks a color out of a
 * gradient.
 * <p>
 * The scaling of the values found and the selection of the color is all handled
 * in this class. All that has to be defined is a mapping between the values
 * being shown as colors and plain doubles.
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: C7111EE29ADB9FAAB41796E765435679
 * 
 * @param <E>
 *            the type expected to be read.
 * @param <I>
 *            the type used for parameter input (which is also used for building
 *            the legend).
 */
public abstract class GradientColorizerBase<E, I extends Comparable<I>> extends
		ColorizerBase<E, I> {

	/** Storage for the points on the color gradient. */
	private final SortedMap<Double, Color> gradientPoints = new TreeMap<Double, Color>();

	/** Determines whether to scale the gradient to the min and max values. */
	private boolean autoscale = false;

	/** The value used shift each value found (after scaling). */
	private double offset = 0;

	/** The value used to scale each value found (before shifting). */
	private double scale = 1;

	/** The minimal value encountered. */
	private double minValue = Double.POSITIVE_INFINITY;

	/** The maximal value encountered. */
	private double maxValue = Double.NEGATIVE_INFINITY;

	/** Set autoscale mode. */
	@AConQATParameter(name = "autoscale", maxOccurrences = 1, description = ""
			+ "If this is set to true, the gradient will be scaled to fit to the minimal "
			+ "and maximal value found. Default is not to scale.")
	public void setAutoscale(
			@AConQATAttribute(name = "value", description = "The new value for autoscaling.")
			boolean value) {
		autoscale = value;
	}

	/** Add a color to the gradient. */
	@AConQATParameter(name = "gradient", minOccurrences = 2, description = ""
			+ "Add a color value to the gradient used.")
	public void setGradientPoint(
			@AConQATAttribute(name = "value", description = "The value for which to fix the color.")
			I value,
			@AConQATAttribute(name = ConQATParamDoc.HTML_COLOR_NAME, description = ConQATParamDoc.HTML_COLOR_DESC)
			Color color) throws ConQATException {

		gradientPoints.put(inputToDouble(value), color);
	}

	/** {@inheritDoc} */
	@Override
	public void visitValue(E value) {
		double d = convertToDouble(value);
		minValue = Math.min(minValue, d);
		maxValue = Math.max(maxValue, d);
	}

	/** Modify scaling using min and max values. */
	@Override
	public void calculateColorTable() {
		if (autoscale) {
			double gradMin = gradientPoints.firstKey();
			double gradMax = gradientPoints.lastKey();
			if (minValue >= maxValue) {
				scale = 1;
			} else {
				scale = (gradMax - gradMin) / (maxValue - minValue);
			}
			offset = gradMin - scale * minValue;
		}
	}

	/**
	 * Determines the color for a single value. This is done by transforming the
	 * value to a double using {@link #convertToDouble(Object)}, scaling this
	 * value accordingly and performing the lookup in the gradient defined.
	 */
	@Override
	protected Color determineColor(E value) {
		double d = convertToDouble(value) * scale + offset;
		double lastKey = Double.MIN_VALUE;
		Color lastColor = null;
		for (Entry<Double, Color> e : gradientPoints.entrySet()) {
			double key = e.getKey();
			Color currentColor = e.getValue();
			if (lastKey <= d && d <= key) {
				if (lastColor == null) {
					return currentColor;
				}
				double amount = (d - lastKey) / (key - lastKey);
				return blendColors(lastColor, currentColor, 1. - amount);
			}
			lastKey = key;
			lastColor = currentColor;
		}
		return gradientPoints.get(gradientPoints.lastKey());
	}

	/**
	 * Linearly blends the given colors, using <code>amount</code> of the
	 * first and <code>1 - amount</code> of the second color-
	 */
	private Color blendColors(Color first, Color second, double amount) {
		double red = first.getRed() * amount + second.getRed() * (1. - amount);
		double green = first.getGreen() * amount + second.getGreen()
				* (1. - amount);
		double blue = first.getBlue() * amount + second.getBlue()
				* (1. - amount);
		return new Color((int) (red + .5), (int) (green + .5),
				(int) (blue + .5));
	}

	/**
	 * Returns the legend. This is created by using the minimum and maximum
	 * values obtained, as well as all points explicitly given on the gradient.
	 */
	@Override
	protected Map<I, Color> getLegend() {
		Map<I, Color> result = new HashMap<I, Color>();

		addToLegend(minValue, result);

		if (minValue == maxValue) {
			return result;
		}

		addToLegend(maxValue, result);

		Double lastVal = null;
		for (Entry<Double, Color> e : gradientPoints.entrySet()) {
			double unscaled = (e.getKey() - offset) / scale;
			addToLegend(unscaled, result);
			if (lastVal != null) {
				addToLegend((lastVal + unscaled) / 2, result);
			}
			lastVal = unscaled;
		}

		return result;
	}

	/**
	 * Puts the color for the sample point (using original unscaled values) into
	 * the given map.
	 */
	private void addToLegend(double unscaled, Map<I, Color> map) {
		map.put(doubleToInput(unscaled),
				determineColor(convertFromDouble(unscaled)));
	}

	/** Converts the input type into the type used internally. */
	protected abstract double inputToDouble(I i) throws ConQATException;

	/**
	 * Converts the given double value to the used input type (should be inverse
	 * to {@link #inputToDouble(Comparable)}).
	 */
	protected abstract I doubleToInput(double d);

	/**
	 * Convert the value type used into a double value (should be a linear
	 * transformation).
	 */
	protected abstract double convertToDouble(E e);

	/**
	 * Convert the provided double into the value type. This should be the
	 * inverse of {@link #convertToDouble(Object)}.
	 */
	protected abstract E convertFromDouble(double d);
}