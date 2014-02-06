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
package org.conqat.engine.html_presentation.chart;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.html_presentation.image.IImageDescriptor;
import org.jfree.chart.JFreeChart;

/**
 * Base class for chart creators.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 869DAC740DC86D46EF4FDC33C868BCEC
 */
public abstract class ChartCreatorBase extends ConQATProcessorBase {

	/** Default preferred size for charts. */
	protected static final Dimension DEFAULT_PREFERRED_SIZE = new Dimension(
			800, 600);

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "background", attribute = "color", optional = true, description = ""
			+ "Set background color of chart. Default is to use JFreeCharts default background color.")
	public Color backgroundColor;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = ConQATParamDoc.DRAW_LEGEND_PARAM, attribute = ConQATParamDoc.DRAW_LEGEND_ATTRIBUTE, optional = true, description = ""
			+ ConQATParamDoc.DRAW_LEGEND_DESC)
	public boolean drawLegend = true;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "title-font", attribute = "size", optional = true, description = ""
			+ "Sets the font size of the chart's title. If this is negative (which is the default), the default size of JFreeChart is used.")
	public int titleFontSize = -1;

	/**
	 * Template method that deriving classes can override to perform
	 * initialization tasks. The method is guaranteed to be called after the
	 * ConQAT parameters have been set by the driver and before the
	 * {@link #createChart()} method gets called.
	 * 
	 * Default implementation does nothing
	 */
	protected void setUp() {
		// do nothing
	}

	/** {@inheritDoc} */
	@Override
	public IImageDescriptor process() throws ConQATException {
		setUp();
		JFreeChart chart = createChart();

		if (backgroundColor != null) {
			chart.getPlot().setBackgroundPaint(backgroundColor);
		}

		if (titleFontSize >= 0) {
			Font newFont = chart.getTitle().getFont()
					.deriveFont((float) titleFontSize);
			chart.getTitle().setFont(newFont);
		}

		return createImageDescriptor(chart);
	}

	/**
	 * Template method to return a chart descriptor. The default implementation
	 * return an instance of {@link ChartImageDescriptor}.
	 */
	protected IImageDescriptor createImageDescriptor(JFreeChart chart) {
		return new ChartImageDescriptor(chart, getPreferredSize());
	}

	/**
	 * Template method to obtain preferred size. Default implementation returns
	 * {@link #DEFAULT_PREFERRED_SIZE}.
	 */
	protected Dimension getPreferredSize() {
		return DEFAULT_PREFERRED_SIZE;
	}

	/**
	 * Template method for creating the actual chart.
	 * 
	 * @throws ConQATException
	 *             if a problem occurs during chart creation.
	 */
	protected abstract JFreeChart createChart() throws ConQATException;

}