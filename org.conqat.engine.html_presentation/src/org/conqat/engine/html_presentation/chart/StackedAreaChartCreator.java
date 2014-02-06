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
import java.util.ArrayList;

import org.conqat.engine.commons.statistics.DateValueSeries;
import org.conqat.engine.commons.statistics.MultiDateValueSeries;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.color.ColorUtils;
import org.conqat.lib.commons.string.StringUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.time.TimeTableXYDataset;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 41B70BF3BDF74942167CB1AFD6CD1A24
 */
@AConQATProcessor(description = "This layouter creates stacked area charts from "
		+ "DateValueSeries-objects. The provide series must be perfectly aligned, "
		+ "i.e. each series must contain exactly the same measurement times. "
		+ SeriesCreatorBase.SERIES_LAYOUTER_COMMENT)
public class StackedAreaChartCreator extends SeriesCreatorBase {

	/** Constant for AUTO coloring */
	private static final String AUTO = "AUTO";

	/** List of series to be included in chart. */
	private final ArrayList<SeriesDescriptor> seriesList = new ArrayList<SeriesDescriptor>();

	/** Range axis label. */
	private String rangeAxisLabel;

	/** Current hue value (ins HVS color model) used for auto color generation. */
	private float hue = .66f;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "labels", minOccurrences = 1, maxOccurrences = 1, description = "Chart labels")
	public void setAxisLabels(
			@AConQATAttribute(name = "range-axis", description = "Domain axis label") String rangeAxisLabel) {
		this.rangeAxisLabel = rangeAxisLabel;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "series", minOccurrences = 0, description = "Value series to chart.")
	public void addValueSeries(
			@AConQATAttribute(name = "ref", description = "Reference to series generating processor.") DateValueSeries series,
			@AConQATAttribute(name = "title", description = "Series title") String title,
			@AConQATAttribute(name = "color", description = "Series color", defaultValue = AUTO) String colorString) {

		Color color;
		if (AUTO.equals(colorString)) {
			color = null;
		} else {
			color = ColorUtils.fromString(colorString);
			if (color == null) {
				getLogger()
						.warn(colorString
								+ " is not a valid color. Using automatic coloring for series "
								+ title);
			}
		}

		seriesList.add(new SeriesDescriptor(series, title, color));
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "multi-series", minOccurrences = 0, description = "Adds a multi-series to chart.")
	public void addMultiValueSeries(
			@AConQATAttribute(name = "ref", description = "Reference to series generating processor.") MultiDateValueSeries multiSeries) {
		for (int i = 0; i < multiSeries.getSize(); ++i) {
			seriesList.add(new SeriesDescriptor(multiSeries.getSeries(i),
					multiSeries.getName(i), null));
		}
	}

	/** Create chart. */
	@Override
	protected JFreeChart createChart() throws ConQATException {
		if (seriesList.isEmpty()) {
			throw new ConQATException("No input series provided!");
		}

		JFreeChart chart = ChartFactory.createStackedXYAreaChart(chartTitle,
				StringUtils.EMPTY_STRING, rangeAxisLabel, createDataset(),
				PlotOrientation.VERTICAL, drawLegend, false, false);

		XYPlot plot = (XYPlot) chart.getPlot();

		ValueAxis timeAxis = new DateAxis();
		timeAxis.setLowerMargin(0);
		timeAxis.setUpperMargin(0);
		plot.setDomainAxis(timeAxis);

		setupXYPlot(plot, true);

		XYItemRenderer renderer = plot.getRenderer();
		for (int i = 0; i < seriesList.size(); i++) {
			Color color = seriesList.get(i).seriesColor;
			if (color == null) {
				color = getNextAutoColor();
			}
			renderer.setSeriesPaint(i, color);
		}

		return chart;
	}

	/** Create the dataset used for the plot. */
	private TimeTableXYDataset createDataset() {
		TimeTableXYDataset dataset = new TimeTableXYDataset();
		for (SeriesDescriptor seriesDesriptor : seriesList) {
			String seriesTitle = seriesDesriptor.seriesTitle;
			TimeSeries timeSeries = createTimeSeries(seriesTitle,
					seriesDesriptor.series);
			for (int i = 0; i < timeSeries.getItemCount(); ++i) {
				TimeSeriesDataItem di = timeSeries.getDataItem(i);
				dataset.add(di.getPeriod(), di.getValue().doubleValue(),
						seriesTitle);
			}
		}
		return dataset;
	}

	/** Class to store information that belongs to a series. */
	private class SeriesDescriptor {

		/** The value series. */
		private final DateValueSeries series;

		/** Series title. */
		private final String seriesTitle;

		/** Series color. Use <code>null</code> for auto color selection. */
		private final Color seriesColor;

		/** Create descriptor. */
		public SeriesDescriptor(DateValueSeries series, String seriesTitle,
				Color seriesColor) {
			this.series = series;
			this.seriesTitle = seriesTitle;
			this.seriesColor = seriesColor;
		}
	}

	/** {@inheritDoc} */
	@Override
	protected ArrayList<DateValueSeries> getSeries() {
		ArrayList<DateValueSeries> series = new ArrayList<DateValueSeries>();
		for (SeriesDescriptor descriptor : seriesList) {
			series.add(descriptor.series);
		}
		return series;
	}

	/**
	 * Creates a new color for auto-coloring. The reason for this is that
	 * JFreeChart will provide different color based on whether a legend is
	 * drawn or not, resulting in strange effects when displaying the same trend
	 * twice.
	 * <p>
	 * For the approach chosen and the magic number (called golden ratio) see
	 * http://martin.ankerl.com/2009/12/09/how-to-create-random-colors-
	 * programmatically/
	 */
	private Color getNextAutoColor() {
		Color color = Color.getHSBColor(hue, .6f, .8f);
		hue += 0.618033988749895;
		return color;
	}
}