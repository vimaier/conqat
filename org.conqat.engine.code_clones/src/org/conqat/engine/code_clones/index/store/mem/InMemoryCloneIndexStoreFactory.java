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
package org.conqat.engine.code_clones.index.store.mem;

import org.conqat.engine.code_clones.index.store.ICloneIndexStore;
import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * {@ConQAT.Doc}
 * 
 * @author hummelb
 * @author $Author: juergens $
 * @version $Rev: 34670 $
 * @ConQAT.Rating GREEN Hash: BEB0B98D3280BA7184EB0748CD6D07D9
 */
@AConQATProcessor(description = "Factory for a clone index store that resides completely in memory. "
		+ "No disk access is needed but also nothing will be persisted for later use.")
public class InMemoryCloneIndexStoreFactory extends ConQATProcessorBase {

	/** {@inheritDoc} */
	@Override
	public ICloneIndexStore process() {
		// no need to close this in the end
		return new InMemoryCloneIndexStore();
	}
}