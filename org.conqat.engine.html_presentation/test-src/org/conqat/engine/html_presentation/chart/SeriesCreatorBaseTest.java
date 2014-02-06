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

import java.util.Calendar;
import java.util.Date;

import junit.framework.TestCase;

import org.conqat.engine.commons.statistics.DateValueSeries;
import org.jfree.data.time.TimeSeries;

/**
 * Test for {@link SeriesCreatorBase}.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 9F92E1914056EC0621B3026653C28446
 */
public class SeriesCreatorBaseTest extends TestCase {

	/** Creator under test. */
	private final SeriesCreatorBase creator = new SingleRangeSeriesCreator();

	/** Series used for test. */
	private DateValueSeries series = new DateValueSeries();

	/** Create series with to data point at two different days. */
	@Override
	protected void setUp() throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(0);
		Date date1 = calendar.getTime();
		calendar.add(Calendar.DAY_OF_YEAR, 1);
		Date date2 = calendar.getTime();
		series.addValue(date1, 1);
		series.addValue(date2, 2);
	}

	/**
	 * Check if assignment to time resolution works. Resolution greater than day
	 * are expected to hold only one value.
	 */
	public void testCreateTimeSeries() {
		assertItems(ETimeResolution.MILLISECOND, 2);
		assertItems(ETimeResolution.SECOND, 2);
		assertItems(ETimeResolution.MINUTE, 2);
		assertItems(ETimeResolution.HOUR, 2);
		assertItems(ETimeResolution.DAY, 2);
		assertItems(ETimeResolution.WEEK, 1);
		assertItems(ETimeResolution.MONTH, 1);
		assertItems(ETimeResolution.YEAR, 1);
	}

	/**
	 * This is a test for CR #1589. This ensures that the most recent value with
	 * a resolution entity is used.
	 */
	public void testMostRecent() {
		creator.resolution = ETimeResolution.YEAR;
		series = new DateValueSeries();

		Calendar calendar = Calendar.getInstance();

		// inverted loop to ensure that order of addition is not relevant
		for (int i = 365; i > 0; i--) {
			calendar.set(Calendar.DAY_OF_YEAR, i);
			series.addValue(calendar.getTime(), i);
		}

		TimeSeries result = creator.createTimeSeries("test", series);
		assertEquals(365.0, result.getDataItem(0).getValue().doubleValue());
	}

	/**
	 * Create time series and assert that the number of values matches the
	 * expected item count.
	 */
	public void assertItems(ETimeResolution resolution, int expectedItemCount) {
		creator.resolution = resolution;
		TimeSeries result = creator.createTimeSeries("test", series);
		assertEquals(expectedItemCount, result.getItemCount());
	}
}