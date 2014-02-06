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
package org.conqat.engine.sourcecode.analysis;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.pattern.PatternList;
import org.conqat.engine.commons.util.ConQATInputProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.util.ResourceTraversalUtils;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.resource.ITokenResource;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.CounterSet;
import org.conqat.lib.commons.collections.UnmodifiableList;
import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.ETokenType.ETokenClass;
import org.conqat.lib.scanner.IToken;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: deissenb $
 * @version $Rev: 41838 $
 * @ConQAT.Rating GREEN Hash: 790EDC09AD2B0D48AEDC533F523403B5
 */
@AConQATProcessor(description = "This processor counts the number of "
		+ "occurences of token values (their textual appearance) within the entire "
		+ "element tree. You may specify individual token types or token classes "
		+ "to select which tokens are considered in the analysis. Remember to "
		+ "specify at least one token type or token class. In addition, a cutoff "
		+ "can be specified to determine how many of each token value's characters "
		+ "are considered in the analysis. This can be used to shorten overly long "
		+ "values which sometimes occur in long comments (complete files commented "
		+ "out, inline revision histories,...). The cutoff can be used to ease "
		+ "the subsequent presentation of the results.")
public class TokenValueCounter extends ConQATInputProcessorBase<ITokenResource> {

	/** Types of tokens whose values are included in the analysis. */
	private Set<ETokenType> includeTokenTypes = EnumSet
			.noneOf(ETokenType.class);

	/** Types of tokens whose values are excluded from the analysis. */
	private Set<ETokenType> excludeTokenTypes = EnumSet
			.noneOf(ETokenType.class);

	/**
	 * The maximum length of the token's value that is considered. If this value
	 * is <= 0 the complete value of each token is considered.
	 */
	private int cutoff = 0;

	/** Exclude patterns. */
	private PatternList excludePatterns = new PatternList();

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "include-token-type", description = "Include a single token type. The values of all tokens with the "
			+ "given type will be counted.")
	public void addTokenType(
			@AConQATAttribute(name = "type", description = "Count tokens with the given type.") ETokenType tokenType) {
		includeTokenTypes.add(tokenType);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "include-token-class", description = "Include all token types contained in the given class. The "
			+ "values of all tokens whose type belongs to the given class will "
			+ "be counted.")
	public void addTokenClass(
			@AConQATAttribute(name = "class", description = "Count tokens whose type belongs to the given class.") ETokenClass tokenClass) {
		includeTokenTypes.addAll(ETokenType.getTokenTypesByClass(tokenClass));
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "exclude-token-type", description = "Exclude a single token type. The values of all tokens with the "
			+ "given type will not be counted. This parameter can be used to "
			+ "exclude individual token types after including a particular "
			+ "token class.")
	public void removeTokenType(
			@AConQATAttribute(name = "type", description = "Do not count tokens with the given type.") ETokenType tokenType) {
		excludeTokenTypes.add(tokenType);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = ConQATParamDoc.EXCLUDE_NAME, maxOccurrences = 1, description = "Patterns for token values to be excluded. Only "
			+ "tokens whose value matches the entire pattern are excluded. "
			+ "This decision is made before a potential cutoff is applied to "
			+ "the token's value.")
	public void setExcludePattern(
			@AConQATAttribute(name = ConQATParamDoc.PATTERN_LIST, description = ConQATParamDoc.PATTERN_LIST_DESC) PatternList excludePatterns) {
		this.excludePatterns = excludePatterns;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "cutoff", description = "Defines how many characters of a token's value are considered.", minOccurrences = 0, maxOccurrences = 1)
	public void setMaxLength(
			@AConQATAttribute(name = "length", description = "Defines how many characters of a token's value are considered.") int cutoff) {
		this.cutoff = cutoff;
	}

	/** {@inheritDoc} */
	@Override
	public CounterSet<String> process() throws ConQATException {
		checkParameters();

		// Maps the token's values to the number of occurrences.
		CounterSet<String> counter = new CounterSet<String>();

		List<ITokenElement> elements = ResourceTraversalUtils.listElements(
				input, ITokenElement.class);

		for (ITokenElement element : elements) {
			UnmodifiableList<IToken> tokens = element.getTokens(getLogger());
			for (IToken token : tokens) {

				if (!includeTokenTypes.contains(token.getType())
						|| excludeTokenTypes.contains(token.getType())) {
					continue;
				}

				if (excludePatterns.matchesAny(token.getText())) {
					continue;
				}

				if (cutoff > 0) {
					// If a cutoff has been explicitly set, consider only the
					// substring up to the cutoff index for comparison.
					counter.inc(token.getText().substring(0,
							Math.min(token.getText().length(), cutoff)));
				} else {
					// Otherwise, consider the token's original value.
					counter.inc(token.getText());
				}
			}
		}

		return counter;
	}

	/**
	 * Checks whether the parameter set is useful, e.g., that at least one token
	 * type is included.
	 */
	@SuppressWarnings("unchecked")
	private void checkParameters() throws ConQATException {
		if (includeTokenTypes.isEmpty()) {
			throw new ConQATException("No token types included.");
		}

		if (CollectionUtils.intersectionSet(includeTokenTypes,
				excludeTokenTypes).size() == includeTokenTypes.size()) {
			throw new ConQATException(
					"All included token types are also excluded.");
		}
	}

}