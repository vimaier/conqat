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
import org.conqat.engine.resource.IElement;
import org.conqat.engine.text.comments.Comment;
import org.conqat.engine.text.comments.ECommentCategory;
import org.conqat.engine.text.comments.utils.CommentUtils;

/**
 * {@ConQAT.Doc}
 * 
 * Processor to create findings for empty interface comments.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 46264 $
 * @ConQAT.Rating GREEN Hash: A9C9783FD3CB5F2A8E6F84F9FD855112
 */
@AConQATProcessor(description = " Processor to find comments that are empty interface comments, e.g. automatically created java doc comments that do not contain further information.")
public class EmptyInterfaceCommentAnalysis extends CommentFindingAnalysisBase {

	/** Name of the findings group for empty java doc comments. */
	private static final String FINDING_GROUP_NAME = "Empty Interface Comment";

	/** Constructor. */
	public EmptyInterfaceCommentAnalysis() {
		super(FINDING_GROUP_NAME);
	}

	/** {@inheritDoc} */
	@Override
	protected void analyzeComment(IElement element, Comment comment,
			ECommentCategory category) throws ConQATException {
		if (category == ECommentCategory.INTERFACE) {
			if (CommentUtils.hasOnlyJavaDoc(comment.getCommentString())) {
				createFinding(comment, FINDING_GROUP_NAME);
			}
		}
	}
}