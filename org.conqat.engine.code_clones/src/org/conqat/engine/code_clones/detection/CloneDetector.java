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
package org.conqat.engine.code_clones.detection;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.code_clones.core.CloneClass;
import org.conqat.engine.code_clones.core.Unit;
import org.conqat.engine.code_clones.detection.suffixtree.CloneDetectingSuffixTree;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.collections.CounterSet;
import org.conqat.lib.commons.digest.Digester;
import org.conqat.lib.commons.digest.MD5Digest;

/**
 * Exact clone detection based on suffix trees. Does not find approximate
 * (a.k.a. gapped) clones.
 * 
 * @author $Author: juergens $
 * @version $Rev: 34670 $
 * @ConQAT.Rating GREEN Hash: 8D598522181ED686A457286160078C1B
 */
@AConQATProcessor(description = "Suffix tree based exact clone detector.")
public class CloneDetector extends CloneDetectorBase {

	/** Use the (safe) unit trimming heuristic. */
	private boolean isTrimUnits = true;

	/** MD5 digester used for unit trimming. */
	private final MessageDigest md5 = Digester.getMD5();

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "trim-units", maxOccurrences = 1, description = "Controls whether unit trimming is activated. "
			+ "Unit trimming reduces the amount of units which have to be inspected during clone detection and usually this should be kept enabled.")
	public void setTrimUnits(
			@AConQATAttribute(name = "value", description = "True enables unit trimming, false disables it. The default is enabled.") boolean value) {
		isTrimUnits = value;
	}

	/** Performs detection of non-gapped clones */
	@Override
	protected List<CloneClass> detectClones() throws ConQATException {
		if (isTrimUnits) {
			trimUnits();
		}

		long start = System.currentTimeMillis();
		CloneDetectingSuffixTree cdstree = new CloneDetectingSuffixTree(units);
		long treeConstruction = System.currentTimeMillis() - start;
		getLogger().debug("Suffix tree created. Detecting clones...");
		getLogger().info("Tree construction: " + treeConstruction + " ms");

		start = System.currentTimeMillis();
		CloneConsumer cloneConsumer = new CloneConsumer();
		cdstree.findClones(minLength, cloneConsumer);
		long detection = System.currentTimeMillis() - start;
		getLogger().debug("Clone detection finished!");
		getLogger().info("Detection construction: " + detection + " ms");

		return cloneConsumer.getCloneClasses();
	}

	/**
	 * Applies unit trimming to the {@link CloneDetectorBase#units} list. The
	 * goal is to conservatively remove units which will not appear in a clone
	 * in any case. The idea is to break the unit sequence into chunks
	 * (subsequences) of equal size and keep only units from those chunks which
	 * appear at least twice. This is safe, as long as chunks are small enough,
	 * so that each clone would contain at least one chunk (and thus if the
	 * chunk only appears once, this can not be a clone). To make this work
	 * correctly, the chunk size may be at most ceil(minSize/2).
	 */
	private void trimUnits() {
		// as the chunk size is a linear factor in the first calculation
		// (chunkCounter), it should not be too large (e.g. 100 would not be a
		// good idea), so we limit it (arbitrarily).
		int chunkSize = Math.min((minLength + 1) / 2, 7);

		CounterSet<MD5Digest> chunkCount = determineChunkCount(chunkSize);

		int numChunks = units.size() / chunkSize;
		boolean[] keep = fillKeepArray(chunkSize, numChunks, chunkCount);

		List<Unit> trimmed = performTrimming(chunkSize, numChunks, keep);

		getLogger().info(
				"Unit trimming reduced " + units.size() + " units to "
						+ trimmed.size());
		units = trimmed;
	}

	/**
	 * Fills a counter set, such that it stores for each chunk how often it
	 * appears. To make this efficient, we only manage MD5 hashes instead of the
	 * chunks, so chunks might be mapped to each other.
	 */
	private CounterSet<MD5Digest> determineChunkCount(int chunkSize) {
		CounterSet<MD5Digest> chunkCount = new CounterSet<MD5Digest>();
		for (int i = chunkSize; i < units.size(); ++i) {
			MD5Digest digest = getChunkDigest(i - chunkSize, chunkSize);
			if (digest != null) {
				chunkCount.inc(digest);
			}
		}
		return chunkCount;
	}

	/**
	 * Fills an array which determines for each chunk whether this should be
	 * kept (i.e. there are multiple instances) or not. The array will be one
	 * element larger as required with the last element being <code>true</code>,
	 * as the remaining units (which are not enough for an own chunk) are always
	 * kept.
	 */
	private boolean[] fillKeepArray(int chunkSize, int numChunks,
			CounterSet<MD5Digest> chunkCount) {
		boolean[] keep = new boolean[numChunks + 1];
		for (int i = 0; i < numChunks; ++i) {
			MD5Digest digest = getChunkDigest(i * chunkSize, chunkSize);
			if (digest != null) {
				keep[i] = chunkCount.getValue(digest) > 1;
			} // else false, which is automatically initialized by java
		}
		keep[numChunks] = true;
		return keep;
	}

	/**
	 * Returns the digest for a chunk or null if this guaranteed to not match
	 * any other chunk (e.g. if a sentinel unit is encountered).
	 */
	private MD5Digest getChunkDigest(int start, int chunkSize) {
		md5.reset();
		for (int i = 0; i < chunkSize; ++i) {
			Unit unit = units.get(start + i);
			if (unit instanceof SentinelUnit) {
				// completely skip sentinel units (will not match anything)
				return null;
			}
			md5.update(unit.getContent().getBytes());
		}
		return new MD5Digest(md5);
	}

	/**
	 * Performs the actual trimming based on which chunks are to be kept (keep
	 * array).
	 */
	private List<Unit> performTrimming(int chunkSize, int numChunks,
			boolean[] keep) {
		List<Unit> trimmed = new ArrayList<Unit>();

		// if less units than chunkSiuze, no trimming required
		if (numChunks <= 0) {
			return units;
		}

		int readPosition = 0;

		// always keep first chunk as this simplifies the loop below
		readPosition = copyChunk(trimmed, chunkSize, readPosition);

		// determines whether the previous chunk was included
		boolean prevIncluded = true;

		for (int i = 1; i < numChunks; ++i) {

			// we include a chunk if it should be kept (multiple occurrences) or
			// one of its neighbors is kept (as then the clone might also cover
			// part of the chunk)
			boolean include = keep[i - 1] || keep[i] || keep[i + 1];
			if (include) {
				if (!prevIncluded) {
					// we need artificial unit as separation; as the unit will
					// not leave the detector, a null-file may be used.
					trimmed.add(new SentinelUnit(null));
				}
				readPosition = copyChunk(trimmed, chunkSize, readPosition);
			} else { // skip
				readPosition += chunkSize;
			}
			prevIncluded = include;
		}

		// remainder
		while (readPosition < units.size()) {
			trimmed.add(units.get(readPosition++));
		}

		return trimmed;
	}

	/**
	 * Copies a chunk from the units list to the given (trimmed) list. The new
	 * read position is returned.
	 */
	private int copyChunk(List<Unit> trimmed, int chunkSize, int readPosition) {
		for (int i = 0; i < chunkSize; ++i) {
			trimmed.add(units.get(readPosition++));
		}
		return readPosition;
	}

}