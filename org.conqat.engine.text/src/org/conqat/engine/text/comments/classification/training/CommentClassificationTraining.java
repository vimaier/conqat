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

package org.conqat.engine.text.comments.classification.training;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.conqat.engine.commons.machine_learning.EClassificationAlgorithm;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.resource.ITokenResource;
import org.conqat.engine.text.comments.Comment;
import org.conqat.engine.text.comments.ECommentCategory;
import org.conqat.engine.text.comments.analysis.CommentAnalysisBase;
import org.conqat.engine.text.comments.classification.CommentClassifier;
import org.conqat.engine.text.comments.utils.CommentUtils;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.enums.EnumUtils;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.scanner.ELanguage;
import org.conqat.lib.scanner.IToken;

/**
 * Training of machine learners for comment categorization. This implementation
 * uses J48 decision trees.
 * 
 * 
 * @author $Author: hummelb $
 * @version $Rev: 46288 $
 * @ConQAT.Rating GREEN Hash: A2B7CFB8C4424F5C53D9A2B5BEBB3698
 */
@AConQATProcessor(description = "trains a machine learning classifier for comment classification")
public class CommentClassificationTraining extends CommentAnalysisBase {

	/**
	 * Map to store a machine learning classifier for comment categorization in
	 * each language found during the analysis
	 */
	protected Map<ELanguage, CommentClassifier> classifiers = new HashMap<ELanguage, CommentClassifier>();

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void analyzeComments(List<Comment> comments,
			ITokenElement element, List<IToken> tokens) {
		for (Comment comment : comments) {
			addAsTrainingData(comment, element.getLanguage());
		}
	}

	/**
	 * Adds the comment for the classifier corresponding to the given language
	 * as training data point.
	 */
	private void addAsTrainingData(Comment comment, ELanguage language) {

		if (!classifiers.containsKey(language)) {
			CommentClassifier classifier = new CommentClassifier(language);
			classifiers.put(language, classifier);
		}

		String tag = CommentUtils.getClassTag(comment.getCommentString());

		if (StringUtils.isEmpty(tag)) {
			// this comment has not been tagged. Hence, it cannot be used as
			// training data.
			return;
		}

		ECommentCategory tagEnum = EnumUtils.valueOfIgnoreCase(
				ECommentCategory.class, tag);
		CCSMAssert.isNotNull(tagEnum,
				"Could not resolve tag to a comment category.");
		classifiers.get(language).addData(comment, tagEnum);
	}

	/**
	 * Overrides superclass method because classifier is built here after adding
	 * all comments to the training data set. Builds a classifiers for each
	 * language seen during analysis and saves the classifiers to file.
	 * Classifiers are built as J48 decision trees.
	 */
	@Override
	protected void finish(ITokenResource root) throws ConQATException {
		for (ELanguage language : classifiers.keySet()) {
			(classifiers.get(language))
					.buildAndSaveClassifier(EClassificationAlgorithm.DECISION_TREE_J48);
		}
	}

}