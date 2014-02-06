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
package org.conqat.engine.sourcecode.pattern;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.conqat.engine.commons.findings.Finding;
import org.conqat.engine.commons.findings.FindingCategory;
import org.conqat.engine.commons.findings.FindingGroup;
import org.conqat.engine.commons.findings.FindingReport;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.util.ResourceUtils;
import org.conqat.engine.sourcecode.analysis.TokenAnalyzerBase;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.resource.ITokenResource;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.IToken;

/**
 * {@ConQAT.Doc}
 * 
 * @author herrmama
 * @author $Author: hummelb $
 * @version $Rev: 43290 $
 * @ConQAT.Rating GREEN Hash: B04E0AF876BEB4EE63BF835EE6C71342
 */
@AConQATProcessor(description = "A processor that searches source code files by "
		+ "means of patterns on token sequences.")
public class TokenTypePatternFinder extends TokenAnalyzerBase {

	/** Default name of the category to which the findings should be attached. */
	private static final String DEFAULT_CATEGORY_NAME = "Search for Token Type Patterns";

	/** Finding report */
	private FindingReport report;

	/** Key to store the resulting findings */
	private String key;

	/** List of patterns on token sequences */
	private final Map<TokenTypePattern, String> patterns = new IdentityHashMap<TokenTypePattern, String>();

	/** Name of the category to which the findings should be attached */
	private String categoryName = DEFAULT_CATEGORY_NAME;

	/** The token types that are ignored */
	private final Set<ETokenType> ignoredTokenTypes = new HashSet<ETokenType>();

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "search", minOccurrences = 1, maxOccurrences = -1, description = "Adds a pattern.")
	public void addPattern(
			@AConQATAttribute(name = "pattern", description = "The pattern to be matched.") String pattern,
			@AConQATAttribute(name = "group", description = "The findings group to which matches of this pattern should be associated.") String group)
			throws ConQATException {
		TokenTypePattern tokenTypePattern = new TokenTypePattern(pattern);
		patterns.put(tokenTypePattern, group);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "result", minOccurrences = 1, maxOccurrences = 1, description = "Sets the key.")
	public void setKey(
			@AConQATAttribute(name = "key", description = "The key under which the results should be stored.") String key) {
		this.key = key;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "category", minOccurrences = 0, maxOccurrences = 1, description = "Sets the category.")
	public void setCategory(
			@AConQATAttribute(name = "category", description = "The name of the category to which the findings should be attached.") String name) {
		categoryName = name;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "ignore", minOccurrences = 0, maxOccurrences = -1, description = "Ignore a token type.")
	public void addIgnoredTokenType(
			@AConQATAttribute(name = "tokenType", description = "The token type that should be ignored.") ETokenType tokenType) {
		ignoredTokenTypes.add(tokenType);
	}

	/** {@inheritDoc} */
	@Override
	protected void setUp(ITokenResource root) throws ConQATException {
		super.setUp(root);
		report = NodeUtils.getFindingReport(root);
	}

	/** {@inheritDoc} */
	@Override
	protected void analyzeTokens(List<IToken> tokens, ITokenElement element)
			throws ConQATException {
		tokens = filter(tokens);
		for (Entry<TokenTypePattern, String> entry : patterns.entrySet()) {
			TokenTypePattern pattern = entry.getKey();
			String groupDescription = entry.getValue();
			EnumPatternMatcher matcher = pattern.matcher(tokens);
			while (matcher.find()) {
				createAndAttachFinding(pattern, groupDescription, element,
						tokens.subList(matcher.start(), matcher.end()));
			}
		}
	}

	/**
	 * Filter out the ignored token types. An in-place update of the token list
	 * is avoided, since the list may be cached internally by ConQAT.
	 */
	private List<IToken> filter(List<IToken> tokens) {
		if (ignoredTokenTypes.isEmpty()) {
			return tokens;
		}
		List<IToken> filteredTokens = new ArrayList<IToken>();
		for (IToken token : tokens) {
			if (!ignoredTokenTypes.contains(token.getType())) {
				filteredTokens.add(token);
			}
		}
		return filteredTokens;
	}

	/** Create finding based on a token sequence. */
	private Finding createAndAttachFinding(TokenTypePattern pattern,
			String groupDescription, ITokenElement element, List<IToken> tokens)
			throws ConQATException {

		FindingCategory category = report.getOrCreateCategory(categoryName);
		FindingGroup group = category.getOrCreateFindingGroup(groupDescription);

		return createAndAttachFinding(group,
				"Pattern \"" + pattern.getExpression() + "\" found", element,
				tokens, key);
	}

	/** Create finding based on its constituents */
	private Finding createAndAttachFinding(FindingGroup group, String message,
			ITokenElement element, List<IToken> tokens, String key)
			throws ConQATException {

		int startOffset = tokens.get(0).getOffset();
		int endOffset = CollectionUtils.getLast(tokens).getEndOffset();
		return ResourceUtils.createAndAttachFindingForFilteredRegion(group,
				message, element, startOffset, endOffset, key);
	}

	/** {@inheritDoc} */
	@Override
	protected String[] getKeys() {
		return new String[] { key };
	}

}