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

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.conqat.lib.commons.region.Region;
import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.ETokenType.ETokenClass;
import org.conqat.lib.scanner.IToken;

/**
 * A node used for recognizing a part of the system. The parsers constructed
 * work by trying a list of recognizers and "using" the first that matches.
 * Recognizers can be chained to match more complicated patterns and allow
 * branching.
 * 
 * @author $Author: pfaller $
 * @version $Rev: 44252 $
 * @ConQAT.Rating GREEN Hash: EB3251E0075459C931B1669096FA1DE5
 */
public abstract class RecognizerBase<STATE extends Enum<STATE>> {

	/** Constant used to indicate that no match occurred. */
	public static final int NO_MATCH = -1;

	/**
	 * The matchers that are to be executed after this one. If there are more
	 * than one, they are tried in order.
	 */
	protected final List<RecognizerBase<STATE>> tailRecognizers = new ArrayList<RecognizerBase<STATE>>();

	/**
	 * Creates a recognizer for a certain sequence and moves the offset to the
	 * end of the match.
	 * 
	 * @param matchTerms
	 *            a list of match terms which must match in order. These may be
	 *            instances of {@link ETokenType}, {@link ETokenClass}, or sets
	 *            of them.
	 */
	public RecognizerBase<STATE> sequence(Object... matchTerms) {
		return appendRecognizer(new SequenceRecognizer<STATE>(matchTerms, false));
	}

	/**
	 * Creates a recognizer for a certain sequence and moves the offset to the
	 * beginning of the match (in contrast to {@link #sequence(Object...)},
	 * which moves the offset to the end of the match). This is useful to
	 * implement branches in the parser.
	 * 
	 * @param matchTerms
	 *            a list of match terms which must match in order. These may be
	 *            instances of {@link ETokenType}, {@link ETokenClass}, or sets
	 *            of them.
	 */
	public RecognizerBase<STATE> sequenceBefore(Object... matchTerms) {
		return appendRecognizer(new SequenceRecognizer<STATE>(matchTerms, true));
	}

	/**
	 * Creates a recognizer that matches a certain sequence repeatedly
	 * (including no matches).
	 * 
	 * @param matchTerms
	 *            a list of match terms which must match in order. These may be
	 *            instances of {@link ETokenType}, {@link ETokenClass}, or sets
	 *            of them.
	 */
	public RecognizerBase<STATE> repeated(Object... matchTerms) {
		return appendRecognizer(new RepeatedRecognizer<STATE>(matchTerms, 0,
				Integer.MAX_VALUE));
	}

	/**
	 * Creates a recognizer that matches the given sub-recognizer the given
	 * number of times.
	 */
	public RecognizerBase<STATE> subRecognizer(RecognizerBase<STATE> sub,
			int minRepetitions, int maxRepetitions) {
		return appendRecognizer(new SubRecognizer<STATE>(sub, minRepetitions,
				maxRepetitions));
	}

	/**
	 * Creates a recognizer that matches a certain sequence optionally.
	 * 
	 * @param matchTerms
	 *            a list of match terms which must match in order. These may be
	 *            instances of {@link ETokenType}, {@link ETokenClass}, or sets
	 *            of them.
	 */
	public RecognizerBase<STATE> optional(Object... matchTerms) {
		return appendRecognizer(new RepeatedRecognizer<STATE>(matchTerms, 0, 1));
	}

	/**
	 * Creates a recognizer that skips until the given match terms are found.
	 * The new position is set behind the found match.
	 * 
	 * @param matchTerms
	 *            a list of match terms which must match in order. These may be
	 *            instances of {@link ETokenType}, {@link ETokenClass}, or sets
	 *            of them.
	 */
	public RecognizerBase<STATE> skipTo(Object... matchTerms) {
		return appendRecognizer(new SkipToRecognizer<STATE>(matchTerms, false));
	}

	/**
	 * Creates a recognizer that skips until the given match terms are found.
	 * The new position is set directly before the found match.
	 * 
	 * @param matchTerms
	 *            a list of match terms which must match in order. These may be
	 *            instances of {@link ETokenType}, {@link ETokenClass}, or sets
	 *            of them.
	 */
	public RecognizerBase<STATE> skipBefore(Object... matchTerms) {
		return appendRecognizer(new SkipToRecognizer<STATE>(matchTerms, true));
	}

