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

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.code_clones.core.CloneDetectionException;
import org.conqat.engine.code_clones.lazyscope.TokenElementProvider;
import org.conqat.engine.core.logging.testutils.ProcessorInfoMock;
import org.conqat.engine.sourcecode.resource.ITokenResource;
import org.conqat.engine.sourcecode.resource.TokenTestCaseBase;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.scanner.ELanguage;
import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.ILenientScanner;
import org.conqat.lib.scanner.IScanner;
import org.conqat.lib.scanner.IToken;
import org.conqat.lib.scanner.ScannerException;
import org.conqat.lib.scanner.ScannerFactory;
import org.conqat.lib.scanner.ScannerUtils;

/**
 * Test case for the {@link TokenProvider} class.
 * <p>
 * Test only uses the java scanner, since this test relies on the scanners to
 * work correctly and only tests the functionality that the
 * {@link TokenProvider} adds on top of the scanner code.
 * 
 * @author $Author: hummelb $
 * @version $Revision: 47113 $
 * @ConQAT.Rating YELLOW Hash: FA3C18C701280A1578E65A9EF5889A6F
 */
public class TokenProviderTest extends TokenTestCaseBase {

	/** Name of the file that contains valid java tokens */
	private static final String VALID_TOKENS_FILE = "valid_tokens.java";

	/** Name of the file that contains valid and invalid java tokens */
	private static final String INVALID_TOKENS_FILE = "invalid_tokens.java";

	/** Set up token provider on test data */
	private TokenProvider setUpProvider(String filename, ELanguage language)
			throws Exception {
		ITokenResource root = createTokenScope(useTestFile(""), language,
				new String[] { filename }, null);

		TokenElementProvider sceProvider = new TokenElementProvider();
		TokenProvider tokenProvider = new TokenProvider(sceProvider);
		tokenProvider.init(root, new ProcessorInfoMock().getLogger());

		return tokenProvider;
	}

	/** Test if {@link TokenProvider} delivers the expected tokens */
	public void testGetNextTokenForValidTokens() throws Exception {
		List<IToken> expectedTokens = readValidTokens();
		TokenProvider provider = setUpProvider(VALID_TOKENS_FILE,
				ELanguage.JAVA);

		assertTokensAsExpected(expectedTokens, provider);
	}

	/** Read tokens from test file into list */
	private List<IToken> readValidTokens() throws IOException, ScannerException {
		String content = FileSystemUtils
				.readFile(useTestFile(VALID_TOKENS_FILE));
		content = StringUtils.replaceLineBreaks(content, "\n");
		IScanner scanner = ScannerFactory.newScanner(ELanguage.JAVA,
				new StringReader(content), VALID_TOKENS_FILE);

		List<ScannerException> scannerExceptions = new ArrayList<ScannerException>();
		List<IToken> tokens = new ArrayList<IToken>();
		ScannerUtils.readTokens(scanner, tokens, scannerExceptions);
		if (scannerExceptions.size() > 0) {
			throw scannerExceptions.get(0);
		}

		return tokens;
	}

	/** Test if {@link TokenProvider} delivers the expected tokens */
	public void testGetNextTokenForInvalidTokens() throws Exception {
		List<IToken> expectedTokens = readInvalidTokens();
		filterErrorTokens(expectedTokens);

		TokenProvider provider = setUpProvider(INVALID_TOKENS_FILE,
				ELanguage.JAVA);

		assertTokensAsExpected(expectedTokens, provider);
	}

	/** Removes all error tokens from a list of tokens */
	private void filterErrorTokens(List<IToken> expectedTokens) {
		for (IToken token : new ArrayList<IToken>(expectedTokens)) {
			if (token.getType().isError()) {
				expectedTokens.remove(token);
			}
		}
	}

	/** Compares actual tokens against expected tokens. */
	@SuppressWarnings("null")
	private void assertTokensAsExpected(List<IToken> expectedTokens,
			TokenProvider provider) throws CloneDetectionException {
		// iterate over expected tokens and compare with actual tokens
		for (IToken expectedToken : expectedTokens) {
			IToken actualToken = provider.getNext();
			assertTrue("Less tokens than expected. Expecting: " + expectedToken
					+ " but found null", actualToken != null);

			assertEquals(expectedToken.getType(), actualToken.getType());
			assertEquals(expectedToken.getText(), actualToken.getText());
		}

		// make sure that provider has no additional unexpected tokens
		IToken lastToken = provider.getNext();
		assertTrue(lastToken == null || lastToken.getType() == ETokenType.EOF);
	}

	/** Read tokens from file that contains invalid tokens */
	private List<IToken> readInvalidTokens() throws IOException {
		String content = FileSystemUtils
				.readFile(useTestFile(INVALID_TOKENS_FILE));
		content = StringUtils.replaceLineBreaks(content, "\n");
		ILenientScanner scanner = ScannerFactory.newLenientScanner(
				ELanguage.JAVA, new StringReader(content), INVALID_TOKENS_FILE);

		return ScannerUtils.readTokens(scanner);
	}

	/**
	 * Tests if TokenProvider discards all tokens of a file, if the number of
	 * error tokens exceeds a certain threshold.
	 */
	public void testDiscardAllTokensFromFileIfTooManyErrors() throws Exception {
		TokenProvider provider = setUpProvider("header.png", ELanguage.CS);

		List<IToken> tokens = drainProvider(provider);

		assertTrue(
				"Expecting provider to discard all tokens, but provider returned tokens",
				tokens.size() == 0);
	}

	/** Read all tokens from {@link TokenProvider} into list */
	private List<IToken> drainProvider(TokenProvider provider)
			throws CloneDetectionException {
		List<IToken> tokens = new ArrayList<IToken>();
		IToken token = provider.getNext();
		while (token != null) {
			tokens.add(token);
			token = provider.getNext();
		}
		return tokens;
	}

	/**
	 * Test that no exception is thrown if a file contains an invalid unicode
	 * escape sequence.
	 */
	public void testInvalidUnicodeEscapeSequence() throws Exception {
		TokenProvider provider = setUpProvider("invalidunicodeescape.java",
				ELanguage.JAVA);

		List<IToken> tokens = drainProvider(provider);

		// check number of tokens as regression
		assertEquals(7, tokens.size());
	}

}