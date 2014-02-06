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
package org.conqat.engine.java.listbuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantClass;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.JavaClass;
import org.conqat.engine.commons.keys.IDependencyListKey;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.java.library.JavaLibrary;
import org.conqat.engine.java.resource.IJavaElement;

/**
 * This processor creates a list of all classes a class imports.
 * 
 * @author $Author: steidl $
 * @version $Rev: 43636 $
 * @ConQAT.Rating GREEN Hash: F0DE7AA5DA84DD2489F95137BC7C7EDC
 */
@AConQATProcessor(description = "This processor creates a list of all "
		+ "classes a class imports (has listed in its constant pool).")
public class ImportListBuilder extends ListBuilderBase implements
		IDependencyListKey {

	/** Result key. */
	@AConQATKey(description = "Dependency number", type = "java.lang.Integer")
	public static final String NUM_KEY = "Dependencies";

	/** {@inheritDoc} */
	@Override
	protected String[] getKeys() {
		return new String[] { DEPENDENCY_LIST_KEY };
	}

	/** analyze dependencies for a Java class. */
	@Override
	protected void analyze(IJavaElement classElement, JavaClass clazz) {
		List<String> depList = createDependencyList(clazz);
		classElement.setValue(DEPENDENCY_LIST_KEY, depList);
		classElement.setValue(NUM_KEY, depList.size());
	}

	/**
	 * Create a list containing all referenced classes.
	 * 
	 * @return the list of all classes the given class depends on.
	 */
	private List<String> createDependencyList(JavaClass clazz) {
		Set<String> result = new HashSet<String>();
		ConstantPool cp = clazz.getConstantPool();
		String className = clazz.getClassName();
		for (Constant c : cp.getConstantPool()) {
			if (c instanceof ConstantClass) {
				String usedClassName = cp.constantToString(c);

				usedClassName = JavaLibrary
						.ignoreArtificialPrefix(usedClassName);
				if (!usedClassName.equals("") && !isBlacklisted(usedClassName)
						&& !className.equals(usedClassName)) {
					result.add(usedClassName);
				}
			}
		}
		return new ArrayList<String>(result);
	}
}