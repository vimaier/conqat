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
package org.conqat.engine.commons.statistics;

import java.util.Date;
import java.util.Map.Entry;

import org.conqat.engine.commons.util.ConQATInputProcessorBase;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: pfaller $
 * @version $Rev: 37651 $
 * @ConQAT.Rating GREEN Hash: 8477595B35B2FA0B0360B6BBB695217A
 */
@AConQATProcessor(description = "Allows to trim a value series to a provided date range.")
public class ValueSeriesDateTrimmer extends
		ConQATInputProcessorBase<DateValueSeries> {

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "lower", attribute = "date", optional = true, description = ""
			+ "Provides the lower date, i.e. the minimal date that will be preserved. If not given, the lower bound is ignored.")
	public Date lowerDate = new Date(0);

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "upper", attribute = "date", optional = true, description = ""
			+ "Provides the upper date, i.e. the maximal date that will be preserved. If not given, the upper bound is ignored.")
	public Date upperDate = new Date(Long.MAX_VALUE);

	/** {@inheritDoc} */
	@Override
	public DateValueSeries process() {
		DateValueSeries result = new DateValueSeries();
		for (Entry<Date, Double> entry : input.getValues().entrySet()) {
			Date date = entry.getKey();
			if (date.before(lowerDate) || date.after(upperDate)) {
				continue;
			}

			result.addValue(date, entry.getValue());
		}
		return result;
	}
}
