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

import static org.conqat.lib.simulink.model.SimulinkConstants.PARAM_BlockType;
import static org.conqat.lib.simulink.model.SimulinkConstants.SECTION_Annotation;
import static org.conqat.lib.simulink.model.SimulinkConstants.SECTION_AnnotationDefaults;
import static org.conqat.lib.simulink.model.SimulinkConstants.SECTION_Block;
import static org.conqat.lib.simulink.model.SimulinkConstants.SECTION_BlockDefaults;
import static org.conqat.lib.simulink.model.SimulinkConstants.SECTION_BlockParameterDefaults;
import static org.conqat.lib.simulink.model.SimulinkConstants.SECTION_LineDefaults;
import static org.conqat.lib.simulink.model.SimulinkConstants.SECTION_System;

import java.util.List;

import org.conqat.lib.commons.logging.ILogger;
import org.conqat.lib.simulink.model.SimulinkAnnotation;
import org.conqat.lib.simulink.model.SimulinkBlock;
import org.conqat.lib.simulink.model.SimulinkConstants;
import org.conqat.lib.simulink.model.SimulinkModel;
import org.conqat.lib.simulink.model.stateflow.StateflowBlock;
import org.conqat.lib.simulink.model.stateflow.StateflowChart;
import org.conqat.lib.simulink.model.stateflow.StateflowMachine;
import org.conqat.lib.simulink.util.SimulinkUtils;

/**
 * This class is responsible for building the Simulink part (as opposed to
 * Stateflow) of the models. It delegates some building task to sub builders.
 * 
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 3C02FE585401BFD45D18DF0C55807E5F
 * 
 * @see SimulinkLineBuilder
 * @see SimulinkPortBuilder
 */
