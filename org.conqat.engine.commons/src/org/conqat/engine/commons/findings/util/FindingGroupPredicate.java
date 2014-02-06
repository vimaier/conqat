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

import java.util.regex.Pattern;

import org.conqat.engine.commons.CommonUtils;
import org.conqat.engine.commons.findings.Finding;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.traversal.ConQATNodePredicateBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.collections.PairList;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: steidl $
 * @version $Rev: 46658 $
 * @ConQAT.Rating GREEN Hash: E221457370AB52D830925BB49C5E10F3
 */
@AConQATProcessor(description = "A predicate that returns true for findings that are in one of the given finding groups.")
public class FindingGroupPredicate extends ConQATNodePredicateBase {

	/** Pairs of pattern for category and group. */
	private final PairList<Pattern, Pattern> categoryAndGroupPattern = new PairList<Pattern, Pattern>();

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "match-pattern", minOccurrences = 1, description = "Defines match pattern for the category and group. "
			+ "A finding is matched by the predicate if at least one of the given category/group pattern pairs matches.")
	public void addPatterns(
			@AConQATAttribute(name = "category", description = "A Java regular expression for the category name.") String categoryRegex,
			@AConQATAttribute(name = "group", defaultValue = ".*", description = "A Java regular expression for the group name.") String groupRegex)
			throws ConQATException {
		categoryAndGroupPattern.add(CommonUtils.compilePattern(categoryRegex),
				CommonUtils.compilePattern(groupRegex));
	}

	/** {@inheritDoc} */
	@Override
	public boolean isContained(IConQATNode node) {
		if (!(node instanceof Finding)) {
			return false;
		}

		Finding finding = (Finding) node;
		String categoryName = finding.getParent().getParent().getName();
		String groupName = finding.getParent().getName();

		for (int i = 0; i < categoryAndGroupPattern.size(); ++i) {
			Pattern categoryPattern = categoryAndGroupPattern.getFirst(i);
			Pattern groupPattern = categoryAndGroupPattern.getSecond(i);
			if (categoryPattern.matcher(categoryName).matches()
					&& groupPattern.matcher(groupName).matches()) {
				return true;
			}
		}
		return false;
	}

}
