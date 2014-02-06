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

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashSet;

import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;

import org.apache.bcel.classfile.JavaClass;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.java.ecj.EcjASTAccess;
import org.conqat.engine.java.ecj.EcjCompilationResult;
import org.conqat.engine.java.ecj.EcjCompilerOptions;
import org.conqat.engine.java.ecj.EcjUtils;
import org.conqat.engine.java.ecj.ErrorAwareCompilerRequestor;
import org.conqat.engine.java.resource.IJavaElement;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.lib.commons.cache4j.CacheFactory;
import org.conqat.lib.commons.cache4j.ICache;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.UnmodifiableList;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;

import com.sun.javadoc.ClassDoc;

/**
 * This class offers commonly used functionality for analyzing java files.
 * 
 * All non-static methods are to be accessed using the singleton instance that
 * is accessible via {@link #getInstance()}. They are not static, since they
 * potentially use caching.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 47044 $
 * @ConQAT.Rating GREEN Hash: 1225BD9D098ABD039E29C449A0D6A9E2
 */
public class JavaLibrary {

	/** singleton instance */
	private static JavaLibrary instance;

	/** Cache for the AST representations of the class elements. */
	private final ICache<JavaASTCacheKey, ASTCompilationUnit, ConQATException> astCache = CacheFactory
			.obtainCache(JavaLibrary.class, new JavaASTCacheFactory());

	/** Cache for JavaDoc documentation. */
	private final JavaDocCache docCache = new JavaDocCache();

	/** Get the sole singleton instance. */
	public static JavaLibrary getInstance() {
		if (instance == null) {
			instance = new JavaLibrary();
		}
		return instance;
	}

	/** prevent intantiation */
	private JavaLibrary() {
		// prevent instantiation
	}

	/**
	 * Get full qualified class name of a class. The name is derived by
	 * analyzing the package statement in the file.
	 * 
	 * @param path
	 *            path of the source file (only used for error messages)
	 * @return the full qualified class name
	 * @throws ConQATException
	 *             if file can't be read or another problem occured during
	 *             package name extraction.
	 */
	public static String getFQClassName(String path, StringReader reader)
			throws ConQATException {
		try {
			String packageName = new PackageDeclarationExtractor()
					.getPackageNameFromReader(path, reader);
			if (packageName != null) {
				return packageName + "." + getClassName(path);
			}
			return getClassName(path);
		} catch (IOException e) {
			throw new ConQATException(
					"Could not extract package name for file " + path + " ("
							+ e.getMessage() + ")", e);
		}
	}

	/**
	 * Get the class name of a java class. The class name is derived from the
	 * file name.
	 */
	private static String getClassName(String path) {
		// strip .java extension
		String result = path.substring(0, path.length() - 5);

		// be tolerate in terms of path separators
		int index = FileSystemUtils.normalizeSeparators(result)
				.lastIndexOf('/');
		if (index < 0) {
			return result;
		}
		return result.substring(index + 1);
	}

	/**
	 * Returns the byte-code name for a class name, i.e. replaces dots with
	 * slashes.
	 */
	public static String getByteCodeName(String className) {
		return className.replace('.', '/');
	}

	/**
	 * Returns the byte-code name for a java element, i.e. replaces dots with
	 * slashes in the class name.
	 */
	public static String getByteCodeName(IJavaElement javaElement) {
		return getByteCodeName(javaElement.getClassName());
	}

	/**
	 * Get AST for a class element.
	 * 
	 * @return the AST
	 * @throws ConQATException
	 *             if parsing fails
	 */
	public ASTCompilationUnit getAST(ITokenElement element)
			throws ConQATException {
		return astCache.obtain(new JavaASTCacheKey(element));
	}

	/**
	 * Returns the ECJ AST for the given class element. This uses a standard set
	 * of compiler options for Java version 1.6
	 * 
	 * <I>Note</I>: We do not cache the AST as it is not immutable.
	 * 
	 * @return the AST or null if nothing compilable was found (such as
	 *         interfaces).
	 */
	public static EcjCompilationResult getEcjAST(IJavaElement classElement)
			throws ConQATException {
		return getEcjAST(classElement, new EcjCompilerOptions(
				CompilerOptions.VERSION_1_6));
	}

	/**
	 * Returns the ECJ AST for the given class element. This allows to specify
	 * compiler options.
	 * 
	 * <I>Note</I>: We do not cache the AST as it is not immutable.
	 * 
	 * @return the AST or null if nothing compilable was found (such as
	 *         interfaces).
	 */
	public static EcjCompilationResult getEcjAST(IJavaElement element,
			EcjCompilerOptions options) throws ConQATException {
		String content = element.getTextContent();

		UnmodifiableList<String> scopeClasspath = element.getJavaContext()
				.getClassPath();

		String[] classpath = EcjUtils.obtainClasspath(CollectionUtils.toArray(
				scopeClasspath, String.class));

		ErrorAwareCompilerRequestor requestor = new ErrorAwareCompilerRequestor(
				element.getUniformPath());

		CompilationUnitDeclaration result = EcjASTAccess.compileAST(element
				.getUniformPath(), content, classpath, element.getEncoding()
				.name(), options, requestor);
		return new EcjCompilationResult(result, CollectionUtils.toArray(
				requestor.getErrors(), CategorizedProblem.class));
	}

	/**
	 * Get JavaDoc documentation for class. This includes cache management.
	 * 
	 * @throws ConQATException
	 *             if JavaDoc could not be retrieved for the element.
	 */
	public ClassDoc getDoc(IJavaElement javaClassElement)
			throws ConQATException {
		return docCache.getDoc(javaClassElement);
	}

	/**
	 * Get set of alls super classes and all interfaces of a class.
	 * 
	 * @throws ConQATException
	 *             if super classes and interfaces could not be resolved.
	 */
	public static HashSet<JavaClass> getSuperClassesAndInterfaces(
			JavaClass clazz) throws ConQATException {

		HashSet<JavaClass> result = new HashSet<JavaClass>();
		try {
			JavaClass[] interfaces = clazz.getAllInterfaces();
			result.addAll(Arrays.asList(interfaces));

			JavaClass[] superClasses = clazz.getSuperClasses();
			result.addAll(Arrays.asList(superClasses));
		} catch (ClassNotFoundException e) {
			throw new ConQATException(
					"Could not determine interfaces or super classes for class: "
							+ clazz.getClassName());
		}
		return result;
	}

	/**
	 * Extracts the class name from a VM type signature (see
	 * http://java.sun.com/
	 * javase/6/docs/technotes/guides/jni/spec/types.html#wp16432). If this is
	 * just a plain class name is is returned without modification. In case of
	 * problems or primitive types an empty string is returned.
	 */
	public static String ignoreArtificialPrefix(String className) {
		if (className.startsWith("[")) {
			while (className.startsWith("[")) {
				className = className.substring(1);
			}
			if (className.startsWith("L") && className.endsWith(";")) {
				className = className.substring(1, className.length() - 1);
			} else {
				return "";
			}
		}
		return className.replace('/', '.');
	}

	/**
	 * Returns true if this is an internal class.
	 */
	public static boolean isInternalClass(String usedClassName) {
		return usedClassName.contains("$");
	}
}