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

import org.conqat.engine.commons.testutils.NodeCreator;
import org.conqat.engine.core.driver.error.DriverException;
import org.conqat.engine.core.driver.processors.DataSinkProcessor;
import org.conqat.engine.core.logging.testutils.DriverTestBase;

/**
 * Test the NodeValueCalculator class.
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @levd.rating GREEN Hash: 1C5494AC2DB1B8A1BA1298E78140D837
 */
public class NodeValueCalculatorTest extends DriverTestBase {

	/** Test whether integration works. */
	public void testIntegration() throws DriverException {

		runDriver("NodeValueCalculator.cqb");
		NodeCreator child = (NodeCreator) DataSinkProcessor.accessData("child")
				.get(0);
		NodeCreator root = (NodeCreator) DataSinkProcessor.accessData("root")
				.get(0);

		assertEquals(6., child.getValue("six"));
		assertEquals(6., root.getValue("six"));

		assertEquals(11., child.getValue("aplusb"));
		assertNull(root.getValue("aplusb"));

		assertNull(child.getValue("c2+1")); // because of string value
		assertEquals(10., root.getValue("c2+1"));
	}
}