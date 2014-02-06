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

import java.util.List;

import org.conqat.lib.scanner.IToken;

/**
 * A recognizer that parses until one of its tail recognizers matches.
 * 
 * @author $Author: pfaller $
 * @version $Rev: 44252 $
 * @ConQAT.Rating GREEN Hash: 98E787A6563284A83E46218BF3A6CE4C
 */
/* package */class ParseUntilRecognizer<STATE extends Enum<STATE>> extends
		RecognizerBase<STATE> {

	/** The state to parse in. */
	private final STATE state;

	/**
	 * If this is true, it is also valid to match until end of file, i.e. the
	 * node is marked as complete in this case.
	 */
	private final boolean allowEofMatch;

	/** Constructor. */
	public ParseUntilRecognizer(STATE state, boolean allowEofMatch) {
		this.state = state;
		this.allowEofMatch = allowEofMatch;
	}

	/** {@inheritDoc} */
	@Override
	public int matches(ParserState<STATE> parserState, List<IToken> tokens,
			int startOffset) {
		while (startOffset < tokens.size()) {
			for (RecognizerBase<STATE> recognizer : tailRecognizers) {
				int match = recognizer
						.matches(parserState, tokens, startOffset);
				if (match != NO_MATCH) {
					return match;
				}
			}

			int next = parserState.parse(state, tokens, startOffset);
			if (next == NO_MATCH) {
				startOffset += 1;
			} else {
				startOffset = next;
			}
		}

		// we swallowed up everything
		if (allowEofMatch) {
			parserState.endNode(false);
		}
		return tokens.size();
	}
}