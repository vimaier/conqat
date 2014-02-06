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
package org.conqat.engine.core.driver.instance;

import org.conqat.engine.core.driver.error.DriverException;
import org.conqat.engine.core.driver.processors.DataSinkProcessor;
import org.conqat.engine.core.driver.processors.DeepClonedType;
import org.conqat.engine.core.logging.testutils.DriverTestBase;

/**
 * Test for deep cloning of processor results.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @levd.rating GREEN Hash: DEB908D4B7633D4B65B91C675842FDA4
 * 
 */
public class DeepCloningTest extends DriverTestBase {

	/** Test if clone avoidance works. */
	public void testCloningOneConsumer() throws DriverException {
		runDriver("cloning-01.cqb");
		DeepClonedType dct = (DeepClonedType) DataSinkProcessor.accessData(
				"noclone").get(0);
		assertEquals(0, dct.numberOfDeepCloneCalls);
	}

	/**
	 * tests whether results are really cloned (the right number of times).
	 */
	public void testCloningOfResults() throws DriverException {
		runDriver("cloning-02.cqb");
		DeepClonedType dct = (DeepClonedType) DataSinkProcessor.accessData(
				"clone").get(0);

		// expecting 4 clones as result is used 5 times
		assertEquals(4, dct.numberOfDeepCloneCalls);
	}

	/** Test if cloning is reduced in case of disabled processors.. */
	public void testCloningConditionals() throws DriverException {
		runDriver("cloning-03.cqb");
		DeepClonedType dct = (DeepClonedType) DataSinkProcessor.accessData(
				"clone").get(0);

		// without disablement we would expect 2 clonings; disabling two
		// processors can get rid of 1 or 2 of them (depending on execution
		// order)
		assertTrue(dct.numberOfDeepCloneCalls < 2);
	}
}