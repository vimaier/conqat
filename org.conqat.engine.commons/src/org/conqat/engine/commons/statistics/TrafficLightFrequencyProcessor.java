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

import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.assessment.Assessment;
import org.conqat.lib.commons.assessment.ETrafficLightColor;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 37013 $
 * @ConQAT.Rating GREEN Hash: FAA8F2E50E9EE7B8EB018A2F19813CC4
 */
@AConQATProcessor(description = "This processor creates a KeyedData object where the keys "
		+ "are ETrafficLightColors. By default, for each color, the processor counts the leaves "
		+ "that have an assessment of this color. If the number-key is specified, the "
		+ "processor does not only count but add up the values stored at the number key. This "
		+ "is, e.g., handy to determine the LOC for the leaves assessed red, green and yellow.")
public class TrafficLightFrequencyProcessor extends ValueFrequencyProcessor {
	/**
	 * Checks if the value is either a {@link ETrafficLightColor} or an
	 * {@link Assessment} and returns the color. For all other types an
	 * exception is thrown.
	 */
	@Override
	protected ETrafficLightColor convert(Object value) throws ConQATException {
		if (value instanceof ETrafficLightColor) {
			return (ETrafficLightColor) value;
		}

		if (value instanceof Assessment) {
			return ((Assessment) value).getDominantColor();
		}

		throw new ConQATException("Value " + value
				+ " does not represent a traffic light color.");
	}
}