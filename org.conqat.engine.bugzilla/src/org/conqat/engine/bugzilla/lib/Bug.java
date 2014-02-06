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
package org.conqat.engine.bugzilla.lib;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.UnmodifiableSet;
import org.conqat.lib.commons.string.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.mdimension.jchronic.Chronic;

/**
 * This class represents a Bug stored in Bugzilla. The values of the various
 * Bugzilla fields are stored in a map that maps from {@link EBugzillaField} to
 * the corresponding value. Hence, a Bug is not guaranteed to store all fields.
 * We chose this design to make it possible to iterate over the fields stored in
 * a bug. Moreover, it is more flexible than using dedicated fields with getters
 * and setters as new fields can be added by simply adding a new enumeration
 * element to {@link EBugzillaField}.
 * 
 * @author $Author:deissenb $
 * @version $Revision: 47090 $
 * @ConQAT.Rating RED Hash: 7C10D661D8891093E3B138D6AFC02445
 */
public class Bug {

	/** Bug id. */
	private final int id;

	/** Maps from Bugzilla field to value. */
	private final Map<EBugzillaField, String> fields = new EnumMap<EBugzillaField, String>(
			EBugzillaField.class);

	/** Maps from custom field to value. */
	private final Map<String, String> customFields = new HashMap<String, String>();

	/** Create new Bug. */
	public Bug(int id) {
		this.id = id;
	}

	/** Get bug id. */
	public int getId() {
		return id;
	}

	/** Set a field value. */
	public void setField(EBugzillaField field, String value) {
		fields.put(field, value);
	}

	/** Get field value. Returns <code>null</code> if field is undefined. */
	public String getValue(EBugzillaField field) {
		return fields.get(field);
	}

	/** Get all fields defined for this bug. */
	public UnmodifiableSet<EBugzillaField> getFields() {
		return CollectionUtils.asUnmodifiable(fields.keySet());
	}

	/** Get string representation that includes all field values. */
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();

		result.append("Bug " + id + " [" + StringUtils.CR);
		result.append(StringUtils.toString(fields, "  ") + StringUtils.CR);
		if (!customFields.isEmpty()) {
			result.append(StringUtils.toString(customFields, "  ")
					+ StringUtils.CR);
		}
		result.append("]");

		return result.toString();
	}

	/** Set a custom field value. */
	public void setCustomField(String field, String value) {
		customFields.put(field, value);
	}

	/** Get custom field value. Returns <code>null</code> if field is undefined. */
	public String getCustomFieldValue(String field) {
		return customFields.get(field);
	}

	/** Get names of all custom fields defined for this bug. */
	public UnmodifiableSet<String> getCustomFields() {
		return CollectionUtils.asUnmodifiable(customFields.keySet());
	}

	/** Get milliseconds of an enumeration field that is holding a date. */
	public long getMilliSeconds(EBugzillaField field) {
		// TODO (BH): Why variable here?
		long milliSeconds = 0;

		// TODO (BH): I would invert the condition and return/throw here to
		// reduce the nesting.
		if (fields.get(field) != null) {

			// TODO (BH): Why store value and overwrite in next line? You could
			// also move this outside of the if and use the variable in the if
			// expression.
			String bugzillaDate = StringUtils.EMPTY_STRING;
			bugzillaDate = fields.get(field);

			// TODO (BH): Make constants from these pattern
			Pattern todayPattern = Pattern
					.compile("[0-9]{2}:[0-9]{2}:[0-9]{2}");
			Pattern lastWeekPattern = Pattern
					.compile("[A-Z][a-z][a-z] [0-9]{2}:[0-9]{2}");
			Pattern anyDatePattern = Pattern
					.compile("[0-9]{4}-[0-9]{2}-[0-9]{2}");

			// TODO (BH): Variables only used once. Inline?
			Matcher todayMatcher = todayPattern.matcher(bugzillaDate);
			Matcher lastWeekMatcher = lastWeekPattern.matcher(bugzillaDate);
			Matcher anyDateMatcher = anyDatePattern.matcher(bugzillaDate);

			if (anyDateMatcher.matches()) {

				// TODO (BH): Make this a constant?
				DateTimeFormatter dateTimeFormatter = DateTimeFormat
						.forPattern("yyyy-MM-dd");
				// TODO (BH): Directly return?
				milliSeconds = dateTimeFormatter.parseDateTime(bugzillaDate)
						.getMillis();

			} else if (lastWeekMatcher.matches()) {

				DateTime lastWeekDate = new DateTime(Chronic
						.parse(bugzillaDate).getBeginCalendar().getTime());

				// Since jchronic parses the Bugzilla format exactly seven days
				// to late, we need to subtract those 7 days.
				// TODO (BH): Directly return?
				milliSeconds = lastWeekDate.minusDays(7).getMillis();

			} else if (todayMatcher.matches()) {

				DateTime todayDate = new DateTime();

				// TODO (BH): Make this a constant?
				DateTimeFormatter dateTimeFormatter = DateTimeFormat
						.forPattern("HH:mm:ss");
				DateTime fieldDate = dateTimeFormatter
						.parseDateTime(bugzillaDate);

				// TODO (BH): Directly return?
				milliSeconds = new DateTime(todayDate.getYear(),
						todayDate.getMonthOfYear(), todayDate.getDayOfMonth(),
						fieldDate.getHourOfDay(), fieldDate.getMinuteOfHour(),
						fieldDate.getSecondOfMinute()).getMillis();

			} else {
				// TODO (BH): I think this is not a good way of handling this
				// error as the argument might be valid, but the data is just
				// not good. Better use a checked exception, such as
				// ConQATException.
				throw new IllegalArgumentException(
						"Field is not a Bugzilla date.");
			}

		} else {
			// TODO (BH): I think this is not a good way of handling this error
			// as the argument might be valid, but the data is just not present.
			// Better use a checked exception, such as ConQATException.
			throw new IllegalArgumentException(
					"Argument is not a Bugzilla field.");
		}

		return milliSeconds;
	}
}