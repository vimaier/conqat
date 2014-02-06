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

import org.conqat.engine.commons.statistics.DateValueSeries;

/**
 * A dataset descriptor describes a single dataset for the
 * {@link org.conqat.engine.html_presentation.chart.MultiRangeSeriesCreator} to
 * layout.
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: A4D2A38F60D6ED392B4E8EF12447AEB8
 */
/* package */class DatasetDescriptor {

	/** The values. */
	private final DateValueSeries valueSeries;

	/** Should range include zero? */
	private final boolean includeZero;

	/** Dataset description. */
	private final String description;

	/** Range axis label. */
	private final String axisLabel;

	/**
	 * Create new descriptor.
	 * 
	 * @param series
	 *            the value.
	 * @param description
	 *            dataset description
	 * @param label
	 *            range axis label
	 * @param zero
	 *            should range include zero
	 */
	public DatasetDescriptor(DateValueSeries series, String description,
			String label, boolean zero) {

		includeZero = zero;
		valueSeries = series;
		this.description = description;
		axisLabel = label;
	}

	/** Get description. */
	public String getDescription() {
		return description;
	}

	/** Should range include zero? */
	public boolean isIncludeZero() {
		return includeZero;
	}

	/** Get values. */
	public DateValueSeries getValueSeries() {
		return valueSeries;
	}

	/** Get axis label. */
	public String getAxisLabel() {
		return axisLabel;
	}

}