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

import java.io.File;

import org.conqat.engine.persistence.store.IStorageSystem;
import org.conqat.engine.persistence.store.IStore;
import org.conqat.engine.persistence.store.StorageException;
import org.conqat.engine.persistence.store.StorageSystemTestBase;
import org.conqat.engine.persistence.store.mem.InMemoryStorageSystem;

/**
 * Tests the {@link CompressingStore}.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 45015 $
 * @ConQAT.Rating GREEN Hash: 8394822815EA673FFF406364130564D4
 */
public class CompressingStoreTest extends StorageSystemTestBase {

	/** {@inheritDoc} */
	@Override
	protected IStorageSystem openStorage(File baseDir) throws StorageException {
		return new InMemoryStorageSystem(baseDir) {
			/** {@inheritDoc} */
			@Override
			public IStore openStore(String name) throws StorageException {
				return new CompressingStore(super.openStore(name));
			}
		};
	}

}
