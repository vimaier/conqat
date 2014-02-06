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
package org.conqat.engine.commons.aggregation;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.conqat.engine.core.core.AConQATProcessor;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 38201 $
 * @ConQAT.Rating GREEN Hash: 09BFAAAB35717D115F8B4A7769F4BB2C
 */
@AConQATProcessor(description = "Aggregates collections by computing the union. "
		+ "Elements of the sets must support hashing.")
@SuppressWarnings({ "rawtypes", "unchecked" })
public class SetAggregator extends AggregatorBase<Collection, Set> {

	/** Constructor */
	public SetAggregator() {
		super(Collection.class);
	}

	/** {@inheritDoc} */
	@Override
	protected Set aggregate(List<Set> values) {
		Set result = new HashSet();
		for (Set set : values) {
			result.addAll(set);
		}
		return result;
	}

	/** {@inheritDoc} */
	@Override
	protected Set toAggregator(Collection value) {
		if (value instanceof Set) {
			return (Set) value;
		}
		return new HashSet(value);
	}
}
