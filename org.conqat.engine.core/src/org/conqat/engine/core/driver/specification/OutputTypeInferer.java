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

import org.conqat.engine.core.driver.declaration.DeclarationAttribute;
import org.conqat.engine.core.driver.declaration.DeclarationOutput;
import org.conqat.engine.core.driver.error.BlockFileException;
import org.conqat.engine.core.driver.util.IInputReferencable;
import org.conqat.lib.commons.reflect.ClassType;

/**
 * This class infers the types for all outputs of a block specification. The
 * types for the attributes (inputs) must have already been infered.
 * <p>
 * This inference is rather simple, as there is exactly one path from an output
 * of a block specification either to the output of a declaration or to an input
 * attribute of this block specification. The only problem which has to be
 * handled is the case when the reference is a pipeline output. In this case we
 * have to traverse through to its pipeline input.
 * <p>
 * As this is only used for the initialization of the block specification it has
 * package visibility.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: E3F914D97CA916B7CB25CEFFC66A4B8F
 */
/* package */class OutputTypeInferer {

	/** The block specification we are working on. */
	private final BlockSpecification blockSpecification;

	/** Create new OutputResolver. */
	/* package */OutputTypeInferer(BlockSpecification blockSpecification) {
		this.blockSpecification = blockSpecification;
	}

	/** Perform the actual type inference. */
	public void infer() throws BlockFileException {
		for (BlockSpecificationOutput output : blockSpecification.getOutputs()) {
			output.setType(determineOutputType(output.getReference()));
		}
	}

	/**
	 * Determines the output type for a given reference. If the reference is a
	 * pipeline output, tunneling through it is automatically handled.
	 * 
	 * @param reference
	 *            the reference for which to determine the output.
	 */
	private ClassType determineOutputType(IInputReferencable reference)
			throws BlockFileException {

		DeclarationOutput refOutput = reference.asDeclarationOutput();
		if (refOutput == null) {
			// This is an attribute (input) of this block, thus it has an
			// explicit type
			return reference.getType();
		}

		if (refOutput.getPipelineAttributes() != null) {
			// this is a pipeline output, so use whatever is its input
			ClassType outputType = null;
			for (DeclarationAttribute attribute : refOutput
					.getPipelineAttributes()) {
				if (attribute.isImmediateValue()) {
					outputType = intersect(outputType, attribute.getType());
				} else {
					outputType = intersect(outputType,
							determineOutputType(attribute.getReference()));
				}
			}
			return outputType;
		}

		// this is a reference to a (non-pipeline) declaration output, so is has
		// an explicit type
		return reference.getType();
	}

	/** Intersects the given types, supporting handling of null. */
	private ClassType intersect(ClassType type1, ClassType type2) {
		if (type1 == null) {
			return type2;
		}
		if (type2 == null) {
			return type1;
		}
		return type1.intersect(type2);
	}
}