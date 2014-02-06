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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.pattern.PatternTransformationList;
import org.conqat.engine.commons.sorting.NodeIdComparator;
import org.conqat.engine.commons.traversal.TraversalUtils;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.logging.IConQATLogger;
import org.conqat.engine.resource.IContainer;
import org.conqat.engine.resource.IElement;
import org.conqat.engine.resource.IResource;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.text.ITextResource;

/**
 * Utility methods for resource hierarchy traversal.
 * 
 * @author $Author: pfaller $
 * @version $Rev: 45937 $
 * @ConQAT.Rating YELLOW Hash: 6F352311751EB56966316FBD62905139
 */
public class ResourceTraversalUtils {

	/** List all elements contained in the resource hierarchy. */
	@SuppressWarnings("unchecked")
	public static <E extends IElement> List<E> listElements(IResource input,
			Class<E> clazz) {
		List<E> elements = new ArrayList<E>();
		for (IResource resource : TraversalUtils.listAllDepthFirst(input)) {
			if (clazz.isInstance(resource)) {
				elements.add((E) resource);
			}
		}
		return elements;
	}

	/**
	 * Returns the single element in the resource hierarchy.
	 * 
	 * @throws ConQATException
	 *             If the scope does not contain exactly one item.
	 */
	public static <E extends IElement> E getSingleElement(IResource input,
			Class<E> clazz) throws ConQATException {
		List<E> elements = listElements(input, clazz);
		if (elements.size() != 1) {
			throw new ConQATException(
					"Expected exactly 1 element in the input scope, but found: "
							+ elements.size());
		}
		return elements.get(0);
	}

	/** Lists all elements contained in the resource hierarchy. */
	public static List<IElement> listElements(IResource root) {
		return listElements(root, IElement.class);
	}

	/**
	 * Returns the single element in the resource hierarchy.
	 * 
	 * @throws ConQATException
	 *             If the scope does not contain exactly one item.
	 */
	public static IElement getSingleElement(IResource input)
			throws ConQATException {
		return getSingleElement(input, IElement.class);
	}

	/** Lists all text elements contained in the resource hierarchy. */
	public static List<ITextElement> listTextElements(ITextResource root) {
		return listElements(root, ITextElement.class);
	}

	/**
	 * Returns the single text element in the resource hierarchy.
	 * 
	 * @throws ConQATException
	 *             If the scope does not contain exactly one item.
	 */
	public static ITextElement getSingleTextElement(ITextResource input)
			throws ConQATException {
		return getSingleElement(input, ITextElement.class);
	}

	/**
	 * List all non-ignored elements contained in the resource hierarchy.
	 * 
	 * @param input
	 *            Root of resource hierarchy
	 * @param ignoreKey
	 *            determined the key to be used to identify ignored elements.
	 * @return List of all elements
	 */
	public static <E extends IElement> List<E> listNonIgnoredElements(
			ITextResource input, String ignoreKey, Class<E> clazz) {
		List<E> elements = listElements(input, clazz);

		Collections.sort(elements, new NodeIdComparator());

		// create list without ignored or non-file elements
		List<E> filtered = new ArrayList<E>();
		for (E element : elements) {
			if (!NodeUtils.isIgnored(element, ignoreKey)) {
				filtered.add(element);
			}
		}

		return filtered;

	}

	/**
	 * Returns true, if the value <code>true</code> is stored under one of the
	 * ignore keys
	 */
	public static <Element extends IConQATNode> boolean isIgnored(
			Element element, Iterable<String> ignoreKeys) {
		for (String ignoreKey : ignoreKeys) {
			if (NodeUtils.isIgnored(element, ignoreKey)) {
				return true;
			}
		}
		return false;
	}

	/** Returns the root resource. */
	public static IResource returnRoot(IResource resource) {
		while (resource.getParent() != null) {
			resource = resource.getParent();
		}
		return resource;
	}

