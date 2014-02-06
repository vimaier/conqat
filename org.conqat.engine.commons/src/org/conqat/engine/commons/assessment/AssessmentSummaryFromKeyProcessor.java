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
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeConstants;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.lib.commons.assessment.Assessment;
import org.conqat.lib.commons.assessment.ETrafficLightColor;

/**
 * {ConQAT.Doc}
 * 
 * @author $Author: juergens $
 * @version $Rev: 37653 $
 * @ConQAT.Rating GREEN Hash: 657D253E13A080EED20BF755BE1C51E0
 */
@AConQATProcessor(description = "This processor creates an assessment summary from a value stored at a key of the root node. "
		+ "The value must be either an assessment or a numeric value. "
		+ "In the case of a numberic value, it is interpreted as the fraction of RED assessments.")
public class AssessmentSummaryFromKeyProcessor extends
		ConQATPipelineProcessorBase<IConQATNode> {

	/**
	 * The factor used for scaling percent values to the assessment frequencies
	 * (which are ints). The value of 1000 should provide sufficient precision
	 * while not causing any risks because of long loops or overflow when
	 * working with the resulting assessment.
	 */
	private static final int ASSESSMENT_SCALING_FACTOR = 1000;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = ConQATParamDoc.READKEY_NAME, attribute = ConQATParamDoc.READKEY_KEY_NAME, description = ConQATParamDoc.READKEY_DESC)
	public String key;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "number-is-red-fraction", attribute = "value", optional = true, description = ""
			+ "If this is true (default) a number found at the key is interpreted as the RED part of the assessment, "
			+ "otherwise it is interpreted as the GREEN part.")
	public boolean numberIsRedFraction = true;

	/** {@inheritDoc} */
	@Override
	protected void processInput(IConQATNode input) {
		Object value = input.getValue(key);

		if (value instanceof Assessment) {
			input.setValue(NodeConstants.SUMMARY,
					((Assessment) value).deepClone());
		} else if (value instanceof Number) {
			summaryFromNumber(input, ((Number) value).doubleValue());
		} else if (value == null) {
			getLogger().error(
					"No value found at root node for key " + key + ".");
		} else {
			getLogger().error(
					"No suitable value found at root node for key " + key
							+ ". Value was of type " + value.getClass());
		}
	}

	/** Sets the summary from a number. */
	private void summaryFromNumber(IConQATNode input, double value) {
		if (value < 0 || value > 1) {
			// We just log the problem instead of throwing an exception, as
			// throwing an exception would also stop all following processors
			// and render a dashboard probably useless. Only logging an error
			// allows the dashboard to continue with only the assessment summary
			// missing.
			getLogger().error(
					"Value found at root node for key " + key
							+ " was outside of range [0,1].");
			return;
		}

		if (!numberIsRedFraction) {
			value = 1 - value;
		}

		Assessment summary = new Assessment();
		int redCount = (int) (value * ASSESSMENT_SCALING_FACTOR);
		summary.add(ETrafficLightColor.RED, redCount);
		summary.add(ETrafficLightColor.GREEN, ASSESSMENT_SCALING_FACTOR
				- redCount);

		input.setValue(NodeConstants.SUMMARY, summary);
	}
}
