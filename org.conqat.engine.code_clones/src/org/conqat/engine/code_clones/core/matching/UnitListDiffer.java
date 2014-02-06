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
package org.conqat.engine.code_clones.core.matching;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.assertion.CCSMPre;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.CounterSet;
import org.conqat.lib.commons.collections.ImmutablePair;

/**
 * Computes the {@link UnitListDelta} between unit lists of equal length. The
 * delta counts equal units, renames and differences.
 * 
 * For two unit lists left and right, the delta is defined as follows: For each
 * distinct unit in the left list, the corresponding units in the right list are
 * determined. All textually equal ones are counted as "equal". If a deviating
 * value occurs more than once, it is counted as renamed. If a deviating value
 * only occurs once, it is counted as different.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46673 $
 * @ConQAT.Rating GREEN Hash: 67A61A446AAC06523FBDD80FD9D0D19F
 */
public class UnitListDiffer {

	/**
	 * Compute delta on a collection of lists. The delta with the smallest
	 * similarity is returned.
	 * 
	 * @param unitLists
	 *            Must have at least two entries
	 */
	public static UnitListDelta computeDelta(Collection<List<String>> unitLists) {
		CCSMAssert.isTrue(unitLists.size() >= 2,
				"Need at least 2 lists to compute Delta");

		UnitListDelta maxDelta = null;

		for (ImmutablePair<List<String>, List<String>> pair : CollectionUtils
				.computeUnorderedPairs(unitLists)) {
			UnitListDelta delta = computeDelta(pair.getFirst(),
					pair.getSecond());

			if (maxDelta == null || isBiggerDelta(maxDelta, delta)) {
				maxDelta = delta;
			}
		}

		return maxDelta;
	}

	/** Returns whether the delta is bigger than maxDelta. */
	private static boolean isBiggerDelta(UnitListDelta maxDelta,
			UnitListDelta delta) {
		return delta.getSimilarity() < maxDelta.getSimilarity()
				|| (delta.getSimilarity() == maxDelta.getSimilarity() && delta
						.getEquality() < maxDelta.getEquality());
	}

	/** Compute delta on a pair of lists. Lists must have same size */
	private static UnitListDelta computeDelta(List<String> units1,
			List<String> units2) throws AssertionError {
		CCSMPre.isTrue(units1.size() == units2.size(),
				"Unit lists must have same length");

		CounterSet<ImmutablePair<String, String>> mappings = new CounterSet<ImmutablePair<String, String>>();
		Set<String> terms1 = new HashSet<String>();
		Set<String> terms2 = new HashSet<String>();
		for (int i = 0; i < units1.size(); i++) {
			String unit1 = units1.get(i);
			String unit2 = units2.get(i);
			mappings.inc(new ImmutablePair<String, String>(unit1, unit2));
			terms1.add(unit1);
			terms2.add(unit2);
		}

		UnitListDelta delta = new UnitListDelta();
		delta.calculateAndSetTermEquality(terms1, terms2);
		for (ImmutablePair<String, String> mapping : mappings.getKeys()) {
			int count = mappings.getValue(mapping);
			if (mapping.getFirst().equals(mapping.getSecond())) {
				delta.incEquals(count);
			} else if (count > 1) {
				delta.incRenames(count);
			} else {
				delta.incDifferences(1);
			}
		}

		CCSMAssert.isTrue(units1.size() == delta.getSize(),
				"Delta is inconsistent");
		return delta;
	}
}
