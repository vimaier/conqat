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
package org.conqat.engine.sourcecode.analysis.shallowparsed;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.conqat.engine.sourcecode.shallowparser.SubTypeNames;
import org.conqat.engine.sourcecode.shallowparser.TokenStreamUtils;
import org.conqat.engine.sourcecode.shallowparser.framework.EShallowEntityType;
import org.conqat.engine.sourcecode.shallowparser.framework.ShallowEntity;
import org.conqat.engine.sourcecode.shallowparser.framework.ShallowEntityTraversalUtils;
import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.IToken;

/**
 * Utility methods used for dealing with {@link ShallowEntity}s.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 47154 $
 * @ConQAT.Rating GREEN Hash: DDCE5A81AE350DB0986D7625024F2C99
 */
public class ShallowParsingUtils {

	/** Lists all primitive statements (i.e. statements without children). */
	public static List<ShallowEntity> listPrimitiveStatements(
			Collection<ShallowEntity> entities) {
		return new ShallowEntityTraversalUtils.CollectingVisitorBase() {
			@Override
			protected boolean collect(ShallowEntity entity) {
				return entity.getType() == EShallowEntityType.STATEMENT
						&& entity.getChildren().isEmpty();
			}
		}.apply(entities);
	}

	/** Lists all nested statements (i.e. statements with children). */
	public static List<ShallowEntity> listNestedStatements(
			Collection<ShallowEntity> entities) {
		return new ShallowEntityTraversalUtils.CollectingVisitorBase() {
			@Override
			protected boolean collect(ShallowEntity entity) {
				return entity.getType() == EShallowEntityType.STATEMENT
						&& !entity.getChildren().isEmpty();
			}
		}.apply(entities);
	}

	/**
	 * Returns the tokens of a method entity that correspond to parameter names.
	 */
	public static List<IToken> extractParameterNameTokens(ShallowEntity entity) {
		return extractVariableNameTokens(TokenStreamUtils.tokensBetween(
				entity.ownStartTokens(), ETokenType.LPAREN, ETokenType.RPAREN));
	}

	/**
	 * Returns the tokens corresponding to variable names. Heuristic is to look
	 * at identifier tokens outside of parenthesis and directly before a comma,
	 * equals sign, or closing parenthesis. The equals sign is needed to deal
	 * with C++ default parameters and assignments in local variables.
	 * Additionally, we drop brackets, as they can obscure the type/name
	 * separation.
	 */
	public static List<IToken> extractVariableNameTokens(List<IToken> tokens) {
		List<IToken> result = new ArrayList<IToken>();
		int parenthesisNesting = 0;
		boolean waitForComma = false;
		IToken previousToken = null;
		for (IToken token : tokens) {
			switch (token.getType()) {
			case LBRACK:
			case RBRACK:
				// do not update previousToken
				continue;

			case LT:
			case LPAREN:
				parenthesisNesting += 1;
				break;

			case GT:
			case RPAREN:
				parenthesisNesting -= 1;
				break;

			case COMMA:
				if (parenthesisNesting == 0) {
					if (!waitForComma && previousToken != null
							&& previousToken.getType() == ETokenType.IDENTIFIER) {
						result.add(previousToken);
					}
					waitForComma = false;
				}
				break;

			case EQ:
			case SEMICOLON:
				if (parenthesisNesting == 0 && !waitForComma
						&& previousToken != null
						&& previousToken.getType() == ETokenType.IDENTIFIER) {
					result.add(previousToken);
					waitForComma = true;
				}
				break;
			}

			previousToken = token;
		}

		if (parenthesisNesting == 0 && !waitForComma && previousToken != null
				&& previousToken.getType() == ETokenType.IDENTIFIER) {
			result.add(previousToken);
		}

		return result;
	}

	/**
	 * Returns the list of tokens corresponding to the names of variables newly
	 * declared within a for loop.
	 */
	public static List<IToken> extractVariablesDeclaredInFor(
			ShallowEntity entity) {
		List<IToken> forLoopInitTokens = TokenStreamUtils.tokensBetween(
				entity.ownStartTokens(), ETokenType.LPAREN,
				ETokenType.SEMICOLON);
		List<IToken> variableNameTokens = ShallowParsingUtils
				.extractVariableNameTokens(forLoopInitTokens);

		// handle the case where only existing variables are initialized
		if (!variableNameTokens.isEmpty()
				&& variableNameTokens.get(0) == forLoopInitTokens.get(0)) {
			variableNameTokens.clear();
		}

		return variableNameTokens;
	}

	/** Returns whether the entity is a local variable. */
	public static boolean isLocalVariable(ShallowEntity entity) {
		return entity.getType() == EShallowEntityType.STATEMENT
				&& SubTypeNames.LOCAL_VARIABLE.equals(entity.getSubtype());
	}

	/** Returns whether the entity is a global variable. */
	public static boolean isGlobalVariable(ShallowEntity entity) {
		return entity.getType() == EShallowEntityType.ATTRIBUTE
				&& (entity.getParent() == null || entity.getParent().getType() == EShallowEntityType.MODULE);
	}

	/** Returns whether the given attributes denotes a constant. */
	public static boolean isConstant(ShallowEntity entity) {
		if (entity.getType() != EShallowEntityType.ATTRIBUTE) {
			return false;
		}

		List<IToken> tokens = entity.ownStartTokens();

		// C++
		if (TokenStreamUtils.containsAny(tokens, ETokenType.CONST)) {
			return true;
		}

		// In java we require "static final", as "final" alone only indicates
		// immutable field but not "classic" constants
		if (TokenStreamUtils.containsAll(tokens, ETokenType.FINAL,
				ETokenType.STATIC)) {
			return true;
		}

		return false;
	}
}
