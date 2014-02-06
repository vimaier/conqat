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
import org.conqat.engine.commons.node.ConQATNodeBase;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.IRemovableConQATNode;

/**
 * Result of a simulink clone detection. Basically this is just a list of
 * clones.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 35176 $
 * @ConQAT.Rating GREEN Hash: D467ADAC747BBE6479850401F3670805
 */
public class SimulinkCloneResultNode extends ConQATNodeBase implements
		IRemovableConQATNode {

	/** The children of this node. */
	private final List<SimulinkClone> clones = new ArrayList<SimulinkClone>();

	/** Constructor. */
	public SimulinkCloneResultNode() {
		// nothing to do.
	}

	/** Copy constructor. */
	private SimulinkCloneResultNode(SimulinkCloneResultNode node)
			throws DeepCloneException {
		super(node);
		for (SimulinkClone clone : node.clones) {
			addChild(clone.deepClone());
		}
	}

	/** Add a clone to the list. */
	public void addChild(SimulinkClone clone) {
		clones.add(clone);
		clone.setParent(this);
	}

	/** {@inheritDoc} */
	@Override
	public SimulinkClone[] getChildren() {
		return clones.toArray(new SimulinkClone[clones.size()]);
	}

	/** {@inheritDoc} */
	@Override
	public void remove() {
		// nothing to do; this is root
	}

	/** {@inheritDoc} */
	@Override
	public String getId() {
		return "Simulink Clone Result";
	}

	/** {@inheritDoc} */
	@Override
	public String getName() {
		return getId();
	}

	/** {@inheritDoc} */
	@Override
	public IConQATNode getParent() {
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public boolean hasChildren() {
		return !clones.isEmpty();
	}

	/** {@inheritDoc} */
	@Override
	public SimulinkCloneResultNode deepClone() throws DeepCloneException {
		return new SimulinkCloneResultNode(this);
	}

	/** Remove the given child node. */
	/* package */void removeChild(SimulinkClone clone) {
		clones.remove(clone);
	}
}