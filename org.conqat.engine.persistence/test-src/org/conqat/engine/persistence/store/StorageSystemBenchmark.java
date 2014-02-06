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
package org.conqat.engine.persistence.store;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import org.conqat.engine.core.driver.runner.ConQATRunnableBase;
import org.conqat.engine.persistence.store.bdb.BDBStorageSystem;
import org.conqat.engine.persistence.store.dain_leveldb.DainLevelDBStorageSystem;
import org.conqat.engine.persistence.store.leveldb.LevelDBStorageSystem;
import org.conqat.engine.persistence.store.mem.InMemoryStorageSystem;
import org.conqat.engine.persistence.store.util.StorageUtils;
import org.conqat.lib.commons.filesystem.FileSystemUtils;

/**
 * This is a very simple benchmark that compares storage system implementations
 * using a write/delete heavy workload. Focus of the benchmark is both time and
 * disk space used.
 * 
 * To avoid complicated commandline handling, all configuration is performed
 * using constants.
 * 
 * This class is implemented as a {@link ConQATRunnableBase} but also has a main
 * method to allow easy execution both from Eclipse and from a distribution.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46176 $
 * @ConQAT.Rating GREEN Hash: C6B115C5E2DF50EA31292128F0934724
 */
public class StorageSystemBenchmark extends ConQATRunnableBase {

	/** The directory used for storage. */
	private static final File STORAGE_DIRECTORY = new File(
			FileSystemUtils.getTmpDir(), "conqat-store-test");

	/** The cache size allowed for disk-based storage systems. */
	private static final int CACHE_SIZE_MB = 100;

	/** The number of times we clear and refill the store. */
	private static final int CLEAR_FILL_CYCLES = 4;

	/** The size used for random keys. */
	private static final int KEY_SIZE = 1024;

	/**
	 * The maximal size used for random values. The actual value will be
	 * selected between 0 and this value.
	 */
	private static final int VALUE_MAX_SIZE = 10 * 1024;

	/** The expected number of bytes to insert: about 1 GB. */
	private static final int INSERTION_BYTES = 500 * 1024 * 1024;

	/**
	 * The number of key/value pairs to insert. As value size is max value, the
	 * expected value is half.
	 */
	private static final int INSERTION_COUNT = INSERTION_BYTES
			/ (KEY_SIZE + VALUE_MAX_SIZE / 2);

	/** The name of the test store. */
	private static final String TEST_STORE = "store";

	/** Random number generator used for key/value generation. */
	private Random random;

	/** Constructor. */
	public StorageSystemBenchmark() throws IOException {
		// ensure we can create the directory and it is empty
		FileSystemUtils.ensureDirectoryExists(STORAGE_DIRECTORY);
		FileSystemUtils.deleteRecursively(STORAGE_DIRECTORY);
	}

	/** {@inheritDoc} */
	@Override
	protected void doRun() {
		try {
			benchmarkStorageSystem(new InMemoryStorageSystem(STORAGE_DIRECTORY));
			benchmarkStorageSystem(new LevelDBStorageSystem(STORAGE_DIRECTORY,
					CACHE_SIZE_MB));
			benchmarkStorageSystem(new DainLevelDBStorageSystem(
					STORAGE_DIRECTORY, CACHE_SIZE_MB));
			benchmarkStorageSystem(new BDBStorageSystem(STORAGE_DIRECTORY,
					CACHE_SIZE_MB));
		} catch (StorageException e) {
			// as this is basically test-code, this is valid error handling

			// (LH) How are conqat runnables actually supposed to do error
			// handling?
			throw new RuntimeException(e);
		}
	}

	/** Run the benchmark for one single storage system. */
	private void benchmarkStorageSystem(IStorageSystem storageSystem)
			throws StorageException {
		// reinitialize at each benchmark run to ensure consistent results
		random = new Random(42);
		long startTime = System.currentTimeMillis();

		System.out.println("Running on "
				+ storageSystem.getClass().getSimpleName());
		IStore store = storageSystem.openStore(TEST_STORE);
		insertRandomData(store);
		for (int i = 0; i < CLEAR_FILL_CYCLES; ++i) {
			System.out.println("  start cycle " + (i + 1) + " of "
					+ CLEAR_FILL_CYCLES);
			long cycleStart = System.currentTimeMillis();
			StorageUtils.clearStore(store);
			insertRandomData(store);
			System.out.println("       cycle seconds: "
					+ (System.currentTimeMillis() - cycleStart) / 1000. + ", "
					+ getDiskSize() / 1024. / 1024. + " MB on disk.");
		}
		storageSystem.close();

		long endTime = System.currentTimeMillis();

		System.out.println(storageSystem.getClass().getSimpleName() + ": "
				+ (endTime - startTime) / 1000. + " seconds, " + getDiskSize()
				/ 1024. / 1024. + " MB on disk.");
		FileSystemUtils.deleteRecursively(STORAGE_DIRECTORY);
	}

	/** Returns the size of the store on disk in bytes. */
	private long getDiskSize() {
		long size = 0;
		for (File file : FileSystemUtils
				.listFilesRecursively(STORAGE_DIRECTORY)) {
			size += file.length();
		}
		return size;
	}

	/** Inserts random data into the supplied store. */
	private void insertRandomData(IStore store) throws StorageException {
		for (int i = 0; i < INSERTION_COUNT; ++i) {
			store.put(randomBytes(KEY_SIZE),
					randomBytes(random.nextInt(VALUE_MAX_SIZE)));
		}
	}

	/** Generates a random array of bytes. */
	private byte[] randomBytes(int length) {
		byte[] result = new byte[length];
		random.nextBytes(result);
		return result;
	}

	/** Main method. */
	public static void main(String[] args) throws IOException {
		new StorageSystemBenchmark().doRun();
	}
}
