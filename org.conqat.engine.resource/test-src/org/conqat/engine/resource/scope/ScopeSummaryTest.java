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
package org.conqat.engine.resource.scope;

import org.conqat.engine.resource.test.ResourceProcessorTestCaseBase;
import org.conqat.engine.resource.text.ITextResource;

import org.conqat.engine.commons.node.StringSetNode;

/**
 * Test case for {@link ScopeSummary}.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @levd.rating GREEN Hash: C937F08599DBC498E709398996BBB189
 */
public class ScopeSummaryTest extends ResourceProcessorTestCaseBase {

	/** Used to reproduce bug #1271. */
	public void testBug1271() throws Exception {
		ITextResource element = createTextScope(useCanonicalTestFile(""),
				new String[] { "*" }, null);
		StringSetNode result = (StringSetNode) executeProcessor(
				ScopeSummary.class, "(input=(ref=", element, "))");
		assertEquals("tst", result.getChildren()[0].getName());
		assertEquals(1, result.getChildren().length);
	}
}