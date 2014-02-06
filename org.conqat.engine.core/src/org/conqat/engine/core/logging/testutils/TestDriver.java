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
package org.conqat.engine.core.logging.testutils;

import java.io.IOException;

import org.conqat.engine.core.bundle.BundleException;
import org.conqat.engine.core.bundle.BundlesConfiguration;
import org.conqat.engine.core.driver.Driver;
import org.conqat.engine.core.driver.error.DriverException;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.filesystem.CanonicalFile;
import org.conqat.lib.commons.filesystem.ClassPathUtils;

/**
 * Driver used to run tests.
 * 
 * @author $Author: juergens $
 * @version $Rev: 36750 $
 * @ConQAT.Rating GREEN Hash: D081459232A4603047E17073AD6EF55E
 */
public class TestDriver extends Driver {

	/**
	 * Runs ConQAT on a configuration.
	 * 
	 * @param configurationFilename
	 *            filename of the configuration that gets run
	 */
	public void drive(String configurationFilename, String... properties)
			throws BundleException, DriverException {
		// set up driver on configuration
		setConfigFileName(configurationFilename);
		setupBundleCollection();
		initLogger();

		for (String property : properties) {
			addCommandLineProperty(property);
		}

		// run driver
		BundlesConfiguration bundleConfig = loadBundles();
		drive(bundleConfig);
	}

	/**
	 * Sets the bundle collections. This adds the parent directory (i.e. the
	 * parent of the bundle executing the tests) and the directory containing
	 * the core bundle (unless both directories are the same). We have to use
	 * both directories, as for third-party extensions a typical scenario is to
	 * have the ConQAT Open Source bundles and the third-party bundles in
	 * separate directory trees.
	 */
	private void setupBundleCollection() throws AssertionError {
		addBundleCollection("..");

		try {
			String[] classPaths = ClassPathUtils.createClassPathAsArray(null,
					CCSMAssert.class);
			CCSMAssert.isTrue(classPaths.length > 0,
					"ccsm-commons.jar not found");

			CanonicalFile parent = new CanonicalFile("..");
			CanonicalFile commonsJar = new CanonicalFile(classPaths[0]);

			// in a test setting the jar should be in
			// org.conqat.engine.core/lib, so move three levels up
			CanonicalFile additionalBundleDir = commonsJar.getParentFile()
					.getParentFile().getParentFile();

			if (!parent.equals(additionalBundleDir)) {
				addBundleCollection(additionalBundleDir.getCanonicalPath());
			}
		} catch (IOException e) {
			CCSMAssert
					.fail("Tests running in a non-standard ConQAT environment: "
							+ e.getMessage());
		}
	}

}