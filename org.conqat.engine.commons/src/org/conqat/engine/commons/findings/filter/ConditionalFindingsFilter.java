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
package org.conqat.engine.commons.findings.filter;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.findings.Finding;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.pattern.PatternList;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;

// TODO (EJ) rename include to retain and exclude to remove, to make usage more intuitive 
/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: steidl $
 * @version $Rev: 43636 $
 * @ConQAT.Rating GREEN Hash: 26B501FEF47AD6DBCB78B78F53DA1D64
 */
@AConQATProcessor(description = "This processor filters the findings in the specified groups and "
		+ "categories for all elements where the modification flag (stored at key) is false."
		+ FindingsFilterBase.PROCESSOR_DOC_SUFFIX)
public class ConditionalFindingsFilter extends FindingsFilterBase {

	/** List of patterns for findings groups to remove. */
	private PatternList removeGroupsPattern = new PatternList();

	/** List of patterns for findings groups to retain. */
	private PatternList retainGroupsPattern = new PatternList();

	/** List of patterns for findings categories to remove. */
	private PatternList removeCategoriesPattern = new PatternList();

	/** List of patterns for findings categories to retain. */
	private PatternList retainCategoriesPattern = new PatternList();

	/** Key for modification flag. */
	private String key;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = ConQATParamDoc.READKEY_NAME, minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "The key to read the modification flag from. Findings in modified elements are not subject to filtering. If this key is not provided, all nodes are subject to filtering.")
	public void setReadKey(
			@AConQATAttribute(name = ConQATParamDoc.READKEY_KEY_NAME, description = ConQATParamDoc.READKEY_KEY_DESC) String readKey) {
		key = readKey;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "remove-groups", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "Pattern for findings groups to remove, i.e. they are filtered.")
	public void setRemoveGroups(
			@AConQATAttribute(name = ConQATParamDoc.PATTERN_LIST, description = ConQATParamDoc.PATTERN_LIST_DESC) PatternList patternList)
			throws ConQATException {
		PatternList.checkIfEmpty(patternList);
		removeGroupsPattern = patternList;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "retain-groups", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "Pattern for findings groups to retain, i.e. they are not filtered.")
	public void setRetainGroups(
			@AConQATAttribute(name = ConQATParamDoc.PATTERN_LIST, description = ConQATParamDoc.PATTERN_LIST_DESC) PatternList patternList)
			throws ConQATException {
		PatternList.checkIfEmpty(patternList);
		retainGroupsPattern = patternList;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "remove-categories", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "Pattern for findings categories to remove, i.e. they are filtered.")
	public void setRemoveCategories(
			@AConQATAttribute(name = ConQATParamDoc.PATTERN_LIST, description = ConQATParamDoc.PATTERN_LIST_DESC) PatternList patternList)
			throws ConQATException {
		PatternList.checkIfEmpty(patternList);
		removeCategoriesPattern = patternList;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "retain-categories", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "Pattern for findings categories to retain, i.e. they are not filtered.")
	public void setRetainCategories(
			@AConQATAttribute(name = ConQATParamDoc.PATTERN_LIST, description = ConQATParamDoc.PATTERN_LIST_DESC) PatternList patternList)
			throws ConQATException {
		PatternList.checkIfEmpty(patternList);
		retainCategoriesPattern = patternList;
	}

	/** {@inheritDoc} */
	@Override
	protected boolean skipNode(IConQATNode node) {
		if (key == null) {
			return false;
		}

		return NodeUtils.getValue(node, key, Boolean.class, true);
	}

	/** {@inheritDoc} */
	@Override
	protected boolean isFiltered(IConQATNode node, Finding finding) {
		String groupName = finding.getParent().getName();
		String categoryName = finding.getParent().getParent().getName();

		return removeGroupsPattern.emptyOrMatchesAny(groupName)
				&& !retainGroupsPattern.matchesAny(groupName)
				&& removeCategoriesPattern.emptyOrMatchesAny(categoryName)
				&& !retainCategoriesPattern.matchesAny(categoryName);
	}

}