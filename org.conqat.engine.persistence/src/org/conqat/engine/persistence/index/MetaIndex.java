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
 * An index that is used to store meta information on the storage system, such
 * as schema information. This index is used to store class instances. We expect
 * that for each class only a single instance is stored, thus we use the class
 * name as key.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 44984 $
 * @ConQAT.Rating GREEN Hash: BAE4A418895FED9DFAECA993304DDA8D
 */
public class MetaIndex extends IndexBase {

	/** The name commonly used for the meta index. */
	public static final String NAME = "_meta";

	/** Constructor. */
	public MetaIndex(IStore store) {
		super(store);
	}

	/**
	 * Returns the object that is stored for the given type (or null if none is
	 * stored).
	 */
	@SuppressWarnings("unchecked")
	public <T extends Serializable> T getValue(Class<T> clazz)
			throws StorageException {

		byte[] bytes = store.getWithString(clazz.getName());
		if (bytes == null) {
			return null;
		}

		Object value;
		try {
			value = SerializationUtils.deserializeFromByteArray(bytes, Thread
					.currentThread().getContextClassLoader());
		} catch (ClassNotFoundException e) {
			throw new StorageException(e);
		} catch (IOException e) {
			throw new StorageException(e);
		}

		if (!clazz.isInstance(value)) {
			throw new StorageException(
					"Storage system returned object of unexpected type: "
							+ value.getClass());
		}

		return (T) value;
	}

	/**
	 * Sets a value using an explicit class. This is useful if the value used is
	 * a subclass, but the more general type should be used for storing.
	 */
	public <S extends Serializable, T extends S> void setValue(T value,
			Class<S> explicitClass) throws StorageException {
		try {
			store.putWithString(explicitClass.getName(),
					SerializationUtils.serializeToByteArray(value));
		} catch (IOException e) {
			throw new StorageException(e);
		}
	}

}
