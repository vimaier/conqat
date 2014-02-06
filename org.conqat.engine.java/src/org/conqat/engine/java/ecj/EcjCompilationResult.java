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
package org.conqat.engine.java.ecj;

import org.conqat.lib.commons.assertion.CCSMPre;
import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;

/**
 * Represents a compilation result from Ecj.
 * 
 * @author heineman
 * @author $Author: juergens $
 * @version $Rev: 35196 $
 * @ConQAT.Rating GREEN Hash: 3E9F24BE2E0F818CA98B8F4C0C4788A6
 */
public class EcjCompilationResult {

	/** The compilation result */
	private final CompilationUnitDeclaration compilationUnitDeclaration;

	/** The problems that occurred */
	private final CategorizedProblem[] problems;

	/** Constructor */
	public EcjCompilationResult(
			CompilationUnitDeclaration compilationUnitDeclaration,
			CategorizedProblem[] problems) {
		CCSMPre.isNotNull(problems);
		this.compilationUnitDeclaration = compilationUnitDeclaration;
		this.problems = problems;
	}

	/** Returns the compilation result or <code>null</code> if none */
	public CompilationUnitDeclaration getCompilationUnitDeclaration() {
		return compilationUnitDeclaration;
	}

	/** Returns the problems */
	public CategorizedProblem[] getProblems() {
		return problems;
	}
}