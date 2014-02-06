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

/**
 * Tests for {@link ConQATDocTagletProcessor}.
 * 
 * @author hummelb
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 * @levd.rating GREEN Hash: 5913D4652F44AA18A870A658A2FC8263
 */
public class ConQATDocTagletProcessorTest extends SpecificationTestBase {

	/** Tests the connDoc tag. */
	public void testConnDoc() throws DriverException {
		assertDocumentation("conqatdoc02");
	}

	/** Tests the childDoc tag. */
	public void testChildDoc() throws DriverException {
		assertDocumentation("conqatdoc03");
	}

	/** Both tests are constructed to allow the same set of checks to be used. */
	private void assertDocumentation(String blockName) throws DriverException {
		BlockSpecification block = loadBlock(blockName);
		block.initialize();

		assertEquals("DocPA", block.getParameter("B").getDoc());
		assertEquals("DocAb", block.getParameter("B").getAttributes()[1]
				.getDoc());
		assertEquals("DocAa DocAb", block.getParameter("B").getAttributes()[0]
				.getDoc());
		assertEquals("FooOut1Bar", block.getOutputs()[0].getDoc());
	}

}