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
package org.conqat.engine.persistence.store.hist;

import java.util.List;

import org.conqat.engine.persistence.store.IKeyValueCallback;
import org.conqat.engine.persistence.store.IStore;
import org.conqat.engine.persistence.store.StorageException;

/**
 * This is a read-only store that reads from the head revision/timestamp. All
 * write operations will throw an exception.
 * 
 * @author $Author: heineman $
 * @version $Rev: 38851 $
 * @ConQAT.Rating GREEN Hash: 30477937960A86BDBEFDE8CBBCC9411C
 */
public class HeadReadOnlyHistorizingStore extends HistorizingStoreBase {

	/** Constructor. */
	public HeadReadOnlyHistorizingStore(IStore delegate) {
		super(delegate);
	}

	/** {@inheritDoc} */
	@Override
	public byte[] get(byte[] key) throws StorageException {
		return store.get(headKey(key));
	}

	/** {@inheritDoc} */
	@Override
	public List<byte[]> get(List<byte[]> keys) throws StorageException {
		return store.get(headKeyList(keys));
	}

	/** {@inheritDoc} */
	@Override
	public void scan(byte[] beginKey, byte[] endKey, IKeyValueCallback callback)
			throws StorageException {
		store.scan(headKey(beginKey), headKey(endKey),
				new HeadStrippingCallback(callback));
	}

	/** {@inheritDoc} */
	@Override
	public void scan(byte[] prefix, IKeyValueCallback callback)
			throws StorageException {
		store.scan(headKey(prefix), new HeadStrippingCallback(callback));
	}

	/** {@inheritDoc} */
	@Override
	public void scan(List<byte[]> prefixes, IKeyValueCallback callback)
			throws StorageException {
		store.scan(headKeyList(prefixes), new HeadStrippingCallback(callback));
	}

	/** {@inheritDoc} */
	@Override
	public void scanKeys(byte[] beginKey, byte[] endKey,
			IKeyValueCallback callback) throws StorageException {
		store.scanKeys(headKey(beginKey), headKey(endKey),
				new HeadStrippingCallback(callback));
	}

	/** {@inheritDoc} */
	@Override
	public void scanKeys(byte[] prefix, IKeyValueCallback callback)
			throws StorageException {
		store.scanKeys(headKey(prefix), new HeadStrippingCallback(callback));
	}
}