/* package */class SimulinkBuilder {

	/** Builder for Simulink lines. */
	private final SimulinkLineBuilder lineBuilder;

	/** Builder for Simulink ports. */
	private final SimulinkPortBuilder portBuilder = new SimulinkPortBuilder();

	/** The model to build. */
	private final SimulinkModel model;

	/**
	 * Create new builder.
	 * 
	 * @param model
	 *            model to build
	 * @param logger
	 *            logger
	 * 
	 */
	public SimulinkBuilder(SimulinkModel model, ILogger logger) {
		this.model = model;
		lineBuilder = new SimulinkLineBuilder(logger);
	}

	/**
	 * Build simulink.
	 * 
	 * @param modelSection
	 *            the 'Model' section of the MDL file.
	 * @throws SimulinkModelBuildingException
	 *             if a parsing error occurred.
	 */
	public void buildSimulink(MDLSection modelSection)
			throws SimulinkModelBuildingException {

		// recursively build all blocks
		buildSimulinkBlocks(modelSection, model);

		buildBlockTypeDefaultParams(modelSection);
		buildBlockDefaultParams(modelSection);
		buildLineDefaultParams(modelSection);
		buildAnnotationDefaultParams(modelSection);
	}

	/**
	 * Get 'block' sub section of the specified sections and call
	 * {@link #buildSimulinkBlock(MDLSection, SimulinkBlock)} for each of them.
	 */
	private void buildSimulinkBlocks(MDLSection section, SimulinkBlock parent)
			throws SimulinkModelBuildingException {
		MDLSection systemSection = section.getFirstSubSection(SECTION_System);
		if (systemSection == null) {
			return;
		}
		List<MDLSection> blocks = systemSection.getSubSections(SECTION_Block);
		for (MDLSection block : blocks) {
			buildSimulinkBlock(block, parent);
		}

		lineBuilder.buildLines(systemSection, parent);
		buildAnnotations(systemSection, parent);
	}

	/**
	 * Build Simulink block.
	 * 
	 * @param section
	 *            MDL section that describes the block
	 * @param parent
	 *            parent block
	 * @throws SimulinkModelBuildingException
	 *             if a error occurs
	 */
	private void buildSimulinkBlock(MDLSection section, SimulinkBlock parent)
			throws SimulinkModelBuildingException {

		if (section.getParameter(SimulinkConstants.PARAM_Name) == null) {
			throw new SimulinkModelBuildingException("Block at line "
					+ section.getLineNumber() + " has no name.");
		}

		if (section.getParameter(SimulinkConstants.PARAM_BlockType) == null) {
			throw new SimulinkModelBuildingException("Block at line "
					+ section.getLineNumber() + " has no type.");
		}

		SimulinkBlock simulinkBlock = createBlock(section, parent);

		SimulinkModelBuilder.addParameters(simulinkBlock, section);
		portBuilder.buildPorts(simulinkBlock, section);

		parent.addSubBlock(simulinkBlock);

		// if this block contains a System section also add those parameters
		MDLSection systemSection = section.getFirstSubSection(SECTION_System);
		if (systemSection != null) {
			for (String paramName : systemSection.getParameterNames()) {
				simulinkBlock.setParameter(SECTION_System + "." + paramName,
						systemSection.getParameter(paramName));
			}
		}

		buildSimulinkBlocks(section, simulinkBlock);
	}

	/**
	 * If the model has a Stateflow chart associated with this block, create a
	 * StateflowBlock, otherwise create a SimulinkBlock.
	 */
	private SimulinkBlock createBlock(MDLSection section, SimulinkBlock parent) {
		String name = section.getParameter(SimulinkConstants.PARAM_Name);
		String fqName = parent.getId() + "/" + SimulinkUtils.escape(name);

		StateflowMachine machine = model.getStateflowMachine();
		if (machine != null) {
			StateflowChart chart = machine.getChart(fqName);
			if (chart != null) {
				return new StateflowBlock(chart);
			}
		}

		return new SimulinkBlock();
	}

	/** Build annotations. */
	private void buildAnnotations(MDLSection section,
			SimulinkBlock simulinkBlock) {
		for (MDLSection annotationSection : section
				.getSubSections(SECTION_Annotation)) {
			SimulinkAnnotation annotation = new SimulinkAnnotation();
			SimulinkModelBuilder.addParameters(annotation, annotationSection);
			simulinkBlock.addAnnotation(annotation);
		}
	}

	/** Build block parameter defaults. */
	private void buildBlockTypeDefaultParams(MDLSection modelSection) {
		MDLSection blockDefaults = modelSection
				.getFirstSubSection(SECTION_BlockDefaults);

		if (blockDefaults == null) {
			return;
		}

		for (String name : blockDefaults.getParameterNames()) {
			model.setBlockDefaultParameter(name, blockDefaults
					.getParameter(name));
		}
	}

	/** Build type-specific block parameter defaults. */
	private void buildBlockDefaultParams(MDLSection modelSection) {
		MDLSection blockParameterDefaults = modelSection
				.getFirstSubSection(SECTION_BlockParameterDefaults);

		if (blockParameterDefaults == null) {
			return;
		}

		for (MDLSection childBlock : blockParameterDefaults
				.getSubSections(SECTION_Block)) {
			String type = childBlock.getParameter(PARAM_BlockType);

			for (String name : childBlock.getParameterNames()) {
				// don't add type itself
				if (!name.equals(PARAM_BlockType)) {
					model.setBlockTypeDefaultParameter(type, name, childBlock
							.getParameter(name));
				}
			}
		}
	}

	/** Build line parameter defaults. */
	private void buildLineDefaultParams(MDLSection modelSection) {
		MDLSection lineDefaults = modelSection
				.getFirstSubSection(SECTION_LineDefaults);

		if (lineDefaults == null) {
			return;
		}

		for (String name : lineDefaults.getParameterNames()) {
			model
					.setLineDefaultParameter(name, lineDefaults
							.getParameter(name));
		}
	}

	/** Build annotation parameter defaults. */
	private void buildAnnotationDefaultParams(MDLSection modelSection) {
		MDLSection annotationDefaults = modelSection
				.getFirstSubSection(SECTION_AnnotationDefaults);

		if (annotationDefaults == null) {
			return;
		}

		for (String name : annotationDefaults.getParameterNames()) {
			model.setAnnotationDefaultParameter(name, annotationDefaults
					.getParameter(name));
		}
	}

}