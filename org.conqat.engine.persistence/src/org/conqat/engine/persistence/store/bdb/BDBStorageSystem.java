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

import java.io.File;

import org.conqat.engine.persistence.store.IStore;
import org.conqat.engine.persistence.store.StorageException;
import org.conqat.engine.persistence.store.base.PartitionStoreBase;
import org.conqat.engine.persistence.store.base.StorageSystemBase;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

/**
 * Storage system based on Berkeley DB. While Berkeley DB supports multiple
 * databases, we only use a single database and implement stores using
 * {@link PartitionStoreBase}. The reason is that Berkeley DB is very picky
 * about having too many databases open and even more picky about not closing
 * any databases. With our solution we keep only one database open all the time.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46176 $
 * @ConQAT.Rating GREEN Hash: E2926102767F8B175E045FA28CBE39E3
 */
public class BDBStorageSystem extends StorageSystemBase {

	/** The name of the database used. */
	private static final String DATABASE_NAME = "DB";

	/** The environment. */
	private final Environment environment;

	/** The database */
	private final Database database;

	/**
	 * Constructor.
	 * 
	 * @param cacheMemoryInMB
	 *            the size of the memory cache in MB. If this is 0 or negative,
	 *            BDBs default size is used (which is not recommended).
	 */
	public BDBStorageSystem(File baseDirectory, int cacheMemoryInMB)
			throws StorageException {
		ensureStorageDirectory(baseDirectory);

		try {
			EnvironmentConfig envConfig = new EnvironmentConfig();
			envConfig.setAllowCreate(true);
			if (cacheMemoryInMB > 0) {
				envConfig.setCacheSize(((long) cacheMemoryInMB) * 1024 * 1024);
			}
			environment = new Environment(baseDirectory, envConfig);
		} catch (DatabaseException e) {
			throw new StorageException("Could not initialize BDB store!", e);
		}

		try {
			DatabaseConfig dbConfig = new DatabaseConfig();
			dbConfig.setAllowCreate(true);
			dbConfig.setDeferredWrite(true);
			database = environment.openDatabase(null, DATABASE_NAME, dbConfig);
		} catch (DatabaseException e) {
			throw new StorageException("Could not access BDB database called "
					+ DATABASE_NAME + "!", e);
		}
	}

	/** {@inheritDoc} */
	@Override
	public IStore openStore(String name) {
		return new BDBStore(name, database);
	}

	/** {@inheritDoc} */
	@Override
	public void close() throws StorageException {
		try {
			database.close();
			environment.close();
		} catch (DatabaseException e) {
			throw new StorageException("Could not close!", e);
		}
	}
}