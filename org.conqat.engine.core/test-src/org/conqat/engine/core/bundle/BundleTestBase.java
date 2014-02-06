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
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.conqat.engine.core.driver.error.EDriverExceptionType;
import org.conqat.lib.commons.test.CCSMTestCaseBase;

/**
 * This class serves as base class for buildlet test cases that create bundle
 * configurations in the <code>org.conqat.engine.core.bundle</code> package. It
 * offers helper methods that are used by various test cases.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 44622 $
 * @ConQAT.Rating GREEN Hash: 3633A5199EF8D3D4A3B885D94EB72E2F
 */
public abstract class BundleTestBase extends CCSMTestCaseBase {

	/**
	 * Set that contains the buildlets that are executed during configuration
	 * construction for this test case.
	 */
	private final Set<Class<?>> enabledBuildlets = getEnabledBuildlets();

	/**
	 * The <code>enabledBuildlets</code> set contains the buildlets that are
	 * executed for configuration construction in test cases.
	 * <p>
	 * To enable a builder, its class object must be in the set. Deriving
	 * classes override this template method in order to customize the
	 * incremental configuration build process to their needs.
	 */
	protected abstract Set<Class<?>> getEnabledBuildlets();

	/**
	 * Utility method. Returns a set that contains the passed elements.
	 */
	protected Set<Class<?>> createSet(Class<?>... elements) {
		return new HashSet<Class<?>>(Arrays.asList(elements));
	}

	/**
	 * Load configuration and check if an exception of the specified type is
	 * thrown.
	 * 
	 * Configuration loading is done according to the enabledBuildlets.
	 * 
	 * @param expectedType
	 *            expected exception type
	 * @param directoryNames
	 *            bundle locations.
	 */
	protected void checkException(EDriverExceptionType expectedType,
			String... directoryNames) {
		try {
			loadConfiguration(directoryNames);
			fail("expected exception");
		} catch (BundleException e) {
			assertEquals(expectedType, e.getType());
		}
	}

	/**
	 * Load configuration with a single bundle and return the bundle info.
	 * Configuration loading is done according to the enabledBuildlets.
	 */
	protected BundleInfo loadInfo(String directoryName) throws BundleException {
		BundlesConfiguration config = loadConfiguration(directoryName);
		return config.getBundles().iterator().next();
	}

	/**
	 * Load configuration. Bundles are constructed using an incremental build
	 * process that comprises several steps. Since many steps depend on other
	 * steps, these steps cannot be tested in isolation. However, to keep tests
	 * focused, test cases still are supposed to run on test data as small and
	 * simple, as possible.
	 * <p>
	 * To resolve this conflict (and in order to avoid duplication of test code
	 * among test classes), this method executes the buildlets in a customizable
	 * manner: It only executes the buildlets whose classes are contained in the
	 * set <code>enabledBuildlets</code>.
	 * 
	 * @param directoryNames
	 *            bundle locations
	 */
	protected BundlesConfiguration loadConfiguration(String... directoryNames)
			throws BundleException {

		// LinkedHashSet ensure stable order which is important for the
		// BundleTopSorterTest
		LinkedHashSet<File> bundleLocations = new LinkedHashSet<File>();

		for (String directoryName : directoryNames) {
			File file = useTestFile(directoryName);

			bundleLocations.add(file);
		}

		// Create empty configuration
		BundlesConfiguration config = new BundlesConfiguration();

		// Load bundles
		if (enabledBuildlets.contains(BundlesLoader.class)) {
			BundlesLoader loader = new BundlesLoader(config);
			loader.loadBundles(bundleLocations);
		}

		// Verify dependencies
		if (enabledBuildlets.contains(BundlesDependencyVerifier.class)) {
			new BundlesDependencyVerifier(config).process();
		}

		// Initialize class loaders
		if (enabledBuildlets.contains(BundlesClassLoaderInitializer.class)) {
			BundlesClassLoader classLoader = new BundlesClassLoader();
			new BundlesClassLoaderInitializer(config, classLoader).process();
			Thread.currentThread().setContextClassLoader(classLoader);
		}

		// Load Context
		if (enabledBuildlets.contains(BundlesContextLoader.class)) {
			new BundlesContextLoader(config).process();
		}

		return config;
	}

}