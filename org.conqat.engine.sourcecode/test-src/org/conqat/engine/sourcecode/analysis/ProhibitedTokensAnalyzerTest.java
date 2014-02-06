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
package org.conqat.engine.sourcecode.analysis;

import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.lib.scanner.ELanguage;
import org.conqat.lib.scanner.ETokenType;

/**
 * Test for {@link ProhibitedTokensAnalyzer}.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 37976 $
 * @ConQAT.Rating GREEN Hash: 56ABAA4F0655C86FAD705521BC41558E
 */
public class ProhibitedTokensAnalyzerTest extends FindingsTokenTestCaseBase {

	/** Constructor. */
	public ProhibitedTokensAnalyzerTest() {
		super(ProhibitedTokensAnalyzer.class, ELanguage.CPP);
	}

	/** Test analyzer. */
	public void test() throws Exception {
		String filename = "GotoExample.c";
		ITokenElement element = createTokenElement(
				useCanonicalTestFile(filename), ELanguage.CPP);

		executeProcessor(ProhibitedTokensAnalyzer.class, "(input=(ref=",
				element, "),", "forbidden=(token=", ETokenType.GOTO, "))");
		assertFinding(element, 14);
	}
}
