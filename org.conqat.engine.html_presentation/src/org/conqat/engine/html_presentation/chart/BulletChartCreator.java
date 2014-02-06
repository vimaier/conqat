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

import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.lib.commons.color.ECCSMColor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.Layer;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating YELLOW Hash: 2DC3EFD52871788816EF6D129CCA7238
 */
@AConQATProcessor(description = "Creates a Bullet Chart similar to the Bullet "
		+ "Graphs defined by Stephen Few.")
public class BulletChartCreator extends AssessmentChartCreatorBase {

	/** {@inheritDoc} */
	@Override
	protected Dimension getPreferredSize() {
		return new Dimension(800, 150);
	}

	/** {@inheritDoc} */
	@Override
	protected JFreeChart createChart() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		dataset.addValue(value, "", "");

		JFreeChart chart = ChartFactory.createBarChart(title, null, null,
				dataset, PlotOrientation.HORIZONTAL, false, false, false);

		CategoryPlot plot = customizePlot(chart);
		setIntervals(plot);
		customizeAxis(plot);
		customizeRenderer(plot);

		return chart;
	}

	/** Customize plot */
	private CategoryPlot customizePlot(JFreeChart chart) {
		CategoryPlot plot = chart.getCategoryPlot();

		plot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
		plot.setOutlineVisible(false);
		plot.setInsets(new RectangleInsets(0.0, 0.0, 0.0, 0.0));
		plot.setBackgroundPaint(null);
		plot.setDomainGridlinesVisible(false);
		plot.setRangeGridlinesVisible(false);
		plot.setRangeCrosshairVisible(false);

		return plot;
	}

	/** Set intervals. */
	private void setIntervals(CategoryPlot plot) {
		addInterval(0.0, greenThreshold, ECCSMColor.GREEN, plot);
		addInterval(greenThreshold, yellowThreshold, ECCSMColor.YELLOW, plot);
		addInterval(yellowThreshold, redThreshold, ECCSMColor.RED, plot);
	}

	/** Add a single interval to the plot. */
	private void addInterval(double lower, double upper, ECCSMColor ccsmColor,
			CategoryPlot plot) {
		IntervalMarker interval = new IntervalMarker(lower, upper,
				ccsmColor.getColor());
		plot.addRangeMarker(interval, Layer.BACKGROUND);
	}

	/** Customize renderer. */
	private void customizeRenderer(CategoryPlot plot) {
		BarRenderer renderer = (BarRenderer) plot.getRenderer();
		renderer.setMaximumBarWidth(0.5);
		renderer.setSeriesPaint(0, Color.BLACK);

		renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
		renderer.setBaseItemLabelsVisible(true);
		renderer.setBaseItemLabelPaint(Color.white);
		renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(
				ItemLabelAnchor.CENTER, TextAnchor.CENTER, TextAnchor.CENTER,
				0.0));

		renderer.setBarPainter(new StandardBarPainter());
		renderer.setShadowVisible(false);
	}

	/** Customize ranges. */
	private void customizeAxis(CategoryPlot plot) {
		plot.getDomainAxis().setVisible(false);
		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setRangeWithMargins(0, redThreshold);
		rangeAxis.setLabel(unit);
		rangeAxis.setVisible(false);
	}

}