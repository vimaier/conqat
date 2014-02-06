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

import java.util.ArrayList;

import org.conqat.engine.commons.statistics.DateValueSeries;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: F289526DC0060E207267B4A22374854F
 */
@AConQATProcessor(description = "This processor allows charting "
		+ "multiple value series with different ranges. "
		+ SeriesCreatorBase.SERIES_LAYOUTER_COMMENT)
public class MultiRangeSeriesCreator extends SeriesCreatorBase {

	/** The datasets to layout. */
	private final ArrayList<DatasetDescriptor> datasets = new ArrayList<DatasetDescriptor>();

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "series", minOccurrences = 1, description = "Value series to chart.")
	public void addValueSeries(
			@AConQATAttribute(name = "ref", description = "Reference to series generating processor.") DateValueSeries series,
			@AConQATAttribute(name = "description", description = "Series description.") String seriesDescription,
			@AConQATAttribute(name = "axis-label", description = "Range axis label.") String axisLabel,
			@AConQATAttribute(name = "include-zero", description = "Include 0 in range?", defaultValue = "true") boolean includeZero) {

		datasets.add(new DatasetDescriptor(series, seriesDescription,
				axisLabel, includeZero));
	}

	/** Create the chart. */
	@Override
	protected JFreeChart createChart() {

		XYDataset dataset = createDataset(0);

		// create first data set
		DatasetDescriptor initialDescriptor = datasets.get(0);
		JFreeChart chart = setupChart(dataset,
				initialDescriptor.getAxisLabel(), initialDescriptor
						.isIncludeZero());

		addAdditionalDatasets(chart);
		return chart;
	}

	/**
	 * Add additional data sets to chart.
	 */
	private void addAdditionalDatasets(JFreeChart chart) {

		// return if there are no further data sets.
		if (datasets.size() < 2) {
			return;
		}

		XYPlot xyplot = chart.getXYPlot();

		for (int i = 1; i < datasets.size(); i++) {

			DatasetDescriptor descriptor = datasets.get(i);

			NumberAxis axis = new NumberAxis(descriptor.getAxisLabel());
			axis.setAutoRangeIncludesZero(descriptor.isIncludeZero());
			xyplot.setRangeAxis(i, axis);

			XYDataset xydataset = createDataset(i);
			xyplot.setDataset(i, xydataset);
			xyplot.mapDatasetToRangeAxis(i, i);

			StandardXYItemRenderer renderer = new StandardXYItemRenderer();
			renderer.setBaseShapesVisible(true);
			renderer.setBaseShapesFilled(true);

			xyplot.setRenderer(i, renderer);
		}
	}

	/**
	 * Create ith dataset.
	 */
	private XYDataset createDataset(int i) {
		TimeSeriesCollection dataset = new TimeSeriesCollection();

		dataset.addSeries(createTimeSeries(datasets.get(i).getDescription(),
				datasets.get(i).getValueSeries()));

		return dataset;
	}

	/** {@inheritDoc} */
	@Override
	protected ArrayList<DateValueSeries> getSeries() {
		ArrayList<DateValueSeries> series = new ArrayList<DateValueSeries>();
		for (DatasetDescriptor dataset : datasets) {
			series.add(dataset.getValueSeries());
		}
		return series;
	}

}