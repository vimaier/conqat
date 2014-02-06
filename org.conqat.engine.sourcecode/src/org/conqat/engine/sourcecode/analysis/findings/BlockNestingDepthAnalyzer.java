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

import java.util.List;

import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.lib.commons.assessment.ETrafficLightColor;
import org.conqat.lib.commons.collections.PairList;
import org.conqat.lib.scanner.IToken;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: goede $
 * @version $Rev: 41726 $
 * @ConQAT.Rating GREEN Hash: C3694798ADFBDAF6B67AE7AC25437B86
 */
@AConQATProcessor(description = "Creates findings for blocks over a specified nesting depth")
public class BlockNestingDepthAnalyzer extends BlockAnalyzerBase {

	/** {@ConAT.Doc} */
	@AConQATFieldParameter(parameter = "multiple-findings", attribute = "value", description = "If this is set to true "
			+ "a finding for each block nesting that is above the threshold "
			+ "is reported. To avoid generation of too many findings, findings "
			+ "are only generated for the maximum nesting depth within a too "
			+ "deeply nested block. The generated findings contain the "
			+ "line number of the block with the maximum nesting. If this is "
			+ "set to false, a single finding for each element with a deep "
			+ "nesting is generated [default is false].", optional = true)
	public boolean reportMultipleFindings = false;

	/**
	 * Constant used by {@link #determineMaxNestingDepth(List)} to signal an
	 * ill-formed nesting.
	 */
	private static final int ILL_FORMED_NESTING = -1;

	/**
	 * This maps from nesting depth to the block opening token that opens the
	 * block. Elements are stored in the order they are encountered in an
	 * element.
	 */
	private final PairList<Integer, IToken> nestedTokens = new PairList<Integer, IToken>();

	/** {@inheritDoc} */
	@Override
	protected int analyzeBlock(List<IToken> blockTokens, ITokenElement element)
			throws ConQATException {
		int depth = determineMaxNestingDepth(blockTokens);

		if (depth == ILL_FORMED_NESTING) {
			createFinding(element, blockTokens, blockType
					+ " has unbalanced nesting", ETrafficLightColor.RED);
		} else if (depth > thresholdYellow) {
			if (reportMultipleFindings) {
				createFindings(element);
			} else {
				createFinding(element, blockTokens, blockType
						+ " has nesting depth " + depth, assessmentFor(depth));
			}
		}
		return depth;
	}

	/** {@inheritDoc} */
	@Override
	protected String groupName() {
		return "Deep method nesting";
	}

	/**
	 * Calculate maximum nesting depth for a list of tokens. Returns
	 * {@value #ILL_FORMED_NESTING} if a ill-formed nesting was encountered.
	 */
	private int determineMaxNestingDepth(List<IToken> tokens) {
		int maxNestingDepth = 0;
		int currentNestingDepth = 0;
		nestedTokens.clear();

		for (IToken token : tokens) {
			if (openBlockTokens.contains(token.getType())) {
				currentNestingDepth++;
				maxNestingDepth = Math
						.max(currentNestingDepth, maxNestingDepth);
				nestedTokens.add(currentNestingDepth, token);
			}
			if (closeBlockTokens.contains(token.getType())) {
				if (currentNestingDepth == 0) {
					return ILL_FORMED_NESTING;
				}
				currentNestingDepth--;
			}
		}

		if (currentNestingDepth != 0) {
			return ILL_FORMED_NESTING;
		}
		return maxNestingDepth;
	}

	/**
	 * This method implements the feature to generate multiple findings. It
	 * looks at source code element as a sequence of blocks over which it
	 * iterates. If the block nesting is above the threshold, it looks for the
	 * maximum nesting depth and stores it. Once the nesting level is below the
	 * threshold, it reports the maximum nesting (if there was one above the
	 * threshold) and starts the search anew. If there are multiple blocks with
	 * the same maximum level, only the first is reported.
	 */
	private void createFindings(ITokenElement element) throws ConQATException {
		// this stores the maximum depth if we are in a block above the
		// threshold, otherwise its -1
		int maxInvalidDepth = -1;

		// if there we were above the threshold, this stores the token with
		// maximum nesting to keep track of the line number. Otherwise it is
		// null.
		IToken maxDepthToken = null;

		for (int i = 0; i < nestedTokens.size(); i++) {
			int depth = nestedTokens.getFirst(i);

			// we are in the valid range. If we encountered a deep nesting
			// before, this flagged by the maxDepthToken being != null. Then we
			// report a finding. Otherwise everything is fine.
			if (depth <= thresholdYellow) {
				createFinding(maxDepthToken, element, maxInvalidDepth);
				maxDepthToken = null;
				maxInvalidDepth = -1;
			} else {
				// we are above the threshold and must store the maximum depth
				// and its location
				if (depth > maxInvalidDepth) {
					maxInvalidDepth = depth;
					maxDepthToken = nestedTokens.getSecond(i);
				}
			}
		}

		createFinding(maxDepthToken, element, maxInvalidDepth);
	}

	/** If the maxDepthToken is not null, this generates a finding. */
	private void createFinding(IToken maxDepthToken, ITokenElement element,
			int maxInvalidDepth) throws ConQATException {
		if (maxDepthToken != null) {
			String message = blockType + " has nesting depth "
					+ maxInvalidDepth;
			createFinding(element, maxDepthToken, message,
					assessmentFor(maxInvalidDepth));
		}
	}

}