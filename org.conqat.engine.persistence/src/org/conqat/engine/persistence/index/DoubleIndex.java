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
 * Store that can hold double values.
 * 
 * @author $Author: heineman $
 * @version $Rev: 37965 $
 * @ConQAT.Rating GREEN Hash: 91B15793AC55BE5ABDCE314CAB326D5F
 */
public class DoubleIndex extends ValueIndexBase<Double> {

	/** Constructor. */
	public DoubleIndex(IStore store) {
		super(store);
	}

	/** {@inheritDoc} */
	@Override
	protected byte[] valueToByteArray(Double value) {
		return ByteArrayUtils.doubleToByteArray(value);
	}

	/** {@inheritDoc} */
	@Override
	protected Double byteArrayToValue(byte[] value) throws StorageException {
		try {
			return ByteArrayUtils.byteArrayToDouble(value);
		} catch (IOException e) {
			throw new StorageException(e);
		}
	}

}