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
package org.conqat.lib.simulink.util;

import java.util.Collection;

import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.collections.IdentityHashSet;
import org.conqat.lib.commons.error.NeverThrownRuntimeException;
import org.conqat.lib.commons.visitor.IMeshWalker;
import org.conqat.lib.simulink.model.SimulinkBlock;
import org.conqat.lib.simulink.model.SimulinkElementBase;
import org.conqat.lib.simulink.model.SimulinkInPort;
import org.conqat.lib.simulink.model.SimulinkLine;
import org.conqat.lib.simulink.model.SimulinkModel;
import org.conqat.lib.simulink.model.SimulinkOutPort;
import org.conqat.lib.simulink.model.SimulinkPortBase;
import org.conqat.lib.simulink.model.stateflow.IStateflowNodeContainer;
import org.conqat.lib.simulink.model.stateflow.StateflowBlock;
import org.conqat.lib.simulink.model.stateflow.StateflowDeclContainerBase;
import org.conqat.lib.simulink.model.stateflow.StateflowElementBase;
import org.conqat.lib.simulink.model.stateflow.StateflowMachine;
import org.conqat.lib.simulink.model.stateflow.StateflowNodeBase;
import org.conqat.lib.simulink.model.stateflow.StateflowTransition;

/**
 * Mesh walker for Simulink/Stateflow models.
 * 
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 346C2CAA8E837A0E79BF92815377F773
 */
public class SimulinkModelWalker implements
		IMeshWalker<Object, NeverThrownRuntimeException> {

	/** Gets adjacent elements for all elements of Simulink/Stateflow elements. */
	@Override
	public Collection<Object> getAdjacentElements(Object element) {

		IdentityHashSet<Object> elements = new IdentityHashSet<Object>();

		if (element instanceof SimulinkElementBase) {
			SimulinkElementBase simulinkElement = (SimulinkElementBase) element;
			if (simulinkElement.getParent() != null) {
				elements.add(simulinkElement.getParent());
			}
			elements.add(simulinkElement.getModel());
		}

		if (element instanceof StateflowBlock) {
			StateflowBlock block = (StateflowBlock) element;
			elements.add(block.getChart());
		}

		if (element instanceof SimulinkModel) {
			SimulinkModel model = (SimulinkModel) element;
			if (model.getStateflowMachine() != null) {
				elements.add(model.getStateflowMachine());
			}
		}
		if (element instanceof SimulinkBlock) {
			SimulinkBlock block = (SimulinkBlock) element;
			elements.addAll(block.getSubBlocks());
			elements.addAll(block.getInPorts());
			elements.addAll(block.getOutPorts());
			elements.addAll(block.getAnnotations());
		}
		if (element instanceof StateflowBlock) {
			SimulinkBlock block = (SimulinkBlock) element;
			elements.addAll(block.getSubBlocks());
			elements.addAll(block.getInPorts());
			elements.addAll(block.getOutPorts());
		}
		if (element instanceof SimulinkPortBase) {
			SimulinkPortBase port = (SimulinkPortBase) element;
			elements.add(port.getBlock());
		}
		if (element instanceof SimulinkInPort) {
			SimulinkInPort inPort = (SimulinkInPort) element;
			if (inPort.getLine() != null) {
				elements.add(inPort.getLine());
			}
		}
		if (element instanceof SimulinkOutPort) {
			SimulinkOutPort inPort = (SimulinkOutPort) element;
			elements.addAll(inPort.getLines());
		}
		if (element instanceof SimulinkLine) {
			SimulinkLine line = (SimulinkLine) element;
			elements.add(line.getSrcPort());
			elements.add(line.getDstPort());
		}

		if (element instanceof StateflowElementBase<?>) {
			StateflowElementBase<?> stateflowElement = (StateflowElementBase<?>) element;
			if (stateflowElement.getParent() != null) {
				elements.add(stateflowElement.getParent());
			}
		}

		if (element instanceof StateflowDeclContainerBase<?>) {
			StateflowDeclContainerBase<?> declContainer = (StateflowDeclContainerBase<?>) element;
			elements.addAll(declContainer.getData());
			elements.addAll(declContainer.getEvents());
		}

		if (element instanceof IStateflowNodeContainer<?>) {
			IStateflowNodeContainer<?> nodeContainer = (IStateflowNodeContainer<?>) element;
			elements.addAll(nodeContainer.getNodes());
		}

		if (element instanceof StateflowNodeBase) {
			StateflowNodeBase node = (StateflowNodeBase) element;
			elements.addAll(node.getInTransitions());
			elements.addAll(node.getOutTransitions());
		}

		if (element instanceof StateflowMachine) {
			StateflowMachine machine = (StateflowMachine) element;
			elements.addAll(machine.getTargets());
		}
		if (element instanceof StateflowTransition) {
			StateflowTransition transition = (StateflowTransition) element;
			if (transition.getSrc() != null) {
				elements.add(transition.getSrc());
			}
			elements.add(transition.getDst());
		}

		CCSMAssert.isFalse(elements.contains(null), "Element " + element
				+ " has a null adjancency.");

		return elements;
	}
}