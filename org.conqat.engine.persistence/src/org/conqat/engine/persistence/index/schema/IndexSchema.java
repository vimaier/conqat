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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.conqat.engine.persistence.index.IndexBase;
import org.conqat.engine.persistence.index.MetaIndex;
import org.conqat.engine.persistence.store.IStorageSystem;
import org.conqat.engine.persistence.store.IStore;
import org.conqat.engine.persistence.store.StorageException;
import org.conqat.engine.persistence.store.hist.HistoryAccessOption;
import org.conqat.engine.persistence.store.util.CompressingStore;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.UnmodifiableSet;
import org.conqat.lib.commons.string.StringUtils;

/**
 * The schema describes the structure of the indexes layed over the store.
 * Basically, this is a list of {@link SchemaEntry}s, one for each store/index.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46174 $
 * @ConQAT.Rating GREEN Hash: 100B09629FCE212A2D593BBD132ADD9E
 */
public class IndexSchema implements Serializable {

	/** Serial version UID. */
	private static final long serialVersionUID = 1;

	/** Mapping from store name to entry. */
	private final Map<String, SchemaEntry> entries = new HashMap<String, SchemaEntry>();

	/** Decorates the created stored. */
	private transient IStoreDecorator decorator;

	/**
	 * Inserts or changes an entry. Using an entry of null is equivalent to
	 * deleting the entry.
	 */
	public void updateEntry(String storeName, SchemaEntry entry) {
		if (entry == null) {
			entries.remove(storeName);
		} else {
			entries.put(storeName, entry);
		}
	}

	/** Returns the entry for the given store (or null). */
	public SchemaEntry getEntry(String storeName) {
		return entries.get(storeName);
	}

	/** Returns the names of entries in this schema. */
	public UnmodifiableSet<String> getEntryNames() {
		return CollectionUtils.asUnmodifiable(entries.keySet());
	}

	/**
	 * Opens/creates an index on top of the given store. This ensures that all
	 * required storage options are applied.
	 */
	public <T extends IndexBase> T openIndex(String storeName,
			Class<T> indexClass, IStorageSystem storageSystem,
			HistoryAccessOption historyAccessOption) throws StorageException {
		IStore store = openStoreChecked(storeName, indexClass, storageSystem,
				false, historyAccessOption);
		try {
			return indexClass.getConstructor(IStore.class).newInstance(store);
		} catch (Exception e) {
			throw new StorageException("Failed to create index for "
					+ storeName + " (index class " + indexClass + ")", e);
		}
	}

	/** Opens the given store applying all storage options from the schema. */
	public IStore openStoreChecked(String storeName, Class<?> indexClass,
			IStorageSystem storageSystem, boolean rawAccess,
			HistoryAccessOption historyAccessOption) throws StorageException {
		SchemaEntry entry = entries.get(storeName);
		if (entry == null) {
			throw new StorageException("No schema entry for store '"
					+ storeName + "' found!");
		}

		if (!indexClass.getName().equals(entry.getIndexClass())) {
			throw new StorageException("Index classes do not match for '"
					+ storeName + "'. Schema requires " + entry.getIndexClass()
					+ " but caller requested " + indexClass.getName());
		}

		IStore store = applyStoreOptions(entry,
				storageSystem.openStore(storeName), rawAccess,
				historyAccessOption);
		return store;
	}

	/**
	 * This method applies options to the store and returns a (possibly new)
	 * store. The history access option is expected to be <code>null</code> for
	 * non-historized stores or raw access. For historized indexes, a non-null
	 * historyAccessOption has to be supplied.
	 */
	public IStore applyStoreOptions(SchemaEntry entry, IStore store,
			boolean rawAccess, HistoryAccessOption historyAccessOption) {

		if (entry.usesOption(EStorageOption.COMPRESSED)) {
			store = new CompressingStore(store);
		}

		CCSMAssert.isFalse(rawAccess && historyAccessOption != null,
				"Unexpected history option " + historyAccessOption
						+ " for raw access to " + entry);

		if (!rawAccess) {
			if (entry.usesOption(EStorageOption.HISTORIZED)) {
				CCSMAssert.isNotNull(historyAccessOption,
						"No history access option provided for historized store "
								+ entry);
				store = historyAccessOption.createStore(store);
			} else {
				CCSMAssert.isTrue(historyAccessOption == null,
						"Unexpected history access option "
								+ historyAccessOption
								+ " for non-historized store " + entry);
			}
		}

		// decorator is last, so we see the original data
		if (decorator != null) {
			store = decorator.decorate(store);
		}

		return store;
	}

	/** Sets the decorator (may be set to null to turn decorating off). */
	public void setDecorator(IStoreDecorator decorator) {
		this.decorator = decorator;
	}

	/**
	 * Loads a schema from the given storage system. Throws an exception if no
	 * schema was found.
	 */
	public static IndexSchema load(IStorageSystem storageSystem)
			throws StorageException {
		IndexSchema schema = loadInternal(storageSystem);
		if (schema == null) {
			throw new StorageException("Schema is missing from storage system.");
		}
		return schema;
	}

	/**
	 * Loads a schema from the given storage system. Returns null if no schema
	 * was found.
	 */
	private static IndexSchema loadInternal(IStorageSystem storageSystem)
			throws StorageException {
		IStore store = storageSystem.openStore(MetaIndex.NAME);
		return new MetaIndex(store).getValue(IndexSchema.class);
	}

	/** Returns whether the given storage system contains a valid schema. */
	public static boolean hasSchema(IStorageSystem storageSystem)
			throws StorageException {
		return loadInternal(storageSystem) != null;
	}

	/** Saves this schema to the meta information of the given storage system. */
	public void save(IStorageSystem storageSystem) throws StorageException {
		IStore store = storageSystem.openStore(MetaIndex.NAME);
		new MetaIndex(store).setValue(this, IndexSchema.class);
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (String storeName : CollectionUtils.sort(entries.keySet())) {
			SchemaEntry entry = entries.get(storeName);
			sb.append(storeName + ":" + entry.getIndexClass());
			for (EStorageOption option : entry.getStorageOptions()) {
				sb.append(":" + option.name());
			}
			sb.append(StringUtils.CR);
		}
		return sb.toString();
	}

}
