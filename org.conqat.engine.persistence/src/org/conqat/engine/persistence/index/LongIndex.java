/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2011 The ConQAT Project                                   |
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

import org.conqat.engine.persistence.store.IStore;
import org.conqat.engine.persistence.store.StorageException;
import org.conqat.lib.commons.io.ByteArrayUtils;

/**
 * Store that can hold long values (e.g. timestamps).
 * 
 * @author $Author: heinemann $
 * @version $Rev: 42622 $
 * @ConQAT.Rating GREEN Hash: 2932C373D1FD8A2C7232177CAFB83F99
 */
public class LongIndex extends ValueIndexBase<Long> {

	/** Constructor. */
	public LongIndex(IStore store) {
		super(store);
	}

	/** {@inheritDoc} */
	@Override
	protected byte[] valueToByteArray(Long value) {
		return ByteArrayUtils.longToByteArray(value);
	}

	/** {@inheritDoc} */
	@Override
	protected Long byteArrayToValue(byte[] value) throws StorageException {
		try {
			return ByteArrayUtils.byteArrayToLong(value);
		} catch (IOException e) {
			throw new StorageException(e);
		}
	}

}