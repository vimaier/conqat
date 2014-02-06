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
package org.conqat.engine.persistence.store.base;

import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.persistence.store.IStorageSystem;
import org.conqat.engine.persistence.store.IStore;
import org.conqat.engine.persistence.store.StorageException;

/**
 * Base class for a processor that requires a single store as input.
 * 
 * @author hummelb
 * @author $Author: juergens $
 * @version $Rev: 35197 $
 * @ConQAT.Rating GREEN Hash: C2FAC9B3CFA7E4DF499B50903C1A49B4
 */
public abstract class StorageConsumerProcessorBase extends ConQATProcessorBase {

	/** The storage system. */
	protected IStorageSystem storageSystem;

	/** The name of the store. */
	protected String storeName;

	/** The (cached) store. Use {@link #getStore()} to access this. */
	private IStore store;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "store", minOccurrences = 1, maxOccurrences = 1, description = "Sets the store this processor works on.")
	public void setStore(
			@AConQATAttribute(name = "system", description = "Reference to the storage system.") IStorageSystem storageSystem,
			@AConQATAttribute(name = "name", description = "Name of the accessed store.") String storeName) {
		this.storageSystem = storageSystem;
		this.storeName = storeName;
	}

	/**
	 * Returns the store to be used. The processor is responsible for closing
	 * the store. The store itself is cached in a variable, so this method may
	 * be called multiple times.
	 */
	protected IStore getStore() throws StorageException {
		if (store == null) {
			store = storageSystem.openStore(storeName);
		}
		return store;
	}

}