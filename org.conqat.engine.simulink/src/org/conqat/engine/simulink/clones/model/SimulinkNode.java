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
package org.conqat.engine.simulink.clones.model;

import org.conqat.engine.model_clones.model.INode;
import org.conqat.lib.simulink.model.SimulinkBlock;

/**
 * Implementation of the {@link INode} interface for Simulink.
 * 
 * @author $Author:hummelb $
 * @version $Rev: 35176 $
 * @ConQAT.Rating GREEN Hash: 6C8133CAB0CF169DA4882D5C959F5808
 */
public class SimulinkNode implements INode {

	/** The underlying Simulink block. */
	private final SimulinkBlock block;

	/** The normalized representation. */
	private final String normalized;

	/** The weight of this node. */
	private final int weight;
	
	/** Constructor. */
	public /* package */SimulinkNode(SimulinkBlock block, String normalized, int weight) {
		this.block = block;
		this.normalized = normalized;
		this.weight = weight;
	}

	/** {@inheritDoc} */
	@Override
	public String getEquivalenceClassLabel() {
		return normalized;
	}

	/** {@inheritDoc} */
	@Override
	public int getWeight() {
		return weight;
	}
	
	/** Returns the block. */
	public SimulinkBlock getBlock() {
		return block;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "SimulinkNode: " + block.getId();
	}
}