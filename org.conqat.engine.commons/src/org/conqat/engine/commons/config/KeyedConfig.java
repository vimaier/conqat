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
package org.conqat.engine.commons.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.conqat.lib.commons.clone.IDeepCloneable;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.UnmodifiableCollection;

/**
 * The main transport object used by the key/value based configuration
 * mechanism.
 * <p>
 * This only stores string values. Other types must be converted from the
 * string.
 * <p>
 * We do not implement the map interface, as we only want simple get/set while
 * {@link Map} introduced too many additional methods.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 45115 $
 * @ConQAT.Rating GREEN Hash: FF3E5AA8D27C1403B08A26F34DE272D2
 */
public class KeyedConfig implements IDeepCloneable {

	/** The values for the keys. */
	private final Map<String, String> values = new HashMap<String, String>();

	/** Constructor. */
	public KeyedConfig() {
		// empty but required
	}

	/** Copy constructor. */
	protected KeyedConfig(KeyedConfig other) {
		values.putAll(other.values);
	}

	/** Sets a value (replacing any old value). */
	public void set(String key, String value) {
		values.put(key, value);
	}

	/** Returns the value stored for a key (or null). */
	public String get(String key) {
		return values.get(key);
	}

	/** Returns the keys stored. */
	public UnmodifiableCollection<String> getKeys() {
		return CollectionUtils.asUnmodifiable(values.keySet());
	}
	
	/** Returns all keys that start with the given prefix. */
	public List<String> getKeysWithPrefix(String prefix) {
		List<String> keys = new ArrayList<String>();
		for (String key : getKeys()) {
			if (key.startsWith(prefix)) {
				keys.add(key);
			}
		}
		return keys;
	}

	/** {@inheritDoc} */
	@Override
	public KeyedConfig deepClone() {
		return new KeyedConfig(this);
	}

}
