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
package org.conqat.engine.commons.util;

import org.conqat.engine.commons.ConQATPipelineProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;

/**
 * {@ConQAT.Doc}
 * 
 * @author juergens
 * @author $Author: hummelb $
 * @version $Rev: 36404 $
 * @ConQAT.Rating GREEN Hash: 0CE42293847F84A2D317FEBBAB4997EA
 */
@AConQATProcessor(description = "Processor that can throw a ConQATException. "
		+ "Use this processor to suppress the execution of a part of a configuration, "
		+ "e.g. during debugging to speed up execution.")
public class FailingProcessor extends ConQATPipelineProcessorBase<Object> {

	/** Flag that determines whether an exception gets thrown */
	private boolean throwException = false;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "fail", description = "Determines whether an exception gets thrown", minOccurrences = 1, maxOccurrences = 1)
	public void setThrowException(
			@AConQATAttribute(name = "value", description = "Default is false.") boolean throwException) {
		this.throwException = throwException;
	}

	/** {@inheritDoc} */
	@Override
	protected void processInput(Object input) throws ConQATException {
		if (throwException) {
			throw new ConQATException(
					"Aborting execution. Set parameter of this processor to false to turn off the exception.");
		}
	}

}