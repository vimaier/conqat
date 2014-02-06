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

import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.resource.ITokenResource;
import org.conqat.lib.scanner.IToken;
import org.conqat.lib.scanner.ETokenType.ETokenClass;

/**
 * Base class for analyzers that identify commented code.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43290 $
 * @ConQAT.Rating GREEN Hash: 7AFE80389B69A221BE68611DA209A33B
 */
public abstract class CommentedCodeAnalyzerBase extends
		CommentedCodeKeysBase<ITokenResource, ITokenElement> {

	/** Whether to ignore the header comment */
	private boolean ignoreHeaderComment = false;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "headerComment", minOccurrences = 0, maxOccurrences = 1, description = "Header comment")
	public void setIgnoreHeaderComment(
			@AConQATAttribute(name = "ignore", defaultValue = "false", description = "Whether to ignore the header comment and hence rate it as documentation") boolean ignoreHeaderComment) {
		this.ignoreHeaderComment = ignoreHeaderComment;
	}

	/** {@inheritDoc} */
	@Override
	protected void analyzeElement(ITokenElement element) {
		List<IToken> tokens;
		try {
			tokens = element.getTokens(getLogger());
		} catch (ConQATException e) {
			getLogger().warn("Problems scanning element: " + e);
			return;
		}

		List<List<IToken>> commentSequences = determineCommentSequences(tokens);
		analyzeCommentSequences(element, commentSequences);
	}

	/**
	 * Gathers consecutive comment tokens into lists.
	 * 
	 * @return List of comment sequences.
	 */
	private List<List<IToken>> determineCommentSequences(List<IToken> tokens) {
		List<List<IToken>> commentSequences = new ArrayList<List<IToken>>();

		List<IToken> commentSequence = new ArrayList<IToken>();
		for (IToken token : tokens) {
			if (startNewSequence(commentSequence, token)) {
				commentSequences.add(commentSequence);
				commentSequence = new ArrayList<IToken>();
			}
			if (isComment(token)) {
				commentSequence.add(token);
			}
		}
		if (!commentSequence.isEmpty()) {
			commentSequences.add(commentSequence);
		}

		return commentSequences;
	}

	/**
	 * Start new sequence, in case (1) the current token is not a comment, or
	 * (2) the token is of a different comment type than the tokens in the
	 * current comment sequence.
	 */
	private boolean startNewSequence(List<IToken> commentSequence, IToken token) {
		return (!isComment(token) || !sameTypeAsFirstToken(token,
				commentSequence)) && !commentSequence.isEmpty();
	}

	/** Whether a certain token is a comment */
	private boolean isComment(IToken token) {
		return token.getType().getTokenClass() == ETokenClass.COMMENT;
	}

	/**
	 * Whether a certain token is of the same type than the tokens in a sequence
	 */
	private boolean sameTypeAsFirstToken(IToken token, List<IToken> sequence) {
		return sequence.isEmpty()
				|| sequence.get(0).getType() == token.getType();
	}

	/** Analyze the comment sequences according to whether they contain code. */
	private void analyzeCommentSequences(ITokenElement element,
			List<List<IToken>> commentSequences) {
		int numberOfCodeComments = 0;
		int linesOfCommentedCode = 0;
		int charactersOfCommentedCode = 0;

		List<Comment> comments = new ArrayList<Comment>();
		List<Comment> codeComments = new ArrayList<Comment>();
		for (List<IToken> commentSequence : commentSequences) {
			Comment comment = new Comment(commentSequence);

			if (isHeader(comment) && ignoreHeaderComment) {
				comment.setType(ECommentType.DOCUMENTATION);
			} else {
				ECommentType type = getType(comment, element);
				comment.setType(type);
			}

			if (comment.getType() == ECommentType.CODE) {
				numberOfCodeComments++;
				linesOfCommentedCode += comment.getNumberOfLines();
				charactersOfCommentedCode += comment.getNumberOfCharacters();
				codeComments.add(comment);
			}

			comments.add(comment);
		}

		storeResults(element, comments, codeComments, numberOfCodeComments,
				linesOfCommentedCode, charactersOfCommentedCode);
	}

	/** Check whether the comment is a header. */
	private boolean isHeader(Comment comment) {
		return comment.getStartOffset() == 0;
	}

	/** Get the type for a comment. */
	protected abstract ECommentType getType(Comment comment,
			ITokenElement element);
}