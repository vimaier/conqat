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
package org.conqat.engine.java.extract;

import java.util.ArrayList;
import java.util.List;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.java.base.JavaAnalyzerBase;
import org.conqat.engine.java.resource.IJavaElement;
import org.conqat.engine.java.resource.JavaElementUtils;

/**
 * {@ConQAT.Doc}
 * 
 * @author Benjamin Hummel
 * @author $Author: juergens $
 * @version $Rev: 35196 $
 * @ConQAT.Rating GREEN Hash: AF043A2C5D19BE87D908A0A5CD9B7E6B
 */
@AConQATProcessor(description = "This processor extracts the method signatures from a class.")
public class MethodSignatureExtractor extends JavaAnalyzerBase {

	/** The key to use for saving. */
	@AConQATKey(description = "Methods", type = "java.util.List<java.lang.String>")
	public static final String METHODS_KEY = "Methods";

	/** {@inheritDoc} */
	@Override
	protected void analyze(IJavaElement classElement, JavaClass clazz) {
		try {
			JavaClass javaClass = JavaElementUtils
					.obtainBcelClass(classElement);
			classElement.setValue(METHODS_KEY, getMethods(javaClass));
		} catch (ConQATException ex) {
			getLogger()
					.warn("Could not determine methods for: " + classElement);
		}
	}

	/** Get method signatures from java class. */
	private List<String> getMethods(JavaClass javaClass) {
		List<String> result = new ArrayList<String>();
		for (Method method : javaClass.getMethods()) {
			result.add(method.toString());
		}
		return result;
	}

	/** {@inheritDoc} */
	@Override
	protected String[] getKeys() {
		return new String[] { METHODS_KEY };
	}
}