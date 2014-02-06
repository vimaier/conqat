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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.PropertyConfigurator;
import org.conqat.engine.core.bundle.BundleInfo;
import org.conqat.engine.core.bundle.BundleUtils;
import org.conqat.engine.core.bundle.BundlesConfiguration;
import org.conqat.engine.core.driver.error.DriverException;
import org.conqat.engine.core.driver.error.EDriverExceptionType;
import org.conqat.engine.core.driver.error.EnvironmentException;
import org.conqat.engine.core.driver.error.ErrorLocation;
import org.conqat.engine.core.driver.specification.SpecificationLoader;
import org.conqat.engine.core.driver.util.PropertyUtils;
import org.conqat.lib.commons.cache4j.CacheFactory;
import org.conqat.lib.commons.cache4j.CacheFactory.CacheCreationRule;
import org.conqat.lib.commons.cache4j.CacheRuleParsingException;
import org.conqat.lib.commons.cache4j.ECacheThreadSupport;
import org.conqat.lib.commons.cache4j.backend.ECachingStrategy;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.error.RethrowingExceptionHandler;
import org.conqat.lib.commons.filesystem.CanonicalFile;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.string.StringUtils;

/**
 * This class provides multiple utility methods for implementing the
 * {@link Driver} or classes which somehow try to execute parts of ConQAT.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46960 $
 * @ConQAT.Rating GREEN Hash: 1F41ED3EECD9B3137639BF06BE28CD0D
 */
public class DriverUtils {

	/** Prefix used to mark the block identifier. */
	public static final String BLOCK_IDENTIFIER_PREFIX = PropertyUtils.COMMENT_PREFIX
			+ "!";

	/** Keyword used to mark inheritance of block */
	public static final String CQR_BLOCK_INHERIT_PREFIX = "inherit:";

	/** Keyword used to mark property inclusion (introduced by ##). */
	public static final String CQR_INCLUDE_PREFIX = PropertyUtils.COMMENT_PREFIX
			+ PropertyUtils.COMMENT_PREFIX + "include:";

	/** Keyword used to mark abstract runconfig (introduced by ##). */
	public static final String CQR_ABSTRACT_PREFIX = PropertyUtils.COMMENT_PREFIX
			+ PropertyUtils.COMMENT_PREFIX + "abstract";

	/** where to find log4j configuration */
	private static final String LOGGING_CONFIG_FILE = "config/logging.properties";

	/** where to find cache4j configuration */
	private static final String CACHES_CONFIG_FILE = "config/cache4j.config";

	/** Environment variable. */
	private static final String CONQAT_HOME_ENV = "CONQAT_HOME";

	/** Logger */
	private static final Logger LOGGER = Logger.getLogger(DriverUtils.class);

	/**
	 * Flag used to avoid multiple calls to {@link #initLogger(File)}. This
	 * avoids excessive output in cases where the driver is started multiple
	 * times in the same VM (for example during system testing).
	 */
	private static boolean loggerInitialized = false;

	/**
	 * Causes all processors and blocks available in the bundles to be compiled.
	 * This way the entire ConQAT installation can be checked for errors. In
	 * case of detected errors, an exception is thrown.
	 * <p>
	 * AS there is no simple way to find all blocks relative to the current
	 * configuration, these are not compiled here.
	 */
	public static void compileAllProcessorsAndBlocks(
			BundlesConfiguration bundleConfig, SpecificationLoader specLoader)
			throws EnvironmentException, DriverException {
		for (BundleInfo bundleInfo : bundleConfig.getBundles()) {
			List<String> processorNames;
			try {
				processorNames = BundleUtils.getProvidedProcessors(bundleInfo,
						RethrowingExceptionHandler
								.<EnvironmentException> getInstance());
			} catch (IOException e) {
				throw new EnvironmentException(EDriverExceptionType.IO_ERROR,
						"Problems loading bundle: " + bundleInfo.getId(), e,
						ErrorLocation.UNKNOWN);
			}
			for (String processorSpecName : processorNames) {
				specLoader.getProcessorSpecification(processorSpecName);
			}
			for (String blockSpecName : BundleUtils
					.getProvidedBlockSpecifications(bundleInfo)) {
				specLoader.getBlockSpecification(blockSpecName);
			}
		}
	}

