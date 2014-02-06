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
import org.conqat.engine.java.library.Modifiers;
import org.conqat.engine.java.resource.IJavaElement;

/**
 * Filter for keeping or discarding only certain types.
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: BE30F4BCA07640F9EBE541FB5039E6B8
 */
@AConQATProcessor(description = "Discard java elements based on their type (class, interface). "
		+ "If no include modifiers are given, everything is included. In any case exclusion is stronger "
		+ "than inclusion.")
public class TypeFilter extends JavaClassFilterBase {

	/** Set of modifiers to include. */
	private final Set<Modifiers> includeSet = new HashSet<Modifiers>();

	/** Set of modifiers to exclude. */
	private final Set<Modifiers> excludeSet = new HashSet<Modifiers>();

	/** Add modifier for included types. */
	@AConQATParameter(name = "include", description = "Add modifier that describes which types (classes) "
			+ "should be included in the result.")
	public void addInclude(
			@AConQATAttribute(name = "modifier", description = "Modifier pattern: "
					+ Modifiers.TYPE_PATTERN_DOC) String modifierCode) {
		Modifiers modifier = new Modifiers(modifierCode);

		if (modifier.isEmpty()) {
			getLogger().warn(
					"String '" + modifierCode + "' defines an empty modifier.");
			return;
		}

		includeSet.add(modifier);
	}

	/** Add modifier for excluded types. */
	@AConQATParameter(name = "exclude", description = "Add modifier that describes which types (classes) "
			+ "should be excluded from the result.")
	public void addExclude(
			@AConQATAttribute(name = "modifier", description = "Modifier pattern: "
					+ Modifiers.TYPE_PATTERN_DOC) String modifierCode) {
		Modifiers modifier = new Modifiers(modifierCode);

		if (modifier.isEmpty()) {
			getLogger().warn(
					"String '" + modifierCode + "' defines an empty modifier.");
			return;
		}
		excludeSet.add(modifier);
	}

	/** {@inheritDoc} */
	@Override
	protected boolean isFiltered(IJavaElement classElement, JavaClass clazz) {
		Modifiers mods = new Modifiers(clazz.getAccessFlags(), true);
		return !isIncluded(mods) || isExcluded(mods);
	}

	/** Returns for given modifiers if they are included. */
	private boolean isIncluded(Modifiers mods) {
		if (includeSet.isEmpty()) {
			return true;
		}
		for (Modifiers included : includeSet) {
			if (included.isSatisfied(mods)) {
				return true;
			}
		}
		return false;
	}

	/** Returns for given modifiers if they are excluded. */
	private boolean isExcluded(Modifiers mods) {
		for (Modifiers excluded : excludeSet) {
			if (excluded.isSatisfied(mods)) {
				return true;
			}
		}
		return false;
	}
}