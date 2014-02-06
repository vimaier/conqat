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

import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.lib.commons.collections.CounterSet;
import org.conqat.lib.scanner.ETokenType.ETokenClass;
import org.conqat.lib.scanner.IToken;

/**
 * A simple processor that counts tokens in source files. Tokens are categorized
 * as identifiers, keywords, delimiters, operators and literals.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: DDF66CFE0F545C948D4120B5CAC41566
 */
@AConQATProcessor(description = " A simple processor that counts tokens in source files. "
		+ "Tokens are categorized as identifiers, keywords, delimiters, "
		+ "operators and literals.")
public class TokenCounter extends TokenAnalyzerBase {

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Key for identifier counter", type = "java.lang.Integer")
	public static final String IDENTIFIER_COUNTER_KEY = "# Identifiers";

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Key for keyword counter", type = "java.lang.Integer")
	public static final String KEYWORD_COUNTER_KEY = "# Keywords";

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Key for delimiter counter", type = "java.lang.Integer")
	public static final String DELIMITER_COUNTER_KEY = "# Delimiters";

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Key for operator counter", type = "java.lang.Integer")
	public static final String OPERATOR_COUNTER_KEY = "# Operators";

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Key for literal counter", type = "java.lang.Integer")
	public static final String LITERAL_COUNTER_KEY = "# Literals";

	/** {@inheritDoc} */
	@Override
	protected void analyzeTokens(List<IToken> tokens, ITokenElement element) {
		CounterSet<ETokenClass> tokenCounter = new CounterSet<ETokenClass>();
		for (IToken token : tokens) {
			tokenCounter.inc(token.getType().getTokenClass());
		}

		// Store counts in element
		element.setValue(IDENTIFIER_COUNTER_KEY,
				tokenCounter.getValue(ETokenClass.IDENTIFIER));
		element.setValue(LITERAL_COUNTER_KEY,
				tokenCounter.getValue(ETokenClass.LITERAL));
		element.setValue(KEYWORD_COUNTER_KEY,
				tokenCounter.getValue(ETokenClass.KEYWORD));
		element.setValue(DELIMITER_COUNTER_KEY,
				tokenCounter.getValue(ETokenClass.DELIMITER));
		element.setValue(OPERATOR_COUNTER_KEY,
				tokenCounter.getValue(ETokenClass.OPERATOR));

	}

	/** {@inheritDoc} */
	@Override
	protected String[] getKeys() {
		return new String[] { IDENTIFIER_COUNTER_KEY, LITERAL_COUNTER_KEY,
				KEYWORD_COUNTER_KEY, DELIMITER_COUNTER_KEY,
				OPERATOR_COUNTER_KEY };
	}
}