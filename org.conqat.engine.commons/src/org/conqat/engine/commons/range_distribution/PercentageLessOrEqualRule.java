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
package org.conqat.engine.commons.range_distribution;

import java.util.HashSet;
import java.util.Set;

import org.conqat.engine.commons.assessment.AssessmentRange;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.assessment.Assessment;
import org.conqat.lib.commons.assessment.ETrafficLightColor;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.UnmodifiableSet;

/**
 * {@value #DOC}.
 * 
 * This class is immutable.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 31E25746D9C61572556060DEC76CA696
 */
public class PercentageLessOrEqualRule implements IAssessmentRule {

	/** Documentations string. */
	public static final String DOC = "This rule checks if the combined percentages of the specified "
			+ "ranges are below a specified threshold (inclusive). This allows to express rules like "
			+ "'the LOC in red and yellow ranges may not be more than 25% w.r.t. to the total LOC'.";

	/** The threshold. */
	private final double threshold;

	/** The ranges. */
	private final Set<String> rangeNames = new HashSet<String>();

	/** The secondary metric. */
	private final String secondaryMetric;

	/**
	 * Constructor.
	 * 
	 * @param secondaryMetric
	 *            the secondary metric this rule applies for.
	 * @param threshold
	 *            the threshold.
	 * @param rangeNames
	 *            names of the ranges.
	 */
	public PercentageLessOrEqualRule(String secondaryMetric, double threshold,
			String... rangeNames) {
		this(secondaryMetric, threshold, CollectionUtils.asHashSet(rangeNames));
	}

	/**
	 * Constructor.
	 * 
	 * @param secondaryMetric
	 *            the secondary metric this rule applies for.
	 * @param threshold
	 *            the threshold.
	 * @param rangeNames
	 *            names of the ranges.
	 */
	public PercentageLessOrEqualRule(String secondaryMetric, double threshold,
			Set<String> rangeNames) {
		this.secondaryMetric = secondaryMetric;
		this.threshold = threshold;
		this.rangeNames.addAll(rangeNames);
	}

	/** {@inheritDoc} */
	@Override
	public Assessment assess(RangeDistribution distTable)
			throws ConQATException {
		double sum = 0;

		for (String rangeName : rangeNames) {
			if (!distTable.hasRangeDefinition(rangeName)) {
				throw new ConQATException(
						"Distribution table has no range named '" + rangeName
								+ "'");
			}

			AssessmentRange range = distTable.getRange(rangeName);
			if (range == null) {
				continue;
			}
			sum += distTable.getPercentage(range, secondaryMetric);
		}

		ETrafficLightColor color;
		if (sum <= threshold) {
			color = ETrafficLightColor.GREEN;
		} else {
			color = ETrafficLightColor.RED;
		}
		return new Assessment(color);
	}

	/** Get secondary metric. */
	public String getSecondaryMetric() {
		return secondaryMetric;
	}

	/** Get threshold. */
	public double getThreshold() {
		return threshold;
	}

	/** Get range names. */
	public UnmodifiableSet<String> getRangeNames() {
		return CollectionUtils.asUnmodifiable(rangeNames);
	}

	/** {@inheritDoc} */
	@Override
	public PercentageLessOrEqualRule deepClone() {
		return this;
	}

}
