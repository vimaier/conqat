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
package org.conqat.engine.html_presentation.javascript;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.conqat.engine.commons.logging.ListStructuredLogMessage;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.html_presentation.BundleContext;
import org.conqat.engine.html_presentation.javascript.ConQATJavaScriptCompiler.ECompilationMode;
import org.conqat.engine.html_presentation.javascript.JavaScriptFile.EType;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.digest.Digester;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.logging.ILogger;
import org.conqat.lib.commons.string.StringUtils;

/**
 * This is a singleton that contains references to all used JavaScript files.
 * The references must be set prior to the first access to this singleton, for
 * example in a bundle's BundleContext.
 * <p>
 * This class is responsible for collecting the JavaScript code. The compilation
 * phase is delegated to {@link ConQATJavaScriptCompiler}. As the compilation
 * stage can be slow (especially when using advanced compilation), this class
 * also performs caching of the compiled script. For this, the bundle's resource
 * directory is used (if it is available).
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46960 $
 * @ConQAT.Rating GREEN Hash: 72888696C74369A0AE49D2D5699BAA13
 */
public class JavaScriptManager {

	/** The logger. */
	private static final Logger LOGGER = Logger
			.getLogger(JavaScriptManager.class);

	/** The tag used for structured logging. */
	/* package */static final String LOG_TAG = "javascript";

	/** The default name of the generated script file. */
	public static final String SCRIPT_NAME = "conqat.js";

	/** Singleton instance. */
	private static JavaScriptManager instance;

	/** The registered modules. */
	private final Set<Class<? extends JavaScriptModuleBase>> moduleClasses = new HashSet<Class<? extends JavaScriptModuleBase>>();

	/** Caches the compiled script. */
	private String cachedScript;

	/** Caches any exceptions found during assembly/compilation. */
	private ConQATException cachedException;

	/** The compiler used. */
	private final ConQATJavaScriptCompiler compiler;

	/**
	 * Disable caching of the script in {@link #cachedScript} (useful during
	 * development).
	 */
	private boolean disableMemoryCache = false;

	/**
	 * If this is true, disk caching is disabled (which might help during
	 * development).
	 */
	private boolean forceCompile = false;

	/**
	 * The directory used for caching the JavaScript code (and placing the
	 * compiler configuration). This may be null if caching is not possible
	 * (e.g. from a monolith JAR).
	 */
	private final File jsCacheDirectory;

	/**
	 * The file used for caching the JavaScript on disk (may be null if no
	 * caching is used). This is the concrete file located in
	 * {@link #jsCacheDirectory}.
	 */
	private final File cachedScriptFile;

	/**
	 * Set of prefixes of namespaces that look like closure to the closure
	 * compiler but are not. This is used to filter warnings for some
	 * third-party libraries.
	 */
	private final Set<String> nonClosureNamespacePrefixes = new HashSet<String>();

	/** Hidden constructor. */
	private JavaScriptManager() {
		jsCacheDirectory = determineCacheDirectory();

		compiler = new ConQATJavaScriptCompiler(ECompilationMode.SIMPLE, false);

		if (jsCacheDirectory == null) {
			cachedScriptFile = null;
		} else {
			cachedScriptFile = new File(jsCacheDirectory, SCRIPT_NAME);
		}

		loadCompileConfig();
	}

	/**
	 * Determines the directory used for the cached JavaScript and the compiler
	 * config. May return null, if no suitable directory was found (depends on
	 * execution context).
	 */
	private File determineCacheDirectory() {
		BundleContext bundleContext = BundleContext.getInstance();

		// this may happen if the bundle is not initialized (e.g. monolith JAR)
		if (bundleContext == null) {
			return null;
		}

		File file = bundleContext.getResourceManager().getResourceAsFile(
				"js-cache");
		if (file.canRead() && file.isDirectory()) {
			return file;
		}

		return null;
	}

	/** Loads the compilation configuration if one is present. */
	private void loadCompileConfig() {
		if (jsCacheDirectory == null) {
			return;
		}
		File propertiesFile = new File(jsCacheDirectory, "compile.properties");
		if (!propertiesFile.canRead()) {
			return;
		}

		try {
			Properties properties = FileSystemUtils
					.readPropertiesFile(propertiesFile);
			compiler.configureFromProperties(properties);
			forceCompile = Boolean.parseBoolean(properties
					.getProperty("forceCompile"));
			disableMemoryCache = Boolean.parseBoolean(properties
					.getProperty("disableMemoryCache"));
		} catch (IOException e) {
			LOGGER.error("Error while trying to read compiler config from "
					+ propertiesFile + ": " + e.getMessage(), e);
		}
	}

