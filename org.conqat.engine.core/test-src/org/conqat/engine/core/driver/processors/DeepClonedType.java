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
package org.conqat.engine.core.driver.processors;

import org.conqat.lib.commons.clone.IDeepCloneable;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.IConQATProcessor;
import org.conqat.engine.core.core.IConQATProcessorInfo;

/**
 * A minimalistic IConQATNode with a counter for the number of deepClone calls.
 * This is at the same time a simple processor just returning itself.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 36898 $
 * @ConQAT.Rating GREEN Hash: 32ECEAE22E02DD455B509472D72738AD
 */
@AConQATProcessor(description = "A minimalistic IConQATNode with a counter for "
		+ "the number of deepClone calls. This is at the same time a simple "
		+ "processor just returning itself.")
public class DeepClonedType implements IConQATProcessor, IDeepCloneable {

	/** Counts how often {@link #deepClone()} was called. */
	public int numberOfDeepCloneCalls = 0;

	/** {@inheritDoc} */
	@Override
	public DeepClonedType deepClone() {
		// we will not deepClone, but count the number of calls on this
		++numberOfDeepCloneCalls;
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public void init(IConQATProcessorInfo processorInfo) {
		// nothing to do here
	}

	/** {@inheritDoc} */
	@Override
	public DeepClonedType process() {
		return this;
	}
}