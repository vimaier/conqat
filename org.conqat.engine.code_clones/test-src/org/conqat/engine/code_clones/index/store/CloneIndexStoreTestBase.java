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
package org.conqat.engine.code_clones.index.store;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import org.conqat.engine.code_clones.index.Chunk;
import org.conqat.engine.persistence.store.StorageException;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.digest.MD5Digest;
import org.conqat.lib.commons.test.CCSMTestCaseBase;

/**
 * Base class for tests for {@link ICloneIndexStore} implementations.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43764 $
 * @ConQAT.Rating GREEN Hash: C18DA9F1306706A2873B34F3C637D4CC
 */
public abstract class CloneIndexStoreTestBase extends CCSMTestCaseBase {

	/** Factory method for creating a new store. */
	protected abstract ICloneIndexStore createStore() throws StorageException;

	/** The store used. */
	protected ICloneIndexStore store;

	/** {@inheritDoc} */
	@Override
	protected void setUp() throws Exception {
		store = createStore();
	}

	/** {@inheritDoc} */
	@Override
	protected void tearDown() throws Exception {
		store.close();
	}

	/** Tests whether option setting works. */
	public void testOptions() throws StorageException {
		String key1 = "mykey";
		String key2 = "another_key";

		store.setOption(key1, 1);
		store.setOption(key2, 2);

		assertEquals(1, store.getOption(key1));
		assertEquals(2, store.getOption(key2));

		store.setOption(key1, 3);

		assertEquals(3, store.getOption(key1));
		assertEquals(2, store.getOption(key2));
	}

	/** Tests combination of insert and read. */
	public void testInsertRead() throws StorageException {
		String filename1 = "filename1.java";
		List<Chunk> chunks1 = createChunksForFile(filename1, 12);
		String filename2 = "filename2.java";
		List<Chunk> chunks2 = createChunksForFile(filename2, 77);

		store.batchInsertChunks(chunks1);
		store.batchInsertChunks(chunks2);

		checkQueryByFile(filename1, chunks1);
		checkQueryByFile(filename2, chunks2);

		int index = 3;
		List<Chunk> result = store.getChunksByHashes(Collections
				.singleton(chunks1.get(index).getChunkHash()));
		// as we generate MD5 based on unit index, we should get one match for
		// each file
		assertEquals(2, result.size());
		result = CollectionUtils.sort(result, new Comparator<Chunk>() {
			@Override
			public int compare(Chunk cd1, Chunk cd2) {
				return cd1.getOriginId().compareTo(cd2.getOriginId());
			}
		});
		assertEquals(chunks1.get(index), result.get(0));
		assertEquals(chunks2.get(index), result.get(1));

		// only one result for higher index expected
		index = 50;
		result = store.getChunksByHashes(Collections.singleton(chunks2.get(
				index).getChunkHash()));
		assertEquals(1, result.size());
		assertEquals(chunks2.get(index), result.get(0));
	}

	/** Performs a query-by-file operation and checks the result. */
	private void checkQueryByFile(String filename, List<Chunk> expected)
			throws StorageException {
		List<Chunk> result = store.getChunksByOrigin(filename);
		assertNotNull(result);
		assertEquals(expected.size(), result.size());
		result = CollectionUtils.sort(result, new Comparator<Chunk>() {
			@Override
			public int compare(Chunk cd1, Chunk cd2) {
				return cd1.getFirstUnitIndex() - cd2.getFirstUnitIndex();
			}
		});
		for (int i = 0; i < expected.size(); ++i) {
			assertEquals(expected.get(i), result.get(i));
		}
	}

	/** Tests deletion. */
	public void testDelete() throws StorageException {
		String filename1 = "filename1.java";
		List<Chunk> chunks1 = createChunksForFile(filename1, 12);
		String filename2 = "filename2.java";
		List<Chunk> chunks2 = createChunksForFile(filename2, 77);
		String filename3 = "filename3.java";
		List<Chunk> chunks3 = createChunksForFile(filename3, 66);

		store.batchInsertChunks(chunks1);
		store.batchInsertChunks(chunks2);
		store.batchInsertChunks(chunks3);

		checkQueryByFile(filename1, chunks1);
		checkQueryByFile(filename2, chunks2);
		checkQueryByFile(filename3, chunks3);

		store.removeChunks(filename2);

		assertNull(store.getChunksByOrigin(filename2));
		checkQueryByFile(filename1, chunks1);
		checkQueryByFile(filename3, chunks3);
		assertEquals(
				2,
				store.getChunksByHashes(
						Collections.singleton(chunks1.get(3).getChunkHash()))
						.size());
	}

	/** Creates a dummy chunk list for given filename and length. */
	protected List<Chunk> createChunksForFile(String filename, int listLength) {
		List<Chunk> list = new ArrayList<Chunk>();
		for (int i = 0; i < listLength; ++i) {
			list.add(new Chunk(filename, createRandomDigest(i), i, i + 1,
					i + 2, i + 3, i + 4));
		}
		return list;
	}

	/** Creates a random MD5 digest with given random seed. */
	protected MD5Digest createRandomDigest(int seed) {
		Random r = new Random(seed);
		byte[] b = new byte[MD5Digest.MD5_BYTES];
		r.nextBytes(b);
		return new MD5Digest(b);
	}
}