	/** Modifies the compiler configuration based on a properties file. */
	public void configureFromProperties(Properties properties) {
		compiler.configureFromProperties(properties);
	}

	/** Returns the singleton instance. */
	public static synchronized JavaScriptManager getInstance() {
		if (instance == null) {
			instance = new JavaScriptManager();
		}
		return instance;
	}

	/** Registers a JavaScript source via its descriptor. */
	public void registerModule(Class<? extends JavaScriptModuleBase> moduleClass) {
		CCSMAssert
				.isFalse(wasCompiled(),
						"This message may not be called after compilation of the scripts.");
		moduleClasses.add(moduleClass);
	}

	/** Returns whether compilation has been performed. */
	private boolean wasCompiled() {
		return cachedScript != null || cachedException != null;
	}

	/**
	 * Adds a prefix of a namespace that looks like closure to the closure
	 * compiler but is not. This is used to filter warnings for some third-party
	 * libraries.
	 */
	public void addNonClosureNamespacePrefix(String prefix) {
		nonClosureNamespacePrefixes.add(prefix);
	}

	/** Returns the {@link #nonClosureNamespacePrefixes}. */
	/* package */Set<String> getNonClosureNamespacePrefixes() {
		return nonClosureNamespacePrefixes;
	}

	/** Copies the compiled script into the specified target directory. */
	public void copyScript(File targetDirectory, ILogger logger)
			throws ConQATException {
		File targetFile = new File(targetDirectory, SCRIPT_NAME);
		try {
			FileSystemUtils.writeFileUTF8(targetFile, obtainScript(logger));
		} catch (IOException e) {
			throw new ConQATException("Could not write script " + targetFile, e);
		}
	}

	/**
	 * Assembled the script, possibly compiles it, and returns the result. If
	 * this method has been called before, compilation is not performed again,
	 * but a cached result or exception is used.
	 */
	public String obtainScript(ILogger logger) throws ConQATException {
		if (cachedScript != null && !disableMemoryCache) {
			logger.info("Using pre-compiled JavaScript.");
			return cachedScript;
		}

		if (cachedException != null && !disableMemoryCache) {
			throw cachedException;
		}

		logger.info("Compiling JavaScript.");
		try {
			cachedScript = assembleAndCompile(logger);
		} catch (ConQATException e) {
			cachedException = e;
			throw e;
		}
		return cachedScript;
	}

	/**
	 * Returns whether the memory cache is disabled. This indicates a
	 * development situation.
	 */
	public boolean isMemoryCacheDisabled() {
		return disableMemoryCache;
	}

	/** Performs the actual assembly and compilation. */
	private String assembleAndCompile(ILogger logger) throws ConQATException {

		long startTime = System.currentTimeMillis();
		Map<String, JavaScriptFile> files = obtainFiles();
		List<JavaScriptFile> externs = extractExterns(files);
		ConQATSoyUtils.compileSoy(files);
		List<JavaScriptFile> usedFiles = determineUsedFiles(files);
		List<JavaScriptFile> sorted = topSort(usedFiles);

		logger.info("Preparation of scripts took "
				+ (System.currentTimeMillis() - startTime) / 1000. + " seconds");
		logUsedFiles(sorted, logger);

		startTime = System.currentTimeMillis();
		String script = loadCachedScript(sorted, logger);
		if (script == null) {
			script = compiler.compile(sorted, externs, logger);
			cacheScriptOnDisk(logger, script);
		}
		logger.info("Compilation/loading of script took "
				+ (System.currentTimeMillis() - startTime) / 1000.
				+ " seconds and produced a script of size " + script.length());
		return script;
	}

	/** Attempts to cache the compiled script on disk. */
	private void cacheScriptOnDisk(ILogger logger, String script) {
		if (cachedScriptFile == null
				|| ConQATJavaScriptCompiler.DISABLE_COMPILATION) {
			return;
		}

		try {
			FileSystemUtils.writeFileUTF8(cachedScriptFile, script);
		} catch (IOException e) {
			logger.warn("Failed to cache script file for later use!", e);
		}
	}

