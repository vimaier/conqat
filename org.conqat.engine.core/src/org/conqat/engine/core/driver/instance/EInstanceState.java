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
package org.conqat.engine.core.driver.instance;

import java.awt.Color;

import org.conqat.lib.commons.color.ECCSMColor;

/**
 * This enumeration describes the state of an instance. When merging these
 * values the one with the highest ordinal value will win, so the order in this
 * file is important.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 11773ACC852F26097F85C93B968A6719
 */
public enum EInstanceState {
	/**
	 * This is the minimal state which is used when aggregating states over the
	 * empty set.
	 */
	UNDEFINED(ECCSMColor.LIGHT_GRAY),

	/** Block/processor has been disabled. */
	DISABLED(ECCSMColor.LIGHT_BLUE),

	/** Instance was executed successfully. */
	RUN_SUCCESSFULLY(ECCSMColor.GREEN),

	/**
	 * This instance couldn't be run as providing instances failed or were
	 * disabled.
	 */
	FAILED_DUE_TO_MISSING_INPUT(ECCSMColor.YELLOW),

	/**
	 * Instance failed gracefully (with a
	 * {@link org.conqat.engine.core.core.ConQATException}).
	 */
	FAILED_GRACEFULLY(ECCSMColor.RED),

	/** Instance failed because an object could not be cloned. */
	FAILED_DUE_TO_CLONING_PROBLEM(ECCSMColor.DARK_RED),

	/** Instance failed badly (with any other exception). */
	FAILED_BADLY(ECCSMColor.DARK_RED),

	/** Instance was not executed yet. */
	NOT_RUN(ECCSMColor.LIGHT_GRAY);

	/**
	 * The color of this state. We use AWT color instead of CCSMColor for the
	 * type, so we could later offer a constructor that accepts a "simple"
	 * color.
	 */
	private final Color color;

	/** Constructor. */
	private EInstanceState(ECCSMColor color) {
		this.color = color.getColor();
	}

	/**
	 * Returns the preferred color to be used when displaying this state
	 * graphically.
	 */
	public Color getColor() {
		return color;
	}

	/** Returns those states that indicate failure. */
	public static EInstanceState[] getFailedStates() {
		// always return new instance to avoid modification
		return new EInstanceState[] { FAILED_BADLY,
				FAILED_DUE_TO_CLONING_PROBLEM, FAILED_DUE_TO_MISSING_INPUT,
				FAILED_GRACEFULLY };
	}

	/**
	 * Merge two states into one. Merging means that the one with the highest
	 * ordinal (i.e. position in this file) is returned.
	 */
	public static EInstanceState merge(EInstanceState state1,
			EInstanceState state2) {
		if (state1.ordinal() >= state2.ordinal()) {
			return state1;
		}
		return state2;
	}
}