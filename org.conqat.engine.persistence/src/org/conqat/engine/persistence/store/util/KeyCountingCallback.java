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

import org.conqat.engine.persistence.store.IKeyValueCallback;

/**
 * Callback for counting the keys.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 092427FF4EE8764939CC879441899AEB
 */
public class KeyCountingCallback implements IKeyValueCallback {

	/** The number of keys. */
	private int numKeys = 0;

	/** {@inheritDoc} */
	@Override
	public void callback(byte[] key, byte[] value) {
		numKeys++;
	}

	/** Returns numKeys. */
	public int getNumberOfKeys() {
		return numKeys;
	}
}
