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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.IStatementOracle;
import org.conqat.lib.scanner.IToken;
import org.conqat.lib.scanner.TokenListLookahead;

/**
 * Determines the locations of specified blocks in source files. The
 * {@link BlockParser} can e.g. be used to determine methods.
 * 
 * The parser searches for matching block start and end tokens. For each block,
 * its nesting depth w.r.t. its surrounding blocks is recorded. The parser only
 * reports blocks that start and end at a specified nesting depth, to e.g.
 * discover methods.
 * 
 * Depth counting is relative to scopes. A scope is e.g. a class in Java or a
 * namespace in C#. Which keywords open new scopes is parameterized. The parser
 * is thus language independent.
 * 
 * Does currently not support nested or overlapping blocks.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 37506 $
 * @ConQAT.Rating GREEN Hash: 9AA56DEDE6D3B8960D6768FE2B676E95
 */
public class BlockParser {

	/** Constant used to initialize scope depth */
	private static final int EMPTY = -1;

	/** Determines how to match method regions */
	public enum EMatchStyle {

		/** Only body (text between opening and closing braces) is matched. */
		ONLY_BODY,

		/**
		 * Body and declaration, i.e. everything since the end of the last
		 * statement before the block start.
		 */
		DECLARATION_AND_BODY;

	}

	/** Set of {@link ETokenType}s that opens a new block. */
	private final Set<ETokenType> openBlockTokens;

	/** Set of {@link ETokenType}s that closes a new block. */
	private final Set<ETokenType> closeBlockTokens;

	/** Matching style */
	private final EMatchStyle matchStyle;

	/** Keywords that open a new scope */
	private final Set<ETokenType> scopeKeywords;

	/** Stack of counters that keeps track of nesting depth. */
	private final Stack<Integer> scopes = new Stack<Integer>();

	/** Nesting depth at which we record blocks */
	private final int blockDepth;

	/**
	 * Constant that indicates that the RBrace closing a block could not be
	 * found.
	 */
	public static final int CLOSING_RBRACE_NOT_FOUND = -1;

	/**
	 * Constructor.
	 * 
	 * @param blockDepth
	 *            nesting depth at which blocks are recorded. Counting starts at
	 *            0. A method in a Java class, e.g., thus has depth 1.
	 * @param matchStyle
	 *            determines what constitutes a block
	 * @param scopeKeywords
	 *            Keywords that open a new scope that can contain blocks
	 * @throws ConQATException
	 *             if open and close block token types overlap
	 */
	public BlockParser(int blockDepth, EMatchStyle matchStyle,
			Set<ETokenType> scopeKeywords, Set<ETokenType> openBlockTokens,
			Set<ETokenType> closeBlockTokens) throws ConQATException {
		this.blockDepth = blockDepth;
		this.matchStyle = matchStyle;
		this.scopeKeywords = scopeKeywords;
		this.openBlockTokens = openBlockTokens;
		this.closeBlockTokens = closeBlockTokens;

		// make sure that open and close block tokens do not overlap
		HashSet<ETokenType> overlap = new HashSet<ETokenType>(openBlockTokens);
		overlap.retainAll(closeBlockTokens);
		if (!overlap.isEmpty()) {
			throw new ConQATException(
					"Open and close block tokens must not overlap! Overlapping token types: "
							+ StringUtils.concat(overlap, ", "));
		}
	}

	/**
	 * Parse a list of tokens into a list of pairs of tokens that indicate block
	 * start and ends.
	 * 
	 * @param tokens
	 *            List of tokens that gets parsed.
	 * @throws BlockParserException
	 *             if an ill-formed nesting was discovered
	 */
	public List<Block> createBlocks(List<IToken> tokens)
			throws BlockParserException {
		// we access it via indexes, so we make sure it supports efficient
		// random access
		tokens = CollectionUtils.asRandomAccessList(tokens);

		List<Block> blocks = new ArrayList<Block>();

		int regionStart = 0;
		for (int i = 0; i < tokens.size(); i++) {
			IToken token = tokens.get(i);

			// Creates a new scope, if a scope keyword is seen
			if (scopeKeywords.contains(token.getType())) {
				scopes.push(EMPTY);
			}

			if (openBlockTokens.contains(token.getType())) {
				incNestingDepth();
				if (hasBlockDepth()) {
					regionStart = findHead(i, tokens);
				}
			}

			if (closeBlockTokens.contains(token.getType())) {
				if (hasBlockDepth()) {
					Block block = new Block(tokens.subList(regionStart, i + 1));

					// we expect a block to contain at least its start and its
					// end token. otherwise the parser has a bug.
					CCSMAssert.isTrue(block.getTokens().size() > 0,
							"Empty block encountered!");
					blocks.add(block);
				}

				decNestingDepth();
				// don't delete the last scope
				if (getDepth() == EMPTY && scopes.size() > 1) {
					scopes.pop();
				}
			}
		}

		return blocks;
	}

