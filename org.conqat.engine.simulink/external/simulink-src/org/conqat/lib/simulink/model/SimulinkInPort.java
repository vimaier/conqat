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

/**
 * A Simulink inport. An inport can be connected to only one
 * {@link SimulinkLine}.
 * 
 * 
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 2A5B688750331E4AB0BF9FA2C1329338
 */
public class SimulinkInPort extends SimulinkPortBase {

	/** The line connected to this port. */
	private SimulinkLine line;

	/**
	 * Create simulink inport.
	 * 
	 * @param block
	 *            The block this port belongs to.
	 * @param index
	 *            The port index. This may be a number or a string like 'enable'
	 */
	public SimulinkInPort(SimulinkBlock block, String index) {
		super(block, index);
		block.addInPort(this);
	}

	/**
	 * Get line connected to this port.
	 * 
	 * @return the line or <code>null</code> if no line is connected.
	 */
	public SimulinkLine getLine() {
		return line;
	}

	/**
	 * Set line connected to this port. This is only called from the
	 * {@link SimulinkLine}.
	 * 
	 * @throws PreconditionException
	 *             if this port already has a line or the line's destination
	 *             port does not match this port.
	 */
	/* package */void setLine(SimulinkLine line)
			throws IllegalArgumentException {
		CCSMPre.isTrue(this.line == null, "Port already has a line");
		CCSMPre
				.isTrue(line.getDstPort() == this,
						"Line's port does not match.");
		this.line = line;
	}

	/**
	 * Remove line. This is only called from the {@link SimulinkLine}.
	 * 
	 * @throws PreconditionException
	 *             if the provided line is not connected to this port
	 */
	/* package */void removeLine(SimulinkLine line)
			throws IllegalArgumentException {
		CCSMPre.isTrue(line != null, "Can not remove null line.");
		CCSMPre.isTrue(line == this.line, "Line does not belong to this port.");
		this.line = null;
	}

	/** {@inheritDoc} */
	@Override
	public void remove() {
		getBlock().removeInPort(this);
		if (line != null) {
			line.remove();
		}
		super.remove();
	}
}