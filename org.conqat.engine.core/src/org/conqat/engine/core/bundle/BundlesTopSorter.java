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
package org.conqat.engine.core.bundle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.conqat.engine.core.driver.error.EDriverExceptionType;
import org.conqat.engine.core.driver.error.ErrorLocation;
import org.conqat.lib.commons.collections.CounterSet;

/**
 * This class performs a topological sort on the bundle dependency graph. This
 * is used to ensure that the graph is acyclic and to determine the bundle build
 * order.
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: A7B372D5F980F3942462508A561CC93C
 */
public class BundlesTopSorter {

	/** The configuration that describes the bundle dependency graph. */
	private final BundlesConfiguration config;

	/**
	 * Create new top sorter.
	 * 
	 * @param config
	 *            configuration to sort
	 */
	public BundlesTopSorter(BundlesConfiguration config) {
		this.config = config;
	}

	/**
	 * Sort bundle configuration.
	 * 
	 * @return topologically sorted list of the bundle dependency graph, i.e.
	 *         all bundles will only depend on bundles listed before them.
	 * @throws BundleException
	 *             if the bundle dependecy graph contains cycles.
	 */
	public List<BundleInfo> sort() throws BundleException {

		List<BundleInfo> result = new ArrayList<BundleInfo>();
		Map<String, BundleInfo> bundles = new HashMap<String, BundleInfo>();

		// the number of bundles depending on a bundle (identified by its ID)
		CounterSet<String> dependencyCount = new CounterSet<String>();
		for (BundleInfo bi : config.getBundles()) {
			bundles.put(bi.getId(), bi);
			for (BundleDependency dep : bi.getDependencies()) {
				dependencyCount.inc(dep.getId());
			}
		}

		// determine set of bundles no-one depends on
		Stack<String> freeBundles = new Stack<String>();
		for (String id : bundles.keySet()) {
			if (dependencyCount.getValue(id) == 0) {
				freeBundles.push(id);
			}
		}

		// top sorter main loop
		while (!freeBundles.isEmpty()) {
			BundleInfo bi = bundles.get(freeBundles.pop());
			result.add(bi);
			for (BundleDependency dep : bi.getDependencies()) {
				dependencyCount.inc(dep.getId(), -1);
				if (dependencyCount.getValue(dep.getId()) == 0) {
					freeBundles.push(dep.getId());
				}
			}
		}

		// if not all bundles have been free at some point, there is a cycle
		if (result.size() < bundles.size()) {
			throw new BundleException(
					EDriverExceptionType.CYCLIC_BUNDLE_DEPENDENCY,
					"Cyclic bundle dependency.", ErrorLocation.UNKNOWN);
		}

		// as we performed sorting backwards, reverse the result.
		Collections.reverse(result);
		return result;
	}
}