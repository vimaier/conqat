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

import java.util.Set;

import org.conqat.lib.commons.assertion.CCSMPre;

/**
 * A Simulink line.
 * 
 * @author hummelb
 * @author $Author: deissenb $
 * @version $Rev: 34736 $
 * @ConQAT.Rating GREEN Hash: 724AAD8ED319E68B360BF9ED94D9C011
 */
public class SimulinkLine extends ParameterizedElement {

	/** The source port of this line. */
	private SimulinkOutPort srcPort;

	/** The target port of this line. */
	private SimulinkInPort dstPort;

	/** Creates a new line. This adds the line to the ports. */
	@SuppressWarnings("null")
	public SimulinkLine(SimulinkOutPort srcPort, SimulinkInPort dstPort) {

		CCSMPre.isFalse(srcPort == null || dstPort == null,
				"Ports may not be null!");

		this.srcPort = srcPort;
		this.dstPort = dstPort;

		srcPort.addLine(this);
		dstPort.setLine(this);
	}

	/** Returns target port. */
	public SimulinkInPort getDstPort() {
		return dstPort;
	}

	/** Get model this line belongs to. */
	public SimulinkModel getModel() {
		return srcPort.getBlock().getModel();
	}

	/** Returns source port. */
	public SimulinkOutPort getSrcPort() {
		return srcPort;
	}

	/** Remove the line from the ports. */
	public void remove() {
		CCSMPre.isFalse(srcPort == null || dstPort == null,
				"May not remove lines twice!");

		srcPort.removeLine(this);
		dstPort.removeLine(this);
		srcPort = null;
		dstPort = null;
	}

	/** Get string representation of the line. */
	@Override
	public String toString() {
		return srcPort + " -> " + dstPort;
	}

	/**
	 * Get line default parameter.
	 */
	@Override
	/* package */String getDefaultParameter(String name) {
		return getModel().getLineDefaultParameter(name);
	}

	/**
	 * Get line default parameter names.
	 */
	@Override
	/* package */Set<String> getDefaultParameterNames() {
		return getModel().getLineDefaultParameterNames();
	}

}