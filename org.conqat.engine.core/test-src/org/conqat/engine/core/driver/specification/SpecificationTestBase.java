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
package org.conqat.engine.core.driver.specification;

import java.util.ArrayList;

import org.conqat.engine.core.ConQATInfo;
import org.conqat.engine.core.bundle.BundleInfo;
import org.conqat.engine.core.driver.BlockFileReader;
import org.conqat.engine.core.driver.error.DriverException;
import org.conqat.engine.core.logging.testutils.DriverTestBase;

/**
 * Base class for all driver.specification test cases
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @levd.rating GREEN Hash: 3093FBE76C66C2887427055410BCDE2A
 * 
 */
public abstract class SpecificationTestBase extends DriverTestBase {

	/** Reads the provided configuration and returns the specification. */
	protected BlockSpecification loadConfig(String filename)
			throws DriverException {
		SpecificationLoader specLoader = new SpecificationLoader(null,
				new ArrayList<BundleInfo>());
		return new BlockFileReader(specLoader)
				.readBlockFile(useTestFile(filename));
	}

	/** Reads the named block and returns the specification. */
	protected BlockSpecification loadBlock(String blockName)
			throws DriverException {
		SpecificationLoader specLoader = new SpecificationLoader(
				useTestFile("."), new ArrayList<BundleInfo>());
		return new BlockFileReader(specLoader)
				.readBlockFile(useTestFile(blockName + "."
						+ ConQATInfo.BLOCK_FILE_EXTENSION));
	}
}