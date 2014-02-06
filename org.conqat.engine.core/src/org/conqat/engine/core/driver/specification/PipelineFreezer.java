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

/**
 * This class performs pipeline freezing. This step fixes the type of a pipeline
 * block or processor (actually its attribute) as soon as a concrete value (e.g.
 * from a non-pipeline processor) is used as its input. For "classical"
 * pipelines (where exactly one attribute is mapped to an output), this step
 * would not be required. But for the new (branching) pipelines, where the
 * contract states that any of the pipeline attributes associated with an output
 * can provide the output, this simplifies processing. The alternative would be
 * a much more involved type inference stage (with both lower and upper types),
 * which however seems to be overkill for typical applications.
 * <p>
 * As this is only used for the initialization of the block specification it has
 * package visibility.
 * 
 * @author $Author: deissenb $
 * @version $Rev: 37501 $
 * @ConQAT.Rating GREEN Hash: 189A601EB82F67A69A7408FA61FF5FCE
 */
/* package */class PipelineFreezer {

	/** The block specification whose parameter multiplicities to infer. */
	private final BlockSpecification blockSpecification;

	/** Create a new type inferer. */
	/* package */PipelineFreezer(BlockSpecification blockSpecification) {
		this.blockSpecification = blockSpecification;
	}

	/** Perform the actual type inference. */
	public void freeze() {
		for (IDeclaration declaration : blockSpecification.getDeclarationList()) {
			for (DeclarationParameter parameter : declaration.getParameters()) {
				for (DeclarationAttribute attr : parameter.getAttributes()) {
					freezePipeline(attr);
				}
			}
		}
	}

	/**
	 * Freezes (i.e. disables) pipeline inference for an attribute if required
	 * (as the input type is already fixed and does no longer need inference).
	 */
	private void freezePipeline(DeclarationAttribute attr) {
		if (!attr.getSpecificationAttribute().hasPipelineOutputs()) {
			return;
		}

		if (attr.isImmediateValue()) {
			attr.freezePipeline(attr.getType());
		} else if (referencesNonPipeline(attr)) {
			attr.freezePipeline(attr.getReference().getType());
		}
	}

	/** Returns whether the given attribute references a non-pipeline output. */
	private boolean referencesNonPipeline(DeclarationAttribute attr) {
		return (attr.getReference() instanceof DeclarationOutput)
				&& attr.getReference().asDeclarationOutput()
						.getPipelineAttributes() == null;
	}

}