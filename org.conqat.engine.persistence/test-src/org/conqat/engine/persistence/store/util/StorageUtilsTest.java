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
package org.conqat.engine.persistence.store.util;

import static org.conqat.lib.commons.string.StringUtils.bytesToString;
import static org.conqat.lib.commons.string.StringUtils.stringToBytes;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import junit.framework.TestCase;

import org.conqat.engine.persistence.store.IStore;
import org.conqat.engine.persistence.store.StorageException;
import org.conqat.engine.persistence.store.mem.InMemoryStore;
import org.conqat.lib.commons.collections.CollectionUtils;

/**
 * Tests for {@link StorageUtils}.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 45016 $
 * @ConQAT.Rating GREEN Hash: BD53F28C6914DE00C3691D497BEF7086
 */
public class StorageUtilsTest extends TestCase {

	/** Store used for the tests */
	private IStore store;

	/** {@inheritDoc} */
	@Override
	protected void setUp() throws Exception {
		store = new InMemoryStore();
		store.put(stringToBytes("foo"), stringToBytes("A"));
		store.put(stringToBytes("bar"), stringToBytes("BC"));
		store.put(stringToBytes("baz"), stringToBytes("DEF"));
	}

	/** Test for listStringKeys */
	public void testListStringKeys() throws Exception {
		List<String> stringKeys = CollectionUtils.sort(StorageUtils
				.listStringKeys(store));
		assertEquals(3, stringKeys.size());
		assertEquals("bar", stringKeys.get(0));
		assertEquals("baz", stringKeys.get(1));
		assertEquals("foo", stringKeys.get(2));
	}

	/** Tests import and export of stores. */
	public void testImportExport() throws StorageException, IOException {
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(bao);
		StorageUtils.exportStore(store, out);
		out.close();

		DataInputStream in = new DataInputStream(new ByteArrayInputStream(
				bao.toByteArray()));
		IStore newStore = new InMemoryStore();
		StorageUtils.importStore(newStore, in);

		assertEquals(3, StorageUtils.listKeys(newStore).size());
		assertEquals("A", bytesToString(newStore.get(stringToBytes("foo"))));
		assertEquals("BC", bytesToString(newStore.get(stringToBytes("bar"))));
		assertEquals("DEF", bytesToString(newStore.get(stringToBytes("baz"))));
	}

}