	/**
	 * Attempts to load a previously stored cached JavaScript. For this, a
	 * checksum over both the compiler settings and the compiler input is
	 * calculated and compared with a stored checksum.
	 */
	private String loadCachedScript(List<JavaScriptFile> sorted, ILogger logger) {
		if (jsCacheDirectory == null || cachedScriptFile == null
				|| forceCompile) {
			return null;
		}

		List<String> content = new ArrayList<String>();

		// include stringified compiler settings in content (and thus check-sum)
		// as well.
		content.add(compiler.getSettingsAsString());
		for (JavaScriptFile file : sorted) {
			content.add(file.getName());
			content.add(file.getContent());
		}

		String checksum = Digester.createMD5Digest(content);
		File cacheCheckSumFile = new File(jsCacheDirectory, "checksum.txt");
		try {
			boolean matches = cacheCheckSumFile.canRead()
					&& cachedScriptFile.canRead()
					&& checksum.equals(FileSystemUtils
							.readFileUTF8(cacheCheckSumFile));
			FileSystemUtils.writeFileUTF8(cacheCheckSumFile, checksum);
			if (matches) {
				logger.info("Reusing cached JavaScript, skipping compilation.");
				return FileSystemUtils.readFileUTF8(cachedScriptFile);
			}
		} catch (IOException e) {
			logger.warn(
					"Had problems accessing cached JavaScript. Falling back to compilation.",
					e);
		}

		return null;
	}

	/**
	 * Obtains all JavaScript files from the {@link #moduleClasses} and returns
	 * them as a name to file map.
	 */
	private Map<String, JavaScriptFile> obtainFiles() throws ConQATException {
		Map<String, JavaScriptFile> files = new HashMap<String, JavaScriptFile>();
		for (Class<? extends JavaScriptModuleBase> moduleClass : moduleClasses) {
			try {
				for (JavaScriptFile file : moduleClass.newInstance()
						.obtainFiles()) {
					if (files.put(file.getName(), file) != null) {
						throw new ConQATException("Duplicate file with name "
								+ file.getName() + " provided by "
								+ moduleClass);
					}
				}
			} catch (InstantiationException e) {
				throw new ConQATException("Could not create instance of "
						+ moduleClass, e);
			} catch (IllegalAccessException e) {
				throw new ConQATException("Could not access constructor of "
						+ moduleClass, e);
			}
		}
		return files;
	}

	/**
	 * Extracts and returns all files of type
	 * {@link JavaScriptFile.EType#CLOSURE_EXTERN}.
	 * 
	 * @param files
	 *            the map of all files. This will be modified, as all externs
	 *            are removed.
	 */
	private List<JavaScriptFile> extractExterns(
			Map<String, JavaScriptFile> files) {
		List<JavaScriptFile> externs = new ArrayList<JavaScriptFile>();
		for (JavaScriptFile file : new ArrayList<JavaScriptFile>(files.values())) {
			if (file.getType() == EType.CLOSURE_EXTERN) {
				files.remove(file.getName());
				externs.add(file);
			}
		}
		return externs;
	}

	/**
	 * Determines the file to be actually used for the compiled script. These
	 * include all files of type {@link JavaScriptFile.EType#CODE_REQUIRED} plus
	 * all trasitive dependencies determined via required/provided namespaces.
	 */
	private List<JavaScriptFile> determineUsedFiles(
			Map<String, JavaScriptFile> files) throws ConQATException {
		Set<String> requiredNamespaces = new HashSet<String>();
		Set<String> providedNamespaces = new HashSet<String>();
		List<JavaScriptFile> usedFiles = new ArrayList<JavaScriptFile>();

		for (JavaScriptFile file : new ArrayList<JavaScriptFile>(files.values())) {
			if (file.getType() == EType.CODE_REQUIRED) {
				updateFilesAndNamespaces(file, files, requiredNamespaces,
						providedNamespaces, usedFiles);
			}
		}

		requiredNamespaces.removeAll(providedNamespaces);

		while (!requiredNamespaces.isEmpty()) {
			boolean hadProgress = false;

			for (JavaScriptFile file : new ArrayList<JavaScriptFile>(
					files.values())) {
				if (providesRequirement(file, requiredNamespaces)) {
					hadProgress = true;
					updateFilesAndNamespaces(file, files, requiredNamespaces,
							providedNamespaces, usedFiles);
				}
			}

			requiredNamespaces.removeAll(providedNamespaces);

			if (!hadProgress) {
				throw new ConQATException("Could not locate "
						+ requiredNamespaces.size() + " namespaces: "
						+ StringUtils.concat(requiredNamespaces, ", "));
			}
		}
		return usedFiles;
	}

