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

import org.apache.bcel.classfile.JavaClass;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.java.base.JavaAnalyzerBase;
import org.conqat.engine.java.resource.IJavaElement;

/**
 * This processor extracts the type for each bytecode element.
 * 
 * @author Benjamin Hummel
 * @author $Author: juergens $
 * @version $Rev: 35196 $
 * @ConQAT.Rating GREEN Hash: F92376A169E18378990E2B37F50C7902
 */
@AConQATProcessor(description = "This processor extracts the type for each byte code element. "
		+ "This is one of 'annotation', 'enum', 'interface', 'abstract class', 'class'.")
public class TypeExtractor extends JavaAnalyzerBase {

	/** The key to use for saving. */
	@AConQATKey(description = "Type", type = "java.lang.String")
	public static final String TYPE_KEY = "Type";

	/** {@inheritDoc} */
	@Override
	protected void analyze(IJavaElement classElement, JavaClass clazz) {
		if (clazz.isAnnotation()) {
			classElement.setValue(TYPE_KEY, "annotation");
		} else if (clazz.isEnum()) {
			classElement.setValue(TYPE_KEY, "enum");
		} else if (clazz.isInterface()) {
			classElement.setValue(TYPE_KEY, "interface");
		} else if (clazz.isAbstract()) {
			classElement.setValue(TYPE_KEY, "abstract class");
		} else {
			classElement.setValue(TYPE_KEY, "class");
		}
	}

	/** {@inheritDoc} */
	@Override
	protected String[] getKeys() {
		return new String[] { TYPE_KEY };
	}
}