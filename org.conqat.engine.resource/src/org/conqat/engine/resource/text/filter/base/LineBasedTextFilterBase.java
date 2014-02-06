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
package org.conqat.engine.resource.text.filter.base;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for filters working on the line level.
 * 
 * @author $Author: juergens $
 * @version $Rev: 40976 $
 * @ConQAT.Rating GREEN Hash: 4AB1E5244B696C6127C6B54D5BC65556
 */
public abstract class LineBasedTextFilterBase extends TextFilterBase {

	/** {@inheritDoc} */
	@Override
	public List<Deletion> getDeletions(String s,
			String originUniformPath) {
		List<Deletion> deletions = new ArrayList<Deletion>();

		int lastPos = 0;
		int pos = s.indexOf('\n');
		while (pos >= 0) {
			getDeletionsForLine(s, lastPos, pos, deletions);
			lastPos = pos + 1;
			pos = s.indexOf('\n', lastPos);
		}
		getDeletionsForLine(s, lastPos, s.length(), deletions);

		logDeletions(deletions, originUniformPath);

		return deletions;
	}

	/**
	 * Inserts deletions for a single line into the deletions list. The line is
	 * not given as an explicit string, but rather as the full string and
	 * (inclusive) start and (exclusive) end indexes (parameters are just as for
	 * {@link String#substring(int, int)} method).
	 */
	protected abstract void getDeletionsForLine(String s, int start, int end,
			List<Deletion> deletions);
}