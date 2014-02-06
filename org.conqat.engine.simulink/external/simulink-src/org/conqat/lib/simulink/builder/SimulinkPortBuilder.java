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
package org.conqat.lib.simulink.builder;

import static org.conqat.lib.simulink.model.SimulinkConstants.PARAM_Ports;
import org.conqat.lib.commons.enums.EnumUtils;
import org.conqat.lib.simulink.model.SimulinkBlock;
import org.conqat.lib.simulink.model.SimulinkInPort;
import org.conqat.lib.simulink.model.SimulinkOutPort;
import org.conqat.lib.simulink.util.SimulinkUtils;

/**
 * This class is resonsible for building the ports of Simulink blocks as defined
 * in the MDL file. This class does not maintain state but is implemented in a
 * non-static way to match the implementation of {@link SimulinkLineBuilder}
 * (anyway we might need a Logger later on).
 * 
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 1C59577380D62F2F1A7FA652A5D16008
 */
/* package */class SimulinkPortBuilder {

	/**
	 * The MDL file uses a rather intransparent way of specifying the ports of
	 * block: The parameter 'Ports' points to array that has 0-8 elements. Each
	 * array entry specifies the number of ports of a certain type. This
	 * enumeration is meant to make this more explicit and to prevent an
	 * implementation with a heavy if/then/else-density.
	 * 
	 * The following list explains the port type for each index.
	 * 
	 * <pre>
	 * 0 : inports
	 * 1 : outports
	 * 2 : enable ports
	 * 3 : trigger ports
	 * 4 : state
	 * 5 : LConn
	 * 6 : RConn
	 * 7 : ifaction                             
	 * </pre>
	 */
	private enum EPortType {

		/** Inport */
		INPORT(true, false, true),

		/** Outport */
		OUTPORT(true, false, false),

		/** Enable port */
		ENABLE(false, false, true),

		/** Trigger port */
		TRIGGER(false, false, true),

		/** State port */
		STATE(false, false, false),

		/** LConn port */
		LCONN(false, true, true),

		/** RConn port */
		RCONN(false, true, true),

		/** ifaction port */
		IFACTION(false, false, true);

		/** Does this port allow multiple instances at one block? */
		private final boolean multiple;

		/** Is this port supported? */
		private final boolean unsupported;

		/** Is this a inport (<code>true</code>) or an outport (<code>false</code>)? */
		private final boolean in;

		/**
		 * Create new port type.
		 * 
		 * @param multiple
		 *            allows multiple instances
		 * @param unsupported
		 *            is supported
		 * @param in
		 *            a inport (<code>true</code>) or an outport (<code>false</code>)
		 */
		private EPortType(boolean multiple, boolean unsupported, boolean in) {
			this.multiple = multiple;
			this.unsupported = unsupported;
			this.in = in;
		}

		/**
		 * Creates port name. If a port type does not allow multiple instances,
		 * its name is returned otherwise the port number is returned as string.
		 */
		private String createPortName(int portNumber) {
			if (multiple) {
				return String.valueOf(portNumber);
			}
			return name().toLowerCase();
		}

	}

	/**
	 * This method builds the ports of Simulink Blocks.
	 * 
	 * @param simulinkBlock
	 *            the block to determine ports for
	 * @param section
	 *            the section that describes the block
	 * @throws SimulinkModelBuildingException
	 *             if an illegal port constellation was found
	 */
	public void buildPorts(SimulinkBlock simulinkBlock, MDLSection section)
			throws SimulinkModelBuildingException {
		// get port definition
		String portAttribute = section.getParameter(PARAM_Ports);

		// if ports are not defined explicitly, handle special cases.
		if (portAttribute == null) {
			handleUndefinedPorts(simulinkBlock, section);
			return;
		}

		// determine port array from mdl file
		int[] portArray = SimulinkUtils.getIntParameterArray(portAttribute);

		for (int i = 0; i < portArray.length; i++) {
			processPort(i, portArray[i], simulinkBlock, section);
		}
	}

	/**
	 * Build port.
	 * 
	 * @param portIndex
	 *            index of the port (refers to {@link EPortType}).
	 * @param portCount
	 *            number of ports
	 * @param simulinkBlock
	 *            block this ports belong to
	 * @param section
	 *            MDL section that describes the block
	 * @throws SimulinkModelBuildingException
	 *             if an illegal port setup was found
	 */
	private void processPort(int portIndex, int portCount,
			SimulinkBlock simulinkBlock, MDLSection section)
			throws SimulinkModelBuildingException {

		// nothing to do for zero ports
		if (portCount == 0) {
			return;
		}

		// check for an unknown port type
		if (portIndex >= EPortType.values().length) {
			throw new SimulinkModelBuildingException("Block " + simulinkBlock
					+ " at " + section + " has an unknown port with index "
					+ portIndex);
		}

		// determine port type
		EPortType portType = EPortType.values()[portIndex];

		// check that port type is supported
		if (portType.unsupported) {
			throw new SimulinkModelBuildingException("Block " + simulinkBlock
					+ " at " + section + " has an unsupported port type "
					+ portType.name() + ".");
		}

		// check if there is more than port for a non-multiple port type
		if (portCount > 1 && !portType.multiple) {
			throw new SimulinkModelBuildingException("Block " + simulinkBlock
					+ " at " + section + " has an " + portCount + " "
					+ portType.name() + " whereas only one is supported.");
		}

		// everything is ok, so add ports
		for (int i = 1; i <= portCount; i++) {
			addPort(simulinkBlock, portType, i);
		}
	}

	/**
	 * Add port.
	 * 
	 * @param simulinkBlock
	 *            block to add port to
	 * @param portType
	 *            port type
	 * @param portNumber
	 *            number of this port
	 */
	private void addPort(SimulinkBlock simulinkBlock, EPortType portType,
			int portNumber) {
		if (portType.in) {
			new SimulinkInPort(simulinkBlock, portType
					.createPortName(portNumber));
		} else {
			new SimulinkOutPort(simulinkBlock, portType
					.createPortName(portNumber));
		}
	}

	/**
	 * For certain block types the MDL file does not specify the blocks. These
	 * cases are handled here.
	 */
	private void handleUndefinedPorts(SimulinkBlock simulinkBlock,
			MDLSection section) {
		EBlockType knownBlockType = EnumUtils.valueOf(EBlockType.class, section
				.getParameter("BlockType"));

		// this is not special block type, we assume it has one input port and
		// one output port
		if (knownBlockType == null) {
			new SimulinkInPort(simulinkBlock, "1");
			new SimulinkOutPort(simulinkBlock, "1");
			return;
		}

		// this is sepcial block type, so add ports as specified in the
		// corresponding enum element
		for (int i = 1; i <= knownBlockType.numInPorts; i++) {
			new SimulinkInPort(simulinkBlock, String.valueOf(i));
		}

		for (int i = 1; i <= knownBlockType.numOutPorts; i++) {
			new SimulinkOutPort(simulinkBlock, String.valueOf(i));
		}

	}
}