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
import static org.conqat.lib.scanner.ETokenType.BREAK;
import static org.conqat.lib.scanner.ETokenType.COMMA;
import static org.conqat.lib.scanner.ETokenType.CONTINUE;
import static org.conqat.lib.scanner.ETokenType.DOT;
import static org.conqat.lib.scanner.ETokenType.FUNCTION;
import static org.conqat.lib.scanner.ETokenType.LBRACE;
import static org.conqat.lib.scanner.ETokenType.LBRACK;
import static org.conqat.lib.scanner.ETokenType.LPAREN;
import static org.conqat.lib.scanner.ETokenType.MINUSMINUS;
import static org.conqat.lib.scanner.ETokenType.PLUSPLUS;
import static org.conqat.lib.scanner.ETokenType.RBRACE;
import static org.conqat.lib.scanner.ETokenType.RBRACK;
import static org.conqat.lib.scanner.ETokenType.RETURN;
import static org.conqat.lib.scanner.ETokenType.RPAREN;
import static org.conqat.lib.scanner.ETokenType.SEMICOLON;
import static org.conqat.lib.scanner.ETokenType.THROW;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.conqat.engine.sourcecode.shallowparser.JavaScriptShallowParser.EJavaScriptParserStates;
import org.conqat.engine.sourcecode.shallowparser.framework.EShallowEntityType;
import org.conqat.engine.sourcecode.shallowparser.framework.ParserState;
import org.conqat.engine.sourcecode.shallowparser.framework.RecognizerBase;
import org.conqat.engine.sourcecode.shallowparser.framework.ShallowEntity;
import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.IToken;
import org.conqat.lib.scanner.ETokenType.ETokenClass;

/**
 * Recognizer for simple statements in JavaScript. We need a separate recognizer
 * as the rules for statement continuation are non-trivial due to the optional
 * semicolon. A good introduction to the topic can be found <a href=
 * "http://blog.izs.me/post/2353458699/an-open-letter-to-javascript-leaders-regarding"
 * >here</a>.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 44176 $
 * @ConQAT.Rating GREEN Hash: EC890A4F9B16884FBD6172A07105453A
 */
/* package */class JavaScriptSimpleStatementRecognizer extends
		RecognizerBase<EJavaScriptParserStates> {

	/** Matched tokens for nesting in complex statements. */
	private final static Map<ETokenType, ETokenType> NESTING_MATCH = new EnumMap<ETokenType, ETokenType>(
			ETokenType.class);

	static {
		NESTING_MATCH.put(LPAREN, RPAREN);
		NESTING_MATCH.put(LBRACK, RBRACK);
		NESTING_MATCH.put(LBRACE, RBRACE);
	}

	/** {@inheritDoc} */
	@Override
	protected int matchesLocally(
			ParserState<EJavaScriptParserStates> parserState,
			List<IToken> tokens, int startOffset) {
		IToken lastToken = null;
		Stack<ETokenType> expectedClosing = new Stack<ETokenType>();

		// create a node here, so we can append function nodes
		parserState.setNode(new ShallowEntity(EShallowEntityType.STATEMENT,
				"simple statement", tokens.get(startOffset).getText(), tokens,
				startOffset));

		while (true) {
			if (startOffset >= tokens.size()) {
				return startOffset;
			}

			IToken token = tokens.get(startOffset);
			ETokenType tokenType = token.getType();

			if (!expectedClosing.isEmpty()
					&& tokenType == expectedClosing.peek()) {
				expectedClosing.pop();
			} else if (NESTING_MATCH.containsKey(tokenType)) {
				expectedClosing.push(NESTING_MATCH.get(tokenType));
			} else if (expectedClosing.isEmpty() && tokenType == SEMICOLON) {
				return startOffset + 1;
			} else if (expectedClosing.isEmpty()
					&& startsNewStatement(token, lastToken)) {
				return startOffset;
			} else if (tokenType == FUNCTION) {
				int next = parserState.parse(ANY, tokens, startOffset);
				if (next == NO_MATCH) {
					return NO_MATCH;
				}
				startOffset = next;
				lastToken = null;
				continue;
			}

			lastToken = token;
			startOffset += 1;
		}
	}

	/** Returns true if the given token starts a new statement. */
	private boolean startsNewStatement(IToken token, IToken lastToken) {
		ETokenType tokenType = token.getType();
		if (tokenType == RBRACE) {
			return true;
		}

		if (lastToken == null) {
			return false;
		}

		// same line => no new statement
		if (lastToken.getLineNumber() == token.getLineNumber()) {
			return false;
		}

		ETokenType lastTokenType = lastToken.getType();

		// jump statements always end at a new line
		if (lastTokenType == RETURN || lastTokenType == BREAK
				|| lastTokenType == CONTINUE || lastTokenType == THROW) {
			return true;
		}

		// ++ and -- bind to next line
		if (tokenType == PLUSPLUS || tokenType == MINUSMINUS) {
			return true;
		}

		// continue statement is line ends with '.' or ','
		if (lastTokenType == DOT || lastTokenType == COMMA) {
			return false;
		}

		// continue statement if line ends in operator or next line starts
		// with operator or delimiter
		if (lastTokenType.getTokenClass() == ETokenClass.OPERATOR
				|| tokenType.getTokenClass() == ETokenClass.OPERATOR
				|| tokenType.getTokenClass() == ETokenClass.DELIMITER) {
			return false;
		}

		return true;
	}
}