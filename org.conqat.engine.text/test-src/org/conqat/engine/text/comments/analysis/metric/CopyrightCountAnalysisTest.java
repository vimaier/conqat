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
import org.conqat.lib.scanner.ELanguage;

/**
 * Tests the {@link CopyrightCountAnalysis}.
 * 
 * @author $Author: steidl $
 * @version $Rev: 46293 $
 * @ConQAT.Rating YELLOW Hash: 1390EC274782C9277AA909B6CAA6DAC6
 */
public class CopyrightCountAnalysisTest extends CommentTestBase {

	/** Test detection of copyrights. */
	public void testCopyright() throws Exception {
		initBundleContexts();
		assertCopyright("CompletelyCommentedClass.java", 1);
		assertCopyright("HalfCommentedClass.java", 1);
		assertCopyright("ClassWithNoComments.java", 0);
	}

	/** Assert number of copyrights found is as expected */
	private void assertCopyright(String filename, int expectedNumber)
			throws Exception {
		ITokenElement element = createTokenElement(
				useCanonicalTestFile(filename), ELanguage.JAVA);
		executeProcessor(CopyrightCountAnalysis.class, "(input=(ref=", element,
				"))");
		int num = (Integer) element
				.getValue(CopyrightCountAnalysis.KEY_NUM_COPYRIGHTS);
		assertEquals(expectedNumber, num);
	}
}