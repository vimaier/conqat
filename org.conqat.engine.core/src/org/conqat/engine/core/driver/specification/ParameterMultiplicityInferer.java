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
package org.conqat.engine.core.driver.specification;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.conqat.engine.core.driver.declaration.DeclarationAttribute;
import org.conqat.engine.core.driver.declaration.DeclarationParameter;
import org.conqat.engine.core.driver.declaration.IDeclaration;
import org.conqat.engine.core.driver.error.BlockFileException;
import org.conqat.engine.core.driver.error.EDriverExceptionType;
import org.conqat.engine.core.driver.util.Multiplicity;
import org.conqat.lib.commons.collections.IdentityHashSet;
import org.conqat.lib.commons.collections.ListMap;

/**
 * This class infers the multiplicity for all input parameters of a block
 * specification. For this we have to follow all possible paths but do not have
 * to tunnel through pipelines.
 * <p>
 * As a side effect, the parameter multiplicities of all child declarations are
 * checked.
 * <p>
 * As this is only used for the initialization of the block specification it has
 * package visibility.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: FD86F5F1BA250C961E8C490B6160534E
 */
/* package */class ParameterMultiplicityInferer {

	/** The block specification whose parameter multiplicities to infer. */
	private final BlockSpecification blockSpecification;

	/** Create a new type inferer. */
	/* package */ParameterMultiplicityInferer(
			BlockSpecification blockSpecification) {
		this.blockSpecification = blockSpecification;
	}

	/** Perform the actual type inference. */
	public void infer() throws BlockFileException {

		// we look at each declaration contained in this block ...
		for (IDeclaration declaration : blockSpecification.getDeclarationList()) {

			// ... and cluster its parameters by "name" (i.e. the specification
			// used) ...
			ListMap<ISpecificationParameter, DeclarationParameter> mapping = buildSpecToDeclMap(declaration);

			// ... and handle each such "cluster" (i.e. parameters of the same
			// name) individually
			for (ISpecificationParameter specParameter : mapping.getKeys()) {
				checkAndPropagate(declaration, specParameter,
						mapping.getCollection(specParameter));
			}
		}

		handlePipelineMultiplicities();
	}

	/**
	 * Handle direct input output connections (pipelines) by setting their
	 * multiplicities to 1.
	 */
	private void handlePipelineMultiplicities() throws BlockFileException {
		for (BlockSpecificationOutput output : blockSpecification.getOutputs()) {
			if (output.getReference().asBlockSpecificationAttribute() != null) {
				output.getReference().asBlockSpecificationAttribute()
						.getParameter()
						.refineMultiplicity(new Multiplicity(1, 1));
			}
		}
	}

	/**
	 * Check all declaration parameters of of the same "name" (i.e. of the same
	 * specification parameter) for a single declaration. This includes checking
	 * the multiplicities and propagating any multiplicity constraints to
	 * referenced parameters of the block specification handled here.
	 * 
	 * @param declaration
	 *            the declaration currently under investigation (for error
	 *            messages).
	 * @param specParameter
	 *            the specification parameter currently checked (i.e. the "name"
	 *            of the parameters)
	 * @param declParams
	 *            the list of all declaration parameters corresponding (for this
	 *            declaration) to the specification parameter
	 */
	private static void checkAndPropagate(IDeclaration declaration,
			ISpecificationParameter specParameter,
			List<DeclarationParameter> declParams) throws BlockFileException {

		Set<BlockSpecificationParameter> referenced = new IdentityHashSet<BlockSpecificationParameter>();
		int numReferences = 0;

		// determine how many of the given declaration parameters reference
		// input parameters of this block (and find out which parameters are
		// actually referenced)
		for (DeclarationParameter declParam : declParams) {
			Set<BlockSpecificationParameter> referencedHere = getReferencedSpecificationParameter(declParam);
			if (!referencedHere.isEmpty()) {

				// referencing multiple parameters (from different attributes)
				// is only allowed for fixed multiplicities (such as [4,4]).
				if (referencedHere.size() > 1
						&& !specParameter.getMultiplicity().isSingleton()) {
					throw new BlockFileException(
							EDriverExceptionType.MULTIPLE_INPUT_REFERENCES,
							"Parameter '"
									+ declParam
									+ "' references multiple inputs although its multiplicity is no singleton!",
							declParam);
				}

				if (specParameter.getMultiplicity().equals(new Multiplicity())) {
					// for free multiplicities (no limits) just collect all
					// parameters
					referenced.addAll(referencedHere);
				} else if (referenced.isEmpty()) {
					referenced.addAll(referencedHere);
				} else if (!referenced.equals(referencedHere)) {
					throw new BlockFileException(
							EDriverExceptionType.MULTIPLE_INPUT_REFERENCES,
							"Parameter '" + declParam
									+ "' references multiple inputs.",
							declParam);
				}
				numReferences += 1;
			}
		}

		/*
		 * now we either check if the number of occurrences of the parameter is
		 * correct (if no parameters can be added from block inputs) or we
		 * determine the allowed interval for the input parameter such that this
		 * parameter's multiplicity is fulfilled and propagate this interval to
		 * the input parameter
		 */
		if (referenced.isEmpty()) {
			checkMultiplicities(declaration, specParameter, declParams.size());
		} else {
			// infer multiplicities
			Multiplicity multiplicity = specParameter.getMultiplicity();
			multiplicity = multiplicity.shiftBounds(numReferences
					- declParams.size());
			multiplicity = multiplicity.divideBy(numReferences);
			for (BlockSpecificationParameter referencedParameter : referenced) {
				referencedParameter.refineMultiplicity(multiplicity);
			}
		}
	}

	/**
	 * Checks the multiplicities for the given specification parameter and
	 * throws exceptions if these are not correct.
	 * 
	 * @param declaration
	 *            the declaration currently handled (for error messages).
	 * @param specParameter
	 *            the type of parameter currently checked.
	 * @param numOccurrences
	 *            the number of declaration parameters corresponding to the
	 *            specification parameter in the declaration currently tested.
	 */
	private static void checkMultiplicities(IDeclaration declaration,
			ISpecificationParameter specParameter, int numOccurrences)
			throws BlockFileException {

		if (specParameter.getMultiplicity().getLower() > numOccurrences) {
			throw new BlockFileException(
					EDriverExceptionType.PARAMETER_OCCURS_NOT_OFTEN_ENOUGH,
					"Parameter " + specParameter.getName() + " at "
							+ declaration + " is expected to occurr at least "
							+ specParameter.getMultiplicity().getLower()
							+ " time(s) but occurrs only " + numOccurrences
							+ " time(s).", declaration);
		}
		if (specParameter.getMultiplicity().getUpper() < numOccurrences) {
			throw new BlockFileException(
					EDriverExceptionType.PARAMETER_OCCURS_TOO_OFTEN,
					"Parameter " + specParameter.getName() + " at "
							+ declaration + " is expected to occurr at most "
							+ specParameter.getMultiplicity().getUpper()
							+ " time(s) but occurrs " + numOccurrences
							+ " time(s).", declaration);
		}
	}

	/**
	 * Returns the {@link BlockSpecificationParameter}s referenced by the
	 * attributes of the given declaration parameter. If there is no such
	 * reference, an empty set is returned.
	 */
	private static Set<BlockSpecificationParameter> getReferencedSpecificationParameter(
			DeclarationParameter declParam) {

		Set<BlockSpecificationParameter> result = new IdentityHashSet<BlockSpecificationParameter>();
		for (DeclarationAttribute attr : declParam.getAttributes()) {
			BlockSpecificationAttribute specAttr = attr
					.getReferencedBlockSpecAttr();
			if (specAttr != null) {
				result.add(specAttr.getParameter());
			}
		}
		return result;
	}

	/**
	 * Returns a map from specification parameters to their corresponding
	 * declaration parameters for a single declaration and its underlying
	 * specification.
	 */
	@SuppressWarnings("unchecked")
	private static ListMap<ISpecificationParameter, DeclarationParameter> buildSpecToDeclMap(
			IDeclaration declaration) {

		ListMap<ISpecificationParameter, DeclarationParameter> aggregated = new ListMap<ISpecificationParameter, DeclarationParameter>();

		// Create empty list for each specification parameter
		for (ISpecificationParameter param : declaration.getSpecification()
				.getParameters()) {
			ISpecificationParameter key = declaration.getSpecification()
					.getParameter(param.getName());
			aggregated.addAll(key, Collections.EMPTY_LIST);
		}

		// Add each declaration parameter to the list of its specification
		// parameter
		for (DeclarationParameter declParam : declaration.getParameters()) {
			aggregated.add(declParam.getSpecificationParameter(), declParam);
		}

		return aggregated;
	}
}