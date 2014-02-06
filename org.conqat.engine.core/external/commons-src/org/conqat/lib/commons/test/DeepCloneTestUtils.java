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
package org.conqat.lib.commons.test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;

import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.collections.AllEqualComparator;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.IIdProvider;
import org.conqat.lib.commons.collections.IdComparator;
import org.conqat.lib.commons.collections.IdentityHashSet;
import org.conqat.lib.commons.reflect.MethodNameComparator;
import org.conqat.lib.commons.string.StringUtils;

/**
 * This class provides various utility methods used to test deep cloning
 * implementations.
 * 
 * @author deissenb
 * @author $Author: deissenb $
 * @version $Rev: 44772 $
 * @ConQAT.Rating GREEN Hash: F0540D78DB9651FE510E37132794F517
 */
public class DeepCloneTestUtils {

	/** Name of the deep clone method. */
	private static final String DEEP_CLONE_METHOD_NAME = "deepClone";

	/** Name of the clone method. */
	private static final String CLONE_METHOD_NAME = "clone";

	/**
	 * This method is used to test deep cloning implementations. Provided with
	 * two object, the original and the clone, it automatically traverses the
	 * networks attached to both objects and establishes a mapping between the
	 * objects of both networks.
	 * <p>
	 * To achieve this, the method uses reflection to determine the methods that
	 * define the object networks, typically examples are
	 * <code>getChildren()</code> or <code>getParent()</code>. To limit the
	 * selection of methods, a list of package prefixes may specified. Only
	 * methods that do return a type matched by one of the prefixes are taken
	 * into account. Furthermore, methods annotated with
	 * {@link ADeepCloneTestExclude} are not considered. Additionally all
	 * methods that return an implementation of {@link Collection} are
	 * considered. However, we currently do not consider nested collections like
	 * <code>Set<Set<K></code>.
	 * <p>
	 * To establish the mapping between the networks a comparator is needed to
	 * order members of object in both networks in the same way. This comparator
	 * must not be capable of comparing all possible types but must support
	 * ordering possible member type combinations of the object network under
	 * investigation. Usually, the object already provide some means of
	 * identification via IDs or full qualified names. These can be used to
	 * implement the comparator.
	 * 
	 * @param orig
	 *            point of entry for the original network
	 * @param clone
	 *            point of entry for the clone network
	 * @param comparator
	 *            the comparator is needed to compare the two networks
	 * @param packagePrefixes
	 *            list of package prefixes to take into account
	 */
	public static IdentityHashMap<Object, Object> buildCloneMap(Object orig,
			Object clone, Comparator<Object> comparator,
			String... packagePrefixes) {
		IdentityHashMap<Object, Object> map = new IdentityHashMap<Object, Object>();
		buildCloneMap(orig, clone, map, comparator, packagePrefixes);
		return map;
	}

	/**
	 * This works analogous to
	 * {@link #getAllReferencedObjects(Object, String...)} but allows to limit
	 * the results to a certain type.
	 */
	public static <T> IdentityHashSet<T> getAllReferencedObjects(Object root,
			Class<T> type, String... packagePrefixes) {
		IdentityHashSet<T> result = new IdentityHashSet<T>();
		for (Object object : getAllReferencedObjects(root, packagePrefixes)) {
			if (type.isAssignableFrom(object.getClass())) {
				@SuppressWarnings("unchecked")
				T object2 = (T) object;
				result.add(object2);
			}
		}
		return result;
	}

	/**
	 * Get all objects an object references. To to find the objects, this method
	 * uses reflection to determine the methods that define the object networks,
	 * typically examples are <code>getChildren()</code> or
	 * <code>getParent()</code>. To limit the selection of methods, a list of
	 * package prefixes must be specified. Only methods that do return a type
	 * matched by one of the prefixes are taken into account. Additionally all
	 * methods that return an implementation of {@link Collection} are
	 * considered.
	 * 
	 * @param root
	 *            root of the object network
	 * @param packagePrefixes
	 *            list of package prefixes to take into account
	 */
	public static IdentityHashSet<Object> getAllReferencedObjects(Object root,
			String... packagePrefixes) {
		IdentityHashSet<Object> result = new IdentityHashSet<Object>();
		buildReferenceSet(root, result, packagePrefixes);
		return result;
	}

