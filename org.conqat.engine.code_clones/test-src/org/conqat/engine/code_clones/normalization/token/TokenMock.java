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
package org.conqat.engine.code_clones.normalization.token;

import org.conqat.lib.scanner.ELanguage;
import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.IToken;
import org.conqat.lib.scanner.Token;

/**
 * Mock implementation for tokens.
 * 
 * @author $Author: juergens $
 * @version $Rev: 34670 $
 * @ConQAT.Rating GREEN Hash: 7DF3A93F375A52D4BB39EBBFF675D8E2
 */
public class TokenMock extends Token {

	/** Constructor */
	public TokenMock(ETokenType type, String text) {
		super(type, -1, -1, text, "someorigin");
	}

	/** {@inheritDoc} */
	@Override
	public ELanguage getLanguage() {
		return ELanguage.JAVA;
	}

	/** {@inheritDoc} */
	@Override
	public IToken newToken(ETokenType type, int offset, int lineNumber,
			String text, String originId) {
		return new TokenMock(type, text);
	}
}