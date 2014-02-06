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

import static org.conqat.lib.commons.string.StringUtils.CR;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.html_presentation.javascript.JavaScriptFile.EType;
import org.conqat.engine.html_presentation.util.JsonUtils;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.ListMap;
import org.conqat.lib.commons.collections.UnmodifiableList;
import org.conqat.lib.commons.filesystem.ClassPathUtils;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Base class for JavaScript modules. These are the links between the Java and
 * the JavaScript world. Each logical collection of JavaScript files is
 * represented by a single module. This module provides access to the JavaScript
 * file content (which might also be generated dynamically).
 * <p>
 * The individual module classes should also bundle all methods that are used to
 * integrate Java code with the JavaScript code, i.e. code that creates strings
 * containing JavaScript calls. Using this mechanism we can also make the users
 * of individual scripts more explicit, as we get dependencies between a using
 * class and its descriptor on the Java level.
 * <p>
 * Concrete instances of this interface must provide a parameterless
 * constructor.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 43167 $
 * @ConQAT.Rating GREEN Hash: 7FDB561368731AB6732C3C4EEE07915B
 */
public abstract class JavaScriptModuleBase {

	/**
	 * Pattern that extracts the namespace calls "goog.provide" and
	 * "goog.require" from a string. This expects these calls to appear at the
	 * start of a line, which is also convention in closure.
	 */
	private static final Pattern CLOSURE_NAMESPACE_PATTERN = Pattern
			.compile(
					"^goog\\.(provide|require)\\s*\\(\\s*[\"\']([^\"\']+)[\"\']\\s*\\)",
					Pattern.MULTILINE);

	/**
	 * Closure requires the base.js to be included in any case. Thus, this is
	 * never explicitly mentioned in the require statements. To make our code
	 * work, we have to add it explicitly.
	 */
	private static final String CLOSURE_BASE_JS = "closure/goog/base.js";

	/** The extension used for Soy (closure templates). */
	private static final String SOY_EXTENSION = ".soy";

	/** The extension used for plain JavaScript. */
	private static final String JAVASCRIPT_EXTENSION = ".js";

	/** The cached files. */
	private final List<JavaScriptFile> files = new ArrayList<JavaScriptFile>();

	/** Stores for each file name the symbols to be exported. */
	private final ListMap<String, String> exportedSymbols = new ListMap<String, String>();

	/**
	 * The names of files for which the exports have been added/processed so
	 * far.
	 */
	private final Set<String> fileNamesWithExports = new HashSet<String>();

	/**
	 * Returns the {@link JavaScriptFile}s provided by this module.
	 */
	public final UnmodifiableList<JavaScriptFile> obtainFiles()
			throws ConQATException {
		if (files.isEmpty()) {
			createJavaScriptFiles();
			checkExportsConsumed();
		}

		return CollectionUtils.asUnmodifiable(files);
	}

	/**
	 * Factory/template method for creating {@link JavaScriptFile}. The names of
	 * the files created in this method must be globally unique.
	 */
	protected abstract void createJavaScriptFiles() throws ConQATException;

	/** Checks whether all registered exports have actually been consumed. */
	private void checkExportsConsumed() throws ConQATException {
		Set<String> expectedNames = new HashSet<String>(
				exportedSymbols.getKeys());
		expectedNames.removeAll(fileNamesWithExports);

		if (!expectedNames.isEmpty()) {
			throw new ConQATException("Had exported symbols but no code for: "
					+ StringUtils.concat(expectedNames, ", "));
		}
	}

	/** Adds a {@link JavaScriptFile}. */
	protected void addJavaScriptFile(JavaScriptFile file) {
		files.add(file);
	}

	/** Adds a {@link JavaScriptFile}. */
	protected void addJavaScriptFileFromText(EType type, String name,
			String content, List<String> providedNamespaces,
			List<String> requiredNamespaces) {
		addJavaScriptFile(new JavaScriptFile(type, name, content,
				providedNamespaces, requiredNamespaces));
	}

	/**
	 * Adds a JavaScript file that follows the conventions from Google's closure
	 * library. This method inspects the content of the file to automatically
	 * extract "goog.provide" and "goog.require" calls and add them to the
	 * provided and required namespaces. Additionally, exported symbols
	 * registered via {@link #registerExports(String, String...)} are appended
	 * to the file.
	 */
	protected void addClosureJavaScript(String name, EType type,
			String content, List<String> additionalProvidedNamespaces,
			List<String> additionalRequiredNamespaces) {
		addJavaScriptFile(createJavaScriptFileForClosure(type, name,
				appendExportsToContent(name, content),
				additionalProvidedNamespaces, additionalRequiredNamespaces));
	}

