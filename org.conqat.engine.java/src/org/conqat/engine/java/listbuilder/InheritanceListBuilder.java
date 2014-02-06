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
import java.util.List;

import org.apache.bcel.classfile.JavaClass;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.java.resource.IJavaElement;

/**
 * This processor creates a list with the super class and all interfaces a class
 * implements.
 * 
 * @author Florian Deissenboeck
 * @author Tilman Seifert
 * @author $Author: kinnen $
 * 
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 27319F096AFD09F499A68A67206F9B30
 */
@AConQATProcessor(description = "This processor creates for each class a list with the super "
		+ "class and all interfaces a class implements.")
public class InheritanceListBuilder extends ListBuilderBase {

	/** Result key. */
	@AConQATKey(description = "Dependency list", type = "java.util.List<String>")
	public static final String LIST_KEY = "Inheritance List";

	/** with or without interfaces */
	private boolean excludeInterfaces = false;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "exclude-interfaces", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "If this option is set to true, interfaces are not included in "
			+ "the inheritance list. Default is false")
	public void setExcludeInterfaces(
			@AConQATAttribute(name = "value", description = "exclude interfaces (true), or not (false) [false]") boolean excludeInterfaces) {
		this.excludeInterfaces = excludeInterfaces;
	}

	/** {@inheritDoc} */
	@Override
	protected String[] getKeys() {
		return new String[] { LIST_KEY };
	}

	/** {@inheritDoc} */
	@Override
	protected void analyze(IJavaElement classElement, JavaClass clazz) {
		classElement.setValue(LIST_KEY, createInheritanceList(clazz));
	}

	/** Create a list containing the super class and all implemented interfaces. */
	private List<String> createInheritanceList(JavaClass clazz) {
		List<String> inheritanceList = new ArrayList<String>();
		if (!isBlacklisted(clazz.getSuperclassName())) {
			inheritanceList.add(clazz.getSuperclassName());
		}

		if (!excludeInterfaces) {
			for (String iface : clazz.getInterfaceNames()) {
				if (!isBlacklisted(iface)) {
					inheritanceList.add(iface);
				}
			}
		}
		return inheritanceList;
	}
}