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
package org.conqat.engine.text.comments.analysis;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.sourcecode.analysis.TokenAnalyzerBase;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.resource.ITokenResource;
import org.conqat.engine.sourcecode.shallowparser.ShallowParserFactory;
import org.conqat.engine.sourcecode.shallowparser.framework.EShallowEntityType;
import org.conqat.engine.sourcecode.shallowparser.framework.ShallowEntity;
import org.conqat.engine.sourcecode.shallowparser.framework.ShallowEntityTraversalUtils;
import org.conqat.engine.sourcecode.shallowparser.util.EntitySelectionExpressionParser;
import org.conqat.engine.text.comments.utils.CommentUtils;
import org.conqat.lib.commons.assertion.CCSMPre;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.predicate.IPredicate;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.ETokenType.ETokenClass;
import org.conqat.lib.scanner.IToken;

/**
 * Base class for comment completeness analysis.
 * 
 * @author $Author: steidl $
 * @version $Rev: 47099 $
 * @ConQAT.Rating YELLOW Hash: 1B542DD305AA9CD8B2FA49323D7991D5
 */
public abstract class CommentCompletenessAnalyzerBase extends TokenAnalyzerBase {

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "doc-comment", attribute = "require", optional = true, description = ""
			+ "If this is true, the node must be commented with a doc comment (not just a plain comment). Default is true.")
	public boolean requireDocComment = true;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "selector", attribute = "expression", description = ""
			+ "The expression describing the parser nodes that are expected to be commented. "
			+ "This is a selection expression as understood in the EntitySelectionExpressionParser class.")
	public String entitySelectionExpression;

	/** The selection predicate. */
	private IPredicate<ShallowEntity> entitySelector;

	/** {@inheritDoc} */
	@Override
	protected void setUp(ITokenResource root) throws ConQATException {
		super.setUp(root);
		entitySelector = EntitySelectionExpressionParser
				.parse(entitySelectionExpression);
	}

	/** {@inheritDoc} */
	@Override
	protected void analyzeTokens(List<IToken> tokens, ITokenElement element)
			throws ConQATException {
		List<ShallowEntity> entities = ShallowParserFactory.parse(element,
				getLogger());
		for (ShallowEntity entity : ShallowEntityTraversalUtils.selectEntities(
				entities, entitySelector)) {
			analyzeSelectedEntity(entity, element,
					isCommented(entity, entities, tokens));
		}
	}

	/** Template method that is called for each selected entity. */
	protected abstract void analyzeSelectedEntity(ShallowEntity entity,
			ITokenElement element, boolean isCommented) throws ConQATException;

	/**
	 * Checks if a given entity is commented.
	 * 
	 * @param entities
	 *            all entities parsed from the file containing the entities.
	 * 
	 * @param tokens
	 *            the tokens for the file from which the entity was parsed. This
	 *            is required as the tokens in the entities have comment tokens
	 *            filtered out.
	 */
	private boolean isCommented(ShallowEntity entity,
			List<ShallowEntity> entities, List<IToken> tokens) {

		if (tokens.isEmpty()) {
			return false;
		}

		List<ShallowEntity> siblings = entities;
		if (entity.getParent() != null) {
			siblings = entity.getParent().getChildren();
		}

		int searchStartOffset = determineSearchStartOffset(entity, siblings);
		int searchEndOffset = entity.getStartOffset();

		int tokenIndex = firstTokenIndex(tokens, searchStartOffset);
		while (tokenIndex < tokens.size()
				&& tokens.get(tokenIndex).getOffset() < searchEndOffset) {
			IToken token = tokens.get(tokenIndex);
			if (isNonEmptyInterfaceComment(token)) {
				return true;
			}
			tokenIndex += 1;
		}

		return false;
	}

	/**
	 * Returns whether the given token represents a (part of) a valid/accepted
	 * interface comment and is not empty.
	 */
	private boolean isNonEmptyInterfaceComment(IToken token) {
		if (token.getType().getTokenClass() != ETokenClass.COMMENT) {
			return false;
		}

		if (isEmptyComment(token.getText())) {
			return false;
		}

		return (token.getType() == ETokenType.DOCUMENTATION_COMMENT || !requireDocComment);
	}

	/**
	 * Returns the index of the first token at or after the given offset. As we
	 * know the tokens to be sorted by offset, we can use binary search.
	 * 
	 * @param tokens
	 *            the list of tokens (must not be empty).
	 */
	private int firstTokenIndex(List<IToken> tokens, int offset) {
		CCSMPre.isFalse(tokens.isEmpty(), "Tokens may not be empty!");

		IToken referenceToken = tokens.get(0);
		IToken searchToken = referenceToken.newToken(referenceToken.getType(),
				offset, 0, StringUtils.EMPTY_STRING, StringUtils.EMPTY_STRING);
		int tokenIndex = Collections.binarySearch(tokens, searchToken,
				new Comparator<IToken>() {
					@Override
					public int compare(IToken token1, IToken token2) {
						return token1.getOffset() - token2.getOffset();
					}
				});

		if (tokenIndex < 0) {
			tokenIndex = -tokenIndex - 1;
		}
		return tokenIndex;
	}

	/**
	 * Calculates the first possible search offset at which a comment would be
	 * counted for the given entity. For this, all meta entities (e.g.
	 * annotations) are ignored.
	 */
	private int determineSearchStartOffset(ShallowEntity entity,
			List<ShallowEntity> siblings) {

		// find index of first non-meta predecessor
		int index = siblings.indexOf(entity) - 1;
		while (index >= 0
				&& siblings.get(index).getType() == EShallowEntityType.META) {
			index -= 1;
		}

		if (index >= 0) {
			return siblings.get(index).getEndOffset();
		}

		if (entity.getParent() != null) {
			return CollectionUtils.getLast(entity.getParent().ownStartTokens())
					.getEndOffset();
		}

		// must be first entity in file, so return file start
		return 0;
	}

	/**
	 * Returns true if the comment contains nothing but JavaDoc tags (ignoring
	 * whitespace and special characters).
	 */
	private static boolean isEmptyComment(String commentString) {
		commentString = commentString.replaceAll("@author.*\\n",
				StringUtils.EMPTY_STRING);
		commentString = commentString.replaceAll("@param.*\\n",
				StringUtils.EMPTY_STRING);
		commentString = commentString.replaceAll("@throws.*\\n",
				StringUtils.EMPTY_STRING);
		commentString = StringUtils.removeWhitespace(commentString);
		commentString = CommentUtils.removeCommentIdentifiers(commentString);
		return commentString.isEmpty();
	}

}
