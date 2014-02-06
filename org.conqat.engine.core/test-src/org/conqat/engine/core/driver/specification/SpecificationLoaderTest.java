/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2011 the ConQAT Project                                   |
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
package org.conqat.engine.core.driver.specification;

import java.util.ArrayList;

import org.conqat.engine.core.bundle.BundleInfo;
import org.conqat.engine.core.driver.error.DriverException;
import org.conqat.engine.core.logging.testutils.DriverTestBase;

/**
 * Tests the {@link SpecificationLoader}. This does not handle all cases of
 * specification loading, as this is implicitly tested by many of the other
 * test.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: E625002AB0B0A63ED544E585C366E78D
 */
public class SpecificationLoaderTest extends DriverTestBase {

	/** Tests whether loading a block via the class path works. */
	public void testLoadFromClassloader() throws DriverException {
		SpecificationLoader specLoader = new SpecificationLoader(null,
				new ArrayList<BundleInfo>());

		// block name is located relative to this class in a source directory.
		String blockName = getClass().getPackage().getName()
				+ ".blocks.SimpleBlock";
		assertNotNull(specLoader.getBlockSpecification(blockName));
	}
}
