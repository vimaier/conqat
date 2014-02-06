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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.conqat.engine.commons.logging.ListStructuredLogMessage;
import org.conqat.engine.html_presentation.javascript.JavaScriptFile.EType;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.enums.EnumUtils;
import org.conqat.lib.commons.logging.ILogger;
import org.conqat.lib.commons.string.StringUtils;

import com.google.javascript.jscomp.BasicErrorManager;
import com.google.javascript.jscomp.CheckLevel;
import com.google.javascript.jscomp.ClosureCodingConvention;
import com.google.javascript.jscomp.CommandLineRunner;
import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.JSError;
import com.google.javascript.jscomp.JSSourceFile;
import com.google.javascript.jscomp.Result;

/**
 * This is a thin wrapper around the closure JavaScript compiler for usage
 * within ConQAT. This class is used mostly for managing and persisting the
 * compilation configuration and for translating this configuration to closure.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 45638 $
 * @ConQAT.Rating GREEN Hash: AFDBA414BEE51B4A648AAE9944495114
 */
public class ConQATJavaScriptCompiler {

	/** Pattern used to match the warning about missing requires. */
	private static final Pattern NOT_REQUIRED_WARNING_PATTERN = Pattern
			.compile("'(.+)' used but not goog.require'd");

	/**
	 * Allows to disable compilation completely and return a dummy script during
	 * testing. This is used for system testing, so problems in JavaScript
	 * compilation will not make unrelated tests fail (because some tests count
	 * error messages). The difference to settings the compilation mode to OFF
	 * is that using this flag will not even attempt to collect JavaScript and
	 * thus produce no valid JavaScript output (which, however, is ok during
	 * testing).
	 */
	public static boolean DISABLE_COMPILATION = false;

	/**
	 * Enumeration of compilation modes supported. We do not use the
	 * {@link CompilationLevel} enum, as we want to include a value for no
	 * compilation and also to have slightly shorter values for use in the
	 * config file.
	 */
	public static enum ECompilationMode {

		/** No compilation at all (simple concatenation). */
		OFF,

		/** Closure compiler with simple optimizations. */
		SIMPLE,

		/** Closure compiler with advanced optimizations. */
		ADVANCED
	}

	/** The compilation mode used. */
	private ECompilationMode compilationMode;

	/** Whether pretty printing is enabled. */
	private boolean prettyPrint;

	/** Whether to report compiler warnings in libraries. */
	private boolean reportLibraryWarnings = false;

	/** Constructor. */
	public ConQATJavaScriptCompiler(ECompilationMode compilationMode,
			boolean prettyPrint) {
		this.compilationMode = compilationMode;
		this.prettyPrint = prettyPrint;
	}

	/**
	 * Attempts to compile the given input files and returns the created script.
	 * Any problems are logged to the provided logger. As a fallback strategy,
	 * if the closure compiler fails, a simple concatenated script is returned,
	 * to ensure that at least some of the functionality provided by the scripts
	 * will be present.
	 * 
	 * @param inputFiles
	 *            the input files must already be sorted, i.e. be in a valid
	 *            order w.r.t. dependencies.
	 */
	public String compile(List<JavaScriptFile> inputFiles,
			List<JavaScriptFile> externFiles, ILogger logger) {
		if (DISABLE_COMPILATION) {
			return "/* compilation disabled */";
		}

		logger.info("Compilation mode: " + compilationMode);

		switch (compilationMode) {
		case OFF:
			return concatScripts(inputFiles);
		case SIMPLE:
			return closureCompile(CompilationLevel.SIMPLE_OPTIMIZATIONS,
					inputFiles, externFiles, logger);
		case ADVANCED:
			return closureCompile(CompilationLevel.ADVANCED_OPTIMIZATIONS,
					inputFiles, externFiles, logger);
		}
		throw new AssertionError("Unknown compilation mode: " + compilationMode);
	}

	/**
	 * Compiles the scripts from the given files into a single script using the
	 * Google closure compiler. Any problems encountered are logged as warnings
	 * or errors (depending on severity).
	 * <p>
	 * For better understanding what is going on in this method, please read
	 * "Closure: The Definitve Guide" by M. Bolin. Especially chapters 12 and 14
	 * explain how to use the compiler programmatically.
	 */
	private String closureCompile(CompilationLevel level,
			List<JavaScriptFile> inputFiles, List<JavaScriptFile> externFiles,
			ILogger logger) {

		CollectingErrorManager collectingErrorManager = new CollectingErrorManager(
				inputFiles, externFiles);
		Compiler compiler = new Compiler(collectingErrorManager);
		Compiler.setLoggingLevel(Level.OFF);

		List<JSSourceFile> defaultExterns = new ArrayList<JSSourceFile>();
		try {
			defaultExterns = CommandLineRunner.getDefaultExterns();
		} catch (IOException e) {
			logger.error(
					"Could not load closure's default externs! Probably this will prevent compilation.",
					e);
		}

		Result result = compiler.compile(
				createJSSourceFiles(externFiles, defaultExterns),
				createJSSourceFiles(inputFiles, new ArrayList<JSSourceFile>()),
				determineOptions(level));

		if (!collectingErrorManager.messages.isEmpty()) {
			logger.warn(new ListStructuredLogMessage(
					"JavaScript compilation produced "
							+ collectingErrorManager.messages.size()
							+ " errors and warnings",
					collectingErrorManager.messages, JavaScriptManager.LOG_TAG));
		}

		if (!result.success) {
			logger.error("Compilation of JavaScript code failed! Falling back to concatenated script.");
			return concatScripts(inputFiles);
		}

		return StringUtils.normalizeLineBreaks(compiler.toSource());
	}

