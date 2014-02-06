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

import static org.conqat.engine.java.ecj.EcjUtils.addAllProblems;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.UnmodifiableMap;
import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.internal.compiler.ClassFile;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.ICompilerRequestor;

/**
 * This requestor stores all compilation results of the compile process. For
 * large systems this can be very memory-intensive.
 * 
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 7E08CACC0F84BD5883D8068DFB61DA2A
 */
public class CollectingCompilerRequestor implements ICompilerRequestor {

	/**
	 * Maps from fully qualified name of the main type to the compilation
	 * result.
	 */
	private final Map<String, CompilationResult> results = new HashMap<String, CompilationResult>();

	/** {@inheritDoc} */
	@Override
	public void acceptResult(CompilationResult result) {
		String fqName = EcjUtils.getMainTypeName(result.compilationUnit);
		results.put(fqName, result);
	}

	/** Returns all problems */
	public List<CategorizedProblem> getProblems() {
		ArrayList<CategorizedProblem> problems = new ArrayList<CategorizedProblem>();
		for (CompilationResult result : results.values()) {
			addAllProblems(result.getAllProblems(), problems);
		}
		return problems;
	}

	/** Returns all errors */
	public List<CategorizedProblem> getErrors() {
		ArrayList<CategorizedProblem> errors = new ArrayList<CategorizedProblem>();
		for (CompilationResult result : results.values()) {
			addAllProblems(result.getErrors(), errors);
		}
		return errors;
	}

	/**
	 * Returns the problems for the specified type.
	 * 
	 * @return an empty list signals no problems, <code>null</code> signals that
	 *         the type wasn't found.
	 */
	public List<CategorizedProblem> getProblems(String typeName) {
		CompilationResult result = results.get(typeName);
		if (result == null) {
			return null;
		}

		ArrayList<CategorizedProblem> problems = new ArrayList<CategorizedProblem>();
		addAllProblems(result.getAllProblems(), problems);
		return problems;
	}

	/**
	 * Returns the errors for the specified type.
	 * 
	 * @return an empty list signals no problems, <code>null</code> signals that
	 *         the type wasn't found.
	 */
	public List<CategorizedProblem> getErrors(String typeName) {
		CompilationResult result = results.get(typeName);
		if (result == null) {
			return null;
		}

		ArrayList<CategorizedProblem> problems = new ArrayList<CategorizedProblem>();
		addAllProblems(result.getErrors(), problems);
		return problems;
	}

	/**
	 * Get compilation results; maps from fully qualified name of the main type
	 * to the compilation result.
	 */
	public UnmodifiableMap<String, CompilationResult> getCompilationResults() {
		return CollectionUtils.asUnmodifiable(results);
	}

	/** Get all class files created during compilation. */
	public List<ClassFile> getClassFiles() {
		ArrayList<ClassFile> classFiles = new ArrayList<ClassFile>();
		for (CompilationResult result : results.values()) {
			classFiles.addAll(Arrays.asList(result.getClassFiles()));
		}
		return classFiles;
	}

	/**
	 * Get all class files created for the specified type.
	 * 
	 * @return an empty list signals that no class files were generated,
	 *         <code>null</code> signals that the type wasn't found.
	 */
	public List<ClassFile> getClassFiles(String typeName) {
		CompilationResult result = results.get(typeName);
		if (result == null) {
			return null;
		}

		ArrayList<ClassFile> classFiles = new ArrayList<ClassFile>();
		classFiles.addAll(Arrays.asList(result.getClassFiles()));
		return classFiles;
	}

}