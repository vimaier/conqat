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

import java.util.Set;

import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.analysis.ElementAnalyzerBase;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.resource.ITokenResource;
import org.conqat.lib.commons.collections.UnmodifiableList;
import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.IToken;

/**
 * Base class for counting the occurrences of tokens.
 * 
 * @author $Author: juergens $
 * @version $Rev: 35831 $
 * @ConQAT.Rating GREEN Hash: 9BDBB1AB0FB6F689A78AEC7E40904245
 */
public abstract class TokenOccurenceCounterBase extends
		ElementAnalyzerBase<ITokenResource, ITokenElement> {

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "writeKey", attribute = "name", description = "The key to write the count to", optional = false)
	public String writeKey;

	/** {@inheritDoc} */
	@Override
	protected void analyzeElement(ITokenElement element) throws ConQATException {
		UnmodifiableList<IToken> tokens = element.getTokens(getLogger());
		int tokenCount = 0;
		Set<ETokenType> consideredTokenTypes = getConsideredTokenTypes();
		for (IToken token : tokens) {
			if (consideredTokenTypes.contains(token.getType())) {
				tokenCount++;
			}
		}
		element.setValue(writeKey, tokenCount);
	}

	/** {@inheritDoc} */
	@Override
	protected String[] getKeys() {
		return new String[] { writeKey };
	}

	/** Returns the token types to be counted */
	protected abstract Set<ETokenType> getConsideredTokenTypes();

}
