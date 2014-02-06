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
package org.conqat.engine.persistence.rollback;

import org.conqat.engine.persistence.index.schema.EStorageOption;
import org.conqat.engine.persistence.index.schema.IndexSchema;
import org.conqat.engine.persistence.index.schema.SchemaAwareStorageSystem;
import org.conqat.engine.persistence.index.schema.SchemaEntry;
import org.conqat.engine.persistence.store.StorageException;
import org.conqat.engine.persistence.store.hist.RollbackableHistorizingStore;

/**
 * Code for performing rollback on a store.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46881 $
 * @ConQAT.Rating GREEN Hash: F3346FC8602F3CDDFC9B7CB0CB6028B9
 */
public class StoreRollbackUtils {

	/**
	 * Returns whether the store described by the given schema entry supports
	 * rollback. This is the case if either rollback is explicitly forbidden/not
	 * needed, the store is historized, or the store uses an index that
	 * implements {@link IRollbackableIndex}
	 */
	public static boolean supportsRollback(SchemaEntry schemaEntry)
			throws StorageException {
		if (schemaEntry.usesOption(EStorageOption.NO_ROLLBACK)
				|| schemaEntry.usesOption(EStorageOption.HISTORIZED)) {
			return true;
		}

		return IRollbackableIndex.class
				.isAssignableFrom(createIndexClass(schemaEntry));
	}

	/** Creates the index class for a schema entry. */
	private static Class<?> createIndexClass(SchemaEntry schemaEntry)
			throws StorageException {
		Class<?> indexClass;
		try {
			indexClass = Class.forName(schemaEntry.getIndexClass(), true,
					Thread.currentThread().getContextClassLoader());
		} catch (ClassNotFoundException e) {
			throw new StorageException("Index class not found: "
					+ schemaEntry.getIndexClass(), e);
		}
		return indexClass;
	}

	/**
	 * Performs rollback for all stores in the given storage system, ignoring
	 * those for which {@link EStorageOption#NO_ROLLBACK} is set.
	 */
	public static void performRollback(SchemaAwareStorageSystem storageSystem,
			long timestamp) throws StorageException {
		IndexSchema schema = storageSystem.getSchema();
		for (String name : schema.getEntryNames()) {
			SchemaEntry schemaEntry = schema.getEntry(name);
			if (!schemaEntry.usesOption(EStorageOption.NO_ROLLBACK)) {
				performRollback(storageSystem, name, schema, timestamp);
			}
		}
	}

	/** Performs rollback on a single store. */
	public static void performRollback(SchemaAwareStorageSystem storageSystem,
			String storeName, IndexSchema schema, long timestamp)
			throws StorageException {
		SchemaEntry schemaEntry = schema.getEntry(storeName);
		if (schemaEntry.usesOption(EStorageOption.HISTORIZED)) {
			new RollbackableHistorizingStore(storageSystem.openStore(storeName))
					.performRollback(timestamp);
			return;
		}

		Class<?> indexClass = createIndexClass(schemaEntry);
		if (IRollbackableIndex.class.isAssignableFrom(indexClass)) {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			IRollbackableIndex index = (IRollbackableIndex) storageSystem
					.openIndex(storeName, (Class) indexClass, null);
			index.performRollback(timestamp);
			return;
		}

		throw new StorageException(
				"Don't know how to perform rollback for store " + storeName);
	}
}
