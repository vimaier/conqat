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
package org.conqat.engine.architecture.assessment;

import java.util.ArrayList;
import java.util.Collection;

import org.conqat.engine.architecture.output.ERenderMode;
import org.conqat.engine.architecture.scope.AnnotatedArchitecture;
import org.conqat.engine.architecture.scope.DependencyPolicy;
import org.conqat.engine.commons.ConQATPipelineProcessorBase;
import org.conqat.engine.commons.node.NodeConstants;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.lib.commons.assessment.Assessment;
import org.conqat.lib.commons.assessment.ETrafficLightColor;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 38375 $
 * @ConQAT.Rating GREEN Hash: 89DDAB7B8AED05D8B9CAB587F6C2C42F
 */
@AConQATProcessor(description = "Creates an assessment that reflects the number of satisfied and violated policies.")
public class ArchitectureAssessmentAnnotator extends
		ConQATPipelineProcessorBase<AnnotatedArchitecture> {

	/** The render mode used. */
	private ERenderMode renderMode;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "render", minOccurrences = 1, maxOccurrences = 1, description = "Sets the render mode used.")
	public void setRenderMode(
			@AConQATAttribute(name = "mode", description = "The render mode (POLICIES, ASSESSMENT, VIOLATIONS, VIOLATIONS_AND_TOLERATIONS)") ERenderMode renderMode) {
		this.renderMode = renderMode;
	}

	/** {@inheritDoc} */
	@Override
	protected void processInput(AnnotatedArchitecture input) {

		Collection<DependencyPolicy> policies = new ArrayList<DependencyPolicy>();
		input.collectPolicies(policies);

		Assessment assessment = NodeUtils.getOrCreateAssessment(input,
				NodeConstants.SUMMARY);
		for (DependencyPolicy policy : policies) {
			// skip anything that is not part of the rendermode
			if (!renderMode.includePolicy(policy)) {
				continue;
			}

			ETrafficLightColor color = null;
			switch (policy.getAssessment()) {
			case INVALID:
				color = ETrafficLightColor.RED;
				break;
			case UNNECESSARY:
				// we rate unnecessary as green
				color = ETrafficLightColor.GREEN;
				break;
			case VALID:
				color = ETrafficLightColor.GREEN;
				break;
			default:
				throw new AssertionError("Not implemented");
			}
			assessment.add(color);
		}

	}

}