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

import java.util.ArrayList;
import java.util.Set;

import org.conqat.lib.commons.assertion.CCSMPre;
import org.conqat.lib.commons.assertion.PreconditionException;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.IdentityHashSet;
import org.conqat.lib.commons.collections.UnmodifiableSet;

/**
 * A Simulink inport. An outport can be connected to multiple
 * {@link SimulinkLine}s.
 * 
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 6D7D65E97E1955B74478F704E4F95427
 */
public class SimulinkOutPort extends SimulinkPortBase {

	/** The lines connected to this port. */
	private final Set<SimulinkLine> lines = new IdentityHashSet<SimulinkLine>();

	/**
	 * Create simulink outport.
	 * 
	 * @param block
	 *            The block this port belongs to.
	 * @param index
	 *            The port index. This may be a number or a string like 'enable'
	 */
	public SimulinkOutPort(SimulinkBlock block, String index) {
		super(block, index);
		block.addOutPort(this);
	}

	/**
	 * Add line connected to this port. This is only called from the
	 * {@link SimulinkLine}.
	 * 
	 * @throws PreconditionException
	 *             if this port is already connected to the line or the line's
	 *             source port does not match this port.
	 */
	/* package */void addLine(SimulinkLine line)
			throws IllegalArgumentException {
		CCSMPre.isFalse(lines.contains(line),
				"Line is already connected to this port.");
		CCSMPre
				.isTrue(line.getSrcPort() == this,
						"Line's port does not match.");

		lines.add(line);
	}

	/**
	 * Get lines connected to this port.
	 */
	public UnmodifiableSet<SimulinkLine> getLines() {
		return CollectionUtils.asUnmodifiable(lines);
	}

	/**
	 * Remove line. This is only called from the {@link SimulinkLine}.
	 * 
	 * @throws PreconditionException
	 *             if the provided line is not connected to this port
	 */
	/* package */void removeLine(SimulinkLine line)
			throws IllegalArgumentException {
		CCSMPre.isTrue(lines.contains(line), "Line is not connected to port.");
		lines.remove(line);
	}

	/** {@inheritDoc} */
	@Override
	public void remove() {
		getBlock().removeOutPort(this);
		for (SimulinkLine line : new ArrayList<SimulinkLine>(lines)) {
			line.remove();
		}
		super.remove();
	}

}