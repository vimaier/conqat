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
 * Test for {@link CppTypeDefinitionsAnalyzer}.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 37786 $
 * @ConQAT.Rating GREEN Hash: 7019FB0FF06DB3F1A3E82AEBEA202D23
 */
public class CppTypeDefinitionsAnalyzerTest extends FindingsTokenTestCaseBase {

	/** Constructor. */
	public CppTypeDefinitionsAnalyzerTest() {
		super(CppTypeDefinitionsAnalyzer.class, ELanguage.CPP);
	}

	/** Test analyzer. */
	public void test() throws Exception {
		checkFileAssertFindingsAt("MultipleTypesTestFile01.h", 5, 10);
		checkFileAssertNoFindings("MultipleTypesTestFile02.h");
		checkFileAssertNoFindings("MultipleTypesTestFile03.h");
		checkFileAssertFindingsAt("MultipleTypesTestFile04.h", 3, 7);
		checkFileAssertFindingsAt("MultipleTypesTestFile05.h", 3);
		checkFileAssertNoFindings("MultipleTypesTestFile06.h");
		checkFileAssertNoFindings("MultipleTypesTestFile07.h");
		checkFileAssertFindingsAt("MultipleTypesTestFile08.h", 5, 9);
		checkFileAssertNoFindings("MultipleTypesTestFile09.h");
		checkFileAssertNoFindings("MultipleTypesTestFile10.h");
		checkFileAssertFindingsAt("MultipleTypesTestFile11.h", 3, 4);
	}

}