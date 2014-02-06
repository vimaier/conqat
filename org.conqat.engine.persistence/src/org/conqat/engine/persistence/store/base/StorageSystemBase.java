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
package org.conqat.engine.persistence.store.base;

import java.io.File;
import java.io.IOException;

import org.conqat.engine.persistence.store.IStorageSystem;
import org.conqat.engine.persistence.store.StorageException;
import org.conqat.engine.persistence.store.util.StorageUtils;
import org.conqat.lib.commons.filesystem.FileSystemUtils;

/**
 * Common base class for storage systems.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46175 $
 * @ConQAT.Rating GREEN Hash: BFEDF25F751850A45B016FB4F2989463
 */
public abstract class StorageSystemBase implements IStorageSystem {

	/**
	 * Ensures that the given directory exists (creates if needed) and throws a
	 * {@link StorageException} in case of any problems.
	 */
	protected void ensureStorageDirectory(File storageDirectory)
			throws StorageException {
		try {
			FileSystemUtils.ensureDirectoryExists(storageDirectory);
		} catch (IOException e) {
			throw new StorageException("Could not create store location: "
					+ storageDirectory, e);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void removeStore(String name) throws StorageException {
		StorageUtils.clearStore(this, name);
	}
}
