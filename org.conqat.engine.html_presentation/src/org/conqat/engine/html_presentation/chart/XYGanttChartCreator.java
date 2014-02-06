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

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.gantt.XYTaskDataset;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: poehlmann $
 * @version $Rev: 41870 $
 * @ConQAT.Rating GREEN Hash: F5386B7EFE44B68876D6E14AE5EA3D2A
 */
@AConQATProcessor(description = "Creates a Gantt chart from one or more task series. "
		+ "All tasks contained in a single series are displayed in the same height.")
public class XYGanttChartCreator extends ChartCreatorBase {

	/** Default range axis label */
	private static final String DEFAULT_RANGE_AXIS_LABEL = "Timing";

	/** Default domain axis label */
	private static final String DEFAULT_DOMAIN_AXIS_LABEL = "Series";

	/** The task series that get displayed */
	private TaskSeriesCollection taskSeriesCollection;

	/** Title of the diagram */
	private String title;

	/** Caption of the domain axis */
	private String domainAxisLabel = DEFAULT_DOMAIN_AXIS_LABEL;

	/** Caption of the range axis */
	private String rangeAxisLabel = DEFAULT_RANGE_AXIS_LABEL;

	/** Controls the display of shadows in the chart. */
	private boolean shadowsVisible = true;

	/** Positions of markers that are drawn. */
	private final List<Double> domainAxisMarkers = new ArrayList<Double>();

	/** Maps from series name to color in which series should be rendered */
	private final Map<String, Color> seriesColors = new HashMap<String, Color>();

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "duplicate-domain-axis", attribute = "value", description = "If set to true, domain axis is drawn both on the left and right side of chart. Default is false.", optional = true)
	public boolean duplicateDomainAxis = false;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "title", minOccurrences = 0, maxOccurrences = 1, description = "The title displayed at the top of the chart")
	public void setDescription(
			@AConQATAttribute(name = "title", description = "Data description.") String title) {
		this.title = title;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "domain-axis-marker", minOccurrences = 0, description = "Positions of marker lines that are drawn. Position counting starts with 0 at the tick label of the first series. "
			+ "Every series increments count by 1. To draw a line between two markers, increment by 0.5.")
	public void addDomainAxisMarker(
			@AConQATAttribute(name = "value", description = "The value to mark.") Double value) {
		domainAxisMarkers.add(value);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "shadows-visible", minOccurrences = 0, maxOccurrences = 1, description = "Enable or disable shadows")
	public void setShadowsVisible(
			@AConQATAttribute(name = "value", description = "Default is to enable shadows") Boolean value) {
		shadowsVisible = value;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "range-axis", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "Caption of the range axis (horizontal or X-axis)")
	public void setRangeAxis(
			@AConQATAttribute(name = "label", description = "Default is "
					+ DEFAULT_RANGE_AXIS_LABEL) String rangeAxisLabel) {
		this.rangeAxisLabel = rangeAxisLabel;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "domain-axis", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "Caption of the domain axis (vertical or Y-value)")
	public void setDomainAxisLabel(
			@AConQATAttribute(name = "label", description = "Default is "
					+ DEFAULT_DOMAIN_AXIS_LABEL) String domainAxisLabel) {
		this.domainAxisLabel = domainAxisLabel;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "task-series-collection", minOccurrences = 1, maxOccurrences = 1, description = "Task series collection to chart.")
	public void setTaskSeriesCollection(
			@AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC) TaskSeriesCollection taskSeriesCollection) {
		this.taskSeriesCollection = taskSeriesCollection;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "series-color", minOccurrences = 0, maxOccurrences = -1, description = "Set color of task series")
	public void setSeriesColor(
			@AConQATAttribute(name = "series-name", description = "Name of the series to be colored") String seriesName,
			@AConQATAttribute(name = "color", description = "Color used to draw the series") Color color)
			throws ConQATException {
		Color previousColor = seriesColors.put(seriesName, color);
		if (previousColor != null) {
			throw new ConQATException(
					"Two different colors defined for task series of name: "
							+ seriesName);
		}
	}

	/** {@inheritDoc} */
	@Override
	protected JFreeChart createChart() {
		XYTaskDataset dataset = new XYTaskDataset(taskSeriesCollection);

		JFreeChart chart = ChartFactory.createXYBarChart(title,
				domainAxisLabel, false, rangeAxisLabel, dataset,
				PlotOrientation.HORIZONTAL, drawLegend, false, false);
		XYPlot plot = (XYPlot) chart.getPlot();

		for (Double value : domainAxisMarkers) {
			plot.addDomainMarker(new ValueMarker(value, Color.BLACK,
					new BasicStroke()));
		}

		setSeriesLabelsOnDomainAxis(domainAxisLabel, plot);

		XYBarRenderer renderer = (XYBarRenderer) plot.getRenderer();
		if (!shadowsVisible) {
			// renderer.setShadowVisible() does not work with the current
			// JFreeChart version. we thus hide the shadows behind the bars
			renderer.setShadowXOffset(0);
			renderer.setShadowYOffset(0);
		}
		renderer.setUseYInterval(true);
		plot.setRangeAxis(new DateAxis(rangeAxisLabel));
		ChartUtilities.applyCurrentTheme(chart);

		setSeriesColors(renderer);

		plot.setBackgroundPaint(Color.white);
		return chart;
	}

	/** Set series colors */
	private void setSeriesColors(XYBarRenderer renderer) {
		for (Entry<String, Color> entry : seriesColors.entrySet()) {
			int seriesIndex = taskSeriesCollection.getRowIndex(entry.getKey());

			// If a series name is not found, we silently ignore it. A missing
			// series is not necessarily a configuration error, since it is
			// missing if no data points for it are available.
			if (seriesIndex != -1) {
				renderer.setSeriesPaint(seriesIndex, entry.getValue());
			}
		}
	}

	/** Set descriptions of the individual series as labels of the Y axis */
	private void setSeriesLabelsOnDomainAxis(String label, XYPlot localXYPlot) {
		int seriesCount = taskSeriesCollection.getSeriesCount();
		String[] seriesLabels = new String[seriesCount];
		for (int i = 0; i < seriesCount; i++) {
			seriesLabels[i] = taskSeriesCollection.getSeries(i)
					.getDescription();
		}

		List<ValueAxis> axes = new ArrayList<ValueAxis>();
		axes.add(createAxis(label, seriesLabels));
		if (duplicateDomainAxis) {
			axes.add(createAxis(label, seriesLabels));
		}

		localXYPlot.setDomainAxes(axes.toArray(new ValueAxis[0]));
	}

	/** Creates domain axis */
	private SymbolAxis createAxis(String label, String[] seriesLabels) {
		SymbolAxis leftLocalSymbolAxis = new SymbolAxis(label, seriesLabels);
		leftLocalSymbolAxis.setGridBandsVisible(false);
		return leftLocalSymbolAxis;
	}
}