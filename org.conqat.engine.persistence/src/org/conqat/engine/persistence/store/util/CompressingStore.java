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
package org.conqat.engine.persistence.store.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.persistence.store.IKeyValueCallback;
import org.conqat.engine.persistence.store.IStore;
import org.conqat.engine.persistence.store.StorageException;
import org.conqat.lib.commons.collections.PairList;
import org.conqat.lib.commons.io.ByteArrayUtils;

/**
 * A delegating store that transparently compresses the value arrays.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46026 $
 * @ConQAT.Rating GREEN Hash: 6AA18FA3A0F8BA5D6520A78CDD74CFC0
 */
public class CompressingStore implements IStore {

	/** The store to delegate to. */
	private final IStore store;

	/** Constructor. */
	public CompressingStore(IStore store) {
		this.store = store;
	}

	/** {@inheritDoc} */
	@Override
	public byte[] get(byte[] key) throws StorageException {
		try {
			return ByteArrayUtils.decompress(store.get(key));
		} catch (IOException e) {
			throw new StorageException(e);
		}
	}

	/** {@inheritDoc} */
	@Override
	public List<byte[]> get(List<byte[]> keys) throws StorageException {
		try {
			return decompressValues(store.get(keys));
		} catch (IOException e) {
			throw new StorageException(e);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void put(byte[] key, byte[] value) throws StorageException {
		store.put(key, ByteArrayUtils.compress(value));
	}

	/** {@inheritDoc} */
	@Override
	public void put(PairList<byte[], byte[]> keysValues)
			throws StorageException {
		store.put(compressValues(keysValues));
	}

	/** {@inheritDoc} */
	@Override
	public void remove(byte[] key) throws StorageException {
		store.remove(key);
	}

	/** {@inheritDoc} */
	@Override
	public void remove(List<byte[]> keys) throws StorageException {
		store.remove(keys);
	}

	/** {@inheritDoc} */
	@Override
	public void scan(byte[] beginKey, byte[] endKey, IKeyValueCallback callback)
			throws StorageException {
		DecompressingCallback decompressingCallback = new DecompressingCallback(
				callback);
		store.scan(beginKey, endKey, decompressingCallback);
		decompressingCallback.throwCaughtException();
	}

	/** {@inheritDoc} */
	@Override
	public void scan(byte[] prefix, IKeyValueCallback callback)
			throws StorageException {
		DecompressingCallback decompressingCallback = new DecompressingCallback(
				callback);
		store.scan(prefix, decompressingCallback);
		decompressingCallback.throwCaughtException();
	}

	/** {@inheritDoc} */
	@Override
	public void scan(List<byte[]> prefixes, IKeyValueCallback callback)
			throws StorageException {
		DecompressingCallback decompressingCallback = new DecompressingCallback(
				callback);
		store.scan(prefixes, decompressingCallback);
		decompressingCallback.throwCaughtException();
	}

	/** {@inheritDoc} */
	@Override
	public void scanKeys(byte[] beginKey, byte[] endKey,
			IKeyValueCallback callback) throws StorageException {
		DecompressingCallback decompressingCallback = new DecompressingCallback(
				callback);
		store.scanKeys(beginKey, endKey, decompressingCallback);
		decompressingCallback.throwCaughtException();
	}

	/** {@inheritDoc} */
	@Override
	public void scanKeys(byte[] prefix, IKeyValueCallback callback)
			throws StorageException {
		DecompressingCallback decompressingCallback = new DecompressingCallback(
				callback);
		store.scanKeys(prefix, decompressingCallback);
		decompressingCallback.throwCaughtException();
	}

	/**
	 * Returns a new {@link PairList} with the second values compressed using
	 * {@link ByteArrayUtils#compress(byte[])}.
	 */
	private static PairList<byte[], byte[]> compressValues(
			PairList<byte[], byte[]> keysValues) {
		PairList<byte[], byte[]> result = new PairList<byte[], byte[]>(
				keysValues.size());
		for (int i = 0; i < keysValues.size(); ++i) {
			result.add(keysValues.getFirst(i),
					ByteArrayUtils.compress(keysValues.getSecond(i)));
		}
		return result;
	}

	/**
	 * Returns a new {@link List} with the values decompressed using
	 * {@link ByteArrayUtils#decompress(byte[])}.
	 */
	private static List<byte[]> decompressValues(List<byte[]> list)
			throws IOException {
		List<byte[]> result = new ArrayList<byte[]>(list.size());
		for (byte[] value : list) {
			result.add(ByteArrayUtils.decompress(value));
		}
		return result;
	}

	/**
	 * Wrapper for a callback to decompress the values before delegating to the
	 * original callback.
	 */
	private static class DecompressingCallback extends
			ExceptionHandlingKeyValueCallbackBase {

		/** Delegate callback. */
		private final IKeyValueCallback delegate;

		/** Constructor. */
		public DecompressingCallback(IKeyValueCallback callback) {
			delegate = callback;
		}

		/** {@inheritDoc} */
		@Override
		protected void callbackWithException(byte[] key, byte[] value)
				throws StorageException {
			try {
				delegate.callback(key, ByteArrayUtils.decompress(value));
			} catch (IOException e) {
				throw new StorageException(e);
			}
		}
	}
}
