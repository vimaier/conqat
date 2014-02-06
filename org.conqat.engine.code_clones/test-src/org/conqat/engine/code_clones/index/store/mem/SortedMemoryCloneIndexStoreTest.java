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
package org.conqat.engine.code_clones.index.store.mem;

import org.conqat.engine.code_clones.index.store.CloneIndexStoreTestBase;
import org.conqat.engine.code_clones.index.store.ICloneIndexStore;

/**
 * Tests the {@link SortedMemoryCloneIndexStore}.
 * 
 * @author hummelb
 * @author $Author: juergens $
 * @version $Rev: 34670 $
 * @ConQAT.Rating GREEN Hash: DAAB68A9B85F43FCBEBA276A7E677F70
 */
public class SortedMemoryCloneIndexStoreTest extends CloneIndexStoreTestBase {

	/** {@inheritDoc} */
	@Override
	protected ICloneIndexStore createStore() {
		return new SortedMemoryCloneIndexStore();
	}
}