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

import org.conqat.engine.code_clones.core.Clone;
import org.conqat.engine.code_clones.core.utils.EBooleanStoredValue;
import org.conqat.engine.commons.node.ConQATNodeBase;
import org.conqat.engine.commons.node.IRemovableConQATNode;
import org.conqat.lib.commons.clone.DeepCloneException;

/**
 * This class represents a single clone. It works as an adapter between
 * {@link Clone}s and {@link org.conqat.engine.commons.node.IConQATNode}s.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43764 $
 * @ConQAT.Rating GREEN Hash: 39D946EAA97548382B861778F1C9A998
 */
public class CloneNode extends ConQATNodeBase {

	/** Clone represented by this CloneNode */
	private final Clone clone;

	/**
	 * Path prefix of clone element that gets pruned, since it is the same for
	 * all clones.
	 */
	private final String rootPath;

	/** The parent for this node. */
	private CloneClassNode parent = null;

	/** Create clone node from clone. */
	public CloneNode(Clone clone, String rootPath) {
		this.clone = clone;
		this.rootPath = rootPath;

		setValue(CloneListBuilderBase.CLONE_START_LINE, clone.getLocation()
				.getRawStartLine());
		setValue(CloneListBuilderBase.CLONE_LENGTH_IN_LINES, clone
				.getLocation().getRawEndLine());
		setValue(CloneListBuilderBase.GAP_COUNT, clone.getGapPositions().size());
		setValue(CloneListBuilderBase.COVERED,
				EBooleanStoredValue.COVERED.getValue(clone));
	}

	/** Copy constructor. */
	private CloneNode(CloneNode node) throws DeepCloneException {
		super(node);
		clone = node.clone;
		rootPath = node.rootPath;
	}

	/** {@inheritDoc} */
	@Override
	public String getId() {
		return clone.getUniformPath() + ":"
				+ clone.getLocation().getRawStartLine() + "-"
				+ clone.getLocation().getRawEndLine();
	}

	/** {@inheritDoc} */
	@Override
	public String getName() {
		return clone.getUniformPath();
	}

	/** {@inheritDoc} */
	@Override
	public CloneNode deepClone() throws DeepCloneException {
		return new CloneNode(this);
	}

	/** {@inheritDoc} */
	@Override
	public IRemovableConQATNode[] getChildren() {
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public CloneClassNode getParent() {
		return parent;
	}

	/** {@inheritDoc} */
	@Override
	public boolean hasChildren() {
		return false;
	}

	/** Set the parent. */
	/* package */void setParent(CloneClassNode node) {
		parent = node;
	}
}