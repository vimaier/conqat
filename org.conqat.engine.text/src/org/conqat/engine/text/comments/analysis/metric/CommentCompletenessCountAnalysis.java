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

import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.shallowparser.framework.ShallowEntity;
import org.conqat.engine.text.comments.analysis.CommentCompletenessAnalyzerBase;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: steidl $
 * @version $Rev: 46589 $
 * @ConQAT.Rating GREEN Hash: 102A87D6F4CAABA8D7322511A5CA35C9
 */
@AConQATProcessor(description = "Counts the number of selected entities and how many of them are commented.")
public class CommentCompletenessCountAnalysis extends
		CommentCompletenessAnalyzerBase {

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "overall-count", attribute = "key", description = "Key to write the overall count into.")
	public String overallCountKey;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "commented-count", attribute = "key", description = "Key to write the commented count into.")
	public String commentedCountKey;

	/** Counts the overall number of selected entities per element. */
	private int overallEntityCount = 0;

	/** Counts the number of commented selected entities per element. */
	private int commentedEntityCount = 0;

	/** {@inheritDoc} */
	@Override
	protected void analyzeElement(ITokenElement element) throws ConQATException {
		overallEntityCount = 0;
		commentedEntityCount = 0;

		super.analyzeElement(element);

		element.setValue(overallCountKey, overallEntityCount);
		element.setValue(commentedCountKey, commentedEntityCount);
	}

	/** {@inheritDoc} */
	@Override
	protected void analyzeSelectedEntity(ShallowEntity entity,
			ITokenElement element, boolean isCommented) {
		overallEntityCount += 1;
		if (isCommented) {
			commentedEntityCount += 1;
		}
	}

	/** {@inheritDoc} */
	@Override
	protected String[] getKeys() {
		return new String[] { commentedCountKey, overallCountKey };
	}
}
