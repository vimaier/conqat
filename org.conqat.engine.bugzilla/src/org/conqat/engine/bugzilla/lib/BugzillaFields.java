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

import org.apache.commons.httpclient.NameValuePair;

import org.conqat.lib.commons.assertion.CCSMPre;

/**
 * This class is an enriched mapping from Bugzilla fields to values.
 * 
 * @author deissenb
 * @author $Author: hummelb $
 * @version $Rev: 46826 $
 * @levd.rating GREEN Hash: 338512D0F851FC996F7F0A3DE458CC24
 * @see EBugzillaField
 */
public class BugzillaFields {

	/** The values. */
	private final EnumMap<EBugzillaField, String> fieldValues = new EnumMap<EBugzillaField, String>(
			EBugzillaField.class);

	/**
	 * Create instance. If parameters are present it must be an even number of
	 * parameters that is alternating sequence of {@link EBugzillaField}s and
	 * objects. For eacth {@link EBugzillaField} the <code>toString()</code>
	 * of the following object is used.
	 */
	public BugzillaFields(Object... fields) {
		CCSMPre.isTrue(fields.length % 2 == 0,
				"Expected even number of arguments");

		for (int i = 0; i < fields.length; i += 2) {
			CCSMPre.isTrue(fields[i] instanceof EBugzillaField,
					"Expected Bugzilla field as parameter " + i
							+ " instead of " + fields[i].getClass());
			setValue((EBugzillaField) fields[i], fields[i + 1].toString());
		}
	}

	/** Check if the specified fields are present, throw an exception otherwise. */
	public void checkFields(EBugzillaField... requiredFields)
			throws BugzillaException {
		for (EBugzillaField field : requiredFields) {
			if (!fieldValues.containsKey(field)) {
				throw new BugzillaException("Field " + field.displayName + " ("
						+ field.xmlTag + ") not specified.");
			}
		}
	}

	/** Copy the fields stored at a bug to this field collection. */
	public void copyFields(Bug bug, EBugzillaField... fieldsToCopy) {
		for (EBugzillaField field : fieldsToCopy) {
			setValue(field, bug.getValue(field));
		}
	}

	/** Copy all fields stored at bug to this field collection. */
	public void copyAllFields(Bug bug) {
		copyFields(bug, bug.getFields().toArray(new EBugzillaField[0]));
	}

	/**
	 * Copy fields that are not already defined in this collection from the
	 * given bug.
	 */
	public void copyNonExistingFields(Bug bug, EBugzillaField... fieldsToCopy) {
		for (EBugzillaField field : fieldsToCopy) {
			setNonExistingValue(field, bug.getValue(field));
		}
	}

	/**
	 * Copy fields stored at a bug to this collection if they are not already
	 * defined by this collection.
	 */
	public void copyAllNonExistingFields(Bug bug) {
		copyNonExistingFields(bug, bug.getFields().toArray(
				new EBugzillaField[0]));
	}

	/**
	 * Create form data to be used with the Apache HTTP client from the field
	 * values.
	 */
	public NameValuePair[] createFormData() {
		NameValuePair[] formData = new NameValuePair[fieldValues.size()];

		int i = 0;

		for (EBugzillaField field : fieldValues.keySet()) {
			formData[i] = new NameValuePair(field.xmlTag, fieldValues
					.get(field));
			i++;
		}

		return formData;
	}

	/** Set value if is not already defined. */
	public void setNonExistingValue(EBugzillaField field, String value) {
		if (!fieldValues.containsKey(field)) {
			setValue(field, value);
		}
	}

	/**
	 * Set (and possibly overwrite) value. If value is set for
	 * {@link EBugzillaField#SUMMARY} the value is automatically set for
	 * {@link EBugzillaField#SHORT_DESCRIPTION} and vice versa.
	 */
	public void setValue(EBugzillaField field, String value) {
		fieldValues.put(field, value);
		if (field == EBugzillaField.SUMMARY) {
			setNonExistingValue(EBugzillaField.SHORT_DESCRIPTION, value);
		}
		if (field == EBugzillaField.SHORT_DESCRIPTION) {
			setNonExistingValue(EBugzillaField.SUMMARY, value);
		}
	}

}