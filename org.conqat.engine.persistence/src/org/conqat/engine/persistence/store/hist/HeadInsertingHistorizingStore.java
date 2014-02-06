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

import org.conqat.engine.persistence.store.IStore;
import org.conqat.engine.persistence.store.StorageException;
import org.conqat.lib.commons.assertion.CCSMPre;
import org.conqat.lib.commons.collections.PairList;
import org.conqat.lib.commons.io.ByteArrayUtils;

/**
 * A store that inserts new data at the head. All reading operations work on the
 * head revision.
 * 
 * @author $Author: heineman $
 * @version $Rev: 38851 $
 * @ConQAT.Rating GREEN Hash: B266DDF6649FA34D21BE69C3B8007CEB
 */
public class HeadInsertingHistorizingStore extends HeadReadOnlyHistorizingStore {

	/** The suffix used for the timestamp keys. */
	private final byte[] timestampSuffix;

	/** Constructor. */
	public HeadInsertingHistorizingStore(IStore delegate, long timestamp) {
		super(delegate);

		CCSMPre.isTrue(timestamp > 0, "Timestamp must be positive!");
		this.timestampSuffix = ByteArrayUtils.longToByteArray(timestamp);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * We have to update both the head and the revision key.
	 */
	@Override
	public void put(byte[] key, byte[] value) throws StorageException {
		PairList<byte[], byte[]> keysValues = new PairList<byte[], byte[]>(2);
		keysValues.add(revisionKey(key, timestampSuffix), value);
		keysValues.add(headKey(key), value);
		store.put(keysValues);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * We have to update both the head and the revision key.
	 */
	@Override
	public void put(PairList<byte[], byte[]> keysValues)
			throws StorageException {
		PairList<byte[], byte[]> newKeysValues = new PairList<byte[], byte[]>();
		for (int i = 0; i < keysValues.size(); ++i) {
			newKeysValues.add(
					revisionKey(keysValues.getFirst(i), timestampSuffix),
					keysValues.getSecond(i));
			newKeysValues.add(headKey(keysValues.getFirst(i)),
					keysValues.getSecond(i));
		}
		store.put(newKeysValues);
	}

	/** {@inheritDoc} */
	@Override
	public void remove(byte[] key) throws StorageException {
		store.remove(headKey(key));

		// also persist deletion marker
		store.put(revisionKey(key, timestampSuffix), DELETION_MARKER);
	}

	/** {@inheritDoc} */
	@Override
	public void remove(List<byte[]> keys) throws StorageException {
		// we do not delegate to the other remove method to reduce the number of
		// method calls, as each call to the store is potentially expensive

		store.remove(headKeyList(keys));

		// also persist deletion markers
		PairList<byte[], byte[]> deletionMarkers = new PairList<byte[], byte[]>();
		for (byte[] key : keys) {
			deletionMarkers.add(revisionKey(key, timestampSuffix),
					DELETION_MARKER);
		}
		store.put(deletionMarkers);
	}
}
