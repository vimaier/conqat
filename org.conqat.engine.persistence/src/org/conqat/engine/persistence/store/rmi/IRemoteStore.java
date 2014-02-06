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
package org.conqat.engine.persistence.store.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import org.conqat.engine.persistence.store.IStore;
import org.conqat.engine.persistence.store.StorageException;
import org.conqat.lib.commons.collections.PairList;

/**
 * The interface used for remote communication with the storage system. This is
 * basically the same as {@link IStore}, but all methods also allow to specify
 * the name of the store used.
 * 
 * @author $Author: heineman $
 * @version $Rev: 37938 $
 * @ConQAT.Rating GREEN Hash: 5A6F2B2FFE50D62E91FD14C990D7DE8B
 */
public interface IRemoteStore extends Remote {

	/** Returns the data stored for the given key (or null if none found). */
	byte[] get(String store, byte[] key) throws StorageException,
			RemoteException;

	/** Returns the data stored for the given keys (or null if none found). */
	List<byte[]> getAll(String store, List<byte[]> keys)
			throws StorageException, RemoteException;

	/**
	 * Inserts a key-value-pair into the store. An existing value for the key
	 * will be replaced. A <code>null</code>-value may not be used for any of
	 * the values).
	 */
	void put(String store, byte[] key, byte[] value) throws StorageException,
			RemoteException;

	/**
	 * Inserts key-value-pairs into the store. An existing value for the key
	 * will be replaced. A <code>null</code>-value may not be used for any of
	 * the values).
	 */
	void putAll(String store, PairList<byte[], byte[]> keysValues)
			throws StorageException, RemoteException;

	/** Removes a single element (if it exists). */
	void remove(String store, byte[] key) throws StorageException,
			RemoteException;

	/** Removes multiple elements (if they exist). */
	void removeAll(String store, List<byte[]> keys) throws StorageException,
			RemoteException;

	/**
	 * Scans all key/value pairs where the keys starts with the given beginKey
	 * (inclusive if it exists) and ends before the endKey (exclusive). Any of
	 * the keys may be <code>null</code> to indicate open boundaries. The keys
	 * order is defined by the lexicographic order of the byte arrays.
	 * 
	 * The key/value pairs are reported via the provided callback. The order of
	 * calls is not guaranteed. The callbacks may even be called at the same
	 * time from different threads.
	 */
	void scan(String store, byte[] beginKey, byte[] endKey,
			IRemoteKeyValueCallback callback) throws StorageException,
			RemoteException;

	/**
	 * Scans all key/value pairs where the keys starts with the given prefix.
	 * 
	 * The key/value pairs are reported via the provided callback. The order of
	 * calls is not guaranteed. The callbacks may even be called at the same
	 * time from different threads.
	 */
	void scan(String store, byte[] prefix, IRemoteKeyValueCallback callback)
			throws StorageException, RemoteException;

	/**
	 * Scans all key/value pairs where the keys starts with at least one of the
	 * given prefixes.
	 * 
	 * The key/value pairs are reported via the provided callback. The order of
	 * calls is not guaranteed. The callbacks may even be called at the same
	 * time from different threads.
	 */
	void scan(String store, List<byte[]> prefixes,
			IRemoteKeyValueCallback callback) throws StorageException,
			RemoteException;

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
	void scanKeys(String store, byte[] beginKey, byte[] endKey,
			IRemoteKeyValueCallback callback) throws StorageException,
			RemoteException;

	/**
	 * Scans all keys where the key starts with the given prefix.
	 * 
	 * The keys are reported via the provided callback, the values of the
	 * callback are <code>null</code>. The order of calls is not guaranteed. The
	 * callbacks may even be called at the same time from different threads.
	 */
	void scanKeys(String store, byte[] prefix, IRemoteKeyValueCallback callback)
			throws StorageException, RemoteException;

}