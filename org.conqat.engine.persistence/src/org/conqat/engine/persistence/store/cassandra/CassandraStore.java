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
package org.conqat.engine.persistence.store.cassandra;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.prettyprint.cassandra.serializers.BytesArraySerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.service.template.ColumnFamilyResult;
import me.prettyprint.cassandra.service.template.ColumnFamilyTemplate;
import me.prettyprint.cassandra.service.template.ColumnFamilyUpdater;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.OrderedRows;
import me.prettyprint.hector.api.beans.Row;
import me.prettyprint.hector.api.exceptions.HTimedOutException;
import me.prettyprint.hector.api.exceptions.HectorException;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;
import me.prettyprint.hector.api.query.MultigetSliceQuery;
import me.prettyprint.hector.api.query.RangeSlicesQuery;

import org.conqat.engine.persistence.store.IKeyValueCallback;
import org.conqat.engine.persistence.store.StorageException;
import org.conqat.engine.persistence.store.TimeoutException;
import org.conqat.engine.persistence.store.base.PartitionStoreBase;
import org.conqat.lib.commons.collections.ByteArrayWrapper;
import org.conqat.lib.commons.collections.PairList;

/**
 * Store implementation for the {@link CassandraStorageSystem}.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 46954 $
 * @ConQAT.Rating GREEN Hash: EEF3AEC456ABE6035CC52EF9C261E6FD
 */
public class CassandraStore extends PartitionStoreBase {

	/** Maximal number of rows returned per page when scanning for rows. */
	private static final int MAX_SCAN_ROWS = 100;

	/**
	 * Maximal number of rows to include in batch operations (get, put, remove).
	 */
	private static final int MAX_BATCH_ROWS = 1000;

	/** The keyspace used for storing data. */
	private final Keyspace keyspace;

	/** Template for the column family. */
	private final ColumnFamilyTemplate<byte[], String> template;

	/** Constructor. */
	public CassandraStore(String name, Keyspace keyspace,
			ColumnFamilyTemplate<byte[], String> template) {
		super(name);
		this.keyspace = keyspace;
		this.template = template;
	}

	/** {@inheritDoc} */
	@Override
	protected byte[] doGet(byte[] key) throws StorageException {
		try {
			ColumnFamilyResult<byte[], String> res = template.queryColumns(key);
			return res.getByteArray(CassandraStorageSystem.COLUMN_NAME);
		} catch (HectorException e) {
			handleHectorException(e);
			return null;
		}
	}

	/** Handles a {@link HectorException} . */
	private void handleHectorException(HectorException e)
			throws StorageException {
		if (e instanceof HTimedOutException) {
			throw new TimeoutException(e);
		} else if (e.getMessage().startsWith("All host pools marked down")) {
			throw new TimeoutException(e);
		}
		throw new StorageException(e);
	}

	/** {@inheritDoc} */
	@Override
	protected List<byte[]> doBatchGet(List<byte[]> keys)
			throws StorageException {
		try {
			List<byte[]> result = new ArrayList<byte[]>();
			for (int i = 0; i < keys.size(); i += MAX_BATCH_ROWS) {
				List<byte[]> subKeyList = keys.subList(i,
						Math.min(i + MAX_BATCH_ROWS, keys.size()));
				result.addAll(batchGetSubList(subKeyList));
			}
			return result;
		} catch (HectorException e) {
			handleHectorException(e);
			return null;
		}
	}

	/** Implements a batch get operation for a list of keys. */
	private List<byte[]> batchGetSubList(List<byte[]> keys) {
		MultigetSliceQuery<byte[], String, byte[]> multigetSliceQuery = HFactory
				.createMultigetSliceQuery(keyspace, BytesArraySerializer.get(),
						StringSerializer.get(), BytesArraySerializer.get());
		multigetSliceQuery
				.setColumnFamily(CassandraStorageSystem.COLUMN_FAMILY_NAME);
		multigetSliceQuery.setKeys(keys);
		multigetSliceQuery.setColumnNames(CassandraStorageSystem.COLUMN_NAME);

		Map<ByteArrayWrapper, Integer> keyToIndex = new HashMap<ByteArrayWrapper, Integer>();
		int index = 0;
		for (byte[] key : keys) {
			keyToIndex.put(new ByteArrayWrapper(key), index);
			index += 1;
		}

		List<byte[]> resultList = new ArrayList<byte[]>(
				Collections.<byte[]> nCopies(keys.size(), null));
		for (Row<byte[], String, byte[]> row : multigetSliceQuery.execute()
				.get()) {
			List<HColumn<String, byte[]>> columns = row.getColumnSlice()
					.getColumns();
			if (!columns.isEmpty()) {
				int i = keyToIndex.get(new ByteArrayWrapper(row.getKey()));
				resultList.set(i, columns.get(0).getValue());
			}
		}
		return resultList;
	}

