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
package org.conqat.engine.persistence.store;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.conqat.lib.commons.collections.PairList;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.commons.test.CCSMTestCaseBase;

/**
 * Base class for tests for the various implementations of
 * {@link IStorageSystem} and {@link IStore}.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46842 $
 * @ConQAT.Rating GREEN Hash: F2074286F417A89B49F62B7C0E9450A1
 */
public abstract class StorageSystemTestBase extends CCSMTestCaseBase {

	/** Default name of the store used. */
	private static final String STORE = "test";

	/** A default key used during testing. */
	protected static final byte[] KEY = StringUtils.stringToBytes("my_key");

	/** A default value used during testing. */
	protected static final byte[] VALUE = StringUtils.stringToBytes("my_value");

	/**
	 * The directory where the data is persisted. Initialized in
	 * {@link #setUp()}
	 */
	protected File baseDir;

	/**
	 * The storage system under test. This is created and closed in
	 * {@link #setUp()} and {@link #tearDown()}.
	 */
	protected IStorageSystem storageSystem;

	/**
	 * The store used. This is created and closed in {@link #setUp()} and
	 * {@link #tearDown()}.
	 */
	protected IStore store;

	/**
	 * A {@link CollectingCallBack}, as it is used by many of the tests. This is
	 * recreated each time in {@link #setUp()}.
	 */
	private CollectingCallBack callback;

	/** {@inheritDoc} */
	@Override
	protected void setUp() throws Exception {
		super.setUp();

		baseDir = new File(getTmpDirectory(), getClass().getSimpleName());
		FileSystemUtils.ensureDirectoryExists(baseDir);
		FileSystemUtils.deleteRecursively(baseDir);
		FileSystemUtils.ensureDirectoryExists(baseDir);

		storageSystem = openStorage(baseDir);
		store = storageSystem.openStore(STORE);

		callback = new CollectingCallBack();
	}

	/** {@inheritDoc} */
	@Override
	protected void tearDown() throws Exception {
		if (storageSystem != null) {
			storageSystem.close();
		}
		super.tearDown();
	}

	/** Template method used for opening the */
	protected abstract IStorageSystem openStorage(File baseDir)
			throws StorageException;

	/** Tests the very basic set/get interface. */
	public void testSetGet() throws StorageException {
		// key does not exist in empty store
		assertNull(store.get(KEY));

		// put affects result of get
		store.put(KEY, VALUE);
		assertTrue(Arrays.equals(VALUE, store.get(KEY)));

		// other keys do not affect result of get
		store.put(new byte[] { 7, 8, 9 }, new byte[] { 17, 18, 19 });
		assertTrue(Arrays.equals(VALUE, store.get(KEY)));
	}

	/** Tests removal of elements. */
	public void testRemoveElement() throws StorageException {
		store.put(KEY, VALUE);
		assertTrue(Arrays.equals(VALUE, store.get(KEY)));

		store.remove(KEY);
		assertNull(store.get(KEY));
	}

	/** Tests whether different stores are really independent. */
	public void testIndependentStores() throws StorageException {
		IStore store2 = storageSystem.openStore("other");

		store.put(KEY, VALUE);
		assertTrue(Arrays.equals(VALUE, store.get(KEY)));
		assertNull(store2.get(KEY));
	}

	/** Tests whether data is really lost during deletion of store. */
	public void testDeleteStore() throws StorageException {
		store.put(KEY, VALUE);
		assertTrue(Arrays.equals(VALUE, store.get(KEY)));

		storageSystem.removeStore(STORE);
		store = storageSystem.openStore(STORE);
		assertNull(store.get(KEY));
	}

	/** Tests whether persistence between open/close works. */
	public void testPersistence() throws StorageException {
		store.put(KEY, VALUE);
		assertTrue(Arrays.equals(VALUE, store.get(KEY)));

		storageSystem.close();
		storageSystem = openStorage(baseDir);

		store = storageSystem.openStore(STORE);
		assertTrue(Arrays.equals(VALUE, store.get(KEY)));
	}

	/** Tests the range scanning interface. */
	public void testScanRange() throws StorageException {
		// space makes first key appear before others
		List<String> keys = Arrays.asList(StringUtils.SPACE + "first", "aab",
				"aac", "abc", "aaa", "aaaa");
		for (String s : keys) {
			store.put(StringUtils.stringToBytes(s), new byte[] {});
		}

		store.scan(StringUtils.stringToBytes("aaa"),
				StringUtils.stringToBytes("aaa"), callback);
		assertEquals(0, callback.keys.size());

		callback.keys.clear();
		store.scan(StringUtils.stringToBytes("aaa"),
				StringUtils.stringToBytes("aaab"), callback);
		assertEquals(new HashSet<String>(Arrays.asList("aaa", "aaaa")),
				new HashSet<String>(callback.keys));

		callback.keys.clear();
		store.scan(StringUtils.stringToBytes("aabq"), null, callback);
		assertEquals(new HashSet<String>(Arrays.asList("aac", "abc")),
				new HashSet<String>(callback.keys));
	}

