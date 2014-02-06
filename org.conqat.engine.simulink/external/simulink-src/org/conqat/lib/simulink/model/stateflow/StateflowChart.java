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
package org.conqat.lib.simulink.model.stateflow;

import java.util.ArrayList;

import org.conqat.lib.commons.assertion.CCSMPre;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.IdentityHashSet;
import org.conqat.lib.commons.collections.UnmodifiableSet;
import org.conqat.lib.simulink.model.SimulinkConstants;

/**
 * This class represents Stateflow charts. There is a one-to-one association
 * between {@link StateflowBlock}s and {@link StateflowChart}s.
 * 
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: E602861EA264FB758ED2FAC7D1E37382
 */
public class StateflowChart extends
		StateflowDeclContainerBase<StateflowMachine> implements
		IStateflowNodeContainer<StateflowMachine> {

	/** The Stateflow block associated with this chart. */
	private StateflowBlock stateflowBlock;

	/** List of top level nodes. */
	private final IdentityHashSet<StateflowNodeBase> nodes = new IdentityHashSet<StateflowNodeBase>();

	/** Create new Stateflow block. */
	public StateflowChart() {
		super();
	}

	/**
	 * Create new Stateflow chart from existing chart (for deep cloning).
	 */
	/* package */StateflowChart(StateflowChart origChart) {
		super(origChart);

		for (StateflowNodeBase element : origChart.getNodes()) {
			addNode(element.deepClone());
		}

		TransitionCloneUtils.cloneTransitions(origChart, this);
	}

	/**
	 * Add node.
	 */
	@Override
	public void addNode(StateflowNodeBase node) {
		nodes.add(node);
		node.setParent(this);
	}

	/**
	 * Deep clone this chart.
	 */
	@Override
	public StateflowChart deepClone() {
		return new StateflowChart(this);
	}

	/** Get the Stateflow machine this chart belongs to. */
	public StateflowMachine getMachine() {
		return getParent();
	}

	/** Returns the name of the chart. */
	public String getName() {
		return getParameter(SimulinkConstants.PARAM_name);
	}

	/** Returns the nodes of this chart. */
	@Override
	public UnmodifiableSet<StateflowNodeBase> getNodes() {
		return CollectionUtils.asUnmodifiable(nodes);
	}

	/** Get Stateflow block this chart belongs to. */
	public StateflowBlock getStateflowBlock() {
		return stateflowBlock;
	}

	/**
	 * This method throws an {@link UnsupportedOperationException}. You must
	 * remove the associated {@link StateflowBlock} to remove a chart.
	 */
	@Override
	public void remove() {
		throw new UnsupportedOperationException(
				"Cannot remove chart without removing Stateflow block!");
	}

	/** Returns the name of the chart. */
	@Override
	public String toString() {
		return getName();
	}

	/**
	 * Remove all nodes from this chart.
	 */
	/* package */void removeNodes() {
		for (StateflowNodeBase node : new ArrayList<StateflowNodeBase>(nodes)) {
			node.remove();
		}
	}

	/** Remove node. */
	/* package */void removeNode(StateflowNodeBase node) {
		CCSMPre.isTrue(node.getParent() == this,
				"Node does not belong to this chart.");
		nodes.remove(node);
		node.setParent(null);
	}

	/** Set Stateflow block this chart belongs to. */
	/* package */void setStateflowBlock(StateflowBlock stateflowBlock) {
		if (stateflowBlock != null) {
			CCSMPre.isTrue(this.stateflowBlock == null,
					"Cannot set new Stateflow block.");
		}
		this.stateflowBlock = stateflowBlock;
	}
}