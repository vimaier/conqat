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
package org.conqat.engine.commons.mark;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.ConQATPipelineProcessorBase;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: poehlmann $
 * @version $Rev: 45924 $
 * @ConQAT.Rating GREEN Hash: 8D03B2D98A714402A33FE74109A5E66F
 */
@AConQATProcessor(description = "Annotates each node with the name or id of its parent at a given depth. "
		+ "This can be used to mark nodes belonging to certain packages or modules.")
public class ParentNameToChildrenAnnotator extends
		ConQATPipelineProcessorBase<IConQATNode> {

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "parent-depth", attribute = "value", description = ""
			+ "Depth of the node whose name/id is written to all of its child nodes. "
			+ "The root has depth 0. All nodes with smaller depth will not be marked.")
	public int parentDepth;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = ConQATParamDoc.WRITEKEY_NAME, attribute = ConQATParamDoc.WRITEKEY_KEY_NAME, description = ConQATParamDoc.WRITEKEY_DESC)
	public String writeKey;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "use-id", attribute = "value", optional = true, description = ""
			+ "If this is true, the id of the parent node will be used as label, otherwise the name. Default is false.")
	public boolean useId = false;

	/** {@inheritDoc} */
	@Override
	protected void processInput(IConQATNode input) throws ConQATException {
		if (parentDepth < 0) {
			throw new ConQATException("Parent depth must be non-negative!");
		}

		NodeUtils.addToDisplayList(input, writeKey);
		traverse(input, 0, null);
	}

	/**
	 * Traverses the given node depth first, attaching labels according to the
	 * processor's description.
	 */
	private void traverse(IConQATNode node, int depth, String label) {
		if (depth == parentDepth) {
			if (useId) {
				label = node.getId();
			} else {
				label = node.getName();
			}
		}

		// as label is a local variable/parameter, this check is sufficient and
		// no additional depth check is required
		if (label != null) {
			node.setValue(writeKey, label);
		}

		if (node.hasChildren()) {
			for (IConQATNode child : node.getChildren()) {
				traverse(child, depth + 1, label);
			}
		}
	}

}
