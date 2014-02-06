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

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.text.filter.base.Deletion;
import org.conqat.engine.resource.text.filter.base.ITextFilter;

/**
 * Tests the {@link InvalidCharacterTextFilter}
 * 
 * @author $Author: juergens $
 * @version $Rev: 40963 $
 * @ConQAT.Rating GREEN Hash: 61A374FFD59A21E3B42E4063668770AC
 */
public class InvalidCharacterTextFilterTest extends TextFilterTestBase {

	/** Tests basic operation. */
	public void testSimple() throws ConQATException {
		assertCleanDeletions(
				calculateDeletions("ab\u0555cde\u0555\u0555\u0555"), 2, 3, 6, 9);
	}

	/** Calculates the filter's deletions for the given string. */
	private List<Deletion> calculateDeletions(String s)
			throws ConQATException {
		ITextFilter filter = (ITextFilter) executeProcessor(
				InvalidCharacterTextFilter.class, "()");
		return filter.getDeletions(s,
				InvalidCharacterTextFilterTest.class.getCanonicalName());
	}
}