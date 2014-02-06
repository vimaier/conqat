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
package org.conqat.engine.html_presentation;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.html_presentation.formatters.IHTMLFormatter;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.UnmodifiableMap;
import org.conqat.lib.commons.collections.UnmodifiableSet;
import org.conqat.lib.commons.reflect.ReflectionUtils;

/**
 * A manager which handles multiple parameters and formatting issues for the
 * HTML presentation.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating RED Hash: 1632B3E73E428D1488F4CABC3503BF21
 */
public class HTMLPresentationManager {

	/** The set of paths to copy resources for the HTML presentation from. */
	private final Set<File> resourcePaths = new HashSet<File>();

	/** The formatters which are registered and used for lookup. */
	private final Map<Class<?>, IHTMLFormatter<?>> classFormatters = new HashMap<Class<?>, IHTMLFormatter<?>>();

	/** Cache for the formatter lookup. */
	private final Map<Class<?>, IHTMLFormatter<?>> classFormattersCache = new HashMap<Class<?>, IHTMLFormatter<?>>();

	/** Cache used to store unique abbreviations to be used in file names. */
	private final Map<String, String> abbreviationCache = new HashMap<String, String>();

	/** Int used to generate unique abbreviations. */
	private int abbreviationID = 1;

	/**
	 * Constructor. Use {@link BundleContext#getHtmlPresentationManager()}
	 * instead.
	 */
	/* package */HTMLPresentationManager() {
		// empty constructor to adjust visibility
	}

	/** Adds a path to copy resources for the HTML presentation from. */
	public void addResourcePath(File path) throws ConQATException {
		if (!path.isDirectory()) {
			// TODO (LH) I'd rather document that path has to be a directory and
			// use and unchecked exception here, e.g. IllegalArgumentException
			throw new ConQATException("Given resource path is not a directory.");
		}
		resourcePaths.add(path);
	}

	/** Add a formatter for the given class. */
	public <T> void addHtmlFormatter(Class<? extends T> clazz,
			IHTMLFormatter<T> formatter) {
		classFormatters.put(clazz, formatter);
		classFormattersCache.clear();
	}

	/** Returns a formatter which can be used for objects of the given class. */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> IHTMLFormatter<T> getFormatterForClass(Class<T> clazz) {
		IHTMLFormatter<?> formatter = classFormattersCache.get(clazz);
		if (formatter == null) {
			formatter = ReflectionUtils.performNearestClassLookup(clazz,
					classFormatters);
			classFormattersCache.put(clazz, formatter);
		}
		return (IHTMLFormatter) formatter;
	}

	/** Returns the registered resource paths. */
	public UnmodifiableSet<File> getResourcePaths() {
		return CollectionUtils.asUnmodifiable(resourcePaths);
	}

	/**
	 * Returns the abbreviation for the given long name. Usually the long name
	 * will be the fully qualified name of a unit.
	 */
	public String getAbbreviation(String longName) {
		String result = abbreviationCache.get(longName);
		if (result == null) {
			// Format is 6 digit hex code in brackets
			result = String.format("[%06X]", abbreviationID++);
			abbreviationCache.put(longName, result);
		}
		return result;
	}

	/**
	 * Returns the list of abbreviations, which can be used to dump lookup
	 * tables.
	 */
	public UnmodifiableMap<String, String> getAbbreviations() {
		return CollectionUtils.asUnmodifiable(abbreviationCache);
	}
}