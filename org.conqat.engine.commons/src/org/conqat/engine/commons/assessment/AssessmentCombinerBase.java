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

import java.util.ArrayList;
import java.util.List;

import org.conqat.lib.commons.assessment.Assessment;
import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.traversal.ETargetNodes;
import org.conqat.engine.commons.traversal.TargetExposedNodeTraversingProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.ConQATException;

/**
 * Base class for processors combining multiple assessments found at different
 * keys.
 * 
 * @author hummelb
 * @author $Author: hummelb $
 * @version $Rev: 37013 $
 * @ConQAT.Rating GREEN Hash: 9217DEF391E58DDDB61FB092A97A1C1A
 */
public abstract class AssessmentCombinerBase extends
		TargetExposedNodeTraversingProcessorBase<IConQATNode> {

	/** The keys used for assessment input. */
	private final List<String> inputKeys = new ArrayList<String>();

	/** The key to write the result into. */
	private String outputKey;

	/** Add a key for reading an assessment. */
	@AConQATParameter(name = ConQATParamDoc.READKEY_NAME, minOccurrences = 1, description = ""
			+ "Adds a key to read an assessment from.")
	public void addReadKey(
			@AConQATAttribute(name = ConQATParamDoc.READKEY_KEY_NAME, description = ConQATParamDoc.READKEY_KEY_DESC)
			String key) {
		inputKeys.add(key);
	}

	/** Set the key used for writing. */
	@AConQATParameter(name = ConQATParamDoc.WRITEKEY_NAME, minOccurrences = 1, maxOccurrences = 1, description = ""
			+ "The key to write the combined value to. ")
	public void setWriteKey(
			@AConQATAttribute(name = ConQATParamDoc.WRITEKEY_KEY_NAME, description = ConQATParamDoc.WRITEKEY_KEY_DESC)
			String key) {
		outputKey = key;
	}

	/** {@inheritDoc} */
	@Override
	protected ETargetNodes getDefaultTargetNodes() {
		return ETargetNodes.LEAVES;
	}

	/** {@inheritDoc} */
	@Override
	protected void setUp(IConQATNode root) throws ConQATException {
		super.setUp(root);
		NodeUtils.addToDisplayList(root, outputKey);
	}

	/** {@inheritDoc} */
	@Override
	public void visit(IConQATNode node) {
		ArrayList<Assessment> assessments = new ArrayList<Assessment>();
		for (String key : inputKeys) {
			Object o = node.getValue(key);
			if (!(o instanceof Assessment)) {
				getLogger().info(
						"No assesment found for key " + key + " at node "
								+ node.getId());
			} else {
				assessments.add((Assessment) o);
			}
		}

		Assessment combined = combineAssessments(assessments);
		node.setValue(outputKey, combined);
	}

	/** Template method for combining the assessements at a single node. */
	protected abstract Assessment combineAssessments(
			List<Assessment> assessments);

}