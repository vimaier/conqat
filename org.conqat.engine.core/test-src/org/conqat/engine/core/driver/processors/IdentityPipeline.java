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
import org.conqat.engine.core.core.APipelineSource;
import org.conqat.engine.core.core.IConQATProcessor;
import org.conqat.engine.core.core.IConQATProcessorInfo;

/**
 * This merely pipelines the input object.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 2F62DE28DD7D7C7C589FBE38163AE909
 */
@AConQATProcessor(description = "Pipeline processor.")
public class IdentityPipeline implements IConQATProcessor {

	/** The input of the pipeline */
	Object input = null;

	/** Set the input for the pipeline. */
	@SuppressWarnings("unused")
	@AConQATParameter(name = "input", minOccurrences = 1, maxOccurrences = 1, description = "")
	public void setInput(
			@APipelineSource
			@AConQATAttribute(name = "ref", description = "")
			Object input,
			@AConQATAttribute(name = "unused", defaultValue = "abc", description = "")
			String unused) {
		this.input = input;
	}

	/** {@inheritDoc} */
	@Override
	public void init(IConQATProcessorInfo processorInfo) {
		// nothing to do here
	}

	/** {@inheritDoc} */
	@Override
	public Object process() {
		return input;
	}

}