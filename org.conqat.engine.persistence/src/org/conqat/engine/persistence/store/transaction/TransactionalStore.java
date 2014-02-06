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
package org.conqat.engine.persistence.store.transaction;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.conqat.engine.persistence.store.IKeyValueCallback;
import org.conqat.engine.persistence.store.IStore;
import org.conqat.engine.persistence.store.StorageException;
import org.conqat.engine.persistence.store.base.ByteArrayComparator;
import org.conqat.engine.persistence.store.mem.InMemoryStore;
import org.conqat.engine.persistence.store.util.StorageUtils;
import org.conqat.lib.commons.collections.PairList;

/**
 * Store wrapper that implements a transactional store, i.e. all changes are
 * committed as one change at the end or can be discarded. The changes are
 * managed in two separate stores.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46174 $
 * @ConQAT.Rating GREEN Hash: 6CDDEFCDC65E25E7DED0A0C986239069
 */
public class TransactionalStore implements IStore {

	/** Marker used as value in the deletions store. */
	private static final byte[] DELETION_MARKER = { 1 };

	/** The main store that is modified. */
	private final IStore mainStore;

	/** The store that caches all changes. */
	private final IStore changeStore;

	/** The store that caches all deletions. */
	private final IStore deletionStore;

	/** Constructor using in-memory stores for caching of changes. */
	public TransactionalStore(IStore mainStore) {
		this(mainStore, new InMemoryStore(), new InMemoryStore());
	}

	/** Constructor. */
	public TransactionalStore(IStore mainStore, IStore changeStore,
			IStore deletionStore) {
		this.mainStore = mainStore;
		this.changeStore = changeStore;
		this.deletionStore = deletionStore;
	}

	/** {@inheritDoc} */
	@Override
	public byte[] get(byte[] key) throws StorageException {
		byte[] value = changeStore.get(key);
		if (value != null) {
			return value;
		}

		if (deletionStore.get(key) != null) {
			return null;
		}

		return mainStore.get(key);
	}

	/** {@inheritDoc} */
	@Override
	public List<byte[]> get(List<byte[]> keys) throws StorageException {
		List<byte[]> result = mainStore.get(keys);

		List<byte[]> deleted = deletionStore.get(keys);
		for (int i = 0; i < deleted.size(); ++i) {
			if (deleted.get(i) != null) {
				result.set(i, null);
			}
		}

		List<byte[]> localChanges = changeStore.get(keys);
		for (int i = 0; i < localChanges.size(); ++i) {
			if (localChanges.get(i) != null) {
				result.set(i, localChanges.get(i));
			}
		}

		return result;
	}

	/** {@inheritDoc} */
	@Override
	public void put(byte[] key, byte[] value) throws StorageException {
		deletionStore.remove(key);
		changeStore.put(key, value);
	}

	/** {@inheritDoc} */
	@Override
	public void put(PairList<byte[], byte[]> keysValues)
			throws StorageException {
		deletionStore.remove(keysValues.extractFirstList());
		changeStore.put(keysValues);
	}

	/** {@inheritDoc} */
	@Override
	public void remove(byte[] key) throws StorageException {
		changeStore.remove(key);
		deletionStore.put(key, DELETION_MARKER);
	}

	/** {@inheritDoc} */
	@Override
	public void remove(List<byte[]> keys) throws StorageException {
		changeStore.remove(keys);

		PairList<byte[], byte[]> keysValues = new PairList<byte[], byte[]>();
		for (byte[] key : keys) {
			keysValues.add(key, DELETION_MARKER);
		}
		deletionStore.put(keysValues);
	}

	/** {@inheritDoc} */
	@Override
	public void scan(byte[] beginKey, byte[] endKey, IKeyValueCallback callback)
			throws StorageException {
		Map<byte[], byte[]> entries = new TreeMap<byte[], byte[]>(
				ByteArrayComparator.INSTANCE);
		mainStore.scan(beginKey, endKey, new AppendingStoreCallback(entries));
		deletionStore
				.scan(beginKey, endKey, new DeletingStoreCallback(entries));
		changeStore.scan(beginKey, endKey, new AppendingStoreCallback(entries));
		replayEntries(entries, callback);
	}

	/** Replays all entries in the given map to the callback. */
	private void replayEntries(Map<byte[], byte[]> entries,
			IKeyValueCallback callback) {
		for (Entry<byte[], byte[]> entry : entries.entrySet()) {
			callback.callback(entry.getKey(), entry.getValue());
		}
	}

