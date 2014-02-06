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
package org.conqat.engine.persistence.store.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.conqat.engine.persistence.store.IStore;
import org.conqat.lib.commons.collections.ByteArrayWrapper;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Wrapper for a byte[] used as a key in the {@link IStore}. This class is
 * immutable.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 44832 $
 * @ConQAT.Rating GREEN Hash: C3338E4D1D641D5FDB9FEE2C6D6F5742
 */
public class StorageKey extends ByteArrayWrapper {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	/** Constructor. */
	public StorageKey(byte[] key) {
		super(key);
	}

	/** Constructor. */
	public StorageKey(String key) {
		super(StringUtils.stringToBytes(key));
	}

	/** Returns a clone of the key data. */
	public byte[] getKey() {
		return getBytes();
	}

	/** Returns the UTF-8 interpretation of the key. */
	public String getAsString() {
		return StringUtils.bytesToString(array);
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return getAsString();
	}

	/** Converts string keys to {@link StorageKey}s. */
	public static List<StorageKey> convertFromStringKeys(
			Collection<String> paths) {
		List<StorageKey> result = new ArrayList<StorageKey>();
		for (String path : paths) {
			result.add(new StorageKey(path));
		}
		return result;
	}
}