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
import org.conqat.engine.core.driver.declaration.DeclarationParameter;
import org.conqat.engine.core.driver.declaration.IDeclaration;
import org.conqat.engine.core.driver.error.BlockFileException;
import org.conqat.engine.core.driver.util.IInputReferencable;
import org.conqat.engine.core.driver.util.IInputReferencer;
import org.conqat.lib.commons.collections.ListMap;

/**
 * This class infers the types for all input attributes of a block
 * specification. For this we have to follow all possible paths possibly
 * tunneling through pipelines.
 * <p>
 * What is exploited by this class is the fact that while the declaration graph
 * can be an arbitrary DAG, the type dependency hierarchy is just a forrest
 * where the input attributes are some of its roots.
 * <p>
 * As this is only used for the initialization of the block specification it has
 * package visibility.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: A257B62C1A56C8F84D65420D2304628E
 */
/* package */class AttributeTypeInferer {

	/** The block specification whose input types to infer. */
	private final BlockSpecification blockSpecification;

	/**
	 * Map for storing for each referencable all referencers referencing it. We
	 * need this, as only the other direction is explicitly stored for a block
	 * specification.
	 */
	private final ListMap<IInputReferencable, IInputReferencer> reverseLookup = new ListMap<IInputReferencable, IInputReferencer>();

	/** Create a new type inferer. */
	AttributeTypeInferer(BlockSpecification blockSpecification) {
		this.blockSpecification = blockSpecification;
		buildReverseLookup();
	}

	/** Initialize the reverse lookup table. */
	private void buildReverseLookup() {
		for (IDeclaration declaration : blockSpecification.getDeclarationList()) {
			for (DeclarationParameter param : declaration.getParameters()) {
				for (DeclarationAttribute attr : param.getAttributes()) {
					if (attr.isReference()) {
						reverseLookup.add(attr.getReference(), attr);
					}
				}
			}
		}

		for (BlockSpecificationOutput output : blockSpecification.getOutputs()) {
			reverseLookup.add(output.getReference(), output);
		}
	}

	/** Perform the actual type inference. */
	public void infer() throws BlockFileException {
		for (BlockSpecificationParameter param : blockSpecification
				.getParameters()) {
			for (BlockSpecificationAttribute attr : param.getAttributes()) {
				refineAttributeType(attr, attr);
			}
		}
	}

	/**
	 * Refines the type of the provided attribute. Additionally, finding
	 * pipeline outputs of this attribute is done here.
	 * <p>
	 * The actual type merging is done in
	 * {@link BlockSpecificationAttribute#refineType(org.conqat.lib.commons.reflect.ClassType)}
	 * 
	 * @param refinedAttribute
	 *            the attribute whose type is refined.
	 * @param treeRoot
	 *            the root of the type dependency tree which is used for
	 *            refinement.
	 */
	private void refineAttributeType(
			BlockSpecificationAttribute refinedAttribute,
			IInputReferencable treeRoot) throws BlockFileException {

		if (!reverseLookup.containsCollection(treeRoot)) {
			return;
		}

		for (IInputReferencer referencer : reverseLookup
				.getCollection(treeRoot)) {
			if (referencer.asBlockSpecificationOutput() != null) {
				// we have reached an output, so we are a pipeline
				refinedAttribute.addPipelineOutput(referencer
						.asBlockSpecificationOutput());
			} else {
				// otherwise this must be a DeclarationAttribute
				// (contract of IInputReferencer)
				refinedAttribute.refineType(referencer.asDeclarationAttribute()
						.getType());

				// follow pipeline outputs recursively
				for (DeclarationOutput dOutput : referencer
						.asDeclarationAttribute().getPipelineOutputs()) {
					refineAttributeType(refinedAttribute, dOutput);
				}
			}
		}
	}
}