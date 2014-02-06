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

import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.resource.IElement;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.text.comments.Comment;
import org.conqat.engine.text.comments.ECommentCategory;
import org.conqat.engine.text.comments.analysis.CommentClassificationAnalysisBase;
import org.conqat.lib.scanner.IToken;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: steidl $
 * @version $Rev: 46589 $
 * @ConQAT.Rating YELLOW Hash: DAECF374BDD97D55D86519AE11F2FECD
 */
@AConQATProcessor(description = "Annotates each element with the number of copyright headers found (0 or 1).")
public class CopyrightCountAnalysis extends CommentClassificationAnalysisBase {

	/** Key for number of copyrights */
	@AConQATKey(description = "Number of Copyrights", type = "java.lang.Integer")
	public static final String KEY_NUM_COPYRIGHTS = "#Copyrights";

	/** Number of copyrights seen during analysis. */
	private int numCopyrights;

	/** {@inheritDoc} */
	@Override
	protected void setUpElementAnalysis(List<IToken> tokens,
			ITokenElement element) {
		numCopyrights = 0;
	}

	/** {@inheritDoc} */
	@Override
	protected void completeElementAnalysis(List<IToken> tokens,
			ITokenElement element) {
		element.setValue(KEY_NUM_COPYRIGHTS, numCopyrights);
	}

	/** {@inheritDoc} */
	@Override
	protected void analyzeComment(IElement element, Comment comment,
			ECommentCategory category) {
		if (category == ECommentCategory.COPYRIGHT) {
			// we do not increment the number of copyrights as we only care
			// about whether the file contains 0 or 1 copyrights (we do not care
			// how many copyrights there are, if there are more than 1).
			numCopyrights = 1;
		}

	}
}