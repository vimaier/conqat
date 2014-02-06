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
package org.conqat.engine.commons.findings.util;

import org.conqat.engine.commons.findings.FindingReport;
import org.conqat.engine.commons.findings.location.ElementLocation;
import org.conqat.engine.commons.traversal.ConQATNodePredicateBase;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.logging.testutils.ConQATProcessorTestCaseBase;

/**
 * Tests the {@link FindingGroupPredicate}.
 * 
 * @author $Author: steidl $
 * @version $Rev: 46658 $
 * @ConQAT.Rating GREEN Hash: 61EB209F8690D1F10269CE77D9CD178D
 */
public class FindingGroupPredicateTest extends ConQATProcessorTestCaseBase {

	/** A location used for findings creation in the test. */
	private final ElementLocation dummyLocation = new ElementLocation("test",
			"test");

	/** A report used for findings creation in the test. */
	private final FindingReport report = new FindingReport();

	/** Performs basic tests on the predicate. */
	public void test() throws ConQATException {
		ConQATNodePredicateBase predicate = (ConQATNodePredicateBase) executeProcessor(
				FindingGroupPredicate.class,
				"('match-pattern'=(category='.*CatValid.*', group='Group.*Valid'), "
						+ "'match-pattern'=(category='ExplicitCategory', group='ExplicitGroup'))");

		assertFalse("should not match non-findings",
				predicate.isContained(report));

		assertFalse(contained(predicate, "Foo", "Bar"));
		assertFalse(contained(predicate, "CatValid", "Bar"));
		assertFalse(contained(predicate, "Foo", "GroupValid"));
		assertTrue(contained(predicate, "CatValid123", "Group123Valid"));
		assertTrue(contained(predicate, "ExplicitCategory", "ExplicitGroup"));
	}

	/**
	 * Returns whether a finding with given category/group is contained in the
	 * predicate.
	 */
	private boolean contained(ConQATNodePredicateBase predicate,
			String category, String group) {
		return predicate.isContained(report.getOrCreateCategory(category)
				.getOrCreateFindingGroup(group).createFinding(dummyLocation));
	}
}
