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
package org.conqat.engine.commons.statistics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.conqat.engine.commons.ConQATPipelineProcessorBase;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.collections.Pair;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: poehlmann $
 * @version $Rev: 41615 $
 * @ConQAT.Rating GREEN Hash: FF860335BFE390CB53DA5EC50FC95EF7
 */
@AConQATProcessor(description = "Sorts keyed data by value.")
public class KeyedDataValueSorter extends
		ConQATPipelineProcessorBase<KeyedData<Comparable<?>>> {

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "sort-ascending", attribute = "value", optional = true, description = "Whether to sort ascending or descending. Default is ascending.")
	public boolean ascending = true;

	/** {@inheritDoc} */
	@Override
	protected void processInput(KeyedData<Comparable<?>> keyedData) {
		Map<Comparable<?>, Double> values = keyedData.getValues();
		List<Pair<Double, Comparable<?>>> sorted = new ArrayList<Pair<Double, Comparable<?>>>();
		for (Entry<Comparable<?>, Double> entry : values.entrySet()) {
			sorted.add(new Pair<Double, Comparable<?>>(entry.getValue(), entry
					.getKey()));
		}

		Collections.sort(sorted);
		if (!ascending) {
			Collections.reverse(sorted);
		}

		// The insertion order is preserved as the KeyedData uses a
		// LinkedHashMap internally
		CCSMAssert.isInstanceOf(values, LinkedHashMap.class);
		values.clear();
		for (Pair<Double, Comparable<?>> pair : sorted) {
			values.put(pair.getSecond(), pair.getFirst());
		}
	}
}
