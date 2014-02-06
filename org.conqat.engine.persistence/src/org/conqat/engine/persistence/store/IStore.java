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
package org.conqat.engine.persistence.store;

import java.util.List;

import org.conqat.lib.commons.collections.PairList;

/**
 * This is the interface of a simple key/value store that also supports range
 * and prefix queries.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46008 $
 * @ConQAT.Rating GREEN Hash: DBF83E0073A78EB98E736B9AE797B62B
 */
public interface IStore {

	/** Returns the data stored for the given key (or null if none found). */
	byte[] get(byte[] key) throws StorageException;

	/**
	 * Returns a list that contains for each key provided the data stored for
	 * that key (or null). The length of the returned list has the same size as
	 * the input list.
	 */
	List<byte[]> get(List<byte[]> keys) throws StorageException;

	/**
	 * Inserts a key-value-pair into the store. An existing value for the key
	 * will be replaced. A <code>null</code>-value may not be used for any of
	 * the values).
	 */
	void put(byte[] key, byte[] value) throws StorageException;

	/**
	 * Inserts the given key-value-pairs into the store. An existing value for
	 * the key will be replaced. A <code>null</code>-value may not be used for
	 * any of the values).
	 */
	void put(PairList<byte[], byte[]> keysValues) throws StorageException;

	/** Removes a single element (if it exists). */
	void remove(byte[] key) throws StorageException;

	/** Removes multiple element (if they exist). */
	void remove(List<byte[]> keys) throws StorageException;

	/**
	 * Scans all key/value pairs where the keys starts with the given beginKey
	 * (inclusive if it exists) and ends before the endKey (exclusive). The
	 * begin key may be an empty byte array to indicate an open left boundary.
	 * The end key may be null to indicate an open right boundary. The keys
	 * order is defined by the lexicographic order of the byte arrays.
	 * 
	 * The key/value pairs are reported via the provided callback. The order of
	 * calls is not guaranteed. The callbacks may even be called at the same
	 * time from different threads.
	 */
	void scan(byte[] beginKey, byte[] endKey, IKeyValueCallback callback)
			throws StorageException;

	/**
	 * Scans all key/value pairs where the key starts with the given prefix.
	 * 
	 * The key/value pairs are reported via the provided callback. The order of
	 * calls is not guaranteed. The callbacks may even be called at the same
	 * time from different threads.
	 */
	void scan(byte[] prefix, IKeyValueCallback callback)
			throws StorageException;

	/**
	 * Scans all key/value pairs where the key starts with at least one of the
	 * given prefixes.
	 * 
	 * The key/value pairs are reported via the provided callback. The order of
	 * calls is not guaranteed. The callbacks may even be called at the same
	 * time from different threads.
	 */
	void scan(List<byte[]> prefixes, IKeyValueCallback callback)
			throws StorageException;

	/**
	 * Scans all keys starting from the given beginKey (inclusive if it exists)
	 * and ending before the endKey (exclusive). Any of the keys may be
	 * <code>null</code> to indicate open boundaries. The keys order is defined
	 * by the lexicographic order of the byte arrays.
	 * 
	 * The keys are reported via the provided callback, the values of the
	 * callback are <code>null</code>. The order of calls is not guaranteed. The
	 * callbacks may even be called at the same time from different threads.
	 */
	void scanKeys(byte[] beginKey, byte[] endKey, IKeyValueCallback callback)
			throws StorageException;

	/**
	 * Scans all keys where the key starts with the given prefix.
	 * 
	 * The keys are reported via the provided callback, the values of the
	 * callback are <code>null</code>. The order of calls is not guaranteed. The
	 * callbacks may even be called at the same time from different threads.
	 */
	void scanKeys(byte[] prefix, IKeyValueCallback callback)
			throws StorageException;
}