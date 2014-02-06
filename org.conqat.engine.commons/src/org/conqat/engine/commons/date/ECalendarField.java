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
package org.conqat.engine.commons.date;

import java.util.Calendar;

/**
 * This enum is a wrapper for the fields defined in {@link Calendar} as these
 * are unfortunately defined as int constants.
 * 
 * @author deissenb
 * @author $Author: hummelb $
 * @version $Rev: 37013 $
 * @ConQAT.Rating GREEN Hash: E7BEFFF6B48950D9E1498610E914E54D
 */
public enum ECalendarField {

	/** Calendar field. */
	ERA(Calendar.ERA),

	/** Calendar field. */
	YEAR(Calendar.YEAR),

	/** Calendar field. */
	MONTH(Calendar.MONTH),

	/** Calendar field. */
	WEEK_OF_YEAR(Calendar.WEEK_OF_YEAR),

	/** Calendar field. */
	WEEK_OF_MONTH(Calendar.WEEK_OF_MONTH),

	/** Calendar field. */
	DAY_OF_MONTH(Calendar.DAY_OF_MONTH),

	/** Calendar field. */
	DAY_OF_YEAR(Calendar.DAY_OF_YEAR),

	/** Calendar field. */
	DAY_OF_WEEK(Calendar.DAY_OF_WEEK),

	/** Calendar field. */
	DAY_OF_WEEK_IN_(Calendar.DAY_OF_WEEK_IN_MONTH),

	/** Calendar field. */
	AM_PM(Calendar.AM_PM),

	/** Calendar field. */
	HOUR(Calendar.HOUR),

	/** Calendar field. */
	HOUR_OF_DAY(Calendar.HOUR_OF_DAY),

	/** Calendar field. */
	MINUTE(Calendar.MINUTE),

	/** Calendar field. */
	SECOND(Calendar.SECOND),

	/** Calendar field. */
	MILLISECOND(Calendar.MILLISECOND),

	/** Calendar field. */
	ZONE_OFFSET(Calendar.ZONE_OFFSET),

	/** Calendar field. */
	DST_OFFSET(Calendar.DST_OFFSET);

	/** The corresponding field in {@link Calendar}. */
	private final int field;

	/** Create new field. */
	private ECalendarField(int fieldNum) {
		this.field = fieldNum;
	}

	/**
	 * Set value for this field on the specified calendar instance.
	 * 
	 * @see Calendar#set(int, int)
	 */
	public void set(Calendar calendar, int value) {
		calendar.set(field, value);
	}

	/**
	 * Add value for this field on the specified calendar instance.
	 * 
	 * @see Calendar#add(int, int)
	 */
	public void add(Calendar calendar, int value) {
		calendar.add(field, value);
	}

}