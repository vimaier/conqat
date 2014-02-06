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
package org.conqat.engine.commons.statistics;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.conqat.lib.commons.clone.IDeepCloneable;
import org.conqat.lib.commons.collections.CounterSet;

/**
 * <code>KeyedData</code> objects store a set of values identified by keys.
 * <p>
 * The keys must be comparable as this class is commonly used with a Layouter
 * that uses JFreeChart which expects the data type to be comparable.
 * 
 * @author Florian Deissenboeck
 * @author $Author: hummelb $
 * @version $Rev: 37013 $
 * @ConQAT.Rating GREEN Hash: AA43F8A7F06D71DE270B9B55E176765D
 */
public class KeyedData<T extends Comparable<?>> implements IDeepCloneable {

	/** The stored data. */
	private final HashMap<T, Double> values = new LinkedHashMap<T, Double>();

	/** Create new keyed data object. */
	public KeyedData() {
		// nothing to do
	}

	/** Create new keyed data object from a <code>CounterSet</code>. */
	public KeyedData(CounterSet<T> counter) {
		for (T key : counter.getKeys()) {
			values.put(key, (double) counter.getValue(key));
		}
	}

	/** Copy constructor. Keys are not cloned. */
	private KeyedData(KeyedData<T> series) {
		values.putAll(series.values);
	}

	/** Add a single value. */
	public void add(T key, double value) {
		values.put(key, value);
	}

	/**
	 * Get values. This returns the map this class uses to store the values, so
	 * modifications of the map will be reflected in the <code>KeyedData</code>-object.
	 */
	public Map<T, Double> getValues() {
		return values;
	}

	/** {@inheritDoc} */
	@Override
	public KeyedData<T> deepClone() {
		return new KeyedData<T>(this);
	}
}