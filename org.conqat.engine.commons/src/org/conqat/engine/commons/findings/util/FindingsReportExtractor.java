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

import org.conqat.engine.commons.findings.Finding;
import org.conqat.engine.commons.findings.FindingCategory;
import org.conqat.engine.commons.findings.FindingGroup;
import org.conqat.engine.commons.findings.FindingReport;
import org.conqat.engine.commons.findings.FindingsList;
import org.conqat.engine.commons.node.DisplayList;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.traversal.TraversalUtils;
import org.conqat.engine.commons.util.ConQATInputProcessorBase;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.lib.commons.collections.IdentityHashSet;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: pfaller $
 * @version $Rev: 37466 $
 * @ConQAT.Rating GREEN Hash: 70A473DBAC90B5A30988C7DD75DAC005
 */
@AConQATProcessor(description = ""
		+ "This processor extracts the findings report embedded in a ConQAT node structure.")
public class FindingsReportExtractor extends
		ConQATInputProcessorBase<IConQATNode> {

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "trim", attribute = "value", optional = true, description = ""
			+ "If this is set to true (which is the default), the report is reduced to "
			+ "findings that are actually found in the underlying ConQAT hierarchy and "
			+ "accessible via the display list.")
	public boolean trimReport = true;

	/** {@inheritDoc} */
	@Override
	public FindingReport process() {
		FindingReport report = NodeUtils.getFindingReport(input);
		if (trimReport) {
			trimReport(report, listFindings());
		}
		return report;
	}

	/** Lists all findings that are linked to nodes in the hierarchy. */
	private IdentityHashSet<Finding> listFindings() {
		IdentityHashSet<Finding> linkedFindings = new IdentityHashSet<Finding>();
		DisplayList displayList = NodeUtils.getDisplayList(input);
		for (IConQATNode node : TraversalUtils.listAllDepthFirst(input)) {
			for (String key : displayList) {
				Object value = node.getValue(key);
				if (value instanceof FindingsList) {
					linkedFindings.addAll((FindingsList) value);
				}
			}
		}
		return linkedFindings;
	}

	/**
	 * Trims the report to contain only findings from the given set. Empty
	 * groups and categories are pruned.
	 */
	private void trimReport(FindingReport report,
			IdentityHashSet<Finding> linkedFindings) {
		for (FindingCategory category : report.getChildren()) {
			for (FindingGroup group : category.getChildren()) {
				for (Finding finding : group.getChildren()) {
					if (!linkedFindings.contains(finding)) {
						finding.remove();
					}
				}
				if (!group.hasChildren()) {
					group.remove();
				}
			}
			if (!category.hasChildren()) {
				category.remove();
			}
		}
	}

}