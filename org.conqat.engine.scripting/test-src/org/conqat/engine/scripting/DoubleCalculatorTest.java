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
package org.conqat.engine.scripting;

import org.conqat.engine.core.driver.error.DriverException;
import org.conqat.engine.core.driver.processors.DataSinkProcessor;
import org.conqat.engine.core.logging.testutils.DriverTestBase;

/**
 * Test the DoubleCalculator class.
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @levd.rating GREEN Hash: AA8F4C25AD15B919F2368262D879DEC8
 */
public class DoubleCalculatorTest extends DriverTestBase {

	/** Test whether integration works. */
	public void testIntegration() throws DriverException  {

		runDriver("DoubleCalculator.cqb");

		assertTrue(DataSinkProcessor.accessData("dc1").get(0) instanceof Double);
		assertTrue(DataSinkProcessor.accessData("dc2").get(0) instanceof Double);
		assertTrue(DataSinkProcessor.accessData("dc3").get(0) instanceof Double);

		assertEquals(DataSinkProcessor.accessData("dc1").get(0), 42.);
		assertEquals(DataSinkProcessor.accessData("dc2").get(0), 1.);
		assertEquals(DataSinkProcessor.accessData("dc3").get(0), 18.);
	}
}