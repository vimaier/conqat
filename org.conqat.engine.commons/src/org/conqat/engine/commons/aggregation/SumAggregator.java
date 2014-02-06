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
package org.conqat.engine.commons.aggregation;

import java.util.List;

import org.conqat.engine.core.core.AConQATProcessor;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 40416 $
 * @ConQAT.Rating GREEN Hash: A20F0C885C0C473C73569553D25B389B
 */
@AConQATProcessor(description = "An aggregator for summing up values. "
		+ "This works only for numbers.")
public class SumAggregator extends AggregatorBase<Number, Double> {

	/** Constructor. */
	public SumAggregator() {
		super(Number.class);
	}

	/** {@inheritDoc} */
	@Override
	protected Double aggregate(List<Double> values) {
		double sum = 0;
		for (Double num : values) {
			sum += num;
		}
		return sum;
	}

	/** {@inheritDoc} */
	@Override
	protected Double toAggregator(Number value) {
		return value.doubleValue();
	}
}