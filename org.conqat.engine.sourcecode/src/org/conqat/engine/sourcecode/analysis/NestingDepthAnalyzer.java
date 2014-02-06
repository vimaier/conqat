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

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.lib.commons.collections.Pair;
import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.IToken;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 192A66E9215A20001C618CCA944B9FE5
 */
@AConQATProcessor(description = "This processor determines the "
		+ "maximum nesting depth of a source element.")
public class NestingDepthAnalyzer extends TokenAnalyzerBase {

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Maximum Nesting Depth", type = "java.lang.Integer")
	public static final String NESTING_DEPTH_KEY = "NestingDepth";

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Line that contains token that opens deepest nesting", type = "java.lang.Integer")
	public static final String NESTING_DEPTH_LINE_KEY = "NestingDepth_LINE";

	/**
	 * Constant used by {@link #determineMaxNesting(List)} to signal an
	 * ill-formed nesting.
	 */
	private static final int ILL_FORMED_NESTING = -1;

	/** Tokens that opens a a scope. */
	private final Set<ETokenType> openTokens = EnumSet.noneOf(ETokenType.class);

	/** Tokens that closes a scope. */
	private final Set<ETokenType> closeTokens = EnumSet
			.noneOf(ETokenType.class);

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "delimiters", minOccurrences = 1, description = "Scope delimiters")
	public void setDelimiters(
			@AConQATAttribute(name = "open", description = "scope open delimiter") ETokenType openToken,
			@AConQATAttribute(name = "close", description = "scope close delimiter") ETokenType closeToken) {
		openTokens.add(openToken);
		closeTokens.add(closeToken);
	}

	/** {@inheritDoc} */
	@Override
	protected String[] getKeys() {
		return new String[] { NESTING_DEPTH_KEY, NESTING_DEPTH_LINE_KEY };
	}

	/** {@inheritDoc} */
	@Override
	protected void analyzeTokens(List<IToken> tokens, ITokenElement element) {
		Pair<Integer, Integer> maxNesting = determineMaxNesting(tokens);
		int maxNestingDepth = maxNesting.getFirst();
		int maxNestingLine = maxNesting.getSecond();
		maxNestingLine += 1; // line numbers are 0 based

		if (maxNestingDepth == ILL_FORMED_NESTING) {
			getLogger().warn(
					"Element has ill-formed nesting: " + element.getId());
			return;
		}

		element.setValue(NESTING_DEPTH_KEY, maxNestingDepth);
		element.setValue(NESTING_DEPTH_LINE_KEY, maxNestingLine);
	}

	/**
	 * Calculate maximum nesting depth for a list of tokens. Returns a
	 * {@link Pair} that contains the max nesting depth first, and line number
	 * second. The result contains {@value #ILL_FORMED_NESTING} if a ill-formed
	 * nesting was encountered.
	 */
	private Pair<Integer, Integer> determineMaxNesting(List<IToken> tokens) {
		int maxNestingDepth = 0;
		int currentNestingDepth = 0;
		int maxNestingLine = 0;

		for (IToken token : tokens) {
			if (openTokens.contains(token.getType())) {
				currentNestingDepth++;
				if (currentNestingDepth > maxNestingDepth) {
					maxNestingDepth = currentNestingDepth;
					maxNestingLine = token.getLineNumber();
				}
			}
			if (closeTokens.contains(token.getType())) {
				if (currentNestingDepth == 0) {
					return illformedNesting();
				}
				currentNestingDepth--;
			}
		}

		if (currentNestingDepth != 0) {
			return illformedNesting();
		}

		return new Pair<Integer, Integer>(maxNestingDepth, maxNestingLine);
	}

	/** Create pair that indicates ill-formed nesting */
	private Pair<Integer, Integer> illformedNesting() {
		return new Pair<Integer, Integer>(ILL_FORMED_NESTING,
				ILL_FORMED_NESTING);
	}

}