	/** {@inheritDoc} */
	@Override
	public void scan(byte[] prefix, IKeyValueCallback callback)
			throws StorageException {
		Map<byte[], byte[]> entries = new TreeMap<byte[], byte[]>(
				ByteArrayComparator.INSTANCE);
		mainStore.scan(prefix, new AppendingStoreCallback(entries));
		deletionStore.scan(prefix, new DeletingStoreCallback(entries));
		changeStore.scan(prefix, new AppendingStoreCallback(entries));
		replayEntries(entries, callback);
	}

	/** {@inheritDoc} */
	@Override
	public void scan(List<byte[]> prefixes, IKeyValueCallback callback)
			throws StorageException {
		Map<byte[], byte[]> entries = new TreeMap<byte[], byte[]>(
				ByteArrayComparator.INSTANCE);
		mainStore.scan(prefixes, new AppendingStoreCallback(entries));
		deletionStore.scan(prefixes, new DeletingStoreCallback(entries));
		changeStore.scan(prefixes, new AppendingStoreCallback(entries));
		replayEntries(entries, callback);
	}

	/** {@inheritDoc} */
	@Override
	public void scanKeys(byte[] beginKey, byte[] endKey,
			IKeyValueCallback callback) throws StorageException {
		Map<byte[], byte[]> entries = new TreeMap<byte[], byte[]>(
				ByteArrayComparator.INSTANCE);
		mainStore.scanKeys(beginKey, endKey,
				new AppendingStoreCallback(entries));
		deletionStore.scanKeys(beginKey, endKey, new DeletingStoreCallback(
				entries));
		changeStore.scanKeys(beginKey, endKey, new AppendingStoreCallback(
				entries));
		replayEntries(entries, callback);
	}

	/** {@inheritDoc} */
	@Override
	public void scanKeys(byte[] prefix, IKeyValueCallback callback)
			throws StorageException {
		Map<byte[], byte[]> entries = new TreeMap<byte[], byte[]>(
				ByteArrayComparator.INSTANCE);
		mainStore.scanKeys(prefix, new AppendingStoreCallback(entries));
		deletionStore.scanKeys(prefix, new DeletingStoreCallback(entries));
		changeStore.scanKeys(prefix, new AppendingStoreCallback(entries));
		replayEntries(entries, callback);
	}

	/** Writes all changes into the {@link #mainStore}. */
	public void commit() throws StorageException {
		final PairList<byte[], byte[]> keysValues = new PairList<byte[], byte[]>();
		changeStore.scan(new byte[0], new IKeyValueCallback() {
			@Override
			public void callback(byte[] key, byte[] value) {
				keysValues.add(key, value);
			}
		});

		mainStore.put(keysValues);
		mainStore.remove(StorageUtils.listKeys(deletionStore));

		// delete local changes
		rollback();
	}

	/** Causes this transaction to loose all changes. */
	public void rollback() throws StorageException {
		StorageUtils.clearStore(changeStore);
		StorageUtils.clearStore(deletionStore);
	}

	/**
	 * A callback that is used for a transaction scan on the
	 * {@link TransactionalStore#mainStore} and
	 * {@link TransactionalStore#changeStore} and simply appends entries.
	 */
	private static final class AppendingStoreCallback extends
			TransactionCallbackBase {
		/** Constructor. */
		public AppendingStoreCallback(Map<byte[], byte[]> entries) {
			super(entries);
		}

		/** {@inheritDoc} */
		@Override
		public void callback(byte[] key, byte[] value) {
			entries.put(key, value);
		}
	}

	/**
	 * A callback that is used for a transaction scan on the
	 * {@link TransactionalStore#deletionStore} and deletes entries.
	 */
	private static final class DeletingStoreCallback extends
			TransactionCallbackBase {
		/** Constructor. */
		public DeletingStoreCallback(Map<byte[], byte[]> entries) {
			super(entries);
		}

		/** {@inheritDoc} */
		@Override
		public void callback(byte[] key, byte[] value) {
			entries.remove(key);
		}
	}

	/**
	 * Base class for the callbacks used for resolving scans within a
	 * transaction.
	 */
	private abstract static class TransactionCallbackBase implements
			IKeyValueCallback {

		/** The entries being cached. */
		protected final Map<byte[], byte[]> entries;

		/** Constructor. */
		protected TransactionCallbackBase(Map<byte[], byte[]> entries) {
			this.entries = entries;
		}
	}

}
