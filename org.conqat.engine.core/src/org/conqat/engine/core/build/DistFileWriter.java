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
package org.conqat.engine.core.build;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.conqat.engine.core.bundle.BundleDependency;
import org.conqat.engine.core.bundle.BundleException;
import org.conqat.engine.core.bundle.BundleInfo;
import org.conqat.engine.core.driver.BundleCommandLineBase;
import org.conqat.engine.core.driver.error.EDriverExceptionType;
import org.conqat.engine.core.driver.error.ErrorLocation;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.options.AOption;

/**
 * This program creates a dist file as used in our build process, i.e. a text
 * file where each line describes a bundle. The program works by specifying any
 * number of 'anchor' bundles (via their id). The program adds all required
 * bundles automatically. The program can also run in "regenerate mode", where
 * the anchor bundles are taken from an existing dist file. This allows to add
 * any additional dependencies.
 * 
 * @author $Author: deissenb $
 * @version $Rev: 44798 $
 * @ConQAT.Rating GREEN Hash: 5B7A373EA15DF04E741F0A29138D4299
 */
public class DistFileWriter extends BundleCommandLineBase {

	/** Output file. */
	private File outputFile;

	/** Anchor bundle ids. */
	private final Set<String> anchorBundleIds = new HashSet<String>();

	/**
	 * If this is set to true, an existing dist file is regenerated (new
	 * dependencies added, non-existing bundles removed).
	 */
	private boolean regenerate = false;

	/** Sets the name of the output file */
	@AOption(shortName = 'o', longName = "output", description = "the name of the output file")
	public void setOutputFile(File file) {
		this.outputFile = file;
	}

	/** Sets regenerate mode. */
	@AOption(shortName = 'r', longName = "regenerate", description = "sets the mode to regenerate an existing dist file.")
	public void setRegenerate() {
		this.regenerate = true;
	}

	/** Add anchor bundle id. */
	@AOption(shortName = 'i', longName = "id", description = "anchor bundle id")
	public void addBundleId(String bundleId) {
		anchorBundleIds.add(bundleId);
	}

	/** {@inheritDoc} */
	@Override
	protected void initFromCommandLine(String[] args) {
		super.initFromCommandLine(args);

		if (outputFile == null) {
			System.err.println("Output file missing!");
			printUsageAndExit();
		}

		if (anchorBundleIds.isEmpty() && !regenerate) {
			System.err.println("Must specify at least one bundle id!");
			printUsageAndExit();
		}

		if (regenerate && !outputFile.canRead()) {
			System.err
					.println("In regenerate mode the output file must exist!");
			printUsageAndExit();
		}
	}

	/** Creates the dist file and writes to output file. */
	public void createDistFile() throws BundleException, IOException {
		Map<String, BundleInfo> allBundlesMap = obtainBundleMap();
		if (regenerate) {
			loadAnchorBundles(allBundlesMap);
		}

		HashSet<String> requiredBundles = determineRequiredBundles(allBundlesMap);
		writeDistFile(requiredBundles);
	}

	/** Loads the anchor bundles from the dist file. */
	private void loadAnchorBundles(Map<String, BundleInfo> allBundlesMap)
			throws UnsupportedEncodingException, IOException {
		for (String line : FileSystemUtils.readLinesUTF8(outputFile)) {
			String bundleId = line.trim();
			if (allBundlesMap.containsKey(bundleId)) {
				anchorBundleIds.add(bundleId);
			} else {
				System.out.println("Skipping non-existing bundle " + bundleId);
			}
		}
	}

	/**
	 * Obtain map of all bundles mapping from bundle id to bundle info.
	 */
	private Map<String, BundleInfo> obtainBundleMap() throws BundleException {
		List<BundleInfo> allBundles = BuildFileGenerator.obtainBundleList(
				bundleLocations, bundleCollections);
		Map<String, BundleInfo> allBundlesMap = new HashMap<String, BundleInfo>();

		for (BundleInfo bundle : allBundles) {
			allBundlesMap.put(bundle.getId(), bundle);
		}
		return allBundlesMap;
	}

	/**
	 * Determine the required bundles.
	 */
	private HashSet<String> determineRequiredBundles(
			Map<String, BundleInfo> allBundlesMap) throws BundleException {
		HashSet<String> requiredBundles = new HashSet<String>();

		for (String bundleId : anchorBundleIds) {
			addRequiredBundles(bundleId, allBundlesMap, requiredBundles);
		}

		requiredBundles.add(BuildFileConstants.CONQAT_PROJ);
		return requiredBundles;
	}

	/**
	 * Recursively add bundles required by the specified bundle.
	 */
	private void addRequiredBundles(String bundleId,
			Map<String, BundleInfo> allBundlesMap, Set<String> requiredBundles)
			throws BundleException {
		requiredBundles.add(bundleId);
		BundleInfo bundle = allBundlesMap.get(bundleId);

		if (bundle == null) {
			throw new BundleException(EDriverExceptionType.BUNDLE_NOT_FOUND,
					"Bundle " + bundleId + " not found.", ErrorLocation.UNKNOWN);
		}

		for (BundleDependency dependency : bundle.getDependencies()) {
			addRequiredBundles(dependency.getId(), allBundlesMap,
					requiredBundles);
		}
	}

	/** Write dist file. */
	private void writeDistFile(HashSet<String> requiredBundles)
			throws IOException, FileNotFoundException {
		FileSystemUtils.ensureParentDirectoryExists(outputFile);
		PrintWriter writer = new PrintWriter(outputFile);
		for (String bundleId : CollectionUtils.sort(requiredBundles)) {
			writer.println(bundleId);
		}
		writer.close();
	}

	/** Create dist file. */
	public static void main(String[] args) throws BundleException, IOException {
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.OFF);

		DistFileWriter distFileWriter = new DistFileWriter();
		distFileWriter.initFromCommandLine(args);
		distFileWriter.createDistFile();

		System.out.println("Dist file written to " + distFileWriter.outputFile);
	}
}
