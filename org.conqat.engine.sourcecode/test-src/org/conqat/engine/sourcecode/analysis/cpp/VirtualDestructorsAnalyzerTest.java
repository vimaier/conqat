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

import org.conqat.engine.sourcecode.analysis.FindingsTokenTestCaseBase;
import org.conqat.lib.scanner.ELanguage;

/**
 * Test for {@link VirtualDestructorsAnalyzer}.
 * 
 * @author $Author: kanis $
 * @version $Rev: 39784 $
 * @ConQAT.Rating GREEN Hash: 481A739A879510243159780AF3CCA04C
 */
public class VirtualDestructorsAnalyzerTest extends FindingsTokenTestCaseBase {

	/** Constructor. */
	public VirtualDestructorsAnalyzerTest() {
		super(VirtualDestructorsAnalyzer.class, ELanguage.CPP);
	}

	/** Test analyzer. */
	public void test() throws Exception {
		// This file has a virtual destructor as required
		checkFileAssertNoFindings("MultipleTypesTestFile01.h");
		// This one has no destructor at all
		checkFileAssertFindingsAt("MultipleTypesTestFile02.h", 3);
		// This one has a non-virtual destructor
		checkFileAssertFindingsAt("MultipleTypesTestFile03.h", 7);
		// Nested classes
		checkFileAssertFindingsAt("MultipleTypesTestFile10.h", 3, 11);
		// Inline virtual destructor
		checkFileAssertNoFindings("InlineVirtualDestructor.cpp");
	}

}