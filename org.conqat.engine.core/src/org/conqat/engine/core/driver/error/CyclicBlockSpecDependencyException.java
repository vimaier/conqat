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
package org.conqat.engine.core.driver.error;

import org.conqat.engine.core.driver.specification.BlockSpecification;

/**
 * Exceptions of this class signal cycles in the block specification
 * dependencies, i.e. block specifications referencing each other in a cyclic
 * way.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 875ADB71AF99E3426F934668FD7E148C
 */
public class CyclicBlockSpecDependencyException extends BlockFileException {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	/**
	 * Flag indicating whether the cycle has been closed already (i.e. if the
	 * first element has been encountered a second time). This is used to stop
	 * adding strings to the cycle when stepping down the dependency stack.
	 */
	private boolean cycleClosed = false;

	/**
	 * Create new exception.
	 * 
	 * @param cycleOrigin
	 *            the starting point of the dependency cycle.
	 */
	public CyclicBlockSpecDependencyException(BlockSpecification cycleOrigin) {
		super(EDriverExceptionType.CYCLIC_BLOCK_DEPENDENCY,
				"Found cyclic dependency of block specifications!", cycleOrigin);
	}

	/**
	 * This method is used when handing the exception down the block
	 * specification initialization stack. The idea is that each specification
	 * that should have been initialized is appended to this exception while
	 * reaching it down the call stack.
	 * 
	 * @param blockSpecification
	 *            the block specification which was about to be initialized at
	 *            this level of the call stack.
	 */
	public void unwindDependencyStack(BlockSpecification blockSpecification) {
		if (cycleClosed) {
			// we found our cycle, so no more storing needed.
			return;
		}

		if (blockSpecification.getErrorLocation().equals(locations.get(0))) {
			cycleClosed = true;
		} else {
			locations.add(blockSpecification.getErrorLocation());
		}
	}
}