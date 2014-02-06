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
package org.conqat.lib.simulink.model;

import org.conqat.lib.commons.assertion.CCSMPre;
import org.conqat.lib.commons.assertion.PreconditionException;
import org.conqat.lib.commons.clone.IDeepCloneable;
import org.conqat.lib.simulink.util.SimulinkUtils;

/**
 * Base class for Simulink elements. This is either a {@link SimulinkAnnotation}
 * or a {@link SimulinkBlock}. The common aspect is that they have a name and a
 * parent.
 * 
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: AA86BF5B0566593060D5954FFA88AFD9
 */
public abstract class SimulinkElementBase extends ParameterizedElement
		implements IDeepCloneable {

	/** The parent of this block. */
	private SimulinkBlock parent;

	/** Create element. */
	protected SimulinkElementBase() {
		super();
	}

	/**
	 * Create element from other element (for deep cloning).
	 */
	protected SimulinkElementBase(SimulinkElementBase other) {
		super(other);
	}

	/**
	 * Get id of this element.
	 */
	public String getId() {
		if (parent != null) {
			return parent.getId() + "/" + SimulinkUtils.escape(getName());
		}
		return SimulinkUtils.escape(getName());
	}

	/** Get the model this element belongs to. */
	public SimulinkModel getModel() {
		if (parent == null) {
			return null;
		}
		return parent.getModel();
	}

	/** Returns the name. */
	public String getName() {
		return getParameter(SimulinkConstants.PARAM_Name);
	}

	/** Returns the parent block (may be null). */
	public SimulinkBlock getParent() {
		return parent;
	}

	/** Remove this element from the model. */
	public void remove() {
		if (parent != null) {
			parent.removeElement(this);
			parent = null;
		}
	}

	/** Get string representation of this block. */
	@Override
	public String toString() {
		return getName();
	}

	/**
	 * Sets the parent for this block.
	 * 
	 * @throws PreconditionException
	 *             if element already has parent or the new parent is
	 *             <code>null</code>.
	 */
	protected void setParent(SimulinkBlock parent) {
		CCSMPre.isTrue(this.parent == null, "Element already has a parent!");
		CCSMPre.isFalse(parent == null, "Parent cannot be null!");
		this.parent = parent;
	}
}