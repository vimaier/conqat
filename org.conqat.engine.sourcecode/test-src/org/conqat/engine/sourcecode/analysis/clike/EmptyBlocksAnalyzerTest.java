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
package org.conqat.engine.sourcecode.analysis.clike;

import org.conqat.engine.sourcecode.analysis.FindingsTokenTestCaseBase;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.lib.scanner.ELanguage;

/**
 * Test for {@link EmptyBlocksAnalyzer}.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 45428 $
 * @ConQAT.Rating GREEN Hash: 5E38FA986C004A09BC1AD01BB9964775
 */
public class EmptyBlocksAnalyzerTest extends FindingsTokenTestCaseBase {

	/** Constructor. */
	public EmptyBlocksAnalyzerTest() {
		super(EmptyBlocksAnalyzer.class, ELanguage.JAVA);
	}

	/** Test analyzer including methods. */
	public void testWithMethods() throws Exception {
		ITokenElement element = executeProcessor("EmptyBlocks.java");

		assertFindingCount(element, 3);
		assertFinding(element, 5, 6);
		assertFinding(element, 16, 21);
		assertFinding(element, 24, 24);
	}

	/** Test analyzer excluding methods. */
	public void testWithoutMethods() throws Exception {
		ITokenElement element = createTokenElement(
				useCanonicalTestFile("EmptyBlocks.java"), tokenLanguage);
		executeProcessor(processor, "(input=(ref=", element,
				"), 'empty-methods'=(allow=true))");
		assertFinding(element, 24, 24);
	}

	/** Test analyzer with no empty block. */
	public void testWithNoEmptyBlock() throws Exception {
		checkFileAssertNoFindings("NoEmptyBlocks.java");
	}

	/** Tests JavaScript. */
	public void testJavaScript() throws Exception {
		ITokenElement element = executeProcessor("EmptyBlocks.js");

		assertFindingCount(element, 2);
		assertFinding(element, 3, 3);
		assertFinding(element, 9, 11);
	}
}