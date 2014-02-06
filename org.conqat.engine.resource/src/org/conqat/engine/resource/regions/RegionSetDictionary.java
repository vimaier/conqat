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
package org.conqat.engine.resource.regions;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.region.RegionSet;

/**
 * This class holds {@link RegionSet}s and makes them accessible via their name.
 * <p>
 * In addition, it offers utility methods for convenient access to
 * {@link RegionSetDictionary} instances in {@link IConQATNode}s.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 36499 $
 * @ConQAT.Rating GREEN Hash: 62C1A6F80C18F709E3D21C972E283A1F
 */
public class RegionSetDictionary implements Iterable<RegionSet> {

	/**
	 * Key that is used to store a {@link RegionSetDictionary} at
	 * {@link IConQATNode}s.
	 */
	public static final String KEY = "region-sets";

	/** Maps from names to {@link RegionSet}s */
	public final Map<String, RegionSet> regionSets = new HashMap<String, RegionSet>();

	/**
	 * Adds a {@link RegionSet} to the dictionary. If the dictionary already
	 * contains a {@link RegionSet} with this name, the regions are added to the
	 * existing {@link RegionSet}.
	 */
	public void add(RegionSet regionSet) {
		String name = regionSet.getName();
		if (regionSets.containsKey(name)) {
			regionSets.get(name).addAll(regionSet);
		} else {
			regionSets.put(name, regionSet);
		}
	}

	/** Adds all {@link RegionSet}s from another {@link RegionSetDictionary}. */
	public void addAll(RegionSetDictionary regionSetDict) {
		for (RegionSet rs : regionSetDict) {
			add(rs);
		}
	}

	/** Iterates over the RegionSets in the dictionary */
	@Override
	public Iterator<RegionSet> iterator() {
		return regionSets.values().iterator();
	}

	/**
	 * Returns a {@link RegionSet} with this name, of null, if the dictionary
	 * does not contain a {@link RegionSet} for that name.
	 */
	public RegionSet get(String name) {
		return regionSets.get(name);
	}

	/**
	 * Returns true, if the {@link RegionSetDictionary} contains a
	 * {@link RegionSet} with this name
	 */
	public boolean contains(String name) {
		return regionSets.containsKey(name);
	}

	/**
	 * Removes the {@link RegionSet} with this name
	 * 
	 * @return The {@link RegionSet} that was removed, or null, if the
	 *         {@link RegionSetDictionary} did not contain a {@link RegionSet}
	 *         with that name.
	 */
	public RegionSet remove(String name) {
		return regionSets.remove(name);
	}

	/**
	 * Tries to retrieve the RegionSetDictionary stored at an
	 * {@link IConQATNode}.
	 * 
	 * @return The stored {@link RegionSetDictionary} or null, if none was
	 *         found.
	 * 
	 * @throws ConQATException
	 *             If the object stored under {@link RegionSetDictionary#KEY} is
	 *             not of type {@link RegionSetDictionary}
	 */
	public static RegionSetDictionary retrieve(IConQATNode element)
			throws ConQATException {
		if (element.getValue(KEY) == null) {
			return null;
		}

		return NodeUtils.getValue(element, KEY, RegionSetDictionary.class);
	}

	/**
	 * Tries to retrieve a named {@link RegionSet} stored at an
	 * {@link IConQATNode}
	 * 
	 * @param regionSetName
	 *            Name of the {@link RegionSet} that gets retrieved
	 * 
	 * @return The {@link RegionSet} with this name, or null, if none was found.
	 * 
	 * @throws ConQATException
	 *             If the object stored under {@link RegionSetDictionary#KEY} is
	 *             not of type {@link RegionSetDictionary}
	 */
	public static RegionSet retrieve(IConQATNode element, String regionSetName)
			throws ConQATException {
		RegionSetDictionary dictionary = retrieve(element);

		if (dictionary == null) {
			return null;
		}

		return dictionary.get(regionSetName);
	}

	/**
	 * Tries to retrieve the {@link RegionSetDictionary} stored at an
	 * {@link IConQATNode}. If no {@link RegionSetDictionary} is found, it gets
	 * created and stored at the element.
	 * 
	 * @return The stored {@link RegionSetDictionary} or a newly created one.
	 * 
	 * @throws ConQATException
	 *             If the object stored under {@link RegionSetDictionary#KEY} is
	 *             not of type {@link RegionSetDictionary}
	 */
	public static RegionSetDictionary retrieveOrCreate(IConQATNode element)
			throws ConQATException {
		RegionSetDictionary dictionary = retrieve(element);
		if (dictionary == null) {
			dictionary = new RegionSetDictionary();
			element.setValue(KEY, dictionary);
		}
		return dictionary;
	}

}