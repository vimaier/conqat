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

import static org.conqat.lib.commons.html.EHTMLAttribute.HREF;
import static org.conqat.lib.commons.html.EHTMLElement.A;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.conqat.lib.commons.html.HTMLWriter;

/**
 * Class used for printing types which should be displayed as links to JavaDoc.
 * 
 * @author $Author: deissenb $
 * @version $Rev: 35910 $
 * @ConQAT.Rating GREEN Hash: 176F5ABDB7D875ED0E6CD61CCCC91E26
 */
public class JavaDocLinkResolver {

	/** Default value for linking to the Java API. */
	public static final String JAVA_API_BASE_DEFAULT = "http://download.oracle.com/javase/6/docs/api/";

	/** Mapping from prefixes to external JavaDoc locations. */
	private final Map<String, String> externalJavaDoc = new LinkedHashMap<String, String>();

	/** Lookup map from fully qualified class name to link used. */
	private final Map<String, String> linkCache = new HashMap<String, String>();

	/** Set of unresolved types already reported (to minimize the output). */
	private final Set<String> unresolved = new HashSet<String>();

	/** Suppress warnings flag. */
	private final boolean suppressWarnings;

	/**
	 * Creates a new JavaDocLinkResolver using {@value #JAVA_API_BASE_DEFAULT}
	 * for linking to Java API classes.
	 */
	public JavaDocLinkResolver() {
		this(JAVA_API_BASE_DEFAULT, false);
	}

	/**
	 * Creates a new JavaDocLinkResolver using {@value #JAVA_API_BASE_DEFAULT}
	 * for linking to Java API classes.
	 * 
	 * @param suppressWarnings
	 *            flag to suppress warnings
	 */
	public JavaDocLinkResolver(boolean suppressWarnings) {
		this(JAVA_API_BASE_DEFAULT, suppressWarnings);
	}

	/**
	 * Creates a new JavaDocLinkResolver.
	 * 
	 * @param javaApiBasePrefix
	 *            prefix for standard Java classes.
	 * @param suppressWarnings
	 *            flag to suppress warnings
	 */
	public JavaDocLinkResolver(String javaApiBasePrefix,
			boolean suppressWarnings) {
		this.suppressWarnings = suppressWarnings;
		// add default prefixes
		addExternalJavaDocLocation("java.", javaApiBasePrefix);
		addExternalJavaDocLocation("javax.", javaApiBasePrefix);
		addExternalJavaDocLocation("org.w3c.", javaApiBasePrefix);
		addExternalJavaDocLocation("org.xml.", javaApiBasePrefix);
	}

	/**
	 * Adds a JavaDoc directory to this resolver. This involves scanning the
	 * directory for documentation.
	 * 
	 * @param javaDocDir
	 *            the directory the JavaDoc resides in.
	 * @param relativePath
	 *            the relative path used as a prefix for all links pointing
	 *            there.
	 */
	public void addJavaDocLocation(File javaDocDir, String relativePath) {
		if (!javaDocDir.isDirectory()) {
			throw new IllegalArgumentException(javaDocDir + " is no directory!");
		}
		if (!relativePath.endsWith("/")) {
			relativePath += "/";
		}

		scanJavaDocDir(javaDocDir, relativePath, "");
	}

	/**
	 * Adds an external JavaDoc location (i.e. one we can not scan for classes).
	 * 
	 * @param prefix
	 *            the prefix of classes located there. This is usually the
	 *            package name followed by a dot to be sure (e.g. "java.", so
	 *            classes from javax.* are omitted).
	 * @param urlPrefix
	 *            the prefix of the URL. This is expanded by the class name
	 *            (with dots replaced by slashes) as usually with JavaDoc.
	 */
	/* package */void addExternalJavaDocLocation(String prefix, String urlPrefix) {
		if (!urlPrefix.endsWith("/")) {
			urlPrefix += "/";
		}
		// we only use forward slashes in links
		urlPrefix = urlPrefix.replace('\\', '/');

		externalJavaDoc.put(prefix, urlPrefix);
	}

	/**
	 * Performs the scanning of a JavaDoc directory.
	 * 
	 * @param dir
	 *            the directory to be scanned.
	 * @param relativePath
	 *            the relative path to this directory (including the trainling
	 *            slash).
	 * @param currentPackage
	 *            the package we are currently in (containing a trailing dot,
	 *            unless we are in the default package).
	 */
	private void scanJavaDocDir(File dir, String relativePath,
			String currentPackage) {
		for (File file : dir.listFiles()) {
			if (file.getName().contains("-")) {
				// not a valid java name (used for internal files)
				continue;
			}

			if (file.isDirectory()) {
				scanJavaDocDir(file, relativePath + file.getName() + "/",
						currentPackage + file.getName() + ".");
			} else if (file.isFile() && file.getName().endsWith(".html")) {
				String className = file.getName().substring(0,
						file.getName().length() - ".html".length());

				linkCache.put(currentPackage + className,
						relativePath + file.getName());

				// also put in dollar version for inner classes
				if (className.contains(".")) {
					linkCache.put(currentPackage + className.replace('.', '$'),
							relativePath + file.getName());
				}
			}
		}
	}

	/**
	 * Resolves the given link and writes the results into the given
	 * {@link HTMLWriter}. At least the given String will be added as text, but
	 * if JavaDoc for the class is found, a link to it is added.
	 * 
	 * @param type
	 *            the fully qualified class name to link to. It may have generic
	 *            parameters (these are automatically removed).
	 * @param pageWriter
	 *            the {@link HTMLWriter} to add the results to.
	 */
	public void resolveLink(String type, HTMLWriter pageWriter) {
		// for naming rules see e.g.
		// http://download.oracle.com/javase/1.5.0/docs/guide/jni/spec/types.html#wp276
		int arrayCount = 0;
		while (type.startsWith("[L") && type.endsWith(";")) {
			arrayCount += 1;
			type = type.substring(2, type.length() - 1);
		}

		String link = resolveLink(type);
		
		while (arrayCount > 0) {
			arrayCount -= 1;
			type = type + "[]";
		}
		
		if (link == null) {
			warnUnresolvedType(type);
			pageWriter.addText(type);
		} else {
			pageWriter.addClosedTextElement(A,
					type.replaceFirst("^[^<]+\\.", ""), HREF, link);
		}
	}

	/**
	 * Warn of an unresolved type. This uses {@link #unresolved} to avoid
	 * multiple warnings for the same link.
	 */
	private void warnUnresolvedType(String type) {
		if (!unresolved.contains(type)) {
			unresolved.add(type);
			if (!suppressWarnings) {
				System.err.println("Warning: Could not resolve JavaDoc for "
						+ type);
			}
		}
	}

	/**
	 * Returns the link (URL) for the given type, or null if the resolution
	 * failed.
	 */
	public String resolveLink(String type) {
		// get rid of generics
		type = type.replaceFirst("<.+>$", "");

		if (linkCache.containsKey(type)) {
			return linkCache.get(type);
		}

		for (Entry<String, String> entry : externalJavaDoc.entrySet()) {
			if (type.startsWith(entry.getKey())) {
				return entry.getValue()
						+ type.replace('.', '/').replace('$', '.') + ".html";
			}
		}
		return null;
	}
}