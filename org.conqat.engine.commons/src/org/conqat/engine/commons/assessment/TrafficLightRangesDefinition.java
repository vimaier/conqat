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

import java.awt.Color;
import java.util.Collection;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.assessment.AssessmentUtils;
import org.conqat.lib.commons.assessment.ETrafficLightColor;
import org.conqat.lib.commons.enums.EnumUtils;

/**
 * A special {@link AssessmentRangesDefinition} that is based on
 * {@link ETrafficLightColor}s.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: AAB088F26F587F67E76D24BCA4818AB7
 */
public class TrafficLightRangesDefinition extends
		AssessmentRangesDefinitionBase<TrafficLightRangeDefinition> {

	/** Constructor.  */
	public TrafficLightRangesDefinition(ETrafficLightColor defaultValue,
			Collection<TrafficLightRangeDefinition> ranges) throws ConQATException {
		super(AssessmentUtils.getColor(defaultValue), defaultValue.name(), ranges);
	}

	/** {@inheritDoc} */
	@Override
	protected TrafficLightRangeDefinition newRangeDefinition(double value,
			Color color, String name) {
		ETrafficLightColor trafficLightColor = EnumUtils.valueOf(
				ETrafficLightColor.class, name);

		CCSMAssert.isNotNull(trafficLightColor);
		CCSMAssert.isTrue(color.equals(AssessmentUtils.getColor(trafficLightColor)),
				"Something strange is happening here. "
						+ "This should never be called with the wrong color.");

		return new TrafficLightRangeDefinition(value, trafficLightColor);
	}

	/** {@inheritDoc} */
	@Override
	public IAssessmentRangesDefinition deepClone() {
		return this;
	}
}
