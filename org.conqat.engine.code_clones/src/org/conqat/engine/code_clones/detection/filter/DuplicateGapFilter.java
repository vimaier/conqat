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
package org.conqat.engine.code_clones.detection.filter;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.conqat.engine.code_clones.core.CloneClass;
import org.conqat.engine.code_clones.core.utils.CloneUtils;
import org.conqat.engine.code_clones.core.utils.ECloneClassComparator;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * {@ConQAT.Doc}
 * 
 * @author juergens
 * @author $Author: juergens $
 * @version $Rev: 34670 $
 * @ConQAT.Rating GREEN Hash: 3BB188706D54B5EDFD6E1428C2849BB1
 */
@AConQATProcessor(description = ""
		+ "Filters out clone classes that only contain gaps that are completely"
		+ "contained in other clone classes. This is useful if gapped clone detection is"
		+ "performed in order to find inconsistencies, since the number of duplicate"
		+ "occurrences of a single gap in the clone report is then reduced."
		+ "In order to determine a minimal set of clone classes that contain all gaps,"
		+ "an exaustive search would be required. Instead, this processor uses a"
		+ "heuristic approach: It sorts clone classes in descending order according to"
		+ "their number of gaps. Then clone classes are filtered out whose gaps are"
		+ "already contained in clone classes earlier in the list."
		+ "Ungapped clones are not filtered out. Use {@link UngappedClonesFilter} to"
		+ "remove ungapped clones.")
public class DuplicateGapFilter extends CloneClassFilterBase {

	/** Set that stores known gaps */
	private final Set<String> knownGaps = new HashSet<String>();

	/** {@inheritDoc} */
	@Override
	protected boolean filteredOut(CloneClass cloneClass) {
		// do not filter ungapped clones
		if (cloneClass.getGapCount() == 0) {
			return false;
		}

		Set<String> gapIdentifier = CloneUtils.gapIdentifierFor(cloneClass);

		if (knownGaps.containsAll(gapIdentifier)) {
			return true;
		}

		knownGaps.addAll(gapIdentifier);
		return false;
	}

	/** Sorts clone classes with more gaps to the top */
	@Override
	protected void sort(List<CloneClass> cloneClasses) {
		Collections.sort(cloneClasses, new GapCountSizeComparator());
	}

	/**
	 * Compares {@link CloneClass}es by GapCount. If clone classes have the same
	 * gap count, they are compared by their normalized length.
	 */
	public class GapCountSizeComparator implements Comparator<CloneClass> {

		/** {@inheritDoc} */
		@Override
		public int compare(CloneClass cloneClass1, CloneClass cloneClass2) {
			int gapCompareValue = ECloneClassComparator.GAPCOUNT.compare(
					cloneClass1, cloneClass2);
			if (gapCompareValue != 0) {
				return gapCompareValue;
			}

			return ECloneClassComparator.NORMALIZED_LENGTH.compare(cloneClass1,
					cloneClass2);
		}

	}

}