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
import org.conqat.engine.core.driver.error.EDriverExceptionType;
import org.conqat.lib.commons.reflect.ClassType;

/**
 * This class checks - for all declarations in the declaration graph of a block
 * specification - if attributes receive the correct type.
 * <p>
 * For this process, pipeline effects (i.e. changed return value due to the
 * input value of an attribute) are considered. To simplify this process, the
 * execution list of the block specification must already been sorted correctly
 * (TopSorter).
 * <p>
 * As this is only used for the initialization of the block specification it has
 * package visibility.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: C1EE018249E8C27828A5DE63E4428764
 */
/* package */class DeclarationTypeChecker {

	/** The block specification for which type checking is performed. */
	private final BlockSpecification blockSpecification;

	/** Create a new type checker. */
	/* package */DeclarationTypeChecker(BlockSpecification blockSpecification) {
		this.blockSpecification = blockSpecification;
	}

	/** Perform the checking as desribed for this class. */
	public void check() throws BlockFileException {
		// iterate in execution order to avoid problems with pipelining
		for (IDeclaration declaration : blockSpecification.getDeclarationList()) {
			checkDeclaration(declaration);
		}
	}

	/**
	 * Check type correctness of the attributes of all parameters for a single
	 * declaration.
	 */
	private void checkDeclaration(IDeclaration declaration)
			throws BlockFileException {
		for (DeclarationParameter parameter : declaration.getParameters()) {
			for (DeclarationAttribute attr : parameter.getAttributes()) {
				checkDeclarationAttribute(attr);
			}
		}
	}

	/**
	 * Typechecks a single declaration attribute. Also stores actual pipeline
	 * output values.
	 */
	private void checkDeclarationAttribute(DeclarationAttribute attr)
			throws BlockFileException {

		// Ignore immediate value attributes
		if (!attr.isReference()) {
			return;
		}

		ClassType source = attr.getReference().getType();
		ClassType target = attr.getType();
		if (!target.isAssignableFrom(source)) {
			String message = "Trying to assign " + source + " to " + target
					+ " at unit: "
					+ attr.getParameter().getDeclaration().getName()
					+ ", parameter: " + attr.getParameter().getName()
					+ ", attribute: " + attr.getName();
			throw new BlockFileException(EDriverExceptionType.TYPE_MISMATCH,
					message, attr);
		}

		// Store actual result type for pipeline attributes
		for (DeclarationOutput output : attr.getPipelineOutputs()) {
			output.mergeActualResultType(attr.getReference().getType());
		}
	}
}
