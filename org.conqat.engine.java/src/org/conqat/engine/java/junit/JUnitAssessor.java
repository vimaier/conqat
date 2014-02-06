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
package org.conqat.engine.java.junit;

import org.conqat.engine.commons.ConQATPipelineProcessorBase;
import org.conqat.engine.commons.node.NodeConstants;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.lib.commons.assessment.Assessment;
import org.conqat.lib.commons.assessment.ETrafficLightColor;

/**
 * This processor assess JUnit results. Suites with errors or failures are rated
 * red, others green. Assessments are aggregated and set as summary.
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 8B6389C92C26F3F0FDFAC2D63372F28F
 */
@AConQATProcessor(description = "This processor assess JUnit results. "
		+ " Suites with errors or failures are rated red, others green. "
		+ " Assessments are aggregated and set as summary.")
public class JUnitAssessor extends ConQATPipelineProcessorBase<JUnitResultNode> {

	/** The key to use for saving the <code>Assessment</code>. */
	@AConQATKey(description = "Key for assessment", type = "org.conqat.lib.commons.assessment.Assessment")
	public static final String ASSESSMENT_KEY = "JUnit-Assessment";

	/** {@inheritDoc} */
	@Override
	protected void processInput(JUnitResultNode input) {
		NodeUtils.addToDisplayList(input, ASSESSMENT_KEY);

		Assessment rootAssessment = new Assessment();
		input.setValue(ASSESSMENT_KEY, rootAssessment);
		input.setValue(NodeConstants.SUMMARY, rootAssessment);

		for (JUnitTestSuiteNode testSuite : input.getChildren()) {
			Assessment suiteAssessment = assess(testSuite);
			rootAssessment.add(suiteAssessment);
			testSuite.setValue(ASSESSMENT_KEY, suiteAssessment);
		}
	}

	/**
	 * Determine assessment for a single test suite. Failure and error are added
	 * as red assessments.
	 */
	private Assessment assess(JUnitTestSuiteNode testSuite) {
		Assessment result = new Assessment();
		int reds = testSuite.getErrorCount() + testSuite.getFailureCount();
		int greens = testSuite.getTestCount() - reds;
		result.add(ETrafficLightColor.RED, reds);
		result.add(ETrafficLightColor.GREEN, greens);
		return result;
	}

}