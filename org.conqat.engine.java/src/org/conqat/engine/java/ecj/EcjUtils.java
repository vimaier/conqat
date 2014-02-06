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

import java.io.File;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.string.StringUtils;
import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

/**
 * Collection of utility methods for the Eclipse compiler.
 * 
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: B0EC41D9976E086CA81521F723A2DFC3
 */
public class EcjUtils {
	/** Property name used for the boot class path. */
	private static final String SUN_BOOT_CLASS_PATH = "sun.boot.class.path";

	/** Convert ECJ style names to normal string. */
	public static String getFQName(char[][] name) {
		StringBuilder result = new StringBuilder();
		for (char[] part : name) {
			if (result.length() != 0) {
				result.append(File.separator);
			}
			result.append(part);
		}
		return result.toString();
	}

	/**
	 * Get the boot class path defined by property {@value #SUN_BOOT_CLASS_PATH}
	 * . This returns only paths that actually exist on the system as the ECJ
	 * fails otherwise.
	 */
	public static LinkedHashSet<String> getBootClassPath() {
		LinkedHashSet<String> classPath = new LinkedHashSet<String>();
		String classPathString = System.getProperty(SUN_BOOT_CLASS_PATH);
		for (String pathElement : classPathString.split(File.pathSeparator)) {
			if (new File(pathElement).exists()) {
				classPath.add(pathElement);
			}
		}
		return classPath;
	}

	/**
	 * Obtain a class path object that contains the boot class path plus all
	 * specified elements.
	 */
	public static String[] obtainClasspath(String... pathElements) {
		LinkedHashSet<String> classPath = getBootClassPath();
		classPath.addAll(Arrays.asList(pathElements));
		return CollectionUtils.toArray(classPath, String.class);
	}

	/**
	 * Get the fully qualified main type name of a compilation unit.
	 */
	public static String getMainTypeName(ICompilationUnit compilationUnit) {
		String name;

		if (compilationUnit.getPackageName() == null) {
			// default package
			name = StringUtils.EMPTY_STRING;
		} else {
			name = String.valueOf(compilationUnit.getPackageName()) + ".";
		}

		return name + String.valueOf(compilationUnit.getMainTypeName());
	}

	/** Add all problems to list if not <code>null</code>. */
	public static void addAllProblems(CategorizedProblem[] problems,
			List<CategorizedProblem> problemList) {
		if (problems != null) {
			problemList.addAll(Arrays.asList(problems));
		}
	}

	/** Returns the full qualified class name of the given type. */
	public static String getFullQualifiedClassName(TypeBinding typeBinding) {
		String packageName = new String(typeBinding.qualifiedPackageName());
		if (!packageName.isEmpty()) {
			packageName += ".";
		}
		return packageName + new String(typeBinding.qualifiedSourceName());
	}

	/**
	 * Constructs a String representation of the given MethodBinding. The
	 * notation is <declaringClassFQN>#<method signature>.
	 */
	public static String methodBindingToString(MethodBinding methodBinding) {
		MethodBinding original = methodBinding.original();
		ReferenceBinding declaringClass = original.declaringClass;
		return getFullQualifiedClassName(declaringClass) + "#"
				+ new String(original.readableName());
	}

}