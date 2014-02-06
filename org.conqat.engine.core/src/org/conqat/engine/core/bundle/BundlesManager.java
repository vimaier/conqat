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
import java.util.HashSet;

import org.apache.log4j.Logger;
import org.conqat.engine.core.driver.error.EDriverExceptionType;
import org.conqat.engine.core.driver.error.ErrorLocation;

/**
 * The bundle manager is responsible for loading the bundles, verifying their
 * integrity and preparing the class loader.
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 1FE5BAC4EBA5C0E5A7405E64792C68B1
 */
public class BundlesManager {

	/** Logger */
	private final Logger logger = Logger.getLogger(BundlesManager.class);

	/** Set of bundle locations. */
	private final HashSet<File> bundleLocations = new HashSet<File>();

	/** The bundle configuration. */
	private BundlesConfiguration config;

	/**
	 * Add a collection of bundles.
	 * 
	 * @throws BundleException
	 *             if a bundle location contained in the bundle collection was
	 *             defined multiply or if the bundle collection does not exist.
	 */
	public void addBundleCollection(String bundleCollectionPath)
			throws BundleException {
		File bundleCollection = new File(bundleCollectionPath);

		if (!bundleCollection.isDirectory()) {
			throw new BundleException(
					EDriverExceptionType.BUNDLE_COLLECTION_NOT_FOUND,
					"Bundle collection " + bundleCollectionPath + " not found.",
					ErrorLocation.UNKNOWN);
		}

		// add only directories that contain a bundle descriptor
		for (File bundleLocation : BundleUtils
				.getBundleLocations(bundleCollection)) {
			addBundleLocation(bundleLocation.getAbsolutePath());
		}
	}

	/**
	 * Add a bundle location.
	 * 
	 * @throws BundleException
	 *             if a bundle location was defined multiply.
	 */
	public void addBundleLocation(String bundleLocationPath)
			throws BundleException {

		File directory = normalizeBundleLocation(bundleLocationPath);

		if (bundleLocations.contains(directory)) {
			logger.warn("Bundle location " + directory
					+ " is defined multiple times.");
		}

		bundleLocations.add(directory);
	}

	/**
	 * This method does the actual work: It performs the initialization of the
	 * bundles in the order shown in <img src="bundle_loading_order.png"/>
	 * 
	 * @return the configuration managed by this object.
	 * @throws BundleException
	 *             if any kind of exception during bundle configuration
	 *             occurred.
	 */
	public BundlesConfiguration initBundles() throws BundleException {
		config = loadAndSortBundles();

		BundlesClassLoader classLoader = new BundlesClassLoader();
		new BundlesClassLoaderInitializer(config, classLoader).process();
		Thread.currentThread().setContextClassLoader(classLoader);

		new BundlesContextLoader(config).process();

		return config;
	}

	/**
	 * This method loads all bundle descriptors, verifies the bundle
	 * dependencies and sorts the bundle topologically to find cycles.
	 * 
	 * @return a bundle configuration with all bundles. Bundle dependencies are
	 *         verified.
	 * @throws BundleException
	 *             if any kind of exception during bundle configuration
	 *             occurred.
	 */
	public BundlesConfiguration loadAndSortBundles() throws BundleException {
		config = loadBundles();
		new BundlesTopSorter(config).sort();
		return config;
	}

	/**
	 * This method loads all bundle descriptors and verifies the bundle
	 * dependencies.
	 * 
	 * @return a bundle configuration with all bundles. Bundle dependencies are
	 *         verified.
	 * @throws BundleException
	 *             if any kind of exception during bundle configuration
	 *             occurred.
	 */
	public BundlesConfiguration loadBundles() throws BundleException {
		loadBundlesWithoutCheck();
		new BundlesDependencyVerifier(config).process();
		return config;
	}

	/**
	 * This method loads all bundle descriptors.
	 * 
	 * @return a bundle configuration with all bundles.
	 */
	public BundlesConfiguration loadBundlesWithoutCheck()
			throws BundleException {
		config = new BundlesConfiguration();
		if (bundleLocations.isEmpty()) {
			throw new BundleException(
					EDriverExceptionType.NO_BUNDLES_CONFIGURED,
					"No bundles were found. ConQAT seems to be misconfigured. Check the bundle locations and collections!",
					ErrorLocation.UNKNOWN);
		}
		new BundlesLoader(config).loadBundles(bundleLocations);
		return config;
	}

	/**
	 * Normalize a bundle location (find canonical path).
	 * 
	 * @return the normalized location
	 * @throws BundleException
	 *             if path was not found or could not be normalized.
	 */
	private File normalizeBundleLocation(String bundleLocationPath)
			throws BundleException {
		File bundleLocation = new File(bundleLocationPath);

		File normalizedBundleLocation = null;
		try {
			normalizedBundleLocation = bundleLocation.getCanonicalFile();
		} catch (IOException e) {
			throw new BundleException(
					EDriverExceptionType.BUNDLE_LOCATION_COULD_NOT_BE_NORMALIZED,
					"Could not determine normalized path for bundle location '"
							+ bundleLocationPath + "'.", ErrorLocation.UNKNOWN);
		}

		if (!normalizedBundleLocation.isDirectory()) {
			throw new BundleException(
					EDriverExceptionType.BUNDLE_LOCATION_NOT_FOUND,
					"Bundle location " + bundleLocationPath
							+ " does not exist.", ErrorLocation.UNKNOWN);
		}

		return normalizedBundleLocation;
	}

}