	/**
	 * This method uses
	 * {@link #buildCloneMap(Object, Object, Comparator, String...)} to build a
	 * map between the original object network and clone network. Based on this
	 * map it performs the following checks:
	 * <ul>
	 * <li>Checks if objects are not same.</li>
	 * <li>Checks if objects are of same type.</li>
	 * <li>Checks if object and clone network are disjoint.</li>
	 * </ul>
	 * 
	 * @param orig
	 *            point of entry for the original network
	 * @param clone
	 *            point of entry for the clone network
	 * @param idProvider
	 *            an id provider that generates an id for all objects in the
	 *            networks. This is necessary to establish comparable orderings.
	 * @param packagePrefixes
	 *            list of package prefixes to take into account
	 * @return the map to allow further tests.
	 */
	public static <I extends Comparable<I>> IdentityHashMap<Object, Object> testDeepCloning(
			Object orig, Object clone, IIdProvider<I, Object> idProvider,
			String... packagePrefixes) {

		IdentityHashMap<Object, Object> map = buildCloneMap(orig, clone,
				new IdComparator<I, Object>(idProvider), packagePrefixes);

		for (Object origObject : map.keySet()) {
			Object cloneObject = map.get(origObject);

			CCSMAssert.isTrue(orig.getClass().equals(clone.getClass()),
					"Objects " + origObject + " and " + cloneObject
							+ " have different types.");

			// no need to check this for enums.
			if (origObject.getClass().isEnum()) {
				continue;
			}

			CCSMAssert.isFalse(origObject == cloneObject, "Objects "
					+ origObject + " and " + cloneObject + " are same.");

			CCSMAssert.isFalse(map.values().contains(origObject),
					"Clone network contains original object: " + origObject);
			CCSMAssert.isFalse(map.keySet().contains(cloneObject),
					"Orig network contains clone object: " + origObject);

		}

		return map;
	}

	/**
	 * Recursively build clone map. See
	 * {@link #buildCloneMap(Object, Object, Comparator, String...)} for
	 * details.
	 */
	private static void buildCloneMap(Object orig, Object clone,
			IdentityHashMap<Object, Object> map, Comparator<Object> comparator,
			String... packagePrefixes) {

		if (!orig.getClass().equals(clone.getClass())) {
			throw new RuntimeException("Objects " + orig + " and " + clone
					+ " are of different tpye [orig: "
					+ orig.getClass().getName() + "][clone:"
					+ orig.getClass().getName() + "]");
		}

		map.put(orig, clone);

		// get all objects referenced by the original
		ArrayList<Object> origRefObjects = getReferencedObjects(orig,
				comparator, packagePrefixes);

		// get all objects referenced by the clone
		ArrayList<Object> cloneRefObjects = getReferencedObjects(clone,
				comparator, packagePrefixes);

		if (origRefObjects.size() != cloneRefObjects.size()) {
			throw new RuntimeException("Objects " + orig + " and " + clone
					+ " have unequal numbers of referenced objects [orig: "
					+ origRefObjects + "][clone:" + cloneRefObjects + "]");
		}

		// traverse recursively
		for (int i = 0; i < origRefObjects.size(); i++) {
			Object key = origRefObjects.get(i);
			Object value = cloneRefObjects.get(i);

			// do not traverse objects already visited, but ensure that we have
			// an unambiguous mapping
			if (map.containsKey(key)) {
				if (!(map.get(key) == value)) {
					throw new RuntimeException("Object " + key
							+ " appears to be cloned to " + map.get(key)
							+ " and to " + value);
				}
			} else if (key != null) {
				buildCloneMap(key, value, map, comparator, packagePrefixes);
			}
		}
	}

	/** Recursively build set of all referenced objects. */
	private static void buildReferenceSet(Object object,
			IdentityHashSet<Object> set, String[] packagePrefixes) {

		set.add(object);
		for (Object item : getReferencedObjects(object,
				AllEqualComparator.OBJECT_INSTANCE, packagePrefixes)) {
			if (item != null && !set.contains(item)) {
				buildReferenceSet(item, set, packagePrefixes);
			}
		}
	}

	/**
	 * Get all objects referenced by an object through methods that do return
	 * arrays.
	 */
	private static ArrayList<Object> getArrayObjects(Object object,
			Comparator<Object> comparator, String... packagePrefixes) {

		ArrayList<Object> result = new ArrayList<Object>();

		for (Method method : object.getClass().getMethods()) {
			if (method.getParameterTypes().length == 0 && !isExcluded(method)
					&& hasArrayReturnType(method, packagePrefixes)) {
				Object returnValue = invoke(object, method);
				if (returnValue != null) {
					Object[] array = (Object[]) returnValue;
					List<Object> list = Arrays.asList(array);

					Collections.sort(list, comparator);
					result.addAll(list);
				}
			}
		}

		return result;
	}

	/**
	 * Get all objects referenced by an object through methods that do return
	 * collections.
	 */
	private static ArrayList<Object> getCollectionObjects(Object object,
			Comparator<Object> comparator, String... packagePrefixes) {

		ArrayList<Object> result = new ArrayList<Object>();

		for (Method method : object.getClass().getMethods()) {
			if (method.getParameterTypes().length == 0 && !isExcluded(method)
					&& hasCollectionReturnType(method, packagePrefixes)) {
				Object returnValue = invoke(object, method);

				if (returnValue != null) {
					Collection<?> collection = (Collection<?>) returnValue;
					List<?> list = CollectionUtils.sort(collection, comparator);
					result.addAll(list);
				}
			}

		}

		return result;
	}

