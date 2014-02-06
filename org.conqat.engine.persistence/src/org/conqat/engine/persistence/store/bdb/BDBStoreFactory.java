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
package org.conqat.engine.persistence.store.bdb;

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
 * @ConQAT.Rating GREEN Hash: B4CF056AF45DCE95FA22219FDC83C2C7
 */
@AConQATProcessor(description = "Store implementation based on BerkeleyDB Java edition. "
		+ "This is a disk-based high performance store.")
public class BDBStoreFactory extends ConQATProcessorBase {

	/** The default value for {@link #cacheMemoryInMB}. */
	private static final int DEFAULT_CACHE_MEM = 20;

	/** The name of the base directory to store the data in. */
	private File baseDirectory;

	/** The amount of memory used for caching. */
	private int cacheMemoryInMB = DEFAULT_CACHE_MEM;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "storage", minOccurrences = 1, maxOccurrences = 1, description = ""
			+ "The directory where the data is stored in. "
			+ "There will be multiple files written into this directory, so it should ideally be empty.")
	public void setBaseDirectory(
			@AConQATAttribute(name = "dir", description = "Name of the directory.") String baseDirectory) {
		this.baseDirectory = new File(baseDirectory);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "cache", maxOccurrences = 1, description = "Sets the amount of memory used for caching.")
	public void setCacheMemoryInMB(
			@AConQATAttribute(name = "size", description = "The size of the in-memory cache in MB. "
					+ "The larger the better, but the memory will not be available to ConQAT. The default value is "
					+ DEFAULT_CACHE_MEM + ".") int cacheMemoryInMB) {
		this.cacheMemoryInMB = cacheMemoryInMB;
	}

	/** {@inheritDoc} */
	@Override
	public IStorageSystem process() throws ConQATException {
		final BDBStorageSystem storageSystem = new BDBStorageSystem(
				baseDirectory, cacheMemoryInMB);
		getProcessorInfo().registerShutdownHook(new IShutdownHook() {
			@Override
			public void performShutdown() throws ConQATException {
				storageSystem.close();
			}
		}, false);
		return storageSystem;
	}
}