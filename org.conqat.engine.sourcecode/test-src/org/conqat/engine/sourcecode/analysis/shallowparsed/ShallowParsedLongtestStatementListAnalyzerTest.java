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
package org.conqat.engine.sourcecode.analysis.shallowparsed;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.sourcecode.analysis.LongestStatementListAnalyzerBase;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.resource.TokenTestCaseBase;
import org.conqat.lib.scanner.ELanguage;

/**
 * Tests the {@link ShallowParsedLongestStatementListAnalyzer}.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43863 $
 * @ConQAT.Rating GREEN Hash: 058B166A44732E94D845A82AE72CF685
 */
public class ShallowParsedLongtestStatementListAnalyzerTest extends
		TokenTestCaseBase {

	/** The test file. */
	private static final String TEST_FILE = "LSLTest.cs";

	/** Tests LSL calculation without filters. */
	public void testAll() throws ConQATException {
		ITokenElement element = createTokenElement(
				useCanonicalTestFile(TEST_FILE), ELanguage.CS);
		executeProcessor(ShallowParsedLongestStatementListAnalyzer.class,
				"(input=(ref=", element, "))");

		assertEquals(element.getValue(LongestStatementListAnalyzerBase.KEY),
				25.0);
	}

	/** Tests if empty lines and comments are ignored for LSL calculation. */
	public void testIgnoreEmptyLinesAndComments() throws ConQATException {
		ITokenElement element = createTokenElement(
				useCanonicalTestFile(TEST_FILE), ELanguage.CS);
		executeProcessor(ShallowParsedLongestStatementListAnalyzer.class,
				"(input=(ref=", element, "), 'empty-lines'=(ignore=", true,
				"), 'comment-lines'=(ignore=", true, "))");

		assertEquals(element.getValue(LongestStatementListAnalyzerBase.KEY),
				17.0);
	}

	/** Test if empty lines in a method are ignored */
	public void testIgnoreEmptyLines() throws ConQATException {
		ITokenElement element = createTokenElement(
				useCanonicalTestFile(TEST_FILE), ELanguage.CS);
		executeProcessor(ShallowParsedLongestStatementListAnalyzer.class,
				"(input=(ref=", element, "), 'empty-lines'=(ignore=", true,
				"), 'comment-lines'=(ignore=", false, "))");

		assertEquals(element.getValue(LongestStatementListAnalyzerBase.KEY),
				23.0);
	}

	/** Test if comments in a method are ignored. */
	public void testIgnoreComments() throws ConQATException {
		ITokenElement element = createTokenElement(
				useCanonicalTestFile(TEST_FILE), ELanguage.CS);
		executeProcessor(ShallowParsedLongestStatementListAnalyzer.class,
				"(input=(ref=", element, "), 'empty-lines'=(ignore=", false,
				"), 'comment-lines'=(ignore=", true, "))");

		assertEquals(element.getValue(LongestStatementListAnalyzerBase.KEY),
				19.0);
	}
}