	/**
	 * Adds the exports registered via
	 * {@link #registerExports(String, String...)} to the content and returns
	 * this content. This may be only called once for each file!
	 */
	private String appendExportsToContent(String name, String content) {
		if (!exportedSymbols.containsCollection(name)) {
			return content;
		}

		CCSMAssert.isTrue(!fileNamesWithExports.contains(name), "Exports for "
				+ name + " have already been added!");
		fileNamesWithExports.add(name);

		StringBuilder newContent = new StringBuilder();
		newContent.append(content);
		newContent.append(CR);
		for (String export : CollectionUtils.sort(exportedSymbols
				.getCollection(name))) {
			newContent.append("goog.exportSymbol('" + export + "', " + export
					+ ");" + CR);
		}
		return newContent.toString();
	}

	/**
	 * Creates a JavaScript file that follows the conventions from Google's
	 * closure library. This method inspects the content of the file to
	 * automatically extract "goog.provide" and "goog.require" calls and add
	 * them to the provided and required namespaces.
	 */
	protected static JavaScriptFile createJavaScriptFileForClosure(EType type,
			String name, String content,
			List<String> additionalProvidedNamespaces,
			List<String> additionalRequiredNamespaces) {

		List<String> providedNamespaces = new ArrayList<String>(
				additionalProvidedNamespaces);
		List<String> requiredNamespaces = new ArrayList<String>(
				additionalRequiredNamespaces);

		Matcher matcher = CLOSURE_NAMESPACE_PATTERN.matcher(content);
		while (matcher.find()) {
			// can only be "require" or "provide" in our regex
			if (matcher.group(1).equals("require")) {
				requiredNamespaces.add(decorateClosureNamespace(matcher
						.group(2)));
			} else {
				providedNamespaces.add(decorateClosureNamespace(matcher
						.group(2)));
			}
		}

		// add default requirement to base.js (unless for base.js itself)
		if (name.endsWith(CLOSURE_BASE_JS)) {
			providedNamespaces.add(decorateClosureNamespace(CLOSURE_BASE_JS));
		} else {
			requiredNamespaces.add(decorateClosureNamespace(CLOSURE_BASE_JS));
		}

		return new JavaScriptFile(type, name, content, providedNamespaces,
				requiredNamespaces);
	}

	/**
	 * Decorates a namespace as found in a closure file (such as "goog.dom")
	 * with a prefix to ensure a unique namespace even when we include
	 * namespaces from non-closure code.
	 */
	private static String decorateClosureNamespace(String closureNamespace) {
		return "closure:" + closureNamespace;
	}

	/** Loads and returns a script from the classpath. */
	protected String loadScript(String fileName) throws ConQATException {
		InputStream stream = getClass().getResourceAsStream(fileName);
		if (stream == null) {
			throw new ConQATException("Could not locate script " + fileName
					+ " relative to " + getClass());
		}

		try {
			return FileSystemUtils.readStreamUTF8(stream);
		} catch (IOException e) {
			throw new ConQATException("Failed to read script " + fileName, e);
		} finally {
			FileSystemUtils.close(stream);
		}
	}

	/**
	 * Returns a list of JavaScript files that can be found in the current
	 * package.
	 */
	protected List<String> listJavaScriptAndSoyInCurrentPackage()
			throws ConQATException {
		List<String> result = new ArrayList<String>();
		try {
			for (String filename : FileSystemUtils
					.listFilesInSameLocationForURL(ClassPathUtils
							.obtainClassFileURL(getClass()))) {
				if (StringUtils.endsWithOneOf(filename, JAVASCRIPT_EXTENSION,
						SOY_EXTENSION)) {
					result.add(filename);
				}
			}
		} catch (IOException e) {
			throw new ConQATException(
					"Could not list JavaScript files for package "
							+ getClass().getPackage(), e);
		}
		return result;
	}

	/**
	 * Adds all JavaScript files found in the same package as the module
	 * descriptor as "custom" scripts. This means that they are not third-party
	 * and thus are both required and are expected to be closure compliant (i.e.
	 * use goog.provide/goog.require). The names are constructed from the script
	 * names via {@link #prefixFilename(String)}.
	 */
	protected void addCustomJavaScriptAndSoyFromCurrentPackage()
			throws ConQATException {
		addCustomJavaScriptAndSoyFromCurrentPackage(CollectionUtils
				.<String> emptyList());
	}

