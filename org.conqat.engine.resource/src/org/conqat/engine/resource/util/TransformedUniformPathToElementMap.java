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
package org.conqat.engine.resource.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.conqat.engine.commons.pattern.PatternTransformationList;
import org.conqat.engine.core.logging.IConQATLogger;
import org.conqat.engine.resource.IElement;
import org.conqat.engine.resource.IResource;
import org.conqat.lib.commons.collections.Pair;
import org.conqat.lib.commons.string.StringUtils;

/**
 * This is a helper class for
 * {@link ResourceTraversalUtils#createTransformedUniformPathToElementMap(IResource, Class, PatternTransformationList, IConQATLogger)}
 * . It maps form the potentially moved location of an element to the element
 * itself. This is used to track movements of elements between different scopes
 * (e. g. head and baseline) and/or to match uniform paths with different
 * project names.
 * 
 * The look-up path under which the elements are addressed is the uniform path
 * of the element after the transformation.
 * 
 * This map also supports generic patterns for the look-up path, if the exact
 * name of the transformed location is not known. This is e.g. useful when a
 * package p is split in two packages p1 and p2, and for an element p/x it may
 * be looked-up either under p1/x or p2/x.
 * 
 * @author $Author: pfaller $
 * @version $Rev: 45937 $
 * @ConQAT.Rating YELLOW Hash: 8E09634BB41D04E8851D08EA51AF72BF
 */
public class TransformedUniformPathToElementMap<E extends IElement> {

	/**
	 * Prefix which is used to identify that a regular expression should be
	 * applied to the look-up path
	 */
	private static final String PATTERN_PATH_PREFIX = "?REGEX?";

	/** Maps compare elements to main elements */
	private final Map<String, E> map;

	/**
	 * Patterns which are applied to the look-up path to identify the element.
	 * The first part is the String which is used as key in {@link #map}, the
	 * second is the pattern to apply.
	 */
	private final Set<Pair<String, Pattern>> lookupPathPatterns;

	/**
	 * Creates a new {@link TransformedUniformPathToElementMap}.
	 * 
	 * @param root
	 *            the root of the elements which should be mapped
	 * @param clazz
	 *            the class of the elements
	 * @param transformations
	 *            the transformations which should be applied. These are
	 *            replacements which transform the uniform path of the elements
	 *            under root to the look-up path. Instead of the exact look-up
	 *            path the transformation may also result in a pattern of
	 *            look-up path. If the look-up path should be used as pattern,
	 *            it must be prefixed with {@value #PATTERN_PATH_PREFIX} .
	 * @param logger
	 *            {@link IConQATLogger} to log messages
	 * 
	 */
	/* package */TransformedUniformPathToElementMap(IResource root,
			Class<E> clazz, PatternTransformationList transformations,
			IConQATLogger logger) {
		map = ResourceTraversalUtils.createUniformPathToElementMapHelper(root,
				clazz, transformations, logger);

		lookupPathPatterns = new HashSet<Pair<String, Pattern>>();
		for (String key : map.keySet()) {
			if (key.startsWith(PATTERN_PATH_PREFIX)) {
				Pattern pattern = Pattern.compile(StringUtils.stripPrefix(
						PATTERN_PATH_PREFIX, key));
				lookupPathPatterns.add(new Pair<String, Pattern>(key, pattern));
			}
		}
	}

	/**
	 * Gets the element to the given look-up path. If the given path does not
	 * directly point to an element it is checked if a look-up path pattern
	 * applies to the look-up path and the element where the pattern points to
	 * is returned. If no element could be identified for the given path,
	 * <code>null</code> is returned.
	 */
	public E getElement(String path) {
		E compareeElement = map.get(path);
		if (compareeElement != null) {
			return compareeElement;
		}
		return map.get(applyPatternToLookupPath(path));
	}

	/**
	 * Tries to apply any pattern in {@link #lookupPathPatterns} to the given
	 * look-up path and returns the pattern string which points the the element.
	 * Returns <code>null</code> if lookupPaht does not match any pattern in
	 * {@link #lookupPathPatterns}.
	 */
	private String applyPatternToLookupPath(String lookupPath) {
		for (Pair<String, Pattern> pattern : lookupPathPatterns) {
			Matcher matcher = pattern.getSecond().matcher(lookupPath);
			if (matcher.matches()) {
				return pattern.getFirst();
			}
		}
		return null;
	}

	/**
	 * Removes the element to which the given look-up path points to. Look-up
	 * pattern are applied as in {@link #getElement(String)}
	 */
	public E removeElement(String lookupPath) {
		E compareeElement = map.remove(lookupPath);
		if (compareeElement != null) {
			return compareeElement;
		}
		return map.remove(applyPatternToLookupPath(lookupPath));
	}

	/**
	 * @return the elements in the map.
	 */
	public Collection<E> elements() {
		return map.values();
	}

	/**
	 * @return <code>true</code> if the given look-up path points to an element
	 *         in the map (either directly or by pattern), otherwise
	 *         <code>false</code>
	 */
	public boolean containsPath(String lookupPath) {
		if (map.containsKey(lookupPath)) {
			return true;
		}
		return map.containsKey(applyPatternToLookupPath(lookupPath));
	}

	/**
	 * Gets an element from the given map where look-up pattern are applied.
	 * This method works similar to {@link #getElement(String)} but on a given
	 * map.
	 * 
	 * @return the element which is stored under the key given by lookupPath or
	 *         - if the lookupPath is a pattern - the element where the key
	 *         matches this pattern. If no element can be found
	 *         <code>null</code> is returned.
	 */
	public static <E extends IElement> E getElementFromMap(Map<String, E> map,
			String lookupPath) {
		E element = map.get(lookupPath);
		if (element != null) {
			return element;
		}

		if (lookupPath.startsWith(PATTERN_PATH_PREFIX)) {
			Pattern pattern = Pattern.compile(StringUtils.stripPrefix(
					PATTERN_PATH_PREFIX, lookupPath));
			for (String mainPath : map.keySet()) {
				if (pattern.matcher(mainPath).matches()) {
					return map.get(mainPath);
				}
			}
		}

		return null;
	}

	/**
	 * @return The set of look-up paths and look-up path patterns which point
	 *         to elements.
	 */
	public Collection<String> lookupPaths() {
		return map.keySet();
	}
}
