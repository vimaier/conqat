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

import org.conqat.engine.persistence.store.IStore;
import org.conqat.lib.commons.string.StringUtils;

/**
 * An index for storing string values.
 * 
 * @author $Author: heineman $
 * @version $Rev: 38355 $
 * @ConQAT.Rating GREEN Hash: 33E4110B59FA8B70B913D19E1A15CCE5
 */
public class StringIndex extends ValueIndexBase<String> {

	/** Constructs a new index with the given store. */
	public StringIndex(IStore store) {
		super(store);
	}

	/** {@inheritDoc} */
	@Override
	protected byte[] valueToByteArray(String string) {
		return StringUtils.stringToBytes(string);
	}

	/** {@inheritDoc} */
	@Override
	protected String byteArrayToValue(byte[] bytes) {
		return StringUtils.bytesToString(bytes);
	}
}
