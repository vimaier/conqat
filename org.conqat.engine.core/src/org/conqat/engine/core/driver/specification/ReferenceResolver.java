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

import java.util.HashMap;
import java.util.Map;

import org.conqat.engine.core.driver.declaration.DeclarationAttribute;
import org.conqat.engine.core.driver.declaration.DeclarationOutput;
import org.conqat.engine.core.driver.declaration.DeclarationParameter;
import org.conqat.engine.core.driver.declaration.IDeclaration;
import org.conqat.engine.core.driver.error.BlockFileException;
import org.conqat.engine.core.driver.error.DriverException;
import org.conqat.engine.core.driver.error.EDriverExceptionType;
import org.conqat.engine.core.driver.util.IInputReferencable;

/**
 * This class is responsible for resolving the references between processors
 * defined in the block specification (defined via the '@'-attributes) and
 * setting up plain arguments (constants).
 * <p>
 * As this is only used for the initialization of the block specification it has
 * package visibility.
 * 
 * @author Tilman Seifert
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 30B44CB4F9D6AF753C20384C5C3BE276
 */
/* package */class ReferenceResolver {

	/** The block specification for which we perform reference resolution. */
	private final BlockSpecification blockSpecification;

	/**
	 * A map of all "things" that can be the target of an '@'-reference, i.e.
	 * inputs of the block specification and outputs of declarations. This maps
	 * their names to the actual objects.
	 */
	private final Map<String, IInputReferencable> knownOutputs = new HashMap<String, IInputReferencable>();

	/** Create new reference resolver. */
	/* package */ReferenceResolver(BlockSpecification blockSpecification) {
		this.blockSpecification = blockSpecification;

		findDeclarationOutputs();

		findBlockSpecificationInputs();
	}

	/**
	 * Find all outputs of all declarations and put them into the
	 * {@link #knownOutputs} map.
	 */
	private void findDeclarationOutputs() {
		for (IDeclaration declaration : blockSpecification.getDeclarationList()) {
			for (DeclarationOutput output : declaration.getOutputs()) {
				knownOutputs.put(output.getReferenceName(), output);
			}
		}
	}

	/**
	 * Find all input attributes of this block specification and put them into
	 * the {@link #knownOutputs} map.
	 */
	private void findBlockSpecificationInputs() {
		for (BlockSpecificationParameter param : blockSpecification
				.getParameters()) {
			for (BlockSpecificationAttribute attr : param.getAttributes()) {
				knownOutputs.put(attr.getReferenceName(), attr);
			}
		}
	}

	/** Perform the actual work of resolving the references. */
	public void resolve() throws DriverException {
		resolveAttributes();
		resolveOutputReferences();
	}

	/**
	 * Resolves all references in the attributes of the contained declarations.
	 * In this pass also all constant values are prepared.
	 */
	private void resolveAttributes() throws DriverException {
		for (IDeclaration declaration : blockSpecification.getDeclarationList()) {
			for (DeclarationParameter param : declaration.getParameters()) {
				for (DeclarationAttribute attr : param.getAttributes()) {
					resolveAttribute(attr);
				}
			}
		}
	}

	/**
	 * Resolve the reference in the given attribute (if any). Otherwise
	 * initialize its constant value.
	 */
	private void resolveAttribute(DeclarationAttribute attribute)
			throws BlockFileException {
		String value = attribute.getValueText();

		if (value.startsWith("@")) {
			IInputReferencable ref = knownOutputs.get(value);
			if (ref == null) {
				String message = attribute + " references unknown object '"
						+ value + "'.";
				throw new BlockFileException(
						EDriverExceptionType.UNDEFINED_REFERENCE, message,
						attribute);
			}

			attribute.setReference(ref);
		} else if (attribute.getValueObject() == null) {
			attribute.initConstant();
		}

	}

	/**
	 * Resolves all references of the outputs of the block specification being
	 * resolved.
	 */
	private void resolveOutputReferences() throws BlockFileException {
		for (BlockSpecificationOutput output : blockSpecification.getOutputs()) {
			String value = output.getReferenceText();

			IInputReferencable ref = knownOutputs.get(value);
			if (ref == null) {
				String message = "Output '" + output.getName()
						+ "' in block-spec '" + blockSpecification.getName()
						+ "' references unknown object '" + value + "'.";
				throw new BlockFileException(
						EDriverExceptionType.UNDEFINED_REFERENCE, message,
						output);
			}

			output.setReference(ref);
		}
	}
}