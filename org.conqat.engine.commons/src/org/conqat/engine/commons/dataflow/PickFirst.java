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
 * @ConQAT.Rating GREEN Hash: 8C9D00C33DD96E4A1EB36E576898FA1E
 */
@AConQATProcessor(description = "Processor that returns the first provided parameter. "
		+ "This can be used as a simple way of merging results.")
public class PickFirst extends ConQATProcessorBase {

	/** The result. */
	private Object result;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "value", minOccurrences = 1, description = ""
			+ "Provides the values from which the first one available/enabled will be selected.")
	public void addValue(
			@APipelineSource @AConQATAttribute(name = "ref", description = "Referenced value.") Object value)
			throws ConQATException {
		if (value == null) {
			throw new ConQATException("Null value not allowed!");
		}

		if (result == null) {
			result = value;
		}
	}

	/** {@inheritDoc} */
	@Override
	public Object process() {
		return result;
	}
}