	/** Tests the prefix scanning interface. */
	public void testScanPrefix() throws StorageException {
		List<String> keys = Arrays.asList("/root/foo", "/root/bar",
				"/root/baz", "/some/other/file");
		for (String s : keys) {
			store.put(StringUtils.stringToBytes(s), new byte[] {});
		}

		// well-known prefix
		store.scan(StringUtils.stringToBytes("/roo"), callback);
		checkSubsetAndClear(3, keys, callback.keys);

		// invalid prefix
		store.scan(StringUtils.stringToBytes("inv"), callback);
		checkSubsetAndClear(0, keys, callback.keys);

		// empty prefix
		store.scan(new byte[] {}, callback);
		checkSubsetAndClear(keys.size(), keys, callback.keys);
	}

	/** Tests the corner case in prefix scanning interface. */
	public void testScanPrefixCornerCase() throws StorageException {
		byte[] prefix = { 17, 18, 19, (byte) 0xff };
		store.put(prefix, prefix);

		CollectingCallBack callback = new CollectingCallBack();
		store.scan(prefix, callback);
		assertEquals(1, callback.keys.size());
	}

	/** Tests scanning for multiple prefixes. */
	public void testScanPrefixMultiple() throws StorageException {
		List<String> keys = Arrays.asList("/root/foo", "/root/bar",
				"/root/baz", "/some/other/file", "/a/different/file");
		for (String s : keys) {
			store.put(StringUtils.stringToBytes(s), new byte[] {});
		}

		// well-known prefix
		store.scan(Arrays.asList(StringUtils.stringToBytes("/roo")), callback);
		checkSubsetAndClear(3, keys, callback.keys);

		// overlapping prefix
		store.scan(
				Arrays.asList(StringUtils.stringToBytes("/roo"),
						StringUtils.stringToBytes("/root/f")), callback);
		checkSubsetAndClear(3, keys, callback.keys);

		// no prefixes
		store.scan(new ArrayList<byte[]>(), callback);
		checkSubsetAndClear(0, keys, callback.keys);

		// multiple
		store.scan(
				Arrays.asList(StringUtils.stringToBytes("/roo"),
						StringUtils.stringToBytes("/a/di")), callback);
		checkSubsetAndClear(4, keys, callback.keys);
	}

	/** Test scanning for keys with prefix. */
	public void testScanKeysPrefix() throws StorageException {
		store.put(new byte[] { 1 }, new byte[] { 1 });
		store.scanKeys(new byte[] { 1 }, callback);
		assertEquals(1, callback.keys.size());
	}

	/** Test scanning for keys with begin and end. */
	public void testScanKeysBeginEnd() throws StorageException {
		store.put(new byte[] { 1 }, new byte[] { 1 });
		store.put(new byte[] { 2 }, new byte[] { 2 });
		store.put(new byte[] { 3 }, new byte[] { 3 });
		store.put(new byte[] { 4 }, new byte[] { 1 });

		store.scanKeys(new byte[] { 2 }, new byte[] { 4 }, callback);
		assertEquals(2, callback.keys.size());
	}

	/** Test for multi-get and set. */
	public void testSetGetMultiple() throws StorageException {
		PairList<byte[], byte[]> entries = new PairList<byte[], byte[]>();
		entries.add(new byte[] { 1 }, new byte[] { 1 });
		entries.add(new byte[] { 2 }, new byte[] { 2 });
		store.put(entries);
		List<byte[]> values = store.get(Arrays.asList(new byte[] { 1 },
				new byte[] { 3 }, new byte[] { 2 }));
		assertEquals(3, values.size());
		assertTrue(Arrays.equals(new byte[] { 1 }, values.get(0)));
		assertNull(values.get(1));
		assertTrue(Arrays.equals(new byte[] { 2 }, values.get(2)));

		store.remove(entries.extractFirstList());
		store.scan(new byte[] {}, callback);
		assertEquals(0, callback.keys.size());
	}

	/**
	 * Checks whether the given set is a subset of the superset of given size.
	 * Afterwards the subset is cleared. While we use set semantics, the
	 * parameters use lists as we are also interested in spurious duplicates.
	 */
	private void checkSubsetAndClear(int size, List<String> superSet,
			List<String> subSet) {
		assertEquals(size, subSet.size());
		assertTrue(superSet.containsAll(subSet));
		subSet.clear();
	}

	/** Callback used for testing the scanning interface. */
	public static class CollectingCallBack implements IKeyValueCallback {

		/** Set to store the keys read. */
		public final List<String> keys = new ArrayList<String>();

		/** {@inheritDoc} */
		@Override
		public void callback(byte[] key, byte[] value) {
			synchronized (keys) {
				keys.add(StringUtils.bytesToString(key));
			}
		}
	}
}