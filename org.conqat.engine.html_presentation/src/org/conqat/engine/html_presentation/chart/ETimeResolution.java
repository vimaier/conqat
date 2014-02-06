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

import java.text.SimpleDateFormat;
import java.util.Date;

import org.jfree.data.time.Day;
import org.jfree.data.time.Hour;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.Minute;
import org.jfree.data.time.Month;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.Second;
import org.jfree.data.time.Week;
import org.jfree.data.time.Year;

/**
 * Enumeration for the different time resolutions supported by the series
 * layouters.
 * 
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: EC979C2441B26519B48F6AA803B4CFC7
 */
public enum ETimeResolution {
	/** Resolution year. */
	YEAR(Year.class, "yyyy"),
	/** Resolution month. */
	MONTH(Month.class, "MMM yyyy"),
	/** Resolution week. */
	WEEK(Week.class, "dd MMM yyyy"),
	/** Resolution day. */
	DAY(Day.class, "dd MMM yyyy"),
	/** Resolution hour. */
	HOUR(Hour.class, "HH:mm"),
	/** Resolution minute. */
	MINUTE(Minute.class, "HH:mm"),
	/** Resolution second. */
	SECOND(Second.class, "HH:mm:ss"),
	/** Resolution week. */
	MILLISECOND(Millisecond.class, "HH:mm:ss:SSSS");

	/** Class object describing the time period. */
	private final Class<? extends RegularTimePeriod> clazz;

	/** Format pattern for range values. See {@link SimpleDateFormat}. */
	private final String domainFormat;

	/**
	 * Create new resolution.
	 */
	private ETimeResolution(Class<? extends RegularTimePeriod> clazz,
			String domainFormat) {
		this.clazz = clazz;
		this.domainFormat = domainFormat;
	}

	/** Get class object describing the time period. */
	/* package */Class<? extends RegularTimePeriod> getTimePeriodClass() {
		return clazz;
	}

	/** Get format pattern for range values. See {@link SimpleDateFormat}. */
	/* package */String getDomainFormat() {
		return domainFormat;
	}

	/** Factory method for time period objects for the resolution element. */
	/* package */RegularTimePeriod createTimePeriod(Date date) {
		switch (this) {
		case YEAR:
			return new Year(date);
		case MONTH:
			return new Month(date);
		case WEEK:
			return new Week(date);
		case DAY:
			return new Day(date);
		case HOUR:
			return new Hour(date);
		case MINUTE:
			return new Minute(date);
		case SECOND:
			return new Second(date);
		case MILLISECOND:
			return new Millisecond(date);

		default:
			throw new IllegalStateException("Unknown resolution element: "
					+ name());
		}

	}
}