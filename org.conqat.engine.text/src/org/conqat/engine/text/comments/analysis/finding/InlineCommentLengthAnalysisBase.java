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

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.IElement;
import org.conqat.engine.text.comments.Comment;
import org.conqat.engine.text.comments.ECommentCategory;
import org.conqat.engine.text.comments.utils.CommentUtils;

/**
 * {@ConQAT.Doc}
 * 
 * Base class for analyzing the length of inline comments.
 * 
 * @author $Author: steidl $
 * @version $Rev: 46589 $
 * @ConQAT.Rating GREEN Hash: D4534E199A93CAB585A277B88058CEE8
 */
public abstract class InlineCommentLengthAnalysisBase extends
		CommentFindingAnalysisBase {

	/** Constructor. */
	protected InlineCommentLengthAnalysisBase(String findingGroupKey) {
		super(findingGroupKey);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Calculates the length indicator and calls the abstract method to process
	 * it.
	 */
	@Override
	protected void analyzeComment(IElement element, Comment comment,
			ECommentCategory category) throws ConQATException {

		if (category == ECommentCategory.INLINE) {
			int lengthInWords = countCommentWords(comment);
			analyzeCommentLengthInWords(comment, lengthInWords);
		}
	}

	/**
	 * Abstract method to analyze the length of a comment (measured in number of
	 * words) and create findings for it
	 */
	protected abstract void analyzeCommentLengthInWords(Comment comment,
			int length) throws ConQATException;

	/** Calculates the length of comment by counting words. */
	private int countCommentWords(Comment comment) {
		String commentString = CommentUtils.getTextInComment(comment
				.getCommentString());
		return commentString.split("\\s+").length;
	}
}