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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.conqat.engine.persistence.store.IKeyValueCallback;
import org.conqat.lib.commons.collections.ByteArrayWrapper;
import org.conqat.lib.commons.io.ByteArrayUtils;

/**
 * A callback that collects all values but is timestamp aware, i.e. for each
 * original key (i.e. without timestamp suffix) only the newest at or below the
 * boundary timestamp is kept.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46547 $
 * @ConQAT.Rating GREEN Hash: 2C31BB875D0A3CE6E5AAE6F31FFE7885
 */
/* package */abstract class TimestampAwareCollectingCallbackBase implements
		IKeyValueCallback {

	/**
	 * The suffix used for the timestamp keys. As we perform the comparison on
	 * the byte array level, this saves us from performing a conversion from
	 * long every time.
	 */
	private final byte[] timestampSuffix;

	/**
	 * The "best" values collected so far, i.e. those with the largest
	 * timestamp.
	 */
	private final Map<ByteArrayWrapper, CallbackEntry> values = new HashMap<ByteArrayWrapper, CallbackEntry>();

	/** Constructor. */
	public TimestampAwareCollectingCallbackBase(byte[] timestampSuffix) {
		this.timestampSuffix = timestampSuffix;
	}

	/** {@inheritDoc} */
	@Override
	public void callback(byte[] key, byte[] value) {
		// filter head revision keys
		if (ByteArrayUtils.isPrefix(HistorizingStoreBase.HEAD_PREFIX, key)) {
			return;
		}

		// extract original key without timestamp suffix
		byte[] originalKey = Arrays.copyOf(key, key.length - 1
				- ByteArrayUtils.LONG_BYTE_ARRAY_LENGTH);

		if (!isIncludedKey(originalKey)) {
			return;
		}

		ByteArrayWrapper mapKey = new ByteArrayWrapper(originalKey);
		if (isTimestampLessOrEqual(key, timestampSuffix)
				&& noBetterValueExists(key, mapKey)) {
			values.put(mapKey, new CallbackEntry(key, value));
		}
	}

	/**
	 * Returns true if no better (i.e. higher timestamp) was already found and
	 * placed in {@link #values}.
	 */
	private boolean noBetterValueExists(byte[] key, ByteArrayWrapper mapKey) {
		CallbackEntry currentValue = values.get(mapKey);
		return currentValue == null
				|| isTimestampLessOrEqual(currentValue.key, key);
	}

	/**
	 * Returns true if the timestamp part of key is less or equal to that of the
	 * other key. This is based on the lexicographical ordering of the timestamp
	 * suffix instead of extracting and comparing long values for performance
	 * reasons (especially to reduce the number of small arrays created, which
	 * can negatively affect garbage collection).
	 */
	private static boolean isTimestampLessOrEqual(byte[] key, byte[] otherKey) {
		for (int i = ByteArrayUtils.LONG_BYTE_ARRAY_LENGTH; i > 0; --i) {
			if (ByteArrayUtils.unsignedByte(key[key.length - i]) > ByteArrayUtils
					.unsignedByte(otherKey[otherKey.length - i])) {
				return false;
			}
			if (ByteArrayUtils.unsignedByte(key[key.length - i]) < ByteArrayUtils
					.unsignedByte(otherKey[otherKey.length - i])) {
				return true;
			}
		}

		// equal
		return true;
	}

	/** Returns the value for a given key. */
	public byte[] getValue(byte[] key) {
		CallbackEntry entry = values.get(new ByteArrayWrapper(key));
		if (entry == null || HistorizingStoreBase.isDeletionValue(entry.value)) {
			return null;
		}
		return entry.value;
	}

	/** Returns the value for a given key. */
	public boolean hasValue(byte[] key) {
		return values.keySet().contains(new ByteArrayWrapper(key));
	}

	/** Writes all stored keys to the given callback. */
	public void writeToCallback(IKeyValueCallback callback) {
		for (Map.Entry<ByteArrayWrapper, CallbackEntry> entry : values
				.entrySet()) {
			if (!HistorizingStoreBase.isDeletionValue(entry.getValue().value)) {
				callback.callback(entry.getKey().getBytes(),
						entry.getValue().value);
			}
		}
	}

	/** Returns whether this key may be included in the result. */
	protected abstract boolean isIncludedKey(byte[] key);

	/** A single entry for the callback map. */
	private static class CallbackEntry {

		/** The key. */
		final byte[] key;

		/** The value. */
		final byte[] value;

		/** Constructor. */
		public CallbackEntry(byte[] key, byte[] value) {
			this.key = key;
			this.value = value;
		}
	}
}
