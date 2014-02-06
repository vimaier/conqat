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
package org.conqat.engine.commons.aggregation;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.ConQATPipelineProcessorBase;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * A simple processor that counts the number of leaf nodes and aggregates this
 * value up to the root node.
 * 
 * @author Benjamin Hummel
 * @author $Author: hummelb $
 * @version $Rev: 37013 $
 * @ConQAT.Rating GREEN Hash: 3EBCA4FD39105C84C64835D8AF2EA772
 */
@AConQATProcessor(description = "A processor for counting the number of "
		+ " leaf nodes and aggregating this value up to the root node. "
		+ "Inner nodes are not counted but only used for aggregation.")
public class LeafCounter extends ConQATPipelineProcessorBase<IConQATNode> {

	/** The key to write the result into. */
	protected String key;

	/** Set the key used for writing the result. */
	@AConQATParameter(name = ConQATParamDoc.WRITEKEY_NAME, minOccurrences = 1, maxOccurrences = 1, description = ConQATParamDoc.WRITEKEY_NAME)
	public void setWriteKey(
			@AConQATAttribute(name = ConQATParamDoc.WRITEKEY_KEY_NAME, description = ConQATParamDoc.WRITEKEY_KEY_DESC)
			String key) {
		this.key = key;
	}

	/** {@inheritDoc} */
	@Override
	protected void processInput(IConQATNode root) {
		NodeUtils.addToDisplayList(root, key);
		traverse(root);
	}

	/** Count child nodes and store them at the nodes (recursively). */
	private int traverse(IConQATNode node) {
		int sum = 1;
		if (node.hasChildren()) {
			sum = 0;
			for (IConQATNode child : node.getChildren()) {
				sum += traverse(child);
			}
		}
		node.setValue(key, sum);
		return sum;
	}
}