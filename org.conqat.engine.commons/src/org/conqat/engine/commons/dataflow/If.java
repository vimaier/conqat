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

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: deissenb $
 * @version $Rev: 37508 $
 * @ConQAT.Rating GREEN Hash: 6D43A19B721BC460CDB6734CCF25673E
 */
@AConQATProcessor(description = "A data-flow if, that selects one input based on a boolean condition.")
public class If extends ConQATProcessorBase {

	/** The result. */
	private Object result;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "if", minOccurrences = 1, maxOccurrences = 1, description = ""
			+ "Provides the if condition.")
	public void setParameter(
			@AConQATAttribute(name = "condition", description = "The condition.") boolean condition,
			@APipelineSource @AConQATAttribute(name = "then", description = "Value used if the condition is true.") Object ifValue,
			@APipelineSource @AConQATAttribute(name = "else", description = "Value used if the condition is false.") Object elseValue) {
		if (condition) {
			result = ifValue;
		} else {
			result = elseValue;
		}
	}

	/** {@inheritDoc} */
	@Override
	public Object process() {
		return result;
	}
}
