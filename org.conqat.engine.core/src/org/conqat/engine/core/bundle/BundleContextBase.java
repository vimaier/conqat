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

/**
 * This is the mandatory base class for bundle context classes. The context
 * class enables a bundle to obtain access to resources and bundle specific
 * information defined in the bundle descriptor.
 * <p>
 * Subclasses must be called <code>BundleContext</code> to be instantiated by
 * the bundle mechanism.
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: DC7C558A4B3C7F4D80E791C84694C8CA
 */
public class BundleContextBase {

	/** The resource manager. */
	private final BundleResourceManager resourceManager;

	/** The bundle info. */
	private final BundleInfo bundleInfo;

	/** Create new bundle context. */
	protected BundleContextBase(BundleInfo bundleInfo) {
		if (bundleInfo == null) {
			throw new IllegalArgumentException("BundleInfo may not be null.");
		}
		resourceManager = new BundleResourceManager(bundleInfo);
		this.bundleInfo = bundleInfo;

	}

	/** Get the bundle's resource manager. */
	public BundleResourceManager getResourceManager() {
		return resourceManager;
	}

	/** Get bundle info (enables access to bundle descriptor information). */
	public BundleInfo getBundleInfo() {
		return bundleInfo;
	}
}