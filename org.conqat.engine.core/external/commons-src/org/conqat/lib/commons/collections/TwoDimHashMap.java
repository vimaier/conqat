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
package org.conqat.lib.commons.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A 2-dimensional hash map. Allows storage of items identified by two different
 * keys. This can be used to store the following data structure:
 * <ul>
 * <li>Project A
 * <ul>
 * <li>Dan &mdash; <b>Testing </b></li>
 * <li>Flo &mdash; <b>Documentation </b></li>
 * </ul>
 * </li>
 * <li>Project B
 * <ul>
 * <li>Flo &mdash; <b>Design </b></li>
 * <li>Dan &mdash; <b>QA </b></li>
 * <li>Markus &mdash; <b>CM </b></li>
 * <li>Jorge &mdash; <b>Testing </b></li>
 * </ul>
 * </li>
 * </ul>
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Revision: 41751 $
 * @ConQAT.Rating GREEN Hash: 04D9E6D24A2E5545B4EE6407F35E4953
 */
public class TwoDimHashMap<K1, K2, I> {

	/** The first level map. */
	private final Map<K1, Map<K2, I>> main;

	/** Create a new doubly hashed map. */
	public TwoDimHashMap() {
		main = new HashMap<K1, Map<K2, I>>();
	}

	/** Create a new doubly hashed using the provided map as outer map. */
	public TwoDimHashMap(Map<K1, Map<K2, I>> outerMap) {
		main = outerMap;
	}

	/** Put all values of another TwoDimHashMap into this map. */
	public void putAll(TwoDimHashMap<K1, K2, I> otherMap) {
		for (K1 key1 : otherMap.getFirstKeys()) {
			for (K2 key2 : otherMap.getSecondKeys(key1)) {
				I value = otherMap.getValue(key1, key2);
				putValue(key1, key2, value);
			}
		}
	}

	/**
	 * Put a doubly hashed value. Potentially existing value will be
	 * overwritten.
	 * 
	 * @param key1
	 *            first level key
	 * @param key2
	 *            second level key
	 * @param value
	 *            the value
	 */
	public void putValue(K1 key1, K2 key2, I value) {
		Map<K2, I> map = main.get(key1);
		if (map == null) {
			map = new HashMap<K2, I>();
			main.put(key1, map);
		}
		map.put(key2, value);
	}

	/**
	 * Get a value by specifying first and second level key.
	 * <p>
	 * <i>Examples: </i>
	 * <ul>
	 * <li><code>get("Project A", "Flo") => "Documentation"</code></li>
	 * <li><code>get("Project B", "Dan") => "QA"</code></li>
	 * </ul>
	 * 
	 * @param firstKey
	 *            first level key
	 * @param secondKey
	 *            second level key
	 * @return the value. Is <code>null</code> if first or second level key
	 *         does not exist or if <code>null</code> was explicitly stored.
	 */
	public I getValue(K1 firstKey, K2 secondKey) {
		Map<K2, I> map = main.get(firstKey);
		if (map == null) {
			return null;
		}
		return map.get(secondKey);
	}

	/**
	 * Returns whether the given key combination is available in the map.
	 * <p>
	 * <i>Example: </i>
	 * <ul>
	 * <li><code>containsKey("Project A", "Flo") => true</code></li>
	 * <li><code>containsKey("Project X", "Flo") => false</code></li>
	 * </ul>
	 * 
	 * @param firstKey
	 *            first level key
	 * @param secondKey
	 *            second level key
	 */
	public boolean containsKey(K1 firstKey, K2 secondKey) {
		Map<K2, I> map = main.get(firstKey);
		if (map == null) {
			return false;
		}
		return map.containsKey(secondKey);
	}

