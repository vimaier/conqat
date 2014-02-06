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
package org.conqat.engine.persistence.store.dain_leveldb;

import java.io.File;

import org.conqat.engine.persistence.store.IStorageSystem;
import org.conqat.engine.persistence.store.StorageException;
import org.conqat.engine.persistence.store.StorageSystemTestBase;

/**
 * Tests the {@link DainLevelDBStorageSystem}.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46113 $
 * @ConQAT.Rating GREEN Hash: DD8267EC433767DBF7E304DED303E3F8
 */
public class DainLevelDBStorageSystemTest extends StorageSystemTestBase {

	/** {@inheritDoc} */
	@Override
	protected IStorageSystem openStorage(File baseDir) throws StorageException {
		return new DainLevelDBStorageSystem(baseDir, 4);
	}

}
