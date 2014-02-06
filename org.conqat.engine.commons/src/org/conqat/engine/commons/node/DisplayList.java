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
package org.conqat.engine.commons.node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.conqat.engine.commons.format.IValueFormatter;
import org.conqat.engine.commons.format.NumberValueFormatter;
import org.conqat.lib.commons.clone.IDeepCloneable;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.UnmodifiableList;

/**
 * A "list" that is used to store the "visible" keys and for lookup of
 * {@link IValueFormatter}s for values of certain keys in {@link IConQATNode}s.
 * This is useful for example for <code>double</code> values that should be
 * shown as a percentage or with a fixed amount of digits.
 * 
 * The DisplayList is normally only attached to the root {@link IConQATNode} and
 * can be retrieved with {@link NodeUtils#getDisplayList(IConQATNode)}.
 * 
 * @see NumberValueFormatter as an example
 * 
 * @author $Author: hummelb $
 * @version $Rev: 41317 $
 * @ConQAT.Rating GREEN Hash: 095DA68A87E02DC087E7B1F152044D2F
 */
public class DisplayList implements IDeepCloneable, Iterable<String> {

	/** The ordered entries of the display list. */
	private LinkedHashMap<String, IValueFormatter> entries = new LinkedHashMap<String, IValueFormatter>();

	/** Constructor. */
	public DisplayList() {
		// empty
	}

	/** Constructor. */
	public DisplayList(DisplayList other) {
		entries.putAll(other.entries);
	}

	/** Returns whether the given key is contained in the list. */
	public boolean containsKey(String key) {
		return entries.containsKey(key);
	}

	/**
	 * Adds a new key to the display list.
	 * 
	 * @param formatter
	 *            may be null.
	 */
	public void addKey(String key, IValueFormatter formatter) {
		entries.put(key, formatter);
	}

	/** Returns the list of keys. */
	public UnmodifiableList<String> getKeyList() {
		return CollectionUtils.asUnmodifiable(new ArrayList<String>(entries
				.keySet()));
	}

	/** {@inheritDoc} */
	@Override
	public IDeepCloneable deepClone() {
		return new DisplayList(this);
	}

	/** Removes the given key. */
	public void removeKey(String key) {
		entries.remove(key);
	}

	/** Removes the given keys. */
	public void removeKeys(Collection<String> keys) {
		for (String key : keys) {
			removeKey(key);
		}
	}

	/** Iterates over the keys. */
	@Override
	public Iterator<String> iterator() {
		return entries.keySet().iterator();
	}

	/** Returns the size of the display list. */
	public int size() {
		return entries.size();
	}

	/**
	 * Adds all entries from another display list. Existing entries are
	 * preserved, i.e. the formatter is not replaced.
	 */
	public void addAll(DisplayList displayList) {
		for (Entry<String, IValueFormatter> entry : displayList.entries
				.entrySet()) {
			if (!containsKey(entry.getKey())) {
				addKey(entry.getKey(), entry.getValue());
			}
		}
	}

	/** Returns the formatter to be used for a specific key (may be null). */
	public IValueFormatter getFormatter(String key) {
		return entries.get(key);
	}
}
