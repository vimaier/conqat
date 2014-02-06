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
package org.conqat.engine.text.comments.analysis.metric;

import java.util.List;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.IElement;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.text.comments.Comment;
import org.conqat.engine.text.comments.ECommentCategory;
import org.conqat.engine.text.comments.analysis.CommentClassificationAnalysisBase;
import org.conqat.lib.scanner.IToken;

/**
 * Base class for counting comments of different categories.
 * 
 * @author $Author: steidl $
 * @version $Rev: 46279 $
 * @ConQAT.Rating GREEN Hash: EC3B6A754F6687FFF0FB837F97B97BCD
 */
public abstract class CommentCountBase extends
		CommentClassificationAnalysisBase {

	/** Variable to count a specific type of comment or finding. */
	private int count;

	/** {@inheritDoc} */
	@Override
	protected void setUpElementAnalysis(List<IToken> tokens,
			ITokenElement element) {
		count = 0;
	}

	/** {@inheritDoc} */
	@Override
	protected void analyzeComment(IElement element, Comment comment,
			ECommentCategory category) throws ConQATException {
		count += count(element, comment, category);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Sets the count variable as value of the given element.
	 * 
	 * */
	@Override
	protected void completeElementAnalysis(List<IToken> tokens,
			ITokenElement element) {
		element.setValue(getKey(), count);
	}

	/** Performs the actual counting of comments. */
	protected abstract int count(IElement element, Comment comment,
			ECommentCategory category) throws ConQATException;

	/** Returns the key of the counting variable. */
	protected abstract String getKey();

}
