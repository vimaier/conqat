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
 * Base class for classes that perform some action on all bundles stored in a
 * configuration.
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 6765640EE8773DF78D84E73C34E284DB
 */
public abstract class BundleConfigurationProcessorBase {
	/** The configuration the class works on. */
	protected final BundlesConfiguration config;

	/** Create processor. */
	/* package */BundleConfigurationProcessorBase(BundlesConfiguration config) {
		this.config = config;
	}

	/**
	 * Iterates over all bundles in the configuration and calls
	 * {@link #process(BundleInfo)} for each bundle.
	 */
	/* package */void process() throws BundleException {
		for (BundleInfo bundleInfo : config.getBundles()) {
			process(bundleInfo);
		}
	}

	/** Template method for processing a bundle. */
	protected abstract void process(BundleInfo bundleInfo)
			throws BundleException;
}