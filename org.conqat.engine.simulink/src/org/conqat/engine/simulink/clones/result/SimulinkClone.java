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
package org.conqat.engine.simulink.clones.result;

import java.util.ArrayList;
import java.util.List;

import org.conqat.lib.commons.clone.DeepCloneException;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.UnmodifiableList;
import org.conqat.engine.commons.node.ConQATNodeBase;
import org.conqat.engine.commons.node.IRemovableConQATNode;
import org.conqat.lib.simulink.model.SimulinkBlock;
import org.conqat.lib.simulink.model.SimulinkLine;

/**
 * A class representing a clone in one or two Simulink models.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 35176 $
 * @ConQAT.Rating GREEN Hash: 9E9352AE9896E170001032246BAFFD26
 */
public class SimulinkClone extends ConQATNodeBase implements
		IRemovableConQATNode {

	/** The parent node. */
	private SimulinkCloneResultNode parent;

	/** The id of this clone. */
	private final int id;

	/** All lists of blocks. */
	private final List<UnmodifiableList<SimulinkBlock>> blockLists = new ArrayList<UnmodifiableList<SimulinkBlock>>();

	/** All lists of lines. */
	private final List<UnmodifiableList<SimulinkLine>> lineLists = new ArrayList<UnmodifiableList<SimulinkLine>>();

	/** Constructor. */
	public SimulinkClone(int id) {
		this.id = id;
	}

	/** Copy constructor. */
	private SimulinkClone(SimulinkClone clone) throws DeepCloneException {
		super(clone);
		id = clone.id;
		blockLists.addAll(clone.blockLists);
		lineLists.addAll(clone.lineLists);
	}

	/** Adds the given list of blocks to this clone. */
	public void addBlocksLinesPair(List<SimulinkBlock> blocks,
			List<SimulinkLine> lines) {
		if (blocks.isEmpty()) {
			throw new IllegalArgumentException(
					"May not add empty set of blocks!");
		}
		if (!blockLists.isEmpty() && blockLists.get(0).size() != blocks.size()) {
			throw new IllegalArgumentException(
					"All added block lists must have same size!");
		}
		if (!lineLists.isEmpty() && lineLists.get(0).size() != lines.size()) {
			throw new IllegalArgumentException(
					"All added line lists must have same size!");
		}

		blockLists.add(CollectionUtils.asUnmodifiable(blocks));
		lineLists.add(CollectionUtils.asUnmodifiable(lines));
	}

	/** {@inheritDoc} */
	@Override
	public IRemovableConQATNode[] getChildren() {
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public void remove() {
		if (parent != null) {
			parent.removeChild(this);
			parent = null;
		}
	}

	/** {@inheritDoc} */
	@Override
	public String getId() {
		return "Clone " + Integer.toString(id);
	}

	/** {@inheritDoc} */
	@Override
	public String getName() {
		return getId();
	}

	/** {@inheritDoc} */
	@Override
	public SimulinkCloneResultNode getParent() {
		return parent;
	}

	/** {@inheritDoc} */
	@Override
	public boolean hasChildren() {
		return false;
	}

	/** Sets the parent of this node. */
	public void setParent(SimulinkCloneResultNode parent) {
		this.parent = parent;
	}

	/** {@inheritDoc} */
	@Override
	public SimulinkClone deepClone() throws DeepCloneException {
		return new SimulinkClone(this);
	}

	/** Returns the lists of blocks. */
	public UnmodifiableList<UnmodifiableList<SimulinkBlock>> getBlockLists() {
		return CollectionUtils.asUnmodifiable(blockLists);
	}

	/** Returns the lists of lines. */
	public UnmodifiableList<UnmodifiableList<SimulinkLine>> getLineLists() {
		return CollectionUtils.asUnmodifiable(lineLists);
	}

}