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
package org.conqat.engine.persistence.store.util;

import org.conqat.engine.persistence.store.IStorageSystem;
import org.conqat.engine.persistence.store.IStore;
import org.conqat.engine.persistence.store.StorageException;
import org.conqat.lib.commons.assertion.CCSMPre;

/**
 * This class allows to create virtual views (partitions) on an existing storage
 * system.
 * 
 * @author $Author: heineman $
 * @version $Rev: 38220 $
 * @ConQAT.Rating GREEN Hash: F93BF58225050DEBA63DDA147420A77C
 */
public class PartitionedStorageSystem {

	/**
	 * String used to separate the name of the partition and the store name. It
	 * is important that this will not appear in a partition name.
	 */
	private static final String SEPARATOR = "::";

	/** The underlying storage system. */
	private final IStorageSystem storageSystem;

	/** Constructor. */
	public PartitionedStorageSystem(IStorageSystem storageSystem) {
		this.storageSystem = storageSystem;
	}

	/** Returns a partition as an own (virtual) storage system. */
	public IStorageSystem getPartition(String partitionName) {
		CCSMPre.isFalse(partitionName.contains(SEPARATOR),
				"Partition name may not contain " + SEPARATOR);
		return new StorageSystemPartition(partitionName);
	}

	/** The storage system for the partition. */
	private class StorageSystemPartition implements IStorageSystem {

		/** The name of the partition. */
		private final String partitionName;

		/** Constructor. */
		private StorageSystemPartition(String partitionName) {
			this.partitionName = partitionName;
		}

		/** {@inheritDoc} */
		@Override
		public IStore openStore(String storeName) throws StorageException {
			return storageSystem.openStore(partitionName + SEPARATOR
					+ storeName);
		}

		/** {@inheritDoc} */
		@Override
		public void removeStore(String storeName) throws StorageException {
			storageSystem.removeStore(partitionName + SEPARATOR + storeName);
		}

		/** {@inheritDoc} */
		@Override
		public void close() {
			// does nothing
		}
	}
}