	/**
	 * Initialize logging for ConQAT.
	 * 
	 * @param loggingConfigFile
	 *            the file to read the logging configuration from. If this is
	 *            null or the file is not readable, silently other default
	 *            locations are tried automatically.
	 */
	public static void initLogger(File loggingConfigFile) {
		if (loggerInitialized) {
			return;
		}
		loggerInitialized = true;

		loggingConfigFile = locateConfig(loggingConfigFile, LOGGING_CONFIG_FILE);

		if (loggingConfigFile == null) {
			Logger.getRootLogger().setLevel(Level.INFO);
			BasicConfigurator.configure(new ConsoleAppender(new PatternLayout(
					"%-5p : %m%n")));
			LOGGER.warn("No logging configuration found. Logging to console!");
		} else {
			PropertyConfigurator.configure(loggingConfigFile.getAbsolutePath());
			LOGGER.info("Logger configured by file: "
					+ loggingConfigFile.getAbsolutePath());
		}
	}

	/** Attempts to locate a config file. Returns null if no config was found. */
	private static File locateConfig(File configFile, String defaultPath) {
		if (configFile != null && configFile.canRead()) {
			return configFile;
		}

		// lookup in ConQAT home
		String conqatHome = System.getenv(CONQAT_HOME_ENV);
		if (conqatHome != null) {
			if (!conqatHome.endsWith(File.separator)) {
				conqatHome += File.separator;
			}
			configFile = new File(conqatHome + defaultPath);
			if (configFile.canRead()) {
				return configFile;
			}
		}

		// lookup in working directory
		configFile = new File(defaultPath);
		if (configFile.canRead()) {
			return configFile;
		}

		return null;
	}

	/**
	 * Parse expression of the block as stored in the first line of a run
	 * config. This can be either the name of a block or the keyword
	 * {@link #CQR_BLOCK_INHERIT_PREFIX} followed by the name of a run config.
	 * 
	 * @throws IOException
	 *             if the format is violated.
	 */
	public static String parseBlockExpression(String line) throws IOException {
		if (StringUtils.isEmpty(line)) {
			throw new IOException("Empty file.");
		}
		if (!line.trim().startsWith(BLOCK_IDENTIFIER_PREFIX)) {
			throw new IOException("Block identifier not found.");
		}

		return line.split(BLOCK_IDENTIFIER_PREFIX, 2)[1].trim();
	}

	/** Initializes the caches. */
	public static void initCaches(File cacheConfigFile) {
		CacheFactory.getInstance().setDefaultRule(
				new CacheCreationRule("", ECacheThreadSupport.THREADLOCAL,
						ECachingStrategy.MEMORY, 0));

		cacheConfigFile = locateConfig(cacheConfigFile, CACHES_CONFIG_FILE);

		if (cacheConfigFile == null) {
			LOGGER.warn("No cache configuration found!");
		} else {
			try {
				LOGGER.info("Configuring caches from file "
						+ cacheConfigFile.getAbsolutePath());
				CacheFactory.getInstance().loadCacheConfiguration(
						cacheConfigFile);
			} catch (IOException e) {
				LOGGER.error("Loading cache config failed: " + e.getMessage(),
						e);
			} catch (CacheRuleParsingException e) {
				LOGGER.error("Loading cache config failed: " + e.getMessage(),
						e);
			}
		}
	}

	/**
	 * Loads the content of a runconfiguration as a list of lines. This method
	 * also performs the preprocessing required for CQR inheritance.
	 */
	public static List<String> loadProcessedRunConfig(File runConfigFile)
			throws IOException {
		return loadProcessedRunConfig(new CanonicalFile(runConfigFile),
				new HashSet<CanonicalFile>());
	}

