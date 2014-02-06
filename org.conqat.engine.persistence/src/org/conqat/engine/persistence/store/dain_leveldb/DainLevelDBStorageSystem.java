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

import java.io.File;
import java.io.IOException;

import org.conqat.engine.persistence.store.IStore;
import org.conqat.engine.persistence.store.StorageException;
import org.conqat.engine.persistence.store.base.StorageSystemBase;
import org.iq80.leveldb.CompressionType;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.impl.Iq80DBFactory;

/**
 * Storage system based on Dain's Java port of LeveDB
 * (https://github.com/dain/leveldb).
 * 
 * @author $Author: hummelb $
 * @version $Rev: 46312 $
 * @ConQAT.Rating GREEN Hash: BB561E239DBC85FC33178CB2BFEA9400
 */
public class DainLevelDBStorageSystem extends StorageSystemBase {

	/** The database used. */
	private final DB database;

	/** Constructor. */
	public DainLevelDBStorageSystem(File dir, int cacheSizeMB)
			throws StorageException {
		ensureStorageDirectory(dir);

		Options options = new Options();
		options.createIfMissing(true);

		// we disable compression, as the storage framework has own support for
		// selective compression of sub-stores
		options.compressionType(CompressionType.NONE);
		options.cacheSize(cacheSizeMB * 1024 * 1024);

		try {
			database = Iq80DBFactory.factory.open(dir, options);
		} catch (IOException e) {
			throw new StorageException(e);
		}
	}

	/** {@inheritDoc} */
	@Override
	public IStore openStore(String name) {
		return new DainLevelDBStore(name, database);
	}

	/** {@inheritDoc} */
	@Override
	public void close() throws StorageException {
		try {
			database.close();
		} catch (IOException e) {
			throw new StorageException(e);
		}
	}
}
