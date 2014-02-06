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
package org.conqat.engine.commons.dataflow;

import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.APipelineSource;
import org.conqat.engine.core.core.ConQATException;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: deissenb $
 * @version $Rev: 37537 $
 * @ConQAT.Rating GREEN Hash: F420539A8A1DA2ADB96C6BDAFDFB936E
 */
@AConQATProcessor(description = "This processor allows to make a parameter optional. "
		+ "If the actual value is not set, this processor throws an excecption, so probably it should be disabled in this case.")
public class Optional extends ConQATProcessorBase {

	/** The actual value. */
	protected Object actualValue;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "actual", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "If set, this value will be used. ")
	public void setActualValue(
			@APipelineSource @AConQATAttribute(name = "value", description = "Value") Object value)
			throws ConQATException {
		if (value == null) {
			throw new ConQATException(
					"null value for actual value not supported.");
		}
		actualValue = value;
	}

	/** {@inheritDoc} */
	@Override
	public Object process() throws ConQATException {
		if (actualValue == null) {
			throw new ConQATException("No input value provided!");
		}
		return actualValue;
	}
}
