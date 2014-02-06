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
package org.conqat.engine.sourcecode.resource;

import java.util.regex.Pattern;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.IContentAccessor;
import org.conqat.engine.resource.text.TextElementFactory;
import org.conqat.engine.resource.util.ConQATDirectoryScanner;
import org.conqat.engine.resource.util.UniformPathUtils;
import org.conqat.lib.commons.collections.PairList;
import org.conqat.lib.scanner.ELanguage;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: juergens $
 * @version $Rev: 44686 $
 * @ConQAT.Rating GREEN Hash: 9709723FC4C51719A5D635E91880AB0A
 */
@AConQATProcessor(description = "Factory for token elements that automatically recognizes the language based on the file extensions. "
		+ "Additionally, an explicit pattern can be used to override the extension to language binding.")
public class AutoTokenElementFactory extends TextElementFactory {

	/** Ordered mapping from filename pattern to language. */
	private final PairList<Pattern, ELanguage> languageMap = new PairList<Pattern, ELanguage>();

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "map", description = "Allows to map a file name pattern (such as extensions) to specific languages.")
	public void addLanguageMapping(
			@AConQATAttribute(name = ConQATParamDoc.ANT_PATTERN_NAME, description = ConQATParamDoc.ANT_PATTERN_DESC) String pattern,
			@AConQATAttribute(name = "language", description = "The language to be used") ELanguage language)
			throws ConQATException {
		languageMap.add(ConQATDirectoryScanner.convertPattern(pattern, false),
				language);
	}

	/** {@inheritDoc} */
	@Override
	public ITokenElement create(IContentAccessor accessor) {
		ELanguage language = determineLanguage(accessor.getUniformPath());
		return new TokenElement(accessor, encoding, language, getFilters());
	}

	/**
	 * Determines the language to be used. Fallback solution is
	 * {@link ELanguage#LINE}.
	 */
	private ELanguage determineLanguage(String uniformPath) {

		for (int i = 0; i < languageMap.size(); ++i) {
			if (languageMap.getFirst(i).matcher(uniformPath).matches()) {
				return languageMap.getSecond(i);
			}
		}

		String extension = UniformPathUtils.getExtension(uniformPath);
		if (extension != null) {
			ELanguage language = ELanguage.fromFileExtension(extension);
			if (language != null) {
				return language;
			}
		}
		return ELanguage.LINE;
	}
}