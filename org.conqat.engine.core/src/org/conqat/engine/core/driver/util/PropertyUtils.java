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
package org.conqat.engine.core.driver.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.conqat.engine.core.driver.error.BlockFileException;
import org.conqat.engine.core.driver.error.EDriverExceptionType;
import org.conqat.engine.core.driver.error.EnvironmentException;
import org.conqat.engine.core.driver.error.ErrorLocation;
import org.conqat.lib.commons.collections.ListMap;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Utility class for reading property files. These methods are collected here
 * and made public so that cq.edit can use them, too. ConQAT property files are
 * similar to Java properties files but allow multiple entries for the same key.
 * They do not support escape characters.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 152BC8B5DFDD4F99552CAB1F8CE962E0
 */
public class PropertyUtils {

	/** The prefix used to identify comments. */
	public static final String COMMENT_PREFIX = "#";

	/** Parses ConQAT property file. */
	public static ListMap<String, String> parseCqProperties(File propertiesFile)
			throws IOException {
		return parseCqProperties(StringUtils.splitLinesAsList(FileSystemUtils
				.readFile(propertiesFile)));
	}

	/**
	 * Parse list of strings where each string describes a line in a ConQAT
	 * property file.
	 */
	public static ListMap<String, String> parseCqProperties(List<String> lines)
			throws IOException {
		ListMap<String, String> properties = new ListMap<String, String>();
		for (String line : lines) {
			line = line.trim();
			if (StringUtils.isEmpty(line) || line.startsWith(COMMENT_PREFIX)) {
				continue;
			}
			String[] parts = line.split("=", 2);
			if (parts.length < 2) {
				throw new IOException(
						"Property must be of format <name>=<value>: " + line);
			}
			properties.add(parts[0].trim(), parts[1].trim());
		}
		return properties;
	}

	/**
	 * Splits the properties map into a two level map separated by parameter and
	 * attribute names.
	 */
	public static <T> Map<String, ListMap<String, T>> splitProperties(
			ListMap<String, T> properties) throws BlockFileException {
		Map<String, ListMap<String, T>> preprocessed = new HashMap<String, ListMap<String, T>>();
		for (String key : properties.getKeys()) {
			String[] parts = key.split(Pattern.quote("."), 2);
			if (parts.length < 2) {
				if (StringUtils.isEmpty(key)) {
					throw new BlockFileException(
							EDriverExceptionType.INVALID_PROPERTY_NAME,
							"Empty property name encountered.",
							ErrorLocation.UNKNOWN);
				}
				throw new BlockFileException(
						EDriverExceptionType.INVALID_PROPERTY_NAME,
						"Invalid property name: " + key, ErrorLocation.UNKNOWN);
			}
			ListMap<String, T> attributeMap = preprocessed.get(parts[0]);
			if (attributeMap == null) {
				attributeMap = new ListMap<String, T>();
				preprocessed.put(parts[0], attributeMap);
			}
			attributeMap.addAll(parts[1], properties.getCollection(key));
		}
		return preprocessed;
	}

	/** Extracts the lengths of all attributes in the param/attribute list. */
	public static int extractListLengths(String param,
			ListMap<String, ?> attributeMap) throws EnvironmentException {
		int length = -1;
		for (String attr : attributeMap.getKeys()) {
			int localLength = attributeMap.getCollection(attr).size();
			if (length < 0) {
				length = localLength;
			} else if (length != localLength) {
				throw new EnvironmentException(
						EDriverExceptionType.MISSING_ATTRIBUTE,
						"Inconsistent attribute counts for parameter " + param,
						ErrorLocation.UNKNOWN);
			}
		}
		return length;
	}

	/**
	 * Returns whether the given line is a comment (i.e. starts with
	 * {@link #COMMENT_PREFIX}).
	 */
	public static boolean isCommentLine(String line) {
		return line.startsWith(COMMENT_PREFIX);
	}
}