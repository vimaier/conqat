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

import static org.conqat.lib.commons.filesystem.ClassPathUtils.CLASS_FILE_SUFFIX;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarOutputStream;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.conqat.engine.core.bundle.BundleException;
import org.conqat.engine.core.bundle.BundlesManager;
import org.conqat.engine.core.driver.BundleCommandLineBase;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.filesystem.PathBasedContentProviderBase;
import org.conqat.lib.commons.options.AOption;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.commons.tree.ITreeNodeHandler;
import org.conqat.lib.commons.tree.TreeUtils;

/**
 * This utility reads a jar file and performs the following pruning steps
 * <ul>
 * <li>Filter all entries starting with one of the specified prefixes</li>
 * <li>Filter all classes that are already on the ConQAT classpath (optional)</li>
 * <li>Filter all non-class files that reside in directories without any class
 * files. This prunes empty directories, too (optional).</li>
 * </ul>
 * 
 * When running this, make sure the source jar is not on the ConQAT classpath.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 44651 $
 * @ConQAT.Rating GREEN Hash: 272F2D3395B8369FCA82CFDDCB76FD84
 */
public class JarPruner extends BundleCommandLineBase {

	/** The source file. */
	private File sourceFile;

	/** The target file. */
	private File targetFile;

	/** A log file for the list of pruned classes (may be null). */
	private File logFile;

	/** Set of prefixes to be filtered. */
	private final Set<String> filterPrefixes = new HashSet<String>();

	/** Flag for directory pruning. */
	private boolean pruneDirectories = true;

	/** Flag for class filtering. */
	private boolean filterKnownClasses = true;

	/** Set of prefix to be preserved. */
	private Set<String> preservePrefixesSet = new HashSet<String>();

	/** Array representation of {@link #preservePrefixesSet}. */
	private String[] preservePrefixes;

	/** Set source file */
	@AOption(shortName = 's', longName = "source", description = "Source jar file")
	public void setJarFile(String path) {
		sourceFile = new File(path);
	}

	/** Set target file. */
	@AOption(shortName = 't', longName = "target", description = "Target jar file")
	public void setTargetFile(String path) {
		targetFile = new File(path);
	}

	/** Set log file. */
	@AOption(shortName = 'l', longName = "log", description = "Log file")
	public void setLogFile(String path) {
		logFile = new File(path);
	}

	/** Add prefix. */
	@AOption(shortName = 'p', longName = "prefix", description = "Add entry prefix to be filtered.")
	public void addPrefix(String prefix) {
		filterPrefixes.add(prefix);
	}

	/** Sets flag for filtering known classes. */
	@AOption(shortName = 'k', longName = "classes", description = "Flag to filter all classes found on the ConQAT classpath")
	public void filterKnownClasses(boolean filterKnownClasses) {
		this.filterKnownClasses = filterKnownClasses;
	}

	/** Sets flag for directory pruning. */
	@AOption(shortName = 'd', longName = "dirs", description = "Flag to prune directories that contain no class files")
	public void pruneDirectories(boolean pruneDirectories) {
		this.pruneDirectories = pruneDirectories;
	}

	/** Add prefix. */
	@AOption(shortName = 'r', longName = "pReserve", description = "Add prefix of path that should be preserved. "
			+ "Preservation beats other parameters.")
	public void addPreservePrefix(String prefix) {
		preservePrefixesSet.add(prefix);
	}

	/** Prune Jar File */
	public static void main(String[] args) throws IOException {
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.OFF);

