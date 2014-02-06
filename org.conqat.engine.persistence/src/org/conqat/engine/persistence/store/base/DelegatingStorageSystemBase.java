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
package org.conqat.engine.persistence.store.base;

import org.conqat.engine.persistence.store.IStorageSystem;
import org.conqat.engine.persistence.store.IStore;
import org.conqat.engine.persistence.store.StorageException;

/**
 * Base class for storage systems that delegate all calls to another storage
 * system.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46343 $
 * @ConQAT.Rating GREEN Hash: BC6614853AAC8772A32B309DC4D6C8B4
 */
public abstract class DelegatingStorageSystemBase implements IStorageSystem {

	/** The storage system to delegate to. */
	protected final IStorageSystem delegateStorageSystem;

	/** Constructor. */
	protected DelegatingStorageSystemBase(IStorageSystem delegateStorageSystem) {
		this.delegateStorageSystem = delegateStorageSystem;
	}

	/** {@inheritDoc} */
	@Override
	public IStore openStore(String name) throws StorageException {
		return delegateStorageSystem.openStore(name);
	}

	/** {@inheritDoc} */
	@Override
	public void removeStore(String storeName) throws StorageException {
		delegateStorageSystem.removeStore(storeName);
	}

	/** {@inheritDoc} */
	@Override
	public void close() throws StorageException {
		delegateStorageSystem.close();
	}
}
