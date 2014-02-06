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
package org.conqat.engine.commons.range_distribution;

import org.conqat.engine.commons.assessment.AssessmentRange;
import org.conqat.engine.commons.util.ConQATInputProcessorBase;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.assessment.Assessment;
import org.conqat.lib.commons.assessment.ETrafficLightColor;
import org.conqat.lib.commons.enums.EnumUtils;
import org.conqat.lib.commons.math.EAggregationStrategy;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 45279 $
 * @ConQAT.Rating YELLOW Hash: 5288067C60FFC4C0E5C471696A20FDFF
 */
@AConQATProcessor(description = "Converts an assessment range to an assessment based on a size metric used for aggregation.")
public class RangeDistributionToAssessmentConverter extends
		ConQATInputProcessorBase<AssessedRangeDistribution> {

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "size-metric", attribute = "name", description = "The metric used to determine the size of a single element.")
	public String sizeMetric;

	/** {@inheritDoc} */
	@Override
	public Assessment process() throws ConQATException {

		Assessment result = new Assessment();
		for (AssessmentRange range : input.getRanges()) {
			double count = input.aggregate(range, EAggregationStrategy.SUM,
					sizeMetric);
			ETrafficLightColor color = EnumUtils.valueOfIgnoreCase(
					ETrafficLightColor.class, range.getName());
			if (color == null) {
				throw new ConQATException(
						"Unsupported trafficlight color name found in range: "
								+ range.getName());
			}

			result.add(color, (int) Math.round(count));
		}
		return result;
	}
}
