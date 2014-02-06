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

import org.conqat.engine.commons.pattern.PatternList;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.java.resource.IJavaElement;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;

/**
 * Base class for processors analyzing method calls (including constructors).
 * 
 * @author $Author: hummelb $
 * @version $Rev: 38221 $
 * @ConQAT.Rating GREEN Hash: DF081675065FE5206C521D01CBBB42FC
 */
public abstract class JavaMethodCallAnalyzerBase extends JavaASTAnalyzerBase {

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "declaring-class", attribute = "pattern", optional = true, description = ""
			+ "Pattern for declaring class of methods to be analyzed, e.g., 'java.util.*' will consider every "
			+ "method declared in classes from package java.util. If not set, every method call is processed.")
	public PatternList declaringClassPatternList = null;

	/** {@inheritDoc} */
	@Override
	protected void processAST(CompilationUnitDeclaration ast,
			final IJavaElement javaElement) {
		ast.traverse(new ASTVisitor() {
			@Override
			public boolean visit(AllocationExpression allocationExpression,
					BlockScope scope) {
				process(allocationExpression.binding, javaElement);
				return super.visit(allocationExpression, scope);
			}

			@Override
			public boolean visit(MessageSend messageSend, BlockScope scope) {
				process(messageSend.binding, javaElement);
				return super.visit(messageSend, scope);
			}
		}, (CompilationUnitScope) null);
	}

	/** Process a method call */
	private void process(MethodBinding binding, IJavaElement javaElement) {
		ReferenceBinding declaringClass = binding.original().declaringClass;
		String fqn = EcjUtils.getFullQualifiedClassName(declaringClass);
		if (declaringClassPatternList == null
				|| declaringClassPatternList.emptyOrMatchesAny(fqn)) {
			processMethodCall(binding, javaElement);
		}
	}

	/** Processes the method call */
	protected abstract void processMethodCall(MethodBinding binding,
			IJavaElement javaElement);

}
