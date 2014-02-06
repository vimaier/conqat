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
 * Test the DoubleCalculator class.
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @levd.rating GREEN Hash: 9E8823C67B20CB5F3579FD7E9FBEABAA
 */
public class BeanShellProcessorTest extends DriverTestBase {

	/** Test whether integration works. */
	public void testIntegration() throws DriverException  {

		runDriver("BeanShellProcessor.cqb");

		// first bsh
		Object result1 = DataSinkProcessor.accessData("bsh1").get(0);
		assertTrue(result1 instanceof Double);
		assertEquals(result1, 8.);

		// second bsh
		Object result2 = DataSinkProcessor.accessData("bsh2").get(0);
		assertTrue(result2 instanceof NodeCreator);
		NodeCreator root = (NodeCreator) result2;
		assertEquals(root.getValue("out"), "someValue8.0");
		assertTrue(root.hasChildren());
		assertEquals(root.getChildren()[0].getValue("out"), "valueOne8.0");
	}
}