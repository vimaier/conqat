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

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.conqat.engine.commons.statistics.KeyedData;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.string.StringUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: F55465ECC7F277EBB2B2EC97811E9170
 */
@AConQATProcessor(description = "This processor creates bar charts from KeyedData-objects. "
		+ "All input categories share the same domain and range axis.")
public class BarChartCreator extends ChartCreatorBase {

	/** Data that gets layouted */
	private final List<SeriesDescriptor> seriesList = new ArrayList<SeriesDescriptor>();

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "title", attribute = "title", description = ""
			+ "The title displayed at the top of the chart")
	public String title;

	/** Force inclusion of zero in auto range? */
	private boolean includeZero = true;

	/** Show only ticks for integer values in range axis */
	private boolean integerRange = false;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "domain-axis", attribute = "label", optional = true, description = ""
			+ "Caption of the domain axis")
	public String domainAxisLabel = "Category";

	/** Caption of the range axis */
	private String rangeAxisLabel = "Value";

	/** Flag that determines whether domain axis is visible */
	private boolean domainAxisVisible = true;

	/** Flag that determines whether range axis is visible */
	private boolean rangeAxisVisible = true;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "orientation", attribute = "value", optional = true, description = ""
			+ "Sets the orientation of the plot (default is vertical).")
	public EPlotOrientation orientation = EPlotOrientation.VERTICAL;

	/** The approximate thickness of a single bar. */
	private int barThickness = 0;

	/** The approximate length of a bar. */
	private int barLength = 0;

	/**
	 * {@ConQAT.Doc}
	 * 
	 * @param data
	 *            the series to add <br>
	 *            although this is called from non-generics-aware driver, we can
	 *            safely specify the lower bound here as per definition there
	 *            can be no KeyedData-instances which do not satisfy the
	 *            Comparable-interface.
	 */
	@AConQATParameter(name = "data", minOccurrences = 1, description = "Data to chart.")
	public void addDataSeries(
			@AConQATAttribute(name = "name", description = "Name of the data set") String name,
			@AConQATAttribute(name = "ref", description = "Reference to series generating processor.") KeyedData<Comparable<?>> data,
			@AConQATAttribute(name = "show-values", description = "En- or disables display of the value as text at the top of the bar") boolean showValues) {

		seriesList.add(new SeriesDescriptor(name, data, showValues));
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "range-axis", minOccurrences = 0, maxOccurrences = 1, description = "Cofiguration details for the range axis")
	public void setRangeAxis(
			@AConQATAttribute(name = "label", description = "range axis label") String rangeAxisLabel,
			@AConQATAttribute(name = "include-zero", description = "Force inclusion of zero in auto range? [true]") boolean includeZero,
			@AConQATAttribute(name = "integer-range", description = "Draw axis ticks for integer values only? [false]") boolean integerRange) {

		this.rangeAxisLabel = rangeAxisLabel;
		this.includeZero = includeZero;
		this.integerRange = integerRange;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "hide", description = "Hide axes. Axes are displayed by default", minOccurrences = 0, maxOccurrences = 1)
	public void setHideAxis(
			@AConQATAttribute(name = "domain-axis", description = "Set to true to hide domain axis") boolean hideDomainAxis,
			@AConQATAttribute(name = "range-axis", description = "Set to true to hide range axis") boolean hideRangeAxis) {
		domainAxisVisible = !hideDomainAxis;
		rangeAxisVisible = !hideRangeAxis;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "smart-sizing", description = "Enables smart sizing of the bar chart. The given pixel values will be applied approximately and are far from being pixel-perfect.", minOccurrences = 0, maxOccurrences = 1)
	public void setSmartSizing(
			@AConQATAttribute(name = "bar-thickness", description = "Sets the approximate thickness of a single bar.") int barThickness,
			@AConQATAttribute(name = "bar-length", description = "Sets the approximate length of a single bar.") int barLength) {
		CCSMAssert.isTrue(barThickness > 0,
				"Bar thickness must be a positive integer");
		CCSMAssert.isTrue(barLength > 0,
				"Bar length must be a positive integer");
		this.barThickness = barThickness;
		this.barLength = barLength;
	}

	/** {@inheritDoc} */
	@Override
	protected JFreeChart createChart() {
		DefaultCategoryDataset dataset = createDataset();

		JFreeChart chart = ChartFactory.createBarChart(title, domainAxisLabel,
				rangeAxisLabel, dataset, orientation.getOrientation(),
				drawLegend, // legend
				false, // tooltips
				false); // urls

		CategoryPlot plot = chart.getCategoryPlot();

		labelSeries(plot.getRenderer());
		configureAxes(plot);

		return chart;
	}

	/** Create dataset. */
	@SuppressWarnings({ "rawtypes" })
	private DefaultCategoryDataset createDataset() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		for (int seriesNumber = 0; seriesNumber < seriesList.size(); seriesNumber++) {
			SeriesDescriptor series = seriesList.get(seriesNumber);
			series.number = seriesNumber;
			Map<Comparable<?>, Double> values = series.data.getValues();

			ArrayList<Comparable> list = new ArrayList<Comparable>(
					values.keySet());

			for (Comparable<?> key : list) {
				dataset.setValue(values.get(key), series.name, key);
			}
		}

		return dataset;
	}

	/**
	 * Sets the visibility of labels at the top of a bar for a series to the
	 * value specified in the {@link SeriesDescriptor}.
	 */
	private void labelSeries(CategoryItemRenderer renderer) {
		renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
		renderer.setBaseItemLabelsVisible(Boolean.FALSE);

		for (SeriesDescriptor series : seriesList) {
			if (series.showValues) {
				renderer.setSeriesItemLabelsVisible(series.number, Boolean.TRUE);
			}
		}
	}

	/** Configures display of domain and range axes */
	private void configureAxes(CategoryPlot plot) {
		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setAutoRangeIncludesZero(includeZero);
		if (integerRange) {
			rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		}
		rangeAxis.setUpperMargin(0.15);
		rangeAxis.setVisible(rangeAxisVisible);

		CategoryAxis domainAxis = plot.getDomainAxis();

		if (orientation == EPlotOrientation.HORIZONTAL) {
			domainAxis
					.setCategoryLabelPositions(CategoryLabelPositions.STANDARD);
			domainAxis.setMaximumCategoryLabelWidthRatio(.5f);
		} else {
			domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
		}

		domainAxis.setVisible(domainAxisVisible);
	}

	/** {@inheritDoc} */
	@Override
	protected Dimension getPreferredSize() {
		// return default value if smart sizing is not enabled
		if (barThickness <= 0 || barLength <= 0) {
			return super.getPreferredSize();
		}

		int height = 0;
		int numBars = 0;
		for (SeriesDescriptor series : seriesList) {
			numBars += series.data.getValues().size();
		}

		height = numBars * barThickness;

		// save 35px if we want to show the title
		if (!StringUtils.isEmpty(title)) {
			height += 35;
		}

		// save another 25px per series if we want to show the legend
		if (drawLegend) {
			height += 25 * seriesList.size();
		}

		if (orientation == EPlotOrientation.HORIZONTAL) {
			height += calculateAxisOffset(rangeAxisVisible, rangeAxisLabel);

			return new Dimension(barLength, height);
		}

		height += calculateAxisOffset(domainAxisVisible, domainAxisLabel);

		return new Dimension(height, barLength);
	}

	/**
	 * Calculates an offset depending on visibility of the axis and the axis
	 * label.
	 */
	private int calculateAxisOffset(boolean axisVisible, String axisLabel) {
		int offset = 0;

		// add approximately 20px for the range axis, if visible
		if (axisVisible) {
			offset += 20;
		}

		// also 20px if we have an axis label
		if (!StringUtils.isEmpty(axisLabel)) {
			offset += 20;
		}
		return offset;
	}

	/** Holding all relevant information concerning a series */
	private final static class SeriesDescriptor {

		/** The values */
		private final KeyedData<Comparable<?>> data;

		/** En- or disables display of the value as text at the top of the bar */
		private final boolean showValues;

		/** Series label */
		private final String name;

		/** Identifies the Position in the CategoryDataSet */
		private int number;

		/** Constructor */
		public SeriesDescriptor(String name, KeyedData<Comparable<?>> data,
				boolean showValues) {
			this.name = name;
			this.data = data;
			this.showValues = showValues;
		}
	}
}