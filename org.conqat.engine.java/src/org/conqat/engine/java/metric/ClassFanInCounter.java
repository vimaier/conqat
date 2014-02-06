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
package org.conqat.engine.java.metric;

import java.util.ArrayList;
import java.util.List;

import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantClass;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.JavaClass;
import org.conqat.engine.commons.ConQATPipelineProcessorBase;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.traversal.INodeVisitor;
import org.conqat.engine.commons.traversal.TraversalUtils;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.logging.IConQATLogger;
import org.conqat.engine.java.resource.IJavaElement;
import org.conqat.engine.java.resource.IJavaResource;
import org.conqat.engine.java.resource.JavaElementUtils;
import org.conqat.lib.commons.collections.ListMap;
import org.conqat.lib.commons.error.NeverThrownRuntimeException;

/**
 * This processor counts the number of classes depending on a class (Class
 * Fan-In).
 * 
 * @author Florian Deissenboeck
 * @author $Author: steidl $
 * @version $Rev: 43636 $
 * @ConQAT.Rating GREEN Hash: 46C44B5BE01208176018468E6B6F685A
 */
@AConQATProcessor(description = "This processor counts the number of classes depending"
		+ " on a class (Class Fan-In).")
public class ClassFanInCounter extends
		ConQATPipelineProcessorBase<IJavaResource> {

	/** Result key. */
	@AConQATKey(description = "Class Fan-In", type = "java.lang.Integer")
	public static final String KEY = "Fan-In";

	/** Maps from a class to a list of classes depending on it. */
	private final ListMap<String, String> dependenciesMap = new ListMap<String, String>();

	/**
	 * Redeclared to get rid of warning from nested classes (syntethic accessor
	 * method).
	 */
	@Override
	protected IConQATLogger getLogger() {
		return super.getLogger();
	}

	/** {@inheritDoc} */
	@Override
	protected void processInput(IJavaResource root) {

		NodeUtils.addToDisplayList(root, KEY);

		// store all dependencies
		TraversalUtils.visitAllDepthFirst(new DependenciesVisitor(), root);

		// annotate nodes, this can't be done in the first step as ALL
		// dependencies must be recorded first
		TraversalUtils.visitAllDepthFirst(new FanInVisitor(), root);
	}

	/** Visits all classes and store dependencies. */
	private class DependenciesVisitor implements
			INodeVisitor<IJavaResource, NeverThrownRuntimeException> {

		/** Stores dependencies in the dependencies-map. */
		@Override
		public void visit(IJavaResource element) {
			// in a 'fresh' tree this is called for class elements only as only
			// they can be leaves. If, however, the tree was modified, this may
			// be called for package elements. In this case just skip them.
			if (!(element instanceof IJavaElement)) {
				return;
			}

			JavaClass clazz;
			try {
				clazz = JavaElementUtils
						.obtainBcelClass((IJavaElement) element);
			} catch (ConQATException ex) {
				getLogger().warn(
						"Can't determine dependencies of class "
								+ element.getId());
				return;
			}

			ArrayList<String> dependencies = createDependencyList(clazz);
			String className = element.getId();
			for (String depName : dependencies) {
				dependenciesMap.add(depName, className);
			}
		}

		/** Create a list of dependencies for a given class. */
		private ArrayList<String> createDependencyList(JavaClass clazz) {

			ArrayList<String> depList = new ArrayList<String>();
			ConstantPool cp = clazz.getConstantPool();
			String className = clazz.getClassName();

			for (Constant constant : cp.getConstantPool()) {
				if (constant instanceof ConstantClass) {

					String usedClassName = cp.constantToString(constant);

					// don't include self-reference
					if (!className.equals(usedClassName)) {
						depList.add(usedClassName);
					}
				}
			}
			return depList;
		}
	}

	/** Visit all classes and count the list of dependencies. */
	private class FanInVisitor implements
			INodeVisitor<IJavaResource, NeverThrownRuntimeException> {

		/**
		 * Count the list of dependencies stored in the dependencies map an
		 * annoatate element.
		 */
		@Override
		public void visit(IJavaResource element) {
			// in a 'fresh' tree this is called for class elements only as only
			// they can be leaves. If, however, the tree was modified, this may
			// be called for package elements. In this case just skip them.
			if (!(element instanceof IJavaElement)) {
				return;
			}

			List<String> dependers = dependenciesMap.getCollection(element.getId());
			int dependerCount = 0;
			if (dependers != null) {
				dependerCount = dependers.size();
			}
			element.setValue(KEY, dependerCount);
		}
	}
}