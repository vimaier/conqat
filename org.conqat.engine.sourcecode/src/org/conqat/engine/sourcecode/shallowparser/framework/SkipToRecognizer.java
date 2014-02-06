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
package org.conqat.engine.sourcecode.shallowparser.framework;

import java.util.EnumMap;
import java.util.List;
import java.util.Stack;

import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.IToken;

/**
 * A recognizer that implements skipping of tokens up to a certain match.
 * 
 * @param <STATE>
 *            the enum used for describing parse states.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 05D6B9EDE3A99000742A32B561DF2531
 */
/* package */class SkipToRecognizer<STATE extends Enum<STATE>> extends
		SequenceRecognizer<STATE> {

	/**
	 * Mapping from tokens that open a nested scope to the tokens that close it.
	 * This can be used to skip regions of nested braces, etc.
	 */
	private final EnumMap<ETokenType, ETokenType> nestingMap = new EnumMap<ETokenType, ETokenType>(
			ETokenType.class);

	/** Constructor. */
	public SkipToRecognizer(Object[] matchTerms, boolean reportStartOfEndMatch) {
		super(matchTerms, reportStartOfEndMatch);
	}

	/** Adds a pair of opening and closing tokens for nesting analysis. */
	public void addNestingTokens(ETokenType open, ETokenType close) {
		nestingMap.put(open, close);
	}

	/** {@inheritDoc} */
	@Override
	public int matchesLocally(ParserState<STATE> parserState,
			List<IToken> tokens, int startOffset) {
		Stack<ETokenType> expectedTokens = new Stack<ETokenType>();
		for (int offset = startOffset; offset < tokens.size(); ++offset) {

			ETokenType type = tokens.get(offset).getType();
			if (!expectedTokens.isEmpty() && expectedTokens.peek() == type) {
				expectedTokens.pop();
				continue;
			}

			ETokenType closing = nestingMap.get(type);
			if (closing != null) {
				expectedTokens.push(closing);
				continue;
			}

			if (!expectedTokens.isEmpty()) {
				continue;
			}

			int match = super.matchesLocally(parserState, tokens, offset);
			if (match != NO_MATCH) {
				return match;
			}
		}
		return NO_MATCH;
	}
}
