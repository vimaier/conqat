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
package org.conqat.engine.core.driver.specification.processors;

import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.IConQATProcessor;
import org.conqat.engine.core.core.IConQATProcessorInfo;

/**
 * Processor for testing purposes.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: EB02CF8F3E7AFCDE7D50D4D7EECEF5C1
 */
@SuppressWarnings("unused")
@AConQATProcessor(description = "desc")
public class ProcessorToTestSpec implements IConQATProcessor {

	/** Test field */
	@AConQATFieldParameter(parameter = "field_p", attribute = "field_a", optional = true, description = "field_d")
	public double field;

	/** Test method. */
	@AConQATParameter(description = "xset_desc", name = "xset")
	public void setX(
			@AConQATAttribute(description = "at_a_desc", name = "at_a") int a,
			@AConQATAttribute(description = "at_b_desc", name = "at_b", defaultValue = "test") String b) {
		// nothing to do here
	}

	/** Test method. */
	@AConQATParameter(description = "mult_desc", name = "mult", minOccurrences = 7, maxOccurrences = 42)
	public void mult() {
		// nothing to do here
	}

	/** {@inheritDoc} */
	@Override
	public void init(IConQATProcessorInfo processorInfo) {
		// nothing to do here
	}

	/** {@inheritDoc} */
	@Override
	public Object process() {
		return null;
	}

}