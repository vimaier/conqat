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
package org.conqat.engine.commons.assessment;

import java.awt.Color;
import java.util.Collection;
import java.util.HashSet;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.assertion.CCSMPre;
import org.conqat.lib.commons.assertion.PreconditionException;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.string.StringUtils;

/**
 * This class stores a collection of {@link AssessmentRangeDefinition}s plus
 * defaults for name and color for values outside the specified ranges.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 44615 $
 * @ConQAT.Rating GREEN Hash: C737A96E8136D2EE827486774D073E29
 */
public abstract class AssessmentRangesDefinitionBase<E extends AssessmentRangeDefinition>
		implements IAssessmentRangesDefinition {

	/** Maps from upper boundary value to range definition. */
	private final NavigableMap<Double, E> rangeDefMap = new TreeMap<Double, E>();

	/** Default name. */
	private final String defaultName;

	/** Default color. */
	private final Color defaultColor;

	/**
	 * Constructor.
	 * 
	 * @param defaultColor
	 *            default color
	 * @param defaultName
	 *            default name
	 * @param rangeDefinitions
	 *            collection of range definitions. The range definitions must
	 *            define distinct boundaries and names. Otherwise a
	 *            {@link PreconditionException} is thrown.
	 * @throws ConQATException
	 *             if the range definitions are not unique
	 */
	public AssessmentRangesDefinitionBase(Color defaultColor,
			String defaultName, Collection<E> rangeDefinitions)
			throws ConQATException {
		this.defaultColor = defaultColor;
		this.defaultName = defaultName;

		for (E rangeDef : rangeDefinitions) {
			this.rangeDefMap.put(rangeDef.getUpperBoundary(), rangeDef);
		}

		validateRangeDefinitions();
	}

	/** Assert that names and boundaries are unique. */
	private void validateRangeDefinitions() throws ConQATException {
		Set<Double> boundaries = new HashSet<Double>();
		Set<String> names = new HashSet<String>();
		for (E rangeDef : rangeDefMap.values()) {
			if (rangeDef.getName().equals(defaultName)) {
				throw new ConQATException(
						"One range definition's name equals the default name.");
			}
			checkDuplicate(rangeDef.getName(), names);
			checkDuplicate(rangeDef.getUpperBoundary(), boundaries);
		}

	}

	/** Checks for duplicates. */
	private static <T> void checkDuplicate(T item, Set<T> set)
			throws ConQATException {
		if (!set.add(item)) {
			throw new ConQATException("Duplicate item " + item);
		}
	}

	/** {@inheritDoc} */
	@Override
	public boolean hasRangeDefinition(String rangeName) {

		if (rangeName.equals(defaultName)) {
			return true;
		}

		for (E rangeDef : rangeDefMap.values()) {
			if (rangeName.equals(rangeDef.getName())) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Obtain the range definition for the specified value. This will not return
	 * one of the range definitions provided at construction but a new range
	 * definition whose boundary is equal to the provided value. For color and
	 * names, the following rules apply:
	 * 
	 * <ul>
	 * <li>If this ranges definition is empty or if the value is greater than
	 * the highest bound, default color and names are used.
	 * <li>Otherwise the color name of the range definition that defines the
	 * ceiling value for the provided value are used.
	 * </ul>
	 */
	@Override
	public E obtainRangeDefinition(double value) {

		if (rangeDefMap.isEmpty() || value > rangeDefMap.lastKey()) {
			return newRangeDefinition(value, defaultColor, defaultName);
		}

		E rangeDef = rangeDefMap.ceilingEntry(value).getValue();
		return newRangeDefinition(value, rangeDef.getColor(),
				rangeDef.getName());

	}

	/** Template method to construct new range definition. */
	protected abstract E newRangeDefinition(double value, Color color,
			String name);

	/**
	 * Determine the ranges of this distribution table. This takes into account
	 * the specified boundary values as well as the extrema of the actual
	 * values.
	 */
	@Override
	public Set<AssessmentRange> obtainRanges(double minValue, double maxValue) {
		CCSMAssert.isFalse(minValue > maxValue,
				"Minimum value must not be greater than max value.");

		// includes only range definition with boundaries within the range of
		// the actual measurement values
		NavigableSet<AssessmentRangeDefinition> rangeDefs = obtainRangeDefs(
				minValue, maxValue);

		// we have a single range definition
		if (rangeDefs.size() == 1) {
			return handleSingleRangeDef(rangeDefs);
		}

		Set<AssessmentRange> result = new HashSet<AssessmentRange>();

		// this flag stores if we had to add an additional artificial range
		boolean additionalRange = handleMinValueEqualLowestBoundary(minValue,
				rangeDefs, result);

		// we convert this to an array to support convenient access to the next
		// element
		AssessmentRangeDefinition[] rangeDefsArray = CollectionUtils.toArray(
				rangeDefs, AssessmentRangeDefinition.class);

		for (int i = 0; i < rangeDefsArray.length - 1; i++) {
			result.add(createRange(additionalRange, rangeDefsArray, i));
		}

		return result;
	}

	/**
	 * Obtain the range definitions. This includes only range definition with
	 * boundaries within the range of the actual measurement values.
	 */
	private NavigableSet<AssessmentRangeDefinition> obtainRangeDefs(
			double minValue, double maxValue) {
		NavigableSet<AssessmentRangeDefinition> boundaryMap = new TreeSet<AssessmentRangeDefinition>();

		boundaryMap.add(obtainRangeDefinition(minValue));
		boundaryMap.add(obtainRangeDefinition(maxValue));

		for (AssessmentRangeDefinition rangeDef : rangeDefMap.values()) {
			double bound = rangeDef.getUpperBoundary();
			if (bound > boundaryMap.first().getUpperBoundary()
					&& bound < boundaryMap.last().getUpperBoundary()) {
				boundaryMap.add(rangeDef);
			}

		}
		return boundaryMap;
	}

	/**
	 * If there is only a single range definition, create a single range that
	 * includes exactly the boundary of the range definition and return it.
	 */
	private Set<AssessmentRange> handleSingleRangeDef(
			NavigableSet<AssessmentRangeDefinition> boundarySet) {
		AssessmentRangeDefinition rangeDef = boundarySet.first();
		return CollectionUtils.asHashSet(new AssessmentRange(rangeDef
				.getUpperBoundary(), true, rangeDef));
	}

	/**
	 * If the minimum value and the lowest boundary are equal we have to
	 * introduce an additional range that contains only this value. We have to
	 * deal with this explicitly as the we loose this information due to set
	 * semantics.
	 * 
	 * This method adds this artificial range if required.
	 * 
	 * @return true if an additional artificial range has been added, false
	 *         otherwise
	 */
	private boolean handleMinValueEqualLowestBoundary(double minValue,
			NavigableSet<AssessmentRangeDefinition> boundarySet,
			Set<AssessmentRange> result) {

		if (!rangeDefMap.isEmpty() && minValue == rangeDefMap.firstKey()) {
			result.add(new AssessmentRange(minValue, true, boundarySet.first()));
			return true;
		}
		return false;
	}

	/**
	 * Creates the assessment range.
	 * 
	 * @param additionalRange
	 *            flag if an additional artificial range has been added before
	 * @param rangeDefsArray
	 *            the array of range definitions
	 * @param index
	 *            the current index.
	 */
	private AssessmentRange createRange(boolean additionalRange,
			AssessmentRangeDefinition[] rangeDefsArray, int index) {
		CCSMPre.isTrue(index < rangeDefsArray.length - 1, "Out of bounds");
		// the lower bound is included only if is the first range and no
		// additional range has been added before.
		boolean lowerIsIncluded = index == 0 && !additionalRange;
		return new AssessmentRange(rangeDefsArray[index].getUpperBoundary(),
				lowerIsIncluded, rangeDefsArray[index + 1]);
	}

	/** Returns a string representation for debugging purposes. */
	@Override
	public String toString() {
		return defaultName + ":["
				+ StringUtils.concat(rangeDefMap.values(), ", ") + "]";
	}
}
