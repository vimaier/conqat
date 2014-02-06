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

import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.text.filter.base.Deletion;
import org.conqat.engine.resource.text.filter.base.LineBasedTextFilterBase;
import org.conqat.lib.commons.collections.PairList;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: juergens $
 * @version $Rev: 40961 $
 * @ConQAT.Rating GREEN Hash: 07059D4818BD4550ACDBE34698B38CDC
 */
@AConQATProcessor(description = "Filters dedicated colums from the text. "
		+ "This filter can be used as a preprocessor for analysis of "
		+ "languages whose syntax is column-oriented, such as PL/I. "
		+ "The filtered parts do not create filter gaps.")
public class ColumnTextFilter extends LineBasedTextFilterBase {

	/**
	 * List of column ranges that are to be stripped (start, end); end is
	 * exclusive.
	 */
	private final PairList<Integer, Integer> stripRanges = new PairList<Integer, Integer>();

	/** Add a column range */
	@AConQATParameter(name = "strip", minOccurrences = 1, description = ""
			+ "Add range of columns that get replaced by whitespace. Constraints: start >= 0, end >= start.")
	public void addRange(
			@AConQATAttribute(name = "start", description = "Start column (inclusive)") int start,
			@AConQATAttribute(name = "end", description = "End column (exclusive)") int end)
			throws ConQATException {
		if (start < 0) {
			throw new ConQATException(
					"Column range must have positive start value, but was: "
							+ start);
		}
		if (end < start) {
			throw new ConQATException("Negative size column range: start: "
					+ start + ", end: " + end);
		}

		stripRanges.add(start, end);
	}

	/** {@inheritDoc} */
	@Override
	protected void getDeletionsForLine(String s, int start, int end,
			List<Deletion> deletions) {
		int length = end - start;
		for (int i = 0; i < stripRanges.size(); ++i) {
			if (stripRanges.getFirst(i) < length) {
				deletions.add(new Deletion(start + stripRanges.getFirst(i),
						start + Math.min(stripRanges.getSecond(i), length),
						false));
			}
		}
	}
}