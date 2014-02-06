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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.conqat.engine.code_clones.core.Clone;
import org.conqat.engine.code_clones.core.CloneClass;
import org.conqat.engine.code_clones.index.report.ICloneClassReporter;
import org.conqat.engine.commons.findings.location.TextRegionLocation;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.persistence.store.StorageException;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.ListMap;
import org.conqat.lib.commons.region.Region;

/**
 * This class implements the core search algorithm of the index-based gapped
 * clone detection approach. It searches for clones in a list of
 * {@link ChunkList}s for a single file.
 * 
 * The implemented approach roughly follows the paper
 * "Frequency and Risks of Changes to Clones" from Goede and Koschke.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 45864 $
 * @ConQAT.Rating GREEN Hash: E88803B1B139050CB302F7D659958E70
 */
public class CloneIndexGappedCloneSearcher {

	/** The chunk length used. */
	private final int chunkLength;

	/** The origin ID of the file currently searched for clones. */
	private final String originId;

	/** The reported used for clone class construction/reporting. */
	private final ICloneClassReporter reporter;

	/**
	 * If this is true, only clone classes for which this origin contributes the
	 * first clone instance are reported. If this is false, all clone classes
	 * are reported. This should be set to true when querying all origins
	 * consecutively to avoid duplicate clone groups.
	 */
	private final boolean onlyStartingHere;

	/** Minimal length of reported clones. */
	private final int minLength;

	/** The list of ordered chunks. */
	private final List<ChunkList> orderedChunks;

	/** All clone classes created. */
	private final List<CloneClass> cloneClasses = new ArrayList<CloneClass>();

	/**
	 * Maps clone locations to clones. This is used for merging clone pairs to
	 * larger classes.
	 */
	private final ListMap<String, Clone> locationToClone = new ListMap<String, Clone>();

	/** Constructor. */
	public CloneIndexGappedCloneSearcher(String originId,
			ICloneClassReporter reporter, boolean onlyStartingHere,
			int minLength, List<ChunkList> orderedChunks, int chunkLength) {
		this.originId = originId;
		this.reporter = reporter;
		this.onlyStartingHere = onlyStartingHere;
		this.minLength = minLength;
		this.orderedChunks = orderedChunks;
		this.chunkLength = chunkLength;
	}

	/** Reports clones based on a list of ordered chunks. */
	public void reportClones() throws StorageException, ConQATException {

		ListMap<String, ChunkPair> chunkPairsBySecondOrigin = new ListMap<String, ChunkPair>();

		for (int i = 0; i < orderedChunks.size(); ++i) {
			ChunkList chunkList = orderedChunks.get(i);
			if (chunkList.isEmpty()) {
				continue;
			}

			Chunk originChunk = findOriginChunk(chunkList, i);
			for (Chunk other : chunkList) {
				if (other == originChunk) {
					continue;
				}
				chunkPairsBySecondOrigin.add(other.getOriginId(),
						new ChunkPair(originChunk, other));
			}
		}

		for (String otherOrigin : chunkPairsBySecondOrigin.getKeys()) {
			List<ChunkPair> sortedPairs = CollectionUtils
					.sort(chunkPairsBySecondOrigin.getCollection(otherOrigin));
			reportClonePairs(sortedPairs);
		}
	}

	private void reportClonePairs(List<ChunkPair> sortedPairs)
			throws ConQATException {
		OUTER: for (int i = 0; i < sortedPairs.size(); ++i) {
			for (int j = i - 1; j >= 0; --j) {
				if (sortedPairs.get(i).extendsPair(sortedPairs.get(j))) {
					continue OUTER;
				}
			}
		}

		for (ChunkPair chunkPair : sortedPairs) {
			if (chunkPair.lengthInUnits > chunkLength) {
				continue;
			}
			createPair(chunkPair);
		}

		// join clone classes
		for (String key : locationToClone.getKeys()) {
			List<Clone> clones = locationToClone.getCollection(key);

			int first = 0;
			while (first < clones.size()
					&& clones.get(first).getCloneClass() == null) {
				first += 1;
			}
			if (first >= clones.size()) {
				continue;
			}
			CloneClass cloneClass = clones.get(first).getCloneClass();

			Set<String> locations = new HashSet<String>();
			for (Clone clone : cloneClass.getClones()) {
				locations.add(clone.getLocation().toLocationString());
			}
			for (int i = first + 1; i < clones.size(); ++i) {
				CloneClass mergeCloneClass = clones.get(i).getCloneClass();
				// merge clone class can get null due to earlier purging of
				// clone class
				if (mergeCloneClass == cloneClass || mergeCloneClass == null) {
					continue;
				}
				for (Clone clone : new ArrayList<Clone>(
						mergeCloneClass.getClones())) {
					if (locations.add(clone.getLocation().toLocationString())) {
						cloneClass.add(clone);
					} else {
						mergeCloneClass.remove(clone);
					}
				}
			}
		}

		for (CloneClass cloneClass : cloneClasses) {
			// TODO (BH): Respect onlyStartingHere
			if (cloneClass.size() < 2) {
				continue;
			}

			reporter.report(cloneClass);
		}
	}

	private void createPair(ChunkPair firstChunkPair) throws ConQATException {
		ChunkPair lastChunkPair = firstChunkPair;
		while (lastChunkPair.extension != null) {
			lastChunkPair = lastChunkPair.extension;
		}

		// TODO (BH): All this code is fishy at least...
		int length = lastChunkPair.lengthInUnits;
		if (length < minLength) {
			return;
		}

		CloneClass cloneClass = new CloneClass(length, reporter.provideId());
		Clone clone1 = createClone(firstChunkPair.originChunk,
				lastChunkPair.originChunk, cloneClass);
		Clone clone2 = createClone(firstChunkPair.otherChunk,
				lastChunkPair.otherChunk, cloneClass);
		for (int i = 0; i < lastChunkPair.gapCount; ++i) {
			clone1.addGap(new Region(i, i + 1));
			clone2.addGap(new Region(i, i + 1));
		}

		cloneClasses.add(cloneClass);
	}

	private Clone createClone(Chunk firstChunk, Chunk lastChunk,
			CloneClass cloneClass) {
		TextRegionLocation location = new TextRegionLocation(
				firstChunk.getOriginId(), firstChunk.getOriginId(),
				firstChunk.getRawStartOffset(), lastChunk.getRawEndOffset(),
				firstChunk.getFirstRawLineNumber(),
				lastChunk.getLastRawLineNumber());
		// TODO(BH) fungerprint
		String fingerprint = "FOO/TODO";
		Clone clone = new Clone(reporter.provideId(), cloneClass, location,
				firstChunk.getFirstUnitIndex(), lastChunk.getFirstUnitIndex()
						- firstChunk.getFirstUnitIndex() + chunkLength,
				fingerprint);
		locationToClone.add(location.toLocationString(), clone);
		return clone;
	}

	private Chunk findOriginChunk(ChunkList chunkList, int index) {
		for (Chunk chunk : chunkList) {
			if (chunk.getFirstUnitIndex() == index
					&& originId.equals(chunk.getOriginId())) {
				return chunk;
			}
		}
		throw new AssertionError("This should not be possible!");
	}

	private class ChunkPair implements Comparable<ChunkPair> {

		private final Chunk originChunk;

		private final Chunk otherChunk;

		private int lengthInUnits = chunkLength;

		private int gapCount = 0;

		private ChunkPair extension = null;

		/** Constructor. */
		public ChunkPair(Chunk originChunk, Chunk otherChunk) {
			this.originChunk = originChunk;
			this.otherChunk = otherChunk;
		}

		public boolean extendsPair(ChunkPair toBeExtended) {
			if (toBeExtended.extension != null) {
				return false;
			}

			int originDiff = originChunk.getFirstUnitIndex()
					- toBeExtended.originChunk.getFirstUnitIndex();
			int otherDiff = otherChunk.getFirstUnitIndex()
					- toBeExtended.otherChunk.getFirstUnitIndex();

			if (originDiff <= 0 || otherDiff <= 0) {
				return false;
			}

			int distance = Math.max(originDiff, otherDiff) - chunkLength;
			// TODO (BH): Magic number ahead
			if (distance > toBeExtended.lengthInUnits || distance > 6) {
				return false;
			}

			toBeExtended.extension = this;
			lengthInUnits = toBeExtended.lengthInUnits + distance + chunkLength;

			gapCount = toBeExtended.gapCount;
			if (distance > 1) {
				gapCount += 1;
			}

			return true;
		}

		/**
		 * {@inheritDoc}
		 * <p>
		 * Sort by unit index of {@link #originChunk}, then by unit index of #
		 * {@link CloneIndexGappedCloneSearcher}.
		 */
		@Override
		public int compareTo(ChunkPair other) {
			int firstIndex = originChunk.getFirstUnitIndex()
					- other.originChunk.getFirstUnitIndex();
			if (firstIndex != 0) {
				return firstIndex;
			}

			return otherChunk.getFirstUnitIndex()
					- other.otherChunk.getFirstUnitIndex();
		}

		/** {@inheritDoc} */
		@Override
		public String toString() {
			return originChunk.getOriginId() + ":"
					+ originChunk.getFirstRawLineNumber() + "->"
					+ otherChunk.getOriginId() + ":"
					+ otherChunk.getFirstRawLineNumber();
		}
	}
}
