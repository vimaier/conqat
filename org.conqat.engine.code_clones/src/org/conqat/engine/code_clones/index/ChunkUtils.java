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
package org.conqat.engine.code_clones.index;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.conqat.engine.code_clones.core.Unit;
import org.conqat.engine.code_clones.index.store.ICloneIndexStore;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.persistence.store.StorageException;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.lib.commons.collections.ListMap;
import org.conqat.lib.commons.digest.Digester;
import org.conqat.lib.commons.digest.MD5Digest;
import org.conqat.lib.commons.string.FastStringComparator;

/**
 * Utility methods for dealing with chunks.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 45470 $
 * @ConQAT.Rating GREEN Hash: 23A17D093E67B04F62DF79568E406D94
 */
public class ChunkUtils {

	/**
	 * String used for separating units in
	 * {@link #calculateChunks(List, int, ITokenElement, int)}.
	 */
	private static final byte[] UNIT_SEPARATOR = "*#*".getBytes();

	/**
	 * Singleton instance of a comparator used to sort by originId and then unit
	 * index (both ascending).
	 */
	private static final Comparator<Chunk> CHUNK_COMPARATOR = new Comparator<Chunk>() {
		@Override
		public int compare(Chunk chunk1, Chunk chunk2) {
			int cmp = FastStringComparator.INSTANCE.compare(
					chunk1.getOriginId(), chunk2.getOriginId());
			if (cmp != 0) {
				return cmp;
			}
			return chunk1.getFirstUnitIndex() - chunk2.getFirstUnitIndex();
		}
	};

	/**
	 * Obtains the list of ordered chunks for a list of chunks from a single
	 * origin. The returned list stores for each unit position in the origin
	 * (list) all chunks with the same hash. All of the lists will be sorted by
	 * originId and then unit index.
	 */
	public static List<ChunkList> obtainOrderedChunks(ICloneIndexStore store,
			List<Chunk> originChunks) throws StorageException {

		ListMap<MD5Digest, Chunk> chunksByHash = getChunksByHash(store,
				originChunks);

		List<ChunkList> orderedChunks = new ArrayList<ChunkList>();
		for (Chunk chunk : originChunks) {
			int index = chunk.getFirstUnitIndex();
			while (index >= orderedChunks.size()) {
				orderedChunks.add(new ChunkList());
			}

			List<Chunk> chunks = chunksByHash.getCollection(chunk
					.getChunkHash());
			if (chunks == null) {
				throw new StorageException(
						"Inconsistent database: No chunks returned for hash "
								+ chunk.getChunkHash());
			}
			if (chunks.size() >= 2) {
				List<Chunk> sortedChunks = orderedChunks.get(index);
				sortedChunks.addAll(chunks);
				Collections.sort(sortedChunks, CHUNK_COMPARATOR);
			}
		}
		return orderedChunks;
	}

	/**
	 * Fetches all chunks with hashes that are also present in one of the input
	 * chunks and calculates a hash to chunk list mapping..
	 */
	private static ListMap<MD5Digest, Chunk> getChunksByHash(
			ICloneIndexStore store, List<Chunk> originChunks)
			throws StorageException {
		Set<MD5Digest> chunkHashes = new HashSet<MD5Digest>();
		for (Chunk chunk : originChunks) {
			chunkHashes.add(chunk.getChunkHash());
		}

		ListMap<MD5Digest, Chunk> chunksByHash = new ListMap<MD5Digest, Chunk>();
		for (Chunk chunk : store.getChunksByHashes(chunkHashes)) {
			chunksByHash.add(chunk.getChunkHash(), chunk);
		}
		return chunksByHash;
	}

	/**
	 * Returns the first {@link Chunk} from the list matching the given
	 * originId. Returns null if none is found.
	 */
	public static Chunk findFirstChunkFor(String originId, List<Chunk> chunks) {
		for (Chunk chunk : chunks) {
			if (chunk.getOriginId().equals(originId)) {
				return chunk;
			}
		}
		return null;
	}

	/**
	 * Returns whether the chunk locations from the <code>subSet</code> list are
	 * a subset of those from the <code>superSet</code> list. For this
	 * comparison, only {@link Chunk#getOriginId()} and
	 * {@link Chunk#getFirstUnitIndex()} are used, as this is sufficient to
	 * uniquely identify a chunk. This method respects that the locations in
	 * subSet are advanced by <code>distance</code> compared to superSet.
	 * <p>
	 * The lists put into this method must be sorted!
	 */
	public static boolean isSubSet(List<Chunk> subSet, List<Chunk> superSet,
			int distance) {
		int subIndex = 0;
		int superIndex = 0;
		while (subIndex < subSet.size()) {
			if (superIndex >= superSet.size()) {
				return false;
			}

			Chunk subLocation = subSet.get(subIndex);
			Chunk superLocation = superSet.get(superIndex);
			int cmp = compareWithDistance(superLocation, subLocation, distance);
			if (cmp == 0) {
				++subIndex;
				++superIndex;
			} else if (cmp < 0) {
				// element in super is "too small", but element in sub might
				// still be found
				++superIndex;
			} else {
				// element in super is "too big"; no chance to find element in
				// sub
				return false;
			}
		}

		return true;
	}

