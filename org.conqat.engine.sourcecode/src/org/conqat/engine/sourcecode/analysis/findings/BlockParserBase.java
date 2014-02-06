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
package org.conqat.engine.sourcecode.analysis.findings;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.sourcecode.analysis.Block;
import org.conqat.engine.sourcecode.analysis.BlockParser;
import org.conqat.engine.sourcecode.analysis.BlockParser.BlockParserException;
import org.conqat.engine.sourcecode.analysis.BlockParser.EMatchStyle;
import org.conqat.engine.sourcecode.analysis.TokenAnalyzerBase;
import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.IToken;

/**
 * Base class for processors that parse block structures.
 * 
 * @author $Author: juergens $
 * @version $Rev: 35204 $
 * @ConQAT.Rating GREEN Hash: 2E693DB230EABEE2E7FF226E7138E5A8
 */
public abstract class BlockParserBase extends TokenAnalyzerBase {

	/** Set of scope keywords */
	private final Set<ETokenType> scopeKeywords = new HashSet<ETokenType>();

	/** Block depth */
	private int blockDepth;

	/** Set of tokens that opens a new block. */
	protected final Set<ETokenType> openBlockTokens = EnumSet
			.noneOf(ETokenType.class);

	/** Set of tokens that closes a new block. */
	protected final Set<ETokenType> closeBlockTokens = EnumSet
			.noneOf(ETokenType.class);

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "open-block", minOccurrences = 1, description = "Token types which indicate the opening of a block.")
	public void addOpenBlockToken(
			@AConQATAttribute(name = "token", description = "The type of token.") ETokenType tokenType)
			throws ConQATException {
		if (!openBlockTokens.add(tokenType)) {
			throw new ConQATException("Open block token " + tokenType
					+ " already contained");
		}
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "close-block", minOccurrences = 1, description = "Token types which indicate the closing of a block.")
	public void addCloseBlockToken(
			@AConQATAttribute(name = "token", description = "The type of token.") ETokenType tokenType)
			throws ConQATException {
		if (!closeBlockTokens.add(tokenType)) {
			throw new ConQATException("Close block token " + tokenType
					+ " already contained");
		}
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "block", minOccurrences = 1, maxOccurrences = 1, description = ""
			+ "Nesting depth of blocks")
	public void setBlockDepth(
			@AConQATAttribute(name = "depth", description = "Nesting depth of blocks") int methodDepth) {
		blockDepth = methodDepth;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "scope", description = ""
			+ "Keyword that opens a scope")
	public void addScopeKeyword(
			@AConQATAttribute(name = "keywords", description = "Examples are class, namespace, ...") ETokenType scopeKeyword) {
		scopeKeywords.add(scopeKeyword);
	}

	/**
	 * Determine method regions in the source element
	 * 
	 * @throws ConQATException
	 *             if the block parser was not configured properly
	 * @throws BlockParserException
	 *             if a parsing exception, e.g. ill-formed nesting, was
	 *             discovered
	 */
	protected List<Block> parseBlocks(List<IToken> tokens)
			throws ConQATException, BlockParserException {
		BlockParser parser = new BlockParser(blockDepth, EMatchStyle.ONLY_BODY,
				scopeKeywords, openBlockTokens, closeBlockTokens);
		return parser.createBlocks(tokens);
	}
}