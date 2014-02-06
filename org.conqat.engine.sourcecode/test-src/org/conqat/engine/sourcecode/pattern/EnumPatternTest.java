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

import java.nio.charset.Charset;
import java.util.List;

import org.conqat.engine.resource.scope.filesystem.FileContentAccessor;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.resource.TokenElement;

import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.filesystem.CanonicalFile;
import org.conqat.lib.commons.test.CCSMTestCaseBase;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.logging.testutils.LoggerMock;
import org.conqat.lib.scanner.ELanguage;
import org.conqat.lib.scanner.IToken;

/**
 * Tests for the {@link EnumPattern}.
 * 
 * @author herrmama
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 * @levd.rating GREEN Hash: B83617F53A4DCEB38B1037157817AD45
 */
public class EnumPatternTest extends CCSMTestCaseBase {

	/** The tokens. */
	private List<IToken> tokens;

	/** {@inheritDoc} */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		tokens = getTokens("test.abap");
	}

	/** Test pattern for finding dangerous select statements. */
	public void testSelectEnumPattern() {
		EnumPatternMatcher matcher = createMatcher(tokens,
				"<SELECT>[^<FROM>]*?<FROM><IDENTIFIER=xy.*?>[^<DOT>]*?<DOT>");

		assertMatches(tokens, matcher, 5, 10);
	}

	/** Test pattern for finding empty form statements. */
	public void testEmptyFormEnumPattern() {
		EnumPatternMatcher matcher = createMatcher(tokens,
				"<FORM><IDENTIFIER>[^<DOT>]*?<DOT><ENDFORM><DOT>");

		assertMatches(tokens, matcher, 10, 15);
	}

	/**
	 * Assert whether we find the expected matches.
	 * 
	 * @param tokens
	 *            The tokens that are to be matched.
	 * @param matcher
	 *            The matcher to match the tokens.
	 * @param matches
	 *            The expected positions of the matches, alternately start and
	 *            end of match.
	 */
	private void assertMatches(List<IToken> tokens, EnumPatternMatcher matcher,
			int... matches) {
		for (int i = 0, n = matches.length / 2; i < n; i++) {
			int start = matches[2 * i];
			int end = matches[2 * i + 1];

			String expectedString = toString(tokens, start, end);
			CCSMAssert.isTrue(matcher.find(), "Expected match not found: "
					+ expectedString);
			String actualString = toString(tokens, matcher.start(), matcher
					.end());
			CCSMAssert.isTrue(start == matcher.start() && end == matcher.end(),
					"Expected match different from actual match: "
							+ expectedString + ", " + actualString);
		}
	}

	/** Scan a file and return the tokens. */
	private List<IToken> getTokens(String fileName) throws ConQATException {
		CanonicalFile file = useCanonicalTestFile(fileName);
		ITokenElement element = new TokenElement(new FileContentAccessor(file,
				file.getParentFile(), "TEST"), Charset.defaultCharset(),
				ELanguage.ABAP);
		List<IToken> tokens = element.getTokens(new LoggerMock());
		return tokens;
	}

	/** Create a matcher for a sequence of tokens and an expression. */
	private EnumPatternMatcher createMatcher(List<IToken> tokens,
			String expression) {
		try {
			TokenTypePattern pattern = new TokenTypePattern(expression);
			EnumPatternMatcher matcher = pattern.matcher(tokens);
			return matcher;
		} catch (ConQATException e) {
			CCSMAssert.fail("The pattern should be correct.");
			return null;
		}
	}

	/** Provide nice output for a sublist of tokens. */
	private String toString(List<IToken> tokens, int start, int end) {
		StringBuilder builder = new StringBuilder();
		builder.append('[');
		for (int i = start; i < end; i++) {
			if (i > start) {
				builder.append(", ");
			}
			IToken token = tokens.get(i);
			builder.append(i + ":" + token.getType().name());
		}
		builder.append(']');
		return builder.toString();
	}
}