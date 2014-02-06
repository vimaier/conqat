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
package org.conqat.engine.commons.assessment;

import org.conqat.lib.commons.assessment.AssessmentUtils;
import org.conqat.lib.commons.assessment.ETrafficLightColor;

/**
 * A special {@link AssessmentRangeDefinition} that is based on
 * {@link ETrafficLightColor}s.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 405D7DA0CCE7FC8771AA29F1608943FC
 */
public class TrafficLightRangeDefinition extends AssessmentRangeDefinition {

	/** The traffic light color associated with this range */
	private ETrafficLightColor trafficLightColor;

	/** Constructor. */
	public TrafficLightRangeDefinition(double boundary, ETrafficLightColor color) {
		super(boundary, AssessmentUtils.getColor(color), color.name());
		this.trafficLightColor = color;
	}

	/** Get the traffic light color associated with this range. */
	public ETrafficLightColor getTrafficLightColor() {
		return trafficLightColor;
	}
}