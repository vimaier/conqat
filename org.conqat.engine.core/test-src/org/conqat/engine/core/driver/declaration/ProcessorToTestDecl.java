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
package org.conqat.engine.core.driver.declaration;

import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.IConQATProcessor;
import org.conqat.engine.core.core.IConQATProcessorInfo;

/**
 * This is the processor used for most of the declaration tests.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 464EA14A17419DAB6796E3AF5F024C4D
 */
@SuppressWarnings("unused")
@AConQATProcessor(description = "desc")
public class ProcessorToTestDecl implements IConQATProcessor {

	/** test method */
	@AConQATParameter(description = "a", name = "a")
	public void a(
			@AConQATAttribute(description = "a", name = "a") int a,
			@AConQATAttribute(description = "b", name = "b", defaultValue = "42") int b) {
		// nothing to do here
	}

	/** test method */
	@AConQATParameter(description = "b", name = "b", minOccurrences = 2)
	public void b() {
		// nothing to do here
	}

	/** test method */
	@AConQATParameter(description = "c", name = "c", maxOccurrences = 1)
	public void c() {
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