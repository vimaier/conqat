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

import org.apache.log4j.Logger;
import org.conqat.engine.core.driver.error.EDriverExceptionType;

/**
 * This class merely verifies that dependencies required by a bundle are
 * satisfied. It does not add any information to the {@link BundleInfo} objects.
 * <p>
 * This class does not check for cyclic bundle dependencies. This check is
 * performed by the {@link BundlesTopSorter}.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 4DB8D80E7D3DF4A8AC2BC5385FB8B87E
 */
public class BundlesDependencyVerifier extends BundleConfigurationProcessorBase {

	/** Logger */
	private final Logger logger = Logger
			.getLogger(BundlesDependencyVerifier.class);

	/** Create new verifier. */
	/* package */BundlesDependencyVerifier(BundlesConfiguration config) {
		super(config);
	}

	/**
	 * Verifies all dependencies of a single bundle.
	 * 
	 * @throws BundleException
	 *             if verification failed.
	 */
	@Override
	protected void process(BundleInfo bundleInfo) throws BundleException {
		for (BundleDependency dependency : bundleInfo.getDependencies()) {
			verify(bundleInfo, dependency);
		}
	}

	/**
	 * Verify a single dependency.
	 * 
	 * @param depender
	 *            the dependant bundle
	 * @param dependency
	 *            the dependency to another bundle.
	 * @throws BundleException
	 *             if verification failed.
	 */
	private void verify(BundleInfo depender, BundleDependency dependency)
			throws BundleException {

		// find self dependencies
		if (depender.getId().equals(dependency.getId())) {
			throw new BundleException(EDriverExceptionType.SELF_DEPENDENCY,
					"Bundle " + depender.getId() + " depends on itself.",
					depender.getLocation());
		}

		// find dependee
		BundleInfo dependee = config.getBundle(dependency.getId());

		// dependee not found
		if (dependee == null) {
			throw new BundleException(EDriverExceptionType.BUNDLE_NOT_FOUND,
					"Bundle '" + depender.getId()
							+ "' depends on missing bundle '"
							+ dependency.getId() + "'.", depender.getLocation());
		}

		if (!dependency.getVersion().equals(dependee.getVersion())) {
			logger.warn("Bundle '" + depender.getId() + "' requires bundle "
					+ dependee.getId() + " in version "
					+ dependency.getVersion()
					+ ", but dependee provides version "
					+ dependee.getVersion() + ".");
		}

	}
}