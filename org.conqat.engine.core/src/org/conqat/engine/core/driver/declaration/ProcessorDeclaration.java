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

import org.conqat.engine.core.driver.error.DriverException;
import org.conqat.engine.core.driver.error.EDriverExceptionType;
import org.conqat.engine.core.driver.instance.BlockInstance;
import org.conqat.engine.core.driver.instance.ProcessorInstance;
import org.conqat.engine.core.driver.specification.BlockSpecification;
import org.conqat.engine.core.driver.specification.ProcessorSpecification;
import org.conqat.engine.core.driver.specification.SpecificationLoader;

/**
 * This is the declaration of a processor. This is really simple as most stuff
 * is handled by the {@link DeclarationBase}.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 5749933FCE73E77E11A819CB0B77A2E9
 */
public class ProcessorDeclaration extends DeclarationBase {

	/**
	 * The specification of this processor. This is initialized in
	 * {@link #referenceSpecification()}.
	 */
	private ProcessorSpecification specification;

	/**
	 * Create a new processor declaration.
	 * 
	 * @param name
	 *            processor's name as specified in the config file.
	 * @param className
	 *            processor class name as specified in the config file.
	 * @param surroundingSpecification
	 *            the specification which surrounds this declaration (i.e. its
	 *            "scope").
	 * @param specLoader
	 *            the specification loader used to get the referenced
	 *            specification.
	 */
	public ProcessorDeclaration(String name, String className,
			BlockSpecification surroundingSpecification,
			SpecificationLoader specLoader) {
		super(name, className, surroundingSpecification, specLoader);
	}

	/** {@inheritDoc} */
	@Override
	public ProcessorSpecification getSpecification() {
		return specification;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws DriverException
	 */
	@Override
	public void referenceSpecification() throws DriverException {
		if (specification != null) {
			throw new IllegalStateException("May initialize only once!");
		}

		// if this does not exist, the spec loader raises an error
		try {
			specification = getSpecificationLoader().getProcessorSpecification(
					getSpecificationName());
		} catch (DriverException e) {
			if (e.getType() == EDriverExceptionType.PROCESSOR_CLASS_NOT_FOUND) {
				e.relocate(getErrorLocation());
			}
			throw e;
		}
		linkOutputsAndParameters(specification);
	}

	/** {@inheritDoc} */
	@Override
	public ProcessorInstance instantiate(BlockInstance parentInstance) {

		if (specification == null) {
			throw new IllegalStateException(
					"May not instantiate unless the specification has been referenced!");
		}

		return new ProcessorInstance(this, parentInstance);
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "processor '" + getName() + "'";
	}
}