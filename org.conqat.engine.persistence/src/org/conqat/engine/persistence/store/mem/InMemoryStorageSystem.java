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
package org.conqat.engine.persistence.store.mem;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.conqat.engine.persistence.store.IStore;
import org.conqat.engine.persistence.store.StorageException;
import org.conqat.engine.persistence.store.base.StorageSystemBase;
import org.conqat.lib.commons.string.StringUtils;

/**
 * In memory implementation of a storage system. The store also supports
 * persistence, if a suitable directory is provided. So, if the directory
 * exists, data is read from this directory. Data is only persisted when storage
 * is {@link #close()}d.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46176 $
 * @ConQAT.Rating GREEN Hash: 515FFB852DE714F739CF036D7B273BC2
 */
public class InMemoryStorageSystem extends StorageSystemBase {

	/** The stores available. */
	private final Map<String, InMemoryStore> stores = new HashMap<String, InMemoryStore>();

	/**
	 * If this is not <code>null</code>, the contents of the individual stores
	 * will be persisted in this directory.
	 */
	private final File persistenceDirectory;

	/**
	 * Constructor.
	 * 
	 * @param persistenceDirectory
	 *            if this is not <code>null</code>, the contents of the
	 *            individual stores will be persisted in this directory.
	 */
	public InMemoryStorageSystem(File persistenceDirectory)
			throws StorageException {
		this.persistenceDirectory = persistenceDirectory;

		if (persistenceDirectory != null) {
			ensureStorageDirectory(persistenceDirectory);
		}
	}

	/** {@inheritDoc} */
	@Override
	public IStore openStore(String name) throws StorageException {
		InMemoryStore store = stores.get(name);
		if (store == null) {
			store = new InMemoryStore();
			if (persistenceDirectory != null) {
				File storeFile = new File(persistenceDirectory, name);
				if (storeFile.canRead()) {
					store.loadFromFile(storeFile);
				}
			}
			stores.put(name, store);
		}
		return store;
	}

	/** {@inheritDoc} */
	@Override
	public void removeStore(String storeName) {
		stores.remove(storeName);
		if (persistenceDirectory != null) {
			File storeFile = new File(persistenceDirectory, storeName);
			// we do not care if the file is not deleted
			storeFile.delete();
		}
	}

	/** {@inheritDoc}. */
	@Override
	public void close() throws StorageException {
		if (persistenceDirectory == null) {
			return;
		}

		for (Entry<String, InMemoryStore> entry : stores.entrySet()) {
			entry.getValue().dumpToFile(
					new File(persistenceDirectory, entry.getKey()));
		}
	}

	/** Removes all contents of this storage system. */
	public void clear() {
		for (InMemoryStore store : stores.values()) {
			store.clear();
		}
	}

	/**
	 * Returns statistics on the memory consumption of the storage system. This
	 * operation is potentially expensive and thus should not be called too
	 * often. This method is not thread-safe and should not be called
	 * concurrently to other store operations.
	 */
	public String getUsageStatistics(boolean detailed) {
		StringBuilder sb = new StringBuilder();
		for (Entry<String, InMemoryStore> entry : stores.entrySet()) {
			sb.append(entry.getKey() + ": "
					+ entry.getValue().getUsageStatistics(detailed)
					+ StringUtils.CR);
		}
		return sb.toString();
	}
}
