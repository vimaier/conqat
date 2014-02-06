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

import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.text.filter.base.Deletion;
import org.conqat.engine.resource.text.filter.base.ITextFilter;
import org.conqat.lib.commons.assertion.CCSMPre;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Tests the {@link ColumnTextFilter}
 * 
 * @author $Author: juergens $
 * @version $Rev: 40963 $
 * @ConQAT.Rating GREEN Hash: EBFB65F5A526684B88ABD1525CFE8323
 */
public class ColumnTextFilterTest extends TextFilterTestBase {

	/** Tests the very simple use case. */
	public void testSimple() throws ConQATException {
		assertCleanDeletions(calculateDeletions("abc\ndef", 1, 2), 1, 2, 5, 6);
	}

	/** Tests correctness in the presence of empty lines. */
	public void testEmptyLines() throws ConQATException {
		assertCleanDeletions(calculateDeletions("abc\n\ndef", 1, 2), 1, 2, 6, 7);
	}

	/** Tests trimming at the end (i.e. remainder of line can not be swallowed). */
	public void testTrimEnd() throws ConQATException {
		assertCleanDeletions(calculateDeletions("012\n4\n6789AB", 2, 20), 2, 3,
				8, 12);
	}

	/** Tests trimming of all text. */
	public void testTrimAll() throws ConQATException {
		assertCleanDeletions(calculateDeletions("012\n4\n6789AB", 0, 20), 0, 3,
				4, 5, 6, 12);
	}

	/** Tests multiple columns. */
	public void testMultiColumns() throws ConQATException {
		assertCleanDeletions(calculateDeletions("0123\n5678", 0, 1, 2, 3), 0,
				1, 2, 3, 5, 6, 7, 8);
	}

	/**
	 * Calculates the filter's deletions for the given string. The filter is
	 * initialized by the columns array, which provides start/end pairs.
	 */
	private List<Deletion> calculateDeletions(String s,
			int... columns) throws ConQATException {
		CCSMPre.isTrue(columns.length % 2 == 0, "Expecting start/end pairs!");

		List<String> strips = new ArrayList<String>();
		for (int i = 0; i < columns.length; i += 2) {
			strips.add("strip=(start=" + columns[i] + ",end=" + columns[i + 1]
					+ ")");
		}

		ITextFilter filter = (ITextFilter) executeProcessor(
				ColumnTextFilter.class, "(" + StringUtils.concat(strips, ",")
						+ ")");
		return filter.getDeletions(s,
				ColumnTextFilterTest.class.getCanonicalName());
	}
}