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

import org.conqat.lib.commons.assertion.CCSMPre;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.IdentityHashSet;
import org.conqat.lib.commons.collections.UnmodifiableSet;
import org.conqat.lib.simulink.model.SimulinkConstants;

/**
 * This class represents Stateflow states.
 * 
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 2C0D8E233C214E34344123E62279A5CD
 */
public class StateflowState extends StateflowNodeBase implements
		IStateflowNodeContainer<IStateflowNodeContainer<?>> {

	/** Set of child states. */
	private final IdentityHashSet<StateflowNodeBase> nodes = new IdentityHashSet<StateflowNodeBase>();

	/** Create state. */
	public StateflowState() {
		super();
	}

	/** Create new state from existing one (for deep cloning). */
	private StateflowState(StateflowState orig) {
		super(orig);

		for (StateflowNodeBase child : orig.nodes) {
			addNode(child.deepClone());
		}

		TransitionCloneUtils.cloneTransitions(orig, this);
	}

	/** Add a node to this state. */
	@Override
	public void addNode(StateflowNodeBase node) {
		nodes.add(node);
		node.setParent(this);
	}

	/** Get state label. */
	public String getLabel() {
		return getParameter(SimulinkConstants.PARAM_labelString);
	}

	/** Get child nodes. */
	@Override
	public UnmodifiableSet<StateflowNodeBase> getNodes() {
		return CollectionUtils.asUnmodifiable(nodes);
	}

	/** Remove node. */
	/* package */void removeNode(StateflowNodeBase node) {
		CCSMPre.isTrue(node.getParent() == this,
				"Node does not belong to this chart.");
		nodes.remove(node);
		node.setParent(null);
	}

	/** Returns label and id. */
	@Override
	public String toString() {
		return getLabel() + " (" + getStateflowId() + ")";
	}

	/** Deep clone this state. */
	@Override
	public StateflowState deepClone() {
		return new StateflowState(this);
	}
}