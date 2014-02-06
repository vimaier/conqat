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

import java.util.List;

import org.conqat.engine.persistence.store.IKeyValueCallback;
import org.conqat.engine.persistence.store.IStore;
import org.conqat.engine.persistence.store.StorageException;
import org.conqat.lib.commons.collections.PairList;

/**
 * A delegating store that rejects all operations that could modify the store.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46028 $
 * @ConQAT.Rating GREEN Hash: B1863EEEE654077F557DB3CB18364A60
 */
public class ReadOnlyStore implements IStore {

	/** The store to delegate to. */
	private final IStore store;

	/** Constructor. */
	public ReadOnlyStore(IStore store) {
		this.store = store;
	}

	/** {@inheritDoc} */
	@Override
	public byte[] get(byte[] key) throws StorageException {
		return store.get(key);
	}

	/** {@inheritDoc} */
	@Override
	public List<byte[]> get(List<byte[]> keys) throws StorageException {
		return store.get(keys);
	}

	/** {@inheritDoc} */
	@Override
	public void put(byte[] key, byte[] value) throws StorageException {
		denyWriteAccess();
	}

	/** This method denies write access by throwing an exception. */
	private void denyWriteAccess() throws StorageException {
		throw new StorageException("This store is read-only!");
	}

	/** {@inheritDoc} */
	@Override
	public void put(PairList<byte[], byte[]> keysValues)
			throws StorageException {
		denyWriteAccess();
	}

	/** {@inheritDoc} */
	@Override
	public void remove(byte[] key) throws StorageException {
		denyWriteAccess();
	}

	/** {@inheritDoc} */
	@Override
	public void remove(List<byte[]> keys) throws StorageException {
		denyWriteAccess();
	}

	/** {@inheritDoc} */
	@Override
	public void scan(byte[] beginKey, byte[] endKey, IKeyValueCallback callback)
			throws StorageException {
		store.scan(beginKey, endKey, callback);
	}

	/** {@inheritDoc} */
	@Override
	public void scan(byte[] prefix, IKeyValueCallback callback)
			throws StorageException {
		store.scan(prefix, callback);
	}

	/** {@inheritDoc} */
	@Override
	public void scan(List<byte[]> prefixes, IKeyValueCallback callback)
			throws StorageException {
		store.scan(prefixes, callback);
	}

	/** {@inheritDoc} */
	@Override
	public void scanKeys(byte[] beginKey, byte[] endKey,
			IKeyValueCallback callback) throws StorageException {
		store.scanKeys(beginKey, endKey, callback);
	}

	/** {@inheritDoc} */
	@Override
	public void scanKeys(byte[] prefix, IKeyValueCallback callback)
			throws StorageException {
		store.scanKeys(prefix, callback);
	}
}
