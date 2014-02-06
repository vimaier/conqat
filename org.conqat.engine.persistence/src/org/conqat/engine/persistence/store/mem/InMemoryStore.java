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
package org.conqat.engine.persistence.store.mem;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.conqat.engine.persistence.store.IKeyValueCallback;
import org.conqat.engine.persistence.store.StorageException;
import org.conqat.engine.persistence.store.base.ByteArrayComparator;
import org.conqat.engine.persistence.store.base.StoreBase;
import org.conqat.lib.commons.filesystem.FileSystemUtils;

/**
 * Store implementation that keeps all data in main memory. Very simple
 * persistence is possible, but all data must fit into main memory.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46021 $
 * @ConQAT.Rating GREEN Hash: A9D32638E789AF9C54FB2535AA8924F3
 */
public class InMemoryStore extends StoreBase {

	/** Data store. */
	private final NavigableMap<byte[], byte[]> data = new TreeMap<byte[], byte[]>(
			ByteArrayComparator.INSTANCE);

	/** {@inheritDoc} */
	@Override
	public synchronized byte[] get(byte[] key) {
		byte[] value = data.get(key);
		if (value == null) {
			return null;
		}
		return value.clone();
	}

	/** {@inheritDoc} */
	@Override
	public synchronized void put(byte[] key, byte[] value) {
		data.put(key.clone(), value.clone());
	}

	/** {@inheritDoc} */
	@Override
	public synchronized void remove(byte[] key) {
		data.remove(key);
	}

	/** {@inheritDoc} */
	@Override
	public synchronized void scan(byte[] beginKey, byte[] endKey,
			IKeyValueCallback callback) {
		for (Entry<byte[], byte[]> entry : data.subMap(beginKey, endKey)
				.entrySet()) {
			callback.callback(entry.getKey().clone(), entry.getValue().clone());
		}
	}

	/** {@inheritDoc} */
	@Override
	public synchronized void scanKeys(byte[] beginKey, byte[] endKey,
			IKeyValueCallback callback) {
		for (byte[] key : data.subMap(beginKey, endKey).keySet()) {
			callback.callback(key.clone(), null);
		}
	}

	/**
	 * Serializes this store to the given file. The contents of this file will
	 * be overwritten.
	 */
	public synchronized void dumpToFile(File file) throws StorageException {
		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream(new BufferedOutputStream(
					new FileOutputStream(file)));
			out.writeObject(data);
		} catch (IOException e) {
			throw new StorageException("Could not persist store: "
					+ e.getMessage(), e);
		} finally {
			FileSystemUtils.close(out);
		}
	}

	/** Loads all entries from a map which was serialized to the given file. */
	@SuppressWarnings("unchecked")
	public synchronized void loadFromFile(File file) throws StorageException {
		ObjectInputStream in = null;
		try {
			in = new ObjectInputStream(new BufferedInputStream(
					new FileInputStream(file)));
			Map<byte[], byte[]> m = (Map<byte[], byte[]>) in.readObject();
			data.putAll(m);
		} catch (ClassNotFoundException e) {
			// we map this to IOException as it is an unlikely case and
			// indicates data corruption
			throw new StorageException("Invalid file contents: "
					+ e.getMessage(), e);
		} catch (IOException e) {
			throw new StorageException("Could not load store: "
					+ e.getMessage(), e);
		} finally {
			FileSystemUtils.close(in);
		}
	}

	/** Removes all data from this store. */
	public synchronized void clear() {
		data.clear();
	}

	/**
	 * Returns statistics on the memory consumption of the store. This operation
	 * is potentially expensive and thus should not be called too often. This
	 * method is not thread-safe and should not be called concurrently to other
	 * store operations.
	 */
	public synchronized String getUsageStatistics(boolean detailed) {
		if (!detailed) {
			return "entries: " + data.size();
		}
		long keyBytes = 0;
		long valueBytes = 0;
		for (Entry<byte[], byte[]> entry : data.entrySet()) {
			keyBytes += entry.getKey().length;
			valueBytes += entry.getValue().length;
		}

		return "entries: " + data.size() + ", keys (byte): " + keyBytes
				+ ", values (byte): " + valueBytes;
	}
}