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
package org.conqat.engine.code_clones.normalization.token.configuration;

import java.util.EnumMap;
import java.util.Map;

/**
 * Enum constants for configuration of the normalization phase.
 * 
 * @author juergens
 * @author $Author: juergens $
 * @version $Rev: 34670 $
 * @ConQAT.Rating GREEN Hash: 46FFBE73DED0A2532492ED1B62D43C0C
 */
public enum ENormalizationOption {

	/** See documentation in {@link TokenConfigurationDef} */
	IGNORE_COMMENTS,

	/** See documentation in {@link TokenConfigurationDef} */
	IGNORE_DELIMITERS,

	/** See documentation in {@link TokenConfigurationDef} */
	IGNORE_PREPROCESSOR_DIRECTIVES,

	/** See documentation in {@link TokenConfigurationDef} */
	NORMALIZE_IDENTIFIERS,

	/** See documentation in {@link TokenConfigurationDef} */
	NORMALIZE_FULLY_QUALIFIED_TYPE_NAMES,

	/** See documentation in {@link TokenConfigurationDef} */
	NORMALIZE_TYPE_KEYWORDS,

	/** See documentation in {@link TokenConfigurationDef} */
	NORMALIZE_BOOLEAN_LITERALS,

	/** See documentation in {@link TokenConfigurationDef} */
	NORMALIZE_CHARACTER_LITERALS,

	/** See documentation in {@link TokenConfigurationDef} */
	NORMALIZE_NUMBER_LITERALS,

	/** See documentation in {@link TokenConfigurationDef} */
	NORMALIZE_STRING_LITERALS,

	/** See documentation in {@link TokenConfigurationDef} */
	IGNORE_THIS,

	/** See documentation in {@link TokenConfigurationDef} */
	IGNORE_VISIBILITY_MODIFIER,

	/** See documentation in {@link TokenConfigurationDef} */
	STEM_WORDS,

	/** See documentation in {@link TokenConfigurationDef} */
	IGNORE_STOP_WORDS,

	/** See documentation in {@link TokenConfigurationDef} */
	IGNORE_END_OF_STATEMENT_TOKENS;

	/** Creates a map with all normalization options set to true */
	public static Map<ENormalizationOption, Boolean> setAll() {
		Map<ENormalizationOption, Boolean> options = new EnumMap<ENormalizationOption, Boolean>(
				ENormalizationOption.class);
		for (ENormalizationOption option : ENormalizationOption.values()) {
			options.put(option, true);
		}
		return options;
	}

	/** Creates a map with the default normalization options set */
	public static Map<ENormalizationOption, Boolean> getDefaultOptions() {
		Map<ENormalizationOption, Boolean> defaultOptions = setAll();

		defaultOptions.put(IGNORE_END_OF_STATEMENT_TOKENS, false);
		defaultOptions.put(NORMALIZE_IDENTIFIERS, false);
		defaultOptions.put(NORMALIZE_FULLY_QUALIFIED_TYPE_NAMES, false);
		defaultOptions.put(STEM_WORDS, false);
		defaultOptions.put(IGNORE_STOP_WORDS, false);

		return defaultOptions;
	}

}