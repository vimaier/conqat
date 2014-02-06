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
package org.conqat.engine.persistence.store.hist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.conqat.engine.persistence.store.IKeyValueCallback;
import org.conqat.engine.persistence.store.IStore;
import org.conqat.engine.persistence.store.StorageException;
import org.conqat.engine.persistence.store.StorageSystemTestBase.CollectingCallBack;
import org.conqat.engine.persistence.store.mem.InMemoryStore;
import org.conqat.engine.persistence.store.util.ConvenientStore;
import org.conqat.engine.persistence.store.util.StorageUtils;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.PairList;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.commons.test.CCSMTestCaseBase;

/**
 * This test covers the entire historizing store system.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46886 $
 * @ConQAT.Rating GREEN Hash: D4576D1A491039599E9620FC31C76AE6
 */
public class HistorizingStoreTest extends CCSMTestCaseBase {

	/** The base store used for writing. */
	private IStore baseStore;

	/** {@inheritDoc} */
	@Override
	protected void setUp() throws Exception {
		super.setUp();

		baseStore = new InMemoryStore();

		// insert some data at rev 12
		ConvenientStore rev12 = new ConvenientStore(
				new HeadInsertingHistorizingStore(baseStore, 12));
		PairList<String, byte[]> keysValues = new PairList<String, byte[]>();
		keysValues.add("key1", new byte[] { 1 });
		keysValues.add("key2", new byte[] { 2 });
		keysValues.add("key3", new byte[] { 3 });
		rev12.putWithStrings(keysValues);

		// some changes at rev 27
		ConvenientStore rev27 = new ConvenientStore(
				new HeadInsertingHistorizingStore(baseStore, 27));
		rev27.removeWithString("key2");
		rev27.putWithString("key4", new byte[] { 4 });
		rev27.putWithString("key1", new byte[] { 5 });

		// more changes at rev 42
		ConvenientStore rev42 = new ConvenientStore(
				new HeadInsertingHistorizingStore(baseStore, 42));
		rev42.removeWithString("key1");
		rev42.putWithString("key2", new byte[] { 6 });
		rev42.putWithString("key4", new byte[] { 7 });
	}

	/** Tests reading from the head. */
	public void testHeadReadingStore() throws StorageException {
		headStoreTest(new ConvenientStore(new HeadReadOnlyHistorizingStore(
				baseStore)));
	}

	/** Perform tests for the head store. */
	private void headStoreTest(ConvenientStore headStore)
			throws StorageException {
		assertNull(headStore.getWithString("key1"));
		assertEquals(6, headStore.getWithString("key2")[0]);
		assertEquals(3, headStore.getWithString("key3")[0]);
		assertEquals(7, headStore.getWithString("key4")[0]);

		// scanning
		CollectingCallBack callback = new CollectingCallBack();
		headStore.scan("", callback);
		assertKeys(callback.keys, "key2", "key3", "key4");

		callback.keys.clear();
		headStore.scan(StringUtils.stringToBytes("key1"),
				StringUtils.stringToBytes("key4"), callback);
		assertKeys(callback.keys, "key2", "key3");
	}

	/** Tests reading using a fixed revision. */
	public void testFixedRevisionReadingStore() throws StorageException {
		// using a large revision should be the same as reading from the head
		headStoreTest(new ConvenientStore(
				new TimestampReadOnlyHistorizingStore(baseStore, 100)));

		// using the head revision should be the same as reading from the head
		headStoreTest(new ConvenientStore(
				new TimestampReadOnlyHistorizingStore(baseStore, 42)));

		ConvenientStore rev30 = new ConvenientStore(
				new TimestampReadOnlyHistorizingStore(baseStore, 30));
		assertEquals(5, rev30.getWithString("key1")[0]);
		assertNull(rev30.getWithString("key2"));
		assertEquals(3, rev30.getWithString("key3")[0]);
		assertEquals(4, rev30.getWithString("key4")[0]);

		// scanning
		CollectingCallBack callback = new CollectingCallBack();
		rev30.scan("", callback);
		assertKeys(callback.keys, "key1", "key3", "key4");

		callback.keys.clear();
		rev30.scan(StringUtils.stringToBytes("key1"),
				StringUtils.stringToBytes("key4"), callback);
		assertKeys(callback.keys, "key1", "key3");
	}

	/** Assert the keys match. */
	private static void assertKeys(List<String> actual, String... expected) {
		assertEquals(new HashSet<String>(Arrays.asList(expected)),
				new HashSet<String>(actual));
	}

	/**
	 * Tests rollback to a very early timestamp, which should remove all
	 * content.
	 */
	public void testRollbackToStart() throws StorageException {
		new RollbackableHistorizingStore(baseStore).performRollback(1);
		assertEquals(0, StorageUtils.keyCount(baseStore));
	}

	/**
	 * Tests rollback to a very late timestamp, which should have no effect at
	 * all.
	 */
	public void testRollbackToLateTimestamp() throws StorageException {
		String previous = serializeStore(baseStore);
		new RollbackableHistorizingStore(baseStore).performRollback(1000);
		assertEquals(previous, serializeStore(baseStore));
	}

	/** Tests rollback to a specific timestamp. */
	public void testRollbackToSpecificTimestamp() throws StorageException {
		new RollbackableHistorizingStore(baseStore).performRollback(20);

		// expect 3 head keys and 3 timestamp keys
		assertEquals(6, StorageUtils.keyCount(baseStore));
		assertEquals(3, StorageUtils.keyCount(new HeadReadOnlyHistorizingStore(
				baseStore)));
	}

	/** Serializes the {@link #baseStore} to a readable/comparable string. */
	private static String serializeStore(IStore store) throws StorageException {
		final List<String> lines = new ArrayList<String>();
		store.scan(new byte[0], new IKeyValueCallback() {
			@Override
			public void callback(byte[] key, byte[] value) {
				lines.add(StringUtils.encodeAsHex(key) + " = "
						+ StringUtils.encodeAsHex(value));
			}
		});
		return StringUtils.concat(CollectionUtils.sort(lines), "\n");
	}
}
