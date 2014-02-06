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
package org.conqat.engine.persistence.index.schema;

import org.conqat.engine.persistence.index.IndexBase;
import org.conqat.engine.persistence.store.IStorageSystem;
import org.conqat.engine.persistence.store.IStore;
import org.conqat.engine.persistence.store.StorageException;
import org.conqat.engine.persistence.store.hist.HistoryAccessOption;

/**
 * This is a wrapper/decorator for a storage system that provides a schema
 * backed index view.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 44965 $
 * @ConQAT.Rating GREEN Hash: 9D66D6DB9DB970A6FB4DA5A9F49DF990
 */
public class SchemaAwareStorageSystem implements IStorageSystem {

	/** The inner storage system to delegate to. */
	private final IStorageSystem storageSystem;

	/** The schema. */
	private final IndexSchema schema;

	/** Constructor that extracts the schema from the meta info table. */
	public SchemaAwareStorageSystem(IStorageSystem storageSystem)
			throws StorageException {
		this(storageSystem, IndexSchema.load(storageSystem));
	}

	/** Constructor. */
	public SchemaAwareStorageSystem(IStorageSystem storageSystem,
			IndexSchema schema) {
		this.storageSystem = storageSystem;
		this.schema = schema;
	}

	/** Returns the schema. */
	public IndexSchema getSchema() {
		return schema;
	}

	/**
	 * Opens/creates an index on top of the given store. This ensures that all
	 * required storage options are applied.
	 */
	public <T extends IndexBase> T openIndex(String storeName,
			Class<T> indexClass, HistoryAccessOption historyAccessOption)
			throws StorageException {
		return schema.openIndex(storeName, indexClass, storageSystem,
				historyAccessOption);
	}

	/** Opens the given store applying all storage options from the schema. */
	public IStore openStoreChecked(String storeName, Class<?> indexClass,
			IStorageSystem storageSystem, boolean rawAccess,
			HistoryAccessOption historyAccessOption) throws StorageException {
		return schema.openStoreChecked(storeName, indexClass, storageSystem,
				rawAccess, historyAccessOption);
	}

	/** {@inheritDoc} */
	@Override
	public IStore openStore(String name) throws StorageException {
		return storageSystem.openStore(name);
	}

	/** {@inheritDoc} */
	@Override
	public void removeStore(String storeName) throws StorageException {
		storageSystem.removeStore(storeName);
	}

	/** {@inheritDoc} */
	@Override
	public void close() throws StorageException {
		storageSystem.close();
	}
}
