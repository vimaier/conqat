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
package org.conqat.engine.code_clones.result;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.code_clones.core.Clone;
import org.conqat.engine.code_clones.core.utils.CloneUtils;
import org.conqat.engine.code_clones.detection.CloneDetectionResultElement;
import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.html_presentation.listing.ListingMarkerDescriptor;
import org.conqat.engine.html_presentation.listing.ListingMarkerGeneratorBase;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.lib.commons.collections.ListMap;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43764 $
 * @ConQAT.Rating GREEN Hash: 859FB023BEEEE89110397260F4DDBD26
 */
@AConQATProcessor(description = "Generates markers from clones.")
public class CloneListingMarkerGenerator extends ListingMarkerGeneratorBase {

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "detection-result", attribute = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_DESC)
	public CloneDetectionResultElement detectionResult;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "color", attribute = "name", optional = true, description = "The color to be used for the markers. Default is orange.")
	public Color color = Color.ORANGE;

	/** Mapping from uniform path to clones. */
	private ListMap<String, Clone> unformPathToClones;

	/** {@inheritDoc} */
	@Override
	protected void setup() {
		unformPathToClones = CloneUtils.initElementMapping(
				detectionResult.getList(), new ListMap<String, Clone>());
	}

	/** {@inheritDoc} */
	@Override
	public List<ListingMarkerDescriptor> generateMarkers(ITextElement element) {
		List<Clone> clones = unformPathToClones.getCollection(element
				.getUniformPath());
		if (clones == null) {
			return null;
		}

		List<ListingMarkerDescriptor> result = new ArrayList<ListingMarkerDescriptor>();
		for (Clone clone : clones) {
			StringBuilder message = new StringBuilder("Clone with ");
			for (Clone other : CloneUtils.getSiblings(clone)) {
				int startLine = other.getLocation().getRawStartLine();
				int endLine = other.getLocation().getRawEndLine();
				message.append("\n  " + other.getUniformPath() + " ("
						+ startLine + "-" + endLine + ")");
			}

			result.add(new ListingMarkerDescriptor(clone.getLocation()
					.getRawStartLine(), clone.getLocation().getRawEndLine(),
					color, message.toString()));
		}
		return result;
	}
}
