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
package org.conqat.engine.persistence.store.util;

import java.util.Collection;

import org.conqat.engine.persistence.store.IKeyValueCallback;

/**
 * Callback for collecting the keys.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 464EDBFE7927E93425B665B6666A35B2
 */
public class KeyCollectingCallback implements IKeyValueCallback {

	/** The collection to store the keys in. */
	private final Collection<byte[]> keys;

	/** Constructor. */
	public KeyCollectingCallback(Collection<byte[]> keys) {
		this.keys = keys;
	}

	/** {@inheritDoc} */
	@Override
	public void callback(byte[] key, byte[] value) {
		keys.add(key);
	}
}
