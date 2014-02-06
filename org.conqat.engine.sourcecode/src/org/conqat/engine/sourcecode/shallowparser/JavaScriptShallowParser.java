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

import static org.conqat.engine.sourcecode.shallowparser.JavaScriptShallowParser.EJavaScriptParserStates.ANY;
import static org.conqat.lib.scanner.ETokenType.CASE;
import static org.conqat.lib.scanner.ETokenType.CATCH;
import static org.conqat.lib.scanner.ETokenType.COLON;
import static org.conqat.lib.scanner.ETokenType.DEFAULT;
import static org.conqat.lib.scanner.ETokenType.DO;
import static org.conqat.lib.scanner.ETokenType.DOT;
import static org.conqat.lib.scanner.ETokenType.ELSE;
import static org.conqat.lib.scanner.ETokenType.EQ;
import static org.conqat.lib.scanner.ETokenType.FINALLY;
import static org.conqat.lib.scanner.ETokenType.FOR;
import static org.conqat.lib.scanner.ETokenType.FUNCTION;
import static org.conqat.lib.scanner.ETokenType.IDENTIFIER;
import static org.conqat.lib.scanner.ETokenType.IDENTIFIERS;
import static org.conqat.lib.scanner.ETokenType.IF;
import static org.conqat.lib.scanner.ETokenType.LBRACE;
import static org.conqat.lib.scanner.ETokenType.LITERALS;
import static org.conqat.lib.scanner.ETokenType.LPAREN;
import static org.conqat.lib.scanner.ETokenType.PROTOTYPE;
import static org.conqat.lib.scanner.ETokenType.RBRACE;
import static org.conqat.lib.scanner.ETokenType.RPAREN;
import static org.conqat.lib.scanner.ETokenType.SEMICOLON;
import static org.conqat.lib.scanner.ETokenType.SWITCH;
import static org.conqat.lib.scanner.ETokenType.TRY;
import static org.conqat.lib.scanner.ETokenType.WHILE;
import static org.conqat.lib.scanner.ETokenType.WITH;

import java.util.EnumSet;
import java.util.HashSet;

import org.conqat.engine.sourcecode.shallowparser.JavaScriptShallowParser.EJavaScriptParserStates;
import org.conqat.engine.sourcecode.shallowparser.framework.EShallowEntityType;
import org.conqat.engine.sourcecode.shallowparser.framework.ExactIdentifierMatcher;
import org.conqat.engine.sourcecode.shallowparser.framework.RecognizerBase;
import org.conqat.engine.sourcecode.shallowparser.framework.ShallowParserBase;
import org.conqat.lib.commons.region.Region;
import org.conqat.lib.scanner.ETokenType;

/**
 * Shallow parser for JavaScript. The parser is aware of Google closure and
 * supports special handling of the provide, require and inherits statements.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 44176 $
 * @ConQAT.Rating GREEN Hash: AAF9459128B3D7B61CEAB98F0733496F
 */
public class JavaScriptShallowParser extends
		ShallowParserBase<EJavaScriptParserStates> {

	/** The states used in this parser. */
	public static enum EJavaScriptParserStates {

		/** Single state, as any construct can occur at any place. */
		ANY
	}

	/** Constructor. */
	public JavaScriptShallowParser() {
		super(EJavaScriptParserStates.class, EJavaScriptParserStates.ANY);

		createMetaRules();
		createFunctionRules();
		createStatementRules();
	}

	/** Creates parsing rules for meta elements. */
	private void createMetaRules() {
		// Closure special commands (require, provide, inherits)
		inState(ANY)
				.sequence(
						new ExactIdentifierMatcher("goog"),
						DOT,
						new ExactIdentifierMatcher("provide", "require",
								"inherits"))
				.createNode(EShallowEntityType.META, new Region(0, 2))
				.skipNested(LPAREN, RPAREN).optional(SEMICOLON).endNode();
	}

	/** Creates parsing rules for functions. */
	private void createFunctionRules() {

		// assigned function
		inState(ANY)
				.sequence(IDENTIFIER)
				.repeated(DOT, EnumSet.of(IDENTIFIER, PROTOTYPE))
				.sequence(EQ, FUNCTION)
				.createNode(EShallowEntityType.METHOD, "assigned function",
						new Region(0, -3)).skipNested(LPAREN, RPAREN)
				.sequence(LBRACE).parseUntil(ANY).sequence(RBRACE)
				.optional(SEMICOLON).endNode();

		// named function
		inState(ANY).sequence(FUNCTION, IDENTIFIER)
				.createNode(EShallowEntityType.METHOD, "named function", 1)
				.skipNested(LPAREN, RPAREN).sequence(LBRACE).parseUntil(ANY)
				.sequence(RBRACE).endNode();

		// anonymous function
		inState(ANY).sequence(FUNCTION)
				.createNode(EShallowEntityType.METHOD, "anonymous function")
				.skipNested(LPAREN, RPAREN).sequence(LBRACE).parseUntil(ANY)
				.sequence(RBRACE).endNode();
	}

	/** Creates parsing rules for statements. */
	private void createStatementRules() {
		// empty statement
		inState(ANY).sequence(SEMICOLON)
				.createNode(EShallowEntityType.STATEMENT, "empty statement")
				.endNode();

		// filter out labels as meta as they do not increase statement count
		inState(ANY).sequence(IDENTIFIER, COLON)
				.createNode(EShallowEntityType.META, "label", 0).endNode();

		// else if
		RecognizerBase<EJavaScriptParserStates> elseIfAlternative = inState(ANY)
				.sequence(ELSE, IF).skipNested(LPAREN, RPAREN)
				.createNode(EShallowEntityType.STATEMENT, new int[] { 0, 1 });
		endWithPossibleContinuation(elseIfAlternative.sequence(LBRACE)
				.parseUntil(ANY).sequence(RBRACE), EnumSet.of(ELSE));
		endWithPossibleContinuation(elseIfAlternative.parseOnce(ANY),
				EnumSet.of(ELSE));

		// simple block constructs
		createBlockRuleWithContinuation(EnumSet.of(WHILE, FOR, SWITCH, WITH),
				null, true);
		createBlockRuleWithContinuation(EnumSet.of(ELSE, FINALLY), null, false);
		createBlockRuleWithContinuation(EnumSet.of(IF), EnumSet.of(ELSE), true);
		createBlockRuleWithContinuation(EnumSet.of(TRY, CATCH),
				EnumSet.of(CATCH, FINALLY), true);

		createSwitchCaseRules();
		createDoWhileRules();

		// simple statement
		inState(ANY).subRecognizer(new JavaScriptSimpleStatementRecognizer(),
				1, 1).endNode();
	}

	/** Creates rules for do/while. */
	private void createDoWhileRules() {
		RecognizerBase<EJavaScriptParserStates> doWhileAlternative = inState(
				ANY).sequence(DO).createNode(EShallowEntityType.STATEMENT, 0);
		doWhileAlternative.sequence(LBRACE).parseUntil(ANY)
				.sequence(RBRACE, WHILE).skipNested(LPAREN, RPAREN).endNode();
		doWhileAlternative.parseOnce(ANY).sequence(WHILE)
				.skipNested(LPAREN, RPAREN).endNode();
	}

	/** Creates rules for switch/case. */
	private void createSwitchCaseRules() {
		HashSet<ETokenType> literalsAndIdentifiers = new HashSet<ETokenType>(
				LITERALS);
		literalsAndIdentifiers.addAll(IDENTIFIERS);
		inState(ANY).sequence(CASE, literalsAndIdentifiers, COLON)
				.createNode(EShallowEntityType.META, new int[] { 0, 1 })
				.endNode();
		inState(ANY).sequence(CASE, LPAREN)
				.skipToWithNesting(RPAREN, LPAREN, RPAREN).sequence(COLON)
				.createNode(EShallowEntityType.META, 0).endNode();

		inState(ANY).sequence(DEFAULT, COLON)
				.createNode(EShallowEntityType.META, 0).endNode();
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
	private void createBlockRuleWithContinuation(
			EnumSet<ETokenType> startTokens,
			EnumSet<ETokenType> continuationTokens,
			boolean canBeFollowedByParentheses) {
		RecognizerBase<EJavaScriptParserStates> alternative = inState(ANY)
				.sequence(startTokens);
		if (canBeFollowedByParentheses) {
			alternative = alternative.skipNested(LPAREN, RPAREN);
		}
		alternative = alternative.createNode(EShallowEntityType.STATEMENT, 0);

		endWithPossibleContinuation(alternative.sequence(LBRACE)
				.parseUntil(ANY).sequence(RBRACE), continuationTokens);
		endWithPossibleContinuation(alternative.parseOnce(ANY),
				continuationTokens);
	}
}
