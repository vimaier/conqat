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
package org.conqat.engine.persistence.store.hist;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.conqat.engine.persistence.rollback.IRollbackableIndex;
import org.conqat.engine.persistence.store.IStore;
import org.conqat.engine.persistence.store.StorageException;
import org.conqat.engine.persistence.store.util.ExceptionHandlingKeyValueCallbackBase;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.collections.ByteArrayWrapper;
import org.conqat.lib.commons.collections.PairList;
import org.conqat.lib.commons.io.ByteArrayUtils;

/**
 * Historizing store that supports rollback.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46932 $
 * @ConQAT.Rating GREEN Hash: C36DF62022A2EEA40005ABE8108F296E
 */
public class RollbackableHistorizingStore extends HeadReadOnlyHistorizingStore
		implements IRollbackableIndex {

	/**
	 * Maximal number of keys to process at the same time during HEAD recovery.
	 * This limit is required to avoid out of memory for large values.
	 */
	private static final int BATCH_SIZE = 100;

	/** Constructor. */
	public RollbackableHistorizingStore(IStore delegate) {
		super(delegate);
	}

	/** {@inheritDoc} */
	@Override
	public void performRollback(final long timestamp) throws StorageException {
		RollbackCallback callback = new RollbackCallback(timestamp);
		store.scanKeys(new byte[0], callback);
		callback.persistResults();
	}

	/** Extracts the timestamp part of a key. */
	private static long getKeyTimestamp(byte[] key) throws StorageException {
		try {
			return ByteArrayUtils.byteArrayToLong(Arrays.copyOfRange(key,
					key.length - ByteArrayUtils.LONG_BYTE_ARRAY_LENGTH,
					key.length));
		} catch (IOException e) {
			throw new StorageException(e);
		}
	}

	/** Callback used for performing a rollback. */
	private class RollbackCallback extends
			ExceptionHandlingKeyValueCallbackBase {

		/** The largest timestamp that may be preserved in the store. */
		private final long timestamp;

		/** Keys that should be deleted. */
		private final List<byte[]> toDelete = new ArrayList<byte[]>();

		/** The latest change for each key. */
		private final Map<ByteArrayWrapper, Long> latestChange = new HashMap<ByteArrayWrapper, Long>();

		/** The list of all keys in the head view. */
		private final List<byte[]> headKeys = new ArrayList<byte[]>();

		/** Constructor. */
		public RollbackCallback(long timestamp) {
			this.timestamp = timestamp;
		}

		/** {@inheritDoc} */
		@Override
		protected void callbackWithException(byte[] key, byte[] value)
				throws StorageException {
			if (ByteArrayUtils.isPrefix(HEAD_PREFIX, key)) {
				headKeys.add(stripHeadPrefix(key));
				return;
			}

			// extract original key without timestamp suffix
			ByteArrayWrapper originalKey = new ByteArrayWrapper(
					Arrays.copyOf(key, key.length - 1
							- ByteArrayUtils.LONG_BYTE_ARRAY_LENGTH));

			long keyTimestamp = getKeyTimestamp(key);
			if (keyTimestamp > timestamp) {
				toDelete.add(key);
			} else {
				Long latest = latestChange.get(originalKey);
				if (latest == null || keyTimestamp > latest) {
					latestChange.put(originalKey, keyTimestamp);
				}
			}
		}

		/** Persists the results of the rollback operation. */
		public void persistResults() throws StorageException {
			throwCaughtException();

			for (byte[] key : headKeys) {
				if (!latestChange.containsKey(new ByteArrayWrapper(key))) {
					toDelete.add(headKey(key));
				}
			}

			List<ByteArrayWrapper> allKeys = new ArrayList<ByteArrayWrapper>(
					latestChange.keySet());
			for (int i = 0; i < allKeys.size(); i += BATCH_SIZE) {
				List<ByteArrayWrapper> keys = allKeys.subList(i,
						Math.min(i + BATCH_SIZE, allKeys.size()));
				refreshHead(keys, latestChange, toDelete);
			}

			store.remove(toDelete);
		}

		/** Refreshes the head view for the given keys. */
		private void refreshHead(List<ByteArrayWrapper> keys,
				final Map<ByteArrayWrapper, Long> latestChange,
				final List<byte[]> toDelete) throws StorageException {
			List<byte[]> rawKeys = new ArrayList<byte[]>();
			for (ByteArrayWrapper key : keys) {
				rawKeys.add(revisionKey(key.getBytes(),
						ByteArrayUtils.longToByteArray(latestChange.get(key))));
			}
			List<byte[]> values = store.get(rawKeys);
			PairList<byte[], byte[]> putValues = new PairList<byte[], byte[]>();
			for (int i = 0; i < keys.size(); ++i) {
				byte[] value = values.get(i);
				CCSMAssert.isNotNull(value);
				if (isDeletionValue(value)) {
					toDelete.add(headKey(keys.get(i).getBytes()));
				} else {
					putValues.add(headKey(keys.get(i).getBytes()), value);
				}
			}
			store.put(putValues);
		}
	}
}
