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
import java.util.List;
import java.util.Set;

import org.conqat.lib.commons.collections.CollectionUtils;

/**
 * Test for {@link BundlesConfiguration}.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 44648 $
 * @ConQAT.Rating GREEN Hash: D5786C0199AA51E30C515605AA119DAE
 */
public class BundleTopSorterTest extends BundleTestBase {

	/** Test if bundle configuration returns bundles in predictable order. */
	public void testBundleOrder() throws BundleException {

		String[] expected = { "bundleConfig01", "bundleConfig02",
				"bundleConfig03", "bundleConfig04", "bundleConfig05" };

		String[] expectedSorted = { "bundleConfig05", "bundleConfig04",
				"bundleConfig03", "bundleConfig02", "bundleConfig01" };

		for (List<String> permutation : CollectionUtils
				.getAllPermutations(expected)) {

			BundlesConfiguration config = loadConfiguration(CollectionUtils
					.toArray(permutation, String.class));
			assertEquals(permutation.size(), config.getBundles().size());

			// check if BundleConfiguration returns the bundles in a predictable
			// order
			checkBundleOrder(expected,
					new ArrayList<BundleInfo>(config.getBundles()));

			// check same for the BundlesTopSorter
			checkBundleOrder(expectedSorted,
					new BundlesTopSorter(config).sort());
		}

	}

	/**
	 * Checks for a single permutation if the bundles are in the expected order.
	 */
	private void checkBundleOrder(String[] expected, List<BundleInfo> bundles) {
		assertEquals(expected.length, bundles.size());
		for (int i = 0; i < bundles.size(); i++) {
			assertEquals(expected[i], bundles.get(i).getId());
		}
	}

	/**
	 * Enables the {@link BundlesLoader}.
	 */
	@Override
	protected Set<Class<?>> getEnabledBuildlets() {
		return createSet(BundlesLoader.class);
	}

}