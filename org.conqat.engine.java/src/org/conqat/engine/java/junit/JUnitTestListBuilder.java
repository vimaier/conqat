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
package org.conqat.engine.java.junit;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantClass;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.JavaClass;
import org.conqat.engine.commons.traversal.INodeVisitor;
import org.conqat.engine.commons.traversal.TraversalUtils;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.java.base.JavaAnalyzerBase;
import org.conqat.engine.java.library.JavaLibrary;
import org.conqat.engine.java.resource.IJavaElement;
import org.conqat.engine.java.resource.IJavaResource;
import org.conqat.engine.java.resource.JavaElementUtils;
import org.conqat.lib.commons.collections.ListMap;

/**
 * This processor creates, for each java class in a scope, a list of all JUnit
 * test cases (i.e. classes inherited from junit.framework.TestCase) referencing
 * the class.
 * 
 * @author Benjamin Hummel
 * @author $Author: steidl $
 * @version $Rev: 43636 $
 * @ConQAT.Rating GREEN Hash: 8E902D41280B41892B11D0EA9F356755
 */
@AConQATProcessor(description = "This processor creates for each class a list of all JUnit test "
		+ "cases (i.e. classes inherited from junit.framework.TestCase) referencing the class. "
		+ "Referencing here means it can be found in the constant pool of the tests byte code.")
public class JUnitTestListBuilder extends JavaAnalyzerBase {

	/** Result key. */
	@AConQATKey(description = "The list of tests referencing the class", type = "java.util.List<String>")
	public static final String LIST_KEY = "test list";

	/** Result key. */
	@AConQATKey(description = "The number of test classes referencing the class", type = "java.lang.Integer")
	public static final String NUM_KEY = "#tests";

	/** Name of the JUnit test case class. */
	private static final String TESTCASE_NAME = TestCase.class.getName();

	/** A mapping from the class name to the names of the tests. */
	private final ListMap<String, String> classToTests = new ListMap<String, String>();

	/** {@inheritDoc} */
	@Override
	protected String[] getKeys() {
		return new String[] { LIST_KEY };
	}

	/** Fill the {@link #classToTests} map. */
	@Override
	protected void setUp(IJavaResource root) throws ConQATException {
		super.setUp(root);
		TraversalUtils.visitLeavesDepthFirst(new JUnitVisitor(), root);
	}

	/** analyze dependencies for a Java class. */
	@Override
	protected void analyze(IJavaElement classElement, JavaClass clazz) {
		List<String> depList = classToTests.getCollection(classElement.getId());
		if (depList == null) {
			classElement.setValue(NUM_KEY, 0);
		} else {
			classElement.setValue(LIST_KEY, depList);
			classElement.setValue(NUM_KEY, depList.size());
		}
	}

	/** This visitor fills the {@link JUnitTestListBuilder#classToTests}-map. */
	private class JUnitVisitor implements
			INodeVisitor<IJavaResource, ConQATException> {

		/** Process Java element. */
		@Override
		public void visit(IJavaResource element) throws ConQATException {
			if (!(element instanceof IJavaElement)) {
				return;
			}

			JavaClass clazz;
			try {
				clazz = JavaElementUtils
						.obtainBcelClass((IJavaElement) element);
			} catch (ConQATException ex) {
				getLogger().warn("Couldn't parse class " + element.getId());
				return;
			}

			try {
				if (isJUnitClass(clazz)) {
					addToClassToTestsMap(clazz);
				}
			} catch (ClassNotFoundException ex) {
				throw new ConQATException("Could not resolve class!", ex);
			}
		}

		/** Returns true if this inherits from {@link TestCase}. */
		private boolean isJUnitClass(JavaClass clazz)
				throws ClassNotFoundException {
			for (JavaClass superClass : clazz.getSuperClasses()) {
				if (TESTCASE_NAME.equals(superClass.getClassName())) {
					return true;
				}
			}
			return false;
		}

		/**
		 * Fill the {@link #classToTests} map for a given JUnit test case class.
		 */
		private void addToClassToTestsMap(JavaClass clazz) {
			Set<String> result = new HashSet<String>();
			ConstantPool cp = clazz.getConstantPool();
			String className = clazz.getClassName();
			for (Constant c : cp.getConstantPool()) {
				if (c instanceof ConstantClass) {
					String usedClassName = cp.constantToString(c);

					usedClassName = JavaLibrary
							.ignoreArtificialPrefix(usedClassName);
					if (usedClassName.equals("")) {
						continue;
					}
					if (JavaLibrary.isInternalClass(usedClassName)) {
						continue;
					}
					// don't include self-reference
					if (!className.equals(usedClassName)) {
						result.add(usedClassName);
					}
				}
			}
			for (String foundName : result) {
				classToTests.add(foundName, className);
			}
		}

	}
}