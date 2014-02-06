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
package org.conqat.engine.commons.range_distribution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.assessment.IAssessmentRangesDefinition;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.traversal.ETargetNodes;
import org.conqat.engine.commons.traversal.TraversalUtils;
import org.conqat.engine.commons.util.ConQATInputProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: DB6315F870E7E4751888428E201E951E
 */
@AConQATProcessor(description = "This processor creates a distribution table. "
		+ RangeDistribution.DOC)
public class RangeDistributionCreator extends
		ConQATInputProcessorBase<IConQATNode> {

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "target", attribute = "nodes", description = "The target nodes to operate on [default is LEAVES]", optional = true)
	public ETargetNodes targetNodes = ETargetNodes.LEAVES;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "principal-metric", attribute = ConQATParamDoc.READKEY_KEY_NAME, description = "Key for the principal metric.")
	public String principalKey;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "default-principal", attribute = "value", description = "Default value to be used if principal metric is "
			+ "undefined for an entity [default is 0]", optional = true)
	public double defaultPrincipalValue = 0;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "assessment-definition", attribute = ConQATParamDoc.INPUT_REF_NAME, description = "Asessment ranges definition.")
	public IAssessmentRangesDefinition assessmentDefinition;

	/** Assessment rules. */
	private Collection<IAssessmentRule> rules = new ArrayList<IAssessmentRule>();

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "assessment-rule", description = "Add an assessment rule")
	public void addAssessmentRule(
			@AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC) IAssessmentRule rule) {
		rules.add(rule);
	}

	/** {@inheritDoc} */
	@Override
	public AssessedRangeDistribution process() throws ConQATException {
		List<IConQATNode> entities = TraversalUtils.listDepthFirst(input,
				targetNodes);
		return new AssessedRangeDistribution(entities, principalKey,
				defaultPrincipalValue, assessmentDefinition, rules);
	}
}