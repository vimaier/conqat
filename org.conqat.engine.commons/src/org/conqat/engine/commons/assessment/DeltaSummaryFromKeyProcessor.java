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

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.ConQATPipelineProcessorBase;
import org.conqat.engine.commons.format.DeltaSummary;
import org.conqat.engine.commons.format.EValueFormatter;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeConstants;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.lib.commons.assessment.ETrafficLightColor;

/**
 * {ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 40858 $
 * @ConQAT.Rating GREEN Hash: 5EC8DF8AA7F866D6BCF8C9B36EA43D0D
 */
@AConQATProcessor(description = "Produces a delta summary for the value stored "
		+ "under the given key. The value is compared to the value extracted "
		+ "from the given baseline's root (the same key is used). The summary "
		+ "depends on the rating parameter that defines how the delta between both "
		+ "values is interpreted.")
public class DeltaSummaryFromKeyProcessor extends
		ConQATPipelineProcessorBase<IConQATNode> {

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "baseline", attribute = ConQATParamDoc.INPUT_REF_NAME, description = "The baseline scope which should be used to create the delta summary.")
	public IConQATNode baseline;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "rating", attribute = "value", description = "Interpretation of the delta between the baseline and the input scope.")
	public EDeltaRating rating;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = ConQATParamDoc.READKEY_NAME, attribute = ConQATParamDoc.READKEY_KEY_NAME, description = ConQATParamDoc.READKEY_DESC)
	public String key;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "formatter", attribute = "value", optional = true, description = "Formatter used to format the summary values.")
	public EValueFormatter formatter = EValueFormatter.DEFAULT;

	/** {@inheritDoc} */
	@Override
	protected void processInput(IConQATNode input) {
		Double value = getValue(input);
		Double baselineValue = getValue(baseline);

		if (value == null || baselineValue == null) {
			// The problem has already been logged in getValue(), so we can
			// silently return here.
			return;
		}
		
		DeltaSummary summary = new DeltaSummary(baselineValue, value,
				formatter, getColor(baselineValue, value));
		input.setValue(NodeConstants.SUMMARY, summary);
	}

	/**
	 * Gets the value from the given node or null if there is no value stored
	 * for the given key or the value is not a number.
	 */
	private Double getValue(IConQATNode node) {
		Object value = node.getValue(key);

		if (value instanceof Number) {
			return ((Number) value).doubleValue();
		} 
		
		if (value == null) {
			getLogger().error(
					"No value found at root node for key " + key + ".");
		} else {
			getLogger().error(
					"No suitable value found at root node for key " + key
							+ ". Value was of type " + value.getClass());
		}
		return null;
	}

	/**
	 * Gets the rating color for the given baseline and current value. The color
	 * depends on the values as well as delta rating mode.
	 */
	private ETrafficLightColor getColor(double baselineValue, double value) {
		if (rating == EDeltaRating.NONE || baselineValue == value) {
			return ETrafficLightColor.BASELINE;
		}

		if (value > baselineValue) {
			if (rating == EDeltaRating.HIGHER_IS_BETTER) {
				return ETrafficLightColor.GREEN;
			}
			return ETrafficLightColor.RED;
		}

		if (rating == EDeltaRating.HIGHER_IS_BETTER) {
			return ETrafficLightColor.RED;
		}
		return ETrafficLightColor.GREEN;
	}
}
