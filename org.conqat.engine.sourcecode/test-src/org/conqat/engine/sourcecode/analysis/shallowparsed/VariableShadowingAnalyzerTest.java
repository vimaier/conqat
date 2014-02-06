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
package org.conqat.engine.sourcecode.analysis.shallowparsed;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.sourcecode.analysis.FindingsTokenTestCaseBase;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.lib.scanner.ELanguage;

/**
 * Tests the {@link VariableShadowingAnalyzer}.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 47155 $
 * @ConQAT.Rating GREEN Hash: EBFD623C8D925028194875806F7A81F0
 */
public class VariableShadowingAnalyzerTest extends FindingsTokenTestCaseBase {

	/** Constructor. */
	public VariableShadowingAnalyzerTest() {
		super(VariableShadowingAnalyzer.class, ELanguage.CPP);
	}

	/** Tests basic operation. */
	public void testShadowing() throws ConQATException {
		ITokenElement element = executeProcessor("shadowing.cpp");

		assertFindingCount(element, 5);
		assertFinding(element, 8);
		assertFinding(element, 18);
		assertFinding(element, 26);
		assertFinding(element, 31);
		assertFinding(element, 34);
	}
}
