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

import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.commons.findings.EFindingKeys;
import org.conqat.engine.commons.findings.Finding;
import org.conqat.engine.commons.findings.FindingCategory;
import org.conqat.engine.commons.findings.FindingGroup;
import org.conqat.engine.commons.findings.FindingReport;
import org.conqat.engine.commons.findings.location.QualifiedNameLocation;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43290 $
 * @ConQAT.Rating GREEN Hash: 8C9D3A3ECD6B3BF90351CEA199D17F66
 */
@AConQATProcessor(description = "Builds a finding report from JUnit results.")
public class JUnitFindingReportBuilder extends ConQATProcessorBase {

	/** The reports to read. */
	private final List<JUnitResultNode> junitResults = new ArrayList<JUnitResultNode>();

	/** The report created. */
	private final FindingReport findingReport = new FindingReport();

	/** The finding category used. */
	private FindingCategory findingCategory;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "junit", minOccurrences = 1, description = "Add JUnit results to be added to the report.")
	public void addJUnitResult(
			@AConQATAttribute(name = "result", description = "Reference to result.") JUnitResultNode resultNode) {
		junitResults.add(resultNode);
	}

	/** {@inheritDoc} */
	@Override
	public FindingReport process() {
		findingCategory = findingReport.getOrCreateCategory("JUnit");
		for (JUnitResultNode junitResult : junitResults) {
			convertResults(junitResult);
		}
		return findingReport;
	}

	/** Converts a single JUnit report. */
	private void convertResults(JUnitResultNode junitResult) {
		FindingGroup group = findingCategory
				.createFindingGroup("JUnit Findings "
						+ (findingCategory.getChildren().length + 1));
		for (JUnitTestSuiteNode suite : junitResult.getChildren()) {
			if (suite.getErrorCount() == 0 && suite.getFailureCount() == 0) {
				continue;
			}

			String qname = suite.getName();
			Finding finding = group.createFinding(new QualifiedNameLocation(qname, null,
					qname));
			finding.setValue(EFindingKeys.MESSAGE.toString(), qname
					+ " contains " + suite.getErrorCount() + " erroneous and "
					+ suite.getFailureCount() + " failing test cases.");
		}
	}
}