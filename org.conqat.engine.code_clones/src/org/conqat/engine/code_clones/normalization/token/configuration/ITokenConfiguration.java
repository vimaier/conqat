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

import org.conqat.engine.text.identifier.EStemmer;
import org.conqat.engine.text.identifier.EStopWords;

/**
 * Configuration for token-based normalization.
 * <p>
 * Determines, how normalization is performed.
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: C25EAD6367BE7BB4AD01E9B9953DEFF2
 */
public interface ITokenConfiguration {

	/** Get name of this token configuration. */
	public String getName();

	/** Ignore end of statement tokens? */
	public boolean isIgnoreEndOfStatementTokens();

	/** Ignore delimiters? */
	public boolean isIgnoreDelimiters();

	/** Ignore comments? */
	public boolean isIgnoreComments();

	/** Ignore pre-processor directives? */
	public boolean isIgnorePreprocessorDirectives();

	/** Normalize identifiers? */
	public boolean isNormalizeIdentifiers();

	/** Normalize fully qualified names? */
	public boolean isNormalizeFullyQualifiedNames();

	/** Normalize type keywords? */
	public boolean isNormalizeTypeKeywords();

	/** Normalize string literals? */
	public boolean isNormalizeStringLiterals();

	/** Normalize char literals? */
	public boolean isNormalizeCharacterLiterals();

	/** Normalize number literals? */
	public boolean isNormalizeNumberLiterals();

	/** Normalize boolean literals? */
	public boolean isNormalizeBooleanLiterals();

	/** Ignore this reference? */
	public boolean isIgnoreThis();

	/** Ignore visibility modifier? */
	public boolean isIgnoreModifier();

	/** Perform word stemming? */
	public boolean isStemWords();

	/**
	 * Retrieves stemmer. If no stemmer is set, <code>null</code> is returned.
	 * Must not return <code>null</code>, if stemming is enabled.
	 */
	public EStemmer getStemmer();

	/** Ignore stop words? */
	public boolean isIgnoreStopWords();

	/**
	 * Retrieves the set of stop words. If stop words are not ignored, null is
	 * returned. Must not return <code>null</code>, if stop word elimination is
	 * enabled.
	 */
	public EStopWords getStopWords();

}