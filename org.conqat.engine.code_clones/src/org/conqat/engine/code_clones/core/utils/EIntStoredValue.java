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
package org.conqat.engine.code_clones.core.utils;

import org.conqat.engine.code_clones.core.KeyValueStoreBase;

/**
 * Enumeration of typical integer values that are stored at clones or clone
 * classes.
 * 
 * @author juergens
 * @author $Author: juergens $
 * @version $Rev: 34670 $
 * @ConQAT.Rating GREEN Hash: B1AC490A94629397744718FBE8AD5563
 */
public enum EIntStoredValue {

	/** Start line of clone before edit position propagation */
	ORIGINAL_START_LINE,

	/** Last line of clone before edit position propagation */
	ORIGINAL_LAST_LINE;

	/** Key string */
	private final String key;

	/** Constructor */
	private EIntStoredValue() {
		key = name().toLowerCase();
	}

	/** Set value in store */
	public void setValue(KeyValueStoreBase store, int value) {
		store.setValue(key, value);
	}

	/**
	 * Get boolean value stored at store, or <code>-1</code>, if none was found.
	 */
	public int getValue(KeyValueStoreBase store) {
		if (store.containsValue(key)) {
			return (Integer) store.getValue(key);
		}

		return -1;
	}

}