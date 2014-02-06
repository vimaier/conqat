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
package org.conqat.engine.resource.text.filter.base;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.conqat.lib.commons.collections.CollectionUtils;

/**
 * A deletion of a piece of text determined by offsets into a string.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 44478 $
 * @ConQAT.Rating GREEN Hash: 7A1E9D713F7B71DDE80BDDA1B88D8269
 */
public class Deletion implements Comparable<Deletion>, Serializable {

	/** Serial version UID. */
	private static final long serialVersionUID = 1;

	/** Start position of deletion (inclusive). */
	private final int startOffset;

	/** End position of deletion (exclusive). */
	private final int endOffset;

	/**
	 * If this is true, the deletion is a gap, i.e. clones and findings should
	 * not cross the deletion.
	 */
	private final boolean gap;

	/** Constructor. */
	public Deletion(int startOffset, int endOffset, boolean gap) {
		this.startOffset = startOffset;
		this.endOffset = endOffset;
		this.gap = gap;
	}

	/** Returns start position of deletion (inclusive). */
	public int getStartOffset() {
		return startOffset;
	}

	/** Returns end position of deletion (exclusive). */
	public int getEndOffset() {
		return endOffset;
	}

	/**
	 * If this returns true, the deletion is a gap, i.e. clones and findings
	 * should not cross the deletion.
	 */
	public boolean isGap() {
		return gap;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Sorts by {@link #startOffset}.
	 */
	@Override
	public int compareTo(Deletion other) {
		return startOffset - other.startOffset;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Used for debugging and logging.
	 */
	@Override
	public String toString() {
		String gapString = "gap";
		if (!gap) {
			gapString = "no gap";
		}
		return startOffset + "-" + endOffset + " (" + gapString + ")";
	}

	/**
	 * Returns a list of deletions that is equivalent (i.e. the same intervals
	 * are excluded), but the intervals are in ascending order and do not
	 * overlap. Gapped deletions are dominant during merging, e.g. if a
	 * non-gapped and a gapped deletion are merged, the new deletion is gapped.
	 */
	public static List<Deletion> compactDeletions(List<Deletion> deletions) {
		deletions = CollectionUtils.sort(deletions);

		List<Deletion> result = new ArrayList<Deletion>();

		for (int i = 0; i < deletions.size(); ++i) {
			Deletion deletion = deletions.get(i);
			int startOffset = deletion.startOffset;
			int endOffset = deletion.endOffset;
			boolean gap = deletion.gap;

			// merging possible?
			while (i + 1 < deletions.size()
					&& deletions.get(i + 1).startOffset <= endOffset) {
				Deletion deletion2 = deletions.get(i + 1);
				endOffset = Math.max(endOffset, deletion2.endOffset);
				gap = gap || deletion2.gap;
				i += 1;
			}

			result.add(new Deletion(startOffset, endOffset, gap));
		}
		return result;
	}
}
