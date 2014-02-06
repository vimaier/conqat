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
package org.conqat.engine.text.comments.analysis.finding;

import org.conqat.engine.commons.findings.FindingsList;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.text.comments.analysis.CommentTestBase;
import org.conqat.engine.text.comments.analysis.finding.CommentedOutCodeAnalysis;
import org.conqat.engine.text.comments.analysis.finding.LongInlineCommentAnalysis;
import org.conqat.engine.text.comments.analysis.finding.ShortInlineCommentAnalysis;
import org.conqat.engine.text.comments.analysis.finding.TrivialInterfaceCommentAnalysis;
import org.conqat.engine.text.comments.analysis.finding.UnrelatedInterfaceCommentAnalysis;
import org.conqat.lib.scanner.ELanguage;

/**
 * Test for metric findings.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 46284 $
 * @ConQAT.Rating YELLOW Hash: E2257B7D509675FFAC5AF50226041627
 */
public class CommentFindingTest extends CommentTestBase {

	/** Test API Coverage. */
	public void testFindings() throws Exception {
		initBundleContexts();

		assertInterfaceCoherence("CommentFindings.java", 2, 2);
		assertInlineLength("CommentFindings.java", 1, 1);
		assertCodeLOC("CommentFindings.java", 1);
	}

	/**
	 * Assertion methods for coherence findings: Tests that the number of
	 * trivial and unrelated interface comments matches the expected values.
	 */
	private void assertInterfaceCoherence(String filename,
			int expectedNumberTrivial, int expectedNumberUnrelated)
			throws Exception {

		ITokenElement element = createTokenElement(
				useCanonicalTestFile(filename), ELanguage.JAVA);
		executeProcessor(TrivialInterfaceCommentAnalysis.class, "(input=(ref=",
				element, "))");
		int num = ((FindingsList) element.getValue("findings")).size();
		assertEquals(expectedNumberTrivial, num);

		executeProcessor(UnrelatedInterfaceCommentAnalysis.class,
				"(input=(ref=", element, "))");
		num = ((FindingsList) element.getValue("findings")).size();
		assertEquals(expectedNumberTrivial + expectedNumberUnrelated, num);
	}

	/**
	 * Assertion methods for length findings: Tests that the number of long and
	 * short inline comments matches the expected values.
	 */
	private void assertInlineLength(String filename, int expectedNumberLong,
			int expectedNumberShort) throws Exception {

		ITokenElement element = createTokenElement(
				useCanonicalTestFile(filename), ELanguage.JAVA);
		executeProcessor(LongInlineCommentAnalysis.class, "(input=(ref=",
				element, "))");
		int num = ((FindingsList) element.getValue("findings")).size();
		assertEquals(expectedNumberLong, num);

		executeProcessor(ShortInlineCommentAnalysis.class, "(input=(ref=",
				element, "))");
		num = ((FindingsList) element.getValue("findings")).size();
		assertEquals(expectedNumberLong + expectedNumberShort, num);
	}

	/**
	 * Asserts that the number of LOC of commented out code match the expected
	 * value.
	 */
	private void assertCodeLOC(String filename, int expectedLOC)
			throws Exception {

		ITokenElement element = createTokenElement(
				useCanonicalTestFile(filename), ELanguage.JAVA);
		executeProcessor(CommentedOutCodeAnalysis.class, "(input=(ref=",
				element, "))");
		int num = ((FindingsList) element.getValue("findings")).size();
		assertEquals(expectedLOC, num);

	}

}