	/** Returns true if a file provides at least on of the required namespaces. */
	private boolean providesRequirement(JavaScriptFile file,
			Set<String> requiredNamespaces) {
		for (String provided : file.getProvidedNamespaces()) {
			if (requiredNamespaces.contains(provided)) {
				return true;
			}
		}
		return false;
	}

	/** Helper for {@link #determineUsedFiles(Map)}. */
	private void updateFilesAndNamespaces(JavaScriptFile file,
			Map<String, JavaScriptFile> files, Set<String> requiredNamespaces,
			Set<String> providedNamespaces, List<JavaScriptFile> usedFiles) {
		files.remove(file.getName());
		usedFiles.add(file);
		requiredNamespaces.addAll(file.getRequiredNamespaces());
		providedNamespaces.addAll(file.getProvidedNamespaces());
	}

	/** Logs the files actually used. */
	private static void logUsedFiles(List<JavaScriptFile> sorted, ILogger logger) {
		List<String> fileNames = new ArrayList<String>();
		int index = 0;
		for (JavaScriptFile file : sorted) {
			fileNames.add(String.format("%04d: ", ++index) + file.getName()
					+ " [" + file.getType() + "]");
		}
		logger.info(new ListStructuredLogMessage("Compiling " + sorted.size()
				+ " JavaScript files.", fileNames, LOG_TAG));
	}

	/**
	 * Sorts the files topologically, i.e. a file is guaranteed to no appear
	 * before one which provides a required namespace.
	 * 
	 * @throws ConQATException
	 *             is a cyclic dependency was found.
	 */
	private static List<JavaScriptFile> topSort(List<JavaScriptFile> files)
			throws ConQATException {
		Set<String> providedNamespaces = new HashSet<String>();
		Set<JavaScriptFile> remaining = new HashSet<JavaScriptFile>(files);
		List<JavaScriptFile> topSorted = new ArrayList<JavaScriptFile>();

		while (!remaining.isEmpty()) {

			List<JavaScriptFile> nextFiles = new ArrayList<JavaScriptFile>();
			for (JavaScriptFile file : remaining) {
				if (providedNamespaces
						.containsAll(file.getRequiredNamespaces())) {
					nextFiles.add(file);
				}
			}

			if (nextFiles.isEmpty()) {
				throw new ConQATException("Found cyclic dependency between: "
						+ findCyclicDependencies(remaining));
			}

			remaining.removeAll(nextFiles);
			for (JavaScriptFile file : nextFiles) {
				providedNamespaces.addAll(file.getProvidedNamespaces());
			}

			// sort alphabetically to resolve ties
			Collections.sort(nextFiles);
			topSorted.addAll(nextFiles);
		}

		return topSorted;
	}

	/**
	 * If there is a cyclic dependency in the given list of remaining/unresolved
	 * files, this searches for it and returns it as a human readable string.
	 * This runs in linear time.
	 */
	private static String findCyclicDependencies(
			Set<JavaScriptFile> unresolvedFiles) {

		Map<String, JavaScriptFile> providedNamespaceToFile = new HashMap<String, JavaScriptFile>();
		for (JavaScriptFile file : unresolvedFiles) {
			for (String providedNamespace : file.getProvidedNamespaces()) {
				providedNamespaceToFile.put(providedNamespace, file);
			}
		}

		List<JavaScriptFile> cycle = new ArrayList<JavaScriptFile>();
		JavaScriptFile currentNode = CollectionUtils.getAny(unresolvedFiles);
		while (!cycle.contains(currentNode)) {
			cycle.add(currentNode);

			// we know that all files we still have in unresolvedFiles have at
			// least one unresolved dependency. Thus we must always make
			// progress here.
			for (String required : currentNode.getRequiredNamespaces()) {
				if (providedNamespaceToFile.containsKey(required)) {
					currentNode = providedNamespaceToFile.get(required);
					break;
				}
			}
		}

		StringBuilder resultBuilder = new StringBuilder();
		for (int i = cycle.indexOf(currentNode); i < cycle.size(); ++i) {
			resultBuilder.append(cycle.get(i).getName());
			resultBuilder.append(" -> ");
		}
		resultBuilder.append(currentNode.getName());
		return resultBuilder.toString();
	}
}
