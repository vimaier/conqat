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
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.resource.TokenTestCaseBase;
import org.conqat.lib.scanner.ELanguage;

/**
 * Tests the {@link GlobalVariableCounter}.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 47056 $
 * @ConQAT.Rating GREEN Hash: 503629A0485091275CA55A3B4EE9D026
 */
public class GlobalVariableCounterTest extends TokenTestCaseBase {

	/** Tests counting of global variables. */
	public void testGlobalVariableCounting() throws ConQATException {
		ITokenElement element = createTokenElement(
				useCanonicalTestFile("global-vars.cpp"), ELanguage.CPP);
		executeProcessor(GlobalVariableCounter.class, "(input=(ref=", element,
				"))");
		assertEquals(2.0, element.getValue(GlobalVariableCounter.KEY));
	}
}
