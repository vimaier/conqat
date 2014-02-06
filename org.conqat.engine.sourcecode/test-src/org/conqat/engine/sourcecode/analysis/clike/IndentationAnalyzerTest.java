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
import org.conqat.engine.sourcecode.analysis.clike.IndentationAnalyzer;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.lib.scanner.ELanguage;

/**
 * Test for {@link IndentationAnalyzer}.
 * 
 * @author $Author: kanis $
 * @version $Rev: 39788 $
 * @ConQAT.Rating GREEN Hash: D4554244173DD90CFE212289587AD71D
 */
public class IndentationAnalyzerTest extends
		FindingsTokenTestCaseBase {

	/** Constructor. */
	public IndentationAnalyzerTest() {
		super(IndentationAnalyzer.class, ELanguage.CPP);
	}

	/** Test analyzer. */
	public void test() throws Exception {
		ITokenElement element = executeProcessor("IndentationAnalyzer.cpp");

		assertFindingCount(element, 6);
		assertFinding(element, 32);
		assertFinding(element, 33);
		assertFinding(element, 45);
		assertFinding(element, 46);
		assertFinding(element, 59);
		assertFinding(element, 60);
	}
}
