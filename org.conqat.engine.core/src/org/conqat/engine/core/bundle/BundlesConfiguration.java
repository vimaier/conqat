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

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.conqat.engine.core.driver.error.EDriverExceptionType;

/**
 * This class describes a configuration of bundles. Actually it is merely a map
 * from bundle ids to {@link BundleInfo} objects.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46960 $
 * @ConQAT.Rating GREEN Hash: 58A51DF69DC8BBDD5209CD4D66DAC186
 */
public class BundlesConfiguration {

	/** Logger. */
	private static final Logger LOGGER = Logger
			.getLogger(BundlesConfiguration.class);

	/** Maps from bundle id to bundle info. */
	private final TreeMap<String, BundleInfo> bundles = new TreeMap<String, BundleInfo>();

	/**
	 * Get set of bundles. This returns a new set that may be modified.
	 */
	public Set<BundleInfo> getBundles() {
		return new LinkedHashSet<BundleInfo>(bundles.values());
	}

	/**
	 * Add a bundle.
	 * 
	 * @throws BundleException
	 *             if a bundle with the same id was added before.
	 */
	/* package */void addBundle(BundleInfo bundleInfo) throws BundleException {
		String bundleId = bundleInfo.getId();

		if (bundles.containsKey(bundleId)) {
			throw new BundleException(EDriverExceptionType.DUPLICATE_BUNDLE_ID,
					"Duplicate bundle id '" + bundleId + "'.",
					bundleInfo.getLocation(), bundles.get(bundleId)
							.getLocation());
		}

		for (String existingId : bundles.keySet()) {
			if (existingId.startsWith(bundleId)
					|| bundleId.startsWith(existingId)) {
				LOGGER.warn("Had bundle IDs for which one is a prefix of the other one: "
						+ existingId
						+ ", "
						+ bundleId
						+ ". This may cause unexpected behavior with block lookup!");
			}
		}

		bundles.put(bundleId, bundleInfo);
	}

	/**
	 * Get a specific bundle.
	 * 
	 * @param id
	 *            bundle id.
	 * @return the bundle or <code>null</code> if not found.
	 */
	/* package */BundleInfo getBundle(String id) {
		return bundles.get(id);
	}

	/**
	 * Get a set containing the requested bundle and all bundles that it
	 * (transitively) depends on.
	 * <p>
	 * <b>Note:</b> Before calling this method it must be ensured, that all
	 * bundle dependencies can be resolved (see
	 * {@link BundlesDependencyVerifier}, otherwise the returned set may not be
	 * complete.
	 * 
	 * @param id
	 *            the bundle id
	 * @return the set of bundles or <code>null</code> if the bundle was not
	 *         found.
	 */
	/* package */Set<BundleInfo> getBundleClosure(String id) {

		BundleInfo rootBundle = getBundle(id);

		if (rootBundle == null) {
			return null;
		}

		return getBundleClosure(rootBundle);
	}

	/**
	 * Get a set containing the given bundle and all bundles that it
	 * (transitively) depends on.
	 * <p>
	 * <b>Note:</b> Before calling this method it mus be ensured, that all
	 * bundle dependencies can be resolved (see
	 * {@link BundlesDependencyVerifier}, otherwise the returned set may not be
	 * complete.
	 * 
	 * @param rootBundle
	 *            the bundle to start at
	 * @return the set of bundles or <code>null</code> if the bundle was not
	 *         found.
	 */
	private Set<BundleInfo> getBundleClosure(BundleInfo rootBundle) {
		HashSet<BundleInfo> result = new HashSet<BundleInfo>();

		collectBundles(rootBundle, result);

		return result;
	}

	/**
	 * Adds the given bundle to the set and recursively collects all bundles the
	 * the given bundle depends on.
	 * <p>
	 * This code does not check for cyclic bundle dependencies, since this has
	 * already been checked during construction of the
	 * {@link BundlesConfiguration}. See also {@link BundlesTopSorter}.
	 */
	private void collectBundles(BundleInfo bundle, HashSet<BundleInfo> result) {
		result.add(bundle);

		for (BundleDependency dependency : bundle.getDependencies()) {
			BundleInfo dependee = getBundle(dependency.getId());
			collectBundles(dependee, result);
		}

	}

}