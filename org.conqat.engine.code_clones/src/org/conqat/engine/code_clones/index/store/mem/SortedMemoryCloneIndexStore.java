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
package org.conqat.engine.code_clones.index.store.mem;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.conqat.engine.code_clones.index.Chunk;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.ISortableData;
import org.conqat.lib.commons.collections.SortableDataUtils;
import org.conqat.lib.commons.collections.UnmodifiableList;
import org.conqat.lib.commons.digest.MD5Digest;
import org.conqat.lib.commons.io.SerializationUtils;

/**
 * A clone index store that keeps all data in sorted lists in memory. We do not
 * use the Java lists with custom storage objects but rather keep all data in
 * flat arrays. As each Java object has a size overhead of several bytes, this
 * makes the store more memory efficient.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43764 $
 * @ConQAT.Rating GREEN Hash: C848161F0BB9BB591C8042262EBF11F5
 */
public class SortedMemoryCloneIndexStore extends MemoryStoreBase {

	/** Number of ints needed to store an MD5 sum. */
	private static final int MD5_INTS = MD5Digest.MD5_BYTES / 4;

	/**
	 * Number of ints required for storing a chunk (without originId). This
	 * consists of the MD5 sum, one int for each of the unit index, first raw
	 * line, last raw line, start raw offset, end raw offset.
	 */
	/* package */static final int CHUNK_INTS = MD5_INTS + 5;

	/** Initial size of the buffers. */
	private static final int INITIAL_BUFFER_SIZE = 1024;

	/**
	 * Map used for lazy deletion. This maps from originId to size of the array
	 * when deletion was requested. The size is needed to ensure that any newly
	 * inserted chunks of the same originId are not deleted as well (and these
	 * will be stored at higher positions). This is an {@link IdentityHashMap}
	 * as we use interned strings.
	 */
	private final Map<String, Integer> deletedFiles = new IdentityHashMap<String, Integer>();

	/** The number of chunks currently stored. */
	private int size = 0;

	/** Flag storing whether the index is dirty (i.e. sorting is required). */
	private boolean dirty = false;

	/**
	 * Array for storing the originIds of the chunks. The strings used here will
	 * be interned.
	 */
	private String[] originIds = new String[INITIAL_BUFFER_SIZE];

	/**
	 * Array used for storing the remaining data of the chunks. This have to
	 * match with {@link #originIds}, but will have {@link #CHUNK_INTS} times
	 * the size, as that many ints are required for each chunk.
	 */
	private int[] chunkData = new int[INITIAL_BUFFER_SIZE * CHUNK_INTS];

	/**
	 * Indices into the originIds array in lexicographical order, i.e. providing
	 * a view of the chunks sorted by filename. View is meant that when
	 * accessing the originIds array in the order of the indices given here, the
	 * origins will appear sorted.
	 */
	private int[] byOriginIndices = new int[INITIAL_BUFFER_SIZE];

	/** Sortable data for the originIds and chunkData arrays. */
	private final ISortableData byChunkSortable = new ISortableData() {

		@Override
		public void swap(int i, int j) {
			String tmp = originIds[i];
			originIds[i] = originIds[j];
			originIds[j] = tmp;

			i *= CHUNK_INTS;
			j *= CHUNK_INTS;
			for (int k = 0; k < CHUNK_INTS; ++k, ++i, ++j) {
				int x = chunkData[i];
				chunkData[i] = chunkData[j];
				chunkData[j] = x;
			}
		}

		@Override
		public int size() {
			return size;
		}

		@Override
		public boolean isLess(int i, int j) {
			return isLessByChunk(i, j);
		}
	};

	/**
	 * Sortable data using comparison by originId. This only works on the
	 * {@link #byOriginIndices} array.
	 */
	private final ISortableData byOriginSortable = new ISortableData() {

		@Override
		public void swap(int i, int j) {
			int tmp = byOriginIndices[i];
			byOriginIndices[i] = byOriginIndices[j];
			byOriginIndices[j] = tmp;
		}

		@Override
		public int size() {
			return size;
		}

		@Override
		public boolean isLess(int i, int j) {
			// we use the object's address to speed up comparison. This is ok,
			// as we interned all strings.
			return System.identityHashCode(originIds[byOriginIndices[i]]) < System
					.identityHashCode(originIds[byOriginIndices[j]]);
		}
	};

	/** {@inheritDoc} */
	@Override
	public void removeChunks(String filename) {
		dirty = true;
		deletedFiles.put(filename, size);
	}

	/** {@inheritDoc} */
	@Override
	public void batchInsertChunks(List<Chunk> chunks) {
		dirty = true;
		for (Chunk chunk : chunks) {
			insertChunk(chunk);
		}
	}

	/** Inserts a single chunk. */
	private void insertChunk(Chunk chunk) {
		// resize if required
		if (size >= originIds.length) {
			doubleStoreSize();
		}

		originIds[size] = chunk.getOriginId().intern();
		int offset = size * CHUNK_INTS;
		byte[] hash = chunk.getChunkHash().getBytes();
		for (int i = 0; i < MD5_INTS; ++i) {
			chunkData[offset++] = SerializationUtils.extractInt(hash, 4 * i);
		}
		chunkData[offset++] = chunk.getFirstUnitIndex();
		chunkData[offset++] = chunk.getFirstRawLineNumber();
		chunkData[offset++] = chunk.getLastRawLineNumber();
		chunkData[offset++] = chunk.getRawStartOffset();
		chunkData[offset] = chunk.getRawEndOffset();

		size++;
	}

