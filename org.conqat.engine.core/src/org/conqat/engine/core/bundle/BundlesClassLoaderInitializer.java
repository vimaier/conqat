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
 * This class prepares a bundle class loader according to a previously prepared
 * bundle configuration.
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @author Elmar Juergens
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 6D4FC13EAF7D05C0C82E2552804DA252
 */
public class BundlesClassLoaderInitializer extends
		BundleConfigurationProcessorBase {

	/** Class loader to initialize. */
	private final BundlesClassLoader classLoader;

	/**
	 * Create new initializer.
	 * 
	 * @param classLoader
	 *            class loader to initialize.
	 */
	/* package */BundlesClassLoaderInitializer(BundlesConfiguration config,
			BundlesClassLoader classLoader) {
		super(config);
		this.classLoader = classLoader;
	}

	/** Add bundle to class loader. */
	@Override
	protected void process(BundleInfo bundleInfo) throws BundleException {
		classLoader.addBundle(bundleInfo);
	}
}