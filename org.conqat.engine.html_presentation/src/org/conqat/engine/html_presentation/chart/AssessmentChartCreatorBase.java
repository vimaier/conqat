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

import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.ConQATException;

/**
 * Base class for processor that create charts that visualize a single value
 * w.r.t. to assessment ranges.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 230A2E8BF9C0DE8E7CA00D6BEFAE6E81
 */
public abstract class AssessmentChartCreatorBase extends ChartCreatorBase {

	/** Value to visualize */
	protected double value;

	/** Unit of the chart */
	protected String unit;

	/** Upper bound of the green range */
	protected double greenThreshold;

	/** Upper bound of the yellow range */
	protected double yellowThreshold;

	/** Upper bound of the red range */
	protected double redThreshold;

	/** Chart title. */
	protected String title;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "data", minOccurrences = 1, maxOccurrences = 1, description = "Content")
	public void setValue(
			@AConQATAttribute(name = "ref", description = "Node in which value is stored") IConQATNode node,
			@AConQATAttribute(name = "key", description = "Key that holds the value") String key,
			@AConQATAttribute(name = "unit", description = "Name of the unit") String unitName)
			throws ConQATException {
		value = NodeUtils.getDoubleValue(node, key);
		unit = unitName;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "ranges", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "The upper bounds separating the ranges of the three sections of the meterplot.")
	public void setRange(
			@AConQATAttribute(name = "greenUpperBound", description = "Upper bound for green range.") double greenThreshold,
			@AConQATAttribute(name = "yellowUpperBound", description = "Upper bound for yellow range.") double yellowThreshold,
			@AConQATAttribute(name = "redUpperBound", description = "Upper bound for red range.") double redThreshold)
			throws ConQATException {
		if (yellowThreshold <= greenThreshold
				|| redThreshold <= yellowThreshold) {
			throw new ConQATException(
					"Ranges invalid. Required Sequence: green-yellow-red");
		}
		this.greenThreshold = greenThreshold;
		this.yellowThreshold = yellowThreshold;
		this.redThreshold = redThreshold;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "title", minOccurrences = 1, maxOccurrences = 1, description = "The title displayed at the top of the chart")
	public void setDescription(
			@AConQATAttribute(name = "title", description = "Title text.") String title) {
		this.title = title;
	}

}