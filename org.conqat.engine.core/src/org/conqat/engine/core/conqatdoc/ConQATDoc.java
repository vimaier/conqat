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
package org.conqat.engine.core.conqatdoc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.conqat.engine.core.bundle.BundleException;
import org.conqat.engine.core.bundle.BundleInfo;
import org.conqat.engine.core.bundle.BundleUtils;
import org.conqat.engine.core.bundle.BundlesConfiguration;
import org.conqat.engine.core.conqatdoc.content.BlockSpecificationPageGenerator;
import org.conqat.engine.core.conqatdoc.content.BundleDetailsPageGenerator;
import org.conqat.engine.core.conqatdoc.content.MainPageGenerator;
import org.conqat.engine.core.conqatdoc.content.ProcessorSpecificationPageGenerator;
import org.conqat.engine.core.conqatdoc.menu.BundleSpecificationListGenerator;
import org.conqat.engine.core.conqatdoc.menu.BundlesListGenerator;
import org.conqat.engine.core.conqatdoc.menu.IndexPageGenerator;
import org.conqat.engine.core.conqatdoc.menu.SpecificationListGenerator;
import org.conqat.engine.core.conqatdoc.types.TypeListGenerator;
import org.conqat.engine.core.driver.BundleCommandLineBase;
import org.conqat.engine.core.driver.error.DriverException;
import org.conqat.engine.core.driver.specification.BlockSpecification;
import org.conqat.engine.core.driver.specification.ProcessorSpecification;
import org.conqat.engine.core.driver.specification.SpecificationLoader;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.options.AOption;

/**
 * Entry class for the ConQATDoc tool.
 * <p>
 * ConQATDoc is used to generate documentation for the processors and block
 * specifications of all installed bundles.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 6B424D3D71F23C6B314B1B2FAB4D7FF4
 */
public class ConQATDoc extends BundleCommandLineBase {

	/** The directory to generate the documentation into. */
	private File targetDirectory = null;

	/** All directories where JavaDoc should be read from. */
	private final List<File> javaDocLocations = new ArrayList<File>();

	/**
	 * Mapping from java class prefix (i.e. package name) to external JavaDoc
	 * location (i.e. URL).
	 */
	private final Map<String, String> externalJavaDocLocations = new LinkedHashMap<String, String>();

	/** The resolver used for JavaDoc. */
	private JavaDocLinkResolver javaDocResolver;

	/**
	 * The set of all known processors, filled during the generation of the
	 * pages.
	 */
	private final Set<ProcessorSpecification> allProcessors = new HashSet<ProcessorSpecification>();

	/** The set of all known blocks, filled during the generation of the pages. */
	private final Set<BlockSpecification> allBlocks = new HashSet<BlockSpecification>();

	/** Sets the name of the output directory. */
	@AOption(shortName = 'o', longName = "output", description = "the name of the output directory")
	public void setTargetDirectory(File directory) {
		targetDirectory = directory;
	}

	/** Sets the name of the output directory. */
	@AOption(shortName = 'j', longName = "javadoc", description = "Add a new JavaDoc location.")
	public void addJavaDocLocation(File directory) {
		if (!directory.isDirectory()) {
			throw new IllegalArgumentException("Not a directory: " + directory);
		}
		javaDocLocations.add(directory);
	}

	/** Sets the name of the output directory. */
	@AOption(shortName = 'e', longName = "ext-javadoc", description = ""
			+ "Add a new external JavaDoc location as a name value pair (separated by '='), "
			+ "where the name is the class prefix used (e.g. 'edu.tum.cs.common.') "
			+ "and the value is the first part of the URL where the documentation can be found.")
	public void addExternalJavaDocLocation(String pair) {
		String[] parts = pair.split("=");
		if (parts.length != 2) {
			throw new IllegalArgumentException("Expected exactly one '=' in "
					+ pair);
		}
		externalJavaDocLocations.put(parts[0], parts[1]);
	}

	/** {@inheritDoc} */
	@Override
	protected void initFromCommandLine(String[] args) {
		super.initFromCommandLine(args);

		if (targetDirectory == null) {
			System.err.println("Output directory missing!");
			printUsageAndExit();
		}

		if (targetDirectory.exists() && !targetDirectory.isDirectory()) {
			System.err.println("Output directory can not be created!");
			printUsageAndExit();
		}
	}

	/**
	 * Generates the documentation for all bundles currently in the bundle pool.
	 * 
	 * @throws BundleException
	 */
	public void generateDocumentation() throws IOException, DriverException,
			BundleException {

		targetDirectory.mkdirs();

		BundlesConfiguration bundleConfig = loadBundles();
		Set<BundleInfo> bundles = bundleConfig.getBundles();
		initJavaDocResolver(bundles);
		createStyleSheet();

		SpecificationLoader specLoader = new SpecificationLoader(null, bundles);
		for (BundleInfo bundle : bundles) {
			generateForBundle(bundle, specLoader);
		}
		generateMenu(bundles);

	}

