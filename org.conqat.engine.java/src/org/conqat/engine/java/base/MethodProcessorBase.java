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
package org.conqat.engine.java.base;

import java.util.HashSet;
import java.util.Set;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.java.library.Modifiers;
import org.conqat.engine.java.resource.IJavaElement;

/**
 * Base class for processor that work on methods. This allows to specify the
 * inclusion of particular methods by defining modifiers. If no modifiers are
 * provided all methods are included.
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: F9DB6672EFC80EBF7C6B825CF50FB663
 */
public abstract class MethodProcessorBase extends JavaAnalyzerBase {

	/** Set of method modifiers to include. */
	private final HashSet<Modifiers> methodModifierSet = new HashSet<Modifiers>();

	/** Add modifier for included method. */
	@AConQATParameter(name = "include-method", minOccurrences = 0, description = "Add modifier that describes which "
			+ "methods should be include in analysis. "
			+ "If no modifiers are specified all methods are analyzed.")
	public void addMethodInclusionModifier(
			@AConQATAttribute(name = "modifier", description = "Modifier pattern: "
					+ Modifiers.MEMBER_PATTERN_DOC) String modifierCode) {
		Modifiers modifier = new Modifiers(modifierCode);

		if (modifier.isEmpty()) {
			getLogger().warn(
					"String '" + modifierCode + "' defines an empty modifier.");
			return;
		}

		methodModifierSet.add(modifier);
	}

	/**
	 * Analyze class element. This forward analysis of all included methods to
	 * {@link #analyzeMethods(IJavaElement, Set)}.
	 */
	@Override
	protected void analyze(IJavaElement classElement, JavaClass clazz)
			throws ConQATException {
		HashSet<Method> methods = new HashSet<Method>();
		for (Method method : clazz.getMethods()) {
			Modifiers mods = new Modifiers(clazz.getAccessFlags(), false);
			if (isIncluded(mods)) {
				methods.add(method);
			}
		}
		analyzeMethods(classElement, methods);
	}

	/** Checks if a method is included. */
	private boolean isIncluded(Modifiers mods) {
		if (methodModifierSet.isEmpty()) {
			return true;
		}
		for (Modifiers included : methodModifierSet) {
			if (included.isSatisfied(mods)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Template method for analyzing methods.
	 * 
	 * @param classElement
	 *            the element the methods belong to.
	 * @param methods
	 *            the methods.
	 * @throws ConQATException
	 *             if any problems occur during analysis
	 */
	protected abstract void analyzeMethods(IJavaElement classElement,
			Set<Method> methods) throws ConQATException;
}