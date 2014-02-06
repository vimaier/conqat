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
package org.conqat.engine.simulink.clones.normalize;

import java.util.regex.Pattern;

import org.conqat.lib.simulink.model.SimulinkBlock;

/**
 * Enum for the different kinds of port normalization supported.
 * 
 * @author hummelb
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 * @levd.rating GREEN Hash: 3D9250BA519B0138F9D60FA1CF275646
 */
public enum EPortNormalization {

	/** All ports are equal. */
	EQUAL,

	/** All ports are different. */
	DIFF,

	/** Special treatment for the sum block. */
	SUM,

	/** The first input is special, all other are same. */
	SPECIAL1,

	/** The second port is special, all other are same. */
	SPECIAL2;

	/** Returns a normalization of the given index in the provided block. */
	public String normalize(int index, SimulinkBlock block) {
		switch (this) {
		case EQUAL:
			return "0";
		case DIFF:
			return Integer.toString(index);
		case SUM:
			return normalizeSum(index, block);
		case SPECIAL1:
			return index == 1 ? "$" : "0";
		case SPECIAL2:
			return index == 2 ? "$" : "0";
		}
		throw new IllegalStateException("Unknown normalization: " + this);
	}

	/**
	 * Perform normalization for the Sum block.
	 * <p>
	 * The sum block needs special treatment, as its inputs can be marked as
	 * negated. As all negated ports as well as all non-negated ports are
	 * equivalent, we have to find out which are the negated ports. This can be
	 * read from a parameter of the sum block.
	 */
	private String normalizeSum(int index, SimulinkBlock block) {
		String inputs = null;
		if (block.getParameter("Inputs") != null) {
			inputs = block.getParameter("Inputs");
		}
		if (inputs == null && block.getParameter("data/inputs") != null) {
			// Fallback for targetlink
			inputs = block.getParameter("data/inputs");
		}
		if (inputs == null) {
			throw new IllegalStateException("No input field found!");
		}

		// shortcut for numeric parameter
		if (inputs.matches("\\d+")) {
			return "+";
		}

		// remove spacers
		String origIn = inputs;
		inputs = inputs.replaceAll(Pattern.quote("|"), "");
		if (index > inputs.length()) {
			throw new IllegalArgumentException("For block " + block.getId()
					+ ": accessing index " + index
					+ " of sum parameter string " + inputs + " (orig: "
					+ origIn + ")");
		}
		return inputs.substring(index - 1, index);
	}
}