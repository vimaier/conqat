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
package org.conqat.engine.core.driver;

import java.io.PrintWriter;
import java.util.ArrayList;

import org.conqat.engine.core.build.BuildFileGenerator;
import org.conqat.engine.core.bundle.BundleException;
import org.conqat.engine.core.bundle.BundlesConfiguration;
import org.conqat.engine.core.bundle.BundlesManager;
import org.conqat.engine.core.conqatdoc.ConQATDoc;
import org.conqat.lib.commons.options.AOption;
import org.conqat.lib.commons.options.CommandLine;
import org.conqat.lib.commons.options.OptionException;
import org.conqat.lib.commons.options.OptionRegistry;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Base class for command line tools which require a set of bundles to be
 * loaded. This is used both by the {@link Driver}, {@link ConQATDoc} and
 * {@link BuildFileGenerator}.
 * 
 * @author $Author: deissenb $
 * @version $Rev: 37541 $
 * @ConQAT.Rating GREEN Hash: 38799B8CA01E3DBF23E5FC311984A036
 */
public abstract class BundleCommandLineBase {

	/** Command line parser */
	private CommandLine cmdLine;

	/**
	 * List of bundle locations. This is deliberately a list and not set to
	 * discover duplicate bundle locations.
	 */
	protected final ArrayList<String> bundleLocations = new ArrayList<String>();

	/**
	 * List of paths to bundle collections. Here we could use a set, but for
	 * sake of conformity a list is used, duplicates will be found anyway.
	 */
	protected final ArrayList<String> bundleCollections = new ArrayList<String>();

	/** Add bundle. */
	@AOption(shortName = 'b', longName = "bundle", greedy = true, description = "load bundle")
	public void addBundleLocation(String bundleLocation) {
		bundleLocations.add(bundleLocation);
	}

	/** Add bundle collection. */
	@AOption(shortName = 'c', longName = "bundle-collection", greedy = true, description = "load bundle collection")
	public void addBundleCollection(String bundleCollection) {
		bundleCollections.add(bundleCollection);
	}

	/** Help option: print usage and exit. */
	@AOption(shortName = 'h', longName = "help", description = "print this usage message")
	public void printUsageAndExit() {
		cmdLine.printUsage(new PrintWriter(System.err));
		System.exit(1);
	}

	/**
	 * If bundles are defined, this method instantiates the bundle manager and
	 * loads the bundles.
	 */
	protected BundlesConfiguration loadBundles() throws BundleException {
		if (bundleLocations.isEmpty() && bundleCollections.isEmpty()) {
			return null;
		}

		BundlesManager bundlesManager = new BundlesManager();
		for (String bundleLocation : bundleLocations) {
			bundlesManager.addBundleLocation(bundleLocation);
		}

		for (String bundleCollection : bundleCollections) {
			bundlesManager.addBundleCollection(bundleCollection);
		}

		return bundlesManager.initBundles();
	}

	/** Read the command line. */
	protected void initFromCommandLine(String[] args) {
		OptionRegistry optionReg = new OptionRegistry(this);
		cmdLine = new CommandLine(optionReg);

		try {
			String[] leftOvers = cmdLine.parse(args);
			handleLeftOvers(leftOvers);
		} catch (OptionException e) {
			System.err.println("Incorrect options: " + e.getMessage());
			System.exit(1);
		}
	}

	/**
	 * Template methods for dealing with left overs (i.e. parameters that could
	 * not be parsed). The default implementation issues an error if leftovers
	 * are found.
	 */
	protected void handleLeftOvers(String[] leftOvers) {
		if (leftOvers.length > 0) {
			System.err.println("Unsupported trailing options: "
					+ StringUtils.concat(leftOvers, " "));
			cmdLine.printUsage(new PrintWriter(System.err));
			System.exit(1);
		}
	}
}