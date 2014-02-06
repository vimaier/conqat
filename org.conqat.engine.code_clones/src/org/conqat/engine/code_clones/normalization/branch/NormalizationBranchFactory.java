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
import java.util.Map;

import org.conqat.engine.code_clones.core.Unit;
import org.conqat.engine.code_clones.normalization.provider.IUnitProvider;
import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.text.ITextResource;
import org.conqat.engine.sourcecode.resource.ITokenResource;
import org.conqat.lib.scanner.ELanguage;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: goede $
 * @version $Rev: 43295 $
 * @ConQAT.Rating GREEN Hash: 8BF16D5E64192040C9712AF8D39BC7F0
 */
@AConQATProcessor(description = "Allows the creation of a normalization that performs language specific branching.")
public class NormalizationBranchFactory extends ConQATProcessorBase {

	/**
	 * Language specific normalization. This uses a unit provider for token
	 * resources, as we know for language specific branches that we have token
	 * elements.
	 */
	private Map<ELanguage, IUnitProvider<ITokenResource, Unit>> normalizationByLanguage = new EnumMap<ELanguage, IUnitProvider<ITokenResource, Unit>>(
			ELanguage.class);

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "default", attribute = "normalization", description = "The default normalization used for unknown languages and text elements.")
	public IUnitProvider<ITextResource, Unit> defaultNormalization;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "branch", description = "Adds a language specific normalization branch.")
	public void addLanguageNormalization(
			@AConQATAttribute(name = "language", description = "The language to branch off.") ELanguage language,
			@AConQATAttribute(name = "normalization", description = "The normalization to use for the language.") IUnitProvider<ITokenResource, Unit> normalization)
			throws ConQATException {
		if (normalizationByLanguage.put(language, normalization) != null) {
			throw new ConQATException("Multiple normalizations for language "
					+ language);
		}
	}

	/** {@inheritDoc} */
	@Override
	public IUnitProvider<ITextResource, Unit> process() {
		return new NormalizationBranch(defaultNormalization,
				normalizationByLanguage);
	}

}
