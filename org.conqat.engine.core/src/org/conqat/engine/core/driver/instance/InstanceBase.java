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
import java.util.List;
import java.util.Map;

import org.conqat.engine.core.driver.ConQATInstrumentation;
import org.conqat.engine.core.driver.declaration.DeclarationParameter;
import org.conqat.engine.core.driver.declaration.IDeclaration;
import org.conqat.engine.core.driver.specification.BlockSpecificationParameter;
import org.conqat.engine.core.driver.specification.IConditionalParameter;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.clone.DeepCloneException;

/**
 * This is a partial implementation of the {@link IInstance} interface containing
 * code shared between block and processor instances.
 * 
 * @param <D>
 *            The type of declaration referenced by the instance.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 7FE65EE19D422D42E7B1D3DBE91273EE
 */
/* package */abstract class InstanceBase<D extends IDeclaration> implements
		IInstance {

	/**
	 * The parent instance. As only blocks can be used for building hierarchies,
	 * this must be a block instance.
	 */
	private final BlockInstance parent;

	/** The declaration of this instance. */
	private final D declaration;

	/** The list of parameters for this instance. */
	private final List<InstanceParameter> parameters = new ArrayList<InstanceParameter>();

	/**
	 * Creates a new instance and prepares the list of parameters for this
	 * instance from the given declaration.
	 */
	protected InstanceBase(BlockInstance parent, D declaration) {
		this.parent = parent;
		this.declaration = declaration;
		instantiateParameters();
	}

	/** Create instances for all parameters of the declaration. */
	@SuppressWarnings("unchecked")
	private void instantiateParameters() {
		for (DeclarationParameter declParam : declaration.getParameters()) {

			Map<String, BlockSpecificationParameter> referencedSpecificationParameters = declParam
					.getReferencedSpecificationParameters();
			if (referencedSpecificationParameters.isEmpty()) {
				// parameter does not depend on input, so keep as is
				parameters.add(new InstanceParameter(declParam, this,
						Collections.EMPTY_MAP));
			} else {
				// the parameter depends on (potentially) multiple input
				// parameters
				for (Map<String, InstanceParameter> instanceParameterMap : parent
						.getSpecificationParameterInstances(referencedSpecificationParameters)) {
					parameters.add(new InstanceParameter(declParam, this,
							instanceParameterMap));
				}
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	public String getName() {
		if (parent == null) {
			return getDeclaration().getName();
		}
		return parent.getName() + "." + getDeclaration().getName();
	}

	/** {@inheritDoc} */
	@Override
	public List<InstanceParameter> getParameters() {
		return parameters;
	}

	/** Returns the list of non-synthetic parameters. */
	public List<InstanceParameter> getNonSyntheticParameters() {
		List<InstanceParameter> result = new ArrayList<InstanceParameter>();
		for (InstanceParameter parameter : getParameters()) {
			if (!parameter.isSynthetic()) {
				result.add(parameter);
			}
		}
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public BlockInstance getParent() {
		return parent;
	}

	/** {@inheritDoc} */
	@Override
	public D getDeclaration() {
		return declaration;
	}

	/** {@inheritDoc} */
	@Override
	public final void execute(ExecutionContext contextInfo,
			ConQATInstrumentation instrumentation) {
		if (isEnabled()) {
			doExecute(contextInfo, instrumentation);
		}
	}

	/**
	 * Template method called from
	 * {@link #execute(ExecutionContext, ConQATInstrumentation)} to perform the
	 * actual execution.
	 */
	protected abstract void doExecute(ExecutionContext contextInfo,
			ConQATInstrumentation instrumentation);

	/**
	 * Checks whether this unit is enabled. Is not, the correct disablement
	 * state is set and propagated.
	 */
	@SuppressWarnings("null")
	private boolean isEnabled() {
		InstanceParameter conditionParameter = null;
		for (InstanceParameter parameter : parameters) {
			if (IConditionalParameter.PARAMETER_NAME.equals(parameter
					.getDeclaration().getName())) {
				conditionParameter = parameter;
				break;
			}
		}

		// this is enforced by our hard-coded multiplicities for the synthetic
		// parameter
		CCSMAssert.isNotNull(conditionParameter);

		try {
			if (!conditionParameter.prepareAttributes()) {
				disable(EInstanceState.FAILED_DUE_TO_MISSING_INPUT);
				return false;
			}

			Boolean enabled = (Boolean) conditionParameter.getAttributeByName(
					IConditionalParameter.VALUE_ATTRIBUTE).consumeValue();
			Boolean invert = (Boolean) conditionParameter.getAttributeByName(
					IConditionalParameter.INVERT_ATTRIBUTE).consumeValue();

			if (invert) {
				enabled = !enabled;
			}

			if (!enabled) {
				disable(EInstanceState.DISABLED);
				return false;
			}
		} catch (DeepCloneException e) {
			CCSMAssert.fail("This can not happen, as booleans can be cloned!");
		}

		return true;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "Instance '" + getName() + "'";
	}
}