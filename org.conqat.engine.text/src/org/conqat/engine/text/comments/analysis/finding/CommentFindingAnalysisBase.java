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
package org.conqat.engine.text.comments.analysis.finding;

import org.conqat.engine.commons.findings.FindingCategory;
import org.conqat.engine.commons.findings.FindingCategoryNames;
import org.conqat.engine.commons.findings.FindingGroup;
import org.conqat.engine.commons.findings.location.ElementLocation;
import org.conqat.engine.commons.findings.util.FindingUtils;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.util.ResourceUtils;
import org.conqat.engine.sourcecode.resource.ITokenResource;
import org.conqat.engine.text.comments.Comment;
import org.conqat.engine.text.comments.analysis.CommentClassificationAnalysisBase;
import org.conqat.lib.scanner.IToken;

/**
 * Base class for any comment analysis that produces findings.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 46264 $
 * @ConQAT.Rating GREEN Hash: 3EBAFE416A64E3F4991B9F2C7C4439EC
 */
public abstract class CommentFindingAnalysisBase extends
		CommentClassificationAnalysisBase {

	/** Name of the findings category. */
	private static final String CATEGORY_NAME = FindingCategoryNames.COMMENTS_CATEGORY;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "finding", attribute = "key", optional = true, description = "The key used for storing the findings in.")
	public String findingKey = "findings";

	/** Findings category for comments. */
	private FindingCategory findingCategory;

	/** Finding group. */
	private FindingGroup findingGroup;

	/** The key for the finding group. */
	private String findingGroupKey;

	/** Constructor with a key for the finding group. */
	protected CommentFindingAnalysisBase(String findingGroupKey) {
		this.findingGroupKey = findingGroupKey;
	}

	/** {@inheritDoc} */
	@Override
	protected void setUp(ITokenResource root) throws ConQATException {
		super.setUp(root);

		findingCategory = NodeUtils.getFindingReport(root).getOrCreateCategory(
				CATEGORY_NAME);
		findingGroup = findingCategory.getOrCreateFindingGroup(findingGroupKey);
	}

	/** Creates an element location for the given comment. */
	private ElementLocation createElemenLocation(Comment comment)
			throws ConQATException {
		int position = comment.getPosition();
		IToken token = comment.getTokens().get(position);
		int startOffset = token.getOffset();
		int endOffset = token.getEndOffset();

		return ResourceUtils.createTextRegionLocationForFilteredOffsets(
				comment.getElement(), startOffset, endOffset);
	}

	/**
	 * Creates a finding for the given comment and message and attaches it to
	 * the finding group of this processor.
	 */
	protected void createFinding(Comment comment, String message)
			throws ConQATException {
		ElementLocation location = createElemenLocation(comment);
		FindingUtils.createAndAttachFinding(findingGroup, message,
				comment.getElement(), location, findingKey);
	}
}
