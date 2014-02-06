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
package org.conqat.engine.core.driver.specification.processors;

import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.APipelineSource;

/**
 * Processor for testing purposes.
 * 
 * @author $Author: deissenb $
 * @version $Rev: 37499 $
 * @ConQAT.Rating GREEN Hash: 91BFDF7E72C3AFF7A598099DA74DDAED
 */
@AConQATProcessor(description = "desc")
public class ProcessorWithValidPipelineMultiplicity extends
		ProcessorWithOptionalPipelineMultiplicity {

	/** test method */
	@SuppressWarnings("unused")
	@AConQATParameter(description = "mult_desc", name = "mult2", minOccurrences = 7, maxOccurrences = 42)
	public void mult2(
			@AConQATAttribute(name = "a2", description = "a") @APipelineSource Object a) {
		// nothing to do here
	}

}
