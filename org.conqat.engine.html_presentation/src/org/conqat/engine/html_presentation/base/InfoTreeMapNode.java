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
package org.conqat.engine.html_presentation.base;

import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.commons.node.DisplayList;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeConstants;
import org.conqat.engine.core.driver.info.BlockInfo;
import org.conqat.engine.core.driver.info.IInfo;
import org.conqat.engine.core.driver.info.ProcessorInfo;
import org.conqat.engine.html_presentation.color.ColorizerBase;
import org.conqat.lib.commons.clone.DeepCloneException;
import org.conqat.lib.commons.collections.CollectionUtils;

/**
 * Hierarchy for visualizing processor execution times.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating YELLOW Hash: 7BD5ECD3E0B3FE39ABA5FD89FD132999
 */
public class InfoTreeMapNode implements IConQATNode {

	/** The info object this node represents. */
	private final IInfo info;

	/** Parent node. */
	private final IConQATNode parent;

	/** Child list. */
	private final List<IConQATNode> children = new ArrayList<IConQATNode>();

	/** The key used for size- */
	public static final String SIZE_KEY = "size";

	/** The key for execution time */
	private static final String EXECUTION_TIME_KEY = "Execution time";

	/** The key for the processor state */
	private static final String PROCESSOR_STATE_KEY = "ProcessorState";

	/**
	 * Create new node for a block info. This automatically creates all
	 * children.
	 */
	/* package */InfoTreeMapNode(BlockInfo info, IConQATNode parent) {
		this.info = info;
		this.parent = parent;
		initChildren(info);
	}

	/** Add all children. */
	private void initChildren(BlockInfo blockInfo) {
		for (IInfo child : blockInfo.getChildren()) {
			if (child instanceof BlockInfo) {
				BlockInfo blockChild = (BlockInfo) child;
				children.add(new InfoTreeMapNode(blockChild, this));
			} else if (child instanceof ProcessorInfo) {
				ProcessorInfo processorChild = (ProcessorInfo) child;
				children.add(new InfoTreeMapNode(processorChild, this));
			} else {
				throw new IllegalStateException("Unknown info type: "
						+ child.getClass().getName());
			}
		}

	}

	/** Create new node of a processor info. */
	private InfoTreeMapNode(ProcessorInfo info, IConQATNode parent) {
		this.info = info;
		this.parent = parent;
	}

	/** {@inheritDoc} */
	@Override
	public String getName() {
		return info.getDeclarationName();
	}

	/** {@inheritDoc} */
	@Override
	public String getId() {
		return info.getInstanceName();
	}

	/** {@inheritDoc} */
	@Override
	public boolean hasChildren() {
		return !children.isEmpty();
	}

	/** {@inheritDoc} */
	@Override
	public IConQATNode[] getChildren() {
		return CollectionUtils.toArray(children, IConQATNode.class);
	}

	/** {@inheritDoc} */
	@Override
	public Object getValue(String key) {
		if (ColorizerBase.COLOR_KEY_DEFAULT.equals(key)) {
			return info.getState().getColor();
		}
		if (SIZE_KEY.equals(key)) {
			return info.getExecutionTime();
		}
		if (EXECUTION_TIME_KEY.equals(key)) {
			return info.getExecutionTime() + "ms";
		}
		if (PROCESSOR_STATE_KEY.equals(key)) {
			return info.getState().toString();
		}
		if (NodeConstants.DISPLAY_LIST.equals(key)) {
			DisplayList displayList = new DisplayList();
			displayList.addKey(EXECUTION_TIME_KEY, null);
			displayList.addKey(PROCESSOR_STATE_KEY, null);
			return displayList;

		}
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public void setValue(String key, Object value) {
		// not supported
	}

	/** {@inheritDoc} */
	@Override
	public IConQATNode getParent() {
		return parent;
	}

	/** {@inheritDoc} */
	@Override
	public IConQATNode deepClone() throws DeepCloneException {
		throw new DeepCloneException(
				"Deep cloning of this class not supported!");
	}
}