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

import org.conqat.engine.core.driver.error.DriverException;
import org.conqat.engine.core.driver.error.EDriverExceptionType;

/**
 * Test for {@link TopSorter}.
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @levd.rating GREEN Hash: 9E109CDF952B4320C4F396F9D7986B88
 */
public class TopSorterTest extends SpecificationTestBase {

	/** Check if sorting works for trivial cases. */
	public void testTrivialCases() throws DriverException {
		loadConfig("topsort-01.cqb").initialize();
	}

	/** Check if sorting works for "complex" case. */
	public void testComplexCase() throws DriverException {
		loadConfig("topsort-02.cqb").initialize();
	}

	/** Check if sorting finds cycles. */
	public void testCycle() throws DriverException {
		BlockSpecification spec = loadConfig("topsort-03.cqb");
		try {
			spec.initialize();
			fail("Expected exception!");
		} catch (DriverException e) {
			e.printStackTrace();
			assertEquals(EDriverExceptionType.CYCLIC_DEPENDENCIES, e.getType());
		}
	}
}