	/**
	 * Get all values referenced by a first level key.
	 * <p>
	 * <i>Examples: </i>
	 * <ul>
	 * <li>
	 * <code>getValuesByFirstKey("Project A") => ("Testing", "Documentation")</code>
	 * </li>
	 * </ul>
	 * 
	 * @param firstKey
	 *            the first level key
	 * @return a list of values referenced by the specified first level key
	 */
	public Collection<I> getValuesByFirstKey(K1 firstKey) {
		Map<K2, I> map = main.get(firstKey);
		if (map == null) {
			return null;
		}
		return map.values();

	}

	/**
	 * Get all first level keys. <i>Examples: </i>
	 * <ul>
	 * <li><code>getFirstKeys() => ("Project A", "Project B")</code></li>
	 * </ul>
	 * 
	 * @return all first level keys.
	 */
	public Set<K1> getFirstKeys() {
		return main.keySet();
	}

	/**
	 * Get all the second level keys for a first key. <i>Examples: </i>
	 * <ul>
	 * <li><code>getFirstKeys("Project A") => ("Dan", "Flo")</code></li>
	 * </ul>
	 * 
	 * @param firstKey
	 *            the first level key.
	 * @return all second level keys for a first level key.
	 */
	public Set<K2> getSecondKeys(K1 firstKey) {
		Map<K2, I> map = main.get(firstKey);
		if (map == null) {
			return CollectionUtils.emptySet();
		}
		return map.keySet();
	}

	/**
	 * Get all values referenced by a second level key.
	 * <p>
	 * <i>Examples: </i>
	 * <ul>
	 * <li>
	 * <code>getValuesBySecondKey("Flo") => ("Documentation", "Design")</code>
	 * </li>
	 * </ul>
	 * <b>Note: </b> This method's complexity is linear in the number of first
	 * level keys.
	 * 
	 * @param secondKey
	 *            the second level key
	 * @return a new list of values referenced by the specified second level key
	 */
	public List<I> getValuesBySecondKey(K2 secondKey) {
		ArrayList<I> result = new ArrayList<I>();

		for (Map<K2, I> map : main.values()) {
			if (map.containsKey(secondKey)) {
				result.add(map.get(secondKey));
			}
		}

		return result;
	}

	/**
	 * Get all values stored in the map.
	 * 
	 * @return a new list of all values.
	 */
	public List<I> getValues() {
		ArrayList<I> result = new ArrayList<I>();

		for (Map<K2, I> map : main.values()) {
			result.addAll(map.values());
		}

		return result;
	}

	/**
	 * Get size of the map.
	 * 
	 * @return the number of values stored in this map.
	 */
	public int getSize() {
		int size = 0;
		for (Map<K2, I> map : main.values()) {
			size += map.size();
		}
		return size;
	}

	/**
	 * Check if the map is empty.
	 */
	public boolean isEmpty() {
		return getSize() == 0;
	}

	/**
	 * Get the size of the (second) map stored for a first key.
	 * 
	 * @return the size or 0 if key wasn't found.
	 */
	public int getSecondSize(K1 key1) {
		Map<K2, I> map = main.get(key1);
		if (map == null) {
			return 0;
		}
		return map.size();
	}

	/**
	 * Clear the whole map.
	 * 
	 */
	public void clear() {
		main.clear();
	}

	/**
	 * Removes the value associated to the key combination of key1 and key2.
	 * 
	 * @param key1
	 * @param key2
	 * @return previous value associated with specified key, or null if there
	 *         was no mapping for key. A null return can also indicate that the
	 *         map previously associated null with the specified key.
	 */
	public I remove(K1 key1, K2 key2) {
		Map<K2, I> map = main.get(key1);
		if (map == null) {
			return null;
		}

		if (!map.containsKey(key2)) {
			return null;
		}

		I result = map.remove(key2);

		if (map.isEmpty()) {
			main.remove(key1);
		}

		return result;
	}

	/**
	 * Remove all values specified by first key.
	 * 
	 * @param key
	 *            first level key
	 * @return <code>true</code> if key was present, <code>false</code>
	 *         otherwise
	 */
	public boolean remove(K1 key) {
		Map<K2, I> result = main.remove(key);
		return (result != null);
	}
}