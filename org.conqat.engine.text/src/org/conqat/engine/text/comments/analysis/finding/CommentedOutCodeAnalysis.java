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

import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.IElement;
import org.conqat.engine.text.comments.Comment;
import org.conqat.engine.text.comments.ECommentCategory;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 46264 $
 * @ConQAT.Rating GREEN Hash: FA058F1839010F6D96F9E2797D7CB01B
 */
@AConQATProcessor(description = "Creates findings for commented out code.")
public class CommentedOutCodeAnalysis extends CommentFindingAnalysisBase {

	/** Name of the findings group for commented out code. */
	private static final String FINDING_GROUP_NAME = "Commented Out Code";

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Commented Out Code Count", type = "java.lang.Double")
	public static final String KEY_CommentedOutCodeLOC = "commented out code (LOC)";

	/** Constructor */
	public CommentedOutCodeAnalysis() {
		super(FINDING_GROUP_NAME);
	}

	/** {@inheritDoc} */
	@Override
	protected void analyzeComment(IElement element, Comment comment,
			ECommentCategory category) throws ConQATException {
		if (category == ECommentCategory.CODE) {
			createFinding(comment, FINDING_GROUP_NAME);
		}
	}
}