	/** Creates a mapping from elements' uniform paths to the elements. */
	public static <R extends IResource, E extends IElement> Map<String, E> createUniformPathToElementMap(
			IResource root, Class<E> clazz) {
		return createUniformPathToElementMapHelper(root, clazz, null, null);
	}

	/**
	 * Creates a map from uniform path to elements.
	 * 
	 * @param transformations
	 *            if not null, it is applied to the uniform paths before using
	 *            them as keys in the map.
	 * @param logger
	 *            if this is not null, duplicate elements with same uniform maps
	 *            (possibly after mapping) are reported to this logger.
	 */
	public static <R extends IResource, E extends IElement> TransformedUniformPathToElementMap<E> createTransformedUniformPathToElementMap(
			IResource root, Class<E> clazz,
			PatternTransformationList transformations, IConQATLogger logger) {

		return new TransformedUniformPathToElementMap<E>(root, clazz,
				transformations, logger);
	}

	/**
	 * Creates a map from uniform path to elements.
	 * 
	 * @param transformations
	 *            if not null, it is applied to the uniform paths before using
	 *            them as keys in the map.
	 * @param logger
	 *            if this is not null, duplicate elements with same uniform maps
	 *            (possibly after mapping) are reported to this logger.
	 */
	/* package */static <R extends IResource, E extends IElement> Map<String, E> createUniformPathToElementMapHelper(
			IResource root, Class<E> clazz,
			PatternTransformationList transformations, IConQATLogger logger) {

		Map<String, E> elements = new HashMap<String, E>();
		for (E element : ResourceTraversalUtils.listElements(root, clazz)) {
			String uniformPath = element.getUniformPath();
			if (transformations != null) {
				uniformPath = transformations.applyTransformation(uniformPath);
			}

			if (elements.put(uniformPath, element) != null && logger != null) {
				logger.error("Had multiple comparee elements mapped to "
						+ uniformPath + " (e.g. " + element.getUniformPath()
						+ ").");
			}
		}
		return elements;
	}

	/** Creates a mapping from elements' uniform paths to the elements' ids. */
	public static <R extends IResource, E extends IElement> Map<String, String> createUniformPathToIdMapping(
			R root, Class<E> clazz) {
		Map<String, String> elements = new HashMap<String, String>();
		for (E tokenElement : ResourceTraversalUtils.listElements(root, clazz)) {
			elements.put(tokenElement.getUniformPath(), tokenElement.getId());
		}
		return elements;
	}

	/**
	 * Creates a mapping from elements' locations to the elements. The locations
	 * are normalized using {@link #normalizeLocation(String)}.
	 */
	public static <R extends IResource, E extends IElement> Map<String, E> createLocationToElementMap(
			IResource root, Class<E> clazz) {
		Map<String, E> elements = new HashMap<String, E>();
		for (E element : ResourceTraversalUtils.listElements(root, clazz)) {
			elements.put(normalizeLocation(element.getLocation()), element);
		}

		return elements;
	}

	/** Provides basic normalization to provide more robust mapping. */
	public static String normalizeLocation(String location) {
		return UniformPathUtils.normalizeAllSeparators(location);
	}

	/** Counts the number of elements that are not containers. */
	public static int countNonContainers(IResource root) {
		int count = 0;
		for (IResource element : TraversalUtils.listAllDepthFirst(root)) {
			if (!(element instanceof IContainer)) {
				++count;
			}
		}
		return count;
	}

	/**
	 * Makes a copy of a map and converts the key to lower case
	 * 
	 * @throws ConQATException
	 *             if lower case conversion leads to conflicts.
	 */
	public static <T extends IElement> Map<String, T> toLowercase(
			Map<String, T> map) throws ConQATException {
		Map<String, T> result = new HashMap<String, T>();

		for (Map.Entry<String, T> entry : map.entrySet()) {
			String lowerCaseKey = entry.getKey().toLowerCase();
			if (result.containsKey(lowerCaseKey)) {
				throw new ConQATException(
						"Case normalization led to ambiguous mapping for key : "
								+ entry.getKey());
			}
			result.put(lowerCaseKey, entry.getValue());
		}

		return result;
	}
}