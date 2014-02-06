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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.findings.FindingCategory;
import org.conqat.engine.commons.findings.FindingGroup;
import org.conqat.engine.commons.findings.util.FindingUtils;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.pattern.PatternList;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.analysis.ElementAnalyzerBase;
import org.conqat.engine.resource.util.ResourceUtils;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.resource.ITokenResource;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.ETokenType.ETokenClass;
import org.conqat.lib.scanner.IToken;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43290 $
 * @ConQAT.Rating GREEN Hash: 550424BE5FB78626BE38D5E6109A1EC5
 */
@AConQATProcessor(description = "This analyzer scans source code token by token for "
		+ "specified search patterns. Each match is reported with a ConQAT finding. The "
		+ "processor allows to specify wich token classes/token types should be included in "
		+ "the search.")
public class SourceCodeSearchFindingAnalyzer extends
		ElementAnalyzerBase<ITokenResource, ITokenElement> {

	/** The key */
	@AConQATKey(description = "Source Code Search Findings", type = "java.util.List<org.conqat.engine.commons.findings.Finding>")
	public static final String KEY = "SearchFindings";

	/** The group used. */
	private FindingGroup group;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "search", attribute = "patterns", description = ""
			+ "The list of pattern to be searched.")
	public PatternList patternList;

	/** Set of token classes to include in analysis. */
	private final EnumSet<ETokenClass> tokenClasses = EnumSet
			.noneOf(ETokenClass.class);

	/** Set of token types to include in analysis. */
	private final EnumSet<ETokenType> tokenTypes = EnumSet
			.noneOf(ETokenType.class);

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "finding-category", attribute = "name", optional = true, description = ""
			+ "Set finding category name. If not set, a defeault value will be used.")
	public String findingCategoryName = "Source Code Search";

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "finding-group", attribute = "name", optional = true, description = ""
			+ "Set finding group name. If not set, a group name will be constructed from the pattern.")
	public String findingGroupName = null;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = ConQATParamDoc.WRITEKEY_NAME, attribute = ConQATParamDoc.WRITEKEY_KEY_NAME, optional = true, description = ""
			+ "Set key to write findings into. Default is " + KEY + ".")
	public String writeKey = KEY;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "token-class", description = "Add a token class.")
	public void addTokenClass(
			@AConQATAttribute(name = "value", description = "Specifies "
					+ "token class.") ETokenClass tokenClass) {
		tokenClasses.add(tokenClass);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "token-type", description = "Add a token type.")
	public void addTokenType(
			@AConQATAttribute(name = "value", description = "Specifies "
					+ "token class.") ETokenType tokenType) {
		tokenTypes.add(tokenType);
	}

	/** {@inheritDoc} */
	@Override
	protected void setUp(ITokenResource root) throws ConQATException {
		super.setUp(root);
		if (tokenClasses.isEmpty() && tokenTypes.isEmpty()) {
			throw new ConQATException("No token types or classes defined.");
		}
		FindingCategory category = NodeUtils.getFindingReport(root)
				.getOrCreateCategory(findingCategoryName);
		if (findingGroupName == null) {
			findingGroupName = "Source Code Search for "
					+ StringUtils.concat(patternList, " , ");
		}

		group = FindingUtils.getOrCreateFindingGroupAndSetRuleId(category,
				findingGroupName, findingGroupName);
	}

	/** {@inheritDoc} */
	@Override
	protected void analyzeElement(ITokenElement element) {
		try {
			List<IToken> tokens = element.getTokens(getLogger());
			for (IToken token : tokens) {
				if (tokenClasses.contains(token.getType().getTokenClass())
						|| tokenTypes.contains(token.getType())) {
					analyzeToken(token, element);
				}
			}
		} catch (ConQATException ex) {
			getLogger().warn(
					"Could not analyze element: " + element + ": "
							+ ex.getMessage());
		}
	}

	/**
	 * Search a single token for the specified patterns and create findings for
	 * each match.
	 */
	private void analyzeToken(IToken token, ITokenElement element)
			throws ConQATException {
		for (Pattern pattern : patternList) {
			Matcher matcher = pattern.matcher(token.getText());
			while (matcher.find()) {
				String match = matcher.group();
				int start = token.getOffset() + matcher.start();
				ResourceUtils.createAndAttachFindingForFilteredRegion(group,
						match, element, start, start + match.length() - 1,
						writeKey);
			}
		}

	}

	/** {@inheritDoc} */
	@Override
	protected String[] getKeys() {
		return new String[] { writeKey };
	}

}