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
package org.conqat.engine.html_presentation.links;

import org.conqat.engine.commons.ConQATPipelineProcessorBase;
import org.conqat.engine.commons.findings.Finding;
import org.conqat.engine.commons.findings.FindingCategory;
import org.conqat.engine.commons.findings.FindingGroup;
import org.conqat.engine.commons.findings.FindingReport;
import org.conqat.engine.commons.findings.location.ElementLocation;
import org.conqat.engine.commons.findings.location.TextRegionLocation;
import org.conqat.engine.commons.findings.util.FindingsReportBeautifier;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.html_presentation.listing.ListingFileProvider;
import org.conqat.engine.html_presentation.listing.ListingWriter;
import org.conqat.engine.html_presentation.util.HTMLLink;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43290 $
 * @ConQAT.Rating GREEN Hash: 5D1688E988BC5C9C282C1655425E0397
 */
@AConQATProcessor(description = "This class annotates a finding report with a locations column similar to the FindingsReportBeautifier. "
		+ "This class, however, creates linked locations.")
public class FindingReportLocationLinker extends
		ConQATPipelineProcessorBase<FindingReport> {

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Location of the finding", type = "org.conqat.engine.html_presentation.util.HTMLLink")
	public static final String KEY_LOCATION = FindingsReportBeautifier.KEY_LOCATION;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "file-provider", attribute = "ref", description = "The file provider used for mapping elements to storage files.")
	public ListingFileProvider fileProvider;

	/** {@inheritDoc} */
	@Override
	protected void processInput(FindingReport input) {
		NodeUtils.addToDisplayList(input, KEY_LOCATION);

		for (FindingCategory category : input.getChildren()) {
			for (FindingGroup group : category.getChildren()) {
				for (Finding finding : group.getChildren()) {
					annotateFinding(finding);
				}
			}
		}
	}

	/** Annotates a single finding with links. */
	private void annotateFinding(Finding finding) {
		ElementLocation location = finding.getLocation();
		String href = fileProvider.getRootLink(location.getUniformPath());
		if (location instanceof TextRegionLocation) {
			href += "#"
					+ ListingWriter.getLineId(((TextRegionLocation) location)
							.getRawStartLine());
		}
		finding.setValue(KEY_LOCATION, new HTMLLink(
				location.toLocationString(), href));
	}
}
