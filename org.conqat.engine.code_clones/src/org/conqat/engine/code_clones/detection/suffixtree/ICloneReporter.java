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
package org.conqat.engine.code_clones.detection.suffixtree;

import org.conqat.engine.code_clones.core.Clone;
import org.conqat.engine.core.core.ConQATException;

/**
 * The interface for a class accepting found clones.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43150 $
 * @ConQAT.Rating GREEN Hash: 18C4EAB0ABD48EE61CAABA4CC8157FF0
 */
public interface ICloneReporter {

	/** Starts reporting of a new clone class of given length. */
	void startCloneClass(int normalizedLength);

	/** Adds a clone to the clone class started with startCloneClass. */
	Clone addClone(int startPosition, int length) throws ConQATException;

	/**
	 * Completes a clone class.
	 * 
	 * @return true if this clone was accepted. Rejecting of clones can be used
	 *         to implement early filtering, as this might influence some
	 *         internal heuristics of the clone detector.
	 */
	boolean completeCloneClass() throws ConQATException;
}