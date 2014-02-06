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
package org.conqat.engine.core.driver.instance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.conqat.engine.core.driver.ConQATInstrumentation;
import org.conqat.engine.core.driver.declaration.BlockDeclaration;
import org.conqat.engine.core.driver.declaration.DeclarationOutput;
import org.conqat.engine.core.driver.declaration.IDeclaration;
import org.conqat.engine.core.driver.specification.BlockSpecification;
import org.conqat.engine.core.driver.specification.BlockSpecificationParameter;
import org.conqat.engine.core.driver.specification.ISpecificationParameter;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.clone.DeepCloneException;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.ListMap;

/**
 * The instance of a block. This class is responsible for instantiating and
 * "wiring up" all contents of the block.
 * <p>
 * Execution is done by executing all contained block and processor instances.
 * Furthermore, an aggregated view on the results is presented.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: EC83CB54D1BA95AA1810B25953A76E28
 */
public class BlockInstance extends InstanceBase<BlockDeclaration> {

	/** The logger. */
	private static Logger logger = Logger.getLogger(BlockInstance.class);

	/**
	 * This maps from declaration outputs (of all child blocks and processors)
	 * to the corresponsing instance outputs.
	 */
	private final Map<DeclarationOutput, InstanceOutput> childOutputInstantiation = new HashMap<DeclarationOutput, InstanceOutput>();

	/**
	 * The list of instances in execution order, i.e. each instances has only
	 * references to instances which are earlier in the list.
	 */
	private final List<IInstance> executionList = new ArrayList<IInstance>();

	/** The outputs for this instance. */
	private final List<InstanceOutput> outputs = new ArrayList<InstanceOutput>();

	/**
	 * Mapping from specification parameters (from the specification for this
	 * block) to its actual instance parameters (which can be more than one).
	 */
	private final ListMap<ISpecificationParameter, InstanceParameter> specInputInstantiation = new ListMap<ISpecificationParameter, InstanceParameter>();

	/**
	 * Creates a new block instance.
	 * 
	 * @param declaration
	 *            the declaration this is based on.
	 * @param parent
	 *            the parent (block) instance.
	 */
	public BlockInstance(BlockDeclaration declaration, BlockInstance parent) {
		super(parent, declaration);

		initSpecInputInstantiation();
		initChildOutputInstantiation();

		for (DeclarationOutput output : getDeclaration().getOutputs()) {
			outputs.add(new InstanceOutput(output, this));
		}
	}

	/** {@inheritDoc} */
	@Override
	protected void doExecute(ExecutionContext contextInfo,
			ConQATInstrumentation instrumentation) {
		collectParameters();

		// execute
		for (IInstance instance : executionList) {
			if (instrumentation.beforeExecute(instance)) {
				instance.execute(contextInfo, instrumentation);
			}
			instrumentation.afterExecute(instance);
		}

		collectResults();
	}

	/** Collects the parameters before execution of children. */
	private void collectParameters() {
		for (InstanceParameter param : getNonSyntheticParameters()) {
			for (InstanceAttribute attr : param.getAttributes()) {
				try {
					attr.prepareValue();
				} catch (DeepCloneException e) {
					logger.error(
							"In attribute "
									+ attr.toString()
									+ ": could not receive value due to cloning errors!",
							e);
				}
			}
		}
	}

	/** Collects the results after execution of children. */
	private void collectResults() {
		for (InstanceOutput output : getOutputs()) {
			try {
				output.copyReferencedResult();
			} catch (DeepCloneException e) {
				logger.error("In output " + output.toString()
						+ ": could not receive value due to cloning errors!", e);
			}
		}
	}

	/** Returns the execution list. */
	public List<IInstance> getExecutionList() {
		return executionList;
	}

	/** {@inheritDoc} */
	@Override
	public List<InstanceOutput> getOutputs() {
		return outputs;
	}

	/**
	 * Returns for a given declaration output (of a child element of this block)
	 * the corresponding instance output.
	 */
	/* package */InstanceOutput getChildOutputInstance(
			DeclarationOutput declarationOutput) {
		return childOutputInstantiation.get(declarationOutput);
	}

	/**
	 * Returns for a given mapping of attribute names to specification
	 * parameters the list of all mappings of attributes to instances of this
	 * parameter. 
	 */
	/* package */List<Map<String, InstanceParameter>> getSpecificationParameterInstances(
			Map<String, BlockSpecificationParameter> referencedSpecificationParameters) {

		List<String> attributeNames = CollectionUtils
				.sort(referencedSpecificationParameters.keySet());

		List<List<InstanceParameter>> instanceParametersLists = new ArrayList<List<InstanceParameter>>();
		int size = 0;
		for (String attributeName : attributeNames) {
			List<InstanceParameter> instanceParameters = specInputInstantiation
					.getCollection(referencedSpecificationParameters
							.get(attributeName));

			if (instanceParametersLists.isEmpty()) {
				size = instanceParameters.size();
			} else {
				CCSMAssert
						.isTrue(size == instanceParameters.size(),
								"All lists should be of same length as enforced in parameter multiplicity inference.");
			}
			instanceParametersLists.add(instanceParameters);
		}

		List<Map<String, InstanceParameter>> result = new ArrayList<Map<String, InstanceParameter>>();
		for (int i = 0; i < size; ++i) {
			Map<String, InstanceParameter> parameterMap = new HashMap<String, InstanceParameter>();
			for (int j = 0; j < attributeNames.size(); ++j) {
				parameterMap.put(attributeNames.get(j), instanceParametersLists
						.get(j).get(i));
			}
			result.add(parameterMap);
		}
		return result;
	}

	/** Instantiate all child elements and register their outputs. */
	private void initChildOutputInstantiation() {
		for (IDeclaration subDeclaration : getDeclaration().getSpecification()
				.getDeclarationList()) {
			IInstance subInstance = subDeclaration.instantiate(this);
			executionList.add(subInstance);

			for (InstanceOutput subOut : subInstance.getOutputs()) {
				childOutputInstantiation.put(subOut.getDeclaration(), subOut);
			}
		}
	}

	/**
	 * Initialize the "inner" view on the parameters. The "outer" view has
	 * already be handled in {@link InstanceBase}'s constructor.
	 */
	@SuppressWarnings("unchecked")
	private void initSpecInputInstantiation() {
		// create list for each spec param
		BlockSpecification blockSpec = getDeclaration().getSpecification();
		for (BlockSpecificationParameter param : blockSpec.getParameters()) {
			specInputInstantiation.addAll(param, Collections.EMPTY_LIST);
		}

		// Fill inst param lists for each spec param
		for (InstanceParameter param : getParameters()) {
			ISpecificationParameter specParam = param.getDeclaration()
					.getSpecificationParameter();
			specInputInstantiation.add(specParam, param);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void disable(EInstanceState disablementState) {
		for (IInstance child : executionList) {
			child.disable(disablementState);
		}
	}
}