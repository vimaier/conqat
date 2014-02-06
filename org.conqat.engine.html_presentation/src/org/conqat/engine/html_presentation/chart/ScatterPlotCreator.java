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

import static org.jfree.chart.plot.PlotOrientation.VERTICAL;

import java.awt.Color;

import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.function.Function2D;
import org.jfree.data.function.LineFunction2D;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.statistics.Regression;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: F2DA7C1C8D721B833E22AF5BF91EEFB6
 */
@AConQATProcessor(description = "Creates a scatter plot from an XYSeries. "
		+ "Optionally, a regression line is fitted into the scatter plot.")
public class ScatterPlotCreator extends ChartCreatorBase {

	/** Color in which the regression line is rendered */
	private static final Color REGRESSION_LINE_COLOR = Color.blue;

	/** Number of samples computed for the regression line */
	private static final int REGRESSION_LINE_SAMPLE_COUNT = 100;

	/** Data that is displayed in the scatterplot */
	private XYSeries scatterPlotSeries;

	/** Label of the range axis (x-axis) */
	private String rangeAxisLabel;

	/** Label of the domain axis (y-axis) */
	private String domainAxisLabel;

	/** Title of the diagram */
	private String title;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "title", minOccurrences = 0, maxOccurrences = 1, description = "The title displayed at the top of the chart")
	public void setDescription(
			@AConQATAttribute(name = "title", description = "Data description.") String title) {
		this.title = title;
	}

	/** determines if the regression function will be drawn. default: false */
	private boolean drawRegression = false;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "regression", minOccurrences = 0, maxOccurrences = 1, description = "Flag that determines whether a regression line is fitted into the scatter plot")
	public void setDrawRegressionFct(
			@AConQATAttribute(name = "draw", description = "Default: false", defaultValue = "false") boolean drawRegression) {
		this.drawRegression = drawRegression;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "data", minOccurrences = 1, maxOccurrences = 1, description = "Scatter plot data and display options")
	public void setXYSeries(
			@AConQATAttribute(name = "series", description = "Data that gets displayed") XYSeries scatterPlotSeries,
			@AConQATAttribute(name = "name", description = "Data name displayed in legend") String seriesName,
			@AConQATAttribute(name = "domain-axis-label", description = "Label shown on the x axis") String domainAxisName,
			@AConQATAttribute(name = "range-axis-label", description = "Label shown on the y axis") String rangeAxisName) {

		this.scatterPlotSeries = scatterPlotSeries;
		this.scatterPlotSeries.setKey(seriesName);
		this.rangeAxisLabel = rangeAxisName;
		this.domainAxisLabel = domainAxisName;
	}

	/** {@inheritDoc} */
	@Override
	protected JFreeChart createChart() {
		XYDataset dataset = new XYSeriesCollection(scatterPlotSeries);

		JFreeChart chart = ChartFactory.createScatterPlot(title,
				domainAxisLabel, rangeAxisLabel, dataset, VERTICAL, drawLegend// legend
				, false // tooltips
				, false // urls
				);

		if (drawRegression) {
			drawRegression(dataset, chart.getXYPlot());
		}

		return chart;
	}

	/** Handles drawing of the regression line */
	private void drawRegression(XYDataset scatterPlotData, XYPlot xyplot) {
		// compute regression function
		double regressionParams[] = Regression.getOLSRegression(
				scatterPlotData, 0);
		Function2D regression = new LineFunction2D(regressionParams[0],
				regressionParams[1]);

		// sample function values
		double lowerBound = xyplot.getDomainAxis().getLowerBound();
		double upperBound = xyplot.getDomainAxis().getUpperBound();
		XYDataset regressionDataset = DatasetUtilities.sampleFunction2D(
				regression, lowerBound, upperBound,
				REGRESSION_LINE_SAMPLE_COUNT, "Fitted Regression Line");
		xyplot.setDataset(1, regressionDataset);

		// render regression line into plot
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true,
				false);
		xyplot.setRenderer(1, renderer);
		renderer.setSeriesPaint(0, REGRESSION_LINE_COLOR);
	}
}