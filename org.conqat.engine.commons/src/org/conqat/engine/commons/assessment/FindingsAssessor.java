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

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.findings.Finding;
import org.conqat.engine.commons.findings.FindingsList;
import org.conqat.engine.commons.findings.util.FindingUtils;
import org.conqat.engine.commons.node.DisplayList;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.traversal.ETargetNodes;
import org.conqat.engine.commons.traversal.TargetExposedNodeTraversingProcessorBase;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.assessment.Assessment;
import org.conqat.lib.commons.assessment.ETrafficLightColor;
import org.conqat.lib.commons.string.StringUtils;

/**
 * {@ConQAT.Doc}
 * 
 * @author hummelb
 * @author $Author: steidl $
 * @version $Rev: 43636 $
 * @ConQAT.Rating GREEN Hash: E26A20967290F67917AD3166A8EF3E49
 */
@AConQATProcessor(description = "This processor rates leaf nodes based on findings found. "
		+ "For this all keys mentioned in the display list are inspected. "
		+ "By default the color frequency of the assessment is 1 unless parameter color-frequency is provided. "
		+ "If a key for coler-frequency is provided the frequency is taken from the value stored at the given key. "
		+ "If a frequency key is is provided but the key is not set for a node, 0 is used as frequency "
		+ "(this might be e. g. the case in generated code).")
public class FindingsAssessor extends
		TargetExposedNodeTraversingProcessorBase<IConQATNode> {

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "The assessment based on findings for the node.", type = "org.conqat.lib.commons.assessment.Assessment")
	public static final String KEY = "FindingsAssessment";

	/** The display list. */
	private DisplayList displayList;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = ConQATParamDoc.FINDING_PARAM_NAME, attribute = ConQATParamDoc.FINDING_KEY_NAME, optional = true, description = "Key in which findings are stored")
	public String findingKey;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "color-frequency", attribute = ConQATParamDoc.READKEY_KEY_NAME, optional = true, description = "Key for the value which is taken as frequency for the assessment. If key is not set for a node, 0 is used as frequency.")
	public String frequencyKey;

	/** {@inheritDoc} */
	@Override
	protected ETargetNodes getDefaultTargetNodes() {
		return ETargetNodes.LEAVES;
	}

	/** {@inheritDoc} */
	@Override
	protected void setUp(IConQATNode root) throws ConQATException {
		super.setUp(root);
		NodeUtils.addToDisplayList(root, KEY);
		displayList = NodeUtils.getDisplayList(root);
	}

	/** {@inheritDoc} */
	@Override
	public void visit(IConQATNode node) {
		ETrafficLightColor color = ETrafficLightColor.GREEN;

		for (String key : displayList) {
			color = ETrafficLightColor.getDominantColor(color,
					dominantFindingColorFor(node, key));
		}

		if (findingKey != null) {
			color = ETrafficLightColor.getDominantColor(color,
					dominantFindingColorFor(node, findingKey));
		}

		Assessment assessment = new Assessment();
		assessment.add(color, determineFrequency(node));
		node.setValue(KEY, assessment);

	}

	/**
	 * Determines the frequency of the assessment to be created.
	 */
	private int determineFrequency(IConQATNode node) {
		if (StringUtils.isEmpty(frequencyKey)) {
			return 1;
		}
		return (int) NodeUtils.getDoubleValue(node, frequencyKey, 0);
	}

	/** Returns dominant color of all findings stored under key */
	private ETrafficLightColor dominantFindingColorFor(IConQATNode node,
			String key) {
		ETrafficLightColor color = ETrafficLightColor.GREEN;
		Object value = node.getValue(key);
		if (!(value instanceof FindingsList)) {
			return color;
		}

		FindingsList findings = (FindingsList) value;
		if (!findings.isEmpty()) {
			for (Finding finding : findings) {
				color = ETrafficLightColor.getDominantColor(color,
						FindingUtils.getFindingColor(finding));
			}
		}
		return color;
	}
}