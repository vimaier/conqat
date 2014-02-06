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
 * @version $Rev: 46269 $
 * @ConQAT.Rating GREEN Hash: DEC6FEDECB71AB856F4FBC2572C43121
 */

@AConQATProcessor(description = "Processor to find trivial interface comments, i.e. comments that only repeat the information already provided by the method or field name.")
public class TrivialInterfaceCommentAnalysis extends
		InterfaceCommentCoherenceAnalysisBase {

	/** Name of the findings group for trivial member comments. */
	private static final String FINDING_GROUP_NAME = "Trivial Member Comment";

	/** Constructor. */
	public TrivialInterfaceCommentAnalysis() {
		super(FINDING_GROUP_NAME);
	}

	/** {@inheritDoc} */
	@Override
	protected void analyzeCoherence(Comment comment) throws ConQATException {
		if (getCoherenceCoefficient(getCommentHeadline(comment, true)) > 0.5) {
			createFinding(comment, FINDING_GROUP_NAME);
		}
	}
}