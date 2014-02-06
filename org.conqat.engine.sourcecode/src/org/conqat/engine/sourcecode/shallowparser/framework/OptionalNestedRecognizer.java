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

import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.IToken;

/**
 * Recognizer for skipping optional nested structures.
 * 
 * @author $Author: goede $
 * @version $Rev: 40601 $
 * @ConQAT.Rating GREEN Hash: 6EF54FDEA0BE56C1E24002BA04059151
 */
/* package */class OptionalNestedRecognizer<STATE extends Enum<STATE>> extends
		RecognizerBase<STATE> {

	/** Type that opens a nesting level. */
	private final ETokenType open;

	/** Type that closes a nesting level. */
	private final ETokenType close;

	/** Constructor. */
	public OptionalNestedRecognizer(ETokenType open, ETokenType close) {
		this.open = open;
		this.close = close;
	}

	/** {@inheritDoc} */
	@Override
	protected int matchesLocally(ParserState<STATE> parserState,
			List<IToken> tokens, int startOffset) {

		// nothing to skip
		if (startOffset >= tokens.size()
				|| tokens.get(startOffset).getType() != open) {
			return startOffset;
		}

		int depth = 1;
		startOffset += 1;

		while (startOffset < tokens.size() && depth > 0) {
			if (tokens.get(startOffset).getType() == open) {
				depth += 1;
			} else if (tokens.get(startOffset).getType() == close) {
				depth -= 1;
			}
			startOffset += 1;
		}

		if (depth > 0) {
			return NO_MATCH;
		}
		return startOffset;
	}
}
