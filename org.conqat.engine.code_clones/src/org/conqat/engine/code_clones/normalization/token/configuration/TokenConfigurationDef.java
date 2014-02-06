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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.text.identifier.EStemmer;
import org.conqat.engine.text.identifier.EStopWords;
import org.conqat.lib.commons.clone.IDeepCloneable;

/**
 * Creates {@link ITokenConfiguration}s.
 * <p>
 * In order to avoid redundancy, this processor implements
 * {@link ITokenConfiguration} and simply returns a self reference in
 * {@link #process()};
 * 
 * @author juergens
 * @author $Author: hummelb $
 * @version $Rev: 36296 $
 * @ConQAT.Rating GREEN Hash: E803C9E4614208F8662BFBDB03426F10
 */
@AConQATProcessor(description = "Creates ITokenConfigurations.")
public class TokenConfigurationDef extends ConQATProcessorBase implements
		ITokenConfiguration, IDeepCloneable, Serializable {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	/** Configuration that overrides the settings in this configuration */
	private TokenConfigurationDef overridingOptions;

	/** Map with default option values */
	private final Map<ENormalizationOption, Boolean> defaultOptions = ENormalizationOption
			.getDefaultOptions();

	/** Normalization options explicitly set on this configuration */
	private final Map<ENormalizationOption, Boolean> localOptions = new HashMap<ENormalizationOption, Boolean>();

	/** Configuration name */
	private String name = "default";

	/** Stemmer used for stemming. */
	private EStemmer stemmer = null;

	/** Set of stop words */
	private EStopWords stopWords = null;

	/** Default constructor */
	public TokenConfigurationDef() {
		// nothing to do
	}

	/** Copy constructor */
	private TokenConfigurationDef(TokenConfigurationDef def) {
		localOptions.putAll(def.localOptions);
		name = def.getName();

		if (def.overridingOptions != null) {
			overridingOptions = new TokenConfigurationDef(def.overridingOptions);
		}

		stemmer = def.stemmer;
		stopWords = def.stopWords;
	}

	/** {@inheritDoc} */
	@Override
	public IDeepCloneable deepClone() {
		return new TokenConfigurationDef(this);
	}

	/**
	 * Turns on all normalization parameters except word stemming and stop
	 * words.
	 */
	public void setAll() {
		localOptions.clear();
		localOptions.putAll(ENormalizationOption.setAll());

		localOptions.put(ENormalizationOption.IGNORE_STOP_WORDS, false);
		localOptions.put(ENormalizationOption.STEM_WORDS, false);
		localOptions.put(ENormalizationOption.IGNORE_END_OF_STATEMENT_TOKENS,
				false);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "overriding", minOccurrences = 0, maxOccurrences = 1, description = "Token configuration whose settings override the settings in this configuration")
	public void setOverridingTokenConfiguration(
			@AConQATAttribute(name = "configuration", description = "Only the explicit settings are taken into account, default values are ignored.") TokenConfigurationDef overridingOptions) {
		this.overridingOptions = overridingOptions;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "configuration", description = "Name of the configuration", minOccurrences = 0, maxOccurrences = 1)
	public void setName(
			@AConQATAttribute(name = "name", description = "Default value: 'default'", defaultValue = "default") String name) {
		this.name = name;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "comments", description = "Ignore comments?", minOccurrences = 0, maxOccurrences = 1)
	public void setIgnoreComments(
			@AConQATAttribute(name = "ignore", description = "Default value: true", defaultValue = "true") boolean ignoreComments) {
		localOptions.put(ENormalizationOption.IGNORE_COMMENTS, ignoreComments);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "end-of-statement-tokens", description = "Ignore end of statement tokens?", minOccurrences = 0, maxOccurrences = 1)
	public void setIgnoreEndOfStatementTokens(
			@AConQATAttribute(name = "ignore", description = "Default value: false") boolean ignoreEndOfStatementTokens) {
		localOptions.put(ENormalizationOption.IGNORE_END_OF_STATEMENT_TOKENS,
				ignoreEndOfStatementTokens);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "delimiters", description = "Ignore delimiters?", minOccurrences = 0, maxOccurrences = 1)
	public void setIgnoreDelimiters(
			@AConQATAttribute(name = "ignore", description = "Default value: true") boolean ignoreDelimiters) {
		localOptions.put(ENormalizationOption.IGNORE_DELIMITERS,
				ignoreDelimiters);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "preprocessordirectives", description = "Ignore preprocessor directives?", minOccurrences = 0, maxOccurrences = 1)
	public void setIgnorePreprocessorDirectives(
			@AConQATAttribute(name = "ignore", description = "Default value: true") boolean ignorePreprocessorDirectives) {
		localOptions.put(ENormalizationOption.IGNORE_PREPROCESSOR_DIRECTIVES,
				ignorePreprocessorDirectives);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "identifiers", description = "Normalize identifiers?", minOccurrences = 0, maxOccurrences = 1)
	public void setNormalizeIdentifiers(
			@AConQATAttribute(name = "normalize", description = "Default value: false") boolean normalizeIdentifiers) {
		localOptions.put(ENormalizationOption.NORMALIZE_IDENTIFIERS,
				normalizeIdentifiers);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "fq-names", description = "Normalize fully qualified names?", minOccurrences = 0, maxOccurrences = 1)
	public void setNormalizeFullyQualifiedNames(
			@AConQATAttribute(name = "normalize", description = "Default value: false") boolean normalizeFullyQualifiedNames) {
		localOptions.put(
				ENormalizationOption.NORMALIZE_FULLY_QUALIFIED_TYPE_NAMES,
				normalizeFullyQualifiedNames);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "type-keywords", description = "Normalize type keywords?", minOccurrences = 0, maxOccurrences = 1)
	public void setNormalizeTypeKeywords(
			@AConQATAttribute(name = "normalize", description = "Default value: false") boolean normalizeTypeKeywords) {
		localOptions.put(ENormalizationOption.NORMALIZE_TYPE_KEYWORDS,
				normalizeTypeKeywords);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "booleanliterals", description = "Normalize boolean literals?", minOccurrences = 0, maxOccurrences = 1)
	public void setNormalizenBooleanLiterals(
			@AConQATAttribute(name = "normalize", description = "Default value: true") boolean normalizeBooleanLiterals) {
		localOptions.put(ENormalizationOption.NORMALIZE_BOOLEAN_LITERALS,
				normalizeBooleanLiterals);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "characterliterals", description = "Normalize character literals?", minOccurrences = 0, maxOccurrences = 1)
	public void setNormalizenCharacterLiterals(
			@AConQATAttribute(name = "normalize", description = "Default value: true") boolean normalizeCharacterLiterals) {
		localOptions.put(ENormalizationOption.NORMALIZE_CHARACTER_LITERALS,
				normalizeCharacterLiterals);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "numberliterals", description = "Normalize number literals?", minOccurrences = 0, maxOccurrences = 1)
	public void setNormalizeNumberLiterals(
			@AConQATAttribute(name = "normalize", description = "Default value: true") boolean normalizeNumberLiterals) {
		localOptions.put(ENormalizationOption.NORMALIZE_NUMBER_LITERALS,
				normalizeNumberLiterals);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "stringliterals", description = "Normalize string literals?", minOccurrences = 0, maxOccurrences = 1)
	public void setNormalizeStringLiterals(
			@AConQATAttribute(name = "normalize", description = "Default value: true") boolean normalizeStringLiterals) {
		localOptions.put(ENormalizationOption.NORMALIZE_STRING_LITERALS,
				normalizeStringLiterals);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "this", description = "Ignore this reference?", minOccurrences = 0, maxOccurrences = 1)
	public void setIgnoreThis(
			@AConQATAttribute(name = "ignore", description = "Default value: true") boolean ignoreThis) {
		localOptions.put(ENormalizationOption.IGNORE_THIS, ignoreThis);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "visibility-modifier", description = "Ignore visibility modifier?", minOccurrences = 0, maxOccurrences = 1)
	public void setIgnoreVisibilityModifier(
			@AConQATAttribute(name = "ignore", description = "Default value: false") boolean ignoreVisibilityModifier) {
		localOptions.put(ENormalizationOption.IGNORE_VISIBILITY_MODIFIER,
				ignoreVisibilityModifier);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "words", description = "Perform word stemming?", minOccurrences = 0, maxOccurrences = 1)
	public void setStemWords(
			@AConQATAttribute(name = "stem", description = "Default value: false") boolean stemWords,
			@AConQATAttribute(name = "stemmer", description = "Language in which document is written. (Required to choose appropriate stemmer).") EStemmer stemmer) {
		localOptions.put(ENormalizationOption.STEM_WORDS, stemWords);
		this.stemmer = stemmer;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "stop-words", description = "Remove stop words?", minOccurrences = 0, maxOccurrences = 1)
	public void setIgnoreStopWords(
			@AConQATAttribute(name = "ignore", description = "Default value: false") boolean ignoreStopWords,
			@AConQATAttribute(name = "set", description = "Language in which document is written. (Required to choose appropriate stop word set).") EStopWords stopWords) {
		localOptions.put(ENormalizationOption.IGNORE_STOP_WORDS,
				ignoreStopWords);
		this.stopWords = stopWords;
	}

	/** Returns an TokenConfigurationDef according to the processor parameters */
	@Override
	public TokenConfigurationDef process() {
		// override local settings with overriding settings
		if (overridingOptions != null) {
			localOptions.putAll(overridingOptions.localOptions);
			if (overridingOptions.localOptions
					.containsKey(ENormalizationOption.STEM_WORDS)) {
				stemmer = overridingOptions.stemmer;
			}
			if (overridingOptions.localOptions
					.containsKey(ENormalizationOption.IGNORE_STOP_WORDS)) {
				stopWords = overridingOptions.stopWords;
			}
		}

		// return configuration
		return this;
	}

	/** Returns configuration name */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * Retrieve option value. Value lookup is first performed in the explicitly
	 * set values in this configuration and then in the default normalization
	 * options.
	 */
	private boolean getNormalizationOption(ENormalizationOption option) {
		// explicitly stored values
		if (localOptions.containsKey(option)) {
			return localOptions.get(option);
		}

		// default values
		return defaultOptions.get(option);
	}

	/** See comment for corresponding setter. */
	@Override
	public boolean isIgnoreComments() {
		return getNormalizationOption(ENormalizationOption.IGNORE_COMMENTS);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isIgnoreEndOfStatementTokens() {
		return getNormalizationOption(ENormalizationOption.IGNORE_END_OF_STATEMENT_TOKENS);
	}

	/** See comment for corresponding setter. */
	@Override
	public boolean isIgnoreDelimiters() {
		return getNormalizationOption(ENormalizationOption.IGNORE_DELIMITERS);
	}

	/** See comment for corresponding setter. */
	@Override
	public boolean isIgnorePreprocessorDirectives() {
		return getNormalizationOption(ENormalizationOption.IGNORE_PREPROCESSOR_DIRECTIVES);
	}

	/** See comment for corresponding setter. */
	@Override
	public boolean isNormalizeBooleanLiterals() {
		return getNormalizationOption(ENormalizationOption.NORMALIZE_BOOLEAN_LITERALS);
	}

	/** See comment for corresponding setter. */
	@Override
	public boolean isNormalizeCharacterLiterals() {
		return getNormalizationOption(ENormalizationOption.NORMALIZE_CHARACTER_LITERALS);
	}

	/** See comment for corresponding setter. */
	@Override
	public boolean isNormalizeIdentifiers() {
		return getNormalizationOption(ENormalizationOption.NORMALIZE_IDENTIFIERS);
	}

	/** See comment for corresponding setter. */
	@Override
	public boolean isNormalizeFullyQualifiedNames() {
		return getNormalizationOption(ENormalizationOption.NORMALIZE_FULLY_QUALIFIED_TYPE_NAMES);
	}

	/** See comment for corresponding setter. */
	@Override
	public boolean isNormalizeTypeKeywords() {
		return getNormalizationOption(ENormalizationOption.NORMALIZE_TYPE_KEYWORDS);
	}

	/** See comment for corresponding setter. */
	@Override
	public boolean isNormalizeNumberLiterals() {
		return getNormalizationOption(ENormalizationOption.NORMALIZE_NUMBER_LITERALS);
	}

	/** See comment for corresponding setter. */
	@Override
	public boolean isNormalizeStringLiterals() {
		return getNormalizationOption(ENormalizationOption.NORMALIZE_STRING_LITERALS);
	}

	/** See comment for corresponding setter. */
	@Override
	public boolean isIgnoreThis() {
		return getNormalizationOption(ENormalizationOption.IGNORE_THIS);
	}

	/** See comment for corresponding setter. */
	@Override
	public boolean isIgnoreModifier() {
		return getNormalizationOption(ENormalizationOption.IGNORE_VISIBILITY_MODIFIER);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isStemWords() {
		return getNormalizationOption(ENormalizationOption.STEM_WORDS);
	}

	/** {@inheritDoc} */
	@Override
	public EStemmer getStemmer() {
		return stemmer;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isIgnoreStopWords() {
		return getNormalizationOption(ENormalizationOption.IGNORE_STOP_WORDS);
	}

	/** {@inheritDoc} */
	@Override
	public EStopWords getStopWords() {
		return stopWords;
	}

}