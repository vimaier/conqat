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
package org.conqat.engine.commons.node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.conqat.engine.commons.findings.FindingReport;
import org.conqat.engine.commons.findings.FindingsList;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.assertion.CCSMPre;
import org.conqat.lib.commons.assessment.Assessment;
import org.conqat.lib.commons.clone.CloneUtils;
import org.conqat.lib.commons.clone.DeepCloneException;
import org.conqat.lib.commons.collections.PairList;

/**
 * Collection of utility methods used to manipulate key value pairs on
 * IConQATNodes.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43134 $
 * @ConQAT.Rating GREEN Hash: 35309042107A1768C7A13AB8F6650C13
 */
public class NodeUtils {

	/**
	 * Checks whether the value at the given key is a List. Returns the list if
	 * it exists, or <code>null</code> otherwise.
	 */
	public static List<String> getStringList(IConQATNode node, String key) {
		return getTypedList(node, key, String.class);
	}

	/**
	 * Checks whether the value at the given key is a List. Returns the list if
	 * it exists, or <code>null</code> otherwise.
	 */
	@SuppressWarnings("null")
	public static FindingsList getFindingsList(IConQATNode node, String key) {
		CCSMPre.isTrue(node != null, "Node may not be null!");
		Object o = node.getValue(key);
		if (!(o instanceof FindingsList)) {
			return null;
		}

		return (FindingsList) o;
	}

	/**
	 * Checks whether the value at the given key is a List. Returns the list if
	 * it exists, or <code>null</code> otherwise. This does not check the
	 * contents of the list, so if you are unsure use Object as type.
	 */
	@SuppressWarnings("null")
	private static <T> List<T> getTypedList(IConQATNode node, String key,
			@SuppressWarnings("unused") Class<T> type) {
		CCSMPre.isTrue(node != null, "Node may not be null!");

		Object o = node.getValue(key);
		if (!(o instanceof List<?>)) {
			return null;
		}

		@SuppressWarnings("unchecked")
		List<T> list = (List<T>) o;
		return list;
	}

	/**
	 * Checks whether the value at the given key is a {@link Collection}.
	 * Returns the collection if it exists, or <code>null</code> otherwise. This
	 * does not check the contents of the list.
	 * 
	 */
	public static Collection<String> getStringCollection(IConQATNode node,
			String key) {
		return getTypedCollection(node, key, String.class);
	}

	/**
	 * Checks whether the value at the given key is a {@link Collection}.
	 * Returns the collection if it exists, or <code>null</code> otherwise. This
	 * does not check the contents of the list, so if you are unsure use Object
	 * as type.
	 */
	@SuppressWarnings("null")
	public static <T> Collection<T> getTypedCollection(IConQATNode node,
			String key, @SuppressWarnings("unused") Class<T> type) {
		CCSMPre.isTrue(node != null, "Node may not be null!");

		Object o = node.getValue(key);
		if (!(o instanceof Collection<?>)) {
			return null;
		}

		@SuppressWarnings("unchecked")
		Collection<T> collection = (Collection<T>) o;
		return collection;
	}

	/**
	 * Return collection stored at key. Returns null, if null is stored at key.
	 * 
	 * @throws ConQATException
	 *             if the value stored at key is no collection and not null
	 */
	public static Collection<?> getAndCheckCollection(IConQATNode node,
			String key) throws ConQATException {

		Object value = node.getValue(key);
		if (value == null) {
			return null;
		}

		if (!(value instanceof Collection<?>)) {
			throw new ConQATException("Value stored at key '" + key
					+ "' is no collection: " + value.toString());
		}

		return (Collection<?>) value;
	}

	/**
	 * Checks whether the value at the given key is a set. Returns the set if it
	 * exists, or <code>null</code> otherwise. This does not check the contents
	 * of the set, so if you are unsure use Object as type.
	 */
	@SuppressWarnings("null")
	private static <T> Set<T> getTypedSet(IConQATNode node, String key,
			@SuppressWarnings("unused") Class<T> type) {
		CCSMPre.isTrue(node != null, "Node may not be null!");

		Object o = node.getValue(key);
		if (!(o instanceof Set<?>)) {
			return null;
		}

		@SuppressWarnings("unchecked")
		Set<T> set = (Set<T>) o;
		return set;
	}

	/**
	 * Checks whether the value at the given key is a List. If so, returns it,
	 * otherwise a new list is created, added, and returned.
	 */
	public static List<String> getOrCreateStringList(IConQATNode node,
			String key) {
		return getOrCreateTypedList(node, key, String.class);
	}

