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

import java.text.NumberFormat;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.assertion.CCSMAssert;

/**
 * Formatter for numeric values.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 41317 $
 * @ConQAT.Rating GREEN Hash: 2AC2590C4A8E22EFE3F62E6ED90FD379
 */
public class NumberValueFormatter implements IValueFormatter {

	/** The format used for numbers. */
	private final NumberFormat numberFormat;

	/**
	 * Constructor.
	 * 
	 * @param percent
	 *            If <code>true</code> the numbers will be formated as a
	 *            percentage, i. e. 1.0 is 100 %.
	 * @param minFractionDigits
	 *            Minimum number of fraction digits used for output.
	 * @param maxFractionDigits
	 *            Maximum number of fraction digits used for output.
	 */
	public NumberValueFormatter(boolean percent, int minFractionDigits,
			int maxFractionDigits) {
		CCSMAssert.isTrue(minFractionDigits <= maxFractionDigits,
				"Min digits must be less than max digits.");

		if (percent) {
			numberFormat = NumberFormat.getPercentInstance();
		} else {
			numberFormat = NumberFormat.getNumberInstance();
		}

		numberFormat.setMinimumFractionDigits(minFractionDigits);
		numberFormat.setMaximumFractionDigits(maxFractionDigits);
	}

	/** {@inheritDoc} */
	@Override
	public Object format(Object value) throws ConQATException {
		if (!(value instanceof Number)) {
			throw new ConQATException("Formatter requires numeric input!");
		}

		double doubleValue = ((Number) value).doubleValue();
		return numberFormat.format(doubleValue);
	}
}
