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
package org.conqat.engine.java.javadoc;

import java.util.regex.Pattern;

import org.conqat.engine.commons.pattern.PatternList;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.java.resource.IJavaElement;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.Tag;

/**
 * Base class for analyzers that use a heuristic to check if a comment is
 * sensible or not.
 * 
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 4CBAE056A38B0A760B61A253727A9BF6
 */
@AConQATProcessor(description = "This analyzer uses a heuristic to "
		+ "check if a comment is sufficient.")
public class InsufficientCommentAnalyzer extends CommentAnalyzerBase implements
		IProgramElementDocAnalyzer {

	/**
	 * See doc of {@link #setWhiteListPattern(PatternList)} for details.
	 */
	private final PatternList whiteList = new PatternList();

	/** Create processor. */
	public InsufficientCommentAnalyzer() {
		whiteList.add(Pattern.compile("\\{@inheritDoc\\}[.]?"));
	}

	/** Set the include pattern. */
	@AConQATParameter(name = "white-list", maxOccurrences = 1, description = "Add patterns to white list. If "
			+ "a comment matches a pattern on the white list, the comment is considered sufficient.")
	public void setWhiteListPattern(
			@AConQATAttribute(name = "pattern-list", description = "The pattern list describing sufficient comments.") PatternList patternList) {
		whiteList.addAll(patternList);
	}

	/**
	 * Utility method to check if a text is an insufficient comment. This checks
	 * if the comment has at least two words and that the average word length is
	 * not < 2.
	 */
	protected boolean isInsufficient(String commentText) {
		if (whiteList.matchesAny(commentText)) {
			return false;
		}
		String[] words = commentText.trim().split("\\s+");
		if (words.length < 2) {
			return true;
		}
		int totalLength = 0;
		for (String word : words) {
			totalLength += word.length();
		}

		return (double) totalLength / words.length < 2;
	}

	/** {@inheritDoc} */
	@Override
	public void analyze(ClassDoc docElement, IJavaElement element)
			throws ConQATException {
		analyzeSimply(docElement, element);
	}

	/** {@inheritDoc} */
	@Override
	public void analyze(FieldDoc docElement, IJavaElement element)
			throws ConQATException {
		analyzeSimply(docElement, element);
	}

	/** Check comment. */
	private void analyzeSimply(ProgramElementDoc docElement,
			IJavaElement element) throws ConQATException {
		if (isInsufficient(docElement.commentText())) {
			createFinding(docElement, element);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void analyze(MethodDoc docElement, IJavaElement element)
			throws ConQATException {
		if (!isInsufficient(docElement.commentText())) {
			return;
		}

		if (isTagSufficient(docElement, "param")) {
			return;
		}

		if (isTagSufficient(docElement, "return")) {
			return;
		}

		if (isTagSufficient(docElement, "throws")) {
			return;
		}

		if (isSeeTagSufficient(docElement)) {
			return;
		}

		createFinding(docElement, element);
	}

	/** Checks if the comment at a tag is sufficient. */
	private boolean isTagSufficient(MethodDoc docElement, String tagName) {
		Tag[] tags = docElement.tags(tagName);
		if (tags.length == 0) {
			return false;
		}
		for (Tag tag : tags) {
			if (!isInsufficient(tag.text())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if the <code>see</code> tag is sufficient. As this usually
	 * contains only a class name, we cannot use the
	 * {@link #isInsufficient(String)} here.
	 */
	private boolean isSeeTagSufficient(MethodDoc docElement) {
		Tag[] tags = docElement.tags("see");
		if (tags.length == 0) {
			return false;
		}
		for (Tag tag : tags) {
			if (tag.text().length() > 2) {
				return true;
			}
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	protected String getFindingGroupName() {
		return "Insufficient Comment";
	}

	/** {@inheritDoc} */
	@Override
	public IProgramElementDocAnalyzer process() {
		return this;
	}

	/** Create finding for insufficient comments. */
	private void createFinding(ProgramElementDoc docElement,
			IJavaElement element) throws ConQATException {
		createFinding(
				docElement.qualifiedName() + " has insufficient comment.",
				docElement, element);
	}
}