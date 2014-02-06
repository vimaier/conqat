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

import static org.conqat.lib.simulink.model.SimulinkConstants.SECTION_Library;
import static org.conqat.lib.simulink.model.SimulinkConstants.SECTION_Model;
import static org.conqat.lib.simulink.model.SimulinkConstants.SECTION_Stateflow;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.List;

import java_cup.runtime.Symbol;

import org.conqat.lib.commons.logging.ILogger;
import org.conqat.lib.simulink.model.ParameterizedElement;
import org.conqat.lib.simulink.model.SimulinkConstants;
import org.conqat.lib.simulink.model.SimulinkModel;

/**
 * Main Simulink/Stateflow model building class.
 * 
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 1E952923EC72711BF0BEEF5C520E514B
 */
public class SimulinkModelBuilder {

	/** Reader to read model. */
	private final Reader reader;

	/** Logger. */
	private final ILogger logger;

	/** Origin id. */
	private final String originId;

	/**
	 * Create model builder.
	 * 
	 * @param reader
	 *            reader to read model from
	 * @param logger
	 *            logger for reporting anomalies. You may use SimpleLogger here.
	 * @param originId
	 *            the origin id for the model. See
	 *            {@link SimulinkModel#getOriginId()}
	 */
	public SimulinkModelBuilder(Reader reader, ILogger logger, String originId) {
		this.reader = reader;
		this.logger = logger;
		this.originId = originId;
	}

	/**
	 * Create model builder.
	 * 
	 * @param file
	 *            file to parse
	 * @param logger
	 *            logger for reporting anomalies. You may use SimpleLogger here.
	 * @param originId
	 *            the origin id for the model. See
	 *            {@link SimulinkModel#getOriginId()}
	 * 
	 * @throws FileNotFoundException
	 *             if file was not found.
	 */
	public SimulinkModelBuilder(File file, ILogger logger, String originId)
			throws FileNotFoundException {
		this(new FileReader(file), logger, originId);
	}

	/**
	 * Create model builder. Origin id of the model is set to null.
	 * 
	 * @param reader
	 *            reader to read model from
	 * @param logger
	 *            logger for reporting anomalies. You may use SimpleLogger here.
	 */
	public SimulinkModelBuilder(Reader reader, ILogger logger) {
		this(reader, logger, null);
	}

	/**
	 * Create model builder. Origin id of the model is set to null.
	 * 
	 * @param file
	 *            file to parse
	 * @param logger
	 *            logger for reporting anomalies. You may use SimpleLogger here.
	 * 
	 * @throws FileNotFoundException
	 *             if file was not found.
	 */
	public SimulinkModelBuilder(File file, ILogger logger)
			throws FileNotFoundException {
		this(file, logger, null);
	}

	/**
	 * Build model.
	 * 
	 * @return the model
	 * 
	 * @throws SimulinkModelBuildingException
	 *             if a parsing error occurred.
	 */
	public SimulinkModel buildModel() throws SimulinkModelBuildingException {
		MDLSection simulinkFile = parseFile();

		// get section that holds Simulink model to determine model name and
		// type
		MDLSection modelSection = getSimulinkModelSection(simulinkFile);
		SimulinkModel model = new SimulinkModel(modelSection.getName().equals(
				SECTION_Library), originId);
		addParameters(model, modelSection);

		// build Stateflow machine first
		MDLSection stateflowSection = simulinkFile
				.getFirstSubSection(SECTION_Stateflow);
		if (stateflowSection != null) {
			new StateflowBuilder(model, logger)
					.buildStateflow(stateflowSection);
		}

		new SimulinkBuilder(model, logger).buildSimulink(modelSection);

		return model;
	}

	/**
	 * Determine the section that holds the Simulink model. This may be
	 * {@link SimulinkConstants#SECTION_Model} or
	 * {@link SimulinkConstants#SECTION_Library}</code>.
	 * 
	 * @param simulinkFile
	 *            the Simulink file
	 * @throws SimulinkModelBuildingException
	 *             if no or multiple {@link SimulinkConstants#SECTION_Model}/
	 *             {@link SimulinkConstants#SECTION_Library}</code> were found
	 */
	private static MDLSection getSimulinkModelSection(MDLSection simulinkFile)
			throws SimulinkModelBuildingException {
		List<MDLSection> namedBlocks = simulinkFile
				.getSubSections(SECTION_Model);

		if (namedBlocks.isEmpty()) {
			namedBlocks = simulinkFile.getSubSections(SECTION_Library);
		}

		if (namedBlocks.isEmpty() || namedBlocks.size() > 1) {
			throw new SimulinkModelBuildingException(
					"Model must have exactly one Model or Library block.");
		}

		return namedBlocks.get(0);
	}

	/**
	 * Parse Simulink file.
	 * 
	 * @throws SimulinkModelBuildingException
	 *             if an exception occurred during parsing.
	 */
	private MDLSection parseFile() throws SimulinkModelBuildingException {
		MDLScanner scanner = new MDLScanner(reader);
		MDLParser parser = new MDLParser(scanner, logger);
		Symbol sym;
		try {
			sym = parser.parse();
		} catch (Exception e) {
			throw new SimulinkModelBuildingException(e);
		}
		MDLSection mdlFile = (MDLSection) sym.value;
		return mdlFile;
	}

	/**
	 * Add all parameters defined in a section to a Simulink block. The
	 * {@link SimulinkConstants#PARAM_Points} parameter is treated specially
	 * here. This parameter stores layout information and this is merged instead
	 * of overwritten. This behavior is required to deal with the hierarchy in
	 * lines caused by branches.
	 */
	/* package */static void addParameters(ParameterizedElement element,
			MDLSection section) {
		for (String name : section.getParameterNames()) {
			// be smart for some special parameters
			if (SimulinkConstants.PARAM_Points.equals(name)) {
				String value = element
						.getParameter(SimulinkConstants.PARAM_Points);
				String newValue = section
						.getParameter(SimulinkConstants.PARAM_Points);
				if (value == null) {
					value = newValue;
				} else {
					// prepend value
					value = newValue.substring(0, newValue.length() - 1) + "; "
							+ value.substring(1);
				}
				element.setParameter(SimulinkConstants.PARAM_Points, value);
			} else {
				element.setParameter(name, section.getParameter(name));
			}
		}
	}

}