	/**
	 * Returns a (sorted) list containing the intersection of the two lists of
	 * locations based on {@link Chunk#getOriginId()} and
	 * {@link Chunk#getFirstUnitIndex()} only, as this is sufficient to uniquely
	 * identify a chunk. This method respects that the locations in
	 * <code>next</code> are advanced by one compared to <code>prev</code>. The
	 * returned list is a subset of nextLocations (i.e. using its indices).
	 * <p>
	 * The lists put into this method must be sorted!
	 * 
	 * @param prev
	 *            this is the previous chunk (i.e. the one before distance).
	 * @param next
	 *            this is the next chunk (i.e. the one after distance).
	 */
	public static List<Chunk> intersect(List<Chunk> prev, List<Chunk> next) {
		List<Chunk> result = new ArrayList<Chunk>();
		int prevIndex = 0;
		int nextIndex = 0;
		int prevSize = prev.size();
		int nextSize = next.size();
		while (prevIndex < prevSize && nextIndex < nextSize) {
			int cmp = compareWithDistance(prev.get(prevIndex),
					next.get(nextIndex), 1);
			if (cmp == 0) {
				result.add(next.get(nextIndex));
				++prevIndex;
				++nextIndex;
			} else if (cmp < 0) {
				// prev is "too small"
				++prevIndex;
			} else {
				++nextIndex;
			}
		}

		return result;
	}

	/**
	 * Performs a comparison based on {@link Chunk#getOriginId()} and
	 * {@link Chunk#getFirstUnitIndex()}, but increasing the unit index of prev
	 * by <code>distance</code> before comparison.
	 * 
	 * @param prev
	 *            this is the previous chunk (i.e. the one before distance).
	 * @param next
	 *            this is the next chunk (i.e. the one after distance).
	 * @param distance
	 *            the increase in the unit index performed before comparison.
	 */
	public static int compareWithDistance(Chunk prev, Chunk next, int distance) {
		int cmp = FastStringComparator.INSTANCE.compare(prev.getOriginId(),
				next.getOriginId());
		if (cmp != 0) {
			return cmp;
		}
		return prev.getFirstUnitIndex() + distance - next.getFirstUnitIndex();
	}

	/**
	 * Calculates the list of {@link Chunk}s for a list of units for one
	 * element. The chunks are calculated for all continuous subsequences of
	 * given chunk length, where sublists containing sentinel units are skipped
	 * (as they should never match anything).
	 */
	public static List<Chunk> calculateChunks(List<Unit> units, int chunkSize,
			ITokenElement element, int elementUnits) throws ConQATException {
		List<Chunk> result = new ArrayList<Chunk>();

		for (int i = chunkSize - 1; i < units.size(); ++i) {
			int first = i - (chunkSize - 1);
			MD5Digest digest = buildChunkHash(units, first, i);
			if (digest == null) {
				continue;
			}

			Unit firstUnit = units.get(first);
			Unit lastUnit = units.get(first + chunkSize - 1);

			int rawStartOffset = element.getUnfilteredOffset(firstUnit
					.getFilteredStartOffset());
			int rawEndOffset = element.getUnfilteredOffset(lastUnit
					.getFilteredEndOffset()) + 1;
			int rawStartLine = element
					.convertUnfilteredOffsetToLine(rawStartOffset);
			int rawEndLine = element
					.convertUnfilteredOffsetToLine(rawEndOffset - 1) + 1;

			Chunk chunk = new Chunk(element.getUniformPath(), digest,
					firstUnit.getIndexInElement(), rawStartLine, rawEndLine,
					rawStartOffset, rawEndOffset, elementUnits);

			result.add(chunk);
		}

		return result;
	}

	/**
	 * Calculates the chunk hash from the given sub list. Returns null if a
	 * sentinel unit is encountered.
	 */
	public static MD5Digest buildChunkHash(List<Unit> units, int first, int last) {
		MessageDigest md5 = Digester.getMD5();
		for (int j = first; j <= last; ++j) {
			Unit unit = units.get(j);
			if (unit.isSynthetic()) {
				// completely skip sentinel units (will not match anything)
				return null;
			}
			md5.update(unit.getContent().getBytes());
			md5.update(UNIT_SEPARATOR);
		}
		return new MD5Digest(md5);
	}
}
