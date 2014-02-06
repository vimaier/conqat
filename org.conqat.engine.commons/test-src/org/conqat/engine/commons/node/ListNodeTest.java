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
package org.conqat.engine.commons.node;

import org.conqat.lib.commons.clone.DeepCloneException;
import org.conqat.lib.commons.test.CCSMTestCaseBase;
import org.conqat.engine.commons.testutils.NodeTestUtils;

/**
 * Test case for {@link ListNode}
 * 
 * @author Elmar Juergens
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 * @levd.rating GREEN Hash: 398274763474013CF7DED30FB8DFE29F
 */
public class ListNodeTest extends CCSMTestCaseBase {

	/**
	 * Tests deep cloning. This needs to be overriden as we have special
	 * situation where we need to check if the deep cloning of the ConQAT nodes
	 * as well as the model they refer to are properly cloned.
	 */
	public void testDeepClone() throws DeepCloneException {

		ListNode root = new ListNode();
		
		ListNode child = new ListNode("child");
		child.setValue("key1", "value1");
		child.setValue("key2", "value2");
		root.addChild(child);

		NodeTestUtils.testDeepCloning(root);
	}

}