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

import static org.conqat.engine.commons.CommonUtils.DEFAULT_DATE_FORMAT_PATTERN;
import static org.conqat.engine.commons.ConQATParamDoc.DATE_PATTERN_DESC;

import java.util.Calendar;
import java.util.Date;
import java.util.EnumMap;

import org.conqat.engine.commons.CommonUtils;
import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.date.DateUtils;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: pfaller $
 * @version $Rev: 37649 $
 * @ConQAT.Rating GREEN Hash: 5B4341ED1CDAA7F21E053DB996B48087
 */
@AConQATProcessor(description = "This processor creates Date objects. If called "
		+ "without any parameters set, the date is set to the current date. "
		+ "Alternatively the date can be set via a date string. In addition, single "
		+ "fields of the date can be set in relative manner. For example, the date of "
		+ "one week ago can be created by setting no explicit date but setting the "
		+ "DAY_OF_YEAR field to -7 with the 'relative' parameter or to 7 with the "
		+ "'negative-relative' parameter. "
		+ "See class java.util.Calendar for further documentation.")
public class DateDef extends ConQATProcessorBase {

	/** The calendar instanced used. */
	private final Calendar calendar = Calendar.getInstance();

	/** Initialize time. */
	public DateDef() {
		calendar.setTime(DateUtils.getNow());
	}

	/** This maps from fields to values for the absolute values. */
	private final EnumMap<ECalendarField, Integer> absoluteValues = new EnumMap<ECalendarField, Integer>(
			ECalendarField.class);

	/** This maps from fields to values for the relative values. */
	private final EnumMap<ECalendarField, Integer> relativeValues = new EnumMap<ECalendarField, Integer>(
			ECalendarField.class);

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "date-object", minOccurrences = 0, maxOccurrences = 1, description = "Set date as date object. If not specified, "
			+ "the current date is used.")
	public void setDate(
			@AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC) Date date) {
		calendar.setTime(date);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "date", minOccurrences = 0, maxOccurrences = 1, description = "Set date. If not specified, "
			+ "the current date is used.")
	public void setDate(
			@AConQATAttribute(name = "value", description = "Date") String dateString,
			@AConQATAttribute(name = "format", description = DATE_PATTERN_DESC
					+ " [" + DEFAULT_DATE_FORMAT_PATTERN + "]", defaultValue = DEFAULT_DATE_FORMAT_PATTERN) String formatString)
			throws ConQATException {

		calendar.setTime(CommonUtils.parseDate(dateString, formatString));
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "absolute", description = "Set date field with an absolute value.")
	public void setAbsolute(
			@AConQATAttribute(name = "field", description = "The field.") ECalendarField field,
			@AConQATAttribute(name = "value", description = "The value.") int value) {
		absoluteValues.put(field, value);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "relative", description = "Set date field with a relative value (value will be added).")
	public void setRelative(
			@AConQATAttribute(name = "field", description = "The field.") ECalendarField field,
			@AConQATAttribute(name = "value", description = "The value added.") int value) {
		relativeValues.put(field, value);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "negative-relative", description = "Set date field with an relative value (value will be subtracted).")
	public void setNegativeRelative(
			@AConQATAttribute(name = "field", description = "The field.") ECalendarField field,
			@AConQATAttribute(name = "value", description = "The value subtracted.") int value) {
		relativeValues.put(field, -value);
	}

	/** Returns the specified date object. */
	@Override
	public Date process() {

		for (ECalendarField field : absoluteValues.keySet()) {
			field.set(calendar, absoluteValues.get(field));
		}

		for (ECalendarField field : relativeValues.keySet()) {
			field.add(calendar, relativeValues.get(field));
		}

		Date date = calendar.getTime();
		getLogger().debug("Date: " + date);
		return date;
	}

}