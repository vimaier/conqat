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

import org.apache.bcel.classfile.JavaClass;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.traversal.ETargetNodes;
import org.conqat.engine.commons.traversal.NodeTraversingProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.java.library.JavaLibrary;
import org.conqat.engine.java.library.Modifiers;
import org.conqat.engine.java.resource.IJavaElement;
import org.conqat.engine.java.resource.IJavaResource;
import org.conqat.engine.java.resource.JavaElementUtils;

/**
 * Base class for analyzers that do some local analysis on every Java class.
 * This class takes a Java scope and iterates over all classes. This allows to
 * specify the inclusion of particular classes by defining modifiers. If no
 * modifiers are provided all classes are included.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: A7C9343A6E66B3C578055C4EABC2F385
 */
public abstract class JavaAnalyzerBase extends
		NodeTraversingProcessorBase<IJavaResource> {

	/** Single library instance. */
	protected static final JavaLibrary javaLibrary = JavaLibrary.getInstance();

	/** Set of modifiers to include. */
	private final HashSet<Modifiers> modifierSet = new HashSet<Modifiers>();

	/** {@inheritDoc} */
	@Override
	protected ETargetNodes getTargetNodes() {
		return ETargetNodes.ALL;
	}

	/** Add modifier for included types. */
	@AConQATParameter(name = "include-type", minOccurrences = 0, description = "Add modifier that describes which types (classes) "
			+ "should be included in the analysis. "
			+ "If no modifiers are specified all types are analyzed.")
	public void addTypeInclusionModifier(
			@AConQATAttribute(name = "modifier", description = "Modifier pattern: "
					+ Modifiers.TYPE_PATTERN_DOC) String modifierCode) {
		Modifiers modifier = new Modifiers(modifierCode);

		if (modifier.isEmpty()) {
			getLogger().warn(
					"String '" + modifierCode + "' defines an empty modifier.");
			return;
		}

		modifierSet.add(modifier);

	}

	/** {@inheritDoc} */
	@Override
	protected void processInput(IJavaResource root) throws ConQATException {
		super.processInput(root);
		
		// add keys after processor to allow for dynamic key calculation
		if (getKeys() != null) {
			NodeUtils.addToDisplayList(root, getKeys());
		}
	}

	/** {@inheritDoc} */
	@Override
	public void visit(IJavaResource resource) throws ConQATException {
		if (resource instanceof IJavaElement) {
			IJavaElement javaElement = (IJavaElement) resource;
			JavaClass analyzedClass;
			try {
				analyzedClass = JavaElementUtils.obtainBcelClass(javaElement);
			} catch (ConQATException ex) {
				getLogger().warn(
						"Could not analyze class " + javaElement + ": "
								+ ex.getMessage(), ex);
				return;
			}

			Modifiers mods = new Modifiers(analyzedClass.getAccessFlags(), true);
			if (isIncluded(mods)) {
				analyze(javaElement, analyzedClass);
			}
		}
	}

	/** Checks if a class is included. */
	private boolean isIncluded(Modifiers mods) {
		if (modifierSet.isEmpty()) {
			return true;
		}
		for (Modifiers included : modifierSet) {
			if (included.isSatisfied(mods)) {
				return true;
			}
		}
		return false;
	}

	/** Obtain list of keys to add to the display list. */
	protected abstract String[] getKeys();

	/**
	 * Analyze a Java class and write the results to the corresponding keys.
	 * 
	 * @param javaElement
	 *            the ConQATNode representing the class
	 * @param clazz
	 *            the BCEL clazz object
	 * @throws ConQATException
	 *             used to signal an exception during analysis
	 */
	protected abstract void analyze(IJavaElement javaElement, JavaClass clazz)
			throws ConQATException;
}