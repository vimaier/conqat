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

import java.lang.reflect.InvocationTargetException;

import org.conqat.engine.core.core.IConQATProcessor;
import org.conqat.engine.core.driver.error.ErrorLocation;

/**
 * A parameter for a processor specification. This class extracts the relevant
 * information from annotations.
 * <p>
 * Although this class is abstract, it does not end in "Base". The reason is
 * that this class is exposed externally, while the concrete implementations are
 * only visible within this package.
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 3EF6AA2FE63254FC3F4A188637E95C3C
 */
public abstract class ProcessorSpecificationParameter extends
		SpecificationParameterBase<ProcessorSpecificationAttribute> {

	/** The specification this belongs to. */
	private final ProcessorSpecification specification;

	/**
	 * Constructor.
	 * 
	 * @param name
	 *            the name of the parameter.
	 * @param specification
	 *            the specification this belongs to.
	 */
	protected ProcessorSpecificationParameter(String name,
			ProcessorSpecification specification) {
		super(name);
		this.specification = specification;
	}

	/** {@inheritDoc} */
	@Override
	public ErrorLocation getErrorLocation() {
		return specification.getErrorLocation();
	}

	/**
	 * Returns the specification containing this parameter. This is only
	 * provided to allow an attribute to find its specification.
	 */
	/* package */ProcessorSpecification getSpecification() {
		return specification;
	}

	/**
	 * Applies a given parameter to a processor by calling methods and/or
	 * setting the values of certain fields.
	 * 
	 * @param attributeValues
	 *            the values for the attributes in the order implied by
	 *            {@link #getAttributes()}.
	 */
	public abstract void applyParameterToProcessor(IConQATProcessor processor,
			Object[] attributeValues) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException;

	/** {@inheritDoc} */
	@Override
	protected ProcessorSpecificationAttribute[] allocateAttributeArray(int size) {
		return new ProcessorSpecificationAttribute[size];
	}

}