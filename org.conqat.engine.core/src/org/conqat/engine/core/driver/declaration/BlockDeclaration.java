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
package org.conqat.engine.core.driver.declaration;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.conqat.engine.core.driver.error.BlockFileException;
import org.conqat.engine.core.driver.error.DriverException;
import org.conqat.engine.core.driver.error.EDriverExceptionType;
import org.conqat.engine.core.driver.error.EnvironmentException;
import org.conqat.engine.core.driver.error.ErrorLocation;
import org.conqat.engine.core.driver.instance.BlockInstance;
import org.conqat.engine.core.driver.specification.BlockSpecification;
import org.conqat.engine.core.driver.specification.BlockSpecificationParameter;
import org.conqat.engine.core.driver.specification.IConditionalParameter;
import org.conqat.engine.core.driver.specification.SpecificationLoader;
import org.conqat.engine.core.driver.util.Multiplicity;
import org.conqat.engine.core.driver.util.PropertyUtils;
import org.conqat.lib.commons.collections.CounterSet;
import org.conqat.lib.commons.collections.ListMap;

/**
 * This is the declaration of a block. This is really simple as most stuff is
 * handled by the {@link DeclarationBase}.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 42711 $
 * @ConQAT.Rating GREEN Hash: 11744CD6D42E8136FDF43C7757931005
 */
public class BlockDeclaration extends DeclarationBase {

	/**
	 * The specification of this block. This is initialized in
	 * {@link #referenceSpecification()}.
	 */
	private BlockSpecification specification;

	/**
	 * Create a new block declaration.
	 * 
	 * @param name
	 *            block's name as specified in the config file.
	 * @param specificationName
	 *            block-spec name as specified in the config file.
	 * @param surroundingSpecification
	 *            the specification which surrounds this declaration (i.e. its
	 *            "scope").
	 * @param specLoader
	 *            the specification loader used to get the referenced
	 *            specification.
	 */
	public BlockDeclaration(String name, String specificationName,
			BlockSpecification surroundingSpecification,
			SpecificationLoader specLoader) {
		super(name, specificationName, surroundingSpecification, specLoader);
	}

	/**
	 * Create a block declaration from an existing block specification. You
	 * should not call {@link #referenceSpecification()} for the created
	 * BlockDeclaration, as the linkage is created automatically.
	 * 
	 * @param specification
	 *            the specification to create the declaration for.
	 * @param specLoader
	 *            the specification loader used.
	 * @param properties
	 *            additional properties represented as a {@link LinkedHashMap}.
	 *            The key has the form "param.attr" and all values are either
	 *            strings (which are then interpreted) or arbitrary objects,
	 *            which are then passed on directly.
	 */
	public BlockDeclaration(BlockSpecification specification,
			SpecificationLoader specLoader,
			ListMap<String, ? extends Object> properties)
			throws DriverException {
		super(specification.getName(), specification.getName(), specification
				.getErrorLocation(), specLoader);

		this.specification = specification;

		@SuppressWarnings("unchecked")
		Map<String, ListMap<String, Object>> preprocessed = PropertyUtils
				.splitProperties((ListMap<String, Object>) properties);

		List<DeclarationParameter> parameters = initializeParameters(preprocessed);

		CounterSet<String> parameterCount = new CounterSet<String>();
		for (DeclarationParameter parameter : parameters) {
			parameterCount.inc(parameter.getName());
		}
		checkMultiplicities(parameterCount, specification);

		linkOutputsAndParameters(specification);

		// make attributes available
		for (DeclarationParameter parameter : parameters) {
			for (DeclarationAttribute attribute : parameter.getAttributes()) {
				if (attribute.getValueObject() == null) {
					attribute.initConstant();
				}
			}
		}
	}

	/** Initializes the list of parameters. */
	private List<DeclarationParameter> initializeParameters(
			Map<String, ListMap<String, Object>> preprocessed)
			throws EnvironmentException {
		List<DeclarationParameter> parameters = new ArrayList<DeclarationParameter>();
		parameters.add(createConditionParameter(this, Boolean.toString(true)));

		for (String param : preprocessed.keySet()) {
			ListMap<String, Object> attributeMap = preprocessed.get(param);
			int length = PropertyUtils.extractListLengths(param, attributeMap);
			for (int i = 0; i < length; ++i) {
				DeclarationParameter parameter = new DeclarationParameter(
						param, this);
				for (String attr : attributeMap.getKeys()) {
					Object value = attributeMap.getCollection(attr).get(i);
					if (value instanceof String) {
						parameter.addAttribute(new DeclarationAttribute(attr,
								(String) value, parameter));
					} else {
						parameter.addAttribute(new DeclarationAttribute(attr,
								value, parameter));
					}
				}
				parameters.add(parameter);
			}
		}
		setParameters(parameters);

		return parameters;
	}

	/** Creates the synthetic parameter for conditional execution. */
	public static DeclarationParameter createConditionParameter(
			IDeclaration declaration, String condition) {
		DeclarationParameter parameter = new DeclarationParameter(
				IConditionalParameter.PARAMETER_NAME, declaration);
		String invert = Boolean.toString(false);
		if (condition.startsWith("!")) {
			condition = condition.substring(1);
			invert = Boolean.toString(true);
		}

		parameter.addAttribute(new DeclarationAttribute(
				IConditionalParameter.VALUE_ATTRIBUTE, condition, parameter));
		parameter.addAttribute(new DeclarationAttribute(
				IConditionalParameter.INVERT_ATTRIBUTE, invert, parameter));
		return parameter;
	}

	/**
	 * Checks the multiplicities of parameters (given as counter set) and throws
	 * exception is case of wrong value.
	 */
	private void checkMultiplicities(CounterSet<String> parameterCount,
			BlockSpecification specification) throws EnvironmentException {
		for (BlockSpecificationParameter param : specification.getParameters()) {
			Multiplicity m = param.getMultiplicity();
			int count = parameterCount.getValue(param.getName());
			if (count < m.getLower()) {
				throw new EnvironmentException(
						EDriverExceptionType.PARAMETER_OCCURS_NOT_OFTEN_ENOUGH,
						"Too little occurrences of parameter "
								+ param.getName() + " in block "
								+ specification.getName(),
						ErrorLocation.UNKNOWN);
			}
			if (count > m.getUpper()) {
				throw new EnvironmentException(
						EDriverExceptionType.PARAMETER_OCCURS_TOO_OFTEN,
						"Too many occurrences of parameter " + param.getName()
								+ " in block " + specification.getName(),
						ErrorLocation.UNKNOWN);
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	public BlockSpecification getSpecification() {
		return specification;
	}

	/** {@inheritDoc} */
	@Override
	public void referenceSpecification() throws DriverException {
		if (specification != null) {
			throw new IllegalStateException("May initialize only once!");
		}

		specification = getSpecificationLoader().getBlockSpecification(
				getSpecificationName());
		if (specification == null) {
			throw new BlockFileException(
					EDriverExceptionType.UNKNOWN_BLOCK_SPECIFICATION,
					"Unknown block '" + getSpecificationName() + "'.", this);
		}

		linkOutputsAndParameters(specification);
	}

	/** {@inheritDoc} */
	@Override
	public BlockInstance instantiate(BlockInstance parentInstance) {

		if (specification == null) {
			throw new IllegalStateException(
					"May not instantiate unless the type info has been referenced!");
		}

		return new BlockInstance(this, parentInstance);
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "block '" + getName() + "'";
	}
}