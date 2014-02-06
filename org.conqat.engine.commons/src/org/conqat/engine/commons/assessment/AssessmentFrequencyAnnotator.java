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

import java.util.LinkedHashMap;
import java.util.Map;

import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.traversal.ETargetNodes;
import org.conqat.engine.commons.traversal.TargetExposedNodeTraversingProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.assessment.Assessment;
import org.conqat.lib.commons.assessment.ETrafficLightColor;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 35132 $
 * @ConQAT.Rating GREEN Hash: 96F96FF95C34E1D12E9A4BC206CD9D4C
 */
@AConQATProcessor(description = "Extracts the times a color occurs from an assessment stored in a node and stores it in a key.")
public class AssessmentFrequencyAnnotator extends
		TargetExposedNodeTraversingProcessorBase<IConQATNode> {

	/** Maps from traffic light color to target key */
	private final Map<ETrafficLightColor, String> colorToKey = new LinkedHashMap<ETrafficLightColor, String>();

	/** Key under which assessment is stored */
	private String assessmentKey;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "annotate", description = "Extract count of colored rating and store in key. Each key and each color can only be set once.", minOccurrences = 1, maxOccurrences = -1)
	public void addMapping(
			@AConQATAttribute(name = "color", description = "Name of color to extract") ETrafficLightColor color,
			@AConQATAttribute(name = "key", description = "Name of key") String key)
			throws ConQATException {

		// check if color is still free
		if (colorToKey.containsKey(color)) {
			throw new ConQATException("Color " + color
					+ " already set for this processor. Please use only once.");
		}

		// check if key is still free
		if (colorToKey.values().contains(key)) {
			throw new ConQATException("Key " + key
					+ " already set for this processor. Please use only once.");
		}

		colorToKey.put(color, key);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "assessment", description = "Key under which assessment is stored", minOccurrences = 1, maxOccurrences = 1)
	public void setAssessmentKey(
			@AConQATAttribute(name = "key", description = "Name of assessment key") String assessmentKey) {
		this.assessmentKey = assessmentKey;
	}

	/** {@inheritDoc} */
	@Override
	protected ETargetNodes getDefaultTargetNodes() {
		return ETargetNodes.ALL;
	}

	/** {@inheritDoc} */
	@Override
	public void visit(IConQATNode node) throws ConQATException {
		Assessment assessment = NodeUtils.getValue(node, assessmentKey,
				Assessment.class);
		if (assessment == null) {
			getLogger().warn("No assessment found for node: " + node);
			return;
		}

		for (ETrafficLightColor color : colorToKey.keySet()) {
			int colorCount = assessment.getColorFrequency(color);
			node.setValue(colorToKey.get(color), colorCount);
		}
	}

	/** {@inheritDoc} */
	@Override
	protected void setUp(IConQATNode root) {
		NodeUtils.addToDisplayList(root, colorToKey.values());
	}
}