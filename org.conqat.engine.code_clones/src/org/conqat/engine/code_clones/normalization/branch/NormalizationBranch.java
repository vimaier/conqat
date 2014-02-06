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
package org.conqat.engine.code_clones.normalization.branch;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;

import org.conqat.engine.code_clones.core.CloneDetectionException;
import org.conqat.engine.code_clones.core.Unit;
import org.conqat.engine.code_clones.normalization.UnitProviderBase;
import org.conqat.engine.code_clones.normalization.provider.IUnitProvider;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.text.ITextResource;
import org.conqat.engine.resource.util.ResourceTraversalUtils;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.resource.ITokenResource;
import org.conqat.lib.scanner.ELanguage;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: Moritz Marc Beller$
 * @version $Rev: 43295 $
 * @ConQAT.Rating GREEN Hash: 7536315DF1FDE12369A08532174B8C4B
 */
public class NormalizationBranch extends UnitProviderBase<ITextResource, Unit> {

	/** Serial version UID. */
	private static final long serialVersionUID = 1;

	/** The provider used if no language specific normalization is found. */
	private final IUnitProvider<ITextResource, Unit> defaultNormalization;

	/** Language specific normalizations. */
	private final Map<ELanguage, IUnitProvider<ITokenResource, Unit>> normalizationByLanguage = new EnumMap<ELanguage, IUnitProvider<ITokenResource, Unit>>(
			ELanguage.class);

	/** Iterator over all input elements. */
	private transient Iterator<ITextElement> elementIterator;

	/** Provider for the current element. */
	private transient IUnitProvider<?, Unit> currentUnitProvider;

	/** Constructor. */
	/* package */NormalizationBranch(
			IUnitProvider<ITextResource, Unit> defaultNormalization,
			Map<ELanguage, IUnitProvider<ITokenResource, Unit>> normalizationByLanguage) {
		this.defaultNormalization = defaultNormalization;
		this.normalizationByLanguage.putAll(normalizationByLanguage);
	}

	/** {@inheritDoc} */
	@Override
	protected void init(ITextResource root) {
		elementIterator = ResourceTraversalUtils.listTextElements(root)
				.iterator();
	}

	/** {@inheritDoc} */
	@Override
	protected Unit provideNext() throws CloneDetectionException {
		while (true) {
			if (currentUnitProvider != null) {
				Unit nextUnit = currentUnitProvider.getNext();
				if (nextUnit != null) {
					return nextUnit;
				}
			}

			if (elementIterator == null || !elementIterator.hasNext()) {
				return null;
			}
			currentUnitProvider = getCurrentUnitProvider(elementIterator.next());
		}
	}

	/** Returns a unit iterator for the given element. */
	private IUnitProvider<?, Unit> getCurrentUnitProvider(ITextElement element)
			throws CloneDetectionException {
		if (element instanceof ITokenElement) {
			ITokenElement tokenElement = (ITokenElement) element;
			IUnitProvider<ITokenResource, Unit> normalization = normalizationByLanguage
					.get(tokenElement.getLanguage());
			normalization.init(tokenElement, getLogger());
			return normalization;
		}

		defaultNormalization.init(element, getLogger());
		return defaultNormalization;
	}
}
