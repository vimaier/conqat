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
package org.conqat.engine.sourcecode.resource;

import static org.conqat.lib.scanner.ETokenType.CLASS;
import static org.conqat.lib.scanner.ETokenType.PUBLIC;
import static org.conqat.lib.scanner.ETokenType.STATIC;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.engine.core.logging.ELogLevel;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.resource.TokenElement;
import org.conqat.lib.scanner.ELanguage;

/**
 * Test for {@link TokenElement}.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @levd.rating GREEN Hash: CB0AFA682CA610E9C4B054679D1269D2
 */
public class TokenElementTest extends TokenTestCaseBase {

	/** Performs some basic tests. */
	public void testBasic() throws Exception {
		ITokenElement tokenElement = createTokenElement("public static class");
		assertTokens(tokenElement, ELogLevel.ALL, 0, PUBLIC, STATIC, CLASS);

		tokenElement = createTokenElement(StringUtils.EMPTY_STRING);
		assertTokens(tokenElement, ELogLevel.ALL, 0);
	}

	/** Performs some more basic tests. */
	public void testBasic2() throws Exception {
		ITokenElement tokenElement = createTokenElement(StringUtils.EMPTY_STRING);
		assertEquals("foo", tokenElement.getUniformPath());
		assertEquals(ELanguage.JAVA, tokenElement.getLanguage());
	}

	/** Test error cases. */
	public void testErrorCase() throws Exception {
		// there are enough "good" tokens so we expect it to only ignore the
		// broken token
		ITokenElement tokenElement = createTokenElement("public # class class class class class");
		assertTokens(tokenElement, ELogLevel.WARN, 1, PUBLIC, CLASS, CLASS,
				CLASS, CLASS, CLASS);

		// there are too many broken tokens, the result should be empty
		tokenElement = createTokenElement("public # class");
		assertTokens(tokenElement, ELogLevel.ERROR, 1);
	}

}