	/**
	 * Generates the pages for the given bundle and updates the
	 * {@link #allProcessors} and {@link #allBlocks} sets.
	 */
	private void generateForBundle(BundleInfo bundle,
			SpecificationLoader specLoader) throws IOException, DriverException {

		Collection<ProcessorSpecification> processors = findProcessors(bundle,
				specLoader);
		Collection<BlockSpecification> blocks = findBlocks(bundle, specLoader);
		allProcessors.addAll(processors);
		allBlocks.addAll(blocks);

		new BundleDetailsPageGenerator(targetDirectory, bundle, processors,
				blocks, javaDocResolver).generate();
		new BundleSpecificationListGenerator(targetDirectory, bundle,
				processors, blocks).generate();

		for (ProcessorSpecification proc : processors) {
			new ProcessorSpecificationPageGenerator(targetDirectory, proc,
					bundle, javaDocResolver).generate();
		}
		for (BlockSpecification block : blocks) {
			new BlockSpecificationPageGenerator(targetDirectory, block, bundle,
					javaDocResolver).generate();
		}
	}

	/** Generate remaining menu pages. */
	private void generateMenu(Set<BundleInfo> bundles) throws IOException,
			DriverException {
		new IndexPageGenerator(targetDirectory).generate();
		new MainPageGenerator(targetDirectory, bundles).generate();
		new BundlesListGenerator(targetDirectory, bundles).generate();
		new SpecificationListGenerator(targetDirectory, allProcessors,
				allBlocks).generate();
		new TypeListGenerator(targetDirectory, allProcessors, allBlocks)
				.generate();
	}

	/** Initializes the resolver for the JavaDoc links. */
	private void initJavaDocResolver(Set<BundleInfo> bundles)
			throws IOException {

		if (javaDocResolver != null) {
			throw new IllegalStateException("May call this method only once!");
		}

		javaDocResolver = new JavaDocLinkResolver();
		for (File dir : javaDocLocations) {
			javaDocResolver.addJavaDocLocation(dir,
					FileSystemUtils.createRelativePath(dir, targetDirectory));
		}
		for (Entry<String, String> entry : externalJavaDocLocations.entrySet()) {
			javaDocResolver.addExternalJavaDocLocation(entry.getKey(),
					entry.getValue());
		}
		for (BundleInfo bundle : bundles) {
			File javaDocDir = new File(bundle.getLocation(), "javadoc");
			if (javaDocDir.isDirectory()) {
				javaDocResolver.addJavaDocLocation(javaDocDir, FileSystemUtils
						.createRelativePath(javaDocDir, targetDirectory));
			}
		}
	}

	/** WRite the stylesheet into the output directory. */
	private void createStyleSheet() throws IOException, FileNotFoundException {
		PrintStream out = new PrintStream(new FileOutputStream(new File(
				targetDirectory, PageGeneratorBase.STYLESHEET_NAME)));
		ConQATDocCSSMananger.getInstance().writeOut(out);
		out.close();
	}

	/** Find all processors provided by the given bundle. */
	private Collection<ProcessorSpecification> findProcessors(
			BundleInfo bundle, SpecificationLoader specLoader)
			throws IOException {

		List<ProcessorSpecification> processors = new ArrayList<ProcessorSpecification>();
		for (String className : BundleUtils.getProvidedProcessors(bundle)) {
			try {
				processors.add(specLoader.getProcessorSpecification(className));
			} catch (Throwable e) {
				System.err.println("Error while loading processor '"
						+ className + "' in bundle '" + bundle.getId() + "': "
						+ e.getMessage());
				e.printStackTrace();
			}
		}
		return processors;
	}

	/** Find all blocks provided by the given bundle. */
	private Collection<BlockSpecification> findBlocks(BundleInfo bundleInfo,
			SpecificationLoader specLoader) {

		List<BlockSpecification> result = new ArrayList<BlockSpecification>();
		for (String name : BundleUtils
				.getProvidedBlockSpecifications(bundleInfo)) {
			try {
				BlockSpecification blockSpec = specLoader
						.getBlockSpecification(name);
				if (blockSpec != null) {
					result.add(blockSpec);
				} else {
					System.err.println("Error: Could not load block " + name);
				}
			} catch (DriverException e) {
				System.err.println("Config-Error for block " + name + ": "
						+ e.getMessage());
			} catch (Exception e) {
				System.err.println("Error: " + e.getMessage());
				e.printStackTrace();
			}
		}
		return result;
	}

	/** Main method. */
	public static void main(String[] args) {

		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.OFF);

		ConQATDoc doc = new ConQATDoc();
		doc.initFromCommandLine(args);
		try {
			doc.generateDocumentation();
		} catch (RuntimeException e) {
			System.err.println("Internal Error: " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}
}