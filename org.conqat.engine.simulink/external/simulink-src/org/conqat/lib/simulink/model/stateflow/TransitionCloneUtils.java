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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.conqat.lib.commons.collections.IdentityHashSet;
import org.conqat.lib.simulink.util.SimulinkUtils;

/**
 * Support class to handle the cloning of transitions.
 * 
 * @author hummelb
 * @author $Author: deissenb $
 * @version $Rev: 35226 $
 * @ConQAT.Rating GREEN Hash: 0304DFE7CD92E4F01B4E0573B0977537
 */
/* package */class TransitionCloneUtils {

	/**
	 * Clone all transitions for the given <code>orig</code> container which are
	 * not completely contained in one of its subnodes.
	 */
	public static void cloneTransitions(IStateflowNodeContainer<?> orig,
			IStateflowNodeContainer<?> clone) {
		List<StateflowTransition> transitions = new ArrayList<StateflowTransition>();
		extractInTransitionsRecursive(orig, transitions);

		Map<String, StateflowNodeBase> idToNode = new HashMap<String, StateflowNodeBase>();
		fillIdToNodeMap(clone, idToNode);

		// also include container if it is a state
		if (orig instanceof StateflowNodeBase) {
			transitions.addAll(((StateflowNodeBase) orig).getInTransitions());
			idToNode.put(clone.getStateflowId(), (StateflowNodeBase) clone);
		}

		for (StateflowTransition transition : transitions) {
			duplicateTransition(transition, orig, clone, idToNode);
		}
	}

	/** Duplicate the given transition. */
	private static void duplicateTransition(StateflowTransition transition,
			IStateflowNodeContainer<?> orig, IStateflowNodeContainer<?> clone,
			Map<String, StateflowNodeBase> idToNode) {

		StateflowNodeBase dstNode = idToNode.get(transition.getDst()
				.getStateflowId());
		if (dstNode == null) {
			// outside of current scope
			return;
		}

		// default transition?
		if (transition.getSrc() == null) {
			// only clone, if it affects this state, otherwise clone in
			// substate
			if (transition.getDst() == orig) {
				SimulinkUtils.copyParameters(transition,
						new StateflowTransition(dstNode));
			}
			return;
		}

		StateflowNodeBase srcNode = idToNode.get(transition.getSrc()
				.getStateflowId());
		if (srcNode == null) {
			// outside of current scope
			return;
		}

		// only clone if this was not done in a subnode (which would be the
		// lowest common ancestor)
		if (getLowestCommonAncestor(srcNode, dstNode) == clone) {
			SimulinkUtils.copyParameters(transition, new StateflowTransition(
					srcNode, dstNode));
		}
	}

	/**
	 * Adds all incoming transitions of all nodes in the given container and all
	 * subnodes (if present) to the given list.
	 */
	private static void extractInTransitionsRecursive(
			IStateflowNodeContainer<?> container,
			List<StateflowTransition> transitions) {

		for (StateflowNodeBase node : container.getNodes()) {
			transitions.addAll(node.getInTransitions());
			if (node instanceof IStateflowNodeContainer<?>) {
				extractInTransitionsRecursive(
						(IStateflowNodeContainer<?>) node, transitions);
			}
		}
	}

	/**
	 * Fills the provided map from node ids to nodes for all descendant nodes of
	 * the given container.
	 */
	private static void fillIdToNodeMap(IStateflowNodeContainer<?> container,
			Map<String, StateflowNodeBase> idToNode) {

		for (StateflowNodeBase node : container.getNodes()) {
			idToNode.put(node.getStateflowId(), node);
			if (node instanceof IStateflowNodeContainer<?>) {
				fillIdToNodeMap((IStateflowNodeContainer<?>) node, idToNode);
			}
		}
	}

	/**
	 * Returns the lowest common ancestor of the two elements or null if none
	 * exists.
	 */
	private static IStateflowElement<?> getLowestCommonAncestor(
			IStateflowElement<?> elem1, IStateflowElement<?> elem2) {

		Set<IStateflowElement<?>> ancestors1 = new IdentityHashSet<IStateflowElement<?>>();
		while (elem1 != null) {
			ancestors1.add(elem1);
			elem1 = elem1.getParent();
		}

		while (elem2 != null) {
			if (ancestors1.contains(elem2)) {
				return elem2;
			}
			elem2 = elem2.getParent();
		}

		return null;
	}
}