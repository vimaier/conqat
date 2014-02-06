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
package org.conqat.engine.persistence.store.transaction;

import java.io.File;
import java.util.Arrays;

import org.conqat.engine.persistence.store.IStorageSystem;
import org.conqat.engine.persistence.store.IStore;
import org.conqat.engine.persistence.store.StorageException;
import org.conqat.engine.persistence.store.StorageSystemTestBase;
import org.conqat.engine.persistence.store.mem.InMemoryStorageSystem;
import org.conqat.engine.persistence.store.util.ConvenientStore;
import org.conqat.engine.persistence.store.util.StorageUtils;
import org.conqat.lib.commons.collections.PairList;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Tests the {@link TransactionalStore}.
 * 
 * @author $Author: steidl $
 * @version $Rev: 45226 $
 * @ConQAT.Rating GREEN Hash: 8A13A787E87D334F9E24DD41CE02D4AB
 */
public class TransactionalStoreTest extends StorageSystemTestBase {

	/** {@inheritDoc} */
	@Override
	protected IStorageSystem openStorage(File baseDir) throws StorageException {
		return new InMemoryStorageSystem(baseDir) {
			/** {@inheritDoc} */
			@Override
			public IStore openStore(String name) throws StorageException {
				return new TransactionalStore(super.openStore(name));
			}
		};
	}

	/** {@inheritDoc} */
	@Override
	public void testPersistence() {
		// This case can not work as expected, as the base class does not commit
		// the changes
	}

	/** Tests commit and rollback behavior. */
	public void testCommitRollback() throws StorageException {
		TransactionalStore transactionalStore = (TransactionalStore) store;
		ConvenientStore convenientStore = new ConvenientStore(store);

		convenientStore.putWithString("abc", StringUtils.stringToBytes("1"));
		assertEquals("1",
				StringUtils.bytesToString(convenientStore.getWithString("abc")));

		transactionalStore.commit();

		// after a commit, rollback does not affect content.
		transactionalStore.rollback();
		assertEquals("1",
				StringUtils.bytesToString(convenientStore.getWithString("abc")));

		PairList<String, byte[]> keysValues = new PairList<String, byte[]>();
		keysValues.add("abc", StringUtils.stringToBytes("2"));
		keysValues.add("def", StringUtils.stringToBytes("3"));
		convenientStore.putWithStrings(keysValues);

		assertEquals("2",
				StringUtils.bytesToString(convenientStore.getWithString("abc")));
		transactionalStore.rollback();
		assertEquals("1",
				StringUtils.bytesToString(convenientStore.getWithString("abc")));

		convenientStore.putWithStrings(keysValues);
		convenientStore.removeWithString("abc");
		assertNull(convenientStore.getWithString("abc"));

		assertEquals(Arrays.asList("def"),
				StorageUtils.listStringKeys(convenientStore));
		transactionalStore.rollback();
		assertEquals(Arrays.asList("abc"),
				StorageUtils.listStringKeys(convenientStore));
	}
}
