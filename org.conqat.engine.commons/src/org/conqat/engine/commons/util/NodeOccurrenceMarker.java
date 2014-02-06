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

import java.util.Map;

import org.conqat.engine.commons.CommonUtils;
import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.traversal.INodeVisitor;
import org.conqat.engine.commons.traversal.TraversalUtils;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.APipelineSource;
import org.conqat.engine.core.core.ConQATException;

/**
 * {@ConQAT.Doc}
 * 
 * @author Elmar Juergens
 * @author $Author: deissenb $
 * @version $Rev: 34727 $
 * @ConQAT.Rating GREEN Hash: 62097799470F8A0B4C28C2D3EFD00565
 */
@AConQATProcessor(description = ""
		+ "This processor indicates nodes that occur in a source tree in a target tree."
		+ "Node matching is performed on node ids. For each node in the target tree that "
		+ "also occurs in the source tree, a specified value is stored under a specified key.")
public class NodeOccurrenceMarker extends ConQATProcessorBase implements
		INodeVisitor<IConQATNode, ConQATException> {

	/** Target scope from which nodes are read */
	private IConQATNode input;

	/** Target scope into which results are written */
	private IConQATNode target;

	/** Name of target key */
	private String key;

	/** Target value */
	private Object value;

	/** Maps from node id to node */
	private Map<String, IConQATNode> idToLeafNodeMap;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "scopes", minOccurrences = 1, maxOccurrences = 1, description = "Node tree into which keys are written")
	public void setScopes(
			@AConQATAttribute(name = "input", description = "Scope from which nodes are read") IConQATNode input,
			@APipelineSource @AConQATAttribute(name = "target", description = "Scope into which results are written") IConQATNode target) {
		this.input = input;
		this.target = target;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "constant", minOccurrences = 1, maxOccurrences = 1, description = ""
			+ "Constant value that gets set")
	public void setConstant(
			@AConQATAttribute(name = "key", description = "Key under which the constant is stored") String key,
			@AConQATAttribute(name = "value", description = "Value of constant") String valueString,
			@AConQATAttribute(name = "type", description = "Type of constant (int, boolean, ...)") String typeName)
			throws ConQATException {
		value = CommonUtils.convertTo(valueString, typeName);
		this.key = key;
	}

	/** {@inheritDoc} */
	@Override
	public IConQATNode process() throws ConQATException {
		idToLeafNodeMap = TraversalUtils.createIdToLeafNodeMap(target);
		TraversalUtils.visitLeavesDepthFirst(this, input);
		return target;
	}

	/** {@inheritDoc} */
	@Override
	public void visit(IConQATNode node) {
		IConQATNode targetNode = idToLeafNodeMap.get(node.getId());
		if (targetNode != null) {
			targetNode.setValue(key, value);
		} else {
			getLogger().warn(
					"Could not find node " + node.getId() + " in target");
		}
	}

}