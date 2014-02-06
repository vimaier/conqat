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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.conqat.lib.commons.assertion.CCSMPre;
import org.conqat.lib.commons.assertion.PreconditionException;
import org.conqat.lib.commons.clone.DeepCloneException;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.TwoDimHashMap;
import org.conqat.lib.commons.collections.UnmodifiableSet;
import org.conqat.lib.simulink.model.stateflow.StateflowBlock;
import org.conqat.lib.simulink.model.stateflow.StateflowChart;
import org.conqat.lib.simulink.model.stateflow.StateflowMachine;
import org.conqat.lib.simulink.util.SimulinkUtils;

/**
 * A Simulink model a specialized Simulink block that primarily maintains the
 * default parameters of blocks, annotations and lines. See the
 * {@linkplain org.conqat.lib.simulink.model package documentation} for details
 * on the parameter mechanism.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 35217 $
 * @ConQAT.Rating GREEN Hash: 77782D25CA49459AAA08358357461F86
 */
public class SimulinkModel extends SimulinkBlock {

	/**
	 * Block parameter defaults. This maps from (block type x parameter name) to
	 * parameter value.
	 */
	private final TwoDimHashMap<String, String, String> blockTypeDefaultParams = new TwoDimHashMap<String, String, String>();

	/**
	 * Block parameter defaults. This maps from parameter name to parameter
	 * value.
	 */
	private final HashMap<String, String> blockDefaultParams = new HashMap<String, String>();

	/**
	 * Annotation parameter defaults. This maps from parameter name to parameter
	 * value.
	 */
	private final HashMap<String, String> annotationDefaultsParams = new HashMap<String, String>();

	/**
	 * Line parameter defaults. This maps from parameter name to parameter
	 * value.
	 */
	private final HashMap<String, String> lineDefaultParams = new HashMap<String, String>();

	/** Flag marks libraries. */
	private final boolean isLibrary;

	/** Stateflow machine of this model. May be <code>null</code>. */
	private StateflowMachine stateflowMachine;

	/** Origin id. */
	private final String originId;

	/**
	 * Create new model.
	 */
	public SimulinkModel(boolean isLibrary, String originId) {
		this.isLibrary = isLibrary;
		this.originId = originId;
	}

	/** This copy constructor clones the whole model. */
	protected SimulinkModel(SimulinkModel origModel) throws DeepCloneException {
		super(origModel);
		originId = origModel.originId;
		isLibrary = origModel.isLibrary;

		// Clone type-specific block parameter defaults
		blockTypeDefaultParams.putAll(origModel.blockTypeDefaultParams);

		// Clone block parameter defaults
		blockDefaultParams.putAll(origModel.blockDefaultParams);

		// Clone annotation parameter defaults
		annotationDefaultsParams.putAll(origModel.annotationDefaultsParams);

		// Clone line parameter defaults
		lineDefaultParams.putAll(origModel.lineDefaultParams);

		// Clone machine
		if (origModel.stateflowMachine != null) {
			stateflowMachine = new StateflowMachine(origModel.stateflowMachine,
					this);
			for (StateflowChart chart : origModel.stateflowMachine.getCharts()) {
				createLink(chart);
			}
		}
	}

	/** Set annotation default parameter. */
	public void setAnnotationDefaultParameter(String name, String value) {
		annotationDefaultsParams.put(name, value);
	}

	/**
	 * Set a default parameter for all blocks.
	 */
	public void setBlockDefaultParameter(String name, String value) {
		blockDefaultParams.put(name, value);
	}

	/**
	 * Set default parameter for blocks of a specified type.
	 */
	public void setBlockTypeDefaultParameter(String type, String name,
			String value) {
		blockTypeDefaultParams.putValue(type, name, value);
	}

	/** Set default parameter for lines. */
	public void setLineDefaultParameter(String name, String value) {
		lineDefaultParams.put(name, value);
	}

	/** Deep clone this model. */
	@Override
	public SimulinkModel deepClone() throws DeepCloneException {
		return new SimulinkModel(this);
	}

	/**
	 * Get string that identifies the origin of this model. This can, e.g., be a
	 * uniform path to the resource. Its actual content depends on how the model
	 * gets constructed. The origin id can be null.
	 */
	public String getOriginId() {
		return originId;
	}

	/** Get default annotation parameter. */
	public String getAnnotationDefaultParameter(String name) {
		return annotationDefaultsParams.get(name);
	}

