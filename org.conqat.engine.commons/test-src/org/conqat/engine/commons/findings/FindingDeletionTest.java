/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2011 the ConQAT Project                                   |
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

import org.conqat.engine.commons.findings.location.ElementLocation;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.node.StringSetNode;
import org.conqat.lib.commons.test.CCSMTestCaseBase;

/**
 * Tests deletion of findings in multiple lists. This is a test affecting the
 * entire findings subsystem. Also see CR#4477.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43290 $
 * @ConQAT.Rating GREEN Hash: 41155D73B5F31CB3652834450B3EAE0D
 */
public class FindingDeletionTest extends CCSMTestCaseBase {

	/** Finding category used for testing. */
	private FindingCategory category;

	/** Finding group used for testing. */
	private FindingGroup group;

	/** Finding used for testing. */
	private Finding finding1;

	/** Finding used for testing. */
	private Finding finding2;

	/** Finding list used for testing. */
	private FindingsList list1;

	/** Finding list used for testing. */
	private FindingsList list2;

	/**
	 * {@inheritDoc}
	 * <p>
	 * This creates one category with one group and two findings in it. Two
	 * findings lists are created which initially contain both findings each.
	 */
	@Override
	protected void setUp() throws Exception {
		IConQATNode root = new StringSetNode();
		category = NodeUtils.getFindingReport(root).getOrCreateCategory(
				"category");
		group = category.getOrCreateFindingGroup("group");
		finding1 = group
				.createFinding(new ElementLocation("location1", "path1"));
		finding2 = group
				.createFinding(new ElementLocation("location2", "path2"));

		list1 = NodeUtils.getOrCreateFindingsList(root, "key1");
		list2 = NodeUtils.getOrCreateFindingsList(root, "key2");

		list1.add(finding1);
		list1.add(finding2);

		list2.add(finding1);
		list2.add(finding2);
	}

	/** Tests deletion in lists. */
	public void testFindingsDeletion() {
		// remove from list only affects the list but not the report
		list1.remove(1);
		assertEquals(1, list1.size());
		assertEquals(2, list2.size());

		// removal from report affects all lists.
		finding1.remove();
		assertEquals(0, list1.size());
		assertEquals(1, list2.size());
		assertEquals("path2", list2.get(0).getLocation().getUniformPath());

		// repeated deletions are not lost due to caching
		finding2.remove();
		assertEquals(0, list1.size());
		assertEquals(0, list2.size());
	}

	/** Test deletion of a group. */
	public void testGroupDeletion() {
		group.remove();

		// deleting the group should kill all findings
		assertEquals(0, list1.size());
		assertEquals(0, list2.size());
	}

	/** Test deletion of a category. */
	public void testCategoryDeletion() {
		category.remove();

		// deleting the category should kill all findings
		assertEquals(0, list1.size());
		assertEquals(0, list2.size());
	}
}
