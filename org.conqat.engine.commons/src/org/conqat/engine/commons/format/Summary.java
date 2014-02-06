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
import org.conqat.lib.commons.assertion.CCSMPre;

/**
 * This class describes a formatted summary, i.e. an arbitrary value plus a
 * formatter.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 9B8E726239C3F994F65E47B4662684FB
 */
public class Summary {

	/** The value. */
	private final Object value;

	/** The formatter. */
	protected final EValueFormatter formatter;

	/** Create summary with default formatter. */
	public Summary(Object value) {
		this(value, EValueFormatter.DEFAULT);
	}

	/** Constructor. */
	public Summary(Object value, EValueFormatter formatter) {
		CCSMPre.isNotNull(formatter);
		this.value = value;
		this.formatter = formatter;
	}

	/** Format the summary. */
	public String format() {
		try {
			return String.valueOf(formatter.format(value));
		} catch (ConQATException e) {
			return String.valueOf(value);
		}
	}

	/** Get the value. */
	public Object getValue() {
		return value;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return format();
	}
}
