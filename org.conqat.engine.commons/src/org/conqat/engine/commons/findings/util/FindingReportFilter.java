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
package org.conqat.engine.commons.findings.util;

import org.conqat.engine.commons.ConQATPipelineProcessorBase;
import org.conqat.engine.commons.findings.FindingCategory;
import org.conqat.engine.commons.findings.FindingReport;
import org.conqat.engine.commons.pattern.PatternList;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 36160 $
 * @ConQAT.Rating GREEN Hash: 7D4B3DC7F074344F4B59CFFDA1D3809B
 */
@AConQATProcessor(description = "Removes findings from FindingReports based on specified filter criteria")
public class FindingReportFilter extends
		ConQATPipelineProcessorBase<FindingReport> {

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "retain-categories", attribute = "patterns", optional = true, description = ""
			+ "Patterns that specify which categories in the report are retained. If empty, all categories are retained.")
	public PatternList retainCategoryPatterns;

	/** {@inheritDoc} */
	@Override
	protected void processInput(FindingReport report) {

		// if no patterns are specified, filter nothing
		if (retainCategoryPatterns == null || retainCategoryPatterns.isEmpty()) {
			return;
		}

		for (FindingCategory category : report.getChildren()) {
			if (!retainCategoryPatterns.matchesAny(category.getName())) {
				category.remove();
			}
		}

	}

}
