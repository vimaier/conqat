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
package org.conqat.engine.commons.util;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.ConQATPipelineProcessorBase;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: goede $
 * @version $Rev: 42033 $
 * @ConQAT.Rating GREEN Hash: FE19DA145417279594E9AF07FCFCDFE3
 */
@AConQATProcessor(description = "Propagates a value towards the leaves. "
		+ "More precisely, each node gets assigned the value of the first parent node with a non-null value for the key.")
public class ValuePropagator extends ConQATPipelineProcessorBase<IConQATNode> {

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = ConQATParamDoc.READKEY_NAME, attribute = ConQATParamDoc.READKEY_KEY_NAME, description = ConQATParamDoc.READKEY_DESC)
	public String readKey;

	/** {@inheritDoc} */
	@Override
	protected void processInput(IConQATNode input) {
		propagate(input, input.getValue(readKey));
	}

	/**
	 * Propagates the given value to this node and its children. If the node
	 * already has a value for the {@link #readKey} do not replace but use this
	 * value instead.
	 */
	private void propagate(IConQATNode node, Object value) {
		Object localValue = node.getValue(readKey);
		if (localValue != null) {
			value = localValue;
		} else if (value != null) {
			node.setValue(readKey, value);
		}

		if (node.hasChildren()) {
			for (IConQATNode child : node.getChildren()) {
				propagate(child, value);
			}
		}
	}
}
