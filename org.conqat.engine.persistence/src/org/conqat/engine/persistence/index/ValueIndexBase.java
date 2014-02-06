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
package org.conqat.engine.persistence.index;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.conqat.engine.persistence.store.IStore;
import org.conqat.engine.persistence.store.StorageException;
import org.conqat.engine.persistence.store.util.ExceptionHandlingKeyValueCallbackBase;
import org.conqat.engine.persistence.store.util.StorageUtils;
import org.conqat.lib.commons.collections.PairList;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Abstract base class for indexes storing single values for string keys.
 * 
 * @param <T>
 *            the type stored as values.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 45633 $
 * @ConQAT.Rating GREEN Hash: B735675BED05D56D3B549109150360BD
 */
public abstract class ValueIndexBase<T> extends IndexBase {

	/** Constructor. */
	protected ValueIndexBase(IStore store) {
		super(store);
	}

	/**
	 * Returns the value for the given key or <code>null</code> if none is
	 * stored.
	 */
	public T getValue(String key) throws StorageException {
		if (isHiddenKey(key)) {
			return null;
		}

		byte[] bytes = store.getWithString(key);
		if (bytes == null) {
			return null;
		}
		return byteArrayToValue(bytes);
	}

	/**
	 * Retrieves the values of the given keys. If one of the keys is not in the
	 * index, <code>null</code> is returned for this element.
	 */
	public List<T> getValues(List<String> keys) throws StorageException {
		return this.getValues(keys, false);
	}

	/**
	 * Retrieves the values of the given keys. If one of the keys is not in the
	 * index and forceExistence is set to <code>true</code>, a
	 * {@link StorageException} is thrown. Otherwise, a <code>null</code> entry
	 * is added to the resulting list.
	 */
	public List<T> getValues(List<String> keys, boolean forceExistence)
			throws StorageException {
		List<byte[]> values = store.getWithStrings(keys);
		List<T> converted = new ArrayList<T>();
		for (int i = 0; i < keys.size(); ++i) {
			if (isHiddenKey(keys.get(i))) {
				converted.add(null);
				continue;
			}

			byte[] value = values.get(i);
			if (value == null) {
				if (forceExistence) {
					throw new StorageException("No value found for "
							+ keys.get(i));
				}
				converted.add(null);
			} else {
				converted.add(byteArrayToValue(value));
			}
		}
		return converted;
	}

	/** Sets the value for the given key. */
	public void setValue(String key, T value) throws StorageException {
		if (isHiddenKey(key)) {
			throw new StorageException(
					"May not modify this key as it is hidden!");
		}
		store.putWithString(key, valueToByteArray(value));
	}

	/** Removes the value of the given key. */
	public void removeValue(String key) throws StorageException {
		if (isHiddenKey(key)) {
			throw new StorageException(
					"May not modify this key as it is hidden!");
		}
		store.removeWithString(key);
	}

	/** Batch operation for setting multiple values at once. */
	public void setValues(PairList<String, T> values) throws StorageException {
		PairList<byte[], byte[]> byteValues = new PairList<byte[], byte[]>();
		for (int i = 0; i < values.size(); ++i) {
			if (isHiddenKey(values.getFirst(i))) {
				continue;
			}

			byteValues.add(StringUtils.stringToBytes(values.getFirst(i)),
					valueToByteArray(values.getSecond(i)));
		}
		store.put(byteValues);
	}

	/** Batch operation for removing multiple values at once. */
	public void removeValues(List<String> keys) throws StorageException {
		store.removeWithStrings(removeHidden(keys));
	}

	/** Returns a new list with hidden keys removed. */
	private List<String> removeHidden(List<String> input) {
		List<String> result = new ArrayList<String>();
		for (String s : input) {
			if (!isHiddenKey(s)) {
				result.add(s);
			}
		}
		return result;
	}

	/**
	 * Retrieves the names of all keys in the index. Note that this operation
	 * can be very slow if the index contains a large amount of data.
	 */
	public List<String> getAllKeys() throws StorageException {
		return removeHidden(StorageUtils.listStringKeys(store));
	}

	/**
	 * Retrieves all entries in this index. Note that this operation can be very
	 * slow if the index contains a large amount of data.
	 */
	public PairList<String, T> getAllEntries() throws StorageException {
		return getEntriesStartingWith(StringUtils.EMPTY_STRING);
	}

	/**
	 * Retrieves all entries in this index that start with the given prefix.
	 * Note that this operation can be very slow if the index returns a large
	 * amount of data.
	 */
	public PairList<String, T> getEntriesStartingWith(String prefix)
			throws StorageException {
		return getEntriesStartingWith(Collections.singletonList(prefix));
	}

	/**
	 * Retrieves all entries in this index that start with one of the given
	 * prefixes. Note that this operation can be very slow if the index returns
	 * a large amount of data.
	 */
	public PairList<String, T> getEntriesStartingWith(List<String> prefixes)
			throws StorageException {
		final PairList<String, T> result = new PairList<String, T>();
		ExceptionHandlingKeyValueCallbackBase callbackWrapper = new ExceptionHandlingKeyValueCallbackBase() {
			@Override
			protected void callbackWithException(byte[] key, byte[] value)
					throws StorageException {
				String stringKey = StringUtils.bytesToString(key);
				if (!isHiddenKey(stringKey)) {
					synchronized (result) {
						result.add(stringKey, byteArrayToValue(value));
					}
				}
			}
		};

		List<byte[]> bytePrefixes = new ArrayList<byte[]>(prefixes.size());
		for (String prefix : prefixes) {
			bytePrefixes.add(StringUtils.stringToBytes(prefix));
		}
		store.scan(bytePrefixes, callbackWrapper);
		callbackWrapper.throwCaughtException();

		return result;
	}

	/** Converts the value to a byte array */
	protected abstract byte[] valueToByteArray(T value) throws StorageException;

	/** Converts the byte array to a value */
	protected abstract T byteArrayToValue(byte[] bytes) throws StorageException;

	/** This method can be used to hide certain keys from the user. */
	@SuppressWarnings("unused")
	protected boolean isHiddenKey(String key) {
		return false;
	}

}
