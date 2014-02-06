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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.sourcecode.analysis.TokenAnalyzerBase;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.shallowparser.ShallowParserFactory;
import org.conqat.engine.text.comments.Comment;
import org.conqat.lib.scanner.ELanguage;
import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.ETokenType.ETokenClass;
import org.conqat.lib.scanner.IToken;

/**
 * Base class for a processor that analyzes code comments. This class does the
 * analysis to provide a list of comments for each file. A comment is thereby
 * annotated with all required information for machine learning or machine
 * classification.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 46286 $
 * @ConQAT.Rating GREEN Hash: BCDCC454D0C3041DD055FD904A64CB67
 */
public abstract class CommentAnalysisBase extends TokenAnalyzerBase {

	/** If this is not empty, only tokens included in this set are respected. */
	private final Set<ETokenType> includedTokenTypes = EnumSet
			.noneOf(ETokenType.class);

	/** Set of languages for which a machine learner exists. */
	protected final Set<ELanguage> supportedLanguages = new HashSet<ELanguage>(
			Arrays.asList(ELanguage.JAVA, ELanguage.CPP));

	/**
	 * Set of languages for which no machine learner exist and which have been
	 * seen during analysis, causing a warning. We track those languages to not
	 * warn at every single file of an unsupported language.
	 */
	protected final Set<ELanguage> warnedForUnsupportedLanguages = new HashSet<ELanguage>();

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "comment-type", description = "This parameter allows to "
			+ "set specific token types. If this is set, only comments of the specified type are respected. "
			+ "If not set, all comment tokens are respected. This parameter raises an error if a token type "
			+ "is provided that is not a comment.")
	public void addCommentType(
			@AConQATAttribute(name = "value", description = "comment token type") ETokenType tokenType)
			throws ConQATException {
		if (tokenType.getTokenClass() != ETokenClass.COMMENT) {
			throw new ConQATException("Token type " + tokenType
					+ " is not a comment!");
		}
		includedTokenTypes.add(tokenType);
	}

	/** {@inheritDoc} */
	@SuppressWarnings("unused")
	@Override
	protected void analyzeTokens(List<IToken> tokens, ITokenElement element)
			throws ConQATException {
		setUpElementAnalysis(tokens, element);

		List<Comment> comments = new ArrayList<Comment>();

		if (ShallowParserFactory.supportsLanguage(element.getLanguage())
				&& supportedLanguages.contains(element.getLanguage())) {

			List<IToken> tokensToModify = new ArrayList<IToken>(tokens);
			unifyMultipleSingleLineComments(tokensToModify, includedTokenTypes);

			try {
				comments.addAll(CommentExtractor.extractComments(
						tokensToModify, element, includedTokenTypes));

			} catch (ConQATException e) {
				getLogger().error(
						"Error in extracting comments from element "
								+ element.getUniformPath(), e);
			}
		} else if (!warnedForUnsupportedLanguages.contains(element
				.getLanguage())) {
			getLogger().warn(
					"Language " + element.getLanguage()
							+ " is not yet supported for comment analysis");
			warnedForUnsupportedLanguages.add(element.getLanguage());
		}

		// this call is done even though the language might not be supported,
		// e.g. to set "n/a" values in metric analyses.
		try {
			analyzeComments(comments, element, tokens);
		} catch (ConQATException e) {
			getLogger().error(
					"Error in extracting comments from element "
							+ element.getUniformPath(), e);
		}
		completeElementAnalysis(tokens, element);
	}

	/**
	 * Returns the list of comments extracted from the underlying element. This
	 * list always only contains the comments of the currently analyzed element.
	 * For each new element, the list is newly instantiated. This method
	 * requires the call of anaylzeTokens before.
	 */
	protected abstract void analyzeComments(List<Comment> comments,
			ITokenElement element, List<IToken> tokens) throws ConQATException;

	/**
	 * Set up method for analyzing a new element with its tokens
	 */
	@SuppressWarnings("unused")
	protected void setUpElementAnalysis(List<IToken> tokens,
			ITokenElement element) {
		// Empty default implementation.
	}

	/** Finish method after analyzing an element with its tokens */
	@SuppressWarnings("unused")
	protected void completeElementAnalysis(List<IToken> tokens,
			ITokenElement element) {
		// Empty default implementation.
	}

	/**
	 * Modifies the given token list by merging single line comments to one
	 * comment.
	 */
	public static void unifyMultipleSingleLineComments(List<IToken> tokens,
			Set<ETokenType> includedTokenTypes) {
		List<IToken> toRemove = new ArrayList<IToken>();

		if (!includedTokenTypes.contains(ETokenType.END_OF_LINE_COMMENT)
				&& !includedTokenTypes.isEmpty()) {
			return;
		}

		for (int i = 0; i < tokens.size(); i++) {
			IToken token = tokens.get(i);
			String comment = token.getText();

			if (!token.getType().equals(ETokenType.END_OF_LINE_COMMENT)) {
				continue;
			}

			int j = i + 1;
			for (; j < tokens.size(); j++) {
				if (tokens.get(j).getType()
						.equals(ETokenType.END_OF_LINE_COMMENT)
						&& diffLineNumber(token, tokens.get(j)) == (j - i)) {
					comment = comment + tokens.get(j).getText();

					toRemove.add(tokens.get(j));
				} else {
					break;
				}
			}
			if (!comment.equals(token.getText())) {
				tokens.set(i, token.newToken(token.getType(),
						token.getOffset(), token.getLineNumber(), comment,
						token.getOriginId()));
				i = j - 1;
			}

		}
		tokens.removeAll(toRemove);
	}

	/**
	 * Returns the absolute value of the line number difference between two
	 * tokens.
	 */
	private static int diffLineNumber(IToken token1, IToken token2) {
		return Math.abs(token1.getLineNumber() - token2.getLineNumber());
	}

	/** {@inheritDoc} */
	@Override
	protected String[] getKeys() {
		return new String[] {};
	}
}
