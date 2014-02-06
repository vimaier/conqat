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

import java.util.List;

import org.conqat.engine.commons.pattern.PatternList;
import org.conqat.engine.commons.pattern.PatternListDefTest;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.text.filter.base.Deletion;
import org.conqat.engine.resource.text.filter.base.ITextFilter;

/**
 * Tests the {@link RegexTextFilter}.
 * 
 * @author $Author: juergens $
 * @version $Rev: 40963 $
 * @ConQAT.Rating GREEN Hash: 894471D974B9BC8A5DF68E8CFF4E7102
 */
public class RegexTextFilterTest extends TextFilterTestBase {

	/** Tests basic operation. */
	public void testSimple() throws ConQATException {
		assertCleanDeletions(calculateDeletions("012345\n789", "[238]+"), 2, 4,
				8, 9);
	}

	/** Tests with overlapping patterns. */
	public void testOverlapping() throws ConQATException {
		assertCleanDeletions(
				calculateDeletions("abcabcdefxyyz", "b", "abc", "c.?.?f"), 0, 9);
	}

	/** Calculates the filter's deletions for the given string. */
	private List<Deletion> calculateDeletions(String string, String... patterns)
			throws ConQATException {
		PatternList patternList = PatternListDefTest
				.createPatternList(patterns);

		ITextFilter filter = (ITextFilter) executeProcessor(
				RegexTextFilter.class, "(patterns=(ref=", patternList, "))");
		return filter.getDeletions(string,
				RegexTextFilterTest.class.getCanonicalName());
	}

}