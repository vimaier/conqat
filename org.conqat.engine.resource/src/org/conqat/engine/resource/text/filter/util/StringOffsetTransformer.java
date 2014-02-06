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
package org.conqat.engine.resource.text.filter.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.conqat.engine.resource.text.filter.base.Deletion;
import org.conqat.lib.commons.assertion.CCSMPre;
import org.conqat.lib.commons.region.Region;

/**
 * A transformation class for filtering strings. This class can perform forward
 * transformation of string (i.e. delete the corresponding parts) and backwards
 * transformation of offsets. The transformation itself is initialized by a list
 * of deletions.
 * <p>
 * The transformation is represented by two arrays of corresponding offsets into
 * both the filtered and unfiltered (raw) string. These offsets are stored for
 * all end positions of a deletion.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46849 $
 * @ConQAT.Rating GREEN Hash: A539BCAD248FADAA79D639EA3DAF541A
 */
public class StringOffsetTransformer {

	/**
	 * Selected offsets into the raw string. Corresponding to
	 * {@link #filteredOffset}.
	 */
	private int[] rawOffset;

	/**
	 * Selected offsets into the filtered string. Corresponding to
	 * {@link #rawOffset}.
	 */
	private int[] filteredOffset;

	/**
	 * Stores for the deletion ending at the offset in the corresponding
	 * position of {@link #rawOffset} and {@link #filteredOffset}, whether this
	 * is a gap.
	 */
	private boolean[] gap;

	/** Constructor. */
	public StringOffsetTransformer(List<Deletion> deletions) {
		deletions = Deletion.compactDeletions(deletions);

		int start = 0;
		// check if the string starts with a deletion, as we have to initialize
		// the arrays differently in this case
		if (!deletions.isEmpty() && deletions.get(0).getStartOffset() == 0) {
			start += 1;
			initOffsetArrays(deletions.size());
			rawOffset[0] = deletions.get(0).getEndOffset();
			filteredOffset[0] = 0;
			gap[0] = deletions.get(0).isGap();
		} else {
			initOffsetArrays(deletions.size() + 1);
			rawOffset[0] = 0;
			filteredOffset[0] = 0;
			gap[0] = false;
		}

		// fill the rawOffset and filteredOffset arrays by locating each end of
		// a deleted interval (actually the first non-deleted index after an
		// interval) and the corresponding position in the filtered string. The
		// "-start" terms are just needed because we start our iteration at
		// start (and not 0).
		int filteredLength = 0;
		for (int i = start; i < deletions.size(); ++i) {
			Deletion deletion = deletions.get(i);
			rawOffset[i + 1 - start] = deletion.getEndOffset();
			filteredLength += deletion.getStartOffset() - rawOffset[i - start];
			filteredOffset[i + 1 - start] = filteredLength;
			gap[i + 1 - start] = deletion.isGap();
		}
	}

	/** Inits the offset arrays to the given size. */
	private void initOffsetArrays(int size) {
		rawOffset = new int[size];
		filteredOffset = new int[size];
		gap = new boolean[size];
	}

	/** Returns the string after transformation. */
	public String filterString(String s) {
		StringBuilder sb = new StringBuilder();
		for (int i = 1; i < rawOffset.length; ++i) {
			int length = filteredOffset[i] - filteredOffset[i - 1];
			sb.append(s.substring(rawOffset[i - 1], rawOffset[i - 1] + length));
		}
		sb.append(s.substring(rawOffset[rawOffset.length - 1]));

		return sb.toString();
	}

	/** Converts a filtered offset to an unfiltered offset. */
	public int getUnfilteredOffset(int offset) {
		int index = Arrays.binarySearch(filteredOffset, offset);
		if (index < 0) {
			// we need insertion point -1
			index = -index - 2;
		}

		return rawOffset[index] + (offset - filteredOffset[index]);
	}

	/**
	 * Converts an unfiltered offset to a filtered offset. If the offset was
	 * filtered, the first offset after the filtered region is returned (which
	 * might be one after the last offset in the filtered string, if the
	 * provided offset in a filtered tail region).
	 */
	public int getFilteredOffset(int unfilteredOffset) {
		int index = Arrays.binarySearch(rawOffset, unfilteredOffset);
		if (index < 0) {
			// we need insertion point -1
			index = -index - 2;
		}

		if (index < 0) {
			return 0;
		}

		int validRange = Integer.MAX_VALUE;
		if (index + 1 < filteredOffset.length) {
			validRange = filteredOffset[index + 1] - filteredOffset[index];
		}

		int delta = (unfilteredOffset - rawOffset[index]);
		if (delta <= validRange) {
			return filteredOffset[index] + delta;
		}

		// Already in next filter gap, so move at end of filter gap.
		// The +1 is valid, as we had the check before
		return filteredOffset[index + 1];
	}

	/** Returns whether an offset is contained in a deleted region. */
	public boolean isFilteredOffset(int offset) {
		int index = Arrays.binarySearch(rawOffset, offset);
		if (index < 0) {
			// we need insertion point -1
			index = -index - 2;
		}

		if (index < 0) {
			return true;
		}

		if (index >= rawOffset.length - 1) {
			return false;
		}

		int validRange = rawOffset[index] + filteredOffset[index + 1]
				- filteredOffset[index];
		return offset >= validRange;
	}

	/**
	 * Returns whether there is a filtering gap between the two offsets. The
	 * first offset must be strictly smaller than the second offset. Both
	 * offsets should be "filtered offsets".
	 */
	public boolean isFilterGapBetween(int firstOffset, int secondOffset) {
		if (firstOffset >= secondOffset) {
			CCSMPre.fail("First offset must be smaller than second offset: "
					+ firstOffset + " vs. " + secondOffset);
		}

		int index = Arrays.binarySearch(filteredOffset, firstOffset);
		if (index < 0) {
			// we need real insertion point here
			index = -index - 1;
		} else {
			// as we store deletion end positions, we are interested in the next
			// gap and all following (not the one just ending here)
			index += 1;
		}

		while (index < filteredOffset.length
				&& filteredOffset[index] <= secondOffset) {
			if (gap[index]) {
				return true;
			}
			index += 1;
		}

		return false;
	}

	/** Extracts the filtered regions from this transformer. */
	public List<Region> extractFilteredRegions() {
		List<Region> regions = new ArrayList<Region>();

		// (rawOffset[0] != 0) if (and only if) there is a deletion at the
		// beginning
		if (rawOffset[0] != 0) {
			regions.add(new Region(0, rawOffset[0] - 1));
		}

		for (int i = 1; i < rawOffset.length; ++i) {
			regions.add(new Region(rawOffset[i - 1] + filteredOffset[i]
					- filteredOffset[i - 1], rawOffset[i] - 1));
		}
		return regions;
	}
}