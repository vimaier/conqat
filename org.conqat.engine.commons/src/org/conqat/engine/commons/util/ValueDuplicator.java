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

import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.traversal.ETargetNodes;
import org.conqat.engine.commons.traversal.TargetExposedNodeTraversingProcessorBase;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: juergens $
 * @version $Rev: 34529 $
 * @ConQAT.Rating GREEN Hash: 73124D866D80357F6AFA4637DF70D845
 */
@AConQATProcessor(description = "Copies values stored under a given source key "
		+ "to the given target key.")
public class ValueDuplicator extends
		TargetExposedNodeTraversingProcessorBase<IConQATNode> {

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "fromKey", attribute = "value", description = "The key to copy the value from", optional = false)
	public String fromKey;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "toKey", attribute = "value", description = "The key to copy the value to", optional = false)
	public String toKey;

	/** {@inheritDoc} */
	@Override
	public void visit(IConQATNode node) {
		node.setValue(toKey, node.getValue(fromKey));
	}

	/** {@inheritDoc} */
	@Override
	protected ETargetNodes getDefaultTargetNodes() {
		return ETargetNodes.ALL;
	}

}
