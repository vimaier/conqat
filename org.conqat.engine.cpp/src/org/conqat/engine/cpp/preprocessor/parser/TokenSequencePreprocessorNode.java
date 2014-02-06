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
package org.conqat.engine.cpp.preprocessor.parser;

import java.util.ArrayList;
import java.util.List;

import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.UnmodifiableList;
import org.conqat.lib.scanner.IToken;

/**
 * A sequence of tokens. We represent an entire sequence of tokens by a single
 * node, as this is more convenient for most analyses.
 * 
 * @author $Author: feilkas $
 * @version $Rev: 41746 $
 * @ConQAT.Rating GREEN Hash: D3A456B06E49C7562C541DA59CC74731
 */
public class TokenSequencePreprocessorNode extends PreprocessorNodeBase {

	/** The tokens in the sequence. */
	private final List<IToken> tokens = new ArrayList<IToken>();

	/** Adds a token. */
	/* package */void addToken(IToken token) {
		tokens.add(token);
	}

	/** Returns the tokens. */
	public UnmodifiableList<IToken> getTokens() {
		return CollectionUtils.asUnmodifiable(tokens);
	}

}
