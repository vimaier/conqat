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
package org.conqat.engine.java.junit;

import java.io.File;

import org.conqat.lib.commons.clone.DeepCloneException;
import org.conqat.lib.commons.test.CCSMTestCaseBase;
import org.conqat.engine.commons.testutils.NodeTestUtils;
import org.conqat.engine.core.core.ConQATException;

/**
 * Test for Junit scope.
 * 
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @levd.rating GREEN Hash: 7DB8A8364BC9B7FE4FFB635634ED3E02
 */
public class JUnitResultScopeTest extends CCSMTestCaseBase {

	/** Node under test. */
	private JUnitResultNode node;

	/** Read test node from test data. */
	@Override
	public void setUp() throws ConQATException {
		JUnitResultScope scope = new JUnitResultScope();
		File file = useTestFile("TESTS-TestSuites_01.xml");
		scope.addFilename(file.getPath());
		node = scope.process();
	}

	/** Test if deep cloning is implemented properly. */
	public void testDeepCloning() throws DeepCloneException {
		NodeTestUtils.testDeepCloning(node);
	}

	/** Test if all suites were read. */
	public void testTestSuites() {
		assertEquals(40, node.getChildren().length);
	}
}