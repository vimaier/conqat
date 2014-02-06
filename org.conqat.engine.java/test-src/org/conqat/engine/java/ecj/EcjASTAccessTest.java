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
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashSet;

import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.internal.compiler.ClassFile;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;

import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.test.CCSMTestCaseBase;

/**
 * Test class for {@link EcjASTAccess}.
 * 
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @levd.rating GREEN Hash: 8C9B2D7A3317584E33C142F1954B0DDD
 */
public class EcjASTAccessTest extends CCSMTestCaseBase {

	/** Test folder. */
	private final File srcFolder = useTestFile("ecjmultifile");

	/** The main source file. */
	private final File mainSrcFile = new File(srcFolder, "MainClass.java");

	/**
	 * When the source path is present on the class path, all referenced classes
	 * are compiled by the ECJ. This test checks different aspects of the
	 * generated result.
	 */
	public void testWithClasspath() throws IOException {

		String[] classpath = EcjUtils.obtainClasspath(srcFolder
				.getAbsolutePath());

		CollectingCompilerRequestor requestor = compile(classpath);

		assertEquals(6, requestor.getClassFiles().size());

		HashSet<String> classNames = new HashSet<String>();

		for (ClassFile classFile : requestor.getClassFiles()) {
			classNames.add(EcjUtils.getFQName(classFile.getCompoundName()));
		}

		assertTrue(classNames.contains("MainClass"));
		assertTrue(classNames.contains("ReferencedClass1"));
		assertTrue(classNames.contains("ReferencedClass2"));
		assertTrue(classNames.contains("ReferencedClass2$InnerClass"));
		assertTrue(classNames
				.contains("ReferencedClass2$InnerClass$InnerClass2ndLevel"));
		assertTrue(classNames.contains("ReferencedClassWithErrors1"));

		// class files
		assertEquals(1, requestor.getClassFiles("MainClass").size());
		assertEquals(1, requestor.getClassFiles("ReferencedClass1").size());
		assertEquals(3, requestor.getClassFiles("ReferencedClass2").size());

		// errors
		assertEquals(0, requestor.getErrors("MainClass").size());
		assertEquals(0, requestor.getErrors("ReferencedClass1").size());
		assertEquals(0, requestor.getErrors("ReferencedClass2").size());
		assertEquals(1, requestor.getErrors("ReferencedClassWithErrors1")
				.size());

		// problems
		assertEquals(1, requestor.getProblems("ReferencedClass1").size());
	}

	/**
	 * When no classpath is provided, the compiler compiles the class but
	 * generates problems it can can find neither <code>java.lang.Object</code>
	 * nor our referenced classes
	 */
	public void testWithoutClasspath() throws IOException {
		CollectingCompilerRequestor requestor = compile(new String[0]);
		assertEquals(1, requestor.getClassFiles().size());
		assertEquals("MainClass", EcjUtils.getFQName(CollectionUtils.getAny(
				requestor.getClassFiles()).getCompoundName()));

		// we expect 6 errors in total as the compiler can find neither
		// java.lang.Object nor our referenced classes
		assertEquals(6, requestor.getErrors().size());

	}

	/**
	 * When only the boot class path is provided the compiler should be able to
	 * find all system classes but not our referenced classes.
	 */
	public void testWithBootClasspath() throws IOException {
		CollectingCompilerRequestor requestor = compile(EcjUtils
				.obtainClasspath());
		assertEquals(1, requestor.getClassFiles().size());
		assertEquals("MainClass", EcjUtils.getFQName(CollectionUtils.getAny(
				requestor.getClassFiles()).getCompoundName()));

		// the compiler should be able to find all system classes but not our
		// referenced classes
		assertEquals(3, requestor.getErrors().size());

		for (CategorizedProblem problem : requestor.getErrors()) {
			assertEquals(50, problem.getCategoryID());
		}
	}

	/** Compile main class. */
	private CollectingCompilerRequestor compile(String[] classpath)
			throws IOException {
		String code = FileSystemUtils.readFile(mainSrcFile);
		CollectingCompilerRequestor requestor = new CollectingCompilerRequestor();
		EcjASTAccess.compileAST(mainSrcFile.getAbsolutePath(), code, classpath,
				Charset.defaultCharset().name(), new EcjCompilerOptions(
						CompilerOptions.VERSION_1_6), requestor);
		return requestor;

	}
}