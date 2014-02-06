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
package org.conqat.engine.persistence.store.profiler;

import java.util.List;

import org.conqat.engine.persistence.store.IKeyValueCallback;
import org.conqat.engine.persistence.store.IStorageSystem;
import org.conqat.engine.persistence.store.IStore;
import org.conqat.engine.persistence.store.StorageException;
import org.conqat.engine.persistence.store.base.DelegatingStorageSystemBase;
import org.conqat.engine.persistence.store.util.PartitionedStorageSystem;
import org.conqat.lib.commons.collections.PairList;

/**
 * Manages performance counters for storage access. The counted time includes
 * all time spent in methods of the {@link IStore} interface including time
 * spent in any callbacks provided to the scan methods. It does not include time
 * required for store creation and closing.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 47054 $
 * @ConQAT.Rating GREEN Hash: 1A85AA39C4915DB13D32241399862465
 */
public class StorageProfiler {

	/** Accumulates nanoseconds spent. */
	private long nanoSeconds = 0;

	/** The number of calls to the methods of an {@link IStore}. */
	private int numberOfCalls = 0;

	/**
	 * Returns time spent in the created storage systems rounded to milli
	 * seconds.
	 */
	public long getTimeMillis() {
		return (nanoSeconds + 500 * 1000) / 1000 / 1000;
	}

	/** Returns the number of calls to any method of decorated storage systems. */
	public int getNumberOfCalls() {
		return numberOfCalls;
	}

	/**
	 * Helper method for reporting time spent in the storage system relative to
	 * the start time returned via {@link System#nanoTime()}.
	 */
	private void reportTime(long startNanos) {
		nanoSeconds += System.nanoTime() - startNanos;
		numberOfCalls += 1;
	}

	/**
	 * Decorates a storage system to count all method invocations of created
	 * stores in this profiler.
	 */
	public IStorageSystem decorate(IStorageSystem storageSystem) {
		return new DelegatingStorageSystemBase(storageSystem) {
			@Override
			public IStore openStore(String name) throws StorageException {
				return new ProfilingStore(super.openStore(name));
			}
		};
	}

	/**
	 * Decorates a partitioned storage system to count all method invocations of
	 * created stores and storage systems in this profiler.
	 */
	public PartitionedStorageSystem decorate(
			final PartitionedStorageSystem partitionedStorageSystem) {
		return new PartitionedStorageSystem(null) {
			@Override
			public IStorageSystem getPartition(String partitionName) {
				return decorate(partitionedStorageSystem
						.getPartition(partitionName));
			}
		};
	}

	/** Delegate that measures exeution time for all {@link IStore} methods. */
	private class ProfilingStore implements IStore {

		/** Instance to delegate all calls to. */
		private final IStore store;

		/** Constructor. */
		public ProfilingStore(IStore store) {
			this.store = store;
		}

		/** {@inheritDoc} */
		@Override
		public byte[] get(byte[] key) throws StorageException {
			long start = System.nanoTime();
			try {
				return store.get(key);
			} finally {
				reportTime(start);
			}
		}

		/** {@inheritDoc} */
		@Override
		public List<byte[]> get(List<byte[]> keys) throws StorageException {
			long start = System.nanoTime();
			try {
				return store.get(keys);
			} finally {
				reportTime(start);
			}
		}

		/** {@inheritDoc} */
		@Override
		public void put(byte[] key, byte[] value) throws StorageException {
			long start = System.nanoTime();
			try {
				store.put(key, value);
			} finally {
				reportTime(start);
			}
		}

		/** {@inheritDoc} */
		@Override
		public void put(PairList<byte[], byte[]> keysValues)
				throws StorageException {
			long start = System.nanoTime();
			try {
				store.put(keysValues);
			} finally {
				reportTime(start);
			}
		}

		/** {@inheritDoc} */
		@Override
		public void remove(byte[] key) throws StorageException {
			long start = System.nanoTime();
			try {
				store.remove(key);
			} finally {
				reportTime(start);
			}
		}

		/** {@inheritDoc} */
		@Override
		public void remove(List<byte[]> keys) throws StorageException {
			long start = System.nanoTime();
			try {
				store.remove(keys);
			} finally {
				reportTime(start);
			}
		}

		/** {@inheritDoc} */
		@Override
		public void scan(byte[] beginKey, byte[] endKey,
				IKeyValueCallback callback) throws StorageException {
			long start = System.nanoTime();
			try {
				store.scan(beginKey, endKey, callback);
			} finally {
				reportTime(start);
			}
		}

		/** {@inheritDoc} */
		@Override
		public void scan(byte[] prefix, IKeyValueCallback callback)
				throws StorageException {
			long start = System.nanoTime();
			try {
				store.scan(prefix, callback);
			} finally {
				reportTime(start);
			}
		}

		/** {@inheritDoc} */
		@Override
		public void scan(List<byte[]> prefixes, IKeyValueCallback callback)
				throws StorageException {
			long start = System.nanoTime();
			try {
				store.scan(prefixes, callback);
			} finally {
				reportTime(start);
			}
		}

		/** {@inheritDoc} */
		@Override
		public void scanKeys(byte[] beginKey, byte[] endKey,
				IKeyValueCallback callback) throws StorageException {
			long start = System.nanoTime();
			try {
				store.scanKeys(beginKey, endKey, callback);
			} finally {
				reportTime(start);
			}
		}

		/** {@inheritDoc} */
		@Override
		public void scanKeys(byte[] prefix, IKeyValueCallback callback)
				throws StorageException {
			long start = System.nanoTime();
			try {
				store.scanKeys(prefix, callback);
			} finally {
				reportTime(start);
			}
		}
	}
}
