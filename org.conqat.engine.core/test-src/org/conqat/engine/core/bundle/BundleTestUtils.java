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

import java.io.File;
import java.io.IOException;

import org.conqat.lib.commons.assertion.CCSMPre;
import org.conqat.lib.commons.assertion.PreconditionException;

/**
 * Utility methods for testing bundles.
 * 
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @levd.rating GREEN Hash: 391890954B5765CC16EE42571A2DBA2D
 */
public class BundleTestUtils {

	/**
	 * Create bundle info object from the a bundle descriptor. The resulting
	 * bundle descriptor is meant for test purposes only. It has all the
	 * information stored in the bundle descriptor but does not e.g. know
	 * anything about libraries.
	 * 
	 * @param bundleLocation
	 *            the location of the bundle (directory where the descriptor
	 *            resides).
	 * @throws BundleException
	 *             if the descriptor could not be read
	 * @throws PreconditionException
	 *             if the provided location is not a directory or does not
	 *             contain a bundle descriptor.
	 */
	public static BundleInfo createBundleInfo(File bundleLocation)
			throws BundleException, IOException {
		CCSMPre.isTrue(bundleLocation.isDirectory(),
				"Bundle location must be a directory.");
		BundleInfo bundleInfo = new BundleInfo(bundleLocation);
		File descriptor = bundleInfo.getDescriptor();
		CCSMPre.isTrue(descriptor.isFile(),
				"Location does not contain a bundle descriptor.");
		BundleDescriptorReader reader = new BundleDescriptorReader(descriptor);
		reader.read(bundleInfo);
		return bundleInfo;
	}
}