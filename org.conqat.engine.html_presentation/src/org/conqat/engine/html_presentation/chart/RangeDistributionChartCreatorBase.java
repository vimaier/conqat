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
package org.conqat.engine.html_presentation.chart;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.range_distribution.AssessedRangeDistribution;
import org.conqat.engine.commons.range_distribution.RangeDistribution;
import org.conqat.engine.core.core.AConQATFieldParameter;

/**
 * Base class for layouter that visualize {@link RangeDistribution}s.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 3305749A2D588329E5B2AF51266847D6
 */

public abstract class RangeDistributionChartCreatorBase extends
		ChartCreatorBase {

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "title", attribute = "text", description = "The title displayed at the top of the chart", optional = true)
	public String title;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = ConQATParamDoc.INPUT_NAME, attribute = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC)
	public AssessedRangeDistribution rangeDistribution;

}