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
 * Test for the {@link InMemoryCloneIndexStore}.
 * 
 * @author hummelb
 * @author $Author: juergens $
 * @version $Rev: 34670 $
 * @ConQAT.Rating GREEN Hash: F8218BC7DD9F474E11BBB5F91F9265C4
 */
public class InMemoryCloneIndexStoreTest extends CloneIndexStoreTestBase {

	/** {@inheritDoc} */
	@Override
	protected ICloneIndexStore createStore() {
		return new InMemoryCloneIndexStore();
	}
}