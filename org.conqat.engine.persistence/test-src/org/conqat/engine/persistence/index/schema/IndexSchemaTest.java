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
package org.conqat.engine.persistence.index.schema;

import org.conqat.engine.persistence.index.StringIndex;
import org.conqat.engine.persistence.store.IStorageSystem;
import org.conqat.engine.persistence.store.IStore;
import org.conqat.engine.persistence.store.StorageException;
import org.conqat.engine.persistence.store.mem.InMemoryStorageSystem;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.commons.test.CCSMTestCaseBase;

/**
 * Test for the {@link IndexSchema}.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46174 $
 * @ConQAT.Rating GREEN Hash: BC87B4091C8AADC60E2482950BF1AD98
 */
public class IndexSchemaTest extends CCSMTestCaseBase {

	/** The name of the index used for testing. */
	private static final String INDEX_NAME = "test-name";

	/** Entry used for testing. */
	private static final SchemaEntry TEST_ENTRY = new SchemaEntry(
			StringIndex.class, EStorageOption.COMPRESSED);

	/** The storage system used for testing. */
	private IStorageSystem storageSystem;

	/** The schema used for testing. */
	private IndexSchema schema;

	/** {@inheritDoc} */
	@Override
	protected void setUp() throws Exception {
		storageSystem = new InMemoryStorageSystem(null);

		schema = new IndexSchema();
		schema.updateEntry(INDEX_NAME, TEST_ENTRY);
	}

	/** Tests loading and saving of a schema. */
	public void testLoadSave() throws StorageException {
		assertFalse(IndexSchema.hasSchema(storageSystem));

		schema.save(storageSystem);

		assertTrue(IndexSchema.hasSchema(storageSystem));
		assertEquals(schema.toString(), IndexSchema.load(storageSystem)
				.toString());

	}

	/** Tests CRUD on entries. */
	public void testEntryManagement() {
		assertEquals(1, schema.getEntryNames().size());
		assertEquals(TEST_ENTRY, schema.getEntry(INDEX_NAME));

		schema.updateEntry(INDEX_NAME, null);
		assertEquals(0, schema.getEntryNames().size());
		assertNull(schema.getEntry(INDEX_NAME));
	}

	/** Tests schema supported creation of an index. */
	public void testIndexCreation() throws StorageException {
		StringIndex index = schema.openIndex(INDEX_NAME, StringIndex.class,
				storageSystem, null);
		index.setValue("foo", "bar");

		assertNotNull(storageSystem.openStore(INDEX_NAME).get("foo".getBytes()));
	}

	/** Tests application of options to a store. */
	public void testOptionApplication() throws StorageException {
		StringIndex index = schema.openIndex(INDEX_NAME, StringIndex.class,
				storageSystem, null);
		index.setValue("compressed", StringUtils.fillString(1000, 'x'));

		// disable compression
		schema.updateEntry(INDEX_NAME, new SchemaEntry(StringIndex.class));
		index = schema.openIndex(INDEX_NAME, StringIndex.class, storageSystem,
				null);
		index.setValue("non-compressed", StringUtils.fillString(1000, 'x'));

		IStore rawStore = storageSystem.openStore(INDEX_NAME);
		assertEquals(1000, rawStore.get("non-compressed".getBytes()).length);
		assertTrue(rawStore.get("compressed".getBytes()).length < 500);
	}

	/** Tests application of a store decorator. */
	public void testDecorator() throws StorageException {
		final int[] callCount = { 0 };
		schema.setDecorator(new IStoreDecorator() {
			@Override
			public IStore decorate(IStore store) {
				callCount[0] += 1;
				return store;
			}
		});

		schema.openIndex(INDEX_NAME, StringIndex.class, storageSystem, null);
		assertEquals(1, callCount[0]);
		schema.setDecorator(null);

		schema.openIndex(INDEX_NAME, StringIndex.class, storageSystem, null);
		assertEquals(1, callCount[0]);
	}
}