	/**
	 * Adds all JavaScript files found in the same package as the module
	 * descriptor as "custom" scripts. This means that they are not third-party
	 * and thus are both required and are expected to be closure compliant (i.e.
	 * use goog.provide/goog.require). The names are constructed from the script
	 * names via {@link #prefixFilename(String)}.
	 * 
	 * @param additionalRequiredNamespaces
	 *            additional set of namespaces that are added to all files
	 *            loaded. This can be used to add dependencies to third-party
	 *            libraries besides the closure library.
	 */
	protected void addCustomJavaScriptAndSoyFromCurrentPackage(
			List<String> additionalRequiredNamespaces) throws ConQATException {
		for (String filename : listJavaScriptAndSoyInCurrentPackage()) {
			if (filename.endsWith(SOY_EXTENSION)) {
				addClosureTemplate(prefixFilename(filename),
						loadScript(filename));
			} else {
				addClosureJavaScript(prefixFilename(filename),
						EType.CODE_REQUIRED, loadScript(filename),
						CollectionUtils.<String> emptyList(),
						additionalRequiredNamespaces);
			}
		}
	}

	/** Prefixes a local filename with the module class to create a unique name. */
	protected String prefixFilename(String filename) {
		return getClass().getSimpleName() + ":" + filename;
	}

	/** Adds a closure template (aka Soy). */
	protected void addClosureTemplate(String name, String content) {
		addJavaScriptFile(new JavaScriptFile(EType.CLOSURE_TEMPLATE, name,
				content, CollectionUtils.<String> emptyList(),
				CollectionUtils.<String> emptyList()));
	}

	/**
	 * This method loads and adds a simple JavaScriptLibrary from the class
	 * path. This can be used for simple single file libraries.
	 * 
	 * @param name
	 *            the name of the library as used internally in the Java code.
	 *            This name is also used as the provided namespace.
	 * @param classPathName
	 *            the name of the library as found on the class path.
	 * @param requiredNamespaces
	 *            namespaces this library depends on.
	 */
	protected void addSimpleLibraryFromClassPath(String name, EType type,
			String classPathName, String... requiredNamespaces)
			throws ConQATException {
		addJavaScriptFile(new JavaScriptFile(type, name,
				loadScript(classPathName), Collections.singletonList(name),
				Arrays.asList(requiredNamespaces)));
	}

	/**
	 * Creates the code for a JavaScript call inside a
	 * <code>&lt;a href=""&gt;</code> tag including "javascript:" and the
	 * closing semicolon.
	 * 
	 * @param arguments
	 *            the arguments, which are automatically converted to suitable
	 *            JavaScript types.
	 */
	protected static String javaScriptHrefCall(String functionName,
			Object... arguments) {
		return new StringBuilder("javascript:").append(
				javaScriptCall(functionName, arguments)).toString();
	}

	/**
	 * Creates the code for a JavaScript call including the closing semicolon.
	 * 
	 * @param arguments
	 *            the arguments, which are automatically converted to suitable
	 *            JavaScript types.
	 */
	protected static String javaScriptCall(String functionName,
			Object... arguments) {
		StringBuilder sb = new StringBuilder();
		sb.append(functionName + "(");
		boolean needsComma = false;
		for (Object argument : arguments) {
			if (needsComma) {
				sb.append(", ");
			}
			needsComma = true;

			appendJavaScriptArgument(sb, argument);
		}
		sb.append(");");
		return sb.toString();
	}

	/** Formats and appends a JavaScript argument. */
	private static void appendJavaScriptArgument(StringBuilder sb,
			Object argument) {
		if (argument == null) {
			sb.append("null");
		} else {
			sb.append(JsonUtils.serializeToJSON(argument));
		}
	}

	/**
	 * Registers symbols (e.g. function names) to be exported for a script file.
	 * This may not be called after the script file has been loaded!
	 * 
	 * This will prevent the Closure Compiler to change the name of the symbols.
	 * 
	 * @param scriptFileName
	 *            the simple filename (this is automatically prefixed via
	 *            {@link #prefixFilename(String)}).
	 */
	protected void registerExports(String scriptFileName,
			String... exportedSymbols) {
		scriptFileName = prefixFilename(scriptFileName);

		CCSMAssert.isTrue(!fileNamesWithExports.contains(scriptFileName),
				"May not register exports for " + scriptFileName
						+ " as it has already been loaded!");

		this.exportedSymbols.addAll(scriptFileName,
				Arrays.asList(exportedSymbols));
	}

	/** Adds generated JavaScript code. */
	protected void addGeneratedJavaScript(Class<?> sourceClass, String closureJS) {
		addClosureJavaScript("gen:" + sourceClass.getName(),
				EType.CODE_REQUIRED, closureJS,
				CollectionUtils.<String> emptyList(),
				CollectionUtils.<String> emptyList());
	}
}
