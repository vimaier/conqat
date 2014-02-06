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
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.assessment.AssessmentRange;
import org.conqat.engine.commons.range_distribution.IAssessmentRule;
import org.conqat.engine.commons.range_distribution.PercentageLessOrEqualRule;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.html_presentation.EHtmlPresentationFont;
import org.conqat.engine.html_presentation.chart.util.ThresholdAnnotation;
import org.conqat.lib.commons.assessment.ETrafficLightColor;
import org.conqat.lib.commons.collections.UnmodifiableCollection;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 5F88E05A30E1C058C5090DE127439E1A
 */
@AConQATProcessor(description = "This layouter visualizes Range Distributions as stacked bar charts. Charts can be "
		+ "drawn for the percental or absolute values. This layouter can visualize assessments defined through "
		+ "PercentageLessOrEqualRules.")
public class RangeDistributionBarChartCreator extends
		RangeDistributionChartCreatorBase {

	/** Secondary metrics. */
	private final Set<String> metrics = new HashSet<String>();

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "reverse", attribute = "value", description = "If this is true, the range with the highest "
			+ "principal metric value is drawn to the left [true]", optional = true)
	public boolean reverse = true;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "show-treshold-labels", attribute = "value", description = "If this is true, potentially"
			+ "thrown thresholds are labeled [false]", optional = true)
	public boolean showThresholdLabels = false;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "show-absolute-values", attribute = "value", description = "If set to true, absolute values are shown, relative"
			+ " values otherwhise [default is false]", optional = true)
	public boolean showAbsoluteValues = false;

	/** Ranges defined by the range distribution. */
	private List<AssessmentRange> ranges = new ArrayList<AssessmentRange>();

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "secondary-metric", description = "Secondary metrics to display.", minOccurrences = 1)
	public void addRangeName(
			@AConQATAttribute(name = ConQATParamDoc.READKEY_KEY_NAME, description = "Secondary metric key") String key) {
		metrics.add(key);
	}

	/** {@inheritDoc} */
	@Override
	protected JFreeChart createChart() throws ConQATException {

		DefaultCategoryDataset dataset = initDataset();

		JFreeChart chart = ChartFactory.createStackedBarChart(title, null,
				null, dataset, PlotOrientation.HORIZONTAL, false, false, false);

		CategoryPlot plot = stylePlot(chart);

		setUpAnnotations(plot);
		configureAxes(plot);
		configureRenderer(plot);

		return chart;
	}

	/** {@inheritDoc} */
	@Override
	protected Dimension getPreferredSize() {
		return new Dimension(800, 50 + 60 * metrics.size());
	}

	/** Initialize dataset. */
	private DefaultCategoryDataset initDataset() {
		ranges.addAll(rangeDistribution.getRanges());
		if (reverse) {
			Collections.reverse(ranges);
		}

		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		for (String metric : metrics) {
			for (AssessmentRange range : ranges) {
				if (range.isEmpty()) {
					continue;
				}
				dataset.addValue(getSum(range, metric), range, metric);
			}
		}
		return dataset;
	}

	/**
	 * Returns 1 if {@link #showAbsoluteValues} is true. Otherwise, this returns
	 * the maximum total of the specified secondary metrics.
	 */
	private double getTotal() {
		if (!showAbsoluteValues) {
			return 1;

		}

		double max = Double.NEGATIVE_INFINITY;
		for (String metric : metrics) {
			max = Math.max(max, rangeDistribution.getTotal(metric));
		}
		return max;
	}

	/** Style plot. */
	private CategoryPlot stylePlot(JFreeChart chart) {
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

	/** Set up annotations showing thresholds. */
	private void setUpAnnotations(CategoryPlot plot) throws ConQATException {
		UnmodifiableCollection<IAssessmentRule> rules = rangeDistribution
				.getAssessmentRules();

		for (IAssessmentRule rule : rules) {
			if (rule instanceof PercentageLessOrEqualRule
					&& !showAbsoluteValues) {
				PercentageLessOrEqualRule lessThanRule = (PercentageLessOrEqualRule) rule;
				if (metrics.contains(lessThanRule.getSecondaryMetric())
				// only display the annotations for violated thresholds
						&& rule.assess(rangeDistribution).getDominantColor() != ETrafficLightColor.GREEN) {
					ThresholdAnnotation annotation = new ThresholdAnnotation(
							lessThanRule, showThresholdLabels);
					plot.addAnnotation(annotation);
				}

			}
		}
	}

	/** Configure renderer. */
	private void configureRenderer(CategoryPlot plot) {
		BarRenderer renderer = (BarRenderer) plot.getRenderer();
		renderer.setMaximumBarWidth(0.5);
		renderer.setSeriesPaint(0, Color.BLACK);

		renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
		renderer.setBaseItemLabelsVisible(true);
		renderer.setBaseItemLabelPaint(Color.white);
		renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(
				ItemLabelAnchor.CENTER, TextAnchor.CENTER, TextAnchor.CENTER,
				0.0));
		renderer.setBaseItemLabelFont(EHtmlPresentationFont.SANS_CONDENSED
				.getFont());

		for (AssessmentRange range : ranges) {
			int index = plot.getDataset().getRowIndex(range);
			renderer.setSeriesPaint(index, range.getColor());
			if (obtainGrayLevel(range.getColor()) > 128) {
				// use black label color on bright bar, e.g. yellow
				renderer.setSeriesItemLabelPaint(index, Color.black);
			}
		}

		if (!showAbsoluteValues) {
			renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator(
					"{2}", NumberFormat.getPercentInstance()));
		}

		renderer.setBarPainter(new StandardBarPainter());
		renderer.setShadowVisible(false);
	}

	/** Configure axes. */
	private void configureAxes(CategoryPlot plot) {
		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setLabelFont(EHtmlPresentationFont.SANS_CONDENSED.getFont());
		if (!showAbsoluteValues) {
			rangeAxis
					.setNumberFormatOverride(NumberFormat.getPercentInstance());
		}
		rangeAxis.setRange(0, getTotal());

		plot.getDomainAxis().setLabelFont(
				EHtmlPresentationFont.SANS_CONDENSED.getFont());
	}

	/** Obtain gray value of a color. */
	private static int obtainGrayLevel(Color color) {
		return (color.getRed() + color.getGreen() + color.getBlue()) / 3;
	}

	/**
	 * Returns either the sum or the percental value, depending on
	 * {@link #showAbsoluteValues}.
	 */
	private double getSum(AssessmentRange range, String secondaryMetric) {
		if (showAbsoluteValues) {
			return rangeDistribution.getSum(range, secondaryMetric);
		}
		return rangeDistribution.getPercentage(range, secondaryMetric);
	}
}