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
package org.conqat.engine.commons.assessment;

import java.util.List;

import org.conqat.engine.commons.aggregation.AggregatorBase;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeConstants;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.assessment.Assessment;

/**
 * An aggregator that works by summing up the assessments at every node.
 * 
 * @author $Author: pfaller $
 * @version $Rev: 37399 $
 * @ConQAT.Rating GREEN Hash: 56B9A415CBB25092CA8D778858D1CB7A
 */
@AConQATProcessor(description = "This processor aggregates on assessments by "
		+ "recursively merging the assessments of the children of a node into "
		+ "the assessment of the current node. The behaviour can be compared to "
		+ "the SumAggregator. It additionally attaches a summary assessment to "
		+ "the root node, which is based on the aggregated value of the first key provided.")
public class AssessmentAggregator extends
		AggregatorBase<Assessment, Assessment> {

	/** Constructor. */
	public AssessmentAggregator() {
		super(Assessment.class);
	}

	/** {@inheritDoc} */
	@Override
	protected void processInput(IConQATNode input) throws ConQATException {
		super.processInput(input);

		// Finally set the summary
		input.setValue(NodeConstants.SUMMARY,
				input.getValue(getFirstOutputKey()));
	}

	/** {@inheritDoc} */
	@Override
	protected Assessment aggregate(List<Assessment> values) {
		return Assessment.aggregate(values);
	}
}