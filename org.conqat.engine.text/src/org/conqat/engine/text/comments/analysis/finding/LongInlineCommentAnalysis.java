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
 * @author $Author: hummelb $
 * @version $Rev: 46264 $
 * @ConQAT.Rating GREEN Hash: C818E540A6D5BFD458D251AED6A0112C
 */
@AConQATProcessor(description = "Creates findings for inline comments that contain more than 30 words.")
public class LongInlineCommentAnalysis extends InlineCommentLengthAnalysisBase {

	/**
	 * Name of the findings group for long inline comments (Comments more than 2
	 * words).
	 */
	private static final String FINDING_GROUP_NAME = "Long Inline Comment";

	/**
	 * Comments with at least 30 words are considered to be long.
	 */
	private static final int LENGTH_THRESHOLD = 30;

	/** Constructor. */
	public LongInlineCommentAnalysis() {
		super(FINDING_GROUP_NAME);
	}

	/** {@inheritDoc} */
	@Override
	protected void analyzeCommentLengthInWords(Comment comment, int length)
			throws ConQATException {
		if (length >= LENGTH_THRESHOLD) {
			createFinding(comment, FINDING_GROUP_NAME);
		}
	}
}