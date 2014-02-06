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
package org.conqat.engine.persistence.store.leveldb;

import java.io.File;

import org.conqat.engine.persistence.store.IStorageSystem;
import org.conqat.engine.persistence.store.StorageException;
import org.conqat.engine.persistence.store.StorageSystemTestBase;

/**
 * Tests the {@link LevelDBStorageSystem}
 * 
 * @author $Author: heineman $
 * @version $Rev: 39793 $
 * @ConQAT.Rating GREEN Hash: 5E1227A5A2CECFB3EE67A660E4ECB8E3
 */
public class LevelDBStorageSystemTest extends StorageSystemTestBase {

	/** {@inheritDoc} */
	@Override
	protected IStorageSystem openStorage(File baseDir) throws StorageException {
		return new LevelDBStorageSystem(baseDir, 4);
	}
}
