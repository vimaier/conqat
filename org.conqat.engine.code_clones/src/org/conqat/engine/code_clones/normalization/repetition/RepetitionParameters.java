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
package org.conqat.engine.code_clones.normalization.repetition;

import java.io.Serializable;

import org.conqat.engine.core.core.ConQATException;

/**
 * Parameter object that stores repetition detection parameters.
 * 
 * @author juergens
 * @author $Author: hummelb $
 * @version $Rev: 36296 $
 * @ConQAT.Rating GREEN Hash: FADB68796CD8F95333E87B7DCE7D3627
 */
public class RepetitionParameters implements Serializable {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	/** Minimal length of repetition in units */
	private final int minLength;

	/** Length of shortest repetition motif being searched for */
	private final int minMotifLength;

	/** Length of longest repetition motif being searched for */
	private final int maxMotifLength;

	/** Minimal required number of motive instances in repetition */
	private final int minMotifInstances;

	/**
	 * Constructor.
	 * 
	 * @throws ConQATException
	 *             if values are invalid
	 **/
	public RepetitionParameters(int minLength, int minMotifLength,
			int maxMotifLength, int minMotifInstances) throws ConQATException {

		check(minLength > 0, "Repetition min length must be > 0!");
		check(minMotifLength > 0, "Repetition min motif length must be > 0!");
		check(maxMotifLength >= minMotifLength,
				"Repetition max motif length must be > min motig length!");
		check(minMotifInstances >= 2,
				"Repetition min motif instances must ne >= 2!");

		this.minLength = minLength;
		this.minMotifLength = minMotifLength;
		this.maxMotifLength = maxMotifLength;
		this.minMotifInstances = minMotifInstances;
	}

	/** Throws a {@link ConQATException}, if a condition is violated */
	private void check(boolean condition, String exceptionMessage)
			throws ConQATException {
		if (!condition) {
			throw new ConQATException(exceptionMessage);
		}
	}

	/** Returns minimal length of repetition in units */
	public int getMinLength() {
		return minLength;
	}

	/** Returns length of shortest repetition motif being searched for */
	public int getMinMotifLength() {
		return minMotifLength;
	}

	/** Returns length of longest repetition motif being searched for */
	public int getMaxMotifLength() {
		return maxMotifLength;
	}

	/** Returns minimal required number of motive instances in repetition */
	public int getMinMotifInstances() {
		return minMotifInstances;
	}

}