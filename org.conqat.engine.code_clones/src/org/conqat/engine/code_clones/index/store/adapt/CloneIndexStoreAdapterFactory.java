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
package org.conqat.engine.code_clones.index.store.adapt;

import org.conqat.engine.code_clones.index.store.ICloneIndexStore;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.core.IShutdownHook;
import org.conqat.engine.persistence.store.base.StorageConsumerProcessorBase;

/**
 * {@ConQAT.Doc}
 * 
 * @author hummelb
 * @author $Author: juergens $
 * @version $Rev: 34670 $
 * @ConQAT.Rating GREEN Hash: 6717B9D35C439E22183E7A2F58680893
 */
@AConQATProcessor(description = "Factory for a clone index store that uses the ConQAT storage back-end. "
		+ "Closing the store is performed on shutdown.")
public class CloneIndexStoreAdapterFactory extends StorageConsumerProcessorBase {

	/** {@inheritDoc} */
	@Override
	public ICloneIndexStore process() throws ConQATException {
		CloneIndexStoreAdapter cloneStore = new CloneIndexStoreAdapter(
				getStore());
		closeStoreOnExit(cloneStore);
		return cloneStore;
	}

	/** Makes sure the store is closed during shutdown (register hook). */
	private void closeStoreOnExit(final CloneIndexStoreAdapter cloneStore) {
		getProcessorInfo().registerShutdownHook(new IShutdownHook() {
			@Override
			public void performShutdown() throws ConQATException {
				cloneStore.close();
			}
		}, false);
	}
}