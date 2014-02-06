/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2011 the ConQAT Project                                   |
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
package org.conqat.engine.resource.diff;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.pattern.PatternTransformationList;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.base.ElementTraversingProcessorBase;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.text.ITextResource;
import org.conqat.engine.resource.util.ResourceTraversalUtils;
import org.conqat.engine.resource.util.TransformedUniformPathToElementMap;

/**
 * Base class for processors that compare two scopes.
 * <p>
 * The term <i>main</i> refers to the primary resource tree (typically the
 * versioning system's trunk), while <i>comparee</i> is the version we compare
 * to (typically some older snapshot).
 * 
 * @author $Author: pfaller $
 * @version $Rev: 45943 $
 * @ConQAT.Rating YELLOW Hash: 9116D694CB4781C2280F63D8610BF912
 */
public abstract class ScopeDifferBase extends
		ElementTraversingProcessorBase<ITextResource, ITextElement> {

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "comparee", attribute = ConQATParamDoc.INPUT_REF_NAME, description = "Scope to compare to (the 'older' one)")
	public ITextResource compareeRoot;

	/** This maps from transformed uniform path to element. */
	private TransformedUniformPathToElementMap<ITextElement> compareeMap;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = ConQATParamDoc.PATH_TRANSFORMATION_PARAM, attribute = ConQATParamDoc.PATH_TRANSFORMATION_ATTRIBUTE, optional = true, description = ConQATParamDoc.PATH_TRANSFORMATION_DESCRIPTION
			+ " The path replacement can be stated as pattern if prefixed with <i>?REGEX?</i>. Then the comparee element is mapped to the head element which matches that pattern (without the prefix). ")
	public PatternTransformationList transformations = null;

	/** {@inheritDoc} */
	@SuppressWarnings("unused")
	@Override
	protected void setUp(ITextResource root) throws ConQATException {
		compareeMap = ResourceTraversalUtils
				.createTransformedUniformPathToElementMap(compareeRoot,
						ITextElement.class, transformations, getLogger());
	}

	/** {@inheritDoc} */
	@Override
	protected Class<ITextElement> getElementClass() {
		return ITextElement.class;
	}

	/** {@inheritDoc} */
	@Override
	protected void processElement(ITextElement element) throws ConQATException {
		determineDiff(element,
				compareeMap.removeElement(element.getUniformPath()));
	}

	/**
	 * Determine the diff for two elements and possible annotate the main
	 * element. Note that the uniform paths of both elements are typically not
	 * the same due to mapping rules.
	 * 
	 * @param compareeElement
	 *            the comparee (may be null if no comparee found).
	 */
	protected abstract void determineDiff(ITextElement mainElement,
			ITextElement compareeElement) throws ConQATException;

	/** {@inheritDoc} */
	@Override
	protected void finish(ITextResource root) throws ConQATException {
		// as we remove matched elements, in the end the map only contains
		// unmatched elements
		for (ITextElement element : compareeMap.elements()) {
			processUnmatchedCompareeElement(element);
		}
	}

	/**
	 * Template method for processing elements that are only found in the
	 * comparee but not in the main resources. Default implementation is empty.
	 */
	@SuppressWarnings("unused")
	protected void processUnmatchedCompareeElement(ITextElement value)
			throws ConQATException {
		// empty default implementation
	}
}