	/**
	 * Skips a region consisting of nested elements if it exists (i.e., the
	 * skipping is optional).
	 */
	public RecognizerBase<STATE> skipNested(ETokenType open, ETokenType close) {
		// we can not simply use the SkipToRecognizer here, as it implements
		// greedy skipping, i.e. would also skip multiple nested structures
		// most once if possible at all.
		return appendRecognizer(new OptionalNestedRecognizer<STATE>(open, close));
	}

	/** Skips all tokens that are of one of the given types (if any). */
	public RecognizerBase<STATE> skipAny(final EnumSet<ETokenType> types) {
		return appendRecognizer(new RecognizerBase<STATE>() {
			@Override
			protected int matchesLocally(ParserState<STATE> parserState,
					List<IToken> tokens, int startOffset) {
				while (startOffset < tokens.size()
						&& types.contains(tokens.get(startOffset).getType())) {
					startOffset += 1;
				}
				return startOffset;
			}
		});
	}

	/**
	 * Skips all tokens up to the given match term. If the match term occurs
	 * within a nested structure, it is ignored.
	 */
	public RecognizerBase<STATE> skipToWithNesting(Object matchTerm,
			ETokenType open, ETokenType close) {
		SkipToRecognizer<STATE> skipper = new SkipToRecognizer<STATE>(
				new Object[] { matchTerm }, false);
		skipper.addNestingTokens(open, close);
		return appendRecognizer(skipper);
	}

	/**
	 * Skips all tokens up to the given match term. If the match term occurs
	 * within a nested structure, it is ignored. The new position is set
	 * directly before the found match.
	 */
	public RecognizerBase<STATE> skipBeforeWithNesting(Object matchTerm,
			ETokenType open, ETokenType close) {
		SkipToRecognizer<STATE> skipper = new SkipToRecognizer<STATE>(
				new Object[] { matchTerm }, true);
		skipper.addNestingTokens(open, close);
		return appendRecognizer(skipper);
	}

	/**
	 * Skips all tokens up to the given match term. If the match term occurs
	 * within a nested structure, it is ignored. The new position is set
	 * directly before the found match.
	 */
	public RecognizerBase<STATE> skipBeforeWithNesting(Object matchTerm,
			ETokenType open1, ETokenType close1, ETokenType open2,
			ETokenType close2) {
		SkipToRecognizer<STATE> skipper = new SkipToRecognizer<STATE>(
				new Object[] { matchTerm }, true);
		skipper.addNestingTokens(open1, close1);
		skipper.addNestingTokens(open2, close2);
		return appendRecognizer(skipper);
	}

	/**
	 * Marks a different matching start position, which can be used to reference
	 * tokens during node creation.
	 */
	public RecognizerBase<STATE> markStart() {
		return appendRecognizer(new RecognizerBase<STATE>() {
			@Override
			public int matchesLocally(ParserState<STATE> parserState,
					List<IToken> tokens, int startOffset) {
				parserState.markReferencePosition(startOffset);
				return startOffset;
			}
		});
	}

	/**
	 * Creates a shallow parser entity node without a name.
	 * 
	 * @param subtype
	 *            this may be either a string which is used directly, or an
	 *            integer which is interpreted as an offset into the tokens
	 *            matched so far. The index may also be negative, which means
	 *            that the matching occurs from the end. If an array is
	 *            provided, the entries of the array are treated as described
	 *            and concatenated with spaces. If a {@link Region} is given,
	 *            the start and end values define a range of indexes.
	 */
	public RecognizerBase<STATE> createNode(EShallowEntityType type,
			Object subtype) {
		return createNode(type, subtype, null);
	}

	/**
	 * Creates a shallow parser entity node.
	 * 
	 * @param subtype
	 *            this may be either a string which is used directly, or an
	 *            integer which is interpreted as an offset into the tokens
	 *            matched so far. The index may also be negative, which means
	 *            that the matching occurs from the end. If an array is
	 *            provided, the entries of the array are treated as described
	 *            and concatenated with spaces. If a {@link Region} is given,
	 *            the start and end values define a range of indexes.
	 * @param name
	 *            this may be either null, a string which is used directly, or
	 *            an integer which is interpreted as an offset into the tokens
	 *            matched so far. The index may also be negative, which means
	 *            that the matching occurs from the end. If an array is
	 *            provided, the entries of the array are treated as described
	 *            and concatenated with spaces. If a {@link Region} is given,
	 *            the start and end values define a range of indexes.
	 */
	public RecognizerBase<STATE> createNode(final EShallowEntityType type,
			final Object subtype, final Object name) {
		return appendRecognizer(new CreateNodeRecognizer<STATE>(type, subtype,
				name));
	}

	/** Completes node construction. */
	public void endNode() {
		appendRecognizer(new RecognizerBase<STATE>() {
			@Override
			public int matches(ParserState<STATE> parserState,
					List<IToken> tokens, int startOffset) {
				parserState.endNode(false);
				return startOffset;
			}
		});
	}

	/**
	 * Completes node construction and signals that this parsed node is only a
	 * partial language construct, i.e. parsing should in any case continue at
	 * the same level.
	 */
	public void endNodeWithContinuation() {
		appendRecognizer(new RecognizerBase<STATE>() {
			@Override
			public int matches(ParserState<STATE> parserState,
					List<IToken> tokens, int startOffset) {
				parserState.endNode(true);
				return startOffset;
			}
		});
	}

	/** Parses zero or more times until the following recognizer can match. */
	public RecognizerBase<STATE> parseUntil(final STATE state) {
		return appendRecognizer(new ParseUntilRecognizer<STATE>(state, false));
	}

	/**
	 * Parses zero or more times until the following recognizer can match or the
	 * end of file is reached.
	 */
	public RecognizerBase<STATE> parseUntilOrEof(final STATE state) {
		return appendRecognizer(new ParseUntilRecognizer<STATE>(state, true));
	}

	/** Attempts to parse exactly once in the given state. */
	public RecognizerBase<STATE> parseOnce(final STATE state) {
		return appendRecognizer(new RecognizerBase<STATE>() {
			@Override
			public int matchesLocally(ParserState<STATE> parserState,
					List<IToken> tokens, int startOffset) {
				return parserState.parse(state, tokens, startOffset);
			}
		});
	}

	/** Template method that is called to append a recognizer to this node. */
	private RecognizerBase<STATE> appendRecognizer(
			RecognizerBase<STATE> recognizer) {
		tailRecognizers.add(recognizer);
		return recognizer;
	}

	/**
	 * Attempts to match the recognizer including any tail recognizers.
	 * 
	 * @param tokens
	 *            the list of tokens.
	 * @param startOffset
	 *            the current offset into the tokens from where to start
	 *            matching.
	 * @return the new offset (position right behind the match) or
	 *         {@link #NO_MATCH} if matching was not possible at this position.
	 */
	public int matches(ParserState<STATE> parserState, List<IToken> tokens,
			int startOffset) {
		int localOffset = matchesLocally(parserState, tokens, startOffset);
		if (localOffset == NO_MATCH) {
			return NO_MATCH;
		}

		if (tailRecognizers.isEmpty()) {
			return localOffset;
		}

		for (RecognizerBase<STATE> recognizer : tailRecognizers) {
			int match = recognizer.matches(parserState, tokens, localOffset);
			if (match != NO_MATCH) {
				return match;
			}
		}

		return NO_MATCH;
	}

	/**
	 * Attempts to match the recognizer locally (without respecting tail
	 * recognizers).
	 * 
	 * @param parserState
	 *            the current state of the underlying parser.
	 * @param tokens
	 *            the list of tokens.
	 * @param startOffset
	 *            the current offset into the tokens from where to start
	 *            matching.
	 * @return the new offset (position right behind the match) or
	 *         {@link #NO_MATCH} if matching was not possible at this position.
	 */
	protected int matchesLocally(ParserState<STATE> parserState,
			List<IToken> tokens, int startOffset) {
		return startOffset;
	}
}
