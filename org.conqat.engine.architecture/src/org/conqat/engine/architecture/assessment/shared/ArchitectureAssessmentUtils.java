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
package org.conqat.engine.architecture.assessment.shared;

import java.util.List;

import org.conqat.engine.commons.findings.Finding;
import org.conqat.engine.commons.findings.FindingReport;
import org.conqat.engine.commons.findings.util.FindingUtils;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.SetMap;

/**
 * Provides utility methods for architecture assessment.
 * 
 * @author $Author: deissenb $
 * @version $Rev: 41963 $
 * @ConQAT.Rating GREEN Hash: 642E4724FFF52C1AF66A7652EAC4F3EE
 */
public class ArchitectureAssessmentUtils {

	/**
	 * Collects all types and their dependencies from the given finding report.
	 * The result maps each source type to the set of its targets.
	 * 
	 * @param report
	 *            A finding report that stores information about type-level
	 *            dependencies.
	 * @return A map from source types to their targets.
	 */
	public static SetMap<String, String> getTypeDependencies(
			FindingReport report) {
		SetMap<String, String> types = new SetMap<String, String>();

		List<String> emptyStringList = CollectionUtils.emptyList();
		
		// Process all findings that represent types. Because some types may not
		// have a dependency, we first add them all with an empty list of
		// targets. The real targets for those types that are part of a
		// dependency are then added in the second loop.
		for (Finding finding : FindingUtils.getAllFindings(report,
				ArchitectureAssessor.TYPES_CATEGORY)) {
			CCSMAssert.isNotNull(finding.getMessage());
			types.addAll(finding.getMessage(), emptyStringList);
		}

		// Process all findings that represent type-level dependencies.
		for (Finding finding : FindingUtils.getAllFindings(report,
				ArchitectureAssessor.DEPENDENCY_CATEGORY)) {
			CCSMAssert.isNotNull(finding.getDependencySource());
			CCSMAssert.isNotNull(finding.getDependencyTarget());
			
			if (!types.containsCollection(finding.getDependencyTarget())) {
				types.addAll(finding.getDependencyTarget(), emptyStringList);
			}
			
			types.add(finding.getDependencySource(),
			        finding.getDependencyTarget());
		}
		return types;
	}
}
