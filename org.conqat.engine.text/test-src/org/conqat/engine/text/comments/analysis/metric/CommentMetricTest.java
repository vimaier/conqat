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
import org.conqat.engine.text.comments.analysis.metric.CommentMetricAnalysis;
import org.conqat.lib.scanner.ELanguage;

/**
 * Test for comment metrics, i.e. the comment character distribution.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 46289 $
 * @ConQAT.Rating GREEN Hash: EF2F2F0FE92DC9B7A21564AD93CF912C
 */
public class CommentMetricTest extends CommentTestBase {

	/** Tests how many comments were counted for each comment category. */
	public void testCommentDistribution() throws Exception {
		initBundleContexts();

		assertMetricValue("CommentClassification.java", 654,
				CommentMetricAnalysis.KEY_COPYRIGHT_COUNT);
		assertMetricValue("CommentClassification.java", 94,
				CommentMetricAnalysis.KEY_HEADER_COUNT);
		assertMetricValue("CommentClassification.java", 57,
				CommentMetricAnalysis.KEY_INTERFACE_COUNT);
		assertMetricValue("CommentClassification.java", 23,
				CommentMetricAnalysis.KEY_INLINE_COUNT);
		assertMetricValue("CommentClassification.java", 37,
				CommentMetricAnalysis.KEY_TASK_COUNT);
		assertMetricValue("CommentClassification.java", 291,
				CommentMetricAnalysis.KEY_COMMENTED_OUT_CODE_COUNT);
		assertMetricValue("CommentClassification.java", 100,
				CommentMetricAnalysis.KEY_SECTION_COUNT);

	}

	/**
	 * Assertion method that the number of characters counted in the given file
	 * under the given key matches the expected number of characters.
	 */
	private void assertMetricValue(String filename, int expectedCharacters,
			String metricKey) throws Exception {

		ITokenElement element = createTokenElement(
				useCanonicalTestFile(filename), ELanguage.JAVA);
		executeProcessor(CommentMetricAnalysis.class, "(input=(ref=", element,
				"))");
		int num = (Integer) element.getValue(metricKey);
		assertEquals(expectedCharacters, num);
	}
}