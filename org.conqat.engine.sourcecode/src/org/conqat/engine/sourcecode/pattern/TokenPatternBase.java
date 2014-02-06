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
package org.conqat.engine.sourcecode.pattern;

import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.scanner.IToken;

/**
 * Base class for patterns operating on token sequences.
 * 
 * @author herrmama
 * @author $Author: juergens $
 * @version $Rev: 35204 $
 * @ConQAT.Rating GREEN Hash: 6F0ED180DD28013A15EB1B7439EA70B2
 */
public abstract class TokenPatternBase<E extends Enum<E>> extends
		EnumPattern<IToken, E> {

	/** Constructor. */
	public TokenPatternBase(Class<E> enumType, String expression)
			throws ConQATException {
		super(enumType, expression);
	}

	/** Get all matches of this token on a certain sequence. */
	public List<List<IToken>> matchAll(List<IToken> tokens) {
		List<List<IToken>> matches = new ArrayList<List<IToken>>();
		EnumPatternMatcher matcher = matcher(tokens);
		while (matcher.find()) {
			List<IToken> match = tokens.subList(matcher.start(), matcher.end());
			matches.add(match);
		}
		return matches;
	}

	/** {@inheritDoc} */
	@Override
	protected String getContent(IToken element) {
		return element.getText();
	}
}