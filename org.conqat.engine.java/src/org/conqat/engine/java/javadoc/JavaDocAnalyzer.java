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
package org.conqat.engine.java.javadoc;

import java.util.ArrayList;
import java.util.List;

import org.apache.bcel.classfile.JavaClass;
import org.conqat.engine.commons.findings.FindingCategory;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.java.base.JavaAnalyzerBase;
import org.conqat.engine.java.library.Modifiers;
import org.conqat.engine.java.resource.IJavaElement;
import org.conqat.engine.java.resource.IJavaResource;
import org.conqat.lib.commons.collections.PairList;
import org.conqat.lib.commons.string.StringUtils;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 3A78E5AEF5D561AE00CFD38BA1B08113
 */
@AConQATProcessor(description = "This processor analyzes JavaDoc comments. "
		+ "The analyses to be performed can be selected by choosing an appropriate"
		+ "comment analyzer. The available analyzers are best determined via"
		+ "producer search.")
public class JavaDocAnalyzer extends JavaAnalyzerBase {

	/** Comment for modifiers. */
	private static final String MODIFIER_COMMENT = "Modifiers that describe the types the application "
			+ "of this analyzer is restricted to. The syntax for modifiers is "
			+ Modifiers.TYPE_PATTERN_DOC
			+ "[if the default is used, the analyzer will be applied "
			+ "to all types]";

	/**
	 * Special value for the modifier string that signals that all entities
	 * should be included.
	 */
	private static final String MODIFIER_DEFAULT_VALUE = "*";

	/** Key for JavaDoc findings. */
	@AConQATKey(description = "Key for Javadoc findings", type = "java.util.List<org.conqat.engine.commons.findings.Finding>")
	public static final String KEY = "JavaDoc Findings";

	/** List for method analyzers */
	private final PairList<IMethodDocAnalyzer, Modifiers> methodAnalyzers = new PairList<IMethodDocAnalyzer, Modifiers>();

	/** List for field analyzers */
	private final PairList<IFieldDocAnalyzer, Modifiers> fieldAnalyzers = new PairList<IFieldDocAnalyzer, Modifiers>();

	/** List for type analyzers */
	private final List<TypeAnalyzerDescriptor> typeAnalyzers = new ArrayList<TypeAnalyzerDescriptor>();

