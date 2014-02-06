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
import java.util.List;

import org.conqat.engine.persistence.store.IKeyValueCallback;
import org.conqat.engine.persistence.store.IStore;
import org.conqat.engine.persistence.store.StorageException;
import org.conqat.lib.commons.collections.PairList;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Wrapper class that adds convenience methods to an {@link IStore}.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46027 $
 * @ConQAT.Rating GREEN Hash: 7C489EB3543E43D806C07ABB8BCD85D9
 */
public class ConvenientStore implements IStore {

	/** The store to delegate to. */
	private final IStore store;

	/** Constructor. */
	public ConvenientStore(IStore store) {
		this.store = store;
	}

	/** {@inheritDoc} */
	@Override
	public byte[] get(byte[] key) throws StorageException {
		return store.get(key);
	}

	/**
	 * Returns the entry stored for the given string key (or <code>null</code>
	 * if none is found).
	 */
	public byte[] getWithString(String key) throws StorageException {
		return store.get(StringUtils.stringToBytes(key));
	}

	/** {@inheritDoc} */
	@Override
	public List<byte[]> get(List<byte[]> keys) throws StorageException {
		return store.get(keys);
	}

	/**
	 * Returns the entries stored for the given string keys (including
	 * <code>null</code> entries for non-existing keys).
	 */
	public List<byte[]> getWithStrings(List<String> keys)
			throws StorageException {
		return store.get(convertKeys(keys));
	}

	/** Converts the list of string keys to byte[] keys. */
	private List<byte[]> convertKeys(List<String> keys) {
		List<byte[]> byteKeys = new ArrayList<byte[]>();
		for (String key : keys) {
			byteKeys.add(StringUtils.stringToBytes(key));
		}
		return byteKeys;
	}

	/** {@inheritDoc} */
	@Override
	public void put(byte[] key, byte[] value) throws StorageException {
		store.put(key, value);
	}

	/** Stores data for the given key. */
	public void putWithString(String key, byte[] value) throws StorageException {
		store.put(StringUtils.stringToBytes(key), value);
	}

	/** {@inheritDoc} */
	@Override
	public void put(PairList<byte[], byte[]> keysValues)
			throws StorageException {
		store.put(keysValues);
	}

	/** Stores data for the given keys. */
	public void putWithStrings(PairList<String, byte[]> keysValues)
			throws StorageException {
		PairList<byte[], byte[]> byteKeysValues = new PairList<byte[], byte[]>(
				keysValues.size());
		for (int i = 0; i < keysValues.size(); ++i) {
			byteKeysValues.add(
					StringUtils.stringToBytes(keysValues.getFirst(i)),
					keysValues.getSecond(i));
		}

		store.put(byteKeysValues);
	}

	/** {@inheritDoc} */
	@Override
	public void remove(byte[] key) throws StorageException {
		store.remove(key);
	}

	/** Removes the entry stored for the given string key. */
	public void removeWithString(String key) throws StorageException {
		store.remove(StringUtils.stringToBytes(key));
	}

	/** {@inheritDoc} */
	@Override
	public void remove(List<byte[]> keys) throws StorageException {
		store.remove(keys);
	}

	/** Removes the entries stored for the given string keys. */
	public void removeWithStrings(List<String> keys) throws StorageException {
		store.remove(convertKeys(keys));
	}

	/** {@inheritDoc} */
	@Override
	public void scan(byte[] beginKey, byte[] endKey, IKeyValueCallback callback)
			throws StorageException {
		store.scan(beginKey, endKey, callback);
	}

	/** {@inheritDoc} */
	@Override
	public void scan(byte[] prefix, IKeyValueCallback callback)
			throws StorageException {
		store.scan(prefix, callback);
	}

	/** Scans all entries for the given prefix. */
	public void scan(String prefix, IKeyValueCallback callback)
			throws StorageException {
		store.scan(StringUtils.stringToBytes(prefix), callback);
	}

	/** {@inheritDoc} */
	@Override
	public void scan(List<byte[]> prefixes, IKeyValueCallback callback)
			throws StorageException {
		store.scan(prefixes, callback);
	}

	/** {@inheritDoc} */
	@Override
	public void scanKeys(byte[] beginKey, byte[] endKey,
			IKeyValueCallback callback) throws StorageException {
		store.scanKeys(beginKey, endKey, callback);
	}

	/** {@inheritDoc} */
	@Override
	public void scanKeys(byte[] prefix, IKeyValueCallback callback)
			throws StorageException {
		store.scanKeys(prefix, callback);
	}
}