	/** Get names of annotation default parameters. */
	public UnmodifiableSet<String> getAnnotationDefaultParameterNames() {
		return CollectionUtils
				.asUnmodifiable(annotationDefaultsParams.keySet());
	}

	/**
	 * Get a block specified by its full qualified name. The name must start
	 * with the models name. This returns <code>null</code> if the block was not
	 * found.
	 */
	public SimulinkBlock getBlock(String id) {

		List<String> names = SimulinkUtils.splitSimulinkId(id);

		// if the the first name is not the models name, return null (ensure
		// there is a first before)
		if (names.isEmpty() || !names.get(0).equals(getName())) {
			return null;
		}

		SimulinkBlock block = this;

		for (int i = 1; i < names.size(); i++) {
			// names are unormalized
			block = block.getSubBlock(names.get(i));
			if (block == null) {
				return null;
			}
		}

		return block;
	}

	/**
	 * Get block default parameter.
	 */
	public String getBlockDefaultParameter(String name) {
		return blockDefaultParams.get(name);
	}

	/**
	 * Get named default parameter for a given type. If a type-specific
	 * parameter is defined, it is returned. Otherwise the block default (
	 * {@link #getBlockDefaultParameter(String)}) is returned.
	 */
	public String getTypeBlockDefaultParameter(String type, String name) {
		String value = blockTypeDefaultParams.getValue(type, name);
		if (value == null) {
			return getBlockDefaultParameter(name);
		}
		return value;
	}

	/**
	 * Get names of block default parameters.
	 */
	public UnmodifiableSet<String> getBlockDefaultParameterNames() {
		return CollectionUtils.asUnmodifiable(blockDefaultParams.keySet());
	}

	/**
	 * Get all default parameter names for a given type. This includes the block
	 * defaults ({@link #getBlockDefaultParameterNames()}).
	 */
	public Set<String> getBlockDefaultParameterNames(String type) {
		HashSet<String> parameterNames = new HashSet<String>();
		parameterNames.addAll(blockTypeDefaultParams.getSecondKeys(type));
		parameterNames.addAll(blockDefaultParams.keySet());
		return parameterNames;
	}

	/** Returns the name of the model. */
	@Override
	public String getId() {
		return SimulinkUtils.escape(getName());
	}

	/** Get default line parameter. */
	public String getLineDefaultParameter(String name) {
		return lineDefaultParams.get(name);
	}

	/** Get default line parameter names. */
	public UnmodifiableSet<String> getLineDefaultParameterNames() {
		return CollectionUtils.asUnmodifiable(lineDefaultParams.keySet());
	}

	/** Returns itself. */
	@Override
	public SimulinkModel getModel() {
		return this;
	}

	/**
	 * Get Stateflow machine of this model (may be <code>null</code>).
	 */
	public StateflowMachine getStateflowMachine() {
		return stateflowMachine;
	}

	/** Returns {@link SimulinkConstants#TYPE_Model}. */
	@Override
	public String getType() {
		return SimulinkConstants.TYPE_Model;
	}

	/** Is this model a library? */
	public boolean isLibrary() {
		return isLibrary;
	}

	/**
	 * Set Stateflow machine. This is not expected to be called by the user, but
	 * only by the constructors of {@link StateflowMachine}.
	 * 
	 * @throws PreconditionException
	 *             if this model already has a machine of if the machine does
	 *             not belong to this model.
	 */
	public void setStateflowMachine(StateflowMachine machine) {
		if (machine != null) {
			CCSMPre.isTrue(stateflowMachine == null,
					"This model already has a Stateflow machine.");
			CCSMPre
					.isTrue(machine.getModel() == this,
							"Can be called only for the machine that belongs to this model");
		}

		stateflowMachine = machine;
	}

	/** Create line between chart and Stateflow block (during deep cloning). */
	private void createLink(StateflowChart origChart) {
		StateflowBlock block = (StateflowBlock) getBlock(origChart
				.getStateflowBlock().getId());
		StateflowChart cloneChart = block.getChart();
		stateflowMachine.addChart(block.getId(), cloneChart);
	}

	/**
	 * This throws a {@link UnsupportedOperationException} as models cannot have
	 * parents.
	 */
	@Override
	protected void setParent(SimulinkBlock parent) {
		throw new UnsupportedOperationException("Models cannot have parents.");
	}

}