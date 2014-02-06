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
package org.conqat.engine.core.driver.specification;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.conqat.engine.core.ConQATInfo;
import org.conqat.engine.core.bundle.BundleInfo;
import org.conqat.engine.core.driver.BlockFileReader;
import org.conqat.engine.core.driver.error.BlockFileException;
import org.conqat.engine.core.driver.error.DriverException;
import org.conqat.engine.core.driver.error.EDriverExceptionType;
import org.conqat.engine.core.driver.error.ErrorLocation;
import org.conqat.lib.commons.assertion.CCSMPre;

/**
 * The specification loader is the ConQAT equivalent to the Java class loader.
 * It is used to obtain specifications.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: F29EFF4BEF4F6D3D5A89DE587C5DB3FF
 */
public class SpecificationLoader {

	/**
	 * The base directory for this specification loader which is used for block
	 * lookup.
	 */
	private final File baseDir;

	/** The bundles used to lookup blocks. */
	private final List<BundleInfo> bundles;

	/** The reader used for parsing block files. */
	private final BlockFileReader xmlReader = new BlockFileReader(this);

	/**
	 * Mapping from block specification names to block specification
	 * initializers.
	 */
	private final Map<String, BlockSpecificationInitializer> blockSpecifications = new HashMap<String, BlockSpecificationInitializer>();

	/** Mapping from processor specification names to processor specifications. */
	private final Map<String, ProcessorSpecification> processorSpecifications = new HashMap<String, ProcessorSpecification>();

	/**
	 * Constructor.
	 * 
	 * @param baseDir
	 *            the directory that is used to perform relative lookup of
	 *            blocks. This may be null, which limits block lookup to
	 *            bundles.
	 */
	public SpecificationLoader(File baseDir, Collection<BundleInfo> bundles) {
		CCSMPre.isTrue(baseDir == null || baseDir.isDirectory(),
				"baseDir must be either null or directory");

		this.baseDir = baseDir;
		this.bundles = new ArrayList<BundleInfo>(bundles);
	}

	/**
	 * Removes all cached block specifications from this loader. This
	 * invalidates all blocks loaded to far, as they might hold references to
	 * outdated block data.
	 */
	public void clearBlockSpecifications() {
		blockSpecifications.clear();
	}

	/**
	 * Returns the named block specification or null if no specification with
	 * the given name is found.
	 */
	public BlockSpecification getBlockSpecification(String name)
			throws DriverException {
		if (!blockSpecifications.containsKey(name)) {
			File localBlockFile = findLocalBlockFile(name);
			File bundleBlockFile = findBundleBlockFile(name);

			if (localBlockFile != null && bundleBlockFile != null) {
				throw new BlockFileException(
						EDriverExceptionType.AMBIGUOUS_BLOCK_LOOKUP,
						"The block can be found both in a bundle and local to the current configuration.",
						new ErrorLocation(localBlockFile), new ErrorLocation(
								bundleBlockFile));
			}

			// We consider the file use-case to be the default (given our
			// current bundle layout). The classloader is only a fallback for
			// special situations. Thus, we do not check if a block can be found
			// via file and classloader.

			if (localBlockFile != null) {
				loadAndStoreFromFile(name, localBlockFile);
			} else if (bundleBlockFile != null) {
				loadAndStoreFromFile(name, bundleBlockFile);
			} else if (!loadAndStoreFromClassLoader(name)) {
				return null;
			}
		}

		BlockSpecificationInitializer blockSpecInitializer = blockSpecifications
				.get(name);
		if (blockSpecInitializer == null) {
			return null;
		}
		return blockSpecInitializer.accessBlockSpecification();
	}

	/**
	 * Stores the block specification loaded from the file using the given name.
	 */
	private void loadAndStoreFromFile(String name, File localBlockFile)
			throws DriverException {
		BlockSpecification blockSpec = xmlReader.readBlockFile(localBlockFile);
		checkAndStoreBlockSpec(blockSpec, name, new ErrorLocation(
				localBlockFile));
	}

	/**
	 * Performs name consistency check for the given block specification and
	 * stores it.
	 */
	private void checkAndStoreBlockSpec(BlockSpecification blockSpec,
			String name, ErrorLocation errorLocation) throws BlockFileException {
		if (!blockSpec.getName().equals(name)) {
			throw new BlockFileException(
					EDriverExceptionType.BLOCK_FILE_NAME_MUST_MATCH,
					"The name of the block specification ("
							+ blockSpec.getName() + ") does not match '" + name
							+ "'.", errorLocation);
		}
		blockSpecifications.put(name, new BlockSpecificationInitializer(
				blockSpec));
	}

	/**
	 * Attempts to load the given block from the class loader and stores the
	 * parsed block. Returned whether the block could be found.
	 */
	private boolean loadAndStoreFromClassLoader(String name)
			throws BlockFileException {
		String blockFile = name.replace('.', '/') + "."
				+ ConQATInfo.BLOCK_FILE_EXTENSION;

		// we use the current thread's context class loader to ensure we locate
		// blocks in bundles as well.
		URL blockURL = Thread.currentThread().getContextClassLoader()
				.getResource(blockFile);
		if (blockURL == null) {
			return false;
		}

		BlockSpecification blockSpec = xmlReader.readBlockFile(blockURL);
		checkAndStoreBlockSpec(blockSpec, name, new ErrorLocation(blockURL));
		return true;
	}

	/**
	 * Returns the file relative to the {@link #baseDir} containing the block
	 * with the given name or null if it does not exist.
	 */
	private File findLocalBlockFile(String name) {
		if (baseDir == null) {
			return null;
		}
		return findRelativeBlockFile(baseDir, name);
	}

	/**
	 * Returns the file relative to the {@link #baseDir} containing the block
	 * with the given name or null if it does not exist.
	 */
	private File findBundleBlockFile(String name) {
		for (BundleInfo bundleInfo : bundles) {
			String prefix = bundleInfo.getId() + ".";
			if (name.startsWith(prefix)) {
				return findRelativeBlockFile(bundleInfo.getBlocksDirectory(),
						name.substring(prefix.length()));
			}
		}
		return null;
	}

	/**
	 * Returns the block file relative to the given directory or null if it does
	 * not exist.
	 */
	private File findRelativeBlockFile(File blocksDirectory, String name) {
		if (!blocksDirectory.isDirectory()) {
			return null;
		}
		File blockFile = new File(blocksDirectory, name.replace('.',
				File.separatorChar) + '.' + ConQATInfo.BLOCK_FILE_EXTENSION);
		if (blockFile.canRead()) {
			return blockFile;
		}
		return null;
	}

	/**
	 * Returns the named processor specification in compiled form.
	 * 
	 * @throws DriverException
	 *             if the {@link ProcessorSpecification} could not be created
	 *             (invalid processor) or no processor of the given name was
	 *             found.
	 */
	public ProcessorSpecification getProcessorSpecification(String name)
			throws DriverException {
		if (!processorSpecifications.containsKey(name)) {
			processorSpecifications.put(name, new ProcessorSpecification(name));
		}
		return processorSpecifications.get(name);
	}
}