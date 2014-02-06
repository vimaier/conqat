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

import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.IConQATProcessor;
import org.conqat.engine.core.core.IConQATProcessorInfo;

/**
 * {@ConQAT.Doc}
 * <p>
 * Used for testing split parameters.
 * 
 * @author $Author: deissenb $
 * @version $Rev: 37488 $
 * @ConQAT.Rating GREEN Hash: 011AC33602212538441F0CF398A35624
 */
@AConQATProcessor(description = "String generator for testing purposes.")
public class MultiAttributeStringGenerator implements IConQATProcessor {

	/** The value to output. */
	private String value;

	/** Set the value to be output. */
	@AConQATParameter(name = "set", description = "", minOccurrences = 1, maxOccurrences = 1)
	public void setValue(
			@AConQATAttribute(name = "part1", description = "") String part1,
			@AConQATAttribute(name = "part2", description = "") String part2) {
		this.value = part1 + " " + part2;
	}

	/** {@inheritDoc} */
	@Override
	public void init(IConQATProcessorInfo processorInfo) {
		// nothing to do here
	}

	/** {@inheritDoc} */
	@Override
	public String process() {
		return value;
	}
}