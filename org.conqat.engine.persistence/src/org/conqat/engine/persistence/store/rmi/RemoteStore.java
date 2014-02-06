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

import java.rmi.RemoteException;
import java.util.List;

import org.apache.log4j.Logger;
import org.conqat.engine.persistence.store.IKeyValueCallback;
import org.conqat.engine.persistence.store.IStorageSystem;
import org.conqat.engine.persistence.store.IStore;
import org.conqat.engine.persistence.store.StorageException;
import org.conqat.lib.commons.cache4j.CacheFactory;
import org.conqat.lib.commons.cache4j.ICache;
import org.conqat.lib.commons.collections.PairList;
import org.conqat.lib.commons.factory.IParameterizedFactory;

/**
 * Implementation of the {@link IRemoteStore} interface. This is typically used
 * by starting the {@link RmiStorageServer} and not in a ConQAT context (i.e. as
 * processor). Thus, we use our own logger instead of using the ConQAT logging
 * infrastructure.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46960 $
 * @ConQAT.Rating GREEN Hash: D59E9FF527D31CFC2D0D78EDBD4BA044
 */
public class RemoteStore implements IRemoteStore {

	/** Logger */
	private static final Logger LOGGER = Logger
			.getLogger(RmiStorageServer.class);

	/** The storage system used. */
	private final IStorageSystem storageSystem;

	/** Cache open stores. */
	private final ICache<String, IStore, StorageException> openStores = CacheFactory
			.obtainCache(RemoteStore.class, new StoreFactory());

	/** Constructor. */
	public RemoteStore(IStorageSystem storageSystem) {
		this.storageSystem = storageSystem;
	}

	/** Returns the named store. */
	private synchronized IStore getOrOpenStore(String name)
			throws StorageException {
		return openStores.obtain(name);
	}

	/** {@inheritDoc} */
	@Override
	public synchronized byte[] get(String store, byte[] key)
			throws StorageException {
		return getOrOpenStore(store).get(key);
	}

	/** {@inheritDoc} */
	@Override
	public synchronized List<byte[]> getAll(String store, List<byte[]> keys)
			throws StorageException {
		return getOrOpenStore(store).get(keys);
	}

	/** {@inheritDoc} */
	@Override
	public synchronized void put(String store, byte[] key, byte[] value)
			throws StorageException {
		getOrOpenStore(store).put(key, value);
	}

	/** {@inheritDoc} */
	@Override
	public synchronized void putAll(String store,
			PairList<byte[], byte[]> keysValues) throws StorageException {
		getOrOpenStore(store).put(keysValues);
	}

	/** {@inheritDoc} */
	@Override
	public synchronized void remove(String store, byte[] key)
			throws StorageException {
		getOrOpenStore(store).remove(key);
	}

	/** {@inheritDoc} */
	@Override
	public synchronized void removeAll(String store, List<byte[]> keys)
			throws StorageException {
		getOrOpenStore(store).remove(keys);
	}

	/** {@inheritDoc} */
	@Override
	public synchronized void scan(String store, byte[] beginKey, byte[] endKey,
			final IRemoteKeyValueCallback callback) throws StorageException {
		getOrOpenStore(store).scan(beginKey, endKey,
				new ForwardingCallback(callback));
	}

	/** {@inheritDoc} */
	@Override
	public synchronized void scan(String store, byte[] prefix,
			final IRemoteKeyValueCallback callback) throws StorageException {
		getOrOpenStore(store).scan(prefix, new ForwardingCallback(callback));
	}

	/** {@inheritDoc} */
	@Override
	public synchronized void scan(String store, List<byte[]> prefixes,
			final IRemoteKeyValueCallback callback) throws StorageException {
		getOrOpenStore(store).scan(prefixes, new ForwardingCallback(callback));
	}

	/** {@inheritDoc} */
	@Override
	public synchronized void scanKeys(String store, byte[] beginKey,
			byte[] endKey, final IRemoteKeyValueCallback callback)
			throws StorageException {
		getOrOpenStore(store).scanKeys(beginKey, endKey,
				new ForwardingCallback(callback));
	}

	/** {@inheritDoc} */
	@Override
	public synchronized void scanKeys(String store, byte[] prefix,
			final IRemoteKeyValueCallback callback) throws StorageException {
		getOrOpenStore(store)
				.scanKeys(prefix, new ForwardingCallback(callback));
	}

	/**
	 * Factory used for opening stores in the {@link RemoteStore#openStores}
	 * cache.
	 */
	private final class StoreFactory implements
			IParameterizedFactory<IStore, String, StorageException> {
		/** {@inheritDoc} */
		@Override
		public IStore create(String name) throws StorageException {
			return storageSystem.openStore(name);
		}
	}

	/**
	 * An {@link IKeyValueCallback} that simply forwards all calls to an
	 * {@link IRemoteKeyValueCallback}.
	 */
	private static class ForwardingCallback implements IKeyValueCallback {

		/** The inner callback all calls are forwarded to. */
		private final IRemoteKeyValueCallback inner;

		/** Constructor. */
		private ForwardingCallback(IRemoteKeyValueCallback callback) {
			this.inner = callback;
		}

		/** {@inheritDoc} */
		@Override
		public void callback(byte[] key, byte[] value) {
			try {
				inner.callback(key, value);
			} catch (RemoteException e) {
				LOGGER.error("Error while forwarding results!", e);
			}
		}
	}
}