	/**
	 * Get list of methods that (1) do not have parameters, (2) whose return
	 * type starts with one of the given prefixes and whose names is not
	 * <code>deepClone()</code>. The methods are ordered by name.
	 */
	private static ArrayList<Method> getMethods(Object object,
			String[] packagePrefixes) {
		ArrayList<Method> methods = new ArrayList<Method>();
		for (Method method : object.getClass().getMethods()) {
			if (method.getName().equals(CLONE_METHOD_NAME)) {
				continue;
			}
			if (method.getName().equals(DEEP_CLONE_METHOD_NAME)) {
				continue;
			}
			if (method.getParameterTypes().length > 0) {
				continue;
			}
			Class<?> returnType = method.getReturnType();
			if (StringUtils.startsWithOneOf(returnType.getName(),
					packagePrefixes)) {
				methods.add(method);
			}
		}

		Collections.sort(methods, MethodNameComparator.INSTANCE);

		return methods;
	}

	/**
	 * Get all objects referenced by an object through methods that do
	 * <em>not</em> return collections.
	 */
	private static ArrayList<Object> getNonCollectionObjects(Object object,
			String... packagePrefixes) {

		ArrayList<Object> objects = new ArrayList<Object>();

		for (Method method : getMethods(object, packagePrefixes)) {
			if (!isExcluded(method)) {
				objects.add(invoke(object, method));
			}
		}
		return objects;
	}

	/**
	 * Returns true if the given method should be excluded from deep clone
	 * testing. This checks for the presence of the
	 * {@link ADeepCloneTestExclude} annotation.
	 */
	private static boolean isExcluded(Method method) {
		return method.isAnnotationPresent(ADeepCloneTestExclude.class);
	}

	/**
	 * Get all objects an object references in an order defined by the
	 * comparator.
	 */
	private static ArrayList<Object> getReferencedObjects(Object object,
			Comparator<Object> comparator, String... packagePrefixes) {

		ArrayList<Object> result = new ArrayList<Object>();

		ArrayList<Object> nonCollectionObjects = getNonCollectionObjects(
				object, packagePrefixes);
		result.addAll(nonCollectionObjects);

		ArrayList<Object> collectionObjects = getCollectionObjects(object,
				comparator, packagePrefixes);
		result.addAll(collectionObjects);

		ArrayList<Object> arrayObjects = getArrayObjects(object, comparator,
				packagePrefixes);
		result.addAll(arrayObjects);

		return result;
	}

	/**
	 * Checks if a method returns an array with type that starts with one of the
	 * provided prefixes.
	 */
	private static boolean hasArrayReturnType(Method method,
			String... packagePrefixes) {
		Class<?> returnType = method.getReturnType();
		if (!returnType.isArray()) {
			return false;
		}
		Class<?> actualType = returnType.getComponentType();
		return StringUtils.startsWithOneOf(actualType.getName(),
				packagePrefixes);
	}

	/**
	 * Checks if a method returns an collection whose generic type that starts
	 * with one of the provided prefixes.
	 */
	private static boolean hasCollectionReturnType(Method method,
			String... packagePrefixes) {
		Class<?> returnType = method.getReturnType();

		if (!Collection.class.isAssignableFrom(returnType)) {
			return false;
		}

		Type genericReturnType = method.getGenericReturnType();
		// Raw type
		if (returnType == genericReturnType) {
			return false;
		}

		ParameterizedType type = (ParameterizedType) method
				.getGenericReturnType();

		// Collections have only one type parameter
		Type typeArg = type.getActualTypeArguments()[0];

		// potentially this can be another parameterized type, e.g. for
		// Set<Set<K>> or a wildcard type. Handling these is very tricky and is
		// currently not supported. Hence, we silently ignore these.
		if (!(typeArg instanceof Class<?>)) {
			return false;
		}

		Class<?> actualType = (Class<?>) typeArg;

		return StringUtils.startsWithOneOf(actualType.getName(),
				packagePrefixes);
	}

	/**
	 * This simpy calls {@link Method#invoke(Object, Object...)}. If the called
	 * method throws an exception, this returns <code>null</code>. A possible
	 * {@link IllegalAccessException} is converted to a {@link RuntimeException}
	 * .
	 */
	private static Object invoke(Object object, Method method) {
		try {
			return method.invoke(object);
		} catch (RuntimeException e) {
			return null;
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			return null;
		}
	}
}