	/** The category used by this analyzer. */
	private FindingCategory findingCategory;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "type-analyzer", description = "Add analyzer for type comments.")
	public void addTypeAnalyzer(
			@AConQATAttribute(name = "modifiers", description = MODIFIER_COMMENT, defaultValue = MODIFIER_DEFAULT_VALUE) String modifierString,
			@AConQATAttribute(name = "analyzer", description = "Reference to analyzer") ITypeDocAnalyzer analyzer,
			@AConQATAttribute(name = "analyze-inner-classes", description = "Flag to signal if inner "
					+ "classes should be analyzed.", defaultValue = "true") boolean analyzeInnerClasses) {
		typeAnalyzers.add(new TypeAnalyzerDescriptor(analyzer,
				createModifier(modifierString), analyzeInnerClasses));
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "method-analyzer", description = "Add analyzer for method comments.")
	public void addMethodAnalyzer(
			@AConQATAttribute(name = "modifiers", description = MODIFIER_COMMENT, defaultValue = MODIFIER_DEFAULT_VALUE) String modifierString,
			@AConQATAttribute(name = "analyzer", description = "Reference to analyzer") IMethodDocAnalyzer analyzer) {
		methodAnalyzers.add(analyzer, createModifier(modifierString));

	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "field-analyzer", description = "Add analyzer for field comments.")
	public void addFieldAnalyzer(
			@AConQATAttribute(name = "modifiers", description = MODIFIER_COMMENT, defaultValue = MODIFIER_DEFAULT_VALUE) String modifierString,
			@AConQATAttribute(name = "analyzer", description = "Reference to analyzer") IFieldDocAnalyzer analyzer) {
		fieldAnalyzers.add(analyzer, createModifier(modifierString));
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "analyzer", description = "Add analyzer for type, methods and field comments.")
	public void addAnalyzer(
			@AConQATAttribute(name = "modifiers", description = MODIFIER_COMMENT, defaultValue = MODIFIER_DEFAULT_VALUE) String modifierString,
			@AConQATAttribute(name = "analyzer", description = "Define analyzer") IProgramElementDocAnalyzer analyzer) {
		Modifiers modifiers = createModifier(modifierString);
		methodAnalyzers.add(analyzer, modifiers);
		typeAnalyzers.add(new TypeAnalyzerDescriptor(analyzer,
				createModifier(modifierString), true));
		fieldAnalyzers.add(analyzer, modifiers);
	}

	/**
	 * This creates a modifier from a modifier description. If the parameter is
	 * {@link #MODIFIER_DEFAULT_VALUE} a modifiers object that matches
	 * everything is created.
	 */
	private Modifiers createModifier(String modifierString) {
		if (MODIFIER_DEFAULT_VALUE.equals(modifierString)) {
			modifierString = StringUtils.EMPTY_STRING;
		}
		return new Modifiers(modifierString);
	}

	/** {@inheritDoc} */
	@Override
	protected String[] getKeys() {
		return new String[] { KEY };
	}

	/** {@inheritDoc} */
	@Override
	protected void setUp(IJavaResource root) throws ConQATException {
		super.setUp(root);

		findingCategory = NodeUtils.getFindingReport(root).getOrCreateCategory(
				"JavaDoc Analysis");

		for (ICommentAnalyzer analyzer : methodAnalyzers.extractFirstList()) {
			analyzer.init(findingCategory);
		}
		for (TypeAnalyzerDescriptor descriptor : typeAnalyzers) {
			descriptor.analyzer.init(findingCategory);
		}
		for (ICommentAnalyzer analyzer : fieldAnalyzers.extractFirstList()) {
			analyzer.init(findingCategory);
		}
	}

	/** {@inheritDoc} */
	@Override
	protected void analyze(IJavaElement element, JavaClass clazz)
			throws ConQATException {
		ClassDoc classDoc = null;
		try {
			classDoc = javaLibrary.getDoc(element);
		} catch (ConQATException ex) {
			getLogger().warn(
					"Couldn't analyze Javadoc for class " + element.getId()
							+ ": " + ex.getMessage());
			return;
		}

		analyzeType(classDoc, element);
	}

	/** Analyzes type comments. */
	private void analyzeType(ClassDoc classDoc, IJavaElement element)
			throws ConQATException {
		for (TypeAnalyzerDescriptor descriptor : typeAnalyzers) {
			// check if this analyzer should be applied to inner classes
			if (classDoc.containingClass() != null
					&& !descriptor.analyzeInnerClasses) {
				continue;
			}

			Modifiers actualModifiers = new Modifiers(
					classDoc.modifierSpecifier(), true);
			Modifiers requiredModifiers = descriptor.modifiers;

			if (requiredModifiers.isSatisfied(actualModifiers)) {
				descriptor.analyzer.analyze(classDoc, element);
			}

		}

		analyzeMethods(classDoc, element);
		analyzeFields(classDoc, element);

		for (ClassDoc innerClassDoc : classDoc.innerClasses()) {
			analyzeType(innerClassDoc, element);
		}
	}

	/** Analyze method comments. */
	private void analyzeMethods(ClassDoc classDoc, IJavaElement element)
			throws ConQATException {
		for (MethodDoc methodDoc : classDoc.methods()) {
			if (!isEnumMethod(methodDoc)) {
				analyzeMethod(methodDoc, element);
			}
		}
	}

	/** Analyze method comment. */
	private void analyzeMethod(MethodDoc methodDoc, IJavaElement element)
			throws ConQATException {
		for (int i = 0; i < methodAnalyzers.size(); i++) {
			IMethodDocAnalyzer analyzer = methodAnalyzers.getFirst(i);

			Modifiers actualModifiers = new Modifiers(
					methodDoc.modifierSpecifier(), false);
			Modifiers requiredModifiers = methodAnalyzers.getSecond(i);
			if (requiredModifiers.isSatisfied(actualModifiers)) {
				analyzer.analyze(methodDoc, element);
			}
		}

	}

	/** Analyze fields. */
	private void analyzeFields(ClassDoc classDoc, IJavaElement element)
			throws ConQATException {
		for (FieldDoc fieldDoc : classDoc.fields()) {
			for (int i = 0; i < fieldAnalyzers.size(); i++) {
				IFieldDocAnalyzer analyzer = fieldAnalyzers.getFirst(i);

				Modifiers actualModifiers = new Modifiers(
						fieldDoc.modifierSpecifier(), false);
				Modifiers requiredModifiers = fieldAnalyzers.getSecond(i);
				if (requiredModifiers.isSatisfied(actualModifiers)) {
					analyzer.analyze(fieldDoc, element);
				}
			}
		}
	}

	/**
	 * Check if this method documentation concerns one of the methods
	 * <code>valueOf()</code> or <code>values()</code> that are present in enums
	 * by default and therefore need not to be documented.
	 */
	private boolean isEnumMethod(MethodDoc methodDoc) {
		if (!methodDoc.containingClass().isEnum()) {
			return false;
		}
		if ("values".equals(methodDoc.name())
				|| "valueOf".equals(methodDoc.name())) {
			return true;
		}
		return false;
	}

	/** Descriptor for type analyzers. */
	private static class TypeAnalyzerDescriptor {
		/** The analyzer. */
		private final ITypeDocAnalyzer analyzer;

		/** Modifiers of elements this analyzer should be applied for. */
		private final Modifiers modifiers;

		/** Flag that signals if this analyzer should be run on inner classes. */
		private final boolean analyzeInnerClasses;

		/** Constructor. */
		private TypeAnalyzerDescriptor(ITypeDocAnalyzer analyzer,
				Modifiers modifiers, boolean analyzeInnerClasses) {
			this.analyzer = analyzer;
			this.modifiers = modifiers;
			this.analyzeInnerClasses = analyzeInnerClasses;
		}

	}
}