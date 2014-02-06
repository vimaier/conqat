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
package org.conqat.engine.commons.findings;

import java.awt.Color;
import java.util.Map;

import org.conqat.engine.commons.findings.typespec.FindingTypeSpec;
import org.conqat.lib.commons.assessment.ETrafficLightColor;

/**
 * Enumeration of well known finding keys.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46034 $
 * @ConQAT.Rating RED Hash: ECEA087658E5BC53089326F21CAEDDE0
 */
public enum EFindingKeys {

	/** Human readable message. */
	MESSAGE(String.class),

	/** Color. */
	COLOR(Color.class),

	/** Numerical measurement. */
	MEASUREMENT(Double.class),

	/** Assessment. */
	ASSESSMENT(ETrafficLightColor.class),

	/** A description on the structure of this this finding. */
	TYPESPEC(FindingTypeSpec.class),

	/** Source of a dependency if this finding models a dependency. */
	DEPENDENCY_SOURCE(String.class),

	/** Target of a dependency if this finding models a dependency. */
	DEPENDENCY_TARGET(String.class),

	/** Fingerprint that can be used to identify this finding. */
	FINGERPRINT(String.class),

	/** State of the finding w.r.t. the baseline. */
	DELTA_STATE(EFindingDeltaState.class),

	/** Reference to object from which finding was created */
	REFERENCE(Object.class),

	// TODO (LH) Name this PROPERTIES to be consistent?
	/**
	 * Metrics for the finding, i.e. numeric attributes. The map is from string
	 * to double.
	 */
	METRICS(Map.class);

	/** The type usually stored for the key. */
	private final Class<?> type;

	/** Constructor. */
	private EFindingKeys(Class<?> type) {
		this.type = type;
	}

	/** Returns the type stored for the key. */
	public Class<?> getType() {
		return type;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return name().toLowerCase();
	}
}