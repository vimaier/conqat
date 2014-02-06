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
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.IToken;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 41734 $
 * @ConQAT.Rating GREEN Hash: A7348A5B0B09131081089D22B213BFAF
 */
@AConQATProcessor(description = "Calculates the maximal distance between certain tokens for each file. "
		+ "The distance of measured in lines.")
public class LongestTokenDistanceAnalyzer extends TokenMetricAnalyzerBase {

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Maximum line distance between token", type = "java.lang.Integer")
	public static final String KEY = "TokenDistance";

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "first", attribute = "include", optional = true, description = ""
			+ "If this is set to true, also the distance to the start of file is measured. (default: true)")
	public boolean includeFirst = true;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "last", attribute = "include", optional = true, description = ""
			+ "If this is set to true, also the distance to the end of file is measured. (default: true)")
	public boolean includeLast = true;

	/** Tokens that are respected for distance measurement. */
	private final Set<ETokenType> tokenTypes = EnumSet.noneOf(ETokenType.class);

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "token", minOccurrences = 1, description = "Adds a token type that is respected for distance measurement.")
	public void setDelimiters(
			@AConQATAttribute(name = "type", description = "Type of token") ETokenType tokenType) {
		tokenTypes.add(tokenType);
	}

	/** {@inheritDoc} */
	@Override
	protected String getKey() {
		return KEY;
	}

	/** {@inheritDoc} */
	@Override
	protected void calculateMetrics(ITokenElement element)
			throws ConQATException {
		List<IToken> tokens = element.getTokens(getLogger());
		if (tokens.isEmpty()) {
			return;
		}

		IToken first = tokens.get(0);
		IToken last = CollectionUtils.getLast(tokens);

		IToken lastToken = null;
		for (IToken token : tokens) {
			if (isRespected(token, first, last)) {
				if (lastToken != null) {
					reportMetricValueForFilteredTokenRegion(token.getLineNumber()
							- lastToken.getLineNumber(), token, lastToken);
				}
				lastToken = token;
			}
		}
	}

	/**
	 * Returns whether the given token should be respected for distance
	 * measurement.
	 */
	private boolean isRespected(IToken token, IToken first, IToken last) {
		if (includeFirst && token == first) {
			return true;
		}
		if (includeLast && token == last) {
			return true;
		}
		return tokenTypes.contains(token.getType());
	}
}