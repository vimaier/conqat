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
package org.conqat.engine.persistence.store.bdb;

import org.conqat.engine.persistence.store.IKeyValueCallback;
import org.conqat.engine.persistence.store.StorageException;
import org.conqat.engine.persistence.store.base.ByteArrayComparator;
import org.conqat.engine.persistence.store.base.PartitionStoreBase;

import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;

/**
 * Store implementation for Berkeley DB.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 46133 $
 * @ConQAT.Rating GREEN Hash: F9BDB51676FC91007CF9512336101347
 */
public class BDBStore extends PartitionStoreBase {

	/** The BDB database. */
	private final Database database;

	/** Constructor. */
	public BDBStore(String name, Database database) {
		super(name);
		this.database = database;
	}

	/** {@inheritDoc} */
	@Override
	protected byte[] doGet(byte[] key) throws StorageException {
		DatabaseEntry value = new DatabaseEntry();
		try {
			if (database.get(null, new DatabaseEntry(key), value,
					LockMode.DEFAULT) != OperationStatus.SUCCESS) {
				return null;
			}
		} catch (DatabaseException e) {
			throw new StorageException("Could not access BDB data!", e);
		}
		return value.getData();
	}

	/** {@inheritDoc} */
	@Override
	protected void doPut(byte[] key, byte[] value) throws StorageException {
		try {
			database.put(null, new DatabaseEntry(key), new DatabaseEntry(value));
		} catch (DatabaseException e) {
			throw new StorageException("Could not store BDB data!", e);
		}
	}

	/** {@inheritDoc} */
	@Override
	protected void doScan(byte[] beginKey, byte[] endKey,
			IKeyValueCallback callback, boolean includeValue)
			throws StorageException {
		Cursor cursor = null;
		try {
			cursor = database.openCursor(null, null);

			DatabaseEntry key = new DatabaseEntry(beginKey);
			DatabaseEntry value = new DatabaseEntry();
			if (!includeValue) {
				value.setPartial(true);
			}
			OperationStatus result = cursor.getSearchKeyRange(key, value,
					LockMode.DEFAULT);

			while (result == OperationStatus.SUCCESS
					&& isLess(key.getData(), endKey)) {

				byte[] callbackValue = null;
				if (includeValue) {
					callbackValue = value.getData();
				}
				callback.callback(key.getData(), callbackValue);
				result = cursor.getNext(key, value, LockMode.DEFAULT);
			}
		} catch (DatabaseException e) {
			throw new StorageException("Could not scan BDB table!", e);
		} finally {
			if (cursor != null) {
				try {
					cursor.close();
				} catch (DatabaseException e) {
					// nothing we can do here...
				}
			}
		}
	}

	/** Returns whether one key is less than the other one. */
	private static boolean isLess(byte[] b1, byte[] b2) {
		return ByteArrayComparator.INSTANCE.compare(b1, b2) < 0;
	}

	/** {@inheritDoc} */
	@Override
	protected void doRemove(byte[] key) throws StorageException {
		try {
			database.delete(null, new DatabaseEntry(key));
		} catch (Exception e) {
			// we catch exception here, as the number of possible (uncatched)
			// exceptions is really overwhelming
			throw new StorageException("Deletion failed!", e);
		}
	}
}