		JarPruner jarPruner = new JarPruner();
		jarPruner.initFromCommandLine(args);
		jarPruner.prune();
	}

	/** Parse command line and initialize class loader. */
	@Override
	protected void initFromCommandLine(String[] args) {
		super.initFromCommandLine(args);
		checkCommandLine();
	}

	/**
	 * Check command line.
	 */
	private void checkCommandLine() {
		if (sourceFile == null) {
			System.err.println("Jar file is missing!");
			printUsageAndExit();
		}

		if (!sourceFile.canRead()) {
			System.err.println("Cannot read file " + sourceFile);
			printUsageAndExit();
		}

		if (targetFile == null) {
			System.err.println("Target file is missing!");
			printUsageAndExit();
		}
	}

	/**
	 * Performs the actual pruning.
	 */
	private void prune() throws IOException {
		preservePrefixes = CollectionUtils.toArray(preservePrefixesSet,
				String.class);

		System.out.println("Analyzing " + sourceFile);

		JarEntry root = buildTree();

		log("Source jar file", root);

		Set<String> deletedEntries = new HashSet<String>();

		filterPrefixes(root, deletedEntries);

		log("After prefix filtering", root);

		if (filterKnownClasses) {
			initClassLoader();
			root.filterKnownClassFiles(deletedEntries);
			log("After filtering of known classes", root);
		}

		if (pruneDirectories) {
			root.filterNonClassResources(deletedEntries);
			log("After filtering of directories without classes", root);
		}

		writeTargetFile(root);

		System.out.println("Pruned jar file written to " + targetFile);

		if (logFile != null) {
			FileSystemUtils.writeFileUTF8(logFile, StringUtils.concat(
					CollectionUtils.sort(deletedEntries), StringUtils.CR));
			System.out
					.println("List of filtered entries written to " + logFile);
		}
	}

	/**
	 * Build the tree representing the jar file.
	 */
	private JarEntry buildTree() throws IOException {
		PathBasedContentProviderBase provider = PathBasedContentProviderBase
				.createProvider(sourceFile);
		Collection<String> paths = provider.getPaths();
		FileSystemUtils.close(provider);
		return TreeUtils.createTreeFromStrings(new HashSet<String>(paths), "/",
				new EntryHandler());
	}

	/**
	 * Initialized bundle classloader.
	 */
	private void initClassLoader() {
		BundlesManager manager = new BundlesManager();

		try {
			for (String bundleCollection : bundleCollections) {
				manager.addBundleCollection(bundleCollection);
			}

			for (String bundleLocation : bundleLocations) {
				manager.addBundleLocation(bundleLocation);
			}

			manager.initBundles();
		} catch (BundleException ex) {
			System.err.println("Problem loading bundles: " + ex.getMessage());
			System.exit(1);
		}
	}

	/**
	 * Write the target file.
	 */
	private void writeTargetFile(JarEntry root) throws FileNotFoundException,
			IOException {
		PathBasedContentProviderBase provider = PathBasedContentProviderBase
				.createProvider(sourceFile);
		JarOutputStream out = null;

		try {
			out = new JarOutputStream(new FileOutputStream(targetFile));
			root.writeToJar(out, provider);

		} finally {
			FileSystemUtils.close(out);
			FileSystemUtils.close(provider);
		}
	}

	/** Print log statement. */
	private void log(String prefix, JarEntry root) {
		System.out.println(prefix + ": "
				+ root.getFileCount(StringUtils.EMPTY_STRING) + " files ("
				+ root.getFileCount(CLASS_FILE_SUFFIX) + " classes).");
	}

	/**
	 * Filter nodes from tree that match one of prefixes specified in
	 * {@link #filterPrefixes}.
	 */
	private void filterPrefixes(JarEntry root, Set<String> deletedEntries) {
		for (String prefix : filterPrefixes) {
			JarEntry entry = root.getEntryByPath(prefix);
			if (entry == null) {
				System.err.println("No entries with prefix " + prefix
						+ " found.");
			} else {
				entry.delete(deletedEntries);
			}
		}
	}

	/**
	 * Handle to create {@link JarEntry}-tree with
	 * {@link TreeUtils#createTreeFromStrings(Set, String, ITreeNodeHandler)}.
	 */
	private class EntryHandler implements ITreeNodeHandler<JarEntry, String> {

		/** {@inheritDoc} */
		@Override
		public JarEntry getOrCreateChild(JarEntry entry, String name) {
			JarEntry child = entry.getChild(name);
			if (child == null) {
				child = entry.addChild(name);
			}
			return child;
		}

		/** {@inheritDoc} */
		@Override
		public JarEntry createRoot() {
			return new JarEntry("/", null, preservePrefixes);
		}
	}
}
