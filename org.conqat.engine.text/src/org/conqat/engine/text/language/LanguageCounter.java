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
package org.conqat.engine.text.language;

import java.util.EnumSet;
import java.util.List;


import org.conqat.lib.commons.collections.CounterSet;
import org.conqat.engine.commons.pattern.PatternList;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.sourcecode.analysis.TokenAnalyzerBase;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.lib.scanner.IToken;
import org.conqat.lib.scanner.ETokenType.ETokenClass;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: juergens $
 * @version $Rev: 35207 $
 * @ConQAT.Rating GREEN Hash: 212AF63E11098B98E28C579840AB9E29
 */
@AConQATProcessor(description = "This processor counts how often which language is used in selected token types.")
public class LanguageCounter extends TokenAnalyzerBase {

	/** Key for languages. */
	@AConQATKey(description = "Distribution of languages.", type = "org.conqat.lib.commons.collections.CounterSet<String>")
	public static final String KEY_LANG = "Lang";

	/** Set of token classes to include in analysis. */
	private final EnumSet<ETokenClass> tokenClasses = EnumSet
			.noneOf(ETokenClass.class);

	/** List of tokens to be ignored. */
	private PatternList ignorePattern;

	/** Add token class. */
	@AConQATParameter(name = "token", minOccurrences = 1, description = "Add a token class.")
	public void addTokenClass(
			@AConQATAttribute(name = "class", description = "Specifies "
					+ "token class.") ETokenClass tokenClass) {
		tokenClasses.add(tokenClass);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "ignore", minOccurrences = 0, maxOccurrences = 1, description = "Set list of tokens to ignore.")
	public void setIgnorePattern(
			@AConQATAttribute(name = "list", description = "List of pattern which describes ignored tokens.") PatternList list) {
		ignorePattern = list;
	}

	/** {@inheritDoc} */
	@Override
	protected void analyzeTokens(List<IToken> tokens, ITokenElement element) {
		CounterSet<String> languages = new CounterSet<String>();
		for (IToken token : tokens) {
			if (tokenClasses.contains(token.getType().getTokenClass())) {
				if (ignorePattern == null
						|| !ignorePattern.matchesAny(token.getText())) {
					languages.inc(LanguageDecider.decideLanguage(token
							.getText()));
				}
			}
		}
		element.setValue(KEY_LANG, languages);
	}

	/** {@inheritDoc} */
	@Override
	protected String[] getKeys() {
		return new String[]{ KEY_LANG };
	}

}