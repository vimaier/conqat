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
package org.conqat.engine.commons.filter;

import java.util.Collection;

import org.conqat.engine.commons.node.IRemovableConQATNode;
import org.conqat.engine.commons.pattern.PatternList;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * This filter works on string lists available at the given key. It checks all
 * strings against (optional) pattern lists. Each node is marked as included if
 * at least one string in the list matches one of the include patterns (or if
 * the include patterns are not given) and as excluded if at least one string in
 * the list matches one of the exclude patterns. The decision is then based on
 * these include/exclude values, where exclusion is stronger than inclusion.
 * 
 * @author Tilman Seifert
 * @author Benjamin Hummel
 * @author $Author: hummelb $
 * @version $Rev: 37013 $
 * @levd.rating GREEN Hash: 9E94A88A3105BDE783B85BB473CCADC7
 */
@AConQATProcessor(description = "This filter works on string lists available at the given key. "
		+ "It checks all strings against (optional) pattern lists. Each node is marked as included if "
		+ "at least one string in the list matches one of the include patterns (or if the include "
		+ "patterns are not given) and as excluded if at least one string in the list matches one of the"
		+ "exclude patterns. The decision is then based on these include/exclude values, where exclusion "
		+ "is stronger than inclusion.")
public class StringListFilter extends
		KeyBasedFilterBase<Collection<String>, IRemovableConQATNode> {

	/** Include pattern. */
	private PatternList includePattern = null;

	/** Include pattern. */
	private PatternList excludePattern = null;

	/** Set the include pattern. */
	@AConQATParameter(name = "include", maxOccurrences = 1, description = ""
			+ "Sets the include patterns used. If this is omitted, everything is included which is not excluded.")
	public void setIncludePattern(
			@AConQATAttribute(name = "pattern-list", description = "The pattern list describing string to include (compared with matches).")
			PatternList pattern) {
		this.includePattern = pattern;
	}

	/** Set the exclude pattern. */
	@AConQATParameter(name = "exclude", maxOccurrences = 1, description = ""
			+ "Sets the exclude patterns used.")
	public void setExcludePattern(
			@AConQATAttribute(name = "pattern-list", description = "The pattern list describing strings to exclude (compared with matches).")
			PatternList pattern) {
		this.excludePattern = pattern;
	}

	/** {@inheritDoc} */
	@Override
	protected boolean isFilteredForValue(Collection<String> value) {
		boolean include = false;
		boolean exclude = false;

		for (String string : value) {
			if (includePattern != null && includePattern.matchesAny(string)) {
				include = true;
			}
			if (excludePattern != null && excludePattern.matchesAny(string)) {
				exclude = true;
			}
		}

		if (includePattern == null) {
			include = true;
		}

		return include && !exclude;
	}
}