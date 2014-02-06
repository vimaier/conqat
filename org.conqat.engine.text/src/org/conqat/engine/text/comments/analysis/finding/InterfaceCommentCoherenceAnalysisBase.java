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
import org.conqat.engine.text.comments.classification.features.CoherenceUtils;
import org.conqat.engine.text.comments.utils.CommentUtils;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Processor to analyze the coherence between interface comment and method name.
 * 
 * @author $Author: steidl $
 * @version $Rev: 46589 $
 * @ConQAT.Rating GREEN Hash: E8318AEE8527EF2A2F2934397015FF77
 */
public abstract class InterfaceCommentCoherenceAnalysisBase extends
		CommentFindingAnalysisBase {

	/** Constructor. */
	protected InterfaceCommentCoherenceAnalysisBase(String findingGroupKey) {
		super(findingGroupKey);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Analyzes interface comments by calling the abstract method to calculate
	 * the coherence between comment and method name.
	 */
	@Override
	protected void analyzeComment(IElement element, Comment comment,
			ECommentCategory category) throws ConQATException {
		if (category != ECommentCategory.INTERFACE) {
			return;
		}
		String commentString = comment.getCommentString();
		if (!CommentUtils.isDefaultComment(commentString)
				&& !CommentUtils.hasOnlyJavaDoc(commentString)) {

			analyzeCoherence(comment);
		}
	}

	/**
	 * Abstract method to analyze the coherence between method name and comment.
	 */
	protected abstract void analyzeCoherence(Comment comment)
			throws ConQATException;

	/**
	 * Returns the coherence coefficient, which indicates how many words in the
	 * comment correspond to a word in the method name relative to all words in
	 * the comment.
	 */
	protected double getCoherenceCoefficient(Comment classificationObject) {
		String commentText = CommentUtils.getTextInComment(classificationObject
				.getCommentString());

		double numberOfCorrespondingWords = CoherenceUtils.correspond(
				classificationObject.getMethodFinder().getNextDefinition(
						classificationObject.getPosition()),
				classificationObject.getCommentString(), 2);

		double numWordsInComment = 0;
		String[] parts = commentText.split(" ");
		for (String part : parts) {
			// ignore dots and other single characters
			if (part.length() > 1) {
				numWordsInComment++;
			}
		}

		return numberOfCorrespondingWords / numWordsInComment;

	}

	/**
	 * Returns a copy of the given comment that contains the comment content
	 * until the first @.
	 * 
	 * If includingReturn is true, then a potential @return string is included.
	 */
	protected Comment getCommentHeadline(Comment comment,
			boolean includingReturn) {
		String commentString = comment.getCommentString();
		commentString = CommentUtils.removeCommentIdentifiers(commentString);

		String commentStringAdapted = commentString;

		commentStringAdapted = StringUtils.getFirstParts(commentString, 1, '@');

		if (includingReturn && commentString.contains("@return")) {
			// as the return statement might be followed by further @throw etc.
			// declaration, cut them off.
			commentStringAdapted += StringUtils.getFirstParts(
					commentString.split("@return")[1], 1, '@');
		}

		return new Comment(commentStringAdapted, comment.getPosition(),
				comment.getASTLocation(), comment.getElement(),
				comment.getTokens(), comment.getMethodFinder());

	}
}
