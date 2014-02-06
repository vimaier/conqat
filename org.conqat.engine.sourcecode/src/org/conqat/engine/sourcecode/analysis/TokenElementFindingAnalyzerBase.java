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

import java.util.List;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.analysis.ElementFindingAnalyzerBase;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.resource.ITokenResource;
import org.conqat.lib.scanner.IToken;

/**
 * Base class for analyzers that create findings on token elements.
 * 
 * @author $Author: goede $
 * @version $Rev: 43218 $
 * @ConQAT.Rating GREEN Hash: D45D63E0F3ADFAD387F815AB467D0838
 */
public abstract class TokenElementFindingAnalyzerBase extends
		ElementFindingAnalyzerBase<ITokenResource, ITokenElement> {

	/** {@inheritDoc} */
	@Override
	protected Class<?> getElementClass() {
		return ITokenElement.class;
	}

	/** {@inheritDoc} */
	@Override
	protected void analyzeElement(ITokenElement element) throws ConQATException {
		List<IToken> tokens;
		try {
			tokens = element.getTokens(getLogger());
		} catch (ConQATException e) {
			getLogger().warn("Problems scanning element: ", e);
			return;
		}

		analyzeTokens(tokens, element);
	}

	/** Analyze the sequence of tokens of which an element consists */
	protected abstract void analyzeTokens(List<IToken> tokens,
			ITokenElement element) throws ConQATException;
}
