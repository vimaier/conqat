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
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.conqat.engine.sourcecode.shallowparser.IShallowParser;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.collections.PairList;
import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.ETokenType.ETokenClass;
import org.conqat.lib.scanner.IToken;

/**
 * Base class for shallow parsers that implements most of the generic parsing
 * logic.
 * 
 * @param <STATE>
 *            the enum used for describing parse states.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: AAAE0E0DFAA4670925A29A7F03ADE7E1
 */
public abstract class ShallowParserBase<STATE extends Enum<STATE>> implements
		IShallowParser {

	/** The class of the enum used for describing the states. */
	private final Class<STATE> stateClass;

	/** The initial state corresponding to a top-level element. */
	private final STATE initialState;

	/** Pairs of states (for which the recognizer is active) and the recognizer. */
	private final PairList<Set<STATE>, RecognizerBase<STATE>> recognizers = new PairList<Set<STATE>, RecognizerBase<STATE>>();

	/** Constructor. */
	protected ShallowParserBase(Class<STATE> stateClass, STATE initialState) {
		this.stateClass = stateClass;
		this.initialState = initialState;
	}

	/** Creates a rule that is active in each of the given states. */
	protected RecognizerBase<STATE> inState(STATE... states) {
		RecognizerBase<STATE> recognizer = emptyRecognizer();

		// we need this seemingly complicated construct as states may be empty
		EnumSet<STATE> stateSet = EnumSet.noneOf(stateClass);
		stateSet.addAll(Arrays.asList(states));
		recognizers.add(stateSet, recognizer);

		return recognizer;
	}

	/** Returns an empty recognizer that can be used for local sub-rules. */
	protected RecognizerBase<STATE> emptyRecognizer() {
		return new RecognizerBase<STATE>() {
			// empty
		};
	}

	/** Creates a rule that is active in any state. */
	protected RecognizerBase<STATE> inAnyState() {
		return inState(stateClass.getEnumConstants());
	}

	/**
	 * Template method that can be used to ignore/filter certain tokens. Default
	 * implementation ignores comments.
	 * 
	 * @param previousToken
	 *            the last non-filtered token (can be used for context sensitive
	 *            filtering). This may be null is no previous token exists.
	 */
	protected boolean isFilteredToken(IToken token, IToken previousToken) {
		return token.getType().getTokenClass() == ETokenClass.COMMENT;
	}

	/** {@inheritDoc} */
	@Override
	public List<ShallowEntity> parseTopLevel(List<IToken> tokens) {
		tokens = filterTokens(tokens);
		return parse(initialState, tokens);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This works by attempting to parse from all possible states and returns
	 * the best result, where 'best' is measured in terms of completed entities.
	 */
	@Override
	public List<ShallowEntity> parseFragment(List<IToken> tokens) {
		tokens = filterTokens(tokens);

		List<ShallowEntity> best = new ArrayList<ShallowEntity>();
		int bestCount = 0;
		int bestStart = Integer.MAX_VALUE;

		for (STATE state : stateClass.getEnumConstants()) {
			List<ShallowEntity> list = parse(state, tokens);
			int count = countCompleteEntities(list);
			int start = Integer.MAX_VALUE;
			if (!list.isEmpty()) {
				start = list.get(0).getStartTokenIndex();
			}

			// prefer parse results that produce more entities (or if same
			// number those that start earlier, i.e., skip less tokens)
			if (count > bestCount || (count == bestCount && start < bestStart)) {
				best = list;
				bestCount = count;
				bestStart = start;
			}
		}
		return best;
	}

	/** Recursively counts and returns the entities contained in the list. */
	private int countCompleteEntities(List<ShallowEntity> list) {
		int result = 0;
		for (ShallowEntity entity : list) {
			result += entity.getCompleteEntityCount();
		}
		return result;
	}

	/** Creates a filtered view of the tokens. */
	private List<IToken> filterTokens(List<IToken> tokens) {
		List<IToken> result = new ArrayList<IToken>();
		IToken previousToken = null;
		for (IToken token : tokens) {
			if (!isFilteredToken(token, previousToken)) {
				result.add(token);
				previousToken = token;
			}
		}
		return result;
	}

	/**
	 * Performs a shallow parse for the given list of tokens and given initial
	 * state.
	 */
	protected List<ShallowEntity> parse(STATE startState, List<IToken> tokens) {
		ParserState<STATE> parserState = new ParserState<STATE>(recognizers);

		int offset = 0;
		while (offset < tokens.size()) {
			int match = parserState.parse(startState, tokens, offset);
			if (match == RecognizerBase.NO_MATCH) {
				offset += 1;
			} else {
				CCSMAssert.isTrue(match > offset,
						"The parser does not make any progress!");
				offset = match;
			}
		}

		return parserState.result;
	}

	/**
	 * Completes a recognizer with an end. If the next token is one of the
	 * provided continuation tokens, the entity creation finishes with a
	 * continued node.
	 * 
	 * @param continuationTokens
	 *            list of tokens that indicate a continued statement if
	 *            encountered at the end. May be null.
	 */
	protected void endWithPossibleContinuation(
			RecognizerBase<STATE> recognizer,
			EnumSet<ETokenType> continuationTokens) {
		if (continuationTokens != null && !continuationTokens.isEmpty()) {
			recognizer.sequenceBefore(continuationTokens)
					.endNodeWithContinuation();
		}
		recognizer.endNode();
	}
}
