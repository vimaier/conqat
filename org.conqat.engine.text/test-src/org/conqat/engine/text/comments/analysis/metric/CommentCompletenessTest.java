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
import org.conqat.engine.sourcecode.resource.TokenTestCaseBase;
import org.conqat.lib.scanner.ELanguage;

/**
 * Tests the {@link CommentCompletenessCountAnalysis}.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 46284 $
 * @ConQAT.Rating YELLOW Hash: CEE3AFC678B7F6AC5BECEC9F3693744E
 */
public class CommentCompletenessTest extends TokenTestCaseBase {

	/** Test API Coverage. */
	public void testAPICoverage() throws Exception {
		String selectorExpression = "public & method";
		assertCoverage("CompletelyCommentedClass.java", selectorExpression, 1.0);
		assertCoverage("HalfCommentedClass.java", selectorExpression, 0.5);
		assertCoverage("ClassWithNoComments.java", selectorExpression, 0.0);

		// TODO(DS): test the value of CommentedOutClass on Not Available
	}

	/** Test Type Coverage. */
	public void testTypeCoverage() throws Exception {
		String selectorExpression = "public & type";
		assertCoverage("CompletelyCommentedClass.java", selectorExpression, 1.0);
		assertCoverage("HalfCommentedClass.java", selectorExpression, 1.0);
		assertCoverage("ClassWithNoComments.java", selectorExpression, 0.0);

		// TODO(DS): test the value of CommentedOutClass on Not Available
	}

	/** Test the coverage for a given selector expression. */
	private void assertCoverage(String filename, String selectorExpression,
			double expectedCoverage) throws Exception {
		ITokenElement element = createTokenElement(
				useCanonicalTestFile(filename), ELanguage.JAVA);
		executeProcessor(CommentCompletenessCountAnalysis.class,
				"(input=(ref=", element, "), selector=(expression='",
				selectorExpression,
				"'), 'commented-count'=(key='commented'), 'overall-count'=(key='overall'))");
		double coverage = (Integer) element.getValue("commented")
				/ ((Integer) element.getValue("overall")).doubleValue();
		assertEquals(expectedCoverage, coverage);
	}
}
