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

import org.conqat.engine.commons.format.EValueFormatter;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.resource.ITokenResource;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.ETokenType.ETokenClass;
import org.conqat.lib.scanner.IToken;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: B4C5F40B8C356328527868F09D4B377B
 */
@AConQATProcessor(description = "Determines the comment ratio at the character level. "
		+ "For CR computation, whitespace characters are ignored.")
public class CommentRatioAnalyzer extends TokenAnalyzerBase {

	/** If this is not empty only tokens included in this set are respected. */
	private final Set<ETokenType> includedTokenTypes = EnumSet
			.noneOf(ETokenType.class);

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Comment Ratio", type = "java.lang.Double")
	public static final String KEY = "CR";

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "comment-type", minOccurrences = 0, description = "This parameter allows to "
			+ "set specific token types. If this is set, only comments of the specified type are respected. "
			+ "If not set, all comment tokens are respected. This parameter raises an error if a token type "
			+ "is provided that is not a comment.")
	public void addCommentType(
			@AConQATAttribute(name = "value", description = "comment token type") ETokenType tokenType)
			throws ConQATException {
		if (tokenType.getTokenClass() != ETokenClass.COMMENT) {
			throw new ConQATException("Token type " + tokenType
					+ " is not a comment!");
		}
		includedTokenTypes.add(tokenType);
	}

	/** {@inheritDoc} */
	@Override
	protected void setUp(ITokenResource root) throws ConQATException {
		super.setUp(root);

		// ensure formating
		NodeUtils.getDisplayList(root).addKey(KEY,
				EValueFormatter.PERCENT.getFormatter());
	}

	/** {@inheritDoc} */
	@Override
	protected void analyzeTokens(List<IToken> tokens, ITokenElement element) {
		element.setValue(KEY, calculateCR(tokens, element.getLocation()));
	}

	/** Calculate comment ratio for a list of tokens. */
	private double calculateCR(List<IToken> tokens, String elementLocation) {
		int totalCharacterCount = 0;
		int commentCharacterCount = 0;

		for (IToken token : tokens) {
			int tokenCharacterCount = StringUtils.removeWhitespace(
					token.getText()).length();
			totalCharacterCount += tokenCharacterCount;
			if (isIncludedComment(token)) {
				commentCharacterCount += tokenCharacterCount;
			}
		}
		
		
		
		if (totalCharacterCount == 0) {
		    getLogger().warn("Element " + elementLocation + " is empty.");
	        return 0;
		}

        return (double) commentCharacterCount / (double) totalCharacterCount;
	}

	/** Checks if the token should be counted as a comment. */
	private boolean isIncludedComment(IToken token) {
		ETokenType type = token.getType();

		if (type.getTokenClass() != ETokenClass.COMMENT) {
			return false;
		}

		if (includedTokenTypes.isEmpty()) {
			return true;
		}

		return includedTokenTypes.contains(type);
	}

	/** {@inheritDoc} */
	@Override
	protected String[] getKeys() {
		return new String[] { KEY };
	}
}