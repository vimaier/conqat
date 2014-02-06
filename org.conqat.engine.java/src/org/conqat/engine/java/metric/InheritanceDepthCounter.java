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

import org.apache.bcel.classfile.JavaClass;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.java.base.JavaAnalyzerBase;
import org.conqat.engine.java.resource.IJavaElement;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 0B7FEA3CBFBDCFAEEB8858649988607F
 */
@AConQATProcessor(description = "This analyzer determines the depth of "
		+ "inheritance for each class.")
public class InheritanceDepthCounter extends JavaAnalyzerBase {

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Depth of inheritance", type = "java.lang.Integer")
	public static final String DIT_KEY = "DIT";

	/** {@inheritDoc} */
	@Override
	protected void analyze(IJavaElement classElement, JavaClass clazz) {

		JavaClass[] superClasses = null;
		try {
			superClasses = clazz.getSuperClasses();
		} catch (ClassNotFoundException e) {
			getLogger().warn(
					"Can't determine super classes of class "
							+ classElement.getId() + ": " + e.getMessage());
			return;
		}

		if (superClasses == null
				|| (!classElement.getId().equals("java.lang.Object") && superClasses.length == 0)) {
			getLogger().warn(
					"Can't determine super classes of class "
							+ classElement.getId());
		} else {
			classElement.setValue(DIT_KEY, superClasses.length);
		}
	}

	/** {@inheritDoc} */
	@Override
	protected String[] getKeys() {
		return new String[] { DIT_KEY };
	}
}