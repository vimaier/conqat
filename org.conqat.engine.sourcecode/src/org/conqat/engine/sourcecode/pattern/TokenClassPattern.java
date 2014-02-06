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
import org.conqat.lib.scanner.ETokenType.ETokenClass;
import org.conqat.lib.scanner.IToken;

/**
 * A pattern that matches sequences of tokens by means of token classes.
 * 
 * @author herrmama
 * @author $Author: juergens $
 * @version $Rev: 35204 $
 * @ConQAT.Rating GREEN Hash: 065ADC9B05A0A99A16565AFB90C902C8
 */
public class TokenClassPattern extends TokenPatternBase<ETokenClass> {

	/** Constructor. */
	public TokenClassPattern(String regex) throws ConQATException {
		super(ETokenClass.class, regex);
	}

	/** {@inheritDoc} */
	@Override
	protected ETokenClass getEnum(IToken token) {
		return token.getType().getTokenClass();
	}
}