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

import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.APipelineSource;
import org.conqat.engine.core.core.ConQATException;

/**
 * This is a base class for pipeline processors. The benefit of using this class
 * is that not all processors are cluttered with the setter for the pipeline
 * input and additionally this enforces the naming and documentation for this
 * parameter to be consistent.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 37013 $
 * @ConQAT.Rating GREEN Hash: B1399FFAE2F38576F3A462A6614E1202
 */
public abstract class ConQATPipelineProcessorBase<E> extends
		ConQATProcessorBase {

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = ConQATParamDoc.ENABLE_NAME, attribute = ConQATParamDoc.ENABLE_PROCESSOR_NAME, description = ConQATParamDoc.ENABLE_DESC
			+ " [Default is enabled]", optional = true)
	public boolean enable = true;

	/** The object we are working on */
	private E input;

	/** Set the root element to work on. */
	@AConQATParameter(name = ConQATParamDoc.INPUT_NAME, minOccurrences = 1, maxOccurrences = 1, description = ConQATParamDoc.INPUT_DESC)
	public void setRoot(
			@APipelineSource @AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC) E input) {
		this.input = input;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public E process() throws ConQATException {
		if (enable) {
			processInput(input);
		}
		return input;
	}

	/**
	 * This method is called exactly once on the process method. It is
	 * responsible for transforming the input produced by the pipeline. The
	 * object is then returned by the process method.
	 */
	protected abstract void processInput(E input) throws ConQATException;
}