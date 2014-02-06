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
package org.conqat.engine.code_clones.core.utils;

import java.util.Comparator;

import org.conqat.engine.code_clones.core.CloneClass;

/**
 * Enumeration of different comparators that can be used to sort clone classes.
 * <p>
 * All comparators sort in a descending fashion.
 * 
 * @author juergens
 * @author $Author: steidl $
 * @version $Rev: 43634 $
 * @ConQAT.Rating GREEN Hash: 482E9A9059AC72D0FAED182ED7DF346D
 */
public enum ECloneClassComparator implements Comparator<CloneClass> {

	/**
	 * Compares {@link CloneClass}es by the normalized length of their reference
	 * clone
	 */
	NORMALIZED_LENGTH(new NormalizedLengthDimension()),

	/** Compares {@link CloneClass}es by their cardinality */
	CARDINALITY(new CardinalityDimension()),

	/**
	 * Compares {@link CloneClass}es by their volume. The volume is the product
	 * of a clone classes length and its cardinality
	 */
	VOLUME(new VolumeDimension()),

	/** Compares {@link CloneClass}es by their gap count */
	GAPCOUNT(new GapCountDimension()),

	/** Compares {@link CloneClass}es by their ids */
	ID(new IdDimension());

	/** Gets the metric of the clone class that is used for comparison */
	private final ComparisonDimension dimension;

	/** Constructor */
	private ECloneClassComparator(ComparisonDimension dimension) {
		this.dimension = dimension;
	}

	/** {@inheritDoc} */
	@Override
	public int compare(CloneClass cloneClass1, CloneClass cloneClass2) {
		Integer value1 = dimension.compareValue(cloneClass1);
		Integer value2 = dimension.compareValue(cloneClass2);

		// we always sort in descending order
		return value2.compareTo(value1);
	}

	/**
	 * Defines a dimension according to which clone classes can be compared.
	 */
	private interface ComparisonDimension {
		/** Gets the value which is used for comparison in this dimension */
		int compareValue(CloneClass cloneClass);
	}

	/** Dimension: normalized clone class length */
	private static class NormalizedLengthDimension implements
			ComparisonDimension {
		/** {@inheritDoc} */
		@Override
		public int compareValue(CloneClass cloneClass) {
			return cloneClass.getNormalizedLength();
		}
	}

	/** Dimension: clone class cardinality */
	private static class CardinalityDimension implements ComparisonDimension {
		/** {@inheritDoc} */
		@Override
		public int compareValue(CloneClass cloneClass) {
			return cloneClass.size();
		}
	}

	/** Dimension: clone class volume (length * cardinality) */
	private static class VolumeDimension implements ComparisonDimension {
		/** {@inheritDoc} */
		@Override
		public int compareValue(CloneClass cloneClass) {
			return cloneClass.size() * cloneClass.getNormalizedLength();
		}
	}

	/** Dimension: clone class gap count */
	private static class GapCountDimension implements ComparisonDimension {
		/** {@inheritDoc} */
		@Override
		public int compareValue(CloneClass cloneClass) {
			return cloneClass.getGapCount();
		}
	}

	/** Dimension: clone class id */
	private static class IdDimension implements ComparisonDimension {
		/** {@inheritDoc} */
		@Override
		public int compareValue(CloneClass cloneClass) {
			// If id is longer than int.MAX, -1 is returned. We do not care if
			// they are then sorted incorrectly.
		    return (int) cloneClass.getId();
		}
	}

}