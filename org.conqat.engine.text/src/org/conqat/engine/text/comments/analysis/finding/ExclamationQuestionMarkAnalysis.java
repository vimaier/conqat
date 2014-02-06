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

import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.IElement;
import org.conqat.engine.text.comments.Comment;
import org.conqat.engine.text.comments.ECommentCategory;

/**
 * {@ConQAT.Doc}
 * 
 * Processor to analyze the usefulness of comments by looking for exclamation
 * and question marks.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 46269 $
 * @ConQAT.Rating GREEN Hash: BFA79B80B2D8BB84D45D284B99CE185E
 */
@AConQATProcessor(description = "Calculates the exclamation and question mark heuristic to evaluate the usefulness of comments.")
public class ExclamationQuestionMarkAnalysis extends CommentFindingAnalysisBase {

	/**
	 * Name of the findings group for comments containing exclamation and
	 * question marks.
	 */
	private static final String EXCLAMATION_QUESTION_MARK = "Exclamation or Question Mark in Comment";

	/**
	 * Constructor.
	 */
	public ExclamationQuestionMarkAnalysis() {
		super(EXCLAMATION_QUESTION_MARK);
	}

	/**
	 * Creates findings for a comment that contains a question mark or
	 * exclamation mark.
	 */
	private void calculateQorEMarkHeuristic(Comment comment)
			throws ConQATException {
		String commentString = comment.getCommentString();
		if (commentString.contains("!") || (commentString.contains("?"))) {
			createFinding(comment, EXCLAMATION_QUESTION_MARK);
		}
	}

	/** {@inheritDoc} */
	@Override
	protected void analyzeComment(IElement element, Comment comment,
			ECommentCategory category) throws ConQATException {
		if (category == ECommentCategory.INLINE
				|| category == ECommentCategory.INTERFACE
				|| category == ECommentCategory.HEADER) {
			calculateQorEMarkHeuristic(comment);
		}
	}
}