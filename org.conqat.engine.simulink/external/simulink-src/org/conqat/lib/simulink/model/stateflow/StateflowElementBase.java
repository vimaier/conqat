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
import org.conqat.lib.commons.clone.IDeepCloneable;
import org.conqat.lib.simulink.model.ParameterizedElement;
import org.conqat.lib.simulink.model.SimulinkConstants;

/**
 * Base class for all Stateflow elements.
 * 
 * @param
 * <P>
 * Type of the parent of this node.
 * 
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 24E82341B502F07FC6203BBDD9813FBC
 */
public abstract class StateflowElementBase<P extends IStateflowElement<?>>
		extends ParameterizedElement implements IDeepCloneable,
		IStateflowElement<P> {

	/** The parent element. */
	private P parent;

	/** Create Stateflow element. */
	protected StateflowElementBase() {
		super();
	}

	/** Copy constructor for deep cloning. */
	protected StateflowElementBase(StateflowElementBase<? extends P> orig) {
		super(orig);
	}

	/** {@inheritDoc} */
	@Override
	public P getParent() {
		return parent;
	}

	/** {@inheritDoc} */
	@Override
	public String getStateflowId() {
		return getParameter(SimulinkConstants.PARAM_id);
	}

	/** {@inheritDoc} */
	@Override
	public abstract void remove();

	/** Returns Stateflow element type + id. */
	@Override
	public String toString() {
		return getClass().getSimpleName() + " [" + getStateflowId() + "]";
	}

	/** Set parent of this element. */
	/* package */void setParent(P parent) {
		if (parent != null) {
			CCSMPre.isTrue(this.parent == null,
					"Cannot set parent for element that already has a parent");
		}
		this.parent = parent;
	}
}