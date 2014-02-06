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
package org.conqat.engine.sourcecode.analysis.java;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.sourcecode.analysis.FindingsTokenTestCaseBase;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.lib.scanner.ELanguage;

/**
 * Tests the {@link JavaStarImportAnalyzer}.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46732 $
 * @ConQAT.Rating GREEN Hash: 0C7C14863A10712F1D942F107A690115
 */
public class JavaStarImportAnalyzerTest extends FindingsTokenTestCaseBase {

	/** Constructor. */
	public JavaStarImportAnalyzerTest() {
		super(JavaStarImportAnalyzer.class, ELanguage.JAVA);
	}

	/** Tests analysis. */
	public void test() throws ConQATException {
		ITokenElement element = executeProcessor("StarImport.java");

		assertFinding(element, 3);
		assertFinding(element, 5);
		assertFindingCount(element, 2);
	}
}
