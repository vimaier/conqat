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
package org.conqat.engine.architecture.aggregation;

import java.util.List;

import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.lib.commons.assessment.Assessment;

/**
 * {@ConQAT.Doc}
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 30F4AA0D5DF34C1F66C46C4CC9D81446
 */
@AConQATProcessor(description = "This processor aggregates assessments along the "
		+ "hierarchy defined by the architecture in a bottom-up manner.")
public class ArchitectureAssessmentAggregator extends
		ArchitectureAggregatorBase<Assessment> {

	/** Aggregate assessments. */
	@Override
	protected Assessment aggregate(List<Assessment> values) {
		return Assessment.aggregate(values);
	}

	/**
	 * Returns assessment or <code>null</code> if no value or value of incorrect
	 * type was found.
	 */
	@Override
	protected Assessment obtainValue(IConQATNode node, String key) {
		return NodeUtils.getValue(node, key, Assessment.class, null);
	}
}