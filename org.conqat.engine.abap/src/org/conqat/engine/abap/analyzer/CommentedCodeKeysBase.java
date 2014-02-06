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

import java.util.List;

import org.conqat.engine.resource.IElement;
import org.conqat.engine.resource.IResource;

import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.resource.analysis.ElementAnalyzerBase;

/**
 * Base class for processors to display and store keys for information about
 * commented code
 * 
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 * @levd.rating GREEN Hash: 209A655718A2AC66DDB75A7ECAD1BFCE
 */
public abstract class CommentedCodeKeysBase<R extends IResource, E extends IElement>
		extends ElementAnalyzerBase<R, E> {

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Comments", type = "java.util.List")
	public static final String COMMENTS_KEY = "Comments";

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Code Comments", type = "java.util.List")
	public static final String CODE_COMMENTS_KEY = "Code Comments";

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Number of comments", type = "java.lang.Integer")
	public static final String NUMBER_OF_COMMENTS_KEY = "NoC";

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Number of code comments", type = "java.lang.Integer")
	public static final String NUMBER_OF_CODE_COMMENTS_KEY = "NoCC";

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Lines of commented code", type = "java.lang.Integer")
	public static final String LINES_OF_COMMENTED_CODE_KEY = "LoCC";

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Characters of commented code", type = "java.lang.Integer")
	public static final String CHARACTERS_OF_COMMENTED_CODE_KEY = "CoCC";

	/** {@inheritDoc} */
	@Override
	protected String[] getKeys() {
		return new String[] { NUMBER_OF_COMMENTS_KEY,
				NUMBER_OF_CODE_COMMENTS_KEY, COMMENTS_KEY,
				LINES_OF_COMMENTED_CODE_KEY, CHARACTERS_OF_COMMENTED_CODE_KEY };
	}

	/** Store the results of the analysis in the element. */
	protected void storeResults(E element, List<Comment> comments,
			List<Comment> codeComments, int numberOfCodeComments,
			int linesOfCodeComments, int charactersOfCommentedCode) {
		element.setValue(COMMENTS_KEY, comments);
		element.setValue(CODE_COMMENTS_KEY, codeComments);
		element.setValue(NUMBER_OF_COMMENTS_KEY, comments.size());

		element.setValue(NUMBER_OF_CODE_COMMENTS_KEY, numberOfCodeComments);
		element.setValue(LINES_OF_COMMENTED_CODE_KEY, linesOfCodeComments);
		element.setValue(CHARACTERS_OF_COMMENTED_CODE_KEY,
				charactersOfCommentedCode);
	}
}