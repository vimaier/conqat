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
package org.conqat.engine.abap.analyzer;

import java.util.List;

import org.conqat.engine.resource.util.ResourceUtils;

import org.conqat.engine.commons.findings.FindingCategory;
import org.conqat.engine.commons.findings.FindingGroup;
import org.conqat.engine.commons.findings.FindingReport;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.analysis.ElementAnalyzerBase;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.resource.ITokenResource;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43290 $
 * @ConQAT.Rating GREEN Hash: 8F3459A5EDC00E665A5FA33B8B078C42
 */
@AConQATProcessor(description = "A processor that converts comments to findings.")
public class CommentToFindingsConverter extends
		ElementAnalyzerBase<ITokenResource, ITokenElement> {

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Key for the findings of this analysis", type = "org.conqat.engine.commons.findings.FindingsList")
	public static final String COMMENTED_CODE_KEY = "Commented Code";

	/** Finding report */
	private FindingReport report;

	/** {@inheritDoc} */
	@Override
	protected void setUp(ITokenResource root) throws ConQATException {
		super.setUp(root);
		report = NodeUtils.getFindingReport(root);
	}

	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Override
	protected void analyzeElement(ITokenElement element) throws ConQATException {
		List<Comment> comments = (List<Comment>) element
				.getValue(CommentedCodeKeysBase.COMMENTS_KEY);

		if (comments == null) {
			return;
		}
		for (Comment comment : comments) {
			if (comment.getType() == ECommentType.CODE) {
				createAndAttachFinding(element, comment);
			}
		}
	}

	/** Create finding based on a comment */
	private void createAndAttachFinding(ITokenElement element, Comment comment)
			throws ConQATException {
		FindingCategory category = report
				.getOrCreateCategory("Commented Code Assessment");
		FindingGroup group = getFindingGroup(category, comment);

		ResourceUtils.createAndAttachFindingForFilteredRegion(group,
				COMMENTED_CODE_KEY, element, comment.getStartOffset(),
				comment.getEndOffset(), COMMENTED_CODE_KEY);
	}

	/** Get the finding group for a comment */
	private FindingGroup getFindingGroup(FindingCategory category,
			Comment comment) {
		String loccLabel = " Line(s) of Commented Code";
		int numberOfLines = comment.getNumberOfLines();
		if (numberOfLines < 2) {
			return category.getOrCreateFindingGroup("1" + loccLabel);
		} else if (numberOfLines < 5) {
			return category.getOrCreateFindingGroup("2-4" + loccLabel);
		} else if (numberOfLines < 10) {
			return category.getOrCreateFindingGroup("5-9" + loccLabel);
		} else if (numberOfLines < 20) {
			return category.getOrCreateFindingGroup("10-19" + loccLabel);
		} else if (numberOfLines < 50) {
			return category.getOrCreateFindingGroup("20-49" + loccLabel);
		} else {
			return category.getOrCreateFindingGroup("50-" + loccLabel);
		}
	}

	/** {@inheritDoc} */
	@Override
	protected String[] getKeys() {
		return new String[] { COMMENTED_CODE_KEY };
	}
}