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
 * @ConQAT.Rating GREEN Hash: CDDFEC0206FBB3939A88713957B7D663
 */
@AConQATProcessor(description = ""
		+ "Factory for a clone index store that works on sorted lists and resides completely in memory. "
		+ "This store is extremely memory efficient but is slow on non-batch updates, "
		+ "as the internal lists have to be re-sorted after each update. "
		+ "Thus, the store is best suited for one-time clone detection of large systems. "
		+ "No disk access is needed but also nothing will be persisted for later use. "
		+ "Memory consuption is approximately number of units in the system multiplied by "
		+ (SortedMemoryCloneIndexStore.CHUNK_INTS + 2) * 4 + " bytes.")
public class SortedMemoryCloneIndexStoreFactory extends ConQATProcessorBase {

	/** {@inheritDoc} */
	@Override
	public ICloneIndexStore process() {
		// no need to close this in the end
		return new SortedMemoryCloneIndexStore();
	}
}