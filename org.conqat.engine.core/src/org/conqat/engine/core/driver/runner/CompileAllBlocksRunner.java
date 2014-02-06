/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2012 the ConQAT Project                                   |
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
package org.conqat.engine.core.driver.runner;

import java.util.Set;

import org.conqat.engine.core.bundle.BundleInfo;
import org.conqat.engine.core.driver.DriverUtils;
import org.conqat.engine.core.driver.error.DriverException;
import org.conqat.engine.core.driver.specification.SpecificationLoader;

/**
 * ConQAT utility runner mainly used for automated builds. This runner compiles
 * all processors and blocks and exits with 1 in case errors are found, 0 if not.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: DCD756B9B9BE1D047BECB6EC5AB22539
 */
public class CompileAllBlocksRunner extends ConQATRunnableBase {

	/** {@inheritDoc} */
	@Override
	protected void doRun() {
		System.out.println("Compiling all blocks and processors...");
		Set<BundleInfo> bundles = bundleConfig.getBundles();
		SpecificationLoader specLoader = new SpecificationLoader(null, bundles);
		try {
			DriverUtils.compileAllProcessorsAndBlocks(bundleConfig, specLoader);
		} catch (DriverException e) {
			System.err.println("ERROR: " + e.getMessage());
			System.err.println(e.getLocationsAsString());
			System.exit(1);
		}
		System.out.println("SUCCESS: Done.");
	}

}
