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
package org.conqat.engine.java.analyzer;

import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;

import org.apache.bcel.classfile.JavaClass;
import org.conqat.engine.commons.node.NodeConstants;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.java.base.JavaAnalyzerBase;
import org.conqat.engine.java.library.JavaLibrary;
import org.conqat.engine.java.resource.IJavaElement;

/**
 * {@ConQAT.Doc}
 * 
 * @author hummelb
 * @author $Author: hummelb $
 * @version $Rev: 47044 $
 * @ConQAT.Rating GREEN Hash: F4E708D42106B1D6D623406DBA59655E
 */
@AConQATProcessor(description = "Calculates the length of the longest method for each class.")
public class LongestMethodLengthAnalyzer extends JavaAnalyzerBase {

	/** Key for messages. */
	@AConQATKey(description = "Longest Method Length in Class", type = "java.lang.Integer")
	public static final String LONGEST_METHOD_KEY = NodeConstants.LONGEST_METHOD_KEY;

	/** {@inheritDoc} */
	@Override
	protected void analyze(IJavaElement classElement, JavaClass clazz) {
		int maxLength = 0;
		ASTCompilationUnit ast;
		try {
			ast = JavaLibrary.getInstance().getAST(classElement);
		} catch (ConQATException e) {
			getLogger().error("Could not get AST for " + classElement.getId(),
					e);
			return;
		}
		for (ASTMethodDeclaration method : ast
				.findChildrenOfType(ASTMethodDeclaration.class)) {
			int length = method.getEndLine() - method.getBeginLine() + 1;
			maxLength = Math.max(maxLength, length);
		}

		classElement.setValue(LONGEST_METHOD_KEY, maxLength);
	}

	/** {@inheritDoc} */
	@Override
	protected String[] getKeys() {
		return new String[] { LONGEST_METHOD_KEY };
	}
}