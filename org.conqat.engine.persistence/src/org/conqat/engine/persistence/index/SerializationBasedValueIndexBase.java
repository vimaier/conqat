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

import java.io.IOException;
import java.io.Serializable;

import org.conqat.engine.persistence.store.IStore;
import org.conqat.engine.persistence.store.StorageException;
import org.conqat.lib.commons.io.SerializationUtils;

/**
 * Abstract base class for {@link ValueIndexBase} where the byte conversion is
 * just performed by Java serialization.
 * 
 * @param <T>
 *            the type stored as values.
 * 
 * @author $Author: heineman $
 * @version $Rev: 38258 $
 * @ConQAT.Rating GREEN Hash: E8D8F8CFFEBC8FACA1AC86179963F90B
 */
public abstract class SerializationBasedValueIndexBase<T extends Serializable>
		extends ValueIndexBase<T> {

	/** Constructor. */
	protected SerializationBasedValueIndexBase(IStore store) {
		super(store);
	}

	/** {@inheritDoc} */
	@Override
	protected byte[] valueToByteArray(T value) throws StorageException {
		try {
			return SerializationUtils.serializeToByteArray(value);
		} catch (IOException e) {
			throw new StorageException(e);
		}
	}

	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Override
	protected T byteArrayToValue(byte[] bytes) throws StorageException {
		try {
			return (T) SerializationUtils.deserializeFromByteArray(bytes,
					Thread.currentThread().getContextClassLoader());
		} catch (IOException e) {
			throw new StorageException(e);
		} catch (ClassNotFoundException e) {
			throw new StorageException(e);
		} catch (ClassCastException e) {
			throw new StorageException(e);
		}
	}
}
