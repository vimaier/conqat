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
package org.conqat.engine.commons;

import org.conqat.lib.commons.assertion.CCSMPre;
import org.conqat.engine.core.core.IConQATProcessor;
import org.conqat.engine.core.core.IConQATProcessorInfo;
import org.conqat.engine.core.logging.IConQATLogger;

/**
 * This is a base class for processors which implements the init method and
 * provides suitable getters for its contens, so the single processors do not
 * have to implement the init method.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43290 $
 * @ConQAT.Rating GREEN Hash: AAB3E64CA86E33FFB66B8DDFC94FCEAC
 */
public abstract class ConQATProcessorBase implements IConQATProcessor {

	/** The stored processor info. */
	private IConQATProcessorInfo processorInfo = null;

	/** The expected amount of overall work. */
	private int overallWork = 1;

	/** The amount of work performed so far. */
	private int workDone = 0;

	/** {@inheritDoc} */
	@Override
	public void init(IConQATProcessorInfo processorInfo) {
		this.processorInfo = processorInfo;
		processorInfo.reportProgress(0, 1);
	}

	/** Returns the stored processor info. */
	protected IConQATProcessorInfo getProcessorInfo() {
		return processorInfo;
	}

	/** Returns the logger for this processor. */
	protected IConQATLogger getLogger() {
		return processorInfo.getLogger();
	}

	/**
	 * Sets the overall amount of work to be performed (must be positive). This
	 * may only be called exactly once at the beginning of processor execution.
	 * and before {@link #workDone(int)} is called.
	 */
	protected void setOverallWork(int overallWork) {
		CCSMPre.isTrue(overallWork > 0, "Overall work must be positive!");
		CCSMPre.isTrue(workDone == 0, "No work may have been done!");
		this.overallWork = overallWork;
	}

	/**
	 * Indicates that a certain amount of work has been done (this amount should
	 * be positive). This is just a delta from the last time this method was
	 * called. Over the life time of the processor, the sum of "work done"
	 * should not be more than the overall work initially set. However, this
	 * method accounts for this case by increasing overall work (so the
	 * processor does not have to check for it).
	 */
	protected void workDone(int work) {
		CCSMPre.isTrue(work > 0, "Work must be positive!");
		workDone += work;
		if (workDone < 0) {
			// overflow
			workDone = Integer.MAX_VALUE;
		}
		if (workDone > overallWork) {
			overallWork = workDone;
		}
		processorInfo.reportProgress(workDone, overallWork);
	}
}