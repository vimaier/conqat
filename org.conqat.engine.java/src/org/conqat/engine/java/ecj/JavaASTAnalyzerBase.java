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
package org.conqat.engine.java.ecj;

import org.apache.bcel.classfile.JavaClass;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.java.base.JavaAnalyzerBase;
import org.conqat.engine.java.library.JavaLibrary;
import org.conqat.engine.java.resource.IJavaElement;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;

/**
 * Base class for processors analyzing the ECJ AST.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 38203 $
 * @ConQAT.Rating GREEN Hash: 41513C92DAF191A726F3B610D0BDE2E1
 */
public abstract class JavaASTAnalyzerBase extends JavaAnalyzerBase {

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "lenient", attribute = "compile", description = "Whether to fail "
			+ "on compile errors. If set to true errors are only logged. Default is false.", optional = true)
	public boolean lenient = false;

	/** {@inheritDoc} */
	@Override
	protected void analyze(IJavaElement javaElement, JavaClass clazz)
			throws ConQATException {
		CompilationUnitDeclaration ast = getAST(javaElement);
		if (ast != null) {
			processAST(ast, javaElement);
		}
	}

	/** Obtains the AST. */
	private CompilationUnitDeclaration getAST(IJavaElement javaElement)
			throws ConQATException {
		EcjCompilationResult ecjResult = JavaLibrary.getEcjAST(javaElement);

		if (ecjResult.getProblems().length > 0) {
			String message = "Could not compile " + javaElement.getId() + ": "
					+ ecjResult.getProblems()[0];
			if (lenient) {
				getLogger().warn(message);
				return null;
			}
			throw new ConQATException(message);
		}

		return ecjResult.getCompilationUnitDeclaration();

	}

	/** Processes the AST. */
	protected abstract void processAST(CompilationUnitDeclaration ast,
			IJavaElement javaElement) throws ConQATException;

}