	/**
	 * Determines the first token of the block declaration, depending on the
	 * employed match style.
	 * 
	 * If match style is {@link EMatchStyle#DECLARATION_AND_BODY}, we walk back
	 * from the head token until we find an end-of-statement token. The head is
	 * then the token that appears directly after the last end-of-statement
	 * token before the openBlock token.
	 */
	private int findHead(int openBlockIndex, List<IToken> tokens) {
		if (matchStyle == EMatchStyle.ONLY_BODY) {
			return openBlockIndex;
		}

		// Walks back from the lbrace token that opens a block to the head
		// token of the block declaration. We expect this walk not to be too
		// expensive in practice, since we don't expect many statements between
		// the start of a block and the last end-of-statement token before that.
		int currentIndex = openBlockIndex;
		CCSMAssert.isTrue(currentIndex >= 0,
				"Token not contained in tokens list");

		while (currentIndex > 0 && inStatement(tokens, currentIndex - 1)) {
			currentIndex -= 1;
		}

		return currentIndex;
	}

	/** Returns true if token is no end-of-statement token */
	private boolean inStatement(List<IToken> tokens, int index) {
		TokenListLookahead lookahead = new TokenListLookahead(tokens, index);

		IToken token = tokens.get(index);
		ETokenType tokenType = token.getType();
		IStatementOracle statementOracle = token.getLanguage()
				.getStatementOracle();

		return !statementOracle.isEndOfStatementTokenType(tokenType, lookahead)
				&& !openBlockTokens.contains(tokenType)
				&& !closeBlockTokens.contains(tokenType);
	}

	/** Checks whether a token is in method depth */
	private boolean hasBlockDepth() throws BlockParserException {
		return getDepth() == blockDepth;
	}

	/** Returns depth */
	private int getDepth() throws BlockParserException {
		if (scopes.isEmpty()) {
			throw new BlockParserException();
		}
		return scopes.peek();
	}

	/**
	 * Increment the element on top of the scope stack, except if it is the only
	 * element.
	 */
	private void incNestingDepth() {
		int oldDepth = popExceptEmpty();
		scopes.push(oldDepth + 1);
	}

	/**
	 * Returns the top element from the stack, if it is not empty. If it is
	 * empty, it returns 0.
	 */
	private int popExceptEmpty() {
		int value = 0;
		if (!scopes.isEmpty()) {
			value = scopes.pop();
		}
		return value;
	}

	/** Decrements current depth counter */
	private void decNestingDepth() {
		int oldDepth = popExceptEmpty();
		// if the stack is empty, this pushes -1, which equals EMPTY
		scopes.push(oldDepth - 1);
	}

	/**
	 * Determines offset of RBRACE that closes block
	 * 
	 * @param blockStart
	 *            character offset at which block starts
	 * @param tokens
	 *            tokens of the file
	 * @return character offset of closing {@link ETokenType#RBRACE}, or -1, if
	 *         closing {@link ETokenType#RBRACE} cannot be determined
	 */
	public static int findBlockEnd(int blockStart, List<IToken> tokens)
			throws ConQATException {
		// skip tokens before start of block
		int startTokenIndex = 0;
		for (IToken token : tokens) {
			if (token.getOffset() < blockStart) {
				startTokenIndex++;
			}
		}

		int tokenIndex = findBlockEndToken(startTokenIndex, tokens);
		if (tokenIndex == BlockParser.CLOSING_RBRACE_NOT_FOUND) {
			return BlockParser.CLOSING_RBRACE_NOT_FOUND;
		}

		return tokens.get(tokenIndex).getOffset();
	}

	/**
	 * Determines index of RBRACE that closes block
	 * 
	 * @param blockStart
	 *            character offset at which block starts
	 * @param tokens
	 *            tokens of the file
	 * @return index of token of closing {@link ETokenType#RBRACE}, or -1, if
	 *         closing {@link ETokenType#RBRACE} cannot be determined
	 */
	public static int findBlockEndToken(int blockStart, List<IToken> tokens)
			throws ConQATException {
		int nestingDepth = 0;

		for (int i = blockStart; i < tokens.size(); i++) {
			IToken token = tokens.get(i);

			// handle nesting
			if (token.getType() == ETokenType.LBRACE) {
				nestingDepth++;
			} else if (token.getType() == ETokenType.RBRACE) {
				nestingDepth--;
				if (nestingDepth < 0) {
					throw new ConQATException(
							"Negative block nesting encountered. "
									+ "This should not happen for sane code.");
				}
				if (nestingDepth == 0) {
					return i;
				}
			}
		}

		return BlockParser.CLOSING_RBRACE_NOT_FOUND;
	}

	/** Exception class for block parser problems. */
	@SuppressWarnings("serial")
	public static class BlockParserException extends Exception {
		/** Constructor */
		public BlockParserException() {
			super("Ill-formed nesting.");
		}
	}

}