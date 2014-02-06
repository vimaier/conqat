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
package org.conqat.engine.core.bundle;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.apache.log4j.Logger;
import org.conqat.engine.core.bundle.library.LibraryDescriptor;
import org.conqat.engine.core.driver.error.EDriverExceptionType;
import org.conqat.engine.core.driver.error.ErrorLocation;
import org.conqat.lib.commons.filesystem.FileExtensionFilter;

/**
 * This class is responsible for loading bundles from the the disk and thereby
 * creating the respective {@link BundleInfo} objects.
 * 
 * @author Florian Deissenboeck
 * @author Elmar Juergens
 * @author $Author: heinemann $
 * @version $Rev: 46960 $
 * @ConQAT.Rating YELLOW Hash: FF9779607C587F198FCC722E3352E47E
 */
public class BundlesLoader {

	/** Java library archive extension. */
	private static final String LIBRARY_ARCHIVE_EXTENSION = "jar";

	/** Java library descriptor extension. */
	private static final String LIBRARY_DESCRIPTOR_EXTENSION = "lib";

	/** Location of the folder containing the bundle libraries */
	public static final String LIB_LOCATION = "lib";

	/** Logger. */
	private static final Logger LOGGER = Logger.getLogger(BundlesLoader.class);

	/** The configuration to store bundles in. */
	private final BundlesConfiguration config;

	/**
	 * Create new loader.
	 * 
	 * @param config
	 *            the bundle configuration where the {@link BundleInfo} objects
	 *            will be stored.
	 */
	/* package */BundlesLoader(BundlesConfiguration config) {
		this.config = config;
	}

	/**
	 * Load bundles from a set of locations.
	 * 
	 * @param bundleLocations
	 *            the location set
	 * @throws BundleException
	 *             if any exception occurs.
	 */
	/* package */void loadBundles(Set<File> bundleLocations)
			throws BundleException {
		for (File bundleLocation : bundleLocations) {
			loadBundle(bundleLocation);
		}
	}

	/**
	 * Load a bundle from a location. This step loads the bundle descriptor and
	 * the libraries.
	 */
	private void loadBundle(File bundleLocation) throws BundleException {

		if (!bundleLocation.canRead()) {
			throw new BundleException(
					EDriverExceptionType.BUNDLE_LOCATION_NOT_FOUND,
					"Can't read bundle location: " + bundleLocation,
					ErrorLocation.UNKNOWN);
		}

		BundleInfo bundleInfo = new BundleInfo(bundleLocation);

		loadDescriptor(bundleInfo);

		loadLibraryLocations(bundleInfo);

		// add bundle to configuration
		config.addBundle(bundleInfo);

		LOGGER.info("Loading bundle " + bundleInfo.getId() + " "
				+ bundleInfo.getVersion());
	}

	/**
	 * Load bundle descriptor for a bundle. This method forwards the actual
	 * loading of the descriptor to {@link BundleDescriptorReader}.
	 * 
	 * @throws BundleException
	 *             if bundle descriptor was not found or an exception occurred
	 *             while reading the descriptor.
	 */
	private void loadDescriptor(BundleInfo bundleInfo) throws BundleException {

		File descriptorFile = bundleInfo.getDescriptor();
		try {
			new BundleDescriptorReader(descriptorFile).read(bundleInfo);
		} catch (IOException e) {
			throw new BundleException(
					EDriverExceptionType.MISSING_BUNDLE_DESCRIPTOR, "Bundle '"
							+ bundleInfo + "' has no descriptor: "
							+ e.getMessage(), e, bundleInfo.getLocation());
		}
	}

	/**
	 * This method does not actually <em>load</em> the libraries (this is left
	 * to the class loader) but stores the names of libraries and library
	 * descriptors found in the bundle with its {@link BundleInfo} object.
	 * 
	 * @throws BundleException
	 *             if the bundle has a library directory without any libraries
	 *             or a bundle descriptor cannot be read.
	 */
	private void loadLibraryLocations(BundleInfo bundleInfo)
			throws BundleException {
		File libsDirectory = new File(bundleInfo.getLocation()
				.getAbsolutePath() + File.separator + LIB_LOCATION);

		if (!libsDirectory.exists() || !libsDirectory.isDirectory()) {
			LOGGER.debug("Bundle in location " + bundleInfo.getLocation()
					+ " does not provide libraries.");
			return;
		}

		File[] libraries = libsDirectory.listFiles(new FileExtensionFilter(
				LIBRARY_ARCHIVE_EXTENSION));

		// Rationale: If bundle is to have no libraries, it needs no lib
		// directory. Empty lib directories indicate problems
		if (libraries.length == 0) {
			throw new BundleException(
					EDriverExceptionType.EMPTY_LIBRARY_DIRECTORY, "Bundle '"
							+ bundleInfo + "' has empty library directory.",
					libsDirectory);
		}

		for (File lib : libraries) {
			bundleInfo.addLibrary(lib);
		}

		loadLibraryDescriptors(bundleInfo, libsDirectory);
	}

	/**
	 * Loads all bundle descriptors from the {{@link #LIB_LOCATION} and stores
	 * them in the {@link BundleInfo}.
	 * 
	 * @throws BundleException
	 *             If an existing library descriptor cannot be read.
	 */
	private void loadLibraryDescriptors(BundleInfo bundleInfo,
			File libsDirectory) throws BundleException {
		File[] descriptors = libsDirectory.listFiles(new FileExtensionFilter(
				LIBRARY_DESCRIPTOR_EXTENSION));
		for (File descriptor : descriptors) {
			try {
				bundleInfo.addLibraryDescriptor(new LibraryDescriptor(
						descriptor, bundleInfo));
			} catch (IOException e) {
				throw new BundleException(EDriverExceptionType.IO_ERROR,
						"Library descriptor '" + descriptor.getName()
								+ "' for bundle '" + bundleInfo
								+ "' is not readable.", descriptor);
			}
		}
	}
}