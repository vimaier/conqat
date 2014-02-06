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

import java.util.HashMap;
import java.util.Map;

import org.conqat.engine.commons.pattern.PatternList;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.ETokenType.ETokenClass;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43290 $
 * @ConQAT.Rating GREEN Hash: 688D2C321CD62A0DB5FCE62358A641A0
 */
@AConQATProcessor(description = "A processor that uses different patterns for "
		+ "different token types to identify commented code")
public class PatternCommentedCodeAnalyzer extends CommentedCodeAnalyzerBase {

	/** List of patterns for each comment kind. */
	private final Map<ETokenType, PatternList> patternLists = new HashMap<ETokenType, PatternList>();

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "search", minOccurrences = 1, maxOccurrences = -1, description = "Adds a pattern.")
	public void addPattern(
			@AConQATAttribute(name = "commentKind", description = "The kind of comment to be matched. The token type specified here must be of class comment.") ETokenType commentKind,
			@AConQATAttribute(name = "patterns", description = "The list of pattern to be matched.") PatternList patternList)
			throws ConQATException {
		if (commentKind.getTokenClass() != ETokenClass.COMMENT) {
			throw new ConQATException(
					"The commentKind must be of token class comment");
		}
		patternLists.put(commentKind, patternList);
	}

	/** {@inheritDoc} */
	@Override
	protected ECommentType getType(Comment comment, ITokenElement element) {
		if (matches(comment)) {
			return CODE;
		}
		return DOCUMENTATION;
	}

	/** Check whether a comment matches */
	private boolean matches(Comment comment) {
		PatternList patternList = patternLists.get(comment.getKind());
		if (patternList != null) {
			return patternList.findsAnyIn(comment.getContent());
		}
		return false;
	}
}