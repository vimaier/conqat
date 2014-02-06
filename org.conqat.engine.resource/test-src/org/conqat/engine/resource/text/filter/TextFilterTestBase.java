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
package org.conqat.engine.resource.text.filter;

import static org.conqat.lib.commons.string.StringUtils.CR;

import java.util.List;

import org.conqat.engine.resource.test.ResourceProcessorTestCaseBase;
import org.conqat.engine.resource.text.filter.base.Deletion;
import org.conqat.engine.resource.text.filter.base.ITextFilter;
import org.conqat.lib.commons.assertion.CCSMPre;

/**
 * Base class for implementations of {@link ITextFilter}.
 * 
 * @author $Author: juergens $
 * @version $Rev: 40963 $
 * @ConQAT.Rating GREEN Hash: 5BFA0769ACCB1320063E545A1C2E06C9
 */
public abstract class TextFilterTestBase extends ResourceProcessorTestCaseBase {

	/**
	 * Compares the content of the given deletions with the intervals (which are
	 * actually interpreted as pairs). The comparison is performed on the string
	 * level, as this simplifies the comparison.
	 */
	protected static void assertCleanDeletions(List<Deletion> deletions,
			int... intervals) {
		CCSMPre.isTrue(intervals.length % 2 == 0, "Expecting start/end pairs!");
		deletions = Deletion.compactDeletions(deletions);

		StringBuilder actual = new StringBuilder();
		for (Deletion deletion : deletions) {
			actual.append(deletion.getStartOffset() + " - "
					+ deletion.getEndOffset() + CR);
		}

		StringBuilder expected = new StringBuilder();
		for (int i = 0; i < intervals.length; i += 2) {
			expected.append(intervals[i] + " - " + intervals[i + 1] + CR);
		}

		assertEquals(expected.toString(), actual.toString());
	}
}