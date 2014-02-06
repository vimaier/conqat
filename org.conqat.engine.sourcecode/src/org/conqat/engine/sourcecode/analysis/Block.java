/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2011 The ConQAT Project                                   |
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
package org.conqat.engine.sourcecode.analysis;

import java.util.List;

import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.UnmodifiableList;
import org.conqat.lib.scanner.IToken;

/**
 * Block in source code. This class is immutable.
 * 
 * @author $Author: juergens $
 * @version $Rev: 35204 $
 * @ConQAT.Rating GREEN Hash: 8E4E428A87540718446C3F602D425126
 */
public class Block {

	/** List of tokens of the block */
	private final List<IToken> tokens;

	/** Constructor */
	public Block(List<IToken> tokens) {
		this.tokens = tokens;
	}

	/** Get first token of block */
	public IToken getFirst() {
		return tokens.get(0);
	}

	/** Get last token of block */
	public IToken getLast() {
		return CollectionUtils.getLast(tokens);
	}

	/** Get tokens. */
	public UnmodifiableList<IToken> getTokens() {
		return CollectionUtils.asUnmodifiable(tokens);
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();
		for (IToken token : tokens) {
			if (!token.equals(tokens.get(0))) {
				result.append(",");
			}
			result.append("[" + token.getText() + "]");
		}
		return result.toString();
	}
}