	/** Doubles the capacity of the store. */
	private void doubleStoreSize() {
		dirty = true;

		String[] oldFilenames = originIds;
		originIds = new String[2 * originIds.length];
		System.arraycopy(oldFilenames, 0, originIds, 0, oldFilenames.length);

		int[] oldChunkData = chunkData;
		chunkData = new int[2 * chunkData.length];
		System.arraycopy(oldChunkData, 0, chunkData, 0, oldChunkData.length);

		// no need to copy byFileIndices, as we are dirty
		// (regenerated in ensureClean() method)
		byOriginIndices = new int[2 * byOriginIndices.length];
	}

	/** {@inheritDoc} */
	@Override
	public List<Chunk> getChunksByOrigin(String originId) {
		ensureClean();

		originId = originId.intern();

		List<Chunk> result = new ArrayList<Chunk>();

		// store the element to search for in the array (but do not increase
		// size, as we do not want to actually "store" it); see documentation of
		// binarySearch() method below!
		originIds[size] = originId;
		byOriginIndices[size] = size;

		int index = SortableDataUtils.binarySearch(byOriginSortable, size);
		while (index < size && originIds[byOriginIndices[index]] == originId) {
			result.add(extractChunk(byOriginIndices[index]));
			++index;
		}

		if (result.isEmpty()) {
			return null;
		}
		return result;
	}

	/** Extracts the i-th chunk from the arrays. */
	private Chunk extractChunk(int i) {
		String filename = originIds[i];
		i *= CHUNK_INTS;
		byte[] hash = new byte[MD5Digest.MD5_BYTES];
		for (int j = 0; j < MD5_INTS; ++j) {
			SerializationUtils.insertInt(chunkData[i++], hash, 4 * j);
		}
		int firstUnitIndex = chunkData[i++];
		int firstRawLine = chunkData[i++];
		int lastRawLine = chunkData[i++];
		int rawStartOffset = chunkData[i++];
		int rawEndOffset = chunkData[i];

		return new Chunk(filename, new MD5Digest(hash), firstUnitIndex,
				firstRawLine, lastRawLine, rawStartOffset, rawEndOffset);
	}

	/** {@inheritDoc} */
	@Override
	public UnmodifiableList<Chunk> getChunksByHashes(Set<MD5Digest> chunkHashes) {
		ensureClean();

		List<Chunk> result = new ArrayList<Chunk>();
		for (MD5Digest hash : chunkHashes) {
			getChunksByHash(hash, result);
		}
		return CollectionUtils.asUnmodifiable(result);
	}

	/** Adds all chunks for a given hash to the result list. */
	private void getChunksByHash(MD5Digest chunkHash, List<Chunk> result) {
		// store the element to search for in the array (but do not increase
		// size, as we do not want to actually "store" it); see documentation of
		// binarySearch() method below!
		int offset = size * CHUNK_INTS;
		byte[] hash = chunkHash.getBytes();
		for (int i = 0; i < MD5_INTS; ++i) {
			chunkData[offset++] = SerializationUtils.extractInt(hash, 4 * i);
		}

		int index = SortableDataUtils.binarySearch(byChunkSortable, size);
		while (index < size && !isLessByChunk(size, index)) {
			result.add(extractChunk(index));
			++index;
		}
	}

	/** {@inheritDoc} */
	@Override
	public void close() {
		ensureClean();
	}

	/**
	 * Ensures that the store is not dirty, i.e. sorts the chunks if required.
	 * Additionally sufficient space for at least one additional element is
	 * created.
	 */
	private void ensureClean() {
		if (!dirty) {
			return;
		}

		if (size >= originIds.length) {
			doubleStoreSize();
		}

		// remove deleted files
		if (!deletedFiles.isEmpty()) {
			compact();
			deletedFiles.clear();
		}

		// sort by MD5 hash
		SortableDataUtils.sort(byChunkSortable);

		// create filename sorted view
		for (int i = 0; i < size; ++i) {
			byOriginIndices[i] = i;
		}
		SortableDataUtils.sort(byOriginSortable);

		dirty = false;
	}

	/** Removes all entries which belong to deleted files. */
	private void compact() {
		int readPos = 0;
		int writePos = 0;
		while (readPos < size) {
			Integer deleteSize = deletedFiles.get(originIds[readPos]);
			boolean deleted = deleteSize != null && readPos < deleteSize;
			if (!deleted) {
				if (readPos != writePos) {
					originIds[writePos] = originIds[readPos];
					System.arraycopy(chunkData, readPos * CHUNK_INTS,
							chunkData, writePos * CHUNK_INTS, CHUNK_INTS);
				}
				++writePos;
			}
			++readPos;
		}
		size = writePos;
	}

	/**
	 * Returns whether one element is less than the other when compared by chunk
	 * hash.
	 */
	private boolean isLessByChunk(int i, int j) {
		// perform comparison based on hash (MD5)
		i *= CHUNK_INTS;
		j *= CHUNK_INTS;
		for (int k = 0; k < MD5_INTS; ++k, ++i, ++j) {
			if (chunkData[i] < chunkData[j]) {
				return true;
			}
			if (chunkData[i] > chunkData[j]) {
				return false;
			}
		}
		return false;
	}
}
