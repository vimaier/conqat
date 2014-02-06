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
package org.conqat.engine.core.build;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.conqat.engine.core.bundle.BundleException;
import org.conqat.engine.core.bundle.BundleInfo;
import org.conqat.engine.core.bundle.BundlesConfiguration;
import org.conqat.engine.core.bundle.BundlesManager;
import org.conqat.engine.core.bundle.BundlesTopSorter;
import org.conqat.engine.core.driver.BundleCommandLineBase;
import org.conqat.lib.commons.options.AOption;

/**
 * This class generates ANT build files to build complete ConQAT installations.
 * 
 * @author $Author: steidl $
 * @version $Rev: 43635 $
 * @ConQAT.Rating GREEN Hash: ED73AEC3584469D78A3408DA6D770178
 */
public class BuildFileGenerator extends BundleCommandLineBase {

	/** Default name of the build file: {@value} */
	private static final String DEFAULT_BUILD_FILENAME = "build_conqat.xml";

	/**
	 * Main method for the generator. Start with <code>-h</code> for parameter
	 * description.
	 */
	public static void main(String[] args) {
		initLogger();

		BuildFileGenerator generator = new BuildFileGenerator();
		generator.initFromCommandLine(args);

		try {
			generator.run();
		} catch (Exception e) {
			System.err.println(e);
			System.exit(1);
		}
	}

	/** Turn off all logging. */
	private static void initLogger() {
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.OFF);
	}

	/** Name of the build file. */
	private String buildFilename = DEFAULT_BUILD_FILENAME;

	/** Set build file. */
	@AOption(shortName = 'o', longName = "output-file", description = "the name of the build file generated ["
			+ DEFAULT_BUILD_FILENAME + "]")
	public void setConfigFileName(String buildFilename) {
		this.buildFilename = buildFilename;
	}

	/** Run generator. */
	private void run() throws BundleException, FileNotFoundException {
		List<BundleInfo> bundleList = obtainBundleList(bundleLocations,
				bundleCollections);

		if (bundleList.isEmpty()) {
			System.err.println("No bundles defined.");
			return;
		}

		ConQATANTWriter writer = new ConQATANTWriter(new File(buildFilename),
				bundleList);
		writer.writeBuildFile();

		System.out.println("Build file written to " + buildFilename);
	}

	/**
	 * Init Bundle manager, load bundles and sort them topologically along their
	 * dependencies.
	 * 
	 * @return topologically sorted list of bundles.
	 * @throws BundleException
	 *             if a problem, e.g. cyclic bundle dependency occurred.
	 */
	public static List<BundleInfo> obtainBundleList(
			List<String> bundleLocations, List<String> bundleCollections)
			throws BundleException {
		BundlesManager bundlesManager = new BundlesManager();
		for (String bundleLocation : bundleLocations) {
			bundlesManager.addBundleLocation(bundleLocation);
		}

		for (String bundleCollection : bundleCollections) {
			bundlesManager.addBundleCollection(bundleCollection);
		}

		BundlesConfiguration config = bundlesManager.loadBundles();
		return new BundlesTopSorter(config).sort();
	}

}