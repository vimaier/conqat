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
package org.conqat.engine.resource.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.conqat.engine.resource.IElement;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Utility methods for dealing with uniform paths.
 * 
 * @author $Author: kanis $
 * @version $Rev: 45993 $
 * @ConQAT.Rating YELLOW Hash: C1EEAA55BC7EC57854E23BA4F9824C81
 */
public class UniformPathUtils {

	/** Matches windows drive letters prefixes, e.g. "C:\" */
	private static final String DRIVE_LETTER_PATTERN = "[A-Za-z]:[\\\\/](.*)";

	/** The character used as path separator in uniform paths. */
	public static final char SEPARATOR_CHAR = '/';

	/** String representation of {@link #SEPARATOR_CHAR}. */
	public static final String SEPARATOR = String.valueOf(SEPARATOR_CHAR);

	/** Pattern used for splitting along the separator. */
	private static final Pattern SPLIT_PATTERN = Pattern.compile(Pattern
			.quote(SEPARATOR));

	/**
	 * Extracts the project part of a uniform path, which is everything up to
	 * the first {@link #SEPARATOR_CHAR}.
	 */
	public static String extractProject(String uniformPath) {
		return StringUtils.getFirstParts(uniformPath, 1, SEPARATOR_CHAR);
	}

	/**
	 * Returns the path without the project, i.e. removes everything up to the
	 * first {@link #SEPARATOR_CHAR}.
	 */
	public static String stripProject(String uniformPath) {
		int pos = uniformPath.indexOf(SEPARATOR_CHAR);
		if (pos >= 0) {
			return uniformPath.substring(pos + 1);
		}
		return uniformPath;
	}

	/**
	 * Returns the element name for a uniform path, which is the everything
	 * starting from the last {@link #SEPARATOR_CHAR}.
	 */
	public static String getElementName(String uniformPath) {
		return StringUtils.getLastPart(uniformPath, SEPARATOR_CHAR);
	}

	/**
	 * Returns the parent path for a path which is everything up to the last
	 * {@link #SEPARATOR_CHAR}. If no separator is found, the empty string is
	 * returned.
	 */
	public static String getParentPath(String uniformPath) {
		// we can not use StringUtils.removeLastPart(), as the behavior for a
		// string without separator is different here
		int idx = uniformPath.lastIndexOf(SEPARATOR_CHAR);
		if (idx == -1) {
			return StringUtils.EMPTY_STRING;
		}
		return uniformPath.substring(0, idx);
	}

	/** Removes the first <code>count</code> segments from the given path. */
	public static String removeFirstSegments(String uniformPath, int count) {
		String[] segments = splitPath(uniformPath);
		return concatenate(Arrays.copyOfRange(segments, count, segments.length));
	}

	/** Removes the last <code>count</code> segments from the given path. */
	public static String removeLastSegments(String uniformPath, int count) {
		String[] segments = splitPath(uniformPath);
		return concatenate(Arrays.copyOfRange(segments, 0, segments.length
				- count));
	}

	/** Returns segments forming the given path. */
	public static String[] splitPath(String uniformPath) {
		return SPLIT_PATTERN.split(uniformPath);
	}

	/**
	 * Returns the extension of the uniform path.
	 * 
	 * @return File extension, i.e. "java" for "FileSystemUtils.java", or
	 *         <code>null</code>, if the path has no extension (i.e. if a path
	 *         contains no '.'), returns the empty string if the '.' is the
	 *         path's last character.
	 */
	public static String getExtension(String uniformPath) {
		String name = getElementName(uniformPath);
		int posLastDot = name.lastIndexOf('.');
		if (posLastDot < 0) {
			return null;
		}
		return name.substring(posLastDot + 1);
	}

	/**
	 * Replaces forward and backward slashes, not only system-specific
	 * separators, with a forward slash. We do this on purpose, since paths that
	 * e.g. are read from files do not necessarily contain the separators
	 * contained in File.separator.
	 */
	public static String normalizeAllSeparators(String path) {
		return path.replaceAll("[/\\\\]+", "/");
	}

	/**
	 * Creates a clean path by resolving duplicate slashes, single and double
	 * dots. This is the equivalent to path canonization on uniform paths.
	 */
	public static String cleanPath(String path) {
		String[] parts = splitPath(path);
		for (int i = 0; i < parts.length; ++i) {
			// do not use StringUtils.isEmpty(), as we do not want trim
			// semantics!
			if (StringUtils.EMPTY_STRING.equals(parts[i])
					|| ".".equals(parts[i])) {
				parts[i] = null;
			} else if ("..".equals(parts[i])) {
				// cancel last non-null (if any)
				int j = i - 1;
				for (; j >= 0; --j) {
					if ("..".equals(parts[j])) {
						// another '..' acts as boundary
						break;
					}
					if (parts[j] != null) {
						// cancel both parts of the path.
						parts[j] = null;
						parts[i] = null;
						break;
					}
				}
			}
		}

		return joinPath(parts);
	}

	/** Joins the given array as a path, but ignoring null entries. */
	private static String joinPath(String[] parts) {
		StringBuilder sb = new StringBuilder();
		for (String part : parts) {
			if (part != null) {
				if (sb.length() > 0) {
					sb.append(SEPARATOR_CHAR);
				}
				sb.append(part);
			}
		}

		return sb.toString();
	}

	/**
	 * For a uniform path denoting a file and a relative path, constructs the
	 * uniform path for the relatively addressed element.
	 */
	public static String resolveRelativePath(String basePath, String relative) {
		// obtain "directory" from path denoting file
		String directory = getParentPath(basePath);
		if (!directory.isEmpty()) {
			directory += SEPARATOR;
		}
		return cleanPath(directory + relative);
	}

	/** Return sorted list of uniform paths */
	public static List<String> uniformPathList(
			Collection<? extends IElement> elements) {
		List<String> uniformPaths = new ArrayList<String>();
		for (IElement element : elements) {
			uniformPaths.add(element.getUniformPath());
		}
		Collections.sort(uniformPaths);
		return uniformPaths;
	}

	/**
	 * Returns the concatenated path of all given parts. Empty or null strings
	 * are ignored.
	 */
	public static String concatenate(String... parts) {
		List<String> list = new ArrayList<String>(parts.length);
		for (String part : parts) {
			if (!StringUtils.isEmpty(part)) {
				list.add(part);
			}
		}
		return StringUtils.concat(list, SEPARATOR);
	}

	/**
	 * Creates a uniform path by prepending the project to the given path. If
	 * the project is null, it is ignored.
	 */
	public static String prependProject(String projectName, String path) {
		return cleanPath(concatenate(projectName, path));
	}

	/**
	 * Remove drive letters or unix root slash from path. Also normalizes the
	 * path.
	 */
	public static String createSystemIndependentPath(String path) {
		Pattern p = Pattern.compile(DRIVE_LETTER_PATTERN);
		Matcher m1 = p.matcher(path);
		if (m1.matches()) {
			// Remove drive letter
			path = m1.group(1);
		}

		// Remove unix root slash
		path = StringUtils.stripPrefix("/", path);

		return normalizeAllSeparators(path);
	}

}