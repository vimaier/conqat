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
 * @author $Author: pfaller $
 * @version $Rev: 37399 $
 * @ConQAT.Rating GREEN Hash: 5C0EF7366D198A18C10531EBD760B3B1
 */
@SuppressWarnings("rawtypes")
@AConQATProcessor(description = "An aggregator for propagating the maximum value. "
		+ "Any Comparable type (Date, Number, etc.) can be in the keys.")
public class MaximumAggregator extends
		AggregatorBase<Comparable, Comparable<Object>> {

	/** Constructor. */
	public MaximumAggregator() {
		super(Comparable.class);
	}

	/** {@inheritDoc} */
	@Override
	protected Comparable<Object> aggregate(List<Comparable<Object>> values) {
		Comparable<Object> result = values.get(0);
		for (Comparable<Object> value : values) {
			if (result.compareTo(value) < 0) {
				result = value;
			}
		}
		return result;
	}
}