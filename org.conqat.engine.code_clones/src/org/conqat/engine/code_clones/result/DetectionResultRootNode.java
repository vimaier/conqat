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
package org.conqat.engine.code_clones.result;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.conqat.engine.code_clones.core.CloneClass;
import org.conqat.engine.commons.node.ConQATNodeBase;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeConstants;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.lib.commons.clone.DeepCloneException;

/**
 * Root node for a "list" of clone classes.
 * <p>
 * In order to have the different keys contained in a detection result in one
 * place, they are all defined in this class.
 * 
 * @author $Author: juergens $
 * @version $Rev: 43751 $
 * @ConQAT.Rating GREEN Hash: 647553F0FD8EAC8DF19545FEC147418F
 */
public class DetectionResultRootNode extends ConQATNodeBase {

	/** The list of children. */
	private final List<CloneClassNode> children = new ArrayList<CloneClassNode>();

	/** Create new result node. */
	public DetectionResultRootNode(Comparator<CloneClass> comparator) {
		// Hide root node
		setValue(NodeConstants.HIDE_ROOT, true);
		setValue(NodeConstants.COMPARATOR,
				new CloneClassNode.CloneClassNodeComparator(comparator));

		// Add clone and clone class metrics to display list
		NodeUtils.addToDisplayList(this,
				CloneListBuilderBase.NORMALIZED_LENGTH,
				CloneListBuilderBase.CARDINALITY, CloneListBuilderBase.VOLUME,
				CloneListBuilderBase.CLONE_START_LINE,
				CloneListBuilderBase.CLONE_LENGTH_IN_LINES,
				CloneListBuilderBase.GAP_COUNT);
	}

	/** Copy constructor. */
	private DetectionResultRootNode(DetectionResultRootNode node)
			throws DeepCloneException {
		super(node);
		for (CloneClassNode c : children) {
			addChild(c.deepClone());
		}
	}

	/** Add a child node to this node. */
	/* package */void addChild(CloneClassNode node) {
		children.add(node);
		node.setParent(this);
	}

	/** {@inheritDoc} */
	@Override
	public DetectionResultRootNode deepClone() throws DeepCloneException {
		return new DetectionResultRootNode(this);
	}

	/** {@inheritDoc} */
	@Override
	public String getName() {
		return "Clone Detection Result";
	}

	/** Return constant string "Clone Root" */
	@Override
	public String getId() {
		return "Clone Classes Root";
	}

	/** {@inheritDoc} */
	@Override
	public CloneClassNode[] getChildren() {
		return children.toArray(new CloneClassNode[children.size()]);
	}

	/** {@inheritDoc} */
	@Override
	public IConQATNode getParent() {
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public boolean hasChildren() {
		return !children.isEmpty();
	}

	/** Remove the given node. */
	/* package */void removeNode(CloneClassNode node) {
		children.remove(node);
	}
}