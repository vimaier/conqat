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

import org.conqat.lib.commons.digest.MD5Digest;

/**
 * Class for describing basic information for a chunk of units. This class is
 * immutable.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 45469 $
 * @ConQAT.Rating RED Hash: B469C4BF9822DA28377704010F7427FD
 */
public class Chunk {

	/** The origin of the chunk (e.g. a filename). */
	private final String originId;

	/** Hash value of the chunk. */
	private final MD5Digest chunkHash;

	/** Index of first unit in the chunk (zero-based). */
	private final int firstUnitIndex;

	/** First raw line of the chunk (1-based, inclusive). */
	private final int firstRawLineNumber;

	/** Last raw line of the chunk (1-based, exclusive). */
	private final int lastRawLineNumber;

	/** Raw start offset of the chunk (zero-based, inclusive). */
	private final int rawStartOffset;

	/** Raw end offset of the chunk (zero-based, exclusive). */
	private final int rawEndOffset;

	/** The number of total units in the element of the chunk (e.g. the file). */
	private final int elementUnits;

	/** Constructor. */
	public Chunk(String originId, MD5Digest chunkHash, int firstUnitIndex,
			int firstRawLineNumber, int lastRawLineNumber, int rawStartOffset,
			int rawEndOffset) {
		this.originId = originId;
		this.chunkHash = chunkHash;
		this.firstUnitIndex = firstUnitIndex;
		this.firstRawLineNumber = firstRawLineNumber;
		this.lastRawLineNumber = lastRawLineNumber;
		this.rawStartOffset = rawStartOffset;
		this.rawEndOffset = rawEndOffset;

		// TODO (BH): I think this is dangerous. Either document this in
		// elementUnits (i.e. that this may be negative), or remove this
		// constructor completely (which I would prefer).
		this.elementUnits = -1;
	}

	/** Constructor. */
	public Chunk(String originId, MD5Digest chunkHash, int firstUnitIndex,
			int firstRawLineNumber, int lastRawLineNumber, int rawStartOffset,
			int rawEndOffset, int unitCount) {
		this.originId = originId;
		this.chunkHash = chunkHash;
		this.firstUnitIndex = firstUnitIndex;
		this.firstRawLineNumber = firstRawLineNumber;
		this.lastRawLineNumber = lastRawLineNumber;
		this.rawStartOffset = rawStartOffset;
		this.rawEndOffset = rawEndOffset;
		this.elementUnits = unitCount;
	}

	/** Returns the origin of the chunk (for example the name of the file). */
	public String getOriginId() {
		return originId;
	}

	/** Returns the hash value of the chunk. */
	public MD5Digest getChunkHash() {
		return chunkHash;
	}

	/** Returns the index of first unit in the chunk. */
	public int getFirstUnitIndex() {
		return firstUnitIndex;
	}

	/** Returns the first raw line of the chunk (1-based, exclusive). */
	public int getFirstRawLineNumber() {
		return firstRawLineNumber;
	}

	/** Returns the last raw line of the chunk (1-based, exclusive). */
	public int getLastRawLineNumber() {
		return lastRawLineNumber;
	}

	/** Returns the raw start offset of the chunk (zero-based, inclusive). */
	public int getRawStartOffset() {
		return rawStartOffset;
	}

	/** Returns the raw end offset of the chunk (zero-based, exclusive). */
	public int getRawEndOffset() {
		return rawEndOffset;
	}

	/** Returns the total number of units in the origin element of this chunk */
	public int getElementUnits() {
		return elementUnits;
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Chunk)) {
			return false;
		}
		Chunk other = (Chunk) obj;
		return other.originId.equals(originId)
				&& other.chunkHash.equals(chunkHash)
				&& other.firstUnitIndex == firstUnitIndex
				&& other.firstRawLineNumber == firstRawLineNumber
				&& other.lastRawLineNumber == lastRawLineNumber
				&& other.rawStartOffset == rawStartOffset
				&& other.rawEndOffset == rawEndOffset;
	}

	/**
	 * Hash code is only based on originId, chunk hash and first unit, as
	 * remaining fields typically should depend on these fields.
	 */
	@Override
	public int hashCode() {
		return originId.hashCode() + 13 * chunkHash.hashCode() + 413
				* firstUnitIndex;
	}
}