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
package org.conqat.engine.commons.util;

import org.conqat.engine.commons.ConQATPipelineProcessorBase;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: juergens $
 * @version $Rev: 38009 $
 * @ConQAT.Rating GREEN Hash: 653C3D020904BB4CC8ECDE13662A5E08
 */
@AConQATProcessor(description = "A processor that copies its input to its output."
		+ " This can be useful for decoupling block parameters or making optional parameters required.")
public class IdentityProcessor extends ConQATPipelineProcessorBase<Object> {

	/** {@inheritDoc} */
	@Override
	protected void processInput(Object input) {
		// does nothing
	}
}