	/**
	 * Creates and returns the options used for compiling in
	 * {@link #closureCompile(CompilationLevel, List, List, ILogger)}.
	 */
	private CompilerOptions determineOptions(CompilationLevel level) {
		CompilerOptions options = new CompilerOptions();
		level.setOptionsForCompilationLevel(options);

		options.prettyPrint = prettyPrint;

		options.setCodingConvention(new ClosureCodingConvention());

		options.checkEs5Strict = true;
		options.checkTypes = true;
		options.setCheckUnreachableCode(CheckLevel.WARNING);
		options.setCheckMissingReturn(CheckLevel.WARNING);

		options.checkDuplicateMessages = true;
		options.checkControlStructures = true;
		options.checkTypedPropertyCalls = true;
		options.checkSuspiciousCode = true;

		options.setReportMissingOverride(CheckLevel.WARNING);
		options.setCheckFunctions(CheckLevel.WARNING);
		options.setCheckGlobalNamesLevel(CheckLevel.WARNING);
		options.setCheckGlobalThisLevel(CheckLevel.WARNING);
		options.setCheckMethods(CheckLevel.WARNING);
		options.setCheckProvides(CheckLevel.WARNING);
		options.setCheckRequires(CheckLevel.WARNING);
		options.setCheckShadowVars(CheckLevel.WARNING);
		options.setAggressiveVarCheck(CheckLevel.WARNING);

		return options;
	}

	/** Creates an array of {@link JSSourceFile}s from {@link JavaScriptFile}s. */
	private JSSourceFile[] createJSSourceFiles(List<JavaScriptFile> files,
			List<JSSourceFile> baseList) {
		for (int i = 0; i < files.size(); ++i) {
			baseList.add(JSSourceFile.fromCode(files.get(i).getName(), files
					.get(i).getContent()));
		}
		return CollectionUtils.toArray(baseList, JSSourceFile.class);
	}

	/** Concatenates the contents of all scripts. */
	private String concatScripts(List<JavaScriptFile> sorted) {
		StringBuilder sb = new StringBuilder();
		for (JavaScriptFile descriptor : sorted) {
			sb.append("\n/* ConQAT Including " + descriptor.getName() + " */\n");
			sb.append(descriptor.getContent());
		}
		return StringUtils.normalizeLineBreaks(sb.toString());
	}

	/** Modifies the configuration based on a properties file. */
	public void configureFromProperties(Properties properties) {
		prettyPrint = Boolean.parseBoolean(properties.getProperty(
				"prettyPrint", "false"));

		reportLibraryWarnings = Boolean.parseBoolean(properties.getProperty(
				"libraryWarnings", "false"));

		String compilationModeString = properties.getProperty("compilation");
		if (compilationModeString != null) {
			ECompilationMode mode = EnumUtils.valueOfIgnoreCase(
					ECompilationMode.class, compilationModeString);
			if (mode != null) {
				compilationMode = mode;
			}
		}
	}

	/**
	 * Returns a string representation of the compiler settings. The main
	 * purposes are debugging and comparison of compiler settings, thus this
	 * does not have to be pretty.
	 */
	public String getSettingsAsString() {
		return "compilation mode: " + compilationMode + "\npretty print: "
				+ prettyPrint + "\nwarnings in libraries: "
				+ reportLibraryWarnings;
	}

	/** An error manager that collects all problems found. */
	private class CollectingErrorManager extends BasicErrorManager {

		/** The messages. */
		public final List<String> messages = new ArrayList<String>();

		/** Maps files/sources to the type. */
		private final Map<String, JavaScriptFile.EType> fileTypes = new HashMap<String, JavaScriptFile.EType>();

		/** Constructor. */
		public CollectingErrorManager(List<JavaScriptFile> inputFiles,
				List<JavaScriptFile> externFiles) {
			for (JavaScriptFile file : inputFiles) {
				fileTypes.put(file.getName(), file.getType());
			}
			for (JavaScriptFile file : externFiles) {
				fileTypes.put(file.getName(), file.getType());
			}
		}

		/** {@inheritDoc} */
		@Override
		public void println(CheckLevel level, JSError error) {
			EType type = fileTypes.get(error.sourceName);
			boolean isLoadedFromClosureCompilerExterns = error.sourceName
					.startsWith("externs.zip/");
			boolean isLibrary = (type == EType.CODE_LIBRARY || isLoadedFromClosureCompilerExterns);

			if (isLibrary && level != CheckLevel.ERROR
					&& !reportLibraryWarnings) {
				return;
			}

			if (isFalsePositive(error)) {
				return;
			}

			messages.add("JavaScript problem (level: " + level + ") in "
					+ error.sourceName + ", line " + error.lineNumber + ": "
					+ error.description);
		}

		/**
		 * Determines if the given error is a false positive. Such errors should
		 * not be printed to the console.
		 * 
		 * Currently, this function only filters compiler warnings about the
		 * ConQAT rating JSDoc tag, which it does not recognize, and warnings
		 * about missing requires of non-closure code.
		 * 
		 * @param error
		 * @return <code>true</code> iff the given error is a false positive and
		 *         should not be printed.
		 */
		private boolean isFalsePositive(JSError error) {
			if (error.description
					.equals("Parse error. illegal use of unknown JSDoc tag \"ConQAT\"; ignoring it")) {
				return true;
			}

			Matcher matcher = NOT_REQUIRED_WARNING_PATTERN
					.matcher(error.description);
			if (matcher.matches()) {
				String name = matcher.group(1);
				for (String prefix : JavaScriptManager.getInstance()
						.getNonClosureNamespacePrefixes()) {
					if (name.startsWith(prefix)) {
						return true;
					}
				}
			}

			return false;
		}

		/** {@inheritDoc} */
		@Override
		protected void printSummary() {
			// does nothing
		}
	}
}