	/**
	 * Loads the content of a runconfiguration as a list of lines. This method
	 * also performs the preprocessing required for CQR inheritance.
	 * 
	 * @param processedRunConfigFiles
	 *            set of files processed so far to avoid cyclic dependencies.
	 */
	private static List<String> loadProcessedRunConfig(
			CanonicalFile runConfigFile,
			Set<CanonicalFile> processedRunConfigFiles) throws IOException {
		List<String> lines = FileSystemUtils.readLinesUTF8(runConfigFile);
		return loadProcessedRunConfig(lines, runConfigFile,
				processedRunConfigFiles);
	}

	/**
	 * Loads the content of a runconfiguration as a list of lines. This method
	 * also performs the preprocessing required for CQR inheritance.
	 * 
	 * @param configLines
	 *            This may be different from the actual lines of the config
	 *            file.
	 * @param runConfigFile
	 *            the runconfig file. This is only used to lookup relative
	 *            paths, thus the content is not relevant.
	 * @param processedRunConfigFiles
	 *            set of files processed so far to avoid cyclic dependencies.
	 */
	public static List<String> loadProcessedRunConfig(List<String> configLines,
			CanonicalFile runConfigFile,
			Set<CanonicalFile> processedRunConfigFiles) throws IOException {
		if (configLines.size() < 1) {
			throw new IOException("RunConfig must have at least one line: "
					+ runConfigFile);
		}

		Set<CanonicalFile> forbidden = new HashSet<CanonicalFile>(
				processedRunConfigFiles);
		if (!forbidden.add(runConfigFile)) {
			throw new IOException(
					"Had a cyclic inclusion when loading runconfig "
							+ runConfigFile);
		}

		List<String> result = new ArrayList<String>();
		expandInheritance(configLines.get(0), runConfigFile, forbidden, result);
		expandIncludes(configLines, runConfigFile, forbidden, result);
		return result;
	}

	/**
	 * Expands the inheritance construct (if present) from the first line of a
	 * CQR. Result is appended to given list.
	 */
	private static void expandInheritance(String firstLine,
			CanonicalFile runConfigFile, Set<CanonicalFile> forbidden,
			List<String> expandedConfig) throws IOException {
		String blockExpression = parseBlockExpression(firstLine);
		if (blockExpression.startsWith(CQR_BLOCK_INHERIT_PREFIX)) {
			String inheritFile = StringUtils.stripPrefix(
					CQR_BLOCK_INHERIT_PREFIX, blockExpression).trim();
			CanonicalFile configFile = new CanonicalFile(
					runConfigFile.getParentFile(), inheritFile);
			// inherit both block and all parameters
			expandedConfig
					.addAll(loadProcessedRunConfig(configFile, forbidden));
		} else {
			expandedConfig.add(firstLine);
		}
	}

	/**
	 * Expands include constructs (if present) in a CQR. Other content is kept
	 * verbatim. Result is appended to given list.
	 */
	private static void expandIncludes(List<String> configLines,
			CanonicalFile runConfigFile, Set<CanonicalFile> forbidden,
			List<String> result) throws IOException {
		for (String line : CollectionUtils.getRest(configLines)) {
			if (line.startsWith(CQR_INCLUDE_PREFIX)) {
				String inheritFile = StringUtils.stripPrefix(
						CQR_INCLUDE_PREFIX, line).trim();
				CanonicalFile configFile = new CanonicalFile(
						runConfigFile.getParentFile(), inheritFile);
				result.addAll(CollectionUtils.getRest(loadProcessedRunConfig(
						configFile, forbidden)));
			} else {
				result.add(line);
			}
		}
	}

	/**
	 * Returns whether the given line denotes an abstract run config (i.e.
	 * starts with {@link #CQR_ABSTRACT_PREFIX}).
	 */
	public static boolean isAbstractLine(String line) {
		return line.startsWith(CQR_ABSTRACT_PREFIX);
	}

	/**
	 * Returns whether the given line denotes an inclusion (i.e. starts with
	 * {@link #CQR_INCLUDE_PREFIX}).
	 */
	public static boolean isIncludeLine(String line) {
		return line.startsWith(CQR_INCLUDE_PREFIX);
	}
}