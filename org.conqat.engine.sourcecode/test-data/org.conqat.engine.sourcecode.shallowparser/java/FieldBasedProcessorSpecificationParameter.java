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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.IConQATProcessor;
import org.conqat.engine.core.driver.error.DriverException;
import org.conqat.engine.core.driver.error.EDriverExceptionType;
import org.conqat.engine.core.driver.error.ProcessorLayoutException;
import org.conqat.engine.core.driver.util.Multiplicity;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.reflect.GenericTypeResolver;

/**
 * A parameter for a processor specification based on a field. This captures the
 * {@link AConQATFieldParameter} annotation.
 * 
 * 
 * @author hummelb
 * @author $Author: hummelb $
 * @version $Rev: 35946 $
 * @ConQAT.Rating GREEN Hash: FF6E2BD8C8D3F633DE241B62660449B6
 */
/* package */class FieldBasedProcessorSpecificationParameter extends
		ProcessorSpecificationParameter {

	/** The annotation corresponding to this parameter. */
	private final AConQATFieldParameter annotation;

	/** The field being exposed. */
	private final Field field;

	/**
	 * Creates a new parameter for a processor specification.
	 * 
	 * @param field
	 *            the field of the IConQATProcessor this parameter is
	 *            constructed from.
	 * @param resolver
	 *            the type resolver for the class to which the provided method
	 *            belongs to.
	 * @param specification
	 *            the specification this belongs to.
	 */
	/* package */FieldBasedProcessorSpecificationParameter(Field field,
			GenericTypeResolver resolver, ProcessorSpecification specification)
			throws DriverException {
		super(field.getAnnotation(AConQATFieldParameter.class).parameter(),
				specification);
		this.field = field;
		annotation = field.getAnnotation(AConQATFieldParameter.class);

		if (Modifier.isStatic(field.getModifiers())) {
			throw new ProcessorLayoutException(
					EDriverExceptionType.STATIC_PARAMETER, "Field "
							+ field.getName() + " is static!", this);
		}
		if (Modifier.isFinal(field.getModifiers())) {
			throw new ProcessorLayoutException(
					EDriverExceptionType.FINAL_FIELD_PARAMETER, "Field "
							+ field.getName() + " is final!", this);
		}

		addAttribute(new FieldBasedProcessorSpecificationAttribute(field,
				resolver, this));
	}

	/** {@inheritDoc} */
	@Override
	public void applyParameterToProcessor(IConQATProcessor processor,
			Object[] attributeValues) throws IllegalArgumentException,
			IllegalAccessException {
		CCSMAssert.isTrue(attributeValues.length == 1,
				"As this has one attribute, the args should match!");
		field.set(processor, attributeValues[0]);
	}

	/** {@inheritDoc} */
	@Override
	public Multiplicity getMultiplicity() {
		if (annotation.optional()) {
			return new Multiplicity(0, 1);
		}
		return new Multiplicity(1, 1);
	}

	/** {@inheritDoc} */
	@Override
	public String getDoc() {
		return annotation.description();
	}

}