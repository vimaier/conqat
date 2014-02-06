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
package org.conqat.engine.commons.util;

import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.traversal.ETargetNodes;
import org.conqat.engine.commons.traversal.TargetExposedNodeTraversingProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.lib.commons.string.StringUtils;

/**
 * {@ConQAT.Doc}
 * 
 * 
 * @author Florian Deissenboeck
 * @author $Author: hummelb $
 * @version $Rev: 37013 $
 * @ConQAT.Rating GREEN Hash: D59953010B0D867812776405245C546F
 */
@AConQATProcessor(description = "This processor copies the node id to a key.")
public class NodeIdCopier extends
		TargetExposedNodeTraversingProcessorBase<IConQATNode> {
	/** Default key */
	public final static String DEFAULT_KEY = "Id";

	/** Id relative to which ids are pruned */
	private String relativeTo;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "relative", description = "Node relative to which ids are pruned. If not set, no pruning gets performed", minOccurrences = 0, maxOccurrences = 1)
	public void setRelativeTo(
			@AConQATAttribute(name = "to", description = "Reference to node") IConQATNode relativeTo) {
		this.relativeTo = relativeTo.getId();
	}

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "write", attribute = "key", description = "Target key. Default is "
			+ DEFAULT_KEY, optional = true)
	public String targetKey = DEFAULT_KEY;

	/** Add key to display list. */
	@Override
	protected void setUp(IConQATNode root) {
		NodeUtils.addToDisplayList(root, targetKey);
	}

	/** Leaves are the target nodes. */
	@Override
	protected ETargetNodes getDefaultTargetNodes() {
		return ETargetNodes.LEAVES;
	}

	/** Copy id to key. */
	@Override
	public void visit(IConQATNode node) {
		String id = node.getId();
		if (!StringUtils.isEmpty(relativeTo) && id.startsWith(relativeTo)) {
			id = "..." + StringUtils.stripPrefix(relativeTo, id);
		}
		node.setValue(targetKey, id);
	}

}