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
package org.conqat.engine.commons.statistics;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import junit.framework.TestCase;
import org.conqat.engine.commons.statistics.ValueSeriesTrimmer.ETrimPolicy;
import org.conqat.engine.core.core.ConQATException;

/**
 * Test for {@link ValueSeriesTrimmer}.
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @levd.rating GREEN Hash: 3F5AE791C3F3B3CF3A1587DE95ED6E8C
 */
public class ValueSeriesTrimmerTest extends TestCase {

	/** The target. */
	private final DateValueSeries target = new DateValueSeries();

	/** The source. */
	private final DateValueSeries source = new DateValueSeries();

	/** The trimmer. */
	private final ValueSeriesTrimmer trimmer = new ValueSeriesTrimmer();

	/** The calendar. */
	private final Calendar calendar = Calendar.getInstance();

	/** {@inheritDoc} */
	@Override
	public void setUp() {

		// factor is used to distinguish series'
		fillSeries(target, 1, 31, 1);
		fillSeries(source, 10, 20, 2);
	}

	/** Test using policy LOWER. */
	public void testLowerBoundPolicy() throws ConQATException {
		trimmer.setTargetSeries(target);
		trimmer.setSourceSeries(source);
		trimmer.setTrimPolicy(ETrimPolicy.LOWER);

		DateValueSeries trimmedSeries = trimmer.process();

		assertDayEquals(getDayInOct2006(10), Collections.min(trimmedSeries
				.getValues().keySet()));
		assertDayEquals(getDayInOct2006(31), Collections.max(trimmedSeries
				.getValues().keySet()));

		assertSeries(trimmedSeries);
	}

	/** Test using policy UPPER. */
	public void testUpperBoundPolicy() throws ConQATException {
		trimmer.setTargetSeries(target);
		trimmer.setSourceSeries(source);
		trimmer.setTrimPolicy(ETrimPolicy.UPPER);

		DateValueSeries trimmedSeries = trimmer.process();

		assertDayEquals(getDayInOct2006(1), Collections.min(trimmedSeries
				.getValues().keySet()));
		assertDayEquals(getDayInOct2006(20), Collections.max(trimmedSeries
				.getValues().keySet()));

		assertSeries(trimmedSeries);
	}

	/** Test using policy BOTH. */
	public void testBothBoundsPolicy() throws ConQATException {
		trimmer.setTargetSeries(target);
		trimmer.setSourceSeries(source);
		trimmer.setTrimPolicy(ETrimPolicy.BOTH);

		DateValueSeries trimmedSeries = trimmer.process();

		assertDayEquals(getDayInOct2006(10), Collections.min(trimmedSeries
				.getValues().keySet()));
		assertDayEquals(getDayInOct2006(20), Collections.max(trimmedSeries
				.getValues().keySet()));

		assertSeries(trimmedSeries);
	}

	/** Test using empty target series. */
	public void testEmptyTargetSeries() throws ConQATException {
		trimmer.setTargetSeries(new DateValueSeries());
		trimmer.setSourceSeries(source);
		trimmer.setTrimPolicy(ETrimPolicy.BOTH);

		DateValueSeries trimmedSeries = trimmer.process();

		assertTrue(trimmedSeries.getValues().isEmpty());
	}

	/** Test using empty source series. */
	public void testEmptySourceSeries() {

		try {
			trimmer.setTargetSeries(target);
			trimmer.setSourceSeries(new DateValueSeries());
			fail();
		} catch (ConQATException e) {
			// expected
		}
	}

	/**
	 * Makes sure that for the given series the value is the same as the day of
	 * the month.
	 */
	private void assertSeries(DateValueSeries trimmedSeries) {
		Map<Date, Double> values = trimmedSeries.getValues();
		for (Date date : values.keySet()) {
			int value = values.get(date).intValue();
			calendar.setTime(date);
			assertEquals(calendar.get(Calendar.DAY_OF_MONTH), value);
		}
	}

	/**
	 * Fills the given series with dates from October 2006 using days in the
	 * range start till end. The factor is used as distance between the dates
	 * added as well as for the value on the series.
	 */
	private void fillSeries(DateValueSeries series, int start, int end,
			int factor) {
		for (int i = start; i <= end; i += factor) {
			calendar.set(2006, Calendar.OCTOBER, i);
			series.addValue(calendar.getTime(), i * factor);
		}
	}

	/** Returns the given day in October 2006 as date. */
	private Date getDayInOct2006(int day) {
		calendar.set(2006, Calendar.OCTOBER, day);
		return calendar.getTime();
	}

	/** Make sure the day of both calendars is the same. */
	private void assertDayEquals(Date date1, Date date2) {

		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTime(date1);

		Calendar calendar2 = Calendar.getInstance();
		calendar2.setTime(date2);

		System.out.println(calendar1);
		System.out.println(calendar2);

		assertEquals(calendar1, calendar2, Calendar.DAY_OF_MONTH);
		assertEquals(calendar1, calendar2, Calendar.MONTH);
		assertEquals(calendar1, calendar2, Calendar.YEAR);

	}

	/** Make sure the given field of the calendars is the same. */
	private void assertEquals(Calendar calendar1, Calendar calendar2, int field) {
		assertEquals(calendar1.get(field), calendar2.get(field));
	}
}