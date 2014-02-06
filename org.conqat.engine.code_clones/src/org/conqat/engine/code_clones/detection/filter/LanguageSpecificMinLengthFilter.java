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
package org.conqat.engine.code_clones.detection.filter;

import java.util.EnumMap;
import java.util.Map;

import org.conqat.engine.code_clones.core.Clone;
import org.conqat.engine.code_clones.core.CloneClass;
import org.conqat.engine.code_clones.detection.CloneDetectionResultElement;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.util.ResourceTraversalUtils;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.scanner.ELanguage;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 44487 $
 * @ConQAT.Rating GREEN Hash: 5491887D1C6B2BFBA32DADC782AE3E3C
 */
@AConQATProcessor(description = "Filters clone classes based on the lengths "
		+ "of their clones. The minimum length can be specified for each "
		+ "language separately. If a clone class contains any clone whose "
		+ "length is below the minimum length specified for the language of "
		+ "its containing element, the whole clone class is removed. If there "
		+ "is no minimum length specified for a given language, it is "
		+ "assumed to be 0.")
public class LanguageSpecificMinLengthFilter extends CloneClassFilterBase {

	/** The minimum length for specific languages. */
	private final Map<ELanguage, Integer> minimumLengthByLanguage = new EnumMap<ELanguage, Integer>(
			ELanguage.class);

	/** Maps from uniform paths to the elements that contain the clones. */
	private Map<String, ITokenElement> elements;

	/** {@AConQAT.Doc} */
	@AConQATParameter(name = "min-length", description = "Specify the minimum "
			+ "length for clones in elements of a given language.")
	public void addMinimumLength(
			@AConQATAttribute(name = "language", description = "Language for which the minimum length is used.") ELanguage language,
			@AConQATAttribute(name = "value", description = "The actual minimum length as integer value.") int length)
			throws ConQATException {
		if (minimumLengthByLanguage.containsKey(language)) {
			throw new ConQATException("The minimum length for language "
					+ language.name() + " has been specified more than once!");
		}
		minimumLengthByLanguage.put(language, length);
	}

	/** {@inheritDoc} */
	@Override
	protected void setUp(CloneDetectionResultElement input) {
		elements = ResourceTraversalUtils.createUniformPathToElementMap(
				input.getRoot(), ITokenElement.class);
	}

	/** {@inheritDoc} */
	@Override
	protected boolean filteredOut(CloneClass cloneClass) {
		for (Clone clone : cloneClass.getClones()) {
			if (isTooShort(clone)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if the given clone is too short. The clone is too short if an
	 * explicit minimum length has been set for the language of the element that
	 * contains the clone and the clone's length is below that length.
	 */
	private boolean isTooShort(Clone clone) {
		Integer minLength = minimumLengthByLanguage.get(getLanguage(clone));
		return minLength != null && clone.getLengthInUnits() < minLength;
	}

	/** Retrieves the language of the element that contains the given clone. */
	private ELanguage getLanguage(Clone clone) {
		ITokenElement element = elements.get(clone.getUniformPath());
		CCSMAssert.isNotNull(element,
				"Cannot find the element " + clone.getUniformPath()
						+ " that contains the clone.");
		return element.getLanguage();
	}
}
