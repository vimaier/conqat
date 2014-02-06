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
package org.conqat.engine.persistence.store.leveldb;

import leveldb.LevelDB;
import leveldb.SWIGTYPE_p_leveldb_iterator_t;
import leveldb.SWIGTYPE_p_leveldb_readoptions_t;
import leveldb.SWIGTYPE_p_leveldb_t;
import leveldb.SWIGTYPE_p_leveldb_writeoptions_t;

import org.conqat.engine.persistence.store.IKeyValueCallback;
import org.conqat.engine.persistence.store.StorageException;
import org.conqat.engine.persistence.store.base.PartitionStoreBase;
import org.conqat.lib.commons.io.ByteArrayUtils;

/**
 * Store implementation for Level DB (http://leveldb.googlecode.com/).
 * 
 * @author $Author: hummelb $
 * @version $Rev: 46133 $
 * @ConQAT.Rating GREEN Hash: 1706B02883ABD035F654E19567CF0532
 */
public class LevelDBStore extends PartitionStoreBase {

	/** Stores whether static structures have been initialized. */
	private static boolean isInitialized = false;

	/** The globally used read options. */
	private static SWIGTYPE_p_leveldb_readoptions_t readOptions;

	/** The globally used write options. */
	private static SWIGTYPE_p_leveldb_writeoptions_t writeOptions;

	/** The underlying DB. */
	private final SWIGTYPE_p_leveldb_t db;

	/** Constructor. */
	public LevelDBStore(String name, SWIGTYPE_p_leveldb_t db) {
		super(name);
		initStatics();
		this.db = db;
	}

	/**
	 * Initializes the static fields. These are initialized in a lazy fashion,
	 * as otherwise simply loading this class (without construction) would cause
	 * initialization of these fields which makes our search for ConQAT
	 * processors fail.
	 */
	private static synchronized void initStatics() {
		if (isInitialized) {
			return;
		}

		readOptions = LevelDB.leveldb_readoptions_create();
		writeOptions = LevelDB.leveldb_writeoptions_create();
		isInitialized = true;
	}

	/** {@inheritDoc} */
	@Override
	public byte[] doGet(byte[] key) throws StorageException {
		String[] error = new String[1];
		byte[][] result = new byte[][] { null };
		LevelDB.leveldb_get2(db, readOptions, key, result, error);
		LevelDBStorageSystem.checkError(error);
		return result[0];
	}

	/** {@inheritDoc} */
	@Override
	public void doPut(byte[] key, byte[] value) throws StorageException {
		String[] error = new String[1];
		LevelDB.leveldb_put(db, writeOptions, key, value, error);
		LevelDBStorageSystem.checkError(error);
	}

	/** {@inheritDoc} */
	@Override
	public void doRemove(byte[] key) throws StorageException {
		String[] error = new String[1];
		LevelDB.leveldb_delete(db, writeOptions, key, error);
		LevelDBStorageSystem.checkError(error);
	}

	/** {@inheritDoc} */
	@Override
	protected void doScan(byte[] beginKey, byte[] endKey,
			IKeyValueCallback callback, boolean includeValue) {
		SWIGTYPE_p_leveldb_iterator_t iterator = LevelDB
				.leveldb_create_iterator(db, readOptions);
		try {
			LevelDB.leveldb_iter_seek(iterator, beginKey);
			reportEntries(iterator, endKey, callback, includeValue);
		} finally {
			LevelDB.leveldb_iter_destroy(iterator);
		}
	}

	/**
	 * Reports all entries starting from the current position of the given
	 * iterator up to the endKey.
	 */
	private void reportEntries(SWIGTYPE_p_leveldb_iterator_t iterator,
			byte[] endKey, IKeyValueCallback callback, boolean includeValue) {
		byte[][] key = new byte[][] { null };
		byte[][] value = new byte[][] { null };
		while (LevelDB.leveldb_iter_valid(iterator) != 0) {
			LevelDB.leveldb_iter_key2(iterator, key);
			if (ByteArrayUtils.isLess(endKey, key[0], true)) {
				return;
			}
			if (includeValue) {
				LevelDB.leveldb_iter_value2(iterator, value);
			}
			callback.callback(key[0], value[0]);
			LevelDB.leveldb_iter_next(iterator);
		}
	}
}
