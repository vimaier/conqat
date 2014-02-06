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
package org.conqat.engine.core.driver;

import java.util.List;

import org.conqat.engine.core.driver.error.DriverException;
import org.conqat.engine.core.driver.error.EDriverExceptionType;
import org.conqat.engine.core.driver.processors.DataSinkProcessor;
import org.conqat.engine.core.logging.testutils.DriverTestBase;

/**
 * These are tests covering the entire system.
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @levd.rating GREEN Hash: 802E49781BFFB1BEDE2F91057E85B0E1
 */
public class SystemTest extends DriverTestBase {

	/** Test a very simple configuration. */
	public void testSimpleRun() throws DriverException {
		runDriver("systest-01.cqb");

		assertSink("a", "my_test_string");
		assertSink("b", "another_test_string");
		assertSink("c", "my_test_string");
	}

	/** Test a configuration having a simple block. */
	public void testSimpleBlock() throws DriverException {
		runDriver("systest-02.cqb");

		assertSink("a.out", "my_test_string");
		assertSink("b.out", "my_test_string");
	}

	/** Test a configuration with conditionals. */
	public void testCondition() throws DriverException {
		runDriver("systest-condition.cqb");

		assertSink("a.out", "my_test_string");
		assertNull(DataSinkProcessor.accessData("b.out"));
	}

	/** Test the case of block specification depending cyclically on each other. */
	public void testCyclicDependencies() {
		try {
			runDriver("systest-cyclic.cqb");
			fail("Expected exception");
		} catch (DriverException e) {
			assertEquals(EDriverExceptionType.CYCLIC_BLOCK_DEPENDENCY, e
					.getType());
		}
	}

	/** For CR #1932 */
	public void testBlockParameterWithTwoAttributes() throws DriverException {
		runDriver("block-with-parameter-with-two-attributes.cqb",
				"block-with-parameter-with-two-attributes.cqb.properties");

		assertSink("param.attribA", "valueA");
		assertSink("param.attribB", "valueB");
	}

	/** Check the values collected by the DataSinkProcessor. */
	private static void assertSink(String key, Object... values) {
		List<Object> stored = DataSinkProcessor.accessData(key);
		assertNotNull(stored);
		assertEquals(values.length, stored.size());
		for (int i = 0; i < values.length; ++i) {
			assertEquals(values[i], stored.get(i));
		}
	}
}