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

import java.util.List;

import org.conqat.lib.commons.collections.CollectionUtils;

/**
 * This class implements a unit for lists of tokens that represent statements.
 * 
 * @author $Author: juergens $
 * @version $Revision: 46598 $
 * @ConQAT.Rating GREEN Hash: 650F8CF5F44AB1643EBC000111045C56
 */
public class StatementUnit extends Unit {

	/** List of tokens this statement comprises. */
	private TokenUnit[] tokens;

	/** Create new StatementUnit */
	public StatementUnit(int filteredStartOffset, int filteredEndOffset,
			String elementUniformPath, String content,
			String unnormalizedContent, int indexInElement) {
		super(filteredStartOffset, filteredEndOffset, elementUniformPath,
				content, unnormalizedContent, indexInElement);
	}

	/**
	 * Constructor that allows storage of token list.
	 * <p>
	 * Caution: If {@link StatementUnit} is used during clone detection by the
	 * suffix tree, storage of token list requires a lot of memory!
	 * 
	 * @param tokenList
	 *            List of tokens the {@link StatementUnit} comprises
	 * @param storeTokens
	 *            Flag that determines whether list is stored
	 */
	public StatementUnit(List<TokenUnit> tokenList, String elementUniformPath,
			boolean storeTokens, int indexInElement) {
		super(tokenList.get(0).getFilteredStartOffset(), CollectionUtils
				.getLast(tokenList).getFilteredEndOffset(), elementUniformPath,
				createContent(tokenList), createUnnormalizedContent(tokenList),
				indexInElement);
		if (storeTokens) {
			tokens = tokenList.toArray(new TokenUnit[] {});
		}
	}

	/**
	 * Creates unnormalized unit content string from a token list
	 * <p>
	 * Since we call this method from within the call to the superclass
	 * constructor, it must be static.
	 */
	private static String createUnnormalizedContent(List<TokenUnit> tokens) {
		StringBuilder builder = new StringBuilder();
		for (TokenUnit token : tokens) {
			if (builder.length() > 0) {
				builder.append(" ");
			}
			builder.append(token.getUnnormalizedContent());
		}
		return builder.toString();
	}

	/**
	 * Creates unit content string from a token list
	 * <p>
	 * Since we call this method from within the call to the superclass
	 * constructor, it must be static.
	 */
	private static String createContent(List<TokenUnit> tokens) {
		StringBuilder builder = new StringBuilder();
		for (TokenUnit token : tokens) {
			builder.append(token.getContent());
		}
		return builder.toString();
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return getContent() + " [" + getElementUniformPath() + "(f-offset: "
				+ getFilteredStartOffset() + ")][index:" + getIndexInElement()
				+ "]";
	}

	/**
	 * Gets stored tokens.
	 * 
	 * @throws IllegalStateException
	 *             , if underlying tokens have not been stored
	 */
	public TokenUnit[] getTokens() {
		assertTokensStored();

		return tokens;
	}

	/** Throws an {@link IllegalStateException}, if tokens have not been stored */
	private void assertTokensStored() {
		if (tokens == null) {
			throw new IllegalStateException(
					"In order to access the underlying tokens, StatementUnit must store its tokens. "
							+ "(Set storeTokens flag in constructor)");
		}
	}

}