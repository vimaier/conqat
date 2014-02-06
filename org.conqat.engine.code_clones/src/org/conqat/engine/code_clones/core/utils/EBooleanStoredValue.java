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

import org.conqat.engine.code_clones.core.Clone;
import org.conqat.engine.code_clones.core.CloneClass;
import org.conqat.engine.code_clones.core.KeyValueStoreBase;

/**
 * Enumeration of typical boolean values that are stored at clones or clone
 * classes.
 * 
 * @author juergens
 * @author $Author: juergens $
 * @version $Rev: 34670 $
 * @ConQAT.Rating GREEN Hash: B9EAEEA267B5A6E26311072B7C71EF62
 */
public enum EBooleanStoredValue {

	/** Determines whether a tracked clone has an ancestor */
	HAS_ANCESTOR,

	/** Determines whether a tracked clone is a ghost */
	GHOST,

	/** Determines whether a clone fingerprint changed during tracking */
	FINGERPRINT_CHANGED,

	/** Determines whether ancestor of clone was matched fuzzily during tracking */
	FUZZY_MATCH,

	/**
	 * Determines whether the clone position in its file changed during tracking
	 */
	POSITION_CHANGED,

	/** Determines whether tracked clone length is below minimal clone length */
	TOO_SHORT,

	/** Determines whether clone is covered by other clone classes */
	COVERED,

	/** Determines whether tracked clone has successor */
	HAS_SUCCESSOR;

	/** Key string */
	private final String key;

	/** Constructor */
	private EBooleanStoredValue() {
		key = name().toLowerCase();
	}

	/** Set value in store */
	public void setValue(KeyValueStoreBase store, boolean value) {
		store.setValue(key, value);
	}

	/**
	 * @return boolean value stored at store, or <code>false</code>, if none was
	 *         stored.
	 */
	public boolean getValue(KeyValueStoreBase store) {
		if (store.containsValue(key)) {
			return (Boolean) store.getValue(key);
		}
		return false;
	}

	/** Returns true, value is true for at least one clone */
	public boolean trueForAtLeastOneClone(CloneClass cloneClass) {
		for (Clone clone : cloneClass.getClones()) {
			if (getValue(clone)) {
				return true;
			}
		}
		return false;
	}

	/** Returns true, if value is true for all clones */
	public boolean trueForAllClones(CloneClass cloneClass) {
		for (Clone clone : cloneClass.getClones()) {
			if (!getValue(clone)) {
				return false;
			}
		}
		return true;
	}

	/** Returns true, if value is false for all clones */
	public boolean falseForAllClones(CloneClass cloneClass) {
		return !trueForAtLeastOneClone(cloneClass);
	}
}