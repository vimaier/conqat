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
package org.conqat.engine.sourcecode.analysis;

import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.IToken;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 41739 $
 * @ConQAT.Rating GREEN Hash: 0DD0325470787F5E5E129A6BB049FA94
 */
@AConQATProcessor(description = "This processor reports given tokens as findings.")
public class ProhibitedTokensAnalyzer extends TokenElementFindingAnalyzerBase {

	/** The list of token types, that are not allowed. */
	private List<ETokenType> forbiddenTokenTypes = new ArrayList<ETokenType>();

	/** @{ConQAT.Doc */
	@AConQATParameter(name = "forbidden", description = "Token types that should produce findings.", minOccurrences = 1)
	public void addForbiddenToken(
			@AConQATAttribute(name = "token", description = "A token type.") ETokenType type) {
		forbiddenTokenTypes.add(type);
	}

	/** {@inheritDoc} */
	@Override
	protected void analyzeTokens(List<IToken> tokens, ITokenElement element)
			throws ConQATException {
		for (IToken token : tokens) {
			if (forbiddenTokenTypes.contains(token.getType())) {
				createFindingForFilteredOffsets(
						"Forbidden language element: " + token.getText(),
						element, token.getOffset(), token.getEndOffset());
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	protected String getFindingGroupName() {
		return "Prohibited language elements";
	}

	/** {@inheritDoc} */
	@Override
	protected String getFindingCategoryName() {
		return "Language feature checks";
	}

}
