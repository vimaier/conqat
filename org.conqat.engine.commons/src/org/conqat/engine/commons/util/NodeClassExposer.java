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
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.traversal.ETargetNodes;
import org.conqat.engine.commons.traversal.TargetExposedNodeTraversingProcessorBase;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: deissenb $
 * @version $Rev: 39844 $
 * @ConQAT.Rating GREEN Hash: 360F0E36E6F011A42B1207E57A7CFF2C
 */
@AConQATProcessor(description = "Annotates all nodes with the name of the underlying node class. This is mostly used for testing and debugging.")
public class NodeClassExposer extends
		TargetExposedNodeTraversingProcessorBase<IConQATNode> {

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "The simple name of the node class.", type = "java.lang.String")
	public static final String KEY = "node-class";

	/** {@inheritDoc} */
	@Override
	protected void setUp(IConQATNode root) {
		NodeUtils.addToDisplayList(root, KEY);
	}

	/** {@inheritDoc} */
	@Override
	public void visit(IConQATNode node) {
		node.setValue(KEY, node.getClass().getSimpleName());
	}

	/** {@inheritDoc} */
	@Override
	protected ETargetNodes getDefaultTargetNodes() {
		return ETargetNodes.ALL;
	}
}
