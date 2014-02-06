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
package org.conqat.engine.architecture.format;

import org.conqat.lib.commons.assessment.Assessment;
import org.conqat.lib.commons.assessment.ETrafficLightColor;

/**
 * An enumeration of valid policies. See also the XML simple type
 * 'policy-typeType' in architecture_assessment.xsd
 * 
 * @author Benjamin Hummel
 * @author Elmar Juergens
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: BBD30AC2BCCFF6F5ABE6A38AD307A1B2
 */
public enum EPolicyType {

	/** Dependencies explicitly allowed through an allow policy */
	ALLOW_EXPLICIT,

	/** Dependencies implicitly allowed (e.g. through default policy) */
	ALLOW_IMPLICIT,

	/** Dependencies explicitly denied through a deny policy */
	DENY_EXPLICIT,

	/** Dependencies implicitly denied (e.g. through default policy) */
	DENY_IMPLICIT,

	/** Dependencies that are tolerated */
	TOLERATE_EXPLICIT;

	/** Creates an {@link Assessment} based on the policy value */
	public Assessment toAssessment() {
		return new Assessment(toTrafficLightColor());
	}

	/** Returns the {@link ETrafficLightColor} corresponding to the policy value */
	public ETrafficLightColor toTrafficLightColor() {
		switch (this) {
		case ALLOW_EXPLICIT:
		case ALLOW_IMPLICIT:
			return ETrafficLightColor.GREEN;
		case DENY_EXPLICIT:
		case DENY_IMPLICIT:
			return ETrafficLightColor.RED;
		case TOLERATE_EXPLICIT:
			return ETrafficLightColor.YELLOW;
		default:
			throw new AssertionError("Branch not implemented for policy type "
					+ this);
		}
	}

	/** Returns whether this is an implicit policy type. */
	public boolean isImplicit() {
		return this == ALLOW_IMPLICIT || this == DENY_IMPLICIT;
	}
}