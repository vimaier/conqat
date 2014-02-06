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

import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.lib.commons.assessment.ETrafficLightColor;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: pfaller $
 * @version $Rev: 40407 $
 * @ConQAT.Rating YELLOW Hash: EBFBB613C2D7FA120C1D000207C48044
 */
@AConQATProcessor(description = "This processor determines the most dominant color from the given traffic light color values. If no value is set, UNKONWN is returned.")
public class DominantTrafficLightColorDeterminator extends ConQATProcessorBase {

	/** The most dominant color */
	private ETrafficLightColor dominantColor = ETrafficLightColor.UNKNOWN;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "color", description = "Adds a traffic light color", minOccurrences = 0, maxOccurrences = -1)
	public void addColor(
			@AConQATAttribute(name = "value", description = "Color value") ETrafficLightColor color) {
		dominantColor = ETrafficLightColor.getDominantColor(dominantColor,
				color);
	}

	/** {@inheritDoc} */
	@Override
	public ETrafficLightColor process() {
		return dominantColor;
	}

}