	/** {@inheritDoc} */
	@Override
	protected void doPut(byte[] key, byte[] value) throws StorageException {
		try {
			ColumnFamilyUpdater<byte[], String> updater = template
					.createUpdater(key);
			updater.setByteArray(CassandraStorageSystem.COLUMN_NAME, value);
			template.update(updater);
		} catch (HectorException e) {
			handleHectorException(e);
		}
	}

	/** {@inheritDoc} */
	@Override
	protected void doBatchPut(PairList<byte[], byte[]> keysValues)
			throws StorageException {
		try {
			Mutator<byte[]> mutator = template.createMutator();
			for (int i = 0; i < keysValues.size(); ++i) {
				HColumn<String, byte[]> column = HFactory.createColumn(
						CassandraStorageSystem.COLUMN_NAME,
						keysValues.getSecond(i));
				mutator.addInsertion(keysValues.getFirst(i),
						CassandraStorageSystem.COLUMN_FAMILY_NAME, column);

				if ((i + 1) % MAX_BATCH_ROWS == 0) {
					mutator.execute();
				}
			}
			mutator.execute();
		} catch (HectorException e) {
			handleHectorException(e);
		}
	}

	/** {@inheritDoc} */
	@Override
	protected void doRemove(byte[] key) throws StorageException {
		try {
			template.deleteRow(key);
		} catch (HectorException e) {
			handleHectorException(e);
		}
	}

	/** {@inheritDoc} */
	@Override
	protected void doBatchRemove(List<byte[]> keys) throws StorageException {
		try {
			Mutator<byte[]> mutator = template.createMutator();
			int index = 0;
			for (byte[] key : keys) {
				mutator.addDeletion(key,
						CassandraStorageSystem.COLUMN_FAMILY_NAME);
				index += 1;
				if (index % MAX_BATCH_ROWS == 0) {
					mutator.execute();
				}
			}
			mutator.execute();
		} catch (HectorException e) {
			handleHectorException(e);
		}
	}

	/** {@inheritDoc} */
	@Override
	protected void doScan(byte[] beginKey, byte[] endKey,
			IKeyValueCallback callback, boolean includeValue)
			throws StorageException {
		try {
			RangeSlicesQuery<byte[], String, byte[]> query = prepareRangeQuery(
					beginKey, endKey, includeValue);
			byte[] lastKey = null;
			while (true) {
				if (lastKey != null) {
					query.setKeys(lastKey, endKey);
				}

				OrderedRows<byte[], String, byte[]> rows = query.execute()
						.get();
				lastKey = reportScanData(rows, callback, endKey, includeValue);

				if (rows.getCount() < MAX_SCAN_ROWS) {
					break;
				}
			}
		} catch (HectorException e) {
			handleHectorException(e);
		}
	}

	/**
	 * Reports the data in the given row to the callback. Returns the last
	 * reported key (or null if no data was reported).
	 */
	private byte[] reportScanData(OrderedRows<byte[], String, byte[]> rows,
			IKeyValueCallback callback, byte[] endKey, boolean includeValue) {
		byte[] lastKey = null;
		for (Row<byte[], String, byte[]> row : rows) {
			byte[] key = row.getKey();
			lastKey = key;

			// cassandra's end key is inclusive, ours is not
			if (Arrays.equals(endKey, key)) {
				continue;
			}

			byte[] value = null;
			if (includeValue) {
				List<HColumn<String, byte[]>> columns = row.getColumnSlice()
						.getColumns();
				if (columns.isEmpty()) {
					continue;
				}
				value = columns.get(0).getValue();
			}
			callback.callback(key, value);
		}
		return lastKey;
	}

	/** Creates and initializes a range query. */
	private RangeSlicesQuery<byte[], String, byte[]> prepareRangeQuery(
			byte[] beginKey, byte[] endKey, boolean includeValue) {
		RangeSlicesQuery<byte[], String, byte[]> query = HFactory
				.createRangeSlicesQuery(keyspace, BytesArraySerializer.get(),
						StringSerializer.get(), BytesArraySerializer.get());
		query.setColumnFamily(CassandraStorageSystem.COLUMN_FAMILY_NAME);
		query.setColumnNames(CassandraStorageSystem.COLUMN_NAME);
		if (!includeValue) {
			query.setReturnKeysOnly();
		}
		query.setRowCount(MAX_SCAN_ROWS);
		query.setKeys(beginKey, endKey);
		return query;
	}
}
