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
package org.conqat.engine.code_clones.detection.filter;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.code_clones.core.Clone;
import org.conqat.engine.code_clones.core.CloneClass;
import org.conqat.engine.code_clones.core.IdProvider;
import org.conqat.engine.code_clones.detection.CloneDetectionResultElement;
import org.conqat.engine.commons.findings.location.TextRegionLocation;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.logging.testutils.ProcessorInfoMock;
import org.conqat.engine.resource.text.TextElement;
import org.conqat.lib.commons.date.DateUtils;
import org.conqat.lib.commons.region.Region;
import org.conqat.lib.commons.test.CCSMTestCaseBase;

/**
 * Test case for {@link DuplicateGapFilter}.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43764 $
 * @ConQAT.Rating GREEN Hash: E6E19D799208E2EFF4E9BDFA3CF265CE
 */
public class DuplicateGapFilterTest extends CCSMTestCaseBase {

	/** Used to create test data ids */
	private final IdProvider idProvider = new IdProvider();

	/** Tests filtering of simple duplicates */
	public void testFilterSimpleDuplicates() {
		// create input
		List<CloneClass> cloneClasses = new ArrayList<CloneClass>();
		cloneClasses.add(createCloneClass(new String[] { "A", "B" }, true));
		cloneClasses.add(createCloneClass(new String[] { "A", "B" }, true));

		cloneClasses = runFilter(cloneClasses);
		assertEquals(1, cloneClasses.size());
	}

	/**
	 * Tests if a clone class that contains a duplicate gaps is also removed, if
	 * clone class that subsumes it comes after it in the list
	 */
	public void testFilterDifferentCardinalitiesInverseOrder() {
		// create input
		List<CloneClass> cloneClasses = new ArrayList<CloneClass>();
		cloneClasses.add(createCloneClass(new String[] { "A", "B" }, true));
		cloneClasses
				.add(createCloneClass(new String[] { "A", "B", "C" }, true));

		cloneClasses = runFilter(cloneClasses);
		assertEquals(1, cloneClasses.size());
	}

	/**
	 * Tests if a clone class that contains a duplicate gaps is also removed, if
	 * clone class that subsumes it comes before it in the list
	 */
	public void testFilterDifferentCardinalities() {
		// create input
		List<CloneClass> cloneClasses = new ArrayList<CloneClass>();
		cloneClasses
				.add(createCloneClass(new String[] { "A", "B", "C" }, true));
		cloneClasses.add(createCloneClass(new String[] { "A", "B" }, true));

		cloneClasses = runFilter(cloneClasses);
		assertEquals(1, cloneClasses.size());
	}

	/** Tests that clone classes without gaps are not filtered */
	public void testDontFilterUngappedClones() {
		// create input
		List<CloneClass> cloneClasses = new ArrayList<CloneClass>();
		cloneClasses.add(createCloneClass(new String[] { "A", "B" }, false));
		cloneClasses.add(createCloneClass(new String[] { "A", "B" }, false));

		cloneClasses = runFilter(cloneClasses);
		assertEquals(2, cloneClasses.size());
	}

	/** Runs the filter under test */
	private List<CloneClass> runFilter(List<CloneClass> cloneClasses) {
		// filter
		CloneDetectionResultElement result = new CloneDetectionResultElement(
				DateUtils.getNow(), new TextElement(null,
						Charset.defaultCharset()), cloneClasses);
		DuplicateGapFilter filter = new DuplicateGapFilter();
		filter.init(new ProcessorInfoMock());
		filter.setRoot(result);

		try {
			filter.process();
		} catch (ConQATException e) {
			fail("Filter execution failed: " + e.getMessage());
		}

		return result.getList();
	}

	/**
	 * Creates a clone class for a number of files with optional fixed gap
	 * positions
	 */
	private CloneClass createCloneClass(String[] locations, boolean insertGaps) {
		CloneClass cloneClass = new CloneClass(10, idProvider.provideId());

		for (String location : locations) {
			Clone clone = new Clone(idProvider.provideId(), cloneClass,
					new TextRegionLocation(location, location, 1, 2, 3, 4), 8,
					12, "Fingerprint", 1);
			if (insertGaps) {
				clone.addGap(new Region(5, 6));
			}
			cloneClass.add(clone);
		}

		return cloneClass;
	}
}