	/**
	 * Checks whether the value at the given key is a set. If so, returns it,
	 * otherwise a new set is created, added, and returned.
	 */
	public static Set<String> getOrCreateStringSet(IConQATNode node, String key) {
		Set<String> set = getTypedSet(node, key, String.class);
		if (set == null) {
			set = new HashSet<String>();
			node.setValue(key, set);
		}
		return set;
	}

	/**
	 * Checks whether the value at the given key is a List. If so, returns it,
	 * otherwise a new list is created, added, and returned.
	 */
	public static FindingsList getOrCreateFindingsList(IConQATNode node,
			String key) {
		FindingsList list = getFindingsList(node, key);
		if (list == null) {
			list = new FindingsList(node);
			node.setValue(key, list);
		}
		return list;
	}

	/**
	 * Checks whether the value at the given key is a List. If so, returns it,
	 * otherwise a new list is created, added, and returned.
	 */
	public static <T> List<T> getOrCreateTypedList(IConQATNode node,
			String key, Class<T> type) {
		List<T> list = getTypedList(node, key, type);
		if (list == null) {
			list = new ArrayList<T>();
			node.setValue(key, list);
		}
		return list;
	}

	/**
	 * Returns the display list of the provided ConQAT node. If none exists a
	 * new one will be created and added to the node.
	 */
	public static DisplayList getDisplayList(IConQATNode node) {
		Object list = node.getValue(NodeConstants.DISPLAY_LIST);
		if (list instanceof DisplayList) {
			return (DisplayList) list;
		}

		DisplayList displayList = new DisplayList();
		node.setValue(NodeConstants.DISPLAY_LIST, displayList);
		return displayList;
	}

	/**
	 * Appends all provided values not already in the display list to the
	 * display list of the given node.
	 */
	public static void addToDisplayList(IConQATNode node, String... values) {
		DisplayList displayList = getDisplayList(node);
		for (String value : values) {
			if (!displayList.containsKey(value)) {
				displayList.addKey(value, null);
			}
		}
	}

	/**
	 * Appends all provided values not already in the display list to the
	 * display list of the given node.
	 */
	public static void addToDisplayList(IConQATNode node,
			Iterable<String> values) {
		for (String value : values) {
			addToDisplayList(node, value);
		}
	}

	/**
	 * Returns the summary for a node. If none exists null will be returned.
	 */
	public static Object getSummary(IConQATNode node) {
		return node.getValue(NodeConstants.SUMMARY);
	}

	/**
	 * Returns the finding report stored at the node. If none exists, a new one
	 * will be created. Note that anything stored at this key which is not a
	 * {@link FindingReport} will be overwritten.
	 */
	public static FindingReport getFindingReport(IConQATNode node) {
		Object o = node.getValue(NodeConstants.FINDINGS_REPORT);
		if (!(o instanceof FindingReport)) {
			FindingReport report = new FindingReport();
			node.setValue(NodeConstants.FINDINGS_REPORT, report);
			return report;
		}
		return (FindingReport) o;
	}

	/**
	 * Checks whether the value at the given key is an assessment. If so, it is
	 * returned, otherwise creates a new assessment, adds it for the key and
	 * returns it.
	 * 
	 * @param node
	 *            the node to read the list from.
	 * @param key
	 *            the key the list is stored at.
	 */
	public static Assessment getOrCreateAssessment(IConQATNode node, String key) {
		Object o = node.getValue(key);
		if (!(o instanceof Assessment)) {
			Assessment a = new Assessment();
			node.setValue(key, a);
			return a;
		}
		return (Assessment) o;
	}

	/**
	 * Determines if the root node should be hidden or not. It will be hidden if
	 * value with key {@link NodeConstants#HIDE_ROOT} is <code>true</code>. In
	 * all other case or if the value is not present this returns
	 * <code>false</code>.
	 */
	public static boolean getHideRoot(IConQATNode node) {
		Object o = node.getValue(NodeConstants.HIDE_ROOT);
		return o instanceof Boolean && ((Boolean) o).booleanValue();
	}

	/**
	 * Sets whether the root node should be hidden or not, by setting the value
	 * with key {@link NodeConstants#HIDE_ROOT}.
	 */
	public static void setHideRoot(IConQATNode node, boolean hideRoot) {
		node.setValue(NodeConstants.HIDE_ROOT, hideRoot);
	}

