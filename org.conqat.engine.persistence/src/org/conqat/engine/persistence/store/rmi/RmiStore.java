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
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import org.conqat.engine.persistence.store.IKeyValueCallback;
import org.conqat.engine.persistence.store.IStore;
import org.conqat.engine.persistence.store.StorageException;
import org.conqat.lib.commons.collections.PairList;

/**
 * A simple store based on RMI communication.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46024 $
 * @ConQAT.Rating GREEN Hash: 34B5950A74CF9155C4444D9B66364F50
 */
public class RmiStore implements IStore {

	/** The name of the store. */
	private final String storeName;

	/** The remote store. */
	private IRemoteStore remoteStore;

	/** Constructor. */
	/* package */RmiStore(String store, IRemoteStore remoteStore) {
		this.storeName = store;
		this.remoteStore = remoteStore;
	}

	/** {@inheritDoc} */
	@Override
	public byte[] get(byte[] key) throws StorageException {
		try {
			return remoteStore.get(storeName, key);
		} catch (RemoteException e) {
			throw new StorageException("Remote connection failed!", e);
		}
	}

	/** {@inheritDoc} */
	@Override
	public List<byte[]> get(List<byte[]> keys) throws StorageException {
		try {
			return remoteStore.getAll(storeName, keys);
		} catch (RemoteException e) {
			throw new StorageException("Remote connection failed!", e);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void put(byte[] key, byte[] value) throws StorageException {
		try {
			remoteStore.put(storeName, key, value);
		} catch (RemoteException e) {
			throw new StorageException("Remote connection failed!", e);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void put(PairList<byte[], byte[]> keysValues)
			throws StorageException {
		try {
			remoteStore.putAll(storeName, keysValues);
		} catch (RemoteException e) {
			throw new StorageException("Remote connection failed!", e);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void remove(byte[] key) throws StorageException {
		try {
			remoteStore.remove(storeName, key);
		} catch (RemoteException e) {
			throw new StorageException("Remote connection failed!", e);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void remove(List<byte[]> keys) throws StorageException {
		try {
			remoteStore.removeAll(storeName, keys);
		} catch (RemoteException e) {
			throw new StorageException("Remote connection failed!", e);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void scan(byte[] beginKey, byte[] endKey,
			final IKeyValueCallback callback) throws StorageException {
		try {
			remoteStore.scan(storeName, beginKey, endKey,
					new RemoteKeyValueCallback(callback));
		} catch (RemoteException e) {
			throw new StorageException("Remote connection failed!", e);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void scan(byte[] prefix, final IKeyValueCallback callback)
			throws StorageException {
		try {
			remoteStore.scan(storeName, prefix, new RemoteKeyValueCallback(
					callback));
		} catch (RemoteException e) {
			throw new StorageException("Remote connection failed!", e);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void scan(List<byte[]> prefixes, final IKeyValueCallback callback)
			throws StorageException {
		try {
			remoteStore.scan(storeName, prefixes, new RemoteKeyValueCallback(
					callback));
		} catch (RemoteException e) {
			throw new StorageException("Remote connection failed!", e);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void scanKeys(byte[] beginKey, byte[] endKey,
			final IKeyValueCallback callback) throws StorageException {
		try {
			remoteStore.scanKeys(storeName, beginKey, endKey,
					new RemoteKeyValueCallback(callback));
		} catch (RemoteException e) {
			throw new StorageException("Remote connection failed!", e);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void scanKeys(byte[] prefix, final IKeyValueCallback callback)
			throws StorageException {
		try {
			remoteStore.scanKeys(storeName, prefix, new RemoteKeyValueCallback(
					callback));
		} catch (RemoteException e) {
			throw new StorageException("Remote connection failed!", e);
		}
	}

	/**
	 * Implementation of {@link IRemoteKeyValueCallback}. Needed to extend
	 * {@link UnicastRemoteObject}.
	 */
	private static class RemoteKeyValueCallback extends UnicastRemoteObject
			implements IRemoteKeyValueCallback {

		/** Serial version UID. */
		private static final long serialVersionUID = 1;

		/** The callback to be called. */
		private final IKeyValueCallback callback;

		/** Constructor. */
		private RemoteKeyValueCallback(IKeyValueCallback callback)
				throws RemoteException {
			this.callback = callback;
		}

		/** {@inheritDoc} */
		@Override
		public void callback(byte[] key, byte[] value) {
			callback.callback(key, value);
		}
	}
}