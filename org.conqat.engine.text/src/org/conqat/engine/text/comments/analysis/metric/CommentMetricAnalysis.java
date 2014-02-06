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
package org.conqat.engine.text.comments.analysis.metric;

import java.util.List;

import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.IElement;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.shallowparser.ShallowParserFactory;
import org.conqat.engine.text.comments.Comment;
import org.conqat.engine.text.comments.ECommentCategory;
import org.conqat.engine.text.comments.analysis.CommentClassificationAnalysisBase;
import org.conqat.lib.commons.collections.CounterSet;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.scanner.ETokenType.ETokenClass;
import org.conqat.lib.scanner.IToken;

/**
 * Processor that performs comment classification
 * 
 * @author $Author: steidl $
 * @version $Rev: 46279 $
 * @ConQAT.Rating GREEN Hash: AD811D70C1435AAED6FAABEB063EBF55
 */
@AConQATProcessor(description = "Analyzes the comments by calculating the overall comment ratio"
		+ "and the distribution over copyright, header, interface, inline, section, and task"
		+ "comments as well as commented out code using machine learning.")
public class CommentMetricAnalysis extends CommentClassificationAnalysisBase {

	/**
	 * Counts characters belonging to a comment.
	 */
	@AConQATKey(description = "Comment Count", type = "java.lang.Integer")
	public static final String KEY_COMMENT_COUNT = "comment";

	/**
	 * Counts the complete source code in characters (including comments).
	 */
	@AConQATKey(description = "Code Count", type = "java.lang.Integer")
	public static final String KEY_CODE_COUNT = "code";

	/**
	 * Counts characters belonging to a copyright comment.
	 */
	@AConQATKey(description = "Copyright Count", type = "java.lang.Integer")
	public static final String KEY_COPYRIGHT_COUNT = "copyright";

	/**
	 * Counts characters belonging to a header comment.
	 */
	@AConQATKey(description = "Header Count", type = "java.lang.Integer")
	public static final String KEY_HEADER_COUNT = "header";

	/**
	 * Counts characters belonging to an interface comment.
	 */
	@AConQATKey(description = "Interface Count", type = "java.lang.Integer")
	public static final String KEY_INTERFACE_COUNT = "interface";

	/**
	 * Counts characters belonging to an inline comment.
	 */
	@AConQATKey(description = "Inline Count", type = "java.lang.Integer")
	public static final String KEY_INLINE_COUNT = "inline";

	/**
	 * Counts characters belonging to a section comment.
	 */
	@AConQATKey(description = "Section Count", type = "java.lang.Integer")
	public static final String KEY_SECTION_COUNT = "section";

	/**
	 * Counts characters belonging to a task comment.
	 */
	@AConQATKey(description = "Task Count", type = "java.lang.Integer")
	public static final String KEY_TASK_COUNT = "task";

	/**
	 * Counts characters belonging to commented out code.
	 */
	@AConQATKey(description = "Commented Out Code Count", type = "java.lang.Integer")
	public static final String KEY_COMMENTED_OUT_CODE_COUNT = "commented-out-code";

	/**
	 * Denotes the distribution over the different comment categories of the
	 * current file. The values of the map denote the character count for each
	 * comment category.
	 */
	private CounterSet<ECommentCategory> commentDistribution;

	/** Counts all characters in the underlying element. */
	private int codeCount;

	/** Counts all comment characters in the underlying element. */
	private int commentCount;

	/**
	 * {@inheritDoc} Sets all metrics to zero except of the code count which
	 * only needs to be calculated once per underlying element.
	 * */
	@Override
	protected void setUpElementAnalysis(List<IToken> tokens,
			ITokenElement element) {
		commentDistribution = new CounterSet<ECommentCategory>();
		commentCount = 0;
		codeCount = getCodeCharacterCount(tokens);
	}

	/**
	 * Returns the total count of characters within a token list.
	 */
	private int getCodeCharacterCount(List<IToken> tokens) {
		int totalCharacterCount = 0;
		for (IToken token : tokens) {
			totalCharacterCount += StringUtils
					.removeWhitespace(token.getText()).length();
		}
		return totalCharacterCount;
	}

	/** {@inheritDoc} */
	@Override
	protected void analyzeComment(IElement element, Comment comment,
			ECommentCategory category) {

		String commentString = comment.getCommentString();
		commentCount += getCommentLength(commentString);
		commentDistribution.inc(category, getCommentLength(commentString));

	}

	/** {@inheritDoc} */
	@Override
	protected String[] getKeys() {
		return new String[] { KEY_COMMENT_COUNT, KEY_CODE_COUNT,
				KEY_COPYRIGHT_COUNT, KEY_HEADER_COUNT, KEY_INTERFACE_COUNT,
				KEY_INLINE_COUNT, KEY_TASK_COUNT, KEY_COMMENTED_OUT_CODE_COUNT,
				KEY_SECTION_COUNT };
	}

	/**
	 * Returns the length of a comment in characters after removing white
	 * spaces.
	 */
	private int getCommentLength(String comment) {
		return StringUtils.removeWhitespace(comment).length();
	}

	/** {@inheritDoc} */
	@Override
	protected void completeElementAnalysis(List<IToken> tokens,
			ITokenElement element) {
		if (!ShallowParserFactory.supportsLanguage(element.getLanguage())
				|| !supportedLanguages.contains(element.getLanguage())) {
			try {
				codeCount = element.getTextContent().length();
				commentCount = calculateCommentCountLanguageIndependent(tokens);
			} catch (ConQATException e) {
				getLogger().error(e.getMessage());
			}
		}

		element.setValue(KEY_COMMENT_COUNT, commentCount);
		element.setValue(KEY_CODE_COUNT, codeCount);

		element.setValue(KEY_COPYRIGHT_COUNT,
				commentDistribution.getValue(ECommentCategory.COPYRIGHT));
		element.setValue(KEY_HEADER_COUNT,
				commentDistribution.getValue(ECommentCategory.HEADER));
		element.setValue(KEY_INTERFACE_COUNT,
				commentDistribution.getValue(ECommentCategory.INTERFACE));
		element.setValue(KEY_INLINE_COUNT,
				commentDistribution.getValue(ECommentCategory.INLINE));
		element.setValue(KEY_SECTION_COUNT,
				commentDistribution.getValue(ECommentCategory.SECTION));
		element.setValue(KEY_TASK_COUNT,
				commentDistribution.getValue(ECommentCategory.TASK));
		element.setValue(KEY_COMMENTED_OUT_CODE_COUNT,
				commentDistribution.getValue(ECommentCategory.CODE));

	}

	/**
	 * Calculates the number of characters in comments language independent and
	 * independent from included tokens.
	 */
	private int calculateCommentCountLanguageIndependent(List<IToken> tokens) {
		int commentCount = 0;
		for (IToken token : tokens) {
			if (token.getType().getTokenClass() == ETokenClass.COMMENT) {
				commentCount += getCommentLength(token.getText());
			}
		}
		return commentCount;
	}
}
