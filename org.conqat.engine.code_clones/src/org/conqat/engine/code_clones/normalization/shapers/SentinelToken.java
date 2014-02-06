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
package org.conqat.engine.code_clones.normalization.shapers;

import org.conqat.lib.scanner.ELanguage;
import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.IToken;
import org.conqat.lib.scanner.Token;

/**
 * Sentinel Token. Sentinels tokens have a unique textual content and are thus
 * unequal to any other tokens.
 * 
 * @author $Author: juergens $
 * @version $Rev: 34487 $
 * @ConQAT.Rating GREEN Hash: 93C8B321AB391534C708E8A39E968D54
 */
public class SentinelToken extends Token {

	/**
	 * Creates a sentinel token whose origin information is copied from the
	 * token after which the sentinel is to be inserted
	 */
	public static SentinelToken createSentinelAfter(IToken token) {
		return new SentinelToken(ETokenType.SENTINEL, token.getOffset(),
				token.getLineNumber(), "\u00A7" + counter++,
				token.getOriginId(), token.getLanguage());
	}

	/** Counter used to create unique sentinel texts */
	private static int counter = 0;

	/** Stores language */
	private final ELanguage language;

	/** Constructor */
	protected SentinelToken(ETokenType type, int offset, int lineNumber,
			String text, String originId, ELanguage language) {
		super(type, offset, lineNumber, text, originId);
		this.language = language;
	}

	/** {@inheritDoc} */
	@Override
	public ELanguage getLanguage() {
		return language;
	}

	/** {@inheritDoc} */
	@Override
	public IToken newToken(ETokenType type, int offset, int lineNumber,
			String text, String originId) {
		return new SentinelToken(type, offset, lineNumber, text, originId,
				language);
	}

}