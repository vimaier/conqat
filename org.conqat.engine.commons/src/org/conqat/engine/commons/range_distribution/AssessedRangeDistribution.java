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

import java.util.ArrayList;
import java.util.Collection;

import org.conqat.engine.commons.assessment.IAssessmentRangesDefinition;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.assessment.Assessment;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.UnmodifiableCollection;

/**
 * An extension of {@link RangeDistribution} that allows to specify a set of
 * rules that are used to assess the distribution.
 * 
 * This class is immutable provided that all assessment rules are immutable.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 96BCBDBF817F61C4DD4E2DB938FBAD53
 */
public class AssessedRangeDistribution extends RangeDistribution {

	/** Collection of assessment rules. */
	private final Collection<IAssessmentRule> rules = new ArrayList<IAssessmentRule>();

	/**
	 * Create new distribution table.
	 * 
	 * @param entities
	 *            the entities, may not be empty
	 * @param principalMetric
	 *            key of the principal metric
	 * @param defaultPrincipalValue
	 *            the default value used if the principal metric is undefined
	 *            for an entity.
	 * @param assessmentRangeDef
	 *            the range definition
	 * @param rules
	 *            collection of assessment rules.
	 * @throws ConQATException
	 *             if the provided assessment ranges overlap or no entities are
	 *             provided or the principal metric is NaN or infinite for one
	 *             or more entities
	 */
	public AssessedRangeDistribution(
			Collection<? extends IConQATNode> entities, String principalMetric,
			double defaultPrincipalValue,
			IAssessmentRangesDefinition assessmentRangeDef,
			Collection<? extends IAssessmentRule> rules) throws ConQATException {
		super(entities, principalMetric, defaultPrincipalValue,
				assessmentRangeDef);
		this.rules.addAll(rules);
	}

	/** {@inheritDoc} */
	@Override
	public AssessedRangeDistribution deepClone() {
		return this;
	}

	/** Returns all assessment rules defined for this distribution. */
	public UnmodifiableCollection<IAssessmentRule> getAssessmentRules() {
		return CollectionUtils.asUnmodifiable(rules);
	}

	/**
	 * Assesses all assessment rules defined for this distribution and returns
	 * the combined result. If no rules are defined, an empty assessment is
	 * returned.
	 */
	public Assessment assess() throws ConQATException {
		Assessment result = new Assessment();
		for (IAssessmentRule rule : rules) {
			result.add(rule.assess(this));
		}
		return result;
	}
}
