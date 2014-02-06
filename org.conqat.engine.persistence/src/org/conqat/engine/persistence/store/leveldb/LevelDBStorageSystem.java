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

import java.io.File;

import leveldb.LevelDB;
import leveldb.SWIGTYPE_p_leveldb_cache_t;
import leveldb.SWIGTYPE_p_leveldb_options_t;
import leveldb.SWIGTYPE_p_leveldb_t;

import org.apache.log4j.Logger;
import org.conqat.engine.core.bundle.BundleResourceManager;
import org.conqat.engine.persistence.BundleContext;
import org.conqat.engine.persistence.store.IStore;
import org.conqat.engine.persistence.store.StorageException;
import org.conqat.engine.persistence.store.base.StorageSystemBase;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Storage system implementation for LevelDB (http://leveldb.googlecode.com/).
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46176 $
 * @ConQAT.Rating GREEN Hash: 492EF48F03CE9B0821A5D76251F611C0
 */
public class LevelDBStorageSystem extends StorageSystemBase {

	/** The logger. */
	private static final Logger LOGGER = Logger
			.getLogger(LevelDBStorageSystem.class);

	/** Whether the libraries are loaded. */
	private static boolean librariesLoaded = false;

	/** The options used for the DB. */
	private final SWIGTYPE_p_leveldb_options_t options;

	/** The cache used. */
	private final SWIGTYPE_p_leveldb_cache_t cache;

	/** The main DB object. */
	private final SWIGTYPE_p_leveldb_t db;

	/** Constructor. */
	public LevelDBStorageSystem(File dir, int cacheSizeMB)
			throws StorageException {
		loadLibraries();

		ensureStorageDirectory(dir);

		options = LevelDB.leveldb_options_create();
		LevelDB.leveldb_options_set_create_if_missing(options, (short) 1);

		cache = LevelDB.leveldb_cache_create_lru(cacheSizeMB * 1024 * 1024);
		LevelDB.leveldb_options_set_cache(options, cache);

		String[] error = new String[1];
		db = LevelDB.leveldb_open(options, dir.getAbsolutePath(), error);
		checkError(error);
	}

	/** Performs error handling according to the LevelDB interface. */
	/* package */static void checkError(String[] error) throws StorageException {
		if (error[0] != null) {
			throw new StorageException(error[0]);
		}
	}

	/** Attempts to load the native libraries. */
	private static synchronized void loadLibraries() {
		if (librariesLoaded) {
			return;
		}

		String libName = "leveldb/" + determineLibraryName();
		LOGGER.info("LevelDB store using library " + libName);

		File levelDbLib = new File(BundleResourceManager.RESOURCES_LOCATION,
				libName);
		BundleContext context = BundleContext.getInstance();
		if (context != null) {
			levelDbLib = context.getResourceManager()
					.getResourceAsFile(libName);
		}

		CCSMAssert.isTrue(levelDbLib.canRead(),
				"Could not find native code at " + levelDbLib);
		Runtime.getRuntime().load(levelDbLib.getAbsolutePath());
		librariesLoaded = true;
	}

	/** Calculates the name of the library to use based on system properties. */
	private static String determineLibraryName() {
		String extension = StringUtils.EMPTY_STRING;
		if (System.getProperty("os.arch").contains("64")) {
			extension = "64";
		}

		String osName = System.getProperty("os.name").toLowerCase();
		if (osName.startsWith("windows")) {
			return "leveldbjni-windows" + extension + ".dll";
		} else if (osName.startsWith("linux")) {
			return "libleveldbjni-linux" + extension + ".so";
		} else if (osName.startsWith("mac")) {
			return "libleveldbjni-mac" + extension + ".so";
		} else {
			throw new AssertionError("Unsupported platform " + osName);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void close() {
		LevelDB.leveldb_close(db);
		LevelDB.leveldb_cache_destroy(cache);
		LevelDB.leveldb_options_destroy(options);
	}

	/** {@inheritDoc} */
	@Override
	public IStore openStore(String name) {
		return new LevelDBStore(name, db);
	}
}
