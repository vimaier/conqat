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
import java.util.List;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.analysis.ElementAnalyzerBase;
import org.conqat.engine.resource.regions.RegionSetDictionary;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.resource.ITokenResource;
import org.conqat.lib.commons.region.RegionSet;
import org.conqat.lib.scanner.IToken;

/**
 * Base class to analyze the token sequence.
 * 
 * @author $Author: juergens $
 * @version $Rev: 36081 $
 * @ConQAT.Rating YELLOW Hash: 9E48418BCD057FBA0CA430F5956CD121
 */
public abstract class TokenAnalyzerBase extends
		ElementAnalyzerBase<ITokenResource, ITokenElement> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void analyzeElement(ITokenElement element) throws ConQATException {

		List<IToken> tokens;
		try {
			tokens = element.getTokens(getLogger());
		} catch (ConQATException e) {
			getLogger().warn("Problems scanning element: ", e);
			return;
		}
		tokens = filter(tokens, element);

		analyzeTokens(tokens, element);
	}

	/** Filters ignored tokens. Does not modify the passed tokens list. */
	protected List<IToken> filter(List<IToken> tokens, ITokenElement element)
			throws ConQATException {
		List<IToken> filtered = new ArrayList<IToken>(tokens);

		for (String ignoreKey : ignoreKeys) {
			filter(filtered, element, ignoreKey);
		}

		return filtered;
	}

	/** Removes ignored tokens for a single ignore key. Modifies the tokens list */
	private void filter(List<IToken> tokens, ITokenElement element,
			String ignoreKey) throws ConQATException {
		RegionSet regionSet = RegionSetDictionary.retrieve(element, ignoreKey);
		if (regionSet == null) {
			return;
		}

		for (IToken token : new ArrayList<IToken>(tokens)) {
			if (regionSet.contains(token.getOffset())) {
				tokens.remove(token);
			}
		}
	}

	/** Analyze the sequence of tokens of which an element consists */
	protected abstract void analyzeTokens(List<IToken> tokens,
			ITokenElement element) throws ConQATException;
}