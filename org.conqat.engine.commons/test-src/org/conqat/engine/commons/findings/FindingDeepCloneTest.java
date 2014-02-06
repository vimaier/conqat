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
package org.conqat.engine.commons.findings;

import java.util.IdentityHashMap;

import junit.framework.TestCase;
import org.conqat.lib.commons.clone.DeepCloneException;
import org.conqat.lib.commons.collections.IIdProvider;
import org.conqat.lib.commons.collections.IdComparator;
import org.conqat.lib.commons.test.DeepCloneTestUtils;
import org.conqat.engine.commons.findings.location.QualifiedNameLocation;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.ListNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.testutils.NodeTestUtils.ConQATNodeIdProvider;

/**
 * Test case for deep cloning of nodes when using findings.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43290 $
 * @ConQAT.Rating GREEN Hash: B97A5BA0064241B04DFA025912E3BA5B
 */
public class FindingDeepCloneTest extends TestCase {

	/** The key used to store findings lists. */
	private static final String KEY = "findings";

	/** Tests deep cloning. */
	public void testDeepCloning() throws DeepCloneException {

		ListNode root = new ListNode("a");
		ListNode n1 = new ListNode("b");
		ListNode n2 = new ListNode("c");
		ListNode n3 = new ListNode("d");
		ListNode n4 = new ListNode("e");
		ListNode n5 = new ListNode("f");
		root.addChild(n1);
		root.addChild(n2);
		n2.addChild(n3);
		n2.addChild(n4);
		n2.addChild(n5);

		FindingReport report = NodeUtils.getFindingReport(root);
		Finding f1 = report
				.getOrCreateCategory("cat1")
				.createFindingGroup("g1")
				.createFinding(
						new QualifiedNameLocation("ql", "location", "path"));
		Finding f2 = report
				.getOrCreateCategory("cat2")
				.createFindingGroup("g2")
				.createFinding(
						new QualifiedNameLocation("ql", "location", "path"));

		NodeUtils.getOrCreateFindingsList(n3, KEY).add(f1);
		NodeUtils.getOrCreateFindingsList(n5, KEY).add(f2);

		ListNode clone = root.deepClone();

		DeepCloneTestUtils.testDeepCloning(root, clone,
				new ConQATNodeIdProvider(), "org.conqat");

		// we have to manually check actual findings as keys are not visible to
		// DeepCloneTestUtils.
		IdentityHashMap<Object, Object> cloneMap = DeepCloneTestUtils
				.buildCloneMap(root, clone, new IdComparator<String, Object>(
						new ConQATNodeIdProvider()), "org.conqat");

		ListNode n3clone = (ListNode) cloneMap.get(n3);
		ListNode n5clone = (ListNode) cloneMap.get(n5);

		FindingsList fl3 = NodeUtils.getFindingsList(n3clone, KEY);
		FindingsList fl5 = NodeUtils.getFindingsList(n5clone, KEY);

		assertNotNull(fl3);
		assertNotNull(fl5);

		assertEquals(1, fl3.size());
		assertEquals(1, fl5.size());

		IdentityHashMap<Object, Object> reportMap = DeepCloneTestUtils
				.buildCloneMap(report, NodeUtils.getFindingReport(clone),
						new IdComparator<String, Object>(
								new IIdProvider<String, Object>() {
									@Override
									public String obtainId(Object object) {
										if (object instanceof IConQATNode) {
											return ((IConQATNode) object)
													.getId();
										}
										return Integer.toString(object
												.hashCode());
									}
								}), "org.conqat");

		assertNotSame(f1, reportMap.get(f1));
		assertNotSame(f2, reportMap.get(f2));

		assertSame(reportMap.get(f1), fl3.get(0));
		assertSame(reportMap.get(f2), fl5.get(0));
	}
}