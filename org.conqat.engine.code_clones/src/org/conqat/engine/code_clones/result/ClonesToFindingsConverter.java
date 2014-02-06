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
package org.conqat.engine.code_clones.result;

import java.util.Map;

import org.conqat.engine.code_clones.core.Clone;
import org.conqat.engine.code_clones.core.CloneClass;
import org.conqat.engine.code_clones.detection.CloneDetectionResultElement;
import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.findings.EFindingKeys;
import org.conqat.engine.commons.findings.Finding;
import org.conqat.engine.commons.findings.FindingCategory;
import org.conqat.engine.commons.findings.FindingGroup;
import org.conqat.engine.commons.findings.util.FindingUtils;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.resource.IElement;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.util.ResourceTraversalUtils;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 45649 $
 * @ConQAT.Rating GREEN Hash: 013CCC48792F6DE9451183DACA9FD0F3
 */
@AConQATProcessor(description = "Creates findings for the contained clones and "
		+ "adds them to the findings report at the root node. "
		+ "Does not modify or remove the clones.")
public class ClonesToFindingsConverter extends DetectionResultProcessorBase {

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = ConQATParamDoc.FINDING_NAME, attribute = ConQATParamDoc.FINDING_KEY_NAME, optional = true, description = ConQATParamDoc.FINDING_KEY_DESC)
	public String key = "findings";

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "fingerprints", attribute = "include", optional = true, description = "Determines whether the fingerprint should be attached to each clone.")
	public boolean includeFingerprints = false;

	/** The category to work with. */
	private FindingCategory category;

	/** Mapping of uniform path to element. */
	private Map<String, IElement> uniformPathToElementMap;

	/** {@inheritDoc} */
	@Override
	public CloneDetectionResultElement process() {
		category = NodeUtils.getFindingReport(detectionResult.getRoot())
				.getOrCreateCategory("Clones");
		uniformPathToElementMap = ResourceTraversalUtils
				.createUniformPathToElementMap(detectionResult.getRoot(),
						IElement.class);

		convertClonesToFindings();
		return detectionResult;
	}

	/** Creates findings for the detected clones */
	private void convertClonesToFindings() {
		for (CloneClass cloneClass : detectionResult.getList()) {
			FindingGroup group = category.createFindingGroup("Clone class "
					+ cloneClass.getId());
			if (includeFingerprints) {
				group.setValue(EFindingKeys.FINGERPRINT.name(), cloneClass.getFingerprint());
			}
			for (Clone clone : cloneClass.getClones()) {
				IElement element = uniformPathToElementMap.get(clone
						.getUniformPath());
				if (!(element instanceof ITextElement)) {
					getLogger().warn(
							"Inconsistent clone report. Missing element for clone with uniform path "
									+ clone.getUniformPath());
					continue;
				}

				Finding finding = FindingUtils.createAndAttachFinding(group,
						"Clone (class-id " + clone.getCloneClass().getId() + ") of length " + clone.getLengthInUnits(), element,
						clone.getLocation(), key);

				// storing reference to clone to use in later processors
				finding.setValue(EFindingKeys.REFERENCE.name(), clone);

				// copy refactored key if present
				if (clone.containsValue(RefactoredCloneMarker.CLONE_KEY)) {
					finding.setValue(RefactoredCloneMarker.CLONE_KEY,
							clone.getValue(RefactoredCloneMarker.CLONE_KEY));
				}

				// If the fingerprint should be included, calculate and add it.
				if (includeFingerprints) {
					finding.setValue(EFindingKeys.FINGERPRINT.name(),
							clone.getFingerprint());
				}
			}
		}
	}
}