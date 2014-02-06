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

import java.lang.reflect.Array;
import java.util.List;

import org.conqat.lib.commons.assertion.CCSMPre;
import org.conqat.lib.commons.region.Region;
import org.conqat.lib.scanner.IToken;

/**
 * The recognizer used to implement creation of nodes in the parse tree, i.e.
 * ShallowEntities.
 * 
 * @param <STATE>
 *            the enum used for describing parse states.
 * 
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 7056A7040418B8DE72663D7BEC77593E
 */
/* package */class CreateNodeRecognizer<STATE extends Enum<STATE>> extends
		RecognizerBase<STATE> {

	/** The type. */
	private final EShallowEntityType type;

	/**
	 * The subtype, which should be either a string or an integer or an array of
	 * these.
	 */
	private final Object subtype;

	/**
	 * The name, which should be either a string or an integer or an array of
	 * these.
	 */
	private final Object name;

	/**
	 * Constructor. The subtype and name can either be null, a constant name
	 * (String), an index into the token stream (int), a {@link Region} of the
	 * token stream, or an array (indicating multiple of those mentioned before.
	 */
	public CreateNodeRecognizer(EShallowEntityType type, Object subtype,
			Object name) {
		checkNameParameter(subtype);
		checkNameParameter(name);

		this.type = type;
		this.subtype = subtype;
		this.name = name;
	}

	/**
	 * Checks the type of a naming parameter. This can either be null, a
	 * constant name (String), an index into the token stream (int), a
	 * {@link Region} of the token stream, or an array (indicating multiple of
	 * those mentioned before.
	 */
	private void checkNameParameter(Object subtype) {
		CCSMPre.isTrue(subtype == null || subtype instanceof String
				|| subtype instanceof Integer || subtype instanceof Region
				|| subtype.getClass().isArray(),
				"Parameter must be null, String, int, Region or array!");
	}

	/** {@inheritDoc} */
	@Override
	public int matchesLocally(ParserState<STATE> parserState,
			List<IToken> tokens, int startOffset) {
		String resolvedSubtype = resolve(tokens, parserState, startOffset,
				subtype);
		String resolvedName = resolve(tokens, parserState, startOffset, name);
		parserState.setNode(new ShallowEntity(type, resolvedSubtype,
				resolvedName, tokens, parserState.getCurrentMatchStart()));
		return startOffset;
	}

	/** {@inheritDoc} */
	@Override
	public int matches(ParserState<STATE> parserState, List<IToken> tokens,
			int startOffset) {
		// Make sure that we always return a match after creating a node.
		// The "max" is used to handle the case of super.matches() returning -1
		return Math.max(startOffset,
				super.matches(parserState, tokens, startOffset));
	}

	/**
	 * Resolves a {@link #name} or {@link #subtype} based on the token stream
	 * and the parser state. The "name" can either be null, a constant name
	 * (String), an index into the token stream (int), a {@link Region} of the
	 * token stream, or an array (indicating multiple of those mentioned before.
	 */
	private String resolve(List<IToken> tokens, ParserState<STATE> parserState,
			int startOffset, Object name) {
		if (name == null) {
			return null;
		}

		if (name instanceof String) {
			return (String) name;
		}

		if (name instanceof Number) {
			int index = ((Number) name).intValue();
			return tokens.get(resolveIndex(parserState, startOffset, index))
					.getText();
		}

		if (name instanceof Region) {
			Region r = (Region) name;
			int start = resolveIndex(parserState, startOffset, r.getStart());
			int end = resolveIndex(parserState, startOffset, r.getEnd());
			StringBuilder sb = new StringBuilder();
			for (int i = start; i <= end; ++i) {
				sb.append(tokens.get(i).getText());
			}
			return sb.toString();
		}

		if (name.getClass().isArray()) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < Array.getLength(name); ++i) {
				if (i > 0) {
					sb.append(" ");
				}
				sb.append(resolve(tokens, parserState, startOffset,
						Array.get(name, i)));
			}
			return sb.toString();
		}

		throw new AssertionError("Unexpected type in resolving of name: "
				+ name.getClass());
	}

	/** Resolves the index relative to the parser state. */
	private int resolveIndex(ParserState<STATE> parserState, int startOffset,
			int index) {
		if (index >= 0) {
			index = parserState.getCurrentReferencePosition() + index;
		} else {
			// we need addition here, as index is negative
			index = startOffset + index;
		}
		return index;
	}
}