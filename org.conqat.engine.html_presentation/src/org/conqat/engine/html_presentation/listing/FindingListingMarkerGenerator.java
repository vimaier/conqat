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
package org.conqat.engine.html_presentation.listing;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.commons.findings.EFindingKeys;
import org.conqat.engine.commons.findings.Finding;
import org.conqat.engine.commons.findings.FindingReport;
import org.conqat.engine.commons.findings.location.ElementLocation;
import org.conqat.engine.commons.findings.location.TextRegionLocation;
import org.conqat.engine.commons.findings.util.FindingUtils;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.html_presentation.color.ColorizerBase;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.lib.commons.assessment.AssessmentUtils;
import org.conqat.lib.commons.assessment.ETrafficLightColor;
import org.conqat.lib.commons.collections.ListMap;
import org.conqat.lib.commons.color.ECCSMColor;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43290 $
 * @ConQAT.Rating GREEN Hash: 24041FD9B8A7EC57436C9D5EBD1388A4
 */
@AConQATProcessor(description = "A marker generator working on findings.")
public class FindingListingMarkerGenerator extends ListingMarkerGeneratorBase {

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "finding", attribute = "report", description = "The finding report used for marker creation.")
	public FindingReport findingReport;

	/** Maps uniform paths to findings. */
	private ListMap<String, Finding> uniformPathToFinding;

	/** {@inheritDoc} */
	@Override
	protected void setup() {
		uniformPathToFinding = FindingUtils.getFindingsByElement(findingReport);
	}

	/** {@inheritDoc} */
	@Override
	public List<ListingMarkerDescriptor> generateMarkers(ITextElement element) {
		List<Finding> findings = uniformPathToFinding.getCollection(element
				.getUniformPath());
		if (findings == null) {
			return null;
		}

		List<ListingMarkerDescriptor> result = new ArrayList<ListingMarkerDescriptor>();
		for (Finding finding : findings) {
			ElementLocation location = finding.getLocation();
			// we can not show line markers for plain ElementLocations
			if (!(location instanceof TextRegionLocation)) {
				continue;
			}

			int startLine = ((TextRegionLocation) location).getRawStartLine();
			int endLine = ((TextRegionLocation) location).getRawEndLine();

			String message = NodeUtils.getStringValue(finding,
					EFindingKeys.MESSAGE.toString(), "")
					+ " ("
					+ finding.getParent().getName() + ")";

			result.add(new ListingMarkerDescriptor(startLine, endLine,
					markerColor(finding), message));
		}
		return result;
	}

	/** Determine color of marker */
	private Color markerColor(Finding finding) {
		Color markerColor = ECCSMColor.RED.getColor();

		ETrafficLightColor assessment = (ETrafficLightColor) finding
				.getValue(EFindingKeys.ASSESSMENT.toString());
		if (assessment != null) {
			markerColor = AssessmentUtils.getColor(assessment);
		}

		Color storedColor = NodeUtils.getValue(finding,
				ColorizerBase.COLOR_KEY_DEFAULT, Color.class, null);
		if (storedColor != null) {
			markerColor = storedColor;
		}

		return markerColor;
	}

}
