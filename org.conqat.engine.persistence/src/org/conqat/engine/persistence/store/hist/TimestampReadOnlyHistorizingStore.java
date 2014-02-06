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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.conqat.engine.persistence.store.IKeyValueCallback;
import org.conqat.engine.persistence.store.IStore;
import org.conqat.engine.persistence.store.StorageException;
import org.conqat.lib.commons.assertion.CCSMPre;
import org.conqat.lib.commons.date.DateUtils;
import org.conqat.lib.commons.io.ByteArrayUtils;

/**
 * This is a read-only store that reads from a specified timestamp. All write
 * operations will throw an exception. Reading from a given timestamp is more
 * expensive than reading from the head.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46547 $
 * @ConQAT.Rating GREEN Hash: 373964EDAB2FD66B47B0AE41973C7818
 */
public class TimestampReadOnlyHistorizingStore extends HistorizingStoreBase {

	/** The ranges used for finding the most recent value in a history access. */
	private static final long[] SCAN_RANGES = {
			DateUtils.daysToMilliseconds(1), DateUtils.daysToMilliseconds(16),
			DateUtils.daysToMilliseconds(256) };

	/** The suffix used for the timestamp keys. */
	private final byte[] timestampSuffix;

	/** The possible start timestamps to be used for scanning (inclusive). */
	private final List<byte[]> scanStartKeySuffixes = new ArrayList<byte[]>();

	/** The end timestamp to be used for scanning (exclusive). */
	private final byte[] scanEndTimestampSuffix;

	/** Constructor. */
	public TimestampReadOnlyHistorizingStore(IStore delegate, long readTimestamp) {
		super(delegate);
		CCSMPre.isTrue(readTimestamp > 0, "Timestamp must be positive!");
		this.timestampSuffix = ByteArrayUtils.longToByteArray(readTimestamp);

		for (long scanRange : SCAN_RANGES) {
			long startTimeStamp = readTimestamp - scanRange;
			if (startTimeStamp > 0) {
				this.scanStartKeySuffixes.add(ByteArrayUtils
						.longToByteArray(startTimeStamp));
			}
		}
		this.scanEndTimestampSuffix = ByteArrayUtils
				.longToByteArray(readTimestamp + 1);
	}

	/** {@inheritDoc} */
	@Override
	public byte[] get(final byte[] originalKey) throws StorageException {
		TimestampAwareCollectingCallbackBase callback = new TimestampAwareCollectingCallbackBase(
				timestampSuffix) {
			@Override
			public boolean isIncludedKey(byte[] key) {
				return Arrays.equals(key, originalKey);
			}
		};

		byte[] lastEndKey = revisionKey(originalKey, scanEndTimestampSuffix);
		for (byte[] startKeySuffix : scanStartKeySuffixes) {
			byte[] startKey = revisionKey(originalKey, startKeySuffix);
			store.scan(startKey, lastEndKey, callback);
			if (callback.hasValue(originalKey)) {
				return callback.getValue(originalKey);
			}
			lastEndKey = startKey;
		}

		// fallback is full scan
		store.scan(makeScanPrefix(originalKey), lastEndKey, callback);
		return callback.getValue(originalKey);
	}

	/**
	 * Creates a scan key by appending the
	 * {@link HistorizingStoreBase#TIMESTAMP_SEPARATOR}.
	 */
	private static byte[] makeScanPrefix(byte[] key) {
		byte[] result = Arrays.copyOf(key, key.length + 1);
		result[key.length] = TIMESTAMP_SEPARATOR;
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public List<byte[]> get(List<byte[]> keys) throws StorageException {
		// for multiple get query in the historized case it is still faster to
		// retrieve individual keys
		List<byte[]> result = new ArrayList<byte[]>();
		for (byte[] key : keys) {
			result.add(get(key));
		}
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public void scan(final byte[] beginKey, final byte[] endKey,
			IKeyValueCallback originalCallback) throws StorageException {
		TimestampAwareCollectingCallbackBase callback = new TimestampAwareCollectingCallbackBase(
				timestampSuffix) {
			@Override
			public boolean isIncludedKey(byte[] key) {
				return ByteArrayUtils.isLess(beginKey, key, true)
						&& ByteArrayUtils.isLess(key, endKey, false);
			}
		};
		store.scan(beginKey, endKey, callback);
		callback.writeToCallback(originalCallback);
	}

	/** {@inheritDoc} */
	@Override
	public void scan(final byte[] prefix, IKeyValueCallback originalCallback)
			throws StorageException {
		TimestampAwareCollectingCallbackBase callback = new TimestampAwareCollectingCallbackBase(
				timestampSuffix) {
			@Override
			public boolean isIncludedKey(byte[] key) {
				return ByteArrayUtils.isPrefix(prefix, key);
			}
		};
		store.scan(prefix, callback);
		callback.writeToCallback(originalCallback);
	}

	/** {@inheritDoc} */
	@Override
	public void scan(final List<byte[]> prefixes,
			IKeyValueCallback originalCallback) throws StorageException {
		TimestampAwareCollectingCallbackBase callback = new TimestampAwareCollectingCallbackBase(
				timestampSuffix) {
			@Override
			public boolean isIncludedKey(byte[] key) {
				for (byte[] prefix : prefixes) {
					if (ByteArrayUtils.isPrefix(prefix, key)) {
						return true;
					}
				}
				return false;
			}
		};
		store.scan(prefixes, callback);
		callback.writeToCallback(originalCallback);
	}

	/** {@inheritDoc} */
	@Override
	public void scanKeys(byte[] beginKey, byte[] endKey,
			IKeyValueCallback callback) throws StorageException {
		// we need the values anyway (to check for deletion), so we just use a
		// plain scan
		scan(beginKey, endKey, callback);
	}

	/** {@inheritDoc} */
	@Override
	public void scanKeys(byte[] prefix, IKeyValueCallback callback)
			throws StorageException {
		// we need the values anyway (to check for deletion), so we just use a
		// plain scan
		scan(prefix, callback);
	}
}
