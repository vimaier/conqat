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

import org.conqat.lib.commons.assertion.CCSMPre;
import org.conqat.lib.scanner.IToken;

/**
 * A recognizer that allows to match a prepared recognizer (possibly
 * repeatedly).
 * 
 * @author $Author: goede $
 * @version $Rev: 40382 $
 * @ConQAT.Rating GREEN Hash: 303F2667B9A4AAB0C797FFCF674A30AB
 */
public class SubRecognizer<STATE extends Enum<STATE>> extends
		RecognizerBase<STATE> {

	/** The sub-recognizer that is applied. */
	private final RecognizerBase<STATE> subRecognizer;

	/** The minimal number of required repeated matches. */
	private final int minRepetitions;

	/** The maximal number of allowed repeated matches. */
	private final int maxRepetitions;

	/** Constructor. */
	public SubRecognizer(RecognizerBase<STATE> subRecognizer,
			int minRepetitions, int maxRepetitions) {
		CCSMPre.isTrue(minRepetitions >= 0,
				"Minimal number of repetitions must not be negative!");
		CCSMPre.isTrue(maxRepetitions > 0,
				"Maximal number of repetitions must be positive!");
		CCSMPre.isTrue(minRepetitions <= maxRepetitions,
				"Minimal number of repetitions must not be larger than maximal number!");

		this.subRecognizer = subRecognizer;
		this.minRepetitions = minRepetitions;
		this.maxRepetitions = maxRepetitions;
	}

	/** {@inheritDoc} */
	@Override
	protected int matchesLocally(ParserState<STATE> parserState,
			List<IToken> tokens, int startOffset) {

		int numMatches = 0;
		int currentOffset = startOffset;

		while (numMatches < maxRepetitions) {
			int newOffset = subRecognizer.matches(parserState, tokens,
					currentOffset);
			if (newOffset == NO_MATCH || newOffset == currentOffset) {
				if (numMatches >= minRepetitions) {
					return currentOffset;
				}
				return NO_MATCH;
			}

			currentOffset = newOffset;
			numMatches += 1;
		}

		// max repetitions reached
		return currentOffset;
	}
}
