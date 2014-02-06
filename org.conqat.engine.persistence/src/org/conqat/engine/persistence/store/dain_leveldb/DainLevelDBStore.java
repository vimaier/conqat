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
package org.conqat.engine.persistence.store.dain_leveldb;

import java.util.List;
import java.util.Map.Entry;

import org.conqat.engine.persistence.store.IKeyValueCallback;
import org.conqat.engine.persistence.store.StorageException;
import org.conqat.engine.persistence.store.base.PartitionStoreBase;
import org.conqat.lib.commons.collections.PairList;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.io.ByteArrayUtils;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBException;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.WriteBatch;

/**
 * Store implementation for {@link DainLevelDBStorageSystem}.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46176 $
 * @ConQAT.Rating GREEN Hash: 4C95C8AE24287E8802DAB2B0A58CE55F
 */
public class DainLevelDBStore extends PartitionStoreBase {

	/** The underlying database. */
	private final DB database;

	/** Constructor. */
	public DainLevelDBStore(String name, DB database) {
		super(name);
		this.database = database;
	}

	/** {@inheritDoc} */
	@Override
	protected byte[] doGet(byte[] key) throws StorageException {
		try {
			return database.get(key);
		} catch (DBException e) {
			throw new StorageException(e);
		}
	}

	/** {@inheritDoc} */
	@Override
	protected void doPut(byte[] key, byte[] value) throws StorageException {
		try {
			database.put(key, value);
		} catch (DBException e) {
			throw new StorageException(e);
		}
	}

	/** {@inheritDoc} */
	@Override
	protected void doBatchPut(PairList<byte[], byte[]> keysValues)
			throws StorageException {
		WriteBatch batch = null;
		try {
			batch = database.createWriteBatch();
			for (int i = 0; i < keysValues.size(); ++i) {
				batch.put(keysValues.getFirst(i), keysValues.getSecond(i));
			}
			database.write(batch);
		} catch (DBException e) {
			throw new StorageException(e);
		} finally {
			FileSystemUtils.close(batch);
		}
	}

	/** {@inheritDoc} */
	@Override
	protected void doRemove(byte[] key) throws StorageException {
		try {
			database.delete(key);
		} catch (DBException e) {
			throw new StorageException(e);
		}
	}

	/** {@inheritDoc} */
	@Override
	protected void doBatchRemove(List<byte[]> keys) throws StorageException {
		WriteBatch batch = null;
		try {
			batch = database.createWriteBatch();
			for (byte[] key : keys) {
				batch.delete(key);
			}
			database.write(batch);
		} catch (DBException e) {
			throw new StorageException(e);
		} finally {
			FileSystemUtils.close(batch);
		}
	}

	/** {@inheritDoc} */
	@SuppressWarnings("resource")
	@Override
	protected void doScan(byte[] beginKey, byte[] endKey,
			IKeyValueCallback callback, boolean includeValue)
			throws StorageException {

		DBIterator iterator = null;
		try {
			iterator = database.iterator();
			iterator.seek(beginKey);
			while (iterator.hasNext()) {
				Entry<byte[], byte[]> keyValue = iterator.next();
				if (ByteArrayUtils.isLess(endKey, keyValue.getKey(), true)) {
					return;
				}
				callback.callback(keyValue.getKey(), keyValue.getValue());
			}
		} catch (DBException e) {
			throw new StorageException(e);
		} finally {
			FileSystemUtils.close(iterator);
		}
	}
}
