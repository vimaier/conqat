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
import org.conqat.lib.scanner.ELanguage;

/**
 * Tests the {@link CppHeaderProtectionMissingAnalyzer}
 * 
 * @author $Author: goede $
 * @version $Rev: 43210 $
 * @ConQAT.Rating GREEN Hash: 90DD35717DDDCD4119E62DBA30A5168D
 */
public class CppHeaderProtectionMissingAnalyzerTest extends
		FindingsTokenTestCaseBase {

	/** Constructor. */
	public CppHeaderProtectionMissingAnalyzerTest() {
		super(CppHeaderProtectionMissingAnalyzer.class, ELanguage.CPP);
	}

	/** Test analyzer. */
	public void test() throws Exception {
		checkFileAssertNoFindings("CppHeaderProtection01.h");
		checkFileAssertNoFindings("CppHeaderProtection02.h");
		checkFileAssertNoFindings("CppHeaderProtection03.h");
		checkFileForFindings("CppHeaderProtection04.h", true);
		checkFileForFindings("CppHeaderProtection05.h", true);
		checkFileForFindings("CppHeaderProtection06.h", true);
	}

}
