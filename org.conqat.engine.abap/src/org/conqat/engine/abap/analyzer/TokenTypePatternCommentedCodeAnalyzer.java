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
package org.conqat.engine.abap.analyzer;

import static org.conqat.engine.abap.analyzer.ECommentType.CODE;
import static org.conqat.engine.abap.analyzer.ECommentType.DOCUMENTATION;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.sourcecode.pattern.EnumPatternMatcher;
import org.conqat.engine.sourcecode.pattern.TokenTypePattern;
import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.IToken;

/**
 * {@ConQAT.Doc}
 * 
 * @author herrmama
 * @author $Author: hummelb $
 * @version $Rev: 43290 $
 * @ConQAT.Rating GREEN Hash: B0EDDCB0BAE7B8AC8CC77CDB8BD1F09C
 */
@AConQATProcessor(description = "A processor that uses a pattern on token "
		+ "sequences to identify commented code")
public class TokenTypePatternCommentedCodeAnalyzer extends
		ScanningCommentedCodeAnalyzerBase {

	/** List of patterns for each comment kind. */
	private final Map<ETokenType, List<TokenTypePattern>> patternLists = new HashMap<ETokenType, List<TokenTypePattern>>();

	/** Add a pattern. */
	@AConQATParameter(name = "search", minOccurrences = 1, maxOccurrences = -1, description = "Adds a pattern.")
	public void addPattern(
			@AConQATAttribute(name = "commentKind", description = "The kind of comment to be matched.") ETokenType commentKind,
			@AConQATAttribute(name = "pattern", description = "The pattern to be matched.") String pattern)
			throws ConQATException {
		List<TokenTypePattern> patternList = getOrCreatePatternList(commentKind);
		patternList.add(new TokenTypePattern(pattern));
	}

	/** Get or create the list of patterns for a certain kind of comment. */
	private List<TokenTypePattern> getOrCreatePatternList(ETokenType commentKind) {
		List<TokenTypePattern> patternList = patternLists.get(commentKind);
		if (patternList == null) {
			patternList = new ArrayList<TokenTypePattern>();
			patternLists.put(commentKind, patternList);
		}
		return patternList;
	}

	/** {@inheritDoc} */
	@Override
	protected ECommentType getType(Comment comment, List<IToken> commentTokens) {
		if (matches(comment, commentTokens)) {
			return CODE;
		}
		return DOCUMENTATION;
	}

	/** Check whether the token sequence of a comment matches */
	private boolean matches(Comment comment, List<IToken> commentTokens) {
		List<TokenTypePattern> patternList = patternLists
				.get(comment.getKind());
		if (patternList != null) {
			return matches(commentTokens, patternList);
		}
		return false;
	}

	/** Check whether one of the patterns matches on a token sequence. */
	private boolean matches(List<IToken> commentTokens,
			List<TokenTypePattern> patternList) {
		for (TokenTypePattern pattern : patternList) {
			EnumPatternMatcher matcher = pattern.matcher(commentTokens);
			if (matcher.find()) {
				return true;
			}
		}
		return false;
	}
}