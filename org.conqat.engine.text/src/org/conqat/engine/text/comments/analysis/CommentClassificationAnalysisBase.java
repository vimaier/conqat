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
package org.conqat.engine.text.comments.analysis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.IElement;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.text.comments.Comment;
import org.conqat.engine.text.comments.ECommentCategory;
import org.conqat.engine.text.comments.classification.CommentClassifier;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.enums.EnumUtils;
import org.conqat.lib.scanner.ELanguage;
import org.conqat.lib.scanner.IToken;

/**
 * Base class for processors that classifies each comment.
 * 
 * @author $Author: steidl $
 * @version $Rev: 46279 $
 * @ConQAT.Rating GREEN Hash: A56CA692AC6025314B5182CEE5A0BFB0
 */
public abstract class CommentClassificationAnalysisBase extends
		CommentAnalysisBase {

	/**
	 * Map to store a machine learning classifier for comment categorization in
	 * each language found during the analysis.
	 */
	private final Map<ELanguage, CommentClassifier> classifiers = new HashMap<ELanguage, CommentClassifier>();

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void analyzeComments(List<Comment> comments,
			ITokenElement element, List<IToken> tokens) throws ConQATException {
		ELanguage language = element.getLanguage();
		if (!classifiers.containsKey(language)) {
			loadClassifier(language);
		}

		for (Comment comment : comments) {
			classifyComment(element, comment, (classifiers.get(element
					.getLanguage())).getClassification(comment));
		}
	}

	/**
	 * Classifies the given comment and calls the abstract method to analyze it.
	 */
	private void classifyComment(IElement element, Comment comment,
			String classification) throws ConQATException {
		ECommentCategory category = EnumUtils.valueOfIgnoreCase(
				ECommentCategory.class, classification);
		CCSMAssert.isNotNull(category, "Error in classifying a comment");
		analyzeComment(element, comment, category);
	}

	/**
	 * This method needs to be implemented by any subclass to analyze the given
	 * comment depending on its category.
	 */
	protected abstract void analyzeComment(IElement element, Comment comment,
			ECommentCategory category) throws ConQATException;

	/**
	 * Loads a classifier for the given language.
	 */
	protected void loadClassifier(ELanguage language) throws ConQATException {
		CommentClassifier classifier = new CommentClassifier(language);
		classifier.loadWekaClassifierAndData();
		classifiers.put(language, classifier);
	}
}