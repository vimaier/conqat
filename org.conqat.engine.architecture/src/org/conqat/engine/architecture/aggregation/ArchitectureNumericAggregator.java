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
package org.conqat.engine.architecture.aggregation;

import java.util.List;

import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.math.EAggregationStrategy;
import org.conqat.lib.commons.math.MathUtils;

/**
 * {@ConQAT.Doc}
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 0E26DC50261D63D9540B1045889B1186
 */
@AConQATProcessor(description = "This processor aggregates values along the "
		+ "hierarchy defined by the architecture in a bottom-up manner. The"
		+ "aggregation strategy is specified via a parameter. However, not all"
		+ "strategies, e.g. mean, can be applied in a bottom-up manner. ")
public class ArchitectureNumericAggregator extends
		ArchitectureAggregatorBase<Double> {

	/** Aggregation strategy. */
	private EAggregationStrategy strategy = EAggregationStrategy.SUM;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "aggregation", maxOccurrences = 1, description = ""
			+ "Defines aggregation strategy [summation is the default strategy]")
	public void setStrategy(
			@AConQATAttribute(name = "strategy", description = "The strategy") EAggregationStrategy strategy) {
		this.strategy = strategy;
	}

	/** Aggregate with aggregation strategy. */
	@Override
	protected Double aggregate(List<Double> values) throws ConQATException {
		double result = MathUtils.aggregate(values, strategy);
		if (!MathUtils.isNormal(result)) {
			throw new ConQATException(
					"Could not aggregate values. "
							+ "Probably, the aggregation was carried out on an emtpy value list.");
		}
		return result;
	}

	/**
	 * Returns numeric value or <code>null</code> if no value or value of
	 * incorrect type was found.
	 */
	@Override
	protected Double obtainValue(IConQATNode child, String readKey) {
		try {
			return NodeUtils.getDoubleValue(child, readKey);
		} catch (ConQATException e) {
			return null;
		}
	}
}