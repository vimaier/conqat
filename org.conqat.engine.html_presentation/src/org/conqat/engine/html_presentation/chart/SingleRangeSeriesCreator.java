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
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.TimeSeriesCollection;

/**
 * This processor allows charting multiple value series with the same range.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 36EEBB9DC2BD80D477221490455B064B
 */
@AConQATProcessor(description = "This processor allows charting "
		+ "multiple value series with the same range. "
		+ SeriesCreatorBase.SERIES_LAYOUTER_COMMENT)
public class SingleRangeSeriesCreator extends SeriesCreatorBase {

	/** List of series' (parallel to descriptionList) */
	private final ArrayList<DateValueSeries> seriesList = new ArrayList<DateValueSeries>();

	/** List of descriptions (parallel to seriesList) */
	private final ArrayList<String> descriptionList = new ArrayList<String>();

	/** Should zero be included in the range. */
	protected boolean includeZero = true;

	/** Range axis label. */
	protected String axisLabel;

	/** Add value series. */
	@AConQATParameter(name = "series", minOccurrences = 1, description = "Value series to chart.")
	public void addValueSeries(
			@AConQATAttribute(name = "ref", description = "Reference to series generating processor.") DateValueSeries series,
			@AConQATAttribute(name = "description", description = "Series description.") String seriesDescription) {

		seriesList.add(series);
		descriptionList.add(seriesDescription);
	}

	/** Should zero be included in the range. */
	@AConQATParameter(name = "include", minOccurrences = 0, maxOccurrences = 1, description = "Include 0 in range? [true]")
	public void setIncludeZero(
			@AConQATAttribute(name = "zero", description = "Include 0 in range? [true]") boolean includeZero) {

		this.includeZero = includeZero;
	}

	/** Range axis label. */
	@AConQATParameter(name = "axis", minOccurrences = 1, maxOccurrences = 1, description = "Range axis label.")
	public void setAxisLabel(
			@AConQATAttribute(name = "label", description = "Name of the output directory") String axisLabel) {

		this.axisLabel = axisLabel;
	}

	/** Create data set with all serieses. */
	protected TimeSeriesCollection createDataset() {
		TimeSeriesCollection dataset = new TimeSeriesCollection();

		CCSMAssert.isTrue(descriptionList.size() == seriesList.size(),
				"Lists should have same length!");

		for (int i = 0; i < descriptionList.size(); i++) {
			dataset.addSeries(createTimeSeries(descriptionList.get(i),
					seriesList.get(i)));
		}

		return dataset;
	}

	/** Create the chart. */
	@Override
	protected JFreeChart createChart() {
		return setupChart(createDataset(), axisLabel, includeZero);
	}

	/** {@inheritDoc} */
	@Override
	protected ArrayList<DateValueSeries> getSeries() {
		return seriesList;
	}
}