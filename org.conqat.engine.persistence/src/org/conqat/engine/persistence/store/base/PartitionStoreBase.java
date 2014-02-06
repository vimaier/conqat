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
package org.conqat.engine.persistence.store.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;
import org.conqat.engine.persistence.store.IKeyValueCallback;
import org.conqat.engine.persistence.store.StorageException;
import org.conqat.engine.persistence.store.TimeoutException;
import org.conqat.lib.commons.collections.PairList;
import org.conqat.lib.commons.concurrent.ThreadUtils;
import org.conqat.lib.commons.io.ByteArrayUtils;
import org.conqat.lib.commons.string.StringUtils;

/**
 * A store that is managed as a partition of a larger store by maintaining a
 * store name used as a prefix for all keys.
 * 
 * This class also handles timeouts of the underlying storage system. If a
 * timeout occurs, the call is retried after a sleep period which incrementally
 * grows up to a certain maximum. This allows to transparently recover from
 * storage system timeouts. Subclasses for concrete storage systems can signal
 * timeouts via {@link TimeoutException}.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 47018 $
 * @ConQAT.Rating GREEN Hash: 312F4A7599127D30C8DCAC34F02906CE
 */
public abstract class PartitionStoreBase extends StoreBase {

	/** The logger. */
	private static final Logger LOGGER = Logger
			.getLogger(PartitionStoreBase.class);

	/** Separator string used to separate the store name from the values. */
	private static final String NAME_SEPARATOR = "$%$";

	/**
	 * Name prefix used for this store. This is padded with the
	 * {@link #NAME_SEPARATOR} to ensure we have no overlap in keys of other
	 * stores.
	 */
	private final byte[] namePrefix;

	/** Initial timeout sleep in milliseconds. */
	private static final long INITIAL_TIMOUT_SLEEP = 10;

	/**
	 * Maximum timeout sleep in milliseconds. Choose a moderate value for the
	 * tradeoff between log message flooding and recover delay. These are around
	 * 40 seconds. (The value is chose like this as we duplicated the sleep
	 * interval on each subsequent timeout).
	 */
	private static final long MAX_TIMOUT_SLEEP = INITIAL_TIMOUT_SLEEP
			* (long) Math.pow(2, 12);

	/** Timeout sleep in milliseconds. */
	private long timeoutSleep = 10;

	/** Constructor. */
	protected PartitionStoreBase(String name) {
		this.namePrefix = StringUtils.stringToBytes(name + NAME_SEPARATOR);
	}

	/** {@inheritDoc} */
	@Override
	public final byte[] get(byte[] key) throws StorageException {
		while (true) {
			try {
				byte[] result = doGet(extendKey(key));
				resetTimeoutSleep();
				return result;
			} catch (TimeoutException e) {
				handleTimeout(e);
			}
		}
	}

	/** Resets the timeout sleep duration to {@link #INITIAL_TIMOUT_SLEEP}. */
	private void resetTimeoutSleep() {
		timeoutSleep = INITIAL_TIMOUT_SLEEP;
	}

	/** Handles a storage system timeout. */
	private void handleTimeout(TimeoutException e) {
		LOGGER.warn("Timeout: " + e.getMessage() + ". Sleeping for "
				+ timeoutSleep + " ms.");
		ThreadUtils.sleep(timeoutSleep);
		if (timeoutSleep < MAX_TIMOUT_SLEEP) {
			timeoutSleep *= 2;
		}
	}

	/** {@inheritDoc} */
	@Override
	public final List<byte[]> get(List<byte[]> keys) throws StorageException {
		while (true) {
			try {
				List<byte[]> result = doBatchGet(extendKeys(keys));
				resetTimeoutSleep();
				return result;
			} catch (TimeoutException e) {
				handleTimeout(e);
			}
		}
	}

	/**
	 * Base implementation of the batch interface. The default just delegates to
	 * {@link #doGet(byte[])}.
	 * 
	 * @param keys
	 *            the keys in this list have already been extended.
	 */
	protected List<byte[]> doBatchGet(List<byte[]> keys)
			throws StorageException {
		List<byte[]> values = new ArrayList<byte[]>();
		for (byte[] key : keys) {
			values.add(doGet(key));
		}
		return values;
	}

	/**
	 * Extends the given keys by prefixing them with {@link #namePrefix} and
	 * returns the new list.
	 */
	private List<byte[]> extendKeys(List<byte[]> keys) {
		List<byte[]> extendedKeys = new ArrayList<byte[]>(keys.size());
		for (byte[] key : keys) {
			extendedKeys.add(extendKey(key));
		}
		return extendedKeys;
	}

	/** Extends the given key by prefixing it with {@link #namePrefix}. */
	private byte[] extendKey(byte[] key) {
		return ByteArrayUtils.concat(namePrefix, key);
	}

