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

import static org.conqat.engine.commons.range_distribution.RangeDistributionTest.P_METRIC;
import static org.conqat.engine.commons.range_distribution.RangeDistributionTest.S_METRIC;
import static org.conqat.engine.commons.range_distribution.RangeDistributionTest.createAssessmentRangesDefinition;
import static org.conqat.engine.commons.range_distribution.RangeDistributionTest.createEntities;
import static org.conqat.lib.commons.assessment.ETrafficLightColor.RED;
import static org.conqat.lib.commons.assessment.ETrafficLightColor.YELLOW;

import java.util.Arrays;
import java.util.Collection;

import junit.framework.TestCase;

import org.conqat.engine.commons.assessment.AssessmentRangesDefinition;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.assessment.Assessment;
import org.conqat.lib.commons.assessment.ETrafficLightColor;
import org.conqat.lib.commons.color.ECCSMColor;

/**
 * Test for {@link AssessedRangeDistribution}.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 32B82DD4A4479BB10E6F7C3D58B1BBDB
 */
public class AssessedRangeDistributionTest extends TestCase {

	/** Test {@link PercentageLessOrEqualRule}. */
	public void testPercentageLessOrEqualRule() throws ConQATException {

		PercentageLessOrEqualRule rule1 = new PercentageLessOrEqualRule(
				S_METRIC, 0.1, RED.name());
		PercentageLessOrEqualRule rule2 = new PercentageLessOrEqualRule(
				S_METRIC, 0.3, RED.name(), YELLOW.name());

		AssessedRangeDistribution table1 = newDistTable(
				createEntities("a#2:100", "b#4:100", "c#3:100", "d#3:100"),
				Arrays.asList(rule1, rule2), "5:green", "7:yellow");

		assertAssessmentColors(table1, ETrafficLightColor.GREEN,
				ETrafficLightColor.GREEN);

		AssessedRangeDistribution table2 = newDistTable(
				createEntities("a#2:100", "b#4:100", "c#3:100", "d#7:200"),
				Arrays.asList(rule1, rule2), "5:green", "7:yellow");

		assertAssessmentColors(table2, ETrafficLightColor.GREEN,
				ETrafficLightColor.RED);

		AssessedRangeDistribution table3 = newDistTable(
				createEntities("a#2:100", "b#4:100", "c#8:100", "d#7:200"),
				Arrays.asList(rule1, rule2), "5:green", "7:yellow");

		assertAssessmentColors(table3, ETrafficLightColor.RED,
				ETrafficLightColor.RED);
	}

	/** Create a distribution table with default color red. */
	private static AssessedRangeDistribution newDistTable(
			Collection<IConQATNode> entities,
			Collection<? extends IAssessmentRule> rules,
			String... boundaryDescriptors) throws ConQATException {

		AssessmentRangesDefinition assessmentDef = createAssessmentRangesDefinition(
				ECCSMColor.RED, boundaryDescriptors);

		return new AssessedRangeDistribution(entities, P_METRIC, 0,
				assessmentDef, rules);
	}

	/** Assert assessment colors. */
	private static void assertAssessmentColors(AssessedRangeDistribution table,
			ETrafficLightColor... colors) throws ConQATException {

		Assessment assessment = new Assessment();
		for (ETrafficLightColor color : colors) {
			assessment.add(color);
		}

		assertEquals(assessment, table.assess());
	}
}