	/**
	 * Get a date value stored at a node.
	 * 
	 * @throws ConQATException
	 *             if the value is <code>null</code> or not a date.
	 */
	public static Date getDateValue(IConQATNode node, String key)
			throws ConQATException {
		Object value = node.getValue(key);
		if (!(value instanceof Date)) {
			throw new ConQATException("Value '" + value + "' stored for key '"
					+ key + "' at node '" + node.getId() + "' is not a date.");
		}
		return (Date) value;
	}

	/**
	 * Get a double value stored at a node.
	 * 
	 * @throws ConQATException
	 *             if the value is <code>null</code> or not numeric.
	 */
	public static double getDoubleValue(IConQATNode node, String key)
			throws ConQATException {
		Object value = node.getValue(key);
		if (!(value instanceof Number)) {
			throw new ConQATException("Value '" + value + "' stored for key '"
					+ key + "' at node '" + node.getId() + "' is not a number.");
		}
		return ((Number) value).doubleValue();
	}

	/**
	 * Get a double value stored at a node. If the value is <code>null</code> or
	 * not number the provided default value is returned.
	 */
	public static double getDoubleValue(IConQATNode node, String key,
			double defaultValue) {
		try {
			return getDoubleValue(node, key);
		} catch (ConQATException e) {
			return defaultValue;
		}
	}

	/**
	 * Returns the boolean value stored for the key. If no value is stored or
	 * the stored element is not boolean, false is returned. If the stored
	 * element is a string, the result is determined using
	 * {@link Boolean#parseBoolean(String)}.
	 * 
	 * @throws ConQATException
	 *             if the value is not of type boolean or string. Note that a
	 *             null value (i.e. absence of a value) is interpreted as false.
	 */
	public static boolean getBooleanValue(IConQATNode node, String key)
			throws ConQATException {
		Object value = node.getValue(key);
		if (value == null) {
			return false;
		}
		if (value instanceof String) {
			return Boolean.parseBoolean((String) value);
		}
		if (value instanceof Boolean) {
			return (Boolean) value;
		}
		throw new ConQATException("Value " + value + " stored at key " + key
				+ " can not be converted to a boolean.");
	}

	/**
	 * Get string representation of the value stored at a node.
	 * 
	 * @throws ConQATException
	 *             if the value is <code>null</code>.
	 */
	public static String getStringValue(IConQATNode node, String key)
			throws ConQATException {
		Object value = node.getValue(key);
		if (value == null) {
			throw new ConQATException("No value stored for key '" + key
					+ "' at node '" + node.getId() + "'.");
		}
		return value.toString();
	}

	/**
	 * Get string representation of the value stored at a node. If the value is
	 * <code>null</code>, the provided default value is returned.
	 */
	public static String getStringValue(IConQATNode node, String key,
			String defaultValue) {
		Object value = node.getValue(key);
		if (value == null) {
			return defaultValue;
		}
		return value.toString();
	}

	/**
	 * Get value of a specified type stored at a node.
	 * 
	 * @throws ConQATException
	 *             if the value is <code>null</code> or not an instance of the
	 *             specified type.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getValue(IConQATNode node, String key, Class<T> type)
			throws ConQATException {
		Object value = node.getValue(key);
		if (!type.isInstance(value)) {
			throw new ConQATException("Value '" + value + "' stored for key '"
					+ key + "' at node '" + node.getId() + "' is not a "
					+ type.getName() + ".");
		}
		return (T) value;
	}

	/**
	 * Get value of a specified type stored at a node. If the value is
	 * <code>null</code> or not an instance of the specified type the provided
	 * default value is returned.
	 */
	public static <T> T getValue(IConQATNode node, String key, Class<T> type,
			T defaultValue) {
		try {
			return getValue(node, key, type);
		} catch (ConQATException e) {
			return defaultValue;
		}
	}

	/**
	 * Checks if the node defines a comparator via
	 * {@link NodeConstants#COMPARATOR} and returns the sorted children of the
	 * node. If no comparator is defined the children are returned unsorted.
	 * Returns <code>null</code> if the node doesn't have children.
	 */
	public static IConQATNode[] getSortedChildren(IConQATNode node) {
		return getChildren(node, true);
	}

