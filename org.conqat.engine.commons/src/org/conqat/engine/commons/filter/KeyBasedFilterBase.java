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
package org.conqat.engine.commons.filter;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.node.IRemovableConQATNode;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;

/**
 * Base class for filters judging based on the value of some key. The subclass
 * may decide for each node, whether is should be included or not.
 * <p>
 * The class is conservative in that nodes lacking the key or having an invalid
 * value are kept.
 * 
 * @author Benjamin Hummel
 * @author Florian Deissenboeck
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 * @levd.rating GREEN Hash: 3547E4C60E6BD969457612ED5763322C
 * 
 * @param <K>
 *            the type expected when reading the key.
 * @param <N>
 *            the type of node being filtered.
 */
public abstract class KeyBasedFilterBase<K, N extends IRemovableConQATNode>
		extends FilterBase<N> {

	/** The key the value to compare is saved at. */
	private String key;

	/**
	 * Flag that determines whether a node is excluded, if it does not contain a
	 * value for the given key.
	 */
	private boolean filterUnknown = false;

	/**
	 * Set the key to use.
	 * 
	 * @param key
	 *            The key to get the value from that is used for comparsion.
	 */
	@AConQATParameter(name = ConQATParamDoc.READKEY_NAME, minOccurrences = 1, maxOccurrences = 1, description = ConQATParamDoc.READKEY_DESC)
	public void setKey(
			@AConQATAttribute(name = ConQATParamDoc.READKEY_KEY_NAME, description = ConQATParamDoc.READKEY_KEY_DESC) String key) {
		this.key = key;
	}

	/** ConQAT Parameter */
	@AConQATParameter(name = "unknown", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "Handling of nodes for which filter value is unknown")
	public void setFilterUnknown(
			@AConQATAttribute(name = "filter", defaultValue = "false", description = ""
					+ "Flag that determines if such nodes are filtered") boolean filterUnknown) {
		this.filterUnknown = filterUnknown;
	}

	/** Extract the key and perform the test. */
	@Override
	protected boolean isFiltered(N node) {

		Object value = node.getValue(key);
		if (value == null) {
			return filterUnknown;
		}

		try {
			@SuppressWarnings("unchecked")
			K k = (K) value;
			return isFilteredForValue(k);
		} catch (ClassCastException ex) {
			getLogger().warn(
					"Invalid type for key " + key + " at node " + node.getId());
			return false;
		}
	}

	/** Get key. */
	protected String getKey() {
		return key;
	}

	/** Returns whether the file having the provided value should be discarded. */
	protected abstract boolean isFilteredForValue(K value);
}