	/** {@inheritDoc} */
	@Override
	public final void put(byte[] key, byte[] value) throws StorageException {
		while (true) {
			try {
				doPut(extendKey(key), value);
				resetTimeoutSleep();
				return;
			} catch (TimeoutException e) {
				handleTimeout(e);
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	public final void put(PairList<byte[], byte[]> keysValues)
			throws StorageException {
		PairList<byte[], byte[]> extendedKeysValues = new PairList<byte[], byte[]>(
				keysValues.size());
		for (int i = 0; i < keysValues.size(); ++i) {
			extendedKeysValues.add(
					ByteArrayUtils.concat(namePrefix, keysValues.getFirst(i)),
					keysValues.getSecond(i));
		}

		while (true) {
			try {
				doBatchPut(extendedKeysValues);
				resetTimeoutSleep();
				return;
			} catch (TimeoutException e) {
				handleTimeout(e);
			}
		}
	}

	/**
	 * Base implementation of the batch interface. The default just delegates to
	 * {@link #doPut(byte[], byte[])}.
	 * 
	 * @param keysValues
	 *            the keys in this list have already been extended.
	 */
	protected void doBatchPut(PairList<byte[], byte[]> keysValues)
			throws StorageException {
		for (int i = 0; i < keysValues.size(); ++i) {
			doPut(keysValues.getFirst(i), keysValues.getSecond(i));
		}
	}

	/** {@inheritDoc} */
	@Override
	public final void remove(byte[] key) throws StorageException {
		while (true) {
			try {
				doRemove(extendKey(key));
				resetTimeoutSleep();
				return;
			} catch (TimeoutException e) {
				handleTimeout(e);
			}
		}
	}

	/**
	 * Template method for the underlying implementation of {@link #get(byte[])}
	 * 
	 * @param key
	 *            the extended key (including the namePrefix).
	 */
	protected abstract byte[] doGet(byte[] key) throws StorageException;

	/**
	 * Template method for the underlying implementation of
	 * {@link #put(byte[], byte[])}
	 * 
	 * @param key
	 *            the extended key (including the namePrefix).
	 */
	protected abstract void doPut(byte[] key, byte[] value)
			throws StorageException;

	/**
	 * Template method for the underlying implementation of
	 * {@link #remove(byte[])}
	 * 
	 * @param key
	 *            the extended key (including the namePrefix).
	 */
	protected abstract void doRemove(byte[] key) throws StorageException;

	/** {@inheritDoc} */
	@Override
	public final void remove(List<byte[]> keys) throws StorageException {
		while (true) {
			try {
				doBatchRemove(extendKeys(keys));
				resetTimeoutSleep();
				return;
			} catch (TimeoutException e) {
				handleTimeout(e);
			}
		}
	}

	/**
	 * Base implementation of the batch interface. The default just delegates to
	 * {@link #doRemove(byte[])}.
	 * 
	 * @param keys
	 *            the keys in this list have already been extended.
	 */
	protected void doBatchRemove(List<byte[]> keys) throws StorageException {
		for (byte[] key : keys) {
			doRemove(key);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void scan(byte[] beginKey, byte[] endKey, IKeyValueCallback callback)
			throws StorageException {
		scan(beginKey, endKey, callback, true);
	}

	/** {@inheritDoc} */
	@Override
	public void scanKeys(byte[] beginKey, byte[] endKey,
			IKeyValueCallback callback) throws StorageException {
		scan(beginKey, endKey, callback, false);
	}

	/** Helper method for scanning. */
	private void scan(byte[] beginKey, byte[] endKey,
			final IKeyValueCallback callback, boolean includeValue)
			throws StorageException {

		IKeyValueCallback wrappedCallback = new MultiScanAwareCallbackWrapper(
				callback);

		while (true) {
			try {
				scanInner(beginKey, endKey, wrappedCallback, includeValue);
				resetTimeoutSleep();
				return;
			} catch (TimeoutException e) {
				handleTimeout(e);
			}
		}
	}

	/** Helper method for scanning. */
	private void scanInner(byte[] beginKey, byte[] endKey,
			final IKeyValueCallback callback, boolean includeValue)
			throws StorageException {
		beginKey = extendKey(beginKey);

		if (endKey != null) {
			endKey = extendKey(endKey);
		} else {
			endKey = namePrefix.clone();
			// by construction of the key we know that the array is not empty
			// and the last entry will not overflow
			endKey[endKey.length - 1] += 1;
		}

		doScan(beginKey, endKey, new IKeyValueCallback() {
			@Override
			public void callback(byte[] key, byte[] value) {
				byte[] originalKey = Arrays.copyOfRange(key, namePrefix.length,
						key.length);
				callback.callback(originalKey, value);
			}
		}, includeValue);
	}

	/**
	 * Template method for implementing the scan.
	 * 
	 * @param beginKey
	 *            the first key to include in the scan (will never be null).
	 * @param endKey
	 *            the first key not to include in the scan, i.e. exclusive end
	 *            (will never be null).
	 * @param includeValue
	 *            whether to also pass the value parameter to the provided
	 *            callback or not.
	 */
	protected abstract void doScan(byte[] beginKey, byte[] endKey,
			IKeyValueCallback callback, boolean includeValue)
			throws StorageException;

	/**
	 * Callback wrapper that can handle multiple callbacks for the same key
	 * delivering only one to a delegate callback.
	 */
	private static class MultiScanAwareCallbackWrapper implements
			IKeyValueCallback {

		/** The keys for which we already called the delegate callback. */
		private final Collection<String> keys = new HashSet<String>();

		/** The delegate callback */
		private final IKeyValueCallback delegateCallback;

		/** Constructor */
		public MultiScanAwareCallbackWrapper(IKeyValueCallback delegateCallback) {
			this.delegateCallback = delegateCallback;
		}

		/** {@inheritDoc} */
		@Override
		public void callback(byte[] key, byte[] value) {
			// make sure to call callback only once per key
			if (keys.add(StringUtils.bytesToString(key))) {
				delegateCallback.callback(key, value);
			}
		}
	}
}
