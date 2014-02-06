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
package org.conqat.engine.java.filter;

import java.util.HashSet;
import java.util.Set;

import org.apache.bcel.classfile.JavaClass;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.java.library.JavaLibrary;
import org.conqat.engine.java.resource.IJavaElement;

/**
 * This processor filters types that are subtypes of a specific type.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: B5320F7DA98D55059D9D1DAB7693239C
 */
@AConQATProcessor(description = "This processor filters (discards) types that are subtypes of a specific type.")
public class SubTypeOfFilter extends JavaClassFilterBase {

	/** Set of modifiers to include. */
	private final Set<String> typeNames = new HashSet<String>();

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "type", description = "Super type to filter.")
	public void addType(
			@AConQATAttribute(name = "name", description = "Full qualified name of the type.") String name) {
		typeNames.add(name);
	}

	/** Check if a class is a subtype of one of the specified types. */
	@Override
	protected boolean isFiltered(IJavaElement classElement,
			JavaClass analyzedClass) {
		try {
			HashSet<JavaClass> superTypes = JavaLibrary
					.getSuperClassesAndInterfaces(analyzedClass);

			for (JavaClass superType : superTypes) {
				if (typeNames.contains(superType.getClassName())) {
					return true;
				}
			}
			return false;

		} catch (ConQATException e) {
			getLogger().warn(e);
			return false;
		}
	}
}