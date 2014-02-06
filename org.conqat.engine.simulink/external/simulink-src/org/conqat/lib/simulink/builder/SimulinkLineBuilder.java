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

import static org.conqat.lib.simulink.model.SimulinkConstants.PARAM_DstBlock;
import static org.conqat.lib.simulink.model.SimulinkConstants.PARAM_DstPort;
import static org.conqat.lib.simulink.model.SimulinkConstants.PARAM_SrcBlock;
import static org.conqat.lib.simulink.model.SimulinkConstants.PARAM_SrcPort;
import static org.conqat.lib.simulink.model.SimulinkConstants.SECTION_Branch;
import static org.conqat.lib.simulink.model.SimulinkConstants.SECTION_Line;
import org.conqat.lib.commons.logging.ILogger;
import org.conqat.lib.simulink.model.SimulinkBlock;
import org.conqat.lib.simulink.model.SimulinkInPort;
import org.conqat.lib.simulink.model.SimulinkLine;
import org.conqat.lib.simulink.model.SimulinkOutPort;

/**
 * This class is responsible for building the lines between Simulink blocks as
 * defined in the MDL file. This class does not maintain an actual state but is
 * implemented in a non-static way to provide a logger.
 * 
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: C67E666A678A48813AFEB4258AE47BFA
 */
/* package */class SimulinkLineBuilder {

	/** The logger. */
	private final ILogger logger;

	/**
	 * Create line builder.
	 * 
	 * @param logger
	 *            the logger.
	 */
	public SimulinkLineBuilder(ILogger logger) {
		this.logger = logger;
	}

	/**
	 * Build all lines within a Simulink subsystem.
	 * 
	 * @param section
	 *            MDL section that describes the subsystem
	 * @param simulinkBlock
	 *            Simulink block that represents the subsystem
	 * @throws SimulinkModelBuildingException
	 *             if any error occurrs.
	 */
	public void buildLines(MDLSection section, SimulinkBlock simulinkBlock)
			throws SimulinkModelBuildingException {
		for (MDLSection line : section.getSubSections(SECTION_Line)) {
			buildLine(line, simulinkBlock);
		}
	}

	/**
	 * Build a single line.
	 * 
	 * @param lineSection
	 *            section that describes the line.
	 * @param simulinkBlock
	 *            Simulink block that represents the subsystem
	 * @throws SimulinkModelBuildingException
	 *             if any error occurrs.
	 */
	private void buildLine(MDLSection lineSection, SimulinkBlock simulinkBlock)
			throws SimulinkModelBuildingException {

		SimulinkOutPort srcPort = determineSrcPort(lineSection, simulinkBlock);
		// missing src port is logged before
		if (srcPort == null) {
			return;
		}

		// If this line has no branches, create line and exit
		if (!lineSection.hasSubSection(SECTION_Branch)) {
			SimulinkInPort dstPort = determineDstPort(lineSection,
					simulinkBlock);
			// missing dst port is logged before
			if (dstPort == null) {
				return;
			}
			// constructor creates and connects line
			SimulinkLine line = new SimulinkLine(srcPort, dstPort);
			SimulinkModelBuilder.addParameters(line, lineSection);
			return;
		}

		// Flag to check if we found at least one destination for this line
		boolean dstFound = false;

		// Only leaf branches contain destinations
		for (MDLSection branchSection : lineSection
				.getLeafSections(SECTION_Branch)) {
			if (branchSection.hasParameter(PARAM_DstBlock)) {
				SimulinkInPort dstPort = determineDstPort(branchSection,
						simulinkBlock);
				// missing dst port is logged before
				if (dstPort == null) {
					continue;
				}
				// constructor creates and connects line
				SimulinkLine line = new SimulinkLine(srcPort, dstPort);
				while (branchSection != lineSection) {
					SimulinkModelBuilder.addParameters(line, branchSection);
					branchSection = branchSection.getParentSection();
				}
				SimulinkModelBuilder.addParameters(line, lineSection);
				dstFound = true;
			} else {
				logger.info(branchSection
						+ " is a leave branch without destination block. "
						+ "Ignoring branch.");
			}
		}

		if (!dstFound) {
			logger.info(lineSection + " in block " + simulinkBlock
					+ " has no destination." + " Ignoring line.");
		}
	}

	/**
	 * Determine soruce port of a line.
	 * 
	 * @param section
	 *            that describes the source part of a line.
	 * @param simulinkBlock
	 *            the block this destination belongs to
	 * @return the src port or <code>null</code> if the section does not specify
	 *         a src port. This is logged as info.
	 * @throws SimulinkModelBuildingException
	 *             if an error occurrs
	 */
	private SimulinkOutPort determineSrcPort(MDLSection section,
			SimulinkBlock simulinkBlock) throws SimulinkModelBuildingException {
		String srcBlockName = section.getParameter(PARAM_SrcBlock);

		// some model contain lines without source. Ignore them.
		if (srcBlockName == null) {
			logger.info(section + " in block " + simulinkBlock
					+ " has no source." + " Ignoring line.");
			return null;
		}

		// Determine source block
		SimulinkBlock srcBlock = simulinkBlock.getSubBlock(srcBlockName);
		if (srcBlock == null) {
			throw new SimulinkModelBuildingException("Line " + section
					+ " refers to unknown block " + srcBlockName);
		}

		// Determine src port
		String srcPortIndex = section.getParameter(PARAM_SrcPort);
		SimulinkOutPort srcPort = srcBlock.getOutPort(srcPortIndex);
		if (srcPort == null) {
			throw new SimulinkModelBuildingException(section
					+ " refers to unknown source port " + srcPortIndex);
		}

		return srcPort;
	}

	/**
	 * Determine destination port of a line.
	 * 
	 * @param section
	 *            that describes the destination part of a line. This may be a
	 *            line or a branch section.
	 * @param simulinkBlock
	 *            the block this destination belongs to
	 * @return the dst port or <code>null</code> if the section does not specify
	 *         a dst port. This is logged as info.
	 * @throws SimulinkModelBuildingException
	 *             if an error occurrs
	 */
	private SimulinkInPort determineDstPort(MDLSection section,
			SimulinkBlock simulinkBlock) throws SimulinkModelBuildingException {

		// get destination block.
		String dstBlockName = section.getParameter(PARAM_DstBlock);

		if (dstBlockName == null) {
			logger.info(section + " in block " + simulinkBlock
					+ " has no destination." + " Ignoring line.");
			return null;
		}

		SimulinkBlock dstBlock = simulinkBlock.getSubBlock(dstBlockName);
		if (dstBlock == null) {
			throw new SimulinkModelBuildingException(section
					+ " refers to unknown destination block " + dstBlockName
					+ ".");
		}

		String dstPortIndex = section.getParameter(PARAM_DstPort);
		SimulinkInPort dstPort = dstBlock.getInPort(dstPortIndex);
		if (dstPort == null) {
			throw new SimulinkModelBuildingException(section
					+ " refers to unknown destination port " + dstPortIndex
					+ ".");
		}
		return dstPort;
	}
}