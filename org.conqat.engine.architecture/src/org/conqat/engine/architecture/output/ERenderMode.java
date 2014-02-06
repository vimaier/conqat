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
package org.conqat.engine.architecture.output;

import org.conqat.engine.architecture.format.EAssessmentType;
import org.conqat.engine.architecture.format.EPolicyType;
import org.conqat.engine.architecture.scope.DependencyPolicy;
import org.conqat.lib.commons.assessment.ETrafficLightColor;

/**
 * The mode used for rendering.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 41275 $
 * @ConQAT.Rating GREEN Hash: 7D50AE2227EF7FD11B84D0438D52F920
 */
public enum ERenderMode {

	/** Renders plain policies. */
	POLICIES,

	/** Renders assessments (red, yellow, green). */
	ASSESSMENT,

	/** Renders violations only (red). */
	VIOLATIONS,

	/** Renders violations and tolerations (red, yellow). */
	VIOLATIONS_AND_TOLERATIONS;

	/**
	 * Returns whether a policy should be included when using this render mode.
	 */
	public boolean includePolicy(DependencyPolicy edge) {
		switch (this) {
		case POLICIES:
			return edge.getType() != EPolicyType.ALLOW_IMPLICIT
					&& edge.getType() != EPolicyType.DENY_IMPLICIT;
		case VIOLATIONS:
			return edge.getAssessment() == EAssessmentType.INVALID;
		case VIOLATIONS_AND_TOLERATIONS:
			return edge.getAssessment() == EAssessmentType.INVALID
					|| edge.getType() == EPolicyType.TOLERATE_EXPLICIT;
		default:
			return true;
		}
	}

	/** Returns the traffic light color used for a policy. */
	public ETrafficLightColor determineColor(DependencyPolicy edge) {
		if (this == POLICIES) {
			return edge.getType().toTrafficLightColor();
		}

		if (edge.getAssessment() == null) {
			return ETrafficLightColor.UNKNOWN;
		}
		if (edge.getAssessment() == EAssessmentType.INVALID) {
			return ETrafficLightColor.RED;
		}
		if (edge.getType() == EPolicyType.TOLERATE_EXPLICIT) {
			return ETrafficLightColor.YELLOW;
		}

		return ETrafficLightColor.GREEN;
	}
}