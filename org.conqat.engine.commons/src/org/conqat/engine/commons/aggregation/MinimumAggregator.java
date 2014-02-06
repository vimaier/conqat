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
 * @ConQAT.Rating GREEN Hash: 248E7EE00CD6F09E7116384BE2B243BD
 */
@SuppressWarnings("rawtypes")
@AConQATProcessor(description = "An aggregator for propagating the minimum value. "
		+ "Any Comparable type (Date, Number, etc.) can be in the keys.")
public class MinimumAggregator extends
		AggregatorBase<Comparable, Comparable<Object>> {

	/** Constructor. */
	public MinimumAggregator() {
		super(Comparable.class);
	}

	/** {@inheritDoc} */
	@Override
	protected Comparable<Object> aggregate(List<Comparable<Object>> values) {
		Comparable<Object> result = values.get(0);
		for (Comparable<Object> value : values) {
			if (result.compareTo(value) > 0) {
				result = value;
			}
		}
		return result;
	}
}