	/**
	 * Checks if the node defines a comparator via
	 * {@link NodeConstants#COMPARATOR} and returns the sorted children of the
	 * node. If no comparator is defined or the additionally parameter is set to
	 * false, the children are returned unsorted. Returns <code>null</code> if
	 * the node doesn't have children.
	 * 
	 * @param sortIfPossible
	 *            if this is true and a comparator is defined, the children are
	 *            sorted. Otherwise there are not sorted.
	 */
	@SuppressWarnings("unchecked")
	public static IConQATNode[] getChildren(IConQATNode node,
			boolean sortIfPossible) {
		if (!node.hasChildren()) {
			return null;
		}
		IConQATNode[] children = node.getChildren();

		if (!sortIfPossible) {
			return children;
		}

		Comparator<IConQATNode> comparator = (Comparator<IConQATNode>) getComparator(node);
		if (comparator != null) {
			Arrays.sort(children, comparator);
		}
		return children;
	}

	/**
	 * Returns the first child of the given node that has the given id or
	 * <code>null</code> if no such child exists.
	 */
	public static IConQATNode getFirstChildrenWithId(IConQATNode node,
			String childId) {
		if (!node.hasChildren()) {
			return null;
		}
		IConQATNode[] children = node.getChildren();
		for (IConQATNode child : children) {
			if (childId.equals(childId)) {
				return child;
			}
		}
		return null;
	}

	/**
	 * Checks if the node defines a comparator via
	 * {@link NodeConstants#COMPARATOR} and returns the sorted children of the
	 * node. This method is required as the
	 * {@link #getSortedChildren(org.conqat.engine.commons.node.IConQATNode)}
	 * method does not return {@link IRemovableConQATNode}s. We cannot make this
	 * generic as this would require each node to return children of the same
	 * type as the node.
	 */
	@SuppressWarnings("unchecked")
	public static IRemovableConQATNode[] getRemovableSortedChildren(
			IRemovableConQATNode node) {
		if (!node.hasChildren()) {
			return null;
		}
		IRemovableConQATNode[] children = node.getChildren();
		Comparator<IConQATNode> comparator = (Comparator<IConQATNode>) getComparator(node);
		if (comparator != null) {
			Arrays.sort(children, comparator);
		}
		return children;
	}

	/**
	 * Get comparator stored at key {@value NodeConstants#COMPARATOR}. This
	 * returns <code>null</code> if value is not present or not of type
	 * {@link Comparator},
	 */
	public static Comparator<?> getComparator(IConQATNode node) {
		Object comparatorObject = node.getValue(NodeConstants.COMPARATOR);
		if (comparatorObject instanceof Comparator<?>) {
			return (Comparator<?>) comparatorObject;
		}
		return null;
	}

	/**
	 * Sets the given {@link Comparator}, storing it under the key
	 * {@value NodeConstants#COMPARATOR}.
	 */
	public void setComparator(IConQATNode node, Comparator<?> comparator) {
		node.setValue(NodeConstants.COMPARATOR, comparator);
	}

	/**
	 * Returns true, if all key-value pairs are stored in a node. Matching is
	 * performed on the string representations of the values stored in the node.
	 * 
	 * @param node
	 *            Node that gets analyzed.
	 * @param keyValues
	 *            for each key, it is checked if corresponding value is stored
	 *            in finding
	 * */
	public static boolean containsValues(IConQATNode node,
			PairList<String, String> keyValues) {
		for (int i = 0; i < keyValues.size(); i++) {
			String key = keyValues.getFirst(i);
			String expectedValueString = keyValues.getSecond(i);

			Object actualValue = node.getValue(key);
			if (actualValue == null
					|| !actualValue.toString().equals(expectedValueString)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * For each key in keyList, copy stored value from sourceNode to targetNode.
	 * Value is deep cloned during copy. Empty values are not copied, to avoid
	 * overwriting of existing values.
	 * 
	 * @throws ConQATException
	 *             if there are problems with deep cloning.
	 * 
	 * @see #copyValues(Iterable, IConQATNode, IConQATNode, boolean)
	 */
	public static void copyValues(Iterable<String> keyList,
			IConQATNode sourceNode, IConQATNode targetNode)
			throws ConQATException {
		copyValues(keyList, sourceNode, targetNode, true);
	}

	/**
	 * For each key in keyList, copy stored value from sourceNode to targetNode.
	 * Empty values are not copied, to avoid overwriting of existing values.
	 * 
	 * @param keyList
	 *            List of keys for which stored values are copied.
	 * 
	 * @param sourceNode
	 *            Node from which values are read.
	 * 
	 * @param targetNode
	 *            Node to which values are copied
	 * @param deepClone
	 *            Flag that determines whether values are cloned during copying
	 * @throws ConQATException
	 *             if there are problems with deep cloning. This is not thrown
	 *             if the deepClone parameter is set to false.
	 */
	public static void copyValues(Iterable<String> keyList,
			IConQATNode sourceNode, IConQATNode targetNode, boolean deepClone)
			throws ConQATException {

		PairList<String, String> kayMap = new PairList<String, String>();
		for (String key : keyList) {
			kayMap.add(key, key);
		}

		copyValues(kayMap, sourceNode, targetNode, deepClone);
	}

	/**
	 * For each pair of source and target keys in keyList, read stored value
	 * from sourceNode to targetNode. Value is read from first key and stored in
	 * second key. Empty values are not copied, to avoid overwriting of existing
	 * values. Copied values are cloned.
	 * 
	 * @throws ConQATException
	 *             if there are problems with deep cloning.
	 * 
	 * @see #copyValues(PairList, IConQATNode, IConQATNode, boolean)
	 */
	public static void copyValues(PairList<String, String> keyList,
			IConQATNode sourceNode, IConQATNode targetNode)
			throws ConQATException {
		copyValues(keyList, sourceNode, targetNode, true);
	}

	/**
	 * For each pair of source and target keys in keyList, read stored value
	 * from sourceNode to targetNode. Value is read from first key and stored in
	 * second key. Empty values are not copied, to avoid overwriting of existing
	 * values.
	 * 
	 * @param keyList
	 *            List of key mappings for which stored values are copied.
	 * 
	 * @param sourceNode
	 *            Node from which values are read.
	 * 
	 * @param targetNode
	 *            Node to which values are copied
	 * 
	 * @throws ConQATException
	 *             if there are problems with deep cloning. This is not thrown
	 *             if the deepClone parameter is set to false.
	 */
	public static void copyValues(PairList<String, String> keyList,
			IConQATNode sourceNode, IConQATNode targetNode, boolean deepClone)
			throws ConQATException {
		for (int i = 0; i < keyList.size(); i++) {
			String fromKey = keyList.getFirst(i);
			Object value = sourceNode.getValue(fromKey);
			if (value != null) {
				String toKey = keyList.getSecond(i);
				try {
					if (deepClone) {
						value = CloneUtils.cloneAsDeepAsPossible(value);
					}
					targetNode.setValue(toKey, value);
				} catch (DeepCloneException e) {
					throw new ConQATException("Error during deep cloning", e);
				}
			}
		}
	}

	/**
	 * Returns the root node for the given node, which is found by traversing up
	 * the hierarchy as long as the parent node is not null.
	 */
	public static IConQATNode getRootNode(IConQATNode node) {
		while (node.getParent() != null) {
			node = node.getParent();
		}
		return node;
	}

	/**
	 * Returns the path from the root to the given node as a list of nodes
	 * including the given node.
	 */
	public static List<IConQATNode> getPathFromRoot(IConQATNode node) {
		List<IConQATNode> path = new ArrayList<IConQATNode>();
		do {
			path.add(node);
			node = node.getParent();
		} while (node != null);
		Collections.reverse(path);
		return path;
	}

	/**
	 * Computes the lowest (a.k.a. least) common ancestor (LCA) of the two given
	 * nodes. The LCA is the node g which is ancestor of both node1 and node2
	 * and has the greatest depth in the tree.
	 */
	public static IConQATNode getLowestCommonAncestor(IConQATNode node1,
			IConQATNode node2) {
		List<IConQATNode> path1 = getPathFromRoot(node1);
		List<IConQATNode> path2 = getPathFromRoot(node2);
		int i = 0;
		while (path1.size() > i && path2.size() > i
				&& path1.get(i) == path2.get(i)) {
			i++;
		}
		CCSMAssert.isFalse(i == 0, "No common ancestor exists");
		return path1.get(i - 1);
	}

	/**
	 * Returns the distance from node to ancestor. If ancestor is not an
	 * ancestor of node, -1 is returned.
	 */
	public static int getDistanceToAncestor(IConQATNode node,
			IConQATNode ancestor) {
		int distance = 0;
		while (node != ancestor && node.getParent() != null) {
			distance++;
			node = node.getParent();
		}
		if (node != ancestor) {
			return -1;
		}
		return distance;
	}

	/**
	 * Returns true, if the value <code>true</code> is stored under the ignore
	 * key.
	 */
	public static <Element extends IConQATNode> boolean isIgnored(
			Element element, String ignoreKey) {
		return getValue(element, ignoreKey, Boolean.class, false);
	}

}