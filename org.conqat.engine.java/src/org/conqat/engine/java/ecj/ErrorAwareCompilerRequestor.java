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

import java.util.ArrayList;
import java.util.List;

import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.UnmodifiableList;
import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.ICompilerRequestor;

/**
 * This compiler requestor only stores the errors generated for the specified
 * file.
 * 
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 00C87D4DD4E207198DFC5AE02985B56A
 */
public class ErrorAwareCompilerRequestor implements ICompilerRequestor {

	/** List of errors. */
	private final List<CategorizedProblem> errors = new ArrayList<CategorizedProblem>();

	/** Path of the file errors should be stored for. */
	private final String path;

	/**
	 * Create requestor.
	 * 
	 * @param path
	 *            Path of the file errors should be stored for. Exactly the same
	 *            path as used with
	 *            {@link EcjASTAccess#compileAST(String, String, String[], String, EcjCompilerOptions)}
	 *            should be used here.
	 */
	public ErrorAwareCompilerRequestor(String path) {
		this.path = path;
	}

	/** {@inheritDoc} */
	@Override
	public void acceptResult(CompilationResult result) {
		if (String.valueOf(result.getFileName()).equals(path)) {
			EcjUtils.addAllProblems(result.getErrors(), errors);
		}
	}

	/**
	 * Returns the errors for the specified type.
	 */
	public UnmodifiableList<CategorizedProblem> getErrors() {
		return CollectionUtils.asUnmodifiable(errors);
	}

}