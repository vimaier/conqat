/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2011 the ConQAT Project                                   |
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

import java.util.HashSet;
import java.util.Set;

import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.analysis.NumericMetricAnalyzerBase;
import org.conqat.engine.resource.text.TextElementUtils;
import org.conqat.engine.resource.util.ResourceUtils;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.resource.ITokenResource;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.scanner.ETokenType.ETokenClass;
import org.conqat.lib.scanner.IToken;

/**
 * Common base class for analyzers counting longest statement lists (aka method
 * length). This is required, as we support this both for shallow and fully
 * parsed code.
 * 
 * @param <R>
 *            type of the resource
 * 
 * @param <E>
 *            type of the element (in the resource tree)
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46192 $
 * @ConQAT.Rating GREEN Hash: 85B46507C2D3F5E0581AEDDA98602645
 */
public abstract class LongestStatementListAnalyzerBase<R extends ITokenResource, E extends ITokenElement>
		extends NumericMetricAnalyzerBase<R, E> {

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "The length of the longest statement list measured in lines.", type = "java.lang.Number")
	public static final String KEY = "LSL";

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "empty-lines", attribute = "ignore", optional = true, description = ""
			+ "If this is true, empty lines are ignored when calculating the length. Default is false.")
	public boolean ignoreEmptyLines = false;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "comment-lines", attribute = "ignore", optional = true, description = ""
			+ "If this is true, lines containing only comments are ignored when calculating the length. Default is false.")
	public boolean ignoreCommentLines = false;

	/** {@inheritDoc} */
	@Override
	protected String getKey() {
		return KEY;
	}

	/** {@inheritDoc} */
	@Override
	protected void calculateMetrics(E element) throws ConQATException {
		// the reason to have a local variable (that has to be passed across
		// methods) is that this method may be called in parallel for different
		// element, thus using a shared attribute would lead to problems.
		Set<Integer> ignoredLines = new HashSet<Integer>();
		try {
			if (ignoreEmptyLines) {
				ignoredLines.addAll(determineEmptyLines(element));
			}

			if (ignoreCommentLines) {
				ignoredLines.addAll(determineCommentLines(element));
			}
		} catch (ConQATException e) {
			getLogger().error(
					"Failed accessing element content: " + e.getMessage(), e);
		}

		calculateStatementListLocations(element, ignoredLines);
	}

	/** Returns the set of 1-based line numbers corresponding to empty lines. */
	private static Set<Integer> determineEmptyLines(ITokenElement element)
			throws ConQATException {
		Set<Integer> emptyLines = new HashSet<Integer>();
		int lineNumber = 1;
		for (String line : TextElementUtils.getLines(element)) {
			if (StringUtils.isEmpty(line)) {
				emptyLines.add(lineNumber);
			}
			lineNumber += 1;
		}
		return emptyLines;
	}

	/**
	 * Returns the set of 1-based line numbers corresponding to lines containing
	 * only comments (and whitespace).
	 */
	private Set<Integer> determineCommentLines(ITokenElement element)
			throws ConQATException {
		Set<Integer> nonCommentLines = new HashSet<Integer>();
		Set<Integer> commentLines = new HashSet<Integer>();

		for (IToken token : element.getTokens(getLogger())) {
			if (token.getType().getTokenClass() == ETokenClass.COMMENT) {
				insertTokenLines(commentLines, token);
			} else {
				insertTokenLines(nonCommentLines, token);
			}
		}

		commentLines.removeAll(nonCommentLines);
		return commentLines;
	}

	/** Inserts 1-based numbers of lines occupied by the given token. */
	private void insertTokenLines(Set<Integer> lines, IToken token) {
		int startLine = token.getLineNumber();
		int length = StringUtils.splitLinesAsList(token.getText()).size();
		for (int i = 0; i < length; ++i) {
			// add +1 to convert from 0-based token lines
			lines.add(startLine + 1 + i);
		}
	}

	/**
	 * Template method that should traverse all statement lists and report them
	 * using {@link #reportStatementListForOffsets(int, int, Set)}. For this,
	 * the ignoredLines must be passed on.
	 */
	protected abstract void calculateStatementListLocations(E element,
			Set<Integer> ignoredLines) throws ConQATException;

	/** Reports a statement list found. Both line numbers are inclusive. */
	protected void reportStatementListForOffsets(int startOffset,
			int endOffset, Set<Integer> ignoredLines) throws ConQATException {
		int length = computeLength(
				currentElement.convertFilteredOffsetToLine(startOffset),
				currentElement.convertFilteredOffsetToLine(endOffset),
				ignoredLines);

		reportMetricValue(length,
				ResourceUtils.createTextRegionLocationForFilteredOffsets(
						currentElement, startOffset, endOffset));
	}

	/** Compute length of code fragment */
	protected int computeLength(int startLine, int endLine,
			Set<Integer> ignoredLines) {
		int length = 0;
		for (int line = startLine; line <= endLine; ++line) {
			if (!ignoredLines.contains(line)) {
				length += 1;
			}
		}
		return length;
	}
}
