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
package org.conqat.engine.commons.statistics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.math.Range;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: pfaller $
 * @version $Rev: 37529 $
 * @ConQAT.Rating GREEN Hash: B7488D19CC7811C5E3E16F6BD73BD7DA
 */
@AConQATProcessor(description = "This processor creates a KeyedData object by "
		+ "counting the frequency of numbers specified by a key at the leaves of the "
		+ "ConQATNode hierarchy.  If the number-key is specified, the processor does "
		+ "not only add 1 for each leave but the number stored at the specified key. "
		+ "This processor also supports ranges, which means that numbers within certain "
		+ "ranges can be aggregated to a single value. "
		+ "All numbers that are not within a defined range will be counted as singleton ranges.")
public class NumberRangeFrequencyProcessor extends ValueFrequencyProcessor {

	/** Ranges to consider. Package visible for testing. */
	/* package */final List<Range> ranges = new ArrayList<Range>();

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "range", description = "Defines a range of values that are aggregated in the output.")
	public void addRange(
			@AConQATAttribute(name = "lower", description = "Lower bound of the range.") double lower,
			@AConQATAttribute(name = "lower-inclusive", description = "Lower bound of the range is inclusive.", defaultValue = "false") boolean lowerIsInclusive,
			@AConQATAttribute(name = "upper", description = "Upper bound of the range.") double upper,
			@AConQATAttribute(name = "upper-inclusive", description = "Upper bound of the range is inclusive.", defaultValue = "true") boolean upperIsInclusive) {
		ranges.add(new Range(lower, lowerIsInclusive, upper, upperIsInclusive));
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "auto-ranges", description = "Defines multiple ranges. This is simpler that explicitly adding ranges if multiple ranges of the same size are required.")
	public void addAutoRanges(
			@AConQATAttribute(name = "lower", description = "Lower bound of the lowest interval created.") double lower,
			@AConQATAttribute(name = "step-size", description = "The size of the created intervals. The intervals start at the lower bound and continue until the upper bound is covered.") double stepSize,
			@AConQATAttribute(name = "upper", description = "Upper bound of the range that will be included in any case. Depending on the step size the actual upper bound might be higher.") double upper,
			@AConQATAttribute(name = "upper-inclusive", description = "If this is true, left-open intervals (with the upper limit included) are created, otherwise right-open ones.", defaultValue = "true") boolean upperInclusive,
			@AConQATAttribute(name = "lower-collector", description = "If this is true, then a range collecting all values below the lower bound will be created.", defaultValue = "true") boolean createLowerCollector,
			@AConQATAttribute(name = "upper-collector", description = "If this is true, then a range collecting all values above the upper bound will be created.", defaultValue = "true") boolean createUpperCollector)
			throws ConQATException {

		if (upper <= lower) {
			throw new ConQATException(
					"Lower bound must be smaller than upper bound!");
		}

		if (stepSize <= 0) {
			throw new ConQATException("Step size must be positive!");
		}

		// avoid very long running loops
		if ((upper - lower) / stepSize > 1000000) {
			throw new ConQATException(
					"Auto-ranges would create more than 1000000 ranges!");
		}

		if (createLowerCollector) {
			ranges.add(new Range(Double.NEGATIVE_INFINITY, false, lower,
					upperInclusive));
		}

		while (lower < upper) {
			ranges.add(new Range(lower, !upperInclusive, lower + stepSize,
					upperInclusive));
			lower += stepSize;
		}

		if (createUpperCollector) {
			ranges.add(new Range(lower, !upperInclusive,
					Double.POSITIVE_INFINITY, false));
		}
	}

	/** {@inheritDoc} */
	@Override
	public KeyedData<?> process() throws ConQATException {
		sortAndCheckRanges();
		return super.process();
	}

	/** Sorts the ranges and checks for overlaps. */
	/* package */void sortAndCheckRanges() throws ConQATException {
		Collections.sort(ranges);

		for (int i = 1; i < ranges.size(); ++i) {
			if (ranges.get(i - 1).overlaps(ranges.get(i))) {
				throw new ConQATException("Overlapping ranges defined: "
						+ ranges.get(i - 1) + " and " + ranges.get(i));
			}
		}
	}

	/**
	 * Checks if the value is numeric and returns a suitable range. This range
	 * is either from the predefined ranges, or a new singleton range.
	 */
	@Override
	protected Range convert(Object value) throws ConQATException {
		if (!(value instanceof Number)) {
			throw new ConQATException("Value is not numeric.");
		}

		double d = ((Number) value).doubleValue();

		Range singletonRange = new Range(d, d);

		int index = Collections.binarySearch(ranges, singletonRange);
		if (index < 0) {
			index = -index - 1;
		}

		// we have to check two different ranges due to the way we implemented
		// the comparison
		if (index >= 0 && index < ranges.size()
				&& ranges.get(index).contains(d)) {
			return ranges.get(index);
		}
		if (index > 0 && index <= ranges.size()
				&& ranges.get(index - 1).contains(d)) {
			return ranges.get(index - 1);
		}

		// fallback is just the singleton range
		return singletonRange;
	}

}
