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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.resource.analysis.InvalidCharacterAnalyzer;
import org.conqat.engine.resource.text.filter.base.Deletion;
import org.conqat.engine.resource.text.filter.base.TextFilterBase;
import org.conqat.lib.commons.math.Range;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: juergens $
 * @version $Rev: 40961 $
 * @ConQAT.Rating GREEN Hash: 0C04AA5A1B43CCFA8353C0BEE6308011
 */
@AConQATProcessor(description = "A filter that removes all non-ASCII and control characters from the text. "
		+ "The filtered parts do not create filter gaps.")
public class InvalidCharacterTextFilter extends TextFilterBase {

	/** {@inheritDoc} */
	@Override
	public List<Deletion> getDeletions(String s, String originUniformPath) {

		Set<Range> validRanges = new HashSet<Range>();
		InvalidCharacterAnalyzer.addDefaultValidCharacterRanges(validRanges);

		List<Deletion> deletions = new ArrayList<Deletion>();
		char[] chars = s.toCharArray();
		for (int i = 0; i < chars.length; ++i) {
			if (!InvalidCharacterAnalyzer.isValid(chars[i], validRanges)) {
				Deletion deletion = new Deletion(i, i + 1, false);
				deletions.add(deletion);
				logDeletion(deletion, originUniformPath);
			}
		}
		return deletions;
	}
}