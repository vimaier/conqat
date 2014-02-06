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
 * A recognizer that implements skipping of repeated sequences.
 * 
 * @param <STATE>
 *            the enum used for describing parse states.
 * 
 * @author $Author: goede $
 * @version $Rev: 40382 $
 * @ConQAT.Rating GREEN Hash: 37E6F7B2F46AA1BF410AD0E342C3EAB2
 */
/* package */class RepeatedRecognizer<STATE extends Enum<STATE>> extends
		SequenceRecognizer<STATE> {

	/** Minimal number of matches required. */
	private final int minMatches;

	/** Maximal number of matches allowed. */
	private final int maxMatches;

	/** Constructor. */
	public RepeatedRecognizer(Object[] matchTerms, int minMatches,
			int maxMatches) {
		super(matchTerms, false);
		this.minMatches = minMatches;
		this.maxMatches = maxMatches;
	}

	/** {@inheritDoc} */
	@Override
	public int matchesLocally(ParserState<STATE> parserState,
			List<IToken> tokens, int startOffset) {

		int matches = 0;
		while (matches < maxMatches) {
			int offset = super.matchesLocally(parserState, tokens, startOffset);
			if (offset == NO_MATCH) {
				break;
			}
			matches += 1;
			startOffset = offset;
		}

		if (minMatches <= matches && matches <= maxMatches) {
			return startOffset;
		}
		return NO_MATCH;
	}
}
