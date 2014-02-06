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
package org.conqat.engine.commons.findings.util;

import org.conqat.engine.commons.ConQATPipelineProcessorBase;
import org.conqat.engine.commons.findings.Finding;
import org.conqat.engine.commons.findings.FindingCategory;
import org.conqat.engine.commons.findings.FindingGroup;
import org.conqat.engine.commons.findings.FindingReport;
import org.conqat.engine.commons.node.NodeConstants;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43290 $
 * @ConQAT.Rating GREEN Hash: CFC45CA0533F24F54655179B157E2EFF
 */
@AConQATProcessor(description = "This processor prepares a FindingsReport to be "
		+ "used with a TableLayouter. In particular, it adds the findings' message "
		+ "and the findings' locations as keyed values. Additionally, it counts the number "
		+ "of findings in each group.")
public class FindingsReportBeautifier extends
		ConQATPipelineProcessorBase<FindingReport> {

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Counts findings in the group", type = "java.lang.Integer")
	public static final String KEY_COUNT = "#Findings";

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Location of the finding", type = "java.lang.String")
	public static final String KEY_LOCATION = "Location";

	/** {@inheritDoc} */
	@Override
	protected void processInput(FindingReport input) {
		NodeUtils.addToDisplayList(input, KEY_COUNT, KEY_LOCATION);

		int overallCount = 0;
		for (FindingCategory category : input.getChildren()) {
			int categoryCount = 0;
			for (FindingGroup group : category.getChildren()) {
				categoryCount += processGroup(group);
			}

			category.setValue(KEY_COUNT, categoryCount);
			overallCount += categoryCount;
		}

		input.setValue(KEY_COUNT, overallCount);
	}

	/** Process a finding group. */
	private int processGroup(FindingGroup group) {
		int findingsCount = group.getChildrenSize();

		group.setValue(KEY_COUNT, findingsCount);
		group.setValue(NodeConstants.COMPARATOR,
				SmartFindingComparator.INSTANCE);

		for (Finding finding : group.getChildren()) {
			finding.setValue(KEY_COUNT, 1);
			finding.setUseMessageAsName(true);

			finding.setValue(KEY_LOCATION, finding.getLocation()
					.toLocationString());
		}

		return findingsCount;
	}
}
