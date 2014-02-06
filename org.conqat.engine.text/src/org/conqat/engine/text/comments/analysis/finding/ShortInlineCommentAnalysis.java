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
package org.conqat.engine.text.comments.analysis.finding;

import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.text.comments.Comment;

/**
 * {@ConQAT.Doc}
 * 
 * Creates findings for inline comments that contain at most two words.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 46264 $
 * @ConQAT.Rating GREEN Hash: B3A3733F5982303317A459D0C889D8C9
 */
@AConQATProcessor(description = "Creates findings for inline comments that contain at most two words.")
public class ShortInlineCommentAnalysis extends InlineCommentLengthAnalysisBase {

	/**
	 * Name of the findings group for short inline comments (comments with less
	 * than two words).
	 */
	public static final String FINDING_GROUP_NAME = "Short Inline Comment";

	/**
	 * Comments with at most two words are considered to be long.
	 */
	private static final int LENGTH_THRESHOLD = 2;

	/** Constructor. */
	public ShortInlineCommentAnalysis() {
		super(FINDING_GROUP_NAME);
	}

	/** {@inheritDoc} */
	@Override
	protected void analyzeCommentLengthInWords(Comment comment, int length)
			throws ConQATException {
		if (length <= LENGTH_THRESHOLD) {
			createFinding(comment, FINDING_GROUP_NAME);
		}
	}
}