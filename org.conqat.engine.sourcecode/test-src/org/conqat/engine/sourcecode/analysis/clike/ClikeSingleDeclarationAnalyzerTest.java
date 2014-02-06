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
package org.conqat.engine.sourcecode.analysis.clike;

import org.conqat.engine.sourcecode.analysis.FindingsTokenTestCaseBase;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.lib.scanner.ELanguage;

/**
 * Tests the {@link ClikeSingleDeclarationAnalyzer}.
 * 
 * @author $Author: goede $
 * @version $Rev: 43218 $
 * @ConQAT.Rating GREEN Hash: 8B16641A8B35BBB780A8BFDD042A0C19
 */
public class ClikeSingleDeclarationAnalyzerTest extends
		FindingsTokenTestCaseBase {

	/** Constructor. */
	public ClikeSingleDeclarationAnalyzerTest() {
		super(ClikeSingleDeclarationAnalyzer.class, ELanguage.CPP);
	}

	/** Test analyzer. */
	public void test() throws Exception {
		ITokenElement element = executeProcessor("ClikeSingleDeclarationAnalyzer.cpp");

		assertFindingCount(element, 2);
		assertFinding(element, 8, 8);
		assertFinding(element, 19, 19);
	}
}
