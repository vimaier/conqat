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
package org.conqat.engine.cpp.preprocessor;

import java.util.List;

import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.cpp.preprocessor.parser.PreprocessorBranch;
import org.conqat.engine.cpp.preprocessor.parser.PreprocessorConditionNode;
import org.conqat.engine.cpp.preprocessor.parser.PreprocessorNodeBase;
import org.conqat.engine.cpp.preprocessor.parser.PreprocessorParser;
import org.conqat.engine.cpp.preprocessor.parser.TokenSequencePreprocessorNode;
import org.conqat.engine.sourcecode.analysis.TokenMetricAnalyzerBase;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.lib.scanner.ETokenType.ETokenClass;
import org.conqat.lib.scanner.IToken;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: feilkas $
 * @version $Rev: 41749 $
 * @ConQAT.Rating GREEN Hash: 1790A9D9E36E7B18876D9ECA8F97493E
 */
@AConQATProcessor(description = "Counts for each file the LOC that are contained in preprocessor conditionals.")
public class CodeInPreprocessorConditionalsAnalyzer extends
		TokenMetricAnalyzerBase {

	/**
	 * {@ConQAT.Doc}
	 * <p>
	 * The key stores a double key, as the underlying processor works for
	 * arbitrary (non-integer) metrics.
	 */
	@AConQATKey(description = "The LOC that are between #if* and #endif", type = "java.lang.Double")
	public static final String KEY = "Conditional LOC";

	/** {@inheritDoc} */
	@Override
	protected void calculateMetrics(ITokenElement element) {

		List<PreprocessorNodeBase> nodes;
		try {
			nodes = PreprocessorParser.parse(element, getLogger());
		} catch (ConQATException e) {
			getLogger().error(
					"Could not parse " + element.getLocation() + ": "
							+ e.getMessage(), e);
			return;
		}

		PreprocessorBranch headerProtector = findHeaderProtector(nodes);
		if (headerProtector != null) {
			nodes = headerProtector.getContainedCode();
		}

		reportMetricValue(countConditionalLoc(nodes));
	}

	/**
	 * Heuristically tries to find a header protector or returns null. The
	 * header protector must be the only top-level conditional, may only have a
	 * single branch, and all code outside must be comments.
	 */
	private PreprocessorBranch findHeaderProtector(
			List<PreprocessorNodeBase> nodes) {
		PreprocessorBranch result = null;
		for (PreprocessorNodeBase node : nodes) {
			if ((node instanceof TokenSequencePreprocessorNode)
					&& !hasOnlyComments((TokenSequencePreprocessorNode) node)) {
				// non-comment tokens outside of conditionals
				return null;
			}

			if (node instanceof PreprocessorConditionNode) {
				PreprocessorConditionNode conditionNode = (PreprocessorConditionNode) node;
				if (result != null) {
					// more than one top-level conditional
					return null;
				}
				if (conditionNode.getBranches().size() > 1) {
					// more than one branch
					return null;
				}
				result = conditionNode.getBranches().get(0);
			}
		}

		return result;
	}

	/**
	 * Returns whether a {@link TokenSequencePreprocessorNode} contains only
	 * comment tokens.
	 */
	private boolean hasOnlyComments(TokenSequencePreprocessorNode node) {
		for (IToken token : node.getTokens()) {
			if (token.getType().getTokenClass() != ETokenClass.COMMENT) {
				return false;
			}
		}
		return true;
	}

	/** Returns the LOC contained in conditional nodes. */
	private int countConditionalLoc(List<PreprocessorNodeBase> nodes) {
		int result = 0;
		for (PreprocessorNodeBase node : nodes) {
			if (node instanceof PreprocessorConditionNode) {
				result += getLOC((PreprocessorConditionNode) node);
			}
		}
		return result;
	}

	/** Returns the LOC for a condition node. */
	private int getLOC(PreprocessorConditionNode node) {
		int start = node.getBranches().get(0).getConditionToken()
				.getLineNumber();
		int end = node.getEndifToken().getLineNumber();
		return end - start;
	}

	/** {@inheritDoc} */
	@Override
	protected String getKey() {
		return KEY;
	}
}
