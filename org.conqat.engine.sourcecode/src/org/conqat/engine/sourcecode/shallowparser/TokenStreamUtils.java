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
package org.conqat.engine.sourcecode.shallowparser;

import java.util.EnumSet;
import java.util.List;

import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.ETokenType.ETokenClass;
import org.conqat.lib.scanner.IToken;

/**
 * Utility methods for {@link IToken} lists.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 47148 $
 * @ConQAT.Rating GREEN Hash: 796B224E8C28C25F6296BA38A273901F
 */
public class TokenStreamUtils {

	/** The value returned if nothing was found in the various find methods. */
	public static final int NOT_FOUND = -1;

	/**
	 * Returns the index of the first token of given token type (or
	 * {@link #NOT_FOUND}).
	 */
	public static int find(List<IToken> tokens, ETokenType tokenType) {
		return find(tokens, tokenType, 0);
	}

	/**
	 * Returns the index of the first token of given token type not before the
	 * start index (or {@link #NOT_FOUND}).
	 */
	public static int find(List<IToken> tokens, ETokenType tokenType,
			int startIndex) {
		return find(tokens, tokenType, startIndex, tokens.size());
	}

	/**
	 * Returns the index of the first token of given token type not before the
	 * start index and before the end index (or {@link #NOT_FOUND}).
	 */
	public static int find(List<IToken> tokens, ETokenType tokenType,
			int startIndex, int endIndex) {
		startIndex = Math.max(0, startIndex);
		endIndex = Math.min(endIndex, tokens.size());

		for (int i = startIndex; i < endIndex; ++i) {
			if (tokens.get(i).getType() == tokenType) {
				return i;
			}
		}

		return NOT_FOUND;
	}

	/**
	 * Returns the index of the last token of given token type (or
	 * {@link #NOT_FOUND}).
	 */
	public static int findLast(List<IToken> tokens, ETokenType tokenType) {
		return findLast(tokens, tokenType, 0);
	}

	/**
	 * Returns the index of the last token of given token type not before the
	 * start index (or {@link #NOT_FOUND}).
	 */
	public static int findLast(List<IToken> tokens, ETokenType tokenType,
			int startIndex) {
		return findLast(tokens, tokenType, startIndex, tokens.size());
	}

	/**
	 * Returns the index of the last token of given token type not before the
	 * start index and before the end index (or {@link #NOT_FOUND}).
	 */
	public static int findLast(List<IToken> tokens, ETokenType tokenType,
			int startIndex, int endIndex) {
		startIndex = Math.max(0, startIndex);
		endIndex = Math.min(endIndex, tokens.size());

		for (int i = endIndex - 1; i >= startIndex; --i) {
			if (tokens.get(i).getType() == tokenType) {
				return i;
			}
		}

		return NOT_FOUND;
	}

	/**
	 * Returns <code>true</code> if the list of tokens contains a token of
	 * <code>tokenType<code>, false otherwise.
	 */
	public static boolean tokenStreamContains(List<IToken> tokens,
			ETokenType tokenType) {
		return tokenStreamContains(tokens, 0, tokens.size(), tokenType);
	}

	/**
	 * Returns <code>true</code> if the list of tokens contains a token of
	 * <code>tokenType<code> in the given range, false otherwise.
	 */
	public static boolean tokenStreamContains(List<IToken> tokens,
			int startIndex, int endIndex, ETokenType tokenType) {
		return find(tokens, tokenType, startIndex, endIndex) != NOT_FOUND;
	}

	/**
	 * Returns <code>true</code> if the list of tokens contains a any token of
	 * given <code>tokenTypes<code>, false otherwise.
	 */
	public static boolean containsAny(List<IToken> tokens,
			ETokenType... tokenTypes) {
		return containsAny(tokens, 0, tokens.size(), tokenTypes);
	}

	/**
	 * Returns <code>true</code> if the list of tokens contains a any token of
	 * given <code>tokenTypes<code> in the given range, false otherwise.
	 */
	public static boolean containsAny(List<IToken> tokens, int startIndex,
			int endIndex, ETokenType... tokenTypes) {
		if (tokenTypes.length == 0) {
			return false;
		}

		EnumSet<ETokenType> types = EnumSet.of(tokenTypes[0], tokenTypes);
		for (int i = startIndex; i < endIndex; ++i) {
			if (types.contains(tokens.get(i).getType())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns <code>true</code> if the list of tokens contains at least one
	 * token of each of the given <code>tokenTypes<code>, false otherwise.
	 */
	public static boolean containsAll(List<IToken> tokens,
			ETokenType... tokenTypes) {
		return containsAll(tokens, 0, tokens.size(), tokenTypes);
	}

	/**
	 * Returns <code>true</code> if the list of tokens contains at least one
	 * token of each of the given
	 * <code>tokenTypes<code> in the given range, false otherwise.
	 */
	public static boolean containsAll(List<IToken> tokens, int startIndex,
			int endIndex, ETokenType... tokenTypes) {
		if (tokenTypes.length == 0) {
			return true;
		}

		EnumSet<ETokenType> types = EnumSet.of(tokenTypes[0], tokenTypes);
		for (int i = startIndex; i < endIndex; ++i) {
			types.remove(tokens.get(i).getType());
			if (types.isEmpty()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns <code>true</code> if the list of tokens contains a consecutive
	 * subsequence of tokens that match the sequence of token types., false
	 * otherwise.
	 */
	public static boolean containsSequence(List<IToken> tokens, int startIndex,
			int endIndex, ETokenType... tokenTypes) {
		OUTER: for (int i = startIndex; i <= endIndex - tokenTypes.length; ++i) {
			for (int j = 0; j < tokenTypes.length; ++j) {
				if (tokens.get(i + j).getType() != tokenTypes[j]) {
					continue OUTER;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * Returns <code>true</code> if the list of tokens contains a token of
	 * <code>tokenClass<code>, false otherwise.
	 */
	public static boolean tokenStreamContains(List<IToken> tokens,
			ETokenClass tokenClass) {
		for (IToken token : tokens) {
			if (token.getType().getTokenClass() == tokenClass) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the sublist of tokens between the first occurrence of given start
	 * token type and the first occurrence of end token type (after start). If
	 * one of them is not found, an empty list is returned. The tokens for the
	 * start and end are not included in the returned sub list.
	 */
	public static List<IToken> tokensBetween(List<IToken> tokens,
			ETokenType startType, ETokenType endType) {
		int start = TokenStreamUtils.find(tokens, startType);
		if (start == NOT_FOUND) {
			return CollectionUtils.emptyList();
		}
		start += 1;

		int end = TokenStreamUtils.find(tokens, endType, start);
		if (end == NOT_FOUND) {
			return CollectionUtils.emptyList();
		}

		return tokens.subList(start, end);
	}
}
