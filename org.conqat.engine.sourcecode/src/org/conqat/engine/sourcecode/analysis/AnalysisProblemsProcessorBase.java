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
package org.conqat.engine.sourcecode.analysis;

import org.conqat.engine.commons.findings.FindingGroup;
import org.conqat.engine.commons.findings.location.ElementLocation;
import org.conqat.engine.commons.findings.util.FindingUtils;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.resource.ITokenResource;
import org.conqat.engine.sourcecode.resource.TokenElementProcessorBase;
import org.conqat.lib.commons.assessment.Assessment;
import org.conqat.lib.commons.assessment.ETrafficLightColor;

/**
 * Base class for processors that check for possible analysis problems.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 44267 $
 * @ConQAT.Rating GREEN Hash: E07DC79A88E7D624C0B2AEA148BE4B50
 */
public abstract class AnalysisProblemsProcessorBase extends
		TokenElementProcessorBase {

	/** Name of the category used. */
	private static final String CATEGORY_NAME = "Analysis Problems";

	/** The key used for storing assessments. */
	private final String assessmentKey;

	/** The key used for storing messages. */
	private final String messageKey;

	/** The name of the findings group created (if findings are enabled). */
	private final String findingsGroupName;

	/** {ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "findings", attribute = "key", optional = true, description = "If this key is set, findings for analysis problems are created.")
	public String findingsKey = null;

	/** The findings group. */
	private FindingGroup findingsGroup;

	/** Constructor. */
	protected AnalysisProblemsProcessorBase(String assessmentKey,
			String messageKey, String findingsGroupName) {
		this.assessmentKey = assessmentKey;
		this.messageKey = messageKey;
		this.findingsGroupName = findingsGroupName;
	}

	/** {@inheritDoc} */
	@Override
	protected void setUp(ITokenResource root) {
		NodeUtils.addToDisplayList(root, assessmentKey, messageKey);
		if (findingsKey != null) {
			NodeUtils.addToDisplayList(root, findingsKey);
			findingsGroup = NodeUtils.getFindingReport(root)
					.getOrCreateCategory(CATEGORY_NAME)
					.getOrCreateFindingGroup(findingsGroupName);
		}
	}

	/** {@inheritDoc} */
	@Override
	protected void processElement(ITokenElement element) throws ConQATException {
		String errorMessage = getAnalysisErrorMessage(element);

		if (errorMessage == null) {
			element.setValue(assessmentKey, new Assessment(
					ETrafficLightColor.GREEN));
		} else {
			element.setValue(assessmentKey, new Assessment(
					ETrafficLightColor.RED));
			element.setValue(messageKey, errorMessage);

			if (findingsKey != null) {
				FindingUtils.createAndAttachFinding(
						findingsGroup,
						errorMessage,
						element,
						new ElementLocation(element.getLocation(), element
								.getUniformPath()), findingsKey);
			}
		}
	}

	/**
	 * Template method that returns the error message for the analysis or null
	 * if no analysis problems occurred.
	 */
	protected abstract String getAnalysisErrorMessage(ITokenElement element)
			throws ConQATException;
}
