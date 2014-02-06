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
package org.conqat.engine.sourcecode.analysis.cpp;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.sourcecode.analysis.FindingsTokenTestCaseBase;
import org.conqat.engine.sourcecode.analysis.clike.EmptyBlocksAnalyzer;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.lib.scanner.ELanguage;

/**
 * Test for {@link EmptyBlocksAnalyzer}.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43789 $
 * @ConQAT.Rating GREEN Hash: 02E4236F2BAA2B21202DC106E193E8CB
 */
public class CppJavaNamingConventionsAnalyzerTest extends
		FindingsTokenTestCaseBase {

	/** Constructor. */
	public CppJavaNamingConventionsAnalyzerTest() {
		super(CppJavaNamingConventionsAnalyzer.class, ELanguage.CPP);
	}

	/** Test analyzer including methods. */

	public void testWithMethods() throws Exception {
		ITokenElement element = executeProcessor("JavaNamingConventions.cpp");

		assertFinding(element, 6);
		assertFinding(element, 10);
		assertFinding(element, 13);
		assertFinding(element, 14);
		assertFinding(element, 17);
		assertFinding(element, 18);
		assertFinding(element, 21);
		assertFinding(element, 24);
		assertFinding(element, 28);
		assertFinding(element, 29);
		assertFinding(element, 41);
		assertFinding(element, 43);
		assertFinding(element, 45);
		assertFinding(element, 46);
		assertFindingCount(element, 14);
	}

	/**
	 * Tests ignoring naming violations enforced by IDM or BASICS.
	 */

	public void testIgnoreIDMAndBASICS() throws ConQATException {
		ITokenElement element = createTokenElement(
				useCanonicalTestFile("JavaNamingConventionsExceptions.cpp"),
				ELanguage.CPP);
		executeProcessor(processor, "(input=(ref=", element,
				"), 'basics-method-names'=(ignore=", true,
				"), 'idm-method-names'=(ignore=", true, "))");
		assertFinding(element, 5);
		assertFinding(element, 7);
		assertFinding(element, 11);
		assertFinding(element, 13);
		assertFindingCount(element, 4);
	}

	/**
	 * Test analyzer checking operator overloading and defaut arguments.
	 */
	public void testOperatorOverloadingAndDefaultArguments() throws Exception {
		ITokenElement element = executeProcessor("JavaNamingConventionsOverloading.cpp");
		assertNoFindings(element);
	}
}