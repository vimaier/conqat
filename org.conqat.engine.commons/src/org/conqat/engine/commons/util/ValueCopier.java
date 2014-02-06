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

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.pattern.PatternTransformationList;
import org.conqat.engine.commons.traversal.ETargetNodes;
import org.conqat.engine.commons.traversal.TargetExposedNodeTraversingProcessorBase;
import org.conqat.engine.commons.traversal.TraversalUtils;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.collections.PairList;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: poehlmann $
 * @version $Rev: 44532 $
 * @ConQAT.Rating YELLOW Hash: BE31C38967ACB6C7DE1B0D6313C95ED6
 */
@AConQATProcessor(description = "This processor copies values  "
		+ "from the source tree to a target tree (the pipelined "
		+ "input). Node matching is done by node ids unless an explicit mapping is specified. Values in "
		+ "the target tree are overwritten. If no keys are specified "
		+ "all keys from the source tree's display list are used.")
public class ValueCopier extends
		TargetExposedNodeTraversingProcessorBase<IConQATNode> {

	/** List of pairs of source and target keys. */
	private final PairList<String, String> copyList = new PairList<String, String>();

	/** Maps the source tree's node to their id. */
	private Map<String, IConQATNode> map;

	/** Root of the source tree. */
	private IConQATNode source;

	/**
	 * Pattern transformation from source to target node IDs. If
	 * <code>null</code> no transformation is performed.
	 */
	private PatternTransformationList nodeIdMapping;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "source", minOccurrences = 1, maxOccurrences = 1, description = "The source tree that provides the values.")
	public void setSource(
			@AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC) IConQATNode source) {
		this.source = source;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = ConQATParamDoc.READKEY_NAME, description = ""
			+ "Add key to copy. If no keys are specified, the display list of the source "
			+ "tree is used. Target key is the same as source key")
	public void addKey(
			@AConQATAttribute(name = ConQATParamDoc.READKEY_KEY_NAME, description = ConQATParamDoc.READKEY_KEY_DESC) String key) {
		copyList.add(key, key);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "rename", description = ""
			+ "Add key to copy. Result is stored under new name.")
	public void addAndRenameKey(
			@AConQATAttribute(name = "from", description = ConQATParamDoc.READKEY_KEY_DESC) String fromKey,
			@AConQATAttribute(name = "to", description = ConQATParamDoc.READKEY_KEY_DESC) String toKey) {
		copyList.add(fromKey, toKey);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "node-mapping", description = "Defines a mapping from target node IDs to source node IDs.")
	public void addAndRenameKey(
			@AConQATAttribute(name = "transformation", description = "The pattern transformation of tagert node IDs to source node IDs.") PatternTransformationList transformation) {
		nodeIdMapping = transformation;
	}

	/** {@inheritDoc} */
	@Override
	protected void setUp(IConQATNode root) {
		if (copyList.isEmpty()) {
			for (String key : NodeUtils.getDisplayList(source)) {
				copyList.add(key, key);
			}
		}
		map = TraversalUtils.createIdToNodeMap(source);

		// if only root is copied, register source's root to work even if IDs do
		// not match
		if (getTargetNodes() == ETargetNodes.ROOT) {
			map.put(root.getId(), source);
		}

		NodeUtils.addToDisplayList(root, copyList.extractFirstList());
		NodeUtils.addToDisplayList(root, copyList.extractSecondList());
	}

	/** Copy all values from the matching source node to the target node. */
	@Override
	public void visit(IConQATNode targetNode) throws ConQATException {
		String id = targetNode.getId();
		if (nodeIdMapping != null) {
			id = nodeIdMapping.applyTransformation(id);
		}
		IConQATNode sourceNode = map.get(id);
		if (sourceNode == null) {
			return;
		}
		NodeUtils.copyValues(copyList, sourceNode, targetNode);
	}

	/** {@inheritDoc} */
	@Override
	protected ETargetNodes getDefaultTargetNodes() {
		return ETargetNodes.ALL;
	}

}