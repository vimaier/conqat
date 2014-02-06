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
package org.conqat.engine.commons.assessment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.assessment.Assessment;
import org.conqat.lib.commons.assessment.ETrafficLightColor;
import org.conqat.lib.commons.math.Range;

/**
 * An assessor based on given ranges.
 * 
 * @author Benjamin Hummel
 * @author $Author: heinemann $
 * @version $Rev: 46768 $
 * @ConQAT.Rating YELLOW Hash: EAFFDC4B96B4B94E10CE1103B9310304
 */
public abstract class RangeBasedAssessorBase<E> extends LocalAssessorBase<E> {

	/** The ranges used in the assessment. */
	private final List<AssessedRange> ranges = new ArrayList<AssessedRange>();

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "default", attribute = "color", optional = true, description = ""
			+ "Set the assessment color returned if no range contained the value. Default is RED.")
	public ETrafficLightColor defaultColor = ETrafficLightColor.RED;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "range", description = "Adds an assessment range. The ranges defined may not overlap.")
	public void addRange(
			@AConQATAttribute(name = "lower", description = "lower bound of the range (exclusive).") double lower,
			@AConQATAttribute(name = "upper", description = "upper bound of the range (inclusive).") double upper,
			@AConQATAttribute(name = "color", description = "color to use if the value is in this range") ETrafficLightColor color) {
		ranges.add(new AssessedRange(lower, upper, color));
	}

	/** {@inheritDoc} */
	@Override
	protected void setUp(IConQATNode root) throws ConQATException {
		super.setUp(root);
		checkRanges(ranges);
	}

	/**
	 * Checks if there are overlapping ranges. The algorithm checks for each
	 * pair of ranges, whether the upper bound of one range is contained in the
	 * other one. This works, as for each possible overlapping configuration of
	 * ranges this holds for at least one pair.
	 */
	public static void checkRanges(List<AssessedRange> ranges)
			throws ConQATException {
		for (AssessedRange r1 : ranges) {
			for (AssessedRange r2 : ranges) {
				if (r1 != r2 && r1.contains(r2.getUpper())) {
					throw new ConQATException(
							"May not use overlapping ranges: " + r1 + " and "
									+ r2);
				}
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	protected final Assessment assessValue(E e) {
		return determineAssessment(obtainDouble(e), ranges, defaultColor);
	}

	/** Determines the assessment based on ranges and the default color. */
	public static Assessment determineAssessment(double value,
			List<AssessedRange> ranges, ETrafficLightColor defaultColor) {
		for (AssessedRange r : ranges) {
			if (r.contains(value)) {
				return new Assessment(r.color);
			}
		}
		return new Assessment(defaultColor);
	}

	/** Obtains a double from the value which should be assessed. */
	protected abstract double obtainDouble(E value);

	/** Helper class to store ranges and their assigned colors. */
	public static class AssessedRange extends Range implements Serializable {

		/** Version for serialization. */
		private static final long serialVersionUID = 1;

		/** assigned color. */
		public final ETrafficLightColor color;

		/** Constructor. */
		public AssessedRange(double lower, double upper,
				ETrafficLightColor color) {
			super(lower, false, upper, true);
			if (isEmpty()) {
				throw new IllegalArgumentException(
						"Invalid/empty range (lower >= upper): " + lower
								+ " and " + upper);
			}
			this.color = color;
		}
	}
}