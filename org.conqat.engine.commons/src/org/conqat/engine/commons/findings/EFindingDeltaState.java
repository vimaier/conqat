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
package org.conqat.engine.commons.findings;

import java.util.List;

/**
 * Finding tracking state of baseline delta analysis. The sets of finding states
 * are disjunct.
 * 
 * @author $Author: juergens $
 * @version $Rev: 42081 $
 * @ConQAT.Rating GREEN Hash: 1947DEDF0EE609843566630C23B300C3
 */
public enum EFindingDeltaState {

	/** Finding was added w.r.t baseline */
	ADDED,

	/** Finding was present in baseline but not in head */
	REMOVED,

	/**
	 * Finding is present both in head and baseline. If
	 * FindingsDeltaProcessor#addFindingsInModifiedMethods is set to true, this
	 * state only contains common findings that are in unmodified code.
	 * Otherwise, it contains all common findings.
	 */
	COMMON,

	/**
	 * Finding is present in both head and baseline, but in code that was
	 * modified. This state is only used, if
	 * FindingsDeltaProcessor#addFindingsInModifiedMethods is set to true.
	 */
	IN_MODIFIED_CODE;

	/** Retrieve delta state from finding (or null). */
	public static EFindingDeltaState deltaState(Finding finding) {
		Object value = finding.getValue(EFindingKeys.DELTA_STATE.name());
		if (value instanceof EFindingDeltaState) {
			return (EFindingDeltaState) value;
		}
		return null;
	}

	/** Checks if finding carries stored delta state */
	public static boolean isInState(Finding finding, EFindingDeltaState state) {
		return state == deltaState(finding);
	}

	/** Store delta state in finding */
	public static void setStates(List<Finding> findings,
			EFindingDeltaState state) {
		for (Finding finding : findings) {
			setState(finding, state);
		}
	}

	/** Store delta state in finding */
	public static void setState(Finding finding, EFindingDeltaState state) {
		finding.setValue(EFindingKeys.DELTA_STATE.name(), state);
	}

}
