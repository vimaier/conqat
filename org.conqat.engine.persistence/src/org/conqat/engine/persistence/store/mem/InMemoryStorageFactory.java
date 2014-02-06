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
package org.conqat.engine.persistence.store.mem;

import java.io.File;

import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.core.IShutdownHook;
import org.conqat.engine.persistence.store.IStorageSystem;

/**
 * {@ConQAT.Doc}
 * 
 * @author hummelb
 * @author $Author: juergens $
 * @version $Rev: 35197 $
 * @ConQAT.Rating GREEN Hash: 001F6872708AE76760DA7103AD39D40E
 */
@AConQATProcessor(description = "A simple in-memory implementation of a storage system. "
		+ "It also supports disk-based persistence, but the entire store must fit into main memory.")
public class InMemoryStorageFactory extends ConQATProcessorBase {

	/** The name of the base directory to store the data in. */
	private File baseDirectory;

	/**
	 * {@ConQAT.Doc}
	 * <p>
	 * 
	 * Note that in this processor, the directory is optional! So it can not be
	 * merged with other disk-based stores in a common super class.
	 */
	@AConQATParameter(name = "storage", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "The directory where the data is stored in. "
			+ "If this is not provided, no persistence across individual runs will occur.")
	public void setBaseDirectory(
			@AConQATAttribute(name = "dir", description = "Name of the directory.") String baseDirectory) {
		this.baseDirectory = new File(baseDirectory);
	}

	/** {@inheritDoc} */
	@Override
	public IStorageSystem process() throws ConQATException {
		final InMemoryStorageSystem storageSystem = new InMemoryStorageSystem(
				baseDirectory);
		getProcessorInfo().registerShutdownHook(new IShutdownHook() {
			@Override
			public void performShutdown() throws ConQATException {
				storageSystem.close();
			}
		}, false);
		return storageSystem;
	}
}