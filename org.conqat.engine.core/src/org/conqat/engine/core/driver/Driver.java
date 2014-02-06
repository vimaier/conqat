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

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.conqat.engine.core.ConQATInfo;
import org.conqat.engine.core.bundle.BundleException;
import org.conqat.engine.core.bundle.BundleInfo;
import org.conqat.engine.core.bundle.BundlesConfiguration;
import org.conqat.engine.core.driver.declaration.BlockDeclaration;
import org.conqat.engine.core.driver.error.BlockFileException;
import org.conqat.engine.core.driver.error.DriverException;
import org.conqat.engine.core.driver.error.EDriverExceptionType;
import org.conqat.engine.core.driver.error.EnvironmentException;
import org.conqat.engine.core.driver.error.ErrorLocation;
import org.conqat.engine.core.driver.info.BlockInfo;
import org.conqat.engine.core.driver.instance.BlockInstance;
import org.conqat.engine.core.driver.instance.ExecutionContext;
import org.conqat.engine.core.driver.runner.ConQATRunnerBase;
import org.conqat.engine.core.driver.specification.BlockSpecification;
import org.conqat.engine.core.driver.specification.SpecificationLoader;
import org.conqat.engine.core.driver.util.PropertyUtils;
import org.conqat.lib.commons.cache4j.CacheFactory;
import org.conqat.lib.commons.collections.ListMap;
import org.conqat.lib.commons.concurrent.InThreadExecutorService;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.options.AOption;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.commons.system.PerformanceMonitor;

/**
 * This is ConQAT's main class. The driver calls the different steps of ConQAT
 * processing in the right order.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46960 $
 * @ConQAT.Rating GREEN Hash: A05FE4F5F528414953627EAEF2D9FB0D
 */
public class Driver extends ConQATRunnerBase {

	/** Logger */
	private static final Logger LOGGER = Logger.getLogger(Driver.class);

	/** File to store cache statistics in. */
	private File cacheStatisticsFile = null;

	/** The executor used. */
	private ExecutorService executorService = new InThreadExecutorService();

	/** The block file or block name to be used use. */
	private String rootBlock = null;

	/** dry run (= only check configuration) or real run? */
	private boolean dryRun = false;

	/**
	 * If true all block-specs are compiled, otherwise only those that are
	 * required are compiled.
	 */
	private boolean compileAll = false;

	/** The directory used for storing temporary files. */
	private File tempDir = new File(System.getProperty("java.io.tmpdir"));

	/**
	 * The instrumentation interface used (if nothing is set externally use the
	 * default).
	 */
	private ConQATInstrumentation instrumentation = new ConQATInstrumentation();

	/** Name of the instrumentation class. */
	private String instrumentationClassParam;

	/**
	 * Properties set from the command line. Maps from parameter.attribute to
	 * list of actual values.
	 */
	private final ListMap<String, String> commandLineProperties = new ListMap<String, String>();

	/**
	 * Properties taken from properties files. This is managed separately, as
	 * the {@link #commandLineProperties} override these. Maps from
	 * parameter.attribute to list of actual values.
	 */
	private final ListMap<String, String> propertiesFileProperties = new ListMap<String, String>();

	/**
	 * Main method:
	 * <ol>
	 * <li>interpret command line,</li>
	 * <li>create the bundle configuration,</li>
	 * <li>trigger the execution,</li>
	 * <li>catch exceptions.</li>
	 * <ol>
	 * 
	 * @param args
	 *            command line arguments
	 */
	public static void main(String[] args) {
		Driver driver = new Driver();
		driver.initFromCommandLine(args);
		driver.initLogger();
		driver.initCaches();

		try {
			BundlesConfiguration bundleConfig = driver.loadBundles();
			driver.drive(bundleConfig);
		} catch (BundleException e) {
			LOGGER.fatal("Bundle exception: " + e.getMessage() + " @ "
					+ e.getLocationsAsString());
			System.exit(1);
		} catch (DriverException e) {
			LOGGER.fatal("Reading block '" + driver.rootBlock + "' failed: "
					+ e.getMessage() + " @ " + e.getLocationsAsString());
			System.exit(1);
		}
	}

	/** Initialize logger and log status message. */
	protected void initLogger() {
		DriverUtils.initLogger(loggingConfigFile);
		LOGGER.info("ConQAT " + ConQATInfo.DIST_VERSION + " (core "
				+ ConQATInfo.CORE_VERSION + ") running on Java "
				+ System.getProperty("java.version") + " ["
				+ System.getProperty("java.vm.name") + " "
				+ System.getProperty("java.vm.version") + "]");
	}

	/** Initialize cache4j framework. */
	protected void initCaches() {
		DriverUtils.initCaches(cacheConfigFile);
	}

	/**
	 * Create the configuration and trigger the execution.
	 * <p>
	 * For testing purposes, the bundleConfig may be <code>null</code>, but then
	 * {@link #compileAll} may not be used and some feature may not work
	 * correctly.
	 * 
	 * @throws DriverException
	 *             if reading the configuration fails.
	 */
	public void drive(BundlesConfiguration bundleConfig) throws DriverException {

		if (rootBlock == null) {
			throw new IllegalArgumentException(
					"No configuration to be executed provided!");
		}

		LOGGER.info("ConQAT using block '" + rootBlock + "'.");

		PerformanceMonitor monitor = PerformanceMonitor.create(true);

		BlockInstance configInstance = prepareMainInstance(bundleConfig);

		checkInstrumentation();

		if (dryRun) {
			LOGGER.info("Configuration '" + rootBlock + "' seems to be OK.");
		} else {
			ExecutionContext executionContext = new ExecutionContext(
					new BlockInfo(configInstance), bundleConfig,
					instrumentation, tempDir, executorService);
			if (instrumentation.beginExecution(executionContext)) {
				configInstance.execute(executionContext, instrumentation);
			}
			executionContext.performShutdown();
			instrumentation.endExecution();
			executorService.shutdownNow();
		}

		monitor.stop();
		LOGGER.info("Max memory: "
				+ StringUtils.format(monitor.getMaxMemUsageInKBs()) + "kB.");
		LOGGER.info("Total time: " + monitor.getSeconds() + "s");

		dumpCacheStatistics();
	}

	/**
	 * Checks whether the {@link #instrumentationClassParam} is set and updates
	 * the instrumentation accordingly.
	 */
	private void checkInstrumentation() {
		if (instrumentationClassParam == null) {
			return;
		}

		String[] parts = instrumentationClassParam.split(":");
		String className = parts[0];
		String[] parameters = Arrays.copyOfRange(parts, 1, parts.length);

		LOGGER.info("Loading instrumentation from class " + className);

		try {
			// it is crucial here to use the class loader from the bundles, as
			// we might want to locate instrumentation classes in the bundles
			// (at least that is the easiest way to bet them class loadable).
			instrumentation = (ConQATInstrumentation) Thread.currentThread()
					.getContextClassLoader().loadClass(className).newInstance();
			instrumentation.init(parameters);
		} catch (Throwable e) {
			// This catches all: ClassCast, ClassNotFound, etc.
			// IMHO this "reduced" error handling is ok, as this is an option
			// for the ConQAT power user.
			LOGGER.fatal("Could not load class " + className + ": "
					+ e.getMessage());
			System.exit(1);
		}
	}

	/** Writes statistics on cache usage in {@link #cacheStatisticsFile}. */
	protected void dumpCacheStatistics() {
		if (cacheStatisticsFile != null) {
			try {
				FileSystemUtils
						.ensureParentDirectoryExists(cacheStatisticsFile);
				FileSystemUtils.writeFileUTF8(cacheStatisticsFile, CacheFactory
						.getInstance().getStatistics());
			} catch (IOException e) {
				LOGGER.error(
						"Writing cache statistics failed: " + e.getMessage(), e);
			}
		}
	}

	/**
	 * Creates the main instance for the config file, which is then used for
	 * actually executing it.
	 */
	private BlockInstance prepareMainInstance(BundlesConfiguration bundleConfig)
			throws NoClassDefFoundError, EnvironmentException, DriverException {
		Set<BundleInfo> bundles = new HashSet<BundleInfo>();

		// This can be null if no bundle collections and locations are provided
		if (bundleConfig != null) {
			bundles = bundleConfig.getBundles();
		}

		// determine base dir if possible (for non-toplevel blocks this is null)
		File baseDir = new File(rootBlock).getParentFile();
		if (baseDir != null && !baseDir.isDirectory()) {
			baseDir = null;
		}
		if (baseDir != null) {
			LOGGER.info("Using base directory:" + baseDir);
		}
		SpecificationLoader specLoader = new SpecificationLoader(baseDir,
				bundles);
		if (compileAll) {
			DriverUtils.compileAllProcessorsAndBlocks(bundleConfig, specLoader);
		}

		// create configuration

		// first try to load as fully qualified block name
		BlockSpecification configSpecification = specLoader
				.getBlockSpecification(rootBlock);

		// fall back: treat as file
		if (configSpecification == null) {
			File configFile = new File(rootBlock);
			if (!configFile.canRead()) {
				throw new BlockFileException(EDriverExceptionType.IO_ERROR,
						configFile + " does not exists!", ErrorLocation.UNKNOWN);
			}

			configSpecification = new BlockFileReader(specLoader)
					.readBlockFile(configFile);
			configSpecification.initialize();
		}

		ListMap<String, String> properties = determineProperties();
		BlockDeclaration configDeclaration = new BlockDeclaration(
				configSpecification, specLoader, properties);

		return configDeclaration.instantiate(null);
	}

	/**
	 * Compute the properties that are ultimately used. Properties specified via
	 * the command line have preference over properties specified via a
	 * properties file.
	 */
	private ListMap<String, String> determineProperties() {
		ListMap<String, String> properties = new ListMap<String, String>(
				propertiesFileProperties);
		for (String key : commandLineProperties.getKeys()) {
			// command line properties replace file properties
			properties.removeCollection(key);
			properties.addAll(key, commandLineProperties.getCollection(key));
		}
		return properties;
	}

	/**
	 * Get root block. This can either be the full qualified name of a block or
	 * the filename of a block file. Maybe <code>null</code> if not already set.
	 */
	protected String getRootBlock() {
		return rootBlock;
	}

	/** Set file used for cache statistics. */
	@AOption(longName = "cache-stat", description = "set file to write cache statistics to")
	public void setCacheStatistics(String outputFile) {
		cacheStatisticsFile = new File(outputFile);
	}

	/** Set the name of the config file used (root block). */
	@AOption(shortName = 'f', longName = "config", description = "the file containing the root block (config); "
			+ "may also be qualified block name or name of a CQR file")
	public void setConfigFileName(String rootBlockName) {

		if (rootBlock != null) {
			throw new IllegalArgumentException(
					"May not set multiple configurations!");
		}

		// check for CQR file
		if (rootBlockName.toLowerCase().endsWith(
				"." + ConQATInfo.RUNCONFIG_FILE_EXTENSION)) {
			File cqrFile = new File(rootBlockName);
			if (cqrFile.canRead()) {
				rootBlockName = resolveCQRFile(cqrFile);
			}
		}

		// We do not yet check for existence here, as this is done later anyways
		rootBlock = rootBlockName;
	}

	/**
	 * Resolves a CQR file by determining (and returning) the name of the block
	 * to be used and loading the properties from the file.
	 * 
	 * @throws IllegalArgumentException
	 *             if the file is no valid CQR file.
	 */
	private String resolveCQRFile(File cqrFile) {
		try {
			List<String> lines = DriverUtils.loadProcessedRunConfig(cqrFile);

			propertiesFileProperties.addAll(PropertyUtils
					.parseCqProperties(lines));

			String blockIdentifier = DriverUtils.parseBlockExpression(lines
					.get(0));
			File topLevelBlock = new File(cqrFile.getParentFile(),
					blockIdentifier + "." + ConQATInfo.BLOCK_FILE_EXTENSION);
			if (topLevelBlock.canRead()) {
				return topLevelBlock.getAbsolutePath();
			}
			return blockIdentifier;
		} catch (IOException e) {
			// we use an IllegalArgumentException here, as this method is called
			// in the context of option parsing, where such an exception is
			// expected/dealt with properly
			throw new IllegalArgumentException("Could not parse CQR file "
					+ cqrFile.getPath() + ": " + e.getMessage());
		}
	}

	/** Set the dry-run option: only check config file */
	@AOption(shortName = 'n', longName = "dry-run", description = "only check config file")
	public void setDryRun() {
		dryRun = true;
	}

	/** Set the compile-all option: compile all block-specs */
	@AOption(shortName = 'a', longName = "compile-all", description = "compile all block-specs")
	public void setCompileAll() {
		compileAll = true;
	}

	/**
	 * Set properties file. If the file can not be read, the program will be
	 * terminated.
	 */
	@AOption(shortName = 's', longName = "properties-file", greedy = true, description = ""
			+ "load block inputs from a properties file")
	public void readPropertyFile(String propertiesFilename) {
		LOGGER.info("Reading properties from file: "
				+ new File(propertiesFilename));
		try {
			propertiesFileProperties.addAll(PropertyUtils
					.parseCqProperties(new File(propertiesFilename)));
		} catch (IOException e) {
			throw new IllegalArgumentException("Reading properties file '"
					+ propertiesFilename + "' failed: " + e.getMessage());
		}
	}

	/** Adds a property which is set via the command line. */
	@AOption(shortName = 'p', longName = "property", description = ""
			+ "override a property (name=value) in the used configuration "
			+ "(or read from a property file via the -s option.)")
	public void addCommandLineProperty(String nameValue) {
		String[] parts = nameValue.split("=", 2);
		if (parts.length < 2) {
			throw new IllegalArgumentException(
					"Given property must be of format <name>=<value>!");
		}
		commandLineProperties.add(parts[0], parts[1]);
	}

	/** Sets the instrumentation interface used. */
	public void setInstrumentation(ConQATInstrumentation instrumentation) {
		this.instrumentation = instrumentation;
	}

	/** Set worker mode. */
	@AOption(shortName = 'i', longName = "instrument", description = ""
			+ "Provides the name of a class used for instrumentation. "
			+ "Separate additional parameters using colons.")
	public void setInstrumentationClass(String param) {
		instrumentationClassParam = param;
	}

	/** Set temporary directory. */
	@AOption(shortName = 't', longName = "tempdir", description = ""
			+ "Sets the directory used to store temporary files in.")
	public void setTempDir(String tempDir) {
		this.tempDir = new File(tempDir);
	}

	/** Set multi core/thread mode. */
	@AOption(shortName = 'm', longName = "multi-thread", description = ""
			+ "Sets the number of threads to use. This is still an experimental feature!")
	public void setMultiThread(int numThreads) {
		if (numThreads < 1) {
			throw new IllegalArgumentException(
					"Number of threads must be positive!");
		}
		executorService = Executors.newFixedThreadPool(numThreads);
	}
}