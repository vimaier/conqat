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

import static org.conqat.lib.simulink.model.SimulinkConstants.PARAM_Name;
import static org.conqat.lib.simulink.model.SimulinkConstants.PARAM_id;
import static org.conqat.lib.simulink.model.SimulinkConstants.PARAM_intersection;
import static org.conqat.lib.simulink.model.SimulinkConstants.PARAM_linkNode;
import static org.conqat.lib.simulink.model.SimulinkConstants.PARAM_machine;
import static org.conqat.lib.simulink.model.SimulinkConstants.PARAM_name;
import static org.conqat.lib.simulink.model.SimulinkConstants.PARAM_treeNode;
import static org.conqat.lib.simulink.model.SimulinkConstants.SECTION_chart;
import static org.conqat.lib.simulink.model.SimulinkConstants.SECTION_data;
import static org.conqat.lib.simulink.model.SimulinkConstants.SECTION_dst;
import static org.conqat.lib.simulink.model.SimulinkConstants.SECTION_event;
import static org.conqat.lib.simulink.model.SimulinkConstants.SECTION_junction;
import static org.conqat.lib.simulink.model.SimulinkConstants.SECTION_machine;
import static org.conqat.lib.simulink.model.SimulinkConstants.SECTION_src;
import static org.conqat.lib.simulink.model.SimulinkConstants.SECTION_state;
import static org.conqat.lib.simulink.model.SimulinkConstants.SECTION_target;
import static org.conqat.lib.simulink.model.SimulinkConstants.SECTION_transition;

import java.util.HashMap;
import java.util.Map;

import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.logging.ILogger;
import org.conqat.lib.simulink.model.ParameterizedElement;
import org.conqat.lib.simulink.model.SimulinkModel;
import org.conqat.lib.simulink.model.stateflow.IStateflowNodeContainer;
import org.conqat.lib.simulink.model.stateflow.StateflowChart;
import org.conqat.lib.simulink.model.stateflow.StateflowData;
import org.conqat.lib.simulink.model.stateflow.StateflowDeclBase;
import org.conqat.lib.simulink.model.stateflow.StateflowDeclContainerBase;
import org.conqat.lib.simulink.model.stateflow.StateflowElementBase;
import org.conqat.lib.simulink.model.stateflow.StateflowEvent;
import org.conqat.lib.simulink.model.stateflow.StateflowJunction;
import org.conqat.lib.simulink.model.stateflow.StateflowMachine;
import org.conqat.lib.simulink.model.stateflow.StateflowNodeBase;
import org.conqat.lib.simulink.model.stateflow.StateflowState;
import org.conqat.lib.simulink.model.stateflow.StateflowTarget;
import org.conqat.lib.simulink.model.stateflow.StateflowTransition;
import org.conqat.lib.simulink.util.SimulinkUtils;

/**
 * This class builds the Stateflow part of the Simulink models.
 * 
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 8D86B1001E57284F732C5C468337D87A
 */
/* package */class StateflowBuilder {

	/** Logger. */
	private final ILogger logger;

	/** Maps from id to Statflow elements. */
	private final HashMap<String, StateflowElementBase<?>> elements = new HashMap<String, StateflowElementBase<?>>();

	/** The model. */
	private final SimulinkModel model;

	/**
	 * Create new Stateflow builder.
	 * 
	 * @param model
	 *            model the parsed Stateflow parts belong to.
	 * @param logger
	 *            logger
	 */
	public StateflowBuilder(SimulinkModel model, ILogger logger) {
		this.model = model;
		this.logger = logger;
	}

	/**
	 * Build Stateflow parts based on Stateflow section from MDL file.
	 * 
	 * @param stateflowSection
	 *            the section from the MDL file that describes the Stateflow
	 *            part
	 * @throws SimulinkModelBuildingException
	 *             if a problem occurred during building the Stateflow part
	 */
	public void buildStateflow(MDLSection stateflowSection)
			throws SimulinkModelBuildingException {

		buildMachine(stateflowSection.getFirstSubSection(SECTION_machine));

		// the Stateflow section in the MDL file is organized in a
		// non-hierachical manner, therefore we simply process one item after
		// each other, store it and build the relations in a second step
		for (MDLSection section : stateflowSection.getSubSections()) {
			StateflowElementBase<?> element = createElementFromSectionName(section
					.getName());
			if (element != null) {
				process(section, element);
			}
		}

		for (StateflowElementBase<?> element : elements.values()) {
			buildRelation(element);
		}

		for (MDLSection transition : stateflowSection
				.getSubSections(SECTION_transition)) {
			buildTransition(transition);
		}
	}

	/**
	 * Create Stateflow element from MDL section name. This does not create
	 * elements for all sections as some, e.g. transitions are created
	 * elsewhere.
	 * 
	 * @return <code>null</code> for unknown section name.
	 */
	private StateflowElementBase<?> createElementFromSectionName(String name) {
		if (name.equals(SECTION_chart)) {
			return new StateflowChart();
		}
		if (name.equals(SECTION_state)) {
			return new StateflowState();
		}
		if (name.equals(SECTION_junction)) {
			return new StateflowJunction();
		}
		if (name.equals(SECTION_event)) {
			return new StateflowEvent();
		}
		if (name.equals(SECTION_data)) {
			return new StateflowData();
		}
		if (name.equals(SECTION_target)) {
			return new StateflowTarget();
		}
		return null;
	}

	/** Build machine. */
	private void buildMachine(MDLSection machineSection)
			throws SimulinkModelBuildingException {
		if (machineSection == null) {
			throw new SimulinkModelBuildingException(
					"No Stateflow machine defined!");
		}

		StateflowMachine machine = new StateflowMachine(model);
		process(machineSection, machine);
	}

	/**
	 * Recursively add parameter defined in the section to the Stateflow element
	 * and store it in the map {@link #elements}.
	 * 
	 * @throws SimulinkModelBuildingException
	 *             if id of the element could be determined or multiple elements
	 *             with the same id were detected.
	 */
	private void process(MDLSection section, StateflowElementBase<?> element)
			throws SimulinkModelBuildingException {
		addParameters(section, element);

		String id = element.getParameter(PARAM_id);

		if (id == null) {
			throw new SimulinkModelBuildingException("Element has no id.",
					section);
		}
		if (elements.containsKey(id)) {
			throw new SimulinkModelBuildingException(
					"Duplicate id " + id + ".", section);
		}
		elements.put(id, element);
	}

	/**
	 * Recursively add parameter defined in the section to the element.
	 */
	private void addParameters(MDLSection section, ParameterizedElement element) {
		for (Map.Entry<String, String> parameter : section
				.getParameterMapRecursively().entrySet()) {
			element.setParameter(parameter.getKey(), parameter.getValue());
		}
	}

	/** Build relation for Stateflow element. */
	private void buildRelation(StateflowElementBase<?> element)
			throws SimulinkModelBuildingException {
		if (element instanceof StateflowState) {
			buildNodeRelation((StateflowState) element, PARAM_treeNode);
			return;
		}
		if (element instanceof StateflowJunction) {
			buildNodeRelation((StateflowJunction) element, PARAM_linkNode);
			return;
		}
		if (element instanceof StateflowEvent) {
			buildEventRelation((StateflowEvent) element);
			return;
		}
		if (element instanceof StateflowData) {
			buildDataRelation((StateflowData) element);
			return;
		}
		if (element instanceof StateflowTarget) {
			buildTargetRelation((StateflowTarget) element);
			return;
		}
		if (element instanceof StateflowChart) {
			buildChartRelation((StateflowChart) element);
			return;
		}
		if (element instanceof StateflowMachine) {
			// the relation for the machine is already built by
			// StateflowMachine.setSimulinkModel()
			return;
		}

		CCSMAssert.fail("Unkown case: " + element.getClass().getName());
	}

	/** Build node relation. */
	private void buildNodeRelation(StateflowNodeBase node, String relationParam)
			throws SimulinkModelBuildingException {
		StateflowElementBase<?> relatedElement = getRelatedElement(node,
				relationParam);
		if (!(relatedElement instanceof IStateflowNodeContainer<?>)) {
			throw new SimulinkModelBuildingException(relatedElement
					+ " cannot be parent of " + node);
		}
		IStateflowNodeContainer<?> parent = (IStateflowNodeContainer<?>) relatedElement;
		parent.addNode(node);
	}

	/**
	 * Get element related to the given element.
	 * 
	 * @param element
	 *            relationship origin
	 * @param relationshipParam
	 *            parameter that specifies the relationship in the MDL file.
	 * @return the related element
	 * @throws SimulinkModelBuildingException
	 *             if relationship could not be established.
	 */
	private StateflowElementBase<?> getRelatedElement(
			StateflowElementBase<?> element, String relationshipParam)
			throws SimulinkModelBuildingException {
		String array = element.getParameter(relationshipParam);
		if (array == null) {
			throw new SimulinkModelBuildingException("Relationsship parameter "
					+ relationshipParam + " not found for element with id "
					+ element.getStateflowId() + ".");
		}
		String[] relationship = SimulinkUtils.getStringParameterArray(array);
		if (relationship.length == 0) {
			throw new SimulinkModelBuildingException("Relationsship parameter "
					+ relationshipParam + " not found for element with id "
					+ element.getStateflowId() + ".");
		}
		return elements.get(relationship[0]);
	}

	/** Build event relation. */
	private void buildEventRelation(StateflowEvent element)
			throws SimulinkModelBuildingException {
		StateflowDeclContainerBase<?> parent = determineParent(element);
		parent.addEvent(element);
	}

	/** Build relation for data. */
	private void buildDataRelation(StateflowData element)
			throws SimulinkModelBuildingException {
		StateflowDeclContainerBase<?> parent = determineParent(element);
		parent.addData(element);
	}

	/** Determine parent of a Stateflow declaration (event or data). */
	private StateflowDeclContainerBase<?> determineParent(
			StateflowDeclBase element) throws SimulinkModelBuildingException {
		StateflowElementBase<?> relatedElement = getRelatedElement(element,
				PARAM_linkNode);
		if (!(relatedElement instanceof StateflowDeclContainerBase<?>)) {
			throw new SimulinkModelBuildingException(relatedElement
					+ " cannot be parent of " + element);
		}
		return (StateflowDeclContainerBase<?>) relatedElement;
	}

	/** Build target relation. */
	private void buildTargetRelation(StateflowTarget element)
			throws SimulinkModelBuildingException {
		StateflowElementBase<?> relatedElement = getRelatedElement(element,
				PARAM_linkNode);
		StateflowMachine parent = castToMachine(relatedElement, element);
		parent.addTarget(element);
	}

	/** Build relation for charts. */
	private void buildChartRelation(StateflowChart element)
			throws SimulinkModelBuildingException {
		StateflowElementBase<?> relatedElement = elements.get(element
				.getParameter(PARAM_machine));
		StateflowMachine parent = castToMachine(relatedElement, element);
		String fqName = model.getParameter(PARAM_Name) + "/"
				+ element.getParameter(PARAM_name);
		parent.addChart(fqName, element);
	}

	/**
	 * Cast <code>machineElement</code> to machine and check that it is the
	 * machine associated with the Simulink model.
	 * 
	 * @param element
	 *            the element related to the machine (this is used for possible
	 *            error message only)
	 * @throws SimulinkModelBuildingException
	 *             if <code>machineElement</code> does not refer to the only
	 *             existant machine.
	 */
	private StateflowMachine castToMachine(
			StateflowElementBase<?> machineElement,
			StateflowElementBase<?> element)
			throws SimulinkModelBuildingException {
		if (machineElement != model.getStateflowMachine()) {
			throw new SimulinkModelBuildingException(element
					+ " must belong to machine " + model.getStateflowMachine());
		}
		return (StateflowMachine) machineElement;
	}

	/** Build transition. */
	private void buildTransition(MDLSection section)
			throws SimulinkModelBuildingException {
		String srcId = getId(section, SECTION_src);
		String dstId = getId(section, SECTION_dst);

		if (srcId == null && dstId == null) {
			logger.warn("Found null->null transition. Ignoring transition.");
			return;
		}

		if (dstId == null) {
			logger
					.warn("Found transition without destination. Ignoring transition.");
			return;
		}
		StateflowNodeBase dstNode = getNode(dstId, section);

		// initialize default transition
		StateflowTransition transition;
		if (srcId == null) {
			transition = new StateflowTransition(dstNode);
		} else {
			transition = new StateflowTransition(getNode(srcId, section),
					dstNode);
		}

		addParameters(section, transition);

		copyIntersection(section, SECTION_src, transition);
		copyIntersection(section, SECTION_dst, transition);
	}

	/**
	 * Copies the intersection parameter from the src/dst section to the
	 * transition (using src/dst prefix).
	 */
	private void copyIntersection(MDLSection section, String subSectionName,
			StateflowTransition transition) {
		MDLSection subSection = section.getFirstSubSection(subSectionName);
		transition.setParameter(subSectionName + "_" + PARAM_intersection,
				subSection.getParameter(PARAM_intersection));
	}

	/**
	 * Get parameter 'id' in the first sub section with a given name.
	 */
	private String getId(MDLSection section, String subSectionName)
			throws SimulinkModelBuildingException {
		MDLSection subSection = section.getFirstSubSection(subSectionName);

		if (subSection == null) {
			throw new SimulinkModelBuildingException("Section " + section
					+ " has no child " + subSectionName);
		}

		return subSection.getParameter(PARAM_id);
	}

	/**
	 * Get Stateflow node with given id.
	 * 
	 * @throws SimulinkModelBuildingException
	 *             if no node was found for the given id.
	 */
	private StateflowNodeBase getNode(String id, MDLSection section)
			throws SimulinkModelBuildingException {
		StateflowElementBase<?> element = elements.get(id);

		if (element == null) {
			throw new SimulinkModelBuildingException(
					"Stateflow element with id " + id + " not found.", section);
		}

		if (!(element instanceof StateflowNodeBase)) {
			throw new SimulinkModelBuildingException(
					"Only Stateflow nodes can be source or destination of transitions.",
					section);
		}

		return (StateflowNodeBase) element;
	}
}