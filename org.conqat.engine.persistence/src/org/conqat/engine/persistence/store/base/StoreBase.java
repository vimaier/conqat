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
package org.conqat.engine.persistence.store.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.conqat.engine.persistence.store.IKeyValueCallback;
import org.conqat.engine.persistence.store.IStore;
import org.conqat.engine.persistence.store.StorageException;
import org.conqat.lib.commons.assertion.CCSMPre;
import org.conqat.lib.commons.collections.ByteArrayWrapper;
import org.conqat.lib.commons.collections.PairList;

/**
 * Base class which contains utility methods for implementing stores.
 * 
 * @author $Author: heineman $
 * @version $Rev: 39793 $
 * @ConQAT.Rating GREEN Hash: F94461802556206765F1C40D4AB2F057
 */
public abstract class StoreBase implements IStore {

	/**
	 * {@inheritDoc}
	 * <p>
	 * This implementation maps prefix queries to range queries. For this, the
	 * prefix is used as the begin key, and a suitable end key is calculated.
	 */
	@Override
	public void scan(byte[] prefix, IKeyValueCallback callback)
			throws StorageException {
		CCSMPre.isNotNull(prefix);
		scan(prefix, generateEndKey(prefix), callback);
	}

	/** {@inheritDoc} */
	@Override
	public void scan(List<byte[]> prefixes, final IKeyValueCallback callback)
			throws StorageException {
		final Set<ByteArrayWrapper> keys = new HashSet<ByteArrayWrapper>();
		for (byte[] prefix : prefixes) {
			scan(prefix, new IKeyValueCallback() {
				@Override
				public void callback(byte[] key, byte[] value) {
					if (keys.add(new ByteArrayWrapper(key))) {
						callback.callback(key, value);
					}
				}
			});
		}
	}

	/** {@inheritDoc} */
	@Override
	public void scanKeys(byte[] prefix, IKeyValueCallback callback)
			throws StorageException {
		scanKeys(prefix, generateEndKey(prefix), callback);
	}

	/** Generates end key */
	private byte[] generateEndKey(byte[] prefix) {
		// The goal of the following lines is the creation of a key that can act
		// as an end key, i.e. is larger than any key with the given prefix, but
		// small enough to skip all other keys. This is done by incrementing the
		// last (lowest) byte in the prefix key. If this last byte is 0xff, then
		// the overflow has to be propagated. In our case, instead of setting
		// overflown bytes to 0, we have to cut them off. This is performed in
		// the next lines, by finding the last non-0xff, incrementing it and
		// removing everything behind it.
		int lastIndex = prefix.length - 1;
		while (lastIndex >= 0 && prefix[lastIndex] == (byte) 0xFF) {
			--lastIndex;
		}

		byte[] end = null;
		if (lastIndex >= 0) {
			end = Arrays.copyOfRange(prefix, 0, lastIndex + 1);
			end[lastIndex] += 1;
		}
		// else: if haven't found an index, we can use null, as we anyway
		// including everything from the prefix to the end.
		return end;
	}

	/** {@inheritDoc} */
	@Override
	public List<byte[]> get(List<byte[]> keys) throws StorageException {
		List<byte[]> values = new ArrayList<byte[]>();
		for (byte[] key : keys) {
			values.add(get(key));
		}
		return values;
	}

	/** {@inheritDoc} */
	@Override
	public void put(PairList<byte[], byte[]> keysValues)
			throws StorageException {
		for (int i = 0; i < keysValues.size(); ++i) {
			put(keysValues.getFirst(i), keysValues.getSecond(i));
		}
	}

	/** {@inheritDoc} */
	@Override
	public void remove(List<byte[]> keys) throws StorageException {
		for (byte[] key : keys) {
			remove(key);
		}
	}
}