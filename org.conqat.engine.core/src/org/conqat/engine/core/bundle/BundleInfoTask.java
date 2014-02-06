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

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * This tasks reads a bundle descriptor and stores the following information as
 * ANT properties.
 * 
 * <ul>
 * <li>Property {@value #PROPERTY_BUNDLE_ID} -> Bundle id</li>
 * <li>Property {@value #PROPERTY_BUNDLE_VERSION} -> Bundle version</li>
 * </ul>
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 656B672268CD5BC513B7ECE0AC8DB583
 */
public class BundleInfoTask extends Task {

	/** Constant for bundle id property. */
	public static final String PROPERTY_BUNDLE_ID = "bundle.id";

	/** Constant for bundle version property. */
	public static final String PROPERTY_BUNDLE_VERSION = "bundle.version";

	/** Path to the bundle.xml */
	private File bundleLocation;

	/** Set bundle path. */
	public void setBundlePath(String bundlePath) {
		bundleLocation = new File(bundlePath);
	}

	/**
	 * Execute task. This reads the bundle descriptor and sets the properties.
	 * 
	 * @throws BuildException
	 *             if loading the bundle descriptor failed.
	 */
	@Override
	public void execute() throws BuildException {
		if (bundleLocation == null) {
			throw new BuildException("Bundle path undefined!");
		}

		BundleInfo bundleInfo = readBundleInfo();

		getProject().setProperty(PROPERTY_BUNDLE_ID, bundleInfo.getId());
		getProject().setProperty(PROPERTY_BUNDLE_VERSION,
				bundleInfo.getVersion().toString());
	}

	/**
	 * Load bundle descriptor.
	 * 
	 * @throws BuildException
	 *             if loading the bundle descriptor failed.
	 */
	private BundleInfo readBundleInfo() throws BuildException {

		try {
			bundleLocation = bundleLocation.getCanonicalFile();
		} catch (IOException e) {
			throw new BuildException("Error creating canonical file: "
					+ e.getMessage(), e);
		}

		if (!bundleLocation.isDirectory()) {
			throw new BuildException("Bundle location must be a directory!");
		}

		try {
			BundleInfo bundleInfo = new BundleInfo(bundleLocation);
			File descriptor = bundleInfo.getDescriptor();
			if (!descriptor.isFile()) {
				throw new BuildException(
						"Location does not contain a bundle descriptor.");
			}
			BundleDescriptorReader reader = new BundleDescriptorReader(
					descriptor);
			reader.read(bundleInfo);
			return bundleInfo;
		} catch (BundleException e) {
			throw new BuildException(e.getMessage(), e);
		} catch (IOException e) {
			throw new BuildException("File not found: ", e);
		}
	}
}