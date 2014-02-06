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

import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.IToken;

/**
 * A pattern that matches sequences of tokens by means of token types.
 * 
 * @author herrmama
 * @author $Author: juergens $
 * @version $Rev: 35204 $
 * @ConQAT.Rating GREEN Hash: 378F6FAE4F69A4021BF23623F825948B
 */
public class TokenTypePattern extends TokenPatternBase<ETokenType> {

	/** Constructor. */
	public TokenTypePattern(String regex) throws ConQATException {
		super(ETokenType.class, regex);
	}

	/** {@inheritDoc} */
	@Override
	protected ETokenType getEnum(IToken token) {
		return token.getType();
	}
}