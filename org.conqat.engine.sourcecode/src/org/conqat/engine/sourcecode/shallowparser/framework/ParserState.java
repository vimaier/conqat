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
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.collections.PairList;
import org.conqat.lib.scanner.IToken;

/**
 * This class holds the parser state and also implements parts of the parsing
 * algorithm. The benefit of separating them into a separate class is that we
 * can reuse the same parser multiple times without resets and also concurrently
 * in separate threads.
 * 
 * @see ShallowParserBase
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43891 $
 * @ConQAT.Rating GREEN Hash: 04AD01C1DFBB6C4EDBA79E5324DEFE21
 */
public class ParserState<STATE extends Enum<STATE>> {

	/** Pairs of states (for which the recognizer is active) and the recognizer. */
	private final PairList<Set<STATE>, RecognizerBase<STATE>> recognizers;

	/** Constructor. */
	/* package */ParserState(
			PairList<Set<STATE>, RecognizerBase<STATE>> recognizers) {
		this.recognizers = recognizers;
	}

	/**
	 * Stack of entities created. If there are multiple top-level entities, this
	 * only contains the most recently created.
	 */
	private final Stack<ShallowEntity> entities = new Stack<ShallowEntity>();

	/** Stack of start positions for nested parses. */
	private final Stack<Integer> currentMatchStart = new Stack<Integer>();

	/** Stack of reference positions used for naming. */
	private final Stack<Integer> currentReferencePosition = new Stack<Integer>();

	/** The result list of all created top-level entities. */
	final List<ShallowEntity> result = new ArrayList<ShallowEntity>();

	/** Sets the given shallow entity as new target for adding children. */
	public void setNode(ShallowEntity shallowEntity) {
		if (entities.isEmpty()) {
			result.add(shallowEntity);
		} else {
			entities.peek().addChild(shallowEntity);
		}
		entities.push(shallowEntity);
	}

	/** Marks the current node as completed. */
	public void endNode(boolean expectsContinuation) {
		CCSMAssert.isFalse(entities.isEmpty(),
				"Must have setNode() before each endNode()!");
		entities.peek().setComplete(expectsContinuation);
	}

	/**
	 * Attempts to parse exactly one entity in the given state. If the created
	 * entity is a continued node, this automatically continues parsing.
	 * 
	 * @return Either the new parsing offset or {@link RecognizerBase#NO_MATCH}.
	 */
	public int parse(STATE state, List<IToken> tokens, int startOffset) {
		int entitiesSize = entities.size();

		int resultOffset = RecognizerBase.NO_MATCH;
		for (int i = 0; i < recognizers.size(); ++i) {
			if (recognizers.getFirst(i).contains(state)) {
				currentMatchStart.push(startOffset);
				currentReferencePosition.push(startOffset);

				int match = recognizers.getSecond(i).matches(this, tokens,
						startOffset);

				currentReferencePosition.pop();
				currentMatchStart.pop();

				if (match != RecognizerBase.NO_MATCH) {
					resultOffset = match;
					break;
				}
			}
		}

		// there was an entity created that needs completion
		if (entities.size() > entitiesSize) {
			ShallowEntity entity = entities.pop();
			entity.setEndTokenIndex(resultOffset);

			// continue parsing if this is a continued entity (such as an
			// "if" followed by an "else").
			if (entity.isContinued() && resultOffset != RecognizerBase.NO_MATCH) {
				int nextOffset = parse(state, tokens, resultOffset);

				if (nextOffset != RecognizerBase.NO_MATCH) {
					return nextOffset;
					// otherwise fall through and use last parsing position
				}
			}
		}

		return resultOffset;
	}

	/** Returns the start position of the currently running match. */
	public int getCurrentMatchStart() {
		return currentMatchStart.peek();
	}

	/** Returns the reference position of the currently running match. */
	public int getCurrentReferencePosition() {
		return currentReferencePosition.peek();
	}

	/** Stores the given position as match start. */
	public void markReferencePosition(int offset) {
		currentReferencePosition.pop();
		currentReferencePosition.push(offset);
	}
}