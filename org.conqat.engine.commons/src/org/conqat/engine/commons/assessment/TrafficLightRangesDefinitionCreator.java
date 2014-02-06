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
package org.conqat.engine.commons.assessment;

import java.util.ArrayList;
import java.util.Collection;

import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.assessment.ETrafficLightColor;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 64B0A83B906E344BAFE0DE97752938B0
 */
@AConQATProcessor(description = "This processor creates a definition object holding assessment ranges. Ranges and bounds defined here "
		+ "may not overlap.")
public class TrafficLightRangesDefinitionCreator extends ConQATProcessorBase {

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "default", attribute = "color", description = "Default color that is used for ranges "
			+ "for which no color is specified (the range to infinity).")
	public ETrafficLightColor defaultColor;

	/** Assessment ranges. */
	private Collection<TrafficLightRangeDefinition> ranges = new ArrayList<TrafficLightRangeDefinition>();

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "boundary", minOccurrences = 1, description = ""
			+ "The boundary defines the upper value (inclusive) of a range.")
	public void addBoundary(
			@AConQATAttribute(name = "value", description = "Upper range boundary (inclusive)") double boundary,
			@AConQATAttribute(name = "color", description = "Assessment color for this range up to boundary (inclusive)") ETrafficLightColor color) {
		ranges.add(new TrafficLightRangeDefinition(boundary, color));
	}

	/** {@inheritDoc} */
	@Override
	public TrafficLightRangesDefinition process() throws ConQATException {
		return new TrafficLightRangesDefinition(defaultColor, ranges);
	}
}