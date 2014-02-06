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
package org.conqat.engine.code_clones.core;

import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.ETokenType.ETokenClass;

/**
 * Unit-implementation for tokens.
 * 
 * @author $Author: hummelb $
 * @version $Revision: 43764 $
 * @ConQAT.Rating GREEN Hash: 67DF1754EC1ADC70D71F3E202028016F
 */
public class TokenUnit extends Unit {

	/** Type of underlying token */
	private final ETokenType tokenType;

	/**
	 * Create new {@link TokenUnit}.
	 * 
	 * @param content
	 *            the token's content
	 * @param elementUniformPath
	 *            Uniform path of the element this unit stems from
	 */
	public TokenUnit(String content, String unnormalizedContent,
			int filteredStartOffset, int filteredEndOffset,
			String elementUniformPath, ETokenType tokenType, int indexInElement) {
		super(filteredStartOffset, filteredEndOffset, elementUniformPath,
				content, unnormalizedContent, indexInElement);
		this.tokenType = tokenType;
	}

	/**
	 * Create new {@link TokenUnit} with same normalized and unnormalized
	 * content.
	 */
	public TokenUnit(String content, int rawStartOffset, int rawEndOffset,
			String elementUniformPath, ETokenType tokenType, int indexInElement) {
		this(content, content, rawStartOffset, rawEndOffset,
				elementUniformPath, tokenType, indexInElement);
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		String result = "TOKEN '" + getContent() + "' (" + getType() + ")";
		result += " \"" + getElementUniformPath() + "\"" + " f-offset "
				+ getFilteredStartOffset();
		return result;
	}

	/** Returns type of underlying token. */
	public ETokenType getType() {
		return tokenType;
	}

	/** Determines whether the content of a token type could be normalized */
	public static boolean couldBeNormalized(ETokenType tokenType) {
		ETokenClass tokenClass = tokenType.getTokenClass();
		return tokenClass == ETokenClass.IDENTIFIER
				|| tokenClass == ETokenClass.LITERAL
				|| tokenType == ETokenType.WORD;
	}

}