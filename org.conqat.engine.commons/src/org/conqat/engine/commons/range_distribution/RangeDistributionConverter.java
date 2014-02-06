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
package org.conqat.engine.commons.range_distribution;

import java.text.NumberFormat;
import java.util.LinkedHashSet;
import java.util.Set;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.assessment.AssessmentRange;
import org.conqat.engine.commons.format.EValueFormatter;
import org.conqat.engine.commons.node.DisplayList;
import org.conqat.engine.commons.node.NodeConstants;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.node.SetNode;
import org.conqat.engine.commons.util.ConQATInputProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: E9EE72AE6FAFD42F78D07D4115D88422
 */
@AConQATProcessor(description = "This processor converts distribution tables to simple node "
		+ "structures that can be layouted in a table.")
public class RangeDistributionConverter extends
		ConQATInputProcessorBase<RangeDistribution> {

	/** Normal number format. */
	private static final NumberFormat NUMBER_FORMAT = NumberFormat
			.getInstance();

	/** Key used to store color */
	private static final String COLOR = "color";

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "showPercentages", attribute = "value", description = "Turn on/off percentage columns [default is true].", optional = true)
	public boolean showPercentages = true;

	/** Set of secondary metrics. */
	private final Set<String> secondaryMetrics = new LinkedHashSet<String>();

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "metric", minOccurrences = 1, description = ""
			+ "Metric to calculate distribution for.")
	public void addMetric(
			@AConQATAttribute(name = ConQATParamDoc.READKEY_KEY_NAME, description = ConQATParamDoc.READKEY_KEY_DESC) String key) {
		secondaryMetrics.add(key);
	}

	/** {@inheritDoc} */
	@Override
	public SetNode<String> process() {

		SetNode<String> result = new SetNode<String>("root");
		createTableBody(result);
		createSumRow(result);

		DisplayList displayList = NodeUtils.getDisplayList(result);
		for (String key : secondaryMetrics) {
			displayList.addKey(key, EValueFormatter.DOUBLE.getFormatter());
			if (showPercentages) {
				displayList.addKey(getPercentageKey(key),
						EValueFormatter.PERCENT.getFormatter());
			}
		}

		result.setValue(NodeConstants.HIDE_ROOT, true);

		// we want to retain order of addition
		result.setValue(NodeConstants.COMPARATOR, null);
		return result;
	}

	/** Create the table body. */
	private void createTableBody(SetNode<String> result) {
		for (AssessmentRange range : input.getRanges()) {

			SetNode<String> child = new SetNode<String>(
					range.format(NUMBER_FORMAT));
			child.setValue(COLOR, range.getColor());

			for (String key : secondaryMetrics) {
				child.setValue(key, input.getSum(range, key));

				if (showPercentages && input.getTotal(key) != 0) {
					child.setValue(getPercentageKey(key),
							input.getPercentage(range, key));
				}
			}

			result.addChild(child);
		}
	}

	/** Create the bottom row that contains the sums. */
	private void createSumRow(SetNode<String> result) {
		SetNode<String> child = new SetNode<String>("Sum");
		result.addChild(child);

		for (String key : secondaryMetrics) {
			child.setValue(key, input.getTotal(key));

			if (showPercentages && input.getTotal(key) != 0) {
				child.setValue(getPercentageKey(key), 1);
			}
		}
	}

	/**
	 * Determine key to be used for the percentage display of the given key.
	 * This ensure that there are no overlaps between the originally specified
	 * keys and the keys used for percentages.
	 */
	private String getPercentageKey(String key) {
		key = "%" + key;
		while (secondaryMetrics.contains(key)) {
			key += "_";
		}
		return key;
	}
}