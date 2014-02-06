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
package org.conqat.engine.sourcecode.analysis.cpp;

import org.conqat.engine.sourcecode.analysis.FindingsTokenTestCaseBase;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.lib.scanner.ELanguage;

/**
 * Tests the {@link CppCatchExceptionByReferenceAnalyzer}.
 * 
 * @author $Author: goede $
 * @version $Rev: 43210 $
 * @ConQAT.Rating GREEN Hash: D8DA78C5433120FB95592E187BBF8DEC
 */
public class CppCatchExceptionByReferenceAnalyzerTest extends
		FindingsTokenTestCaseBase {

	/** Constructor. */
	public CppCatchExceptionByReferenceAnalyzerTest() {
		super(CppCatchExceptionByReferenceAnalyzer.class, ELanguage.CPP);
	}

	/** Test analyzer. */
	public void test() throws Exception {
		ITokenElement element = executeProcessor("CppCatchExceptionByReferenceAnalyzer.cpp");

		assertFindingCount(element, 3);
		assertFinding(element, 7);
		assertFinding(element, 11);
		assertFinding(element, 13);
	}
}
