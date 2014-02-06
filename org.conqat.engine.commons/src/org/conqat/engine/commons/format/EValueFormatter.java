/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2011 the ConQAT Project                                   |
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
package org.conqat.engine.commons.format;

import org.conqat.engine.core.core.ConQATException;

/**
 * Enumeration providing default formatters.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 37456 $
 * @ConQAT.Rating YELLOW Hash: 73751F6C45579AF2BF259AB9E700A0BC
 */
public enum EValueFormatter {

	/**
	 * The default formatter. Note: Adjust DEFAULT_STRING constant if you change
	 * this name!
	 */
	DEFAULT(null),

	/** Integral number. */
	INTEGER(new NumberValueFormatter(false, 0, 0)),

	/** Floating point number. */
	DOUBLE(new NumberValueFormatter(false, 0, 10)),

	/** Fixed point number with one fractional digit. */
	FIXED_1(new NumberValueFormatter(false, 1, 1)),

	/** Fixed point number with two fractional digits. */
	FIXED_2(new NumberValueFormatter(false, 2, 2)),

	/** Fixed point number with three fractional digits. */
	FIXED_3(new NumberValueFormatter(false, 3, 3)),

	/** Default percent format using zero or one fractional digits. */
	PERCENT(new NumberValueFormatter(true, 0, 1)),

	/** Percent format without fractional digits. */
	PERCENT_0(new NumberValueFormatter(true, 0, 0)),

	/** Percent format with exactly one fractional digit. */
	PERCENT_1(new NumberValueFormatter(true, 1, 1)),

	/** Percent format with exactly two fractional digits. */
	PERCENT_2(new NumberValueFormatter(true, 2, 2));

	/**
	 * The name of the default formatter to be used in annotations. Thus this
	 * can not be derived from the enum literal but must be a plain string.
	 */
	public static final String DEFAULT_STRING = "DEFAULT";

	/** The formatter used. */
	private final IValueFormatter formatter;

	/** Constructor. */
	private EValueFormatter(IValueFormatter formatter) {
		this.formatter = formatter;
	}

	/** Returns the formatter (may be null for default). */
	public IValueFormatter getFormatter() {
		return formatter;
	}

	/** Applies the formatter. */
	public Object format(Object value) throws ConQATException {
		if (formatter == null) {
			return value;
		}
		return formatter.format(value);
	}
}
