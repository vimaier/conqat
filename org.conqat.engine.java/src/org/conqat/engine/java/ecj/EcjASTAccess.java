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

import org.conqat.engine.java.resource.IJavaElement;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.UnmodifiableList;
import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.internal.compiler.Compiler;
import org.eclipse.jdt.internal.compiler.ICompilerRequestor;
import org.eclipse.jdt.internal.compiler.IErrorHandlingPolicy;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.batch.CompilationUnit;
import org.eclipse.jdt.internal.compiler.batch.FileSystem;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.env.INameEnvironment;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblemFactory;

/**
 * Code required to obtain the ECJ AST from Java source code. This class uses
 * the ECJ (Eclipse Compiler for Java) to obtain the AST. We do not use caching
 * for the AST, as the AST may be modified by the caller.
 * 
 * @author hummelb
 * @author $Author: juergens $
 * 
 * @version $Rev: 35196 $
 * @ConQAT.Rating GREEN Hash: 846C88C3145EC985664642F79635E7E7
 */
public class EcjASTAccess {

	/**
	 * Compiles the given code and returns an result object that encapsulates
	 * the AST as well as compile problems found in the specified file.
	 */
	public static EcjCompilationResult compileAST(String filePath, String code,
			String[] classpath, String encoding, EcjCompilerOptions options) {

		ErrorAwareCompilerRequestor requestor = new ErrorAwareCompilerRequestor(
				filePath);

		CompilationUnitDeclaration result = EcjASTAccess.compileAST(filePath,
				code, classpath, encoding, options, requestor);
		return new EcjCompilationResult(result, CollectionUtils.toArray(
				requestor.getErrors(), CategorizedProblem.class));
	}

	/**
	 * Compiles the given code.
	 */
	public static CompilationUnitDeclaration compileAST(String filePath,
			String code, String[] classpath, String encoding,
			EcjCompilerOptions options, ICompilerRequestor requestor) {
		INameEnvironment environment = new FileSystem(classpath, new String[0],
				encoding);

		Compiler compiler = new Compiler(environment,
				new ErrorHandlingPolicy(), options.obtainOptions(), requestor,
				new DefaultProblemFactory());

		ICompilationUnit[] units = { new CompilationUnit(code.toCharArray(),
				filePath, encoding) };

		compiler.parser.reportSyntaxErrorIsRequired = true;

		compiler.compile(units);
		CompilationUnitDeclaration declaration = compiler.parser.compilationUnit;
		// In case of classes the above expression will yield the compilation
		// unit declaration. In case of interfaces it is however null. Instead,
		// the expression below holds the compilation unit declaration.
		if (declaration == null
				&& compiler.parser.referenceContext instanceof CompilationUnitDeclaration) {
			declaration = (CompilationUnitDeclaration) compiler.parser.referenceContext;
		}

		return declaration;
	}

	/** Returns the class path used for the compiler. */
	public static String[] getClassPath(IJavaElement element) {
		UnmodifiableList<String> classpath = element.getJavaContext()
				.getClassPath();
		return EcjUtils.obtainClasspath(CollectionUtils.toArray(classpath,
				String.class));

	}

	/** A simple default error handling policy. */
	private static class ErrorHandlingPolicy implements IErrorHandlingPolicy {

		/** {@inheritDoc} */
		@Override
		public boolean proceedOnErrors() {
			return true;
		}

		/** {@inheritDoc} */
		@Override
		public boolean stopOnFirstError() {
			return false;
		}
	}
}