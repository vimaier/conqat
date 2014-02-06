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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.conqat.engine.core.driver.error.BlockFileException;
import org.conqat.engine.core.driver.error.DriverException;
import org.conqat.engine.core.driver.error.EDriverExceptionType;
import org.conqat.engine.core.driver.error.ErrorLocation;
import org.conqat.engine.core.driver.specification.BlockSpecification;
import org.conqat.engine.core.driver.specification.ISpecification;
import org.conqat.engine.core.driver.specification.ISpecificationParameter;
import org.conqat.engine.core.driver.specification.SpecificationLoader;
import org.conqat.engine.core.driver.specification.SpecificationOutput;

/**
 * This is the base class for declarations. Here we handle the management of
 * parameters and outputs and their linking to their specifications.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: F31E979671650EC53B659FC5990E3A41
 */
/* package */abstract class DeclarationBase implements IDeclaration {

	/** The declaration's name as specified in the config file */
	private final String name;

	/** The name of the specification referenced. */
	private final String specificationName;

	/** The specification which surrounds this declaration (i.e. its "scope"). */
	private final BlockSpecification surroundingSpecification;

	/** The specification loader used to get the referenced specification. */
	private final SpecificationLoader specLoader;

	/** The list of outputs. */
	private final List<DeclarationOutput> outputs = new ArrayList<DeclarationOutput>();

	/** Declared parameters of this declaration. */
	private final List<DeclarationParameter> parameters = new ArrayList<DeclarationParameter>();

	/** Error location. */
	private final ErrorLocation errorLocation;

	/**
	 * Create a new declaration base.
	 * 
	 * @param name
	 *            name of the declaration as specified in the config file.
	 * @param specificationName
	 *            the name of the specification.
	 * @param surroundingSpecification
	 *            the specification which surrounds this declaration (i.e. its
	 *            "scope").
	 * @param specLoader
	 *            the specification loader used to get the referenced
	 *            specification.
	 */
	protected DeclarationBase(String name, String specificationName,
			BlockSpecification surroundingSpecification,
			SpecificationLoader specLoader) {
		this.name = name;
		this.specificationName = specificationName;
		this.surroundingSpecification = surroundingSpecification;
		errorLocation = surroundingSpecification.getErrorLocation();
		this.specLoader = specLoader;
	}

	/** Create declaration with explicit error location. */
	protected DeclarationBase(String name, String specificationName,
			ErrorLocation errorLocation, SpecificationLoader specLoader) {
		this.name = name;
		this.specificationName = specificationName;
		surroundingSpecification = null;
		this.errorLocation = errorLocation;
		this.specLoader = specLoader;
	}

	/** {@inheritDoc} */
	@Override
	public String getName() {
		return name;
	}

	/** {@inheritDoc} */
	@Override
	public ErrorLocation getErrorLocation() {
		return errorLocation;
	}

	/** {@inheritDoc} */
	@Override
	public String getSpecificationName() {
		return specificationName;
	}

	/** {@inheritDoc} */
	@Override
	public BlockSpecification getSurroundingSpecification() {
		return surroundingSpecification;
	}

	/**
	 * Returns the specification loader used to get the referenced
	 * specification.
	 */
	protected SpecificationLoader getSpecificationLoader() {
		return specLoader;
	}

	/** {@inheritDoc} */
	@Override
	public List<DeclarationParameter> getParameters() {
		return parameters;
	}

	/** {@inheritDoc} */
	@Override
	public List<DeclarationParameter> getNonSyntheticParameters() {
		List<DeclarationParameter> result = new ArrayList<DeclarationParameter>();
		for (DeclarationParameter parameter : getParameters()) {
			if (!parameter.isSynthetic()) {
				result.add(parameter);
			}
		}
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public List<DeclarationOutput> getOutputs() {
		return outputs;
	}

	/** {@inheritDoc} */
	@Override
	public void setParameters(List<DeclarationParameter> parameters) {
		this.parameters.clear();
		this.parameters.addAll(parameters);
	}

	/**
	 * Link all outputs, parameters, and attributes of this declaration to their
	 * specification or throw an exception if they have no specification (i.e.,
	 * are not supported). The outputs of this declaration are created in this
	 * step as they are not explicitly given in the XML file (but are implicitly
	 * given by the specification).
	 * 
	 * @param specification
	 *            the specification from which to take the information.
	 */
	protected void linkOutputsAndParameters(ISpecification specification)
			throws DriverException {

		Map<SpecificationOutput, DeclarationOutput> outputLookup = appendOutputs(specification);

		for (DeclarationParameter declParameter : parameters) {
			ISpecificationParameter specParameter = specification
					.getParameter(declParameter.getName());

			if (specParameter == null) {
				throw new BlockFileException(
						EDriverExceptionType.UNSUPPORTED_PARAMETER,
						"Unsupported parameter " + declParameter, errorLocation);
			}
			declParameter.linkTo(specParameter, outputLookup);
		}
	}

	/**
	 * Append all outputs from the given specification to this declaration.
	 * 
	 * @param specification
	 *            the specification from which to take the outputs.
	 * @return a mapping from specification outputs to the newly created
	 *         declaration outputs.
	 */
	private Map<SpecificationOutput, DeclarationOutput> appendOutputs(
			ISpecification specification) {
		Map<SpecificationOutput, DeclarationOutput> result = new HashMap<SpecificationOutput, DeclarationOutput>();
		for (SpecificationOutput specOutput : specification.getOutputs()) {
			DeclarationOutput declOutput = new DeclarationOutput(specOutput,
					this);
			outputs.add(declOutput);
			result.put(specOutput, declOutput);
		}
		return result;
	}

}