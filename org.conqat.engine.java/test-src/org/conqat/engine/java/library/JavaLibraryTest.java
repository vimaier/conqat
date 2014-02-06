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
package org.conqat.engine.java.library;

import java.io.File;


import org.conqat.engine.java.ecj.EcjCompilationResult;
import org.conqat.engine.java.resource.IJavaElement;
import org.conqat.engine.java.resource.JavaElementUtils;
import org.conqat.engine.java.test.JavaProcessorTestCaseBase;

/**
 * Test class for {@link JavaLibrary}.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @levd.rating GREEN Hash: EB45EDC37AA38A52075410AEB354C2B8
 */
public class JavaLibraryTest extends JavaProcessorTestCaseBase {

	/** Test for {@link JavaLibrary#getEcjAST(IJavaElement)}. */
	public void testGetEcjASTSimpleClass() throws Exception {
		EcjCompilationResult result = JavaLibrary
				.getEcjAST(getFirstJavaClassInFolder(useTestFile("ecjtestclass")));
		assertEquals(0, result.getProblems().length);
		assertNotNull(result.getCompilationUnitDeclaration());
	}

	/** Test for {@link JavaLibrary#getEcjAST(IJavaElement)}. */
	public void testGetEcjASTInterface() throws Exception {
		EcjCompilationResult result = JavaLibrary
				.getEcjAST(getFirstJavaClassInFolder(useTestFile("ecjtestinterface")));
		assertEquals(0, result.getProblems().length);
		assertNotNull(result.getCompilationUnitDeclaration());
	}

	/** Retrieves the first class in the given folder */
	private IJavaElement getFirstJavaClassInFolder(File folder)
			throws Exception {
		return JavaElementUtils.listJavaElements(
				createJavaScope(folder, folder)).get(0);
	}

}