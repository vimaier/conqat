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
package org.conqat.engine.text.comments.analysis.metric;

import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.text.comments.analysis.CommentTestBase;
import org.conqat.engine.text.comments.analysis.metric.InlineCommentCounter;
import org.conqat.engine.text.comments.analysis.metric.InterfaceCommentCounter;
import org.conqat.engine.text.comments.analysis.metric.LinesOfCommentedOutCodeCounter;
import org.conqat.lib.scanner.ELanguage;

/**
 * Test for counting comments.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 46284 $
 * @ConQAT.Rating YELLOW Hash: E2257B7D509675FFAC5AF50226041627
 */
public class CommentCountTest extends CommentTestBase {

	/** Test API Coverage. */
	public void testCounts() throws Exception {
		initBundleContexts();

		assertInterfaceCount("CommentFindings.java", 5);
		assertInlineCount("CommentFindings.java", 4);
		assertCodeLOCCount("CommentFindings.java", 7);
	}

	/**
	 * Assertion methods for coherence findings: Tests that the number of
	 * trivial and unrelated interface comments matches the expected values.
	 */
	private void assertInterfaceCount(String filename, int expectedNumber)
			throws Exception {

		ITokenElement element = createTokenElement(
				useCanonicalTestFile(filename), ELanguage.JAVA);
		executeProcessor(InterfaceCommentCounter.class, "(input=(ref=",
				element, "))");

		int num = (Integer) element
				.getValue(InterfaceCommentCounter.KEY_NUM_INTERFACE_Comments);
		assertEquals(expectedNumber, num);
	}

	/**
	 * Assertion methods for length findings: Tests that the number of long and
	 * short inline comments matches the expected values.
	 */
	private void assertInlineCount(String filename, int expectedNumberLong)
			throws Exception {

		ITokenElement element = createTokenElement(
				useCanonicalTestFile(filename), ELanguage.JAVA);
		executeProcessor(InlineCommentCounter.class, "(input=(ref=", element,
				"))");
		int num = (Integer) element
				.getValue(InlineCommentCounter.KEY_NUM_INLINE_Comments);
		assertEquals(expectedNumberLong, num);

	}

	/**
	 * Asserts that the number of LOC of commented out code match the expected
	 * value.
	 */
	private void assertCodeLOCCount(String filename, int expectedLOC)
			throws Exception {

		ITokenElement element = createTokenElement(
				useCanonicalTestFile(filename), ELanguage.JAVA);
		executeProcessor(LinesOfCommentedOutCodeCounter.class, "(input=(ref=",
				element, "))");
		int num = (Integer) element
				.getValue(LinesOfCommentedOutCodeCounter.KEY_CommentedOutCodeLOC);
		assertEquals(expectedLOC, num);

	}

}