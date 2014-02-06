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
package org.conqat.engine.sourcecode.shallowparser;

import static org.conqat.engine.sourcecode.shallowparser.EGenericParserStates.IN_METHOD;
import static org.conqat.engine.sourcecode.shallowparser.EGenericParserStates.IN_TYPE;
import static org.conqat.lib.scanner.ETokenType.CASE;
import static org.conqat.lib.scanner.ETokenType.CATCH;
import static org.conqat.lib.scanner.ETokenType.COLON;
import static org.conqat.lib.scanner.ETokenType.COMMA;
import static org.conqat.lib.scanner.ETokenType.DEFAULT;
import static org.conqat.lib.scanner.ETokenType.DO;
import static org.conqat.lib.scanner.ETokenType.ELSE;
import static org.conqat.lib.scanner.ETokenType.EQ;
import static org.conqat.lib.scanner.ETokenType.FINALLY;
import static org.conqat.lib.scanner.ETokenType.IDENTIFIER;
import static org.conqat.lib.scanner.ETokenType.IF;
import static org.conqat.lib.scanner.ETokenType.LBRACE;
import static org.conqat.lib.scanner.ETokenType.LBRACK;
import static org.conqat.lib.scanner.ETokenType.LITERALS;
import static org.conqat.lib.scanner.ETokenType.LPAREN;
import static org.conqat.lib.scanner.ETokenType.RBRACE;
import static org.conqat.lib.scanner.ETokenType.RPAREN;
import static org.conqat.lib.scanner.ETokenType.SEMICOLON;
import static org.conqat.lib.scanner.ETokenType.TRY;
import static org.conqat.lib.scanner.ETokenType.WHILE;

import java.util.EnumSet;
import java.util.HashSet;

import org.conqat.engine.sourcecode.shallowparser.framework.EShallowEntityType;
import org.conqat.engine.sourcecode.shallowparser.framework.RecognizerBase;
import org.conqat.engine.sourcecode.shallowparser.framework.ShallowParserBase;
import org.conqat.lib.scanner.ETokenType;

/**
 * Base class for C-style languages (C++, Java, C#).
 * 
 * @author $Author: kinnen $
 * @version $Rev: 47148 $
 * @ConQAT.Rating GREEN Hash: BAF484ED64B0D2C512B106B51DFE0DE4
 */
/* package */abstract class CStyleShallowParserBase extends
		ShallowParserBase<EGenericParserStates> {

	/** Constructor. */
	protected CStyleShallowParserBase() {
		super(EGenericParserStates.class, EGenericParserStates.TOP_LEVEL);
		createMetaRules();
		createTypeRules();
		createClassElementsRules();
		createStatementRules();
	}

	/** Creates rules for meta elements. */
	protected void createMetaRules() {
		// deal with dangling closing braces by inserting broken node
		inAnyState().sequence(RBRACE).createNode(EShallowEntityType.META,
				"dangling closing brace"); // endNode() omitted!
	}

	/** Parser rules for module/namespace and type creation. */
	protected void createTypeRules() {
		// types; we have to ensure when skipping to the LBRACE, that there is
		// no earlier SEMICOLON
		inAnyState().repeated(getTypeModifier()).markStart()
				.sequence(getTypeKeywords(), getValidIdentifiers())
				.skipBefore(EnumSet.of(SEMICOLON, LBRACE)).sequence(LBRACE)
				.createNode(EShallowEntityType.TYPE, 0, 1).parseUntil(IN_TYPE)
				.sequence(RBRACE).endNode();
	}

	/**
	 * Returns the valid type modifiers for the language. Default implementation
	 * returns empty set. Override to use correct modifiers.
	 */
	protected EnumSet<ETokenType> getTypeModifier() {
		return EnumSet.noneOf(ETokenType.class);
	}

	/** Returns the set of keywords that start a type. */
	protected abstract EnumSet<ETokenType> getTypeKeywords();

	/** Parser rules for both attributes and methods. */
	protected abstract void createClassElementsRules();

	/** Creates parser rules for statements. */
	protected void createStatementRules() {
		createEmptyStatementRule();
		createLabelRule();
		createElseIfRule();
		createBasicBlockRules();
		createCaseRule();
		createDoWhileRule();
		createGenericBlockRule();
		createSimpleStatementRule();
	}

	/** The empty statement. */
	private void createEmptyStatementRule() {
		inState(IN_METHOD).sequence(SEMICOLON)
				.createNode(EShallowEntityType.STATEMENT, "empty statement")
				.endNode();
	}

	/** Matches labels. */
	private void createLabelRule() {
		// filter out labels as meta as they do not increase statement count
		inState(IN_METHOD).sequence(getValidIdentifiers(), COLON)
				.createNode(EShallowEntityType.META, "label", 0).endNode();
	}

	/** Special rule for else-if. */
	private void createElseIfRule() {
		RecognizerBase<EGenericParserStates> elseIfAlternative = inState(
				IN_METHOD).sequence(ELSE, IF).skipNested(LPAREN, RPAREN)
				.createNode(EShallowEntityType.STATEMENT, new int[] { 0, 1 });
		endWithPossibleContinuation(elseIfAlternative.sequence(LBRACE)
				.parseUntil(IN_METHOD).sequence(RBRACE), EnumSet.of(ELSE));
		endWithPossibleContinuation(elseIfAlternative.parseOnce(IN_METHOD),
				EnumSet.of(ELSE));
	}

	/**
	 * Block constructs, such as if/else, while/for/switch, try/catch/finally,
	 * synchronized (only in some languages).
	 */
	private void createBasicBlockRules() {
		createBlockRuleWithContinuation(
				getSimpleBlockKeywordsWithParentheses(), null, true);
		createBlockRuleWithContinuation(
				getSimpleBlockKeywordsWithoutParentheses(), null, false);
		createBlockRuleWithContinuation(EnumSet.of(IF), EnumSet.of(ELSE), true);
		createBlockRuleWithContinuation(EnumSet.of(TRY, CATCH),
				EnumSet.of(CATCH, FINALLY), true);
	}

	/**
	 * Case statement is parsed as meta, as it is hardly a statement on its own.
	 */
	protected void createCaseRule() {
		HashSet<ETokenType> literalsAndIdentifiers = new HashSet<ETokenType>(
				LITERALS);
		literalsAndIdentifiers.addAll(getValidIdentifiers());
		inState(IN_METHOD).sequence(CASE, literalsAndIdentifiers, COLON)
				.createNode(EShallowEntityType.META, new int[] { 0, 1 })
				.endNode();
		inState(IN_METHOD).sequence(CASE, LPAREN)
				.skipToWithNesting(RPAREN, LPAREN, RPAREN).sequence(COLON)
				.createNode(EShallowEntityType.META, 0).endNode();

		inState(IN_METHOD).sequence(DEFAULT, COLON)
				.createNode(EShallowEntityType.META, 0).endNode();
	}

	/** do-while rule. */
	private void createDoWhileRule() {
		RecognizerBase<EGenericParserStates> doWhileAlternative = inState(
				IN_METHOD).sequence(DO).createNode(
				EShallowEntityType.STATEMENT, 0);
		doWhileAlternative.sequence(LBRACE).parseUntil(IN_METHOD)
				.sequence(RBRACE, WHILE).skipNested(LPAREN, RPAREN)
				.optional(ETokenType.SEMICOLON).endNode();
		doWhileAlternative.parseOnce(IN_METHOD).sequence(WHILE)
				.skipNested(LPAREN, RPAREN).optional(ETokenType.SEMICOLON)
				.endNode();
	}

	/** Generic block. */
	private void createGenericBlockRule() {
		inState(IN_METHOD).sequence(LBRACE)
				.createNode(EShallowEntityType.STATEMENT, "anonymous block")
				.parseUntil(IN_METHOD).sequence(RBRACE).endNode();
	}

	/** Simple statement. */
	protected void createSimpleStatementRule() {
		// heuristic for detecting local variables
		completeSimpleStatement(typePatternInState(IN_METHOD).markStart()
				.sequenceBefore(IDENTIFIER, EnumSet.of(COMMA, EQ, SEMICOLON, LBRACK)),
				SubTypeNames.LOCAL_VARIABLE);

		completeSimpleStatement(typePatternInState(IN_METHOD));
		completeSimpleStatement(inState(IN_METHOD).sequence(LITERALS));
		completeSimpleStatement(inState(IN_METHOD).sequence(
				getStatementStartTokens()));
	}

	/**
	 * Returns the set of all valid identifiers, i.e. token types that can be
	 * used to name elements in the language.
	 */
	protected EnumSet<ETokenType> getValidIdentifiers() {
		return EnumSet.of(IDENTIFIER);
	}

	/**
	 * Returns the set of all keywords that start a simple block with optional
	 * parentheses (see implementers for examples).
	 */
	protected abstract EnumSet<ETokenType> getSimpleBlockKeywordsWithParentheses();

	/**
	 * Returns the set of all keywords that start a simple block but are never
	 * followed by parentheses (see implementers for examples).
	 */
	protected abstract EnumSet<ETokenType> getSimpleBlockKeywordsWithoutParentheses();

	/**
	 * Returns a set of all tokens that can start a statement, besides a type
	 * (see {@link #typePatternInState(EGenericParserStates...)} and a literal.
	 */
	protected abstract EnumSet<ETokenType> getStatementStartTokens();

	/** Creates a recognizer that matches all valid types. */
	protected abstract RecognizerBase<EGenericParserStates> typePattern(
			RecognizerBase<EGenericParserStates> currentState);

	/**
	 * Creates a recognizer that matches all valid types, starting from the
	 * given state.
	 */
	protected RecognizerBase<EGenericParserStates> typePatternInState(
			EGenericParserStates... states) {
		return typePattern(inState(states));
	}

	/**
	 * Creates a rule for recognizing a statement starting with a single
	 * keyword, optionally followed by an expression in parentheses, and
	 * followed by a block or a single statement.
	 * 
	 * @param continuationTokens
	 *            list of tokens that indicate a continued statement if
	 *            encountered after the block. May be null.
	 */
	protected void createBlockRuleWithContinuation(
			EnumSet<ETokenType> startTokens,
			EnumSet<ETokenType> continuationTokens,
			boolean canBeFollowedByParentheses) {
		RecognizerBase<EGenericParserStates> alternative = inState(IN_METHOD)
				.sequence(startTokens);
		if (canBeFollowedByParentheses) {
			alternative = alternative.skipNested(LPAREN, RPAREN);
		}
		alternative = alternative.createNode(EShallowEntityType.STATEMENT, 0);

		endWithPossibleContinuation(
				alternative.sequence(LBRACE).parseUntil(IN_METHOD)
						.sequence(RBRACE), continuationTokens);
		endWithPossibleContinuation(alternative.parseOnce(IN_METHOD),
				continuationTokens);
	}

	/** Completes a recognizer for a simple statement. */
	protected void completeSimpleStatement(
			RecognizerBase<EGenericParserStates> baseRecognizer) {
		completeSimpleStatement(baseRecognizer, "simple statement");
	}

	/** Completes a recognizer for a simple statement. */
	protected void completeSimpleStatement(
			RecognizerBase<EGenericParserStates> baseRecognizer, String subtype) {
		RecognizerBase<EGenericParserStates> alternative = baseRecognizer
				.createNode(EShallowEntityType.STATEMENT, subtype, 0)
				.skipBeforeWithNesting(EnumSet.of(SEMICOLON, RBRACE), LBRACE,
						RBRACE, LPAREN, RPAREN);

		alternative.sequence(SEMICOLON).endNode();

		// this (empty) alternative captures the case where a statement is not
		// closed by a semicolon, so we deliberately leave it open. While in
		// most languages this is an error (and then this rule helps us to
		// continue parsing), in C++ you can construct valid statements without
		// semicolon using macros (although it is discouraged).
		alternative.sequence();
	}
}
