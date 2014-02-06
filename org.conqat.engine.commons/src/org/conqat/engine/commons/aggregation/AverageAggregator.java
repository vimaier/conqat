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
import org.conqat.lib.commons.collections.Pair;

/**
 * {@ConQAT.Doc}
 * <p>
 * The implementation uses a pair that allows to keep track both of the
 * accumulated value and the number of values. This is required, as the
 * {@link #aggregate(List)} method is called for each inner node (resp. its
 * children) and we do not see whether a single child node represents a single
 * ConQAT node or a larger subtree (which has to be weighted differently).
 * Managing the count as second component of the pair solves this issues.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 40416 $
 * @ConQAT.Rating GREEN Hash: E01515DE576B67D7146F549FCE3E7E32
 */
@AConQATProcessor(description = "An aggregator for calculating the average. "
		+ "This works only for numbers.")
public class AverageAggregator extends
		AggregatorBase<Number, Pair<Double, Integer>> {

	/** Constructor. */
	public AverageAggregator() {
		super(Number.class);
	}

	/** {@inheritDoc} */
	@Override
	protected Pair<Double, Integer> aggregate(List<Pair<Double, Integer>> values) {
		double first = 0;
		int second = 0;
		for (Pair<Double, Integer> value : values) {
			first += value.getFirst();
			second += value.getSecond();
		}
		return new Pair<Double, Integer>(first, second);
	}

	/** {@inheritDoc} */
	@Override
	protected Pair<Double, Integer> toAggregator(Number value) {
		return new Pair<Double, Integer>(value.doubleValue(), 1);
	}

	/** {@inheritDoc} */
	@Override
	protected Number fromAggregator(Pair<Double, Integer> aggregator) {
		return aggregator.getFirst() / aggregator.getSecond();
	}
}