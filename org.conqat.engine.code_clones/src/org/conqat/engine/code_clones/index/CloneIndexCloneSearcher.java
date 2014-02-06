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
import java.util.List;

import org.conqat.engine.code_clones.core.Clone;
import org.conqat.engine.code_clones.core.CloneClass;
import org.conqat.engine.code_clones.index.report.ICloneClassReporter;
import org.conqat.engine.commons.findings.location.TextRegionLocation;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.persistence.store.StorageException;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.digest.Digester;

/**
 * This class implements the core search algorithm of the index-based clone
 * detection approach. It searches for clones in a list of {@link ChunkList}s
 * for a single file.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43764 $
 * @ConQAT.Rating GREEN Hash: E88803B1B139050CB302F7D659958E70
 */
public class CloneIndexCloneSearcher {

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

	/** Constructor. */
	public CloneIndexCloneSearcher(String originId,
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
		// pad with empty to simplify algorithm
		orderedChunks.add(new ChunkList());

		List<Chunk> headList = CollectionUtils.emptyList();
		List<Chunk> prevList = null;
		for (int startUnitIndex = 0; startUnitIndex < orderedChunks.size(); ++startUnitIndex) {
			prevList = headList;
			headList = orderedChunks.get(startUnitIndex);
			if (headList.isEmpty()
					|| ChunkUtils.isSubSet(headList, prevList, 1)) {
				continue;
			}

			List<Chunk> tailList = new ArrayList<Chunk>(headList);
			int cloneLength = chunkLength;
			for (int scanUnitIndex = startUnitIndex + 1; scanUnitIndex < orderedChunks
					.size(); ++scanUnitIndex, ++cloneLength) {
				tailList = scanForward(startUnitIndex, scanUnitIndex,
						cloneLength, headList, prevList, tailList);
				if (tailList == null) {
					break;
				}
			}
		}
	}

	/**
	 * Performs a forward scanning step. This checks if clones should be
	 * reported starting at the head list and ending before the current scanning
	 * position.
	 * 
	 * @param startUnitIndex
	 *            the index the current scanning phase starts at.
	 * @param scanUnitIndex
	 *            the index that is scanned in this step.
	 * @param cloneLength
	 *            the length of a clone that starts at the startUnitIndex and
	 *            ends before the scanUnitIndex.
	 * @param headList
	 *            the chunk list corresponding to the startUnitIndex.
	 * @param prevList
	 *            the chunk list directly before the headList (i.e. position
	 *            startUnitIndex-1).
	 * @param oldTailList
	 *            the old tail list, i.e. chunks resulting from intersecting the
	 *            unit lists between startUnitIndex and ending before
	 *            scanUnitIndex.
	 * @return the new tail list including the current scan position. Returns
	 *         null if the current scanning phase can be aborted (e.g. if the
	 *         tail list runs empty).
	 */
	private List<Chunk> scanForward(int startUnitIndex, int scanUnitIndex,
			int cloneLength, List<Chunk> headList, List<Chunk> prevList,
			List<Chunk> oldTailList) throws ConQATException {

		// update tail list via intersection
		List<Chunk> newTailList = ChunkUtils.intersect(oldTailList,
				orderedChunks.get(scanUnitIndex));

		// clone reporting only makes sense if the tail list is reduced (i.e.
		// not all clones can be prolonged)
		if (cloneLength >= minLength && newTailList.size() < oldTailList.size()) {
			Chunk first = ChunkUtils.findFirstChunkFor(originId, oldTailList);

			// stop scanning if no more chunks from current origin are present
			if (first == null) {
				return null;
			}

			// in case of multiple clones in the same file, we only want to
			// report the clone once
			boolean isFirstInOrigin = first.getFirstUnitIndex() == scanUnitIndex - 1;

			// if onlyStartingHere is active, we also must prevent the clone to
			// be reported for each scanned file
			boolean firstOriginCheck = !onlyStartingHere
					|| oldTailList.get(0).getOriginId().equals(originId);

			if (isFirstInOrigin && firstOriginCheck) {
				reportCloneClass(headList, oldTailList, scanUnitIndex - 1
						- startUnitIndex);
			}
		}

		// we can stop scanning if either the tail list runs empty, or is
		// completely contained in prevList, as clones then would have been
		// reported in the scan starting from there.
		if (newTailList.size() < 2
				|| ChunkUtils.isSubSet(newTailList, prevList, 1 + scanUnitIndex
						- startUnitIndex)) {
			return null;
		}
		return newTailList;
	}

	/**
	 * Returns all clones between both of the lists. The <code>tailList</code>
	 * must be a subset of the <code>headList</code>. The unit indices between
	 * both lists must differ by exactly <code>unitSkip</code>.
	 * <p>
	 * The lists put into this method must be sorted!
	 */
	private void reportCloneClass(List<Chunk> headList, List<Chunk> tailList,
			int unitSkip) throws StorageException, ConQATException {

		int lengthInUnits = chunkLength + unitSkip;
		CloneClass cloneClass = new CloneClass(lengthInUnits,
				reporter.provideId());

		int headIndex = 0;
		for (Chunk tail : tailList) {
			headIndex = CloneIndex.advanceHeadIndex(tail, headList, headIndex,
					unitSkip);
			Chunk head = headList.get(headIndex);
			++headIndex;

			constructClone(head, tail, lengthInUnits, cloneClass);
		}

		// clone classes can have cardinality of 1, if all contained clones
		// are considered equal by the set that stores a clone classes'
		// clones. this can happen if clones only differ
		// in start and end units that are located on the same lines.
		if (cloneClass.getClones().size() >= 2) {
			reporter.report(cloneClass);
		}
	}

	/**
	 * Constructs a clone from the given head and tail chunks and adds it to the
	 * clone class.
	 */
	private void constructClone(Chunk head, Chunk tail, int lengthInUnits,
			CloneClass cloneClass) {

		// We respect only the head and tail chunks for the fingerprint.
		// So if there are different clones where the first and last 5 (or
		// chunk-length) units the same, they will have the same
		// fingerprint. This is considered unlikely.
		String fingerprintBase = head.getChunkHash().toString()
				+ tail.getChunkHash().toString();
		String fingerprint = Digester.createMD5Digest(fingerprintBase);

		TextRegionLocation location = new TextRegionLocation(
				head.getOriginId(), head.getOriginId(),
				head.getRawStartOffset(), tail.getRawEndOffset() - 1,
				head.getFirstRawLineNumber(), tail.getLastRawLineNumber() - 1);
		new Clone(reporter.provideId(), cloneClass, location,
				head.getFirstUnitIndex(), lengthInUnits, fingerprint);
	}
}