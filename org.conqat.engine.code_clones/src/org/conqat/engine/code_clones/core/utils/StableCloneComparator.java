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
package org.conqat.engine.code_clones.core.utils;

import java.util.Comparator;

import org.conqat.engine.code_clones.core.Clone;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Comparator that works on clones in a single clone class. It produces a stable
 * order. Other properties of the order (e.g. which clone comes first) are not
 * specified.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43764 $
 * @ConQAT.Rating GREEN Hash: FF97F7F6B6E9B3FB7A887392CED5F17D
 */
public class StableCloneComparator implements Comparator<Clone> {

	/** Singleton instance of comparator that is strict */
	public static StableCloneComparator INSTANCE = new StableCloneComparator(
			false);

	/** Singleton instance of comparator that is lenient */
	public static StableCloneComparator LENIENT_INSTANCE = new StableCloneComparator(
			true);

	/**
	 * If not in lenient mode, an {@link AssertionError} is thrown, if no stable
	 * order can be found. Default is false.
	 */
	private final boolean lenient;

	/** Private constructor to enforce use of static instances */
	private StableCloneComparator(boolean lenient) {
		this.lenient = lenient;
	}

	/** {@inheritDoc} */
	@Override
	public int compare(Clone c1, Clone c2) {
		CCSMAssert.isTrue(c1.getCloneClass() == c2.getCloneClass(),
				"Expecting to sort clones from the same clone class");

		// compare filenames
		int originOrder = c1.getUniformPath().compareTo(c2.getUniformPath());
		if (originOrder != 0) {
			return originOrder;
		}

		// if filenames are equal, compare start positions
		int startUnitOrder = c1.getStartUnitIndexInElement()
				- c2.getStartUnitIndexInElement();
		if (startUnitOrder != 0) {
			return startUnitOrder;
		}

		// we compare start offsets in addition to units. If clones are imported
		// from other tools, they might only have offsets but not unit indexes
		int startOffsetOrder = c1.getLocation().getRawStartOffset()
				- c2.getLocation().getRawStartOffset();
		if (startOffsetOrder != 0) {
			return startOffsetOrder;
		}

		// compare lengths in units (even in same file and at same start
		// positions we can have different clones, since clone classes can be
		// merged)
		int unitLengthOrder = c1.getLengthInUnits() - c2.getLengthInUnits();
		if (unitLengthOrder != 0) {
			return unitLengthOrder;
		}

		// we compare end offsets in addition to units. If clones are imported
		// from
		// other tools, they might only have offsets but not unit indexes
		int endOffsetOrder = c1.getLocation().getRawEndOffset()
				- c2.getLocation().getRawEndOffset();
		if (endOffsetOrder != 0) {
			return endOffsetOrder;
		}

		// compare gaps. since this comparator only guarantees stable order, we
		// don't care how gaps are sorted, as long as their order is stable
		int gapPositionOrder = gapPositions(c1).compareTo(gapPositions(c2));
		if (gapPositionOrder != 0) {
			return gapPositionOrder;
		}

		if (!lenient) {
			throw new AssertionError(
					"Clones inside same clone class found, that start in same file in same line number");
		}

		return 0;
	}

	/** Encode the gap positions of a clone into a string */
	private String gapPositions(Clone clone) {
		return StringUtils.concat(clone.getGapPositions(), ",");
	}
}