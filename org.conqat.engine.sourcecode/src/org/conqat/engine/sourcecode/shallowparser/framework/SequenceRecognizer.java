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
import java.util.Set;

import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.ETokenType.ETokenClass;
import org.conqat.lib.scanner.IToken;

/**
 * A recognizer for sequences.
 * 
 * @param <STATE>
 *            the enum used for describing parse states.
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 7C20F7E692A5B6BE81A81F39B3E58458
 */
public class SequenceRecognizer<STATE extends Enum<STATE>> extends
		RecognizerBase<STATE> {

	/** The match terms. */
	private final ITokenMatcher[] matchers;

	/**
	 * If this is true, the start of the matched sequence is returned as new
	 * parsing position instead of the end.
	 */
	private final boolean reportStartOfMatch;

	/** Constructor. */
	/* package */SequenceRecognizer(Object[] matchTerms,
			boolean reportStartOfMatch) {
		this.reportStartOfMatch = reportStartOfMatch;

		this.matchers = new ITokenMatcher[matchTerms.length];
		for (int i = 0; i < matchTerms.length; ++i) {
			matchers[i] = convertMatchTerm(matchTerms[i]);
		}
	}

	/** Converts a match term to a matcher. */
	private ITokenMatcher convertMatchTerm(final Object matchTerm) {

		if (matchTerm instanceof ITokenMatcher) {
			return (ITokenMatcher) matchTerm;
		}

		if (matchTerm instanceof ETokenType) {
			return new ITokenMatcher() {
				@Override
				public boolean matches(IToken token) {
					return token.getType() == (ETokenType) matchTerm;
				}
			};
		}

		if (matchTerm instanceof ETokenClass) {
			return new ITokenMatcher() {
				@Override
				public boolean matches(IToken token) {
					return token.getType().getTokenClass() == (ETokenClass) matchTerm;
				}
			};
		}

		if (matchTerm instanceof Set<?>) {
			final Set<?> set = (Set<?>) matchTerm;
			return new ITokenMatcher() {
				@Override
				public boolean matches(IToken token) {
					ETokenType type = token.getType();
					return set.contains(type)
							|| set.contains(type.getTokenClass());
				}
			};
		}

		throw new AssertionError("Unsupported match term of type "
				+ matchTerm.getClass());
	}

	/** {@inheritDoc} */
	@Override
	public int matchesLocally(ParserState<STATE> parserState,
			List<IToken> tokens, int startOffset) {
		if ((startOffset + matchers.length) > tokens.size()) {
			return NO_MATCH;
		}

		int offset = startOffset;
		for (ITokenMatcher matcher : matchers) {
			if (!matcher.matches(tokens.get(offset++))) {
				return NO_MATCH;
			}
		}

		if (reportStartOfMatch) {
			return startOffset;
		}
		return offset;
	}

	/** Interface of a custom matcher. */
	public static interface ITokenMatcher {

		/** Returns whether this token is matched. */
		boolean matches(IToken token);
	}
}