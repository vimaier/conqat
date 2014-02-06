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

import java.util.Arrays;
import java.util.List;

import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.APipelineSource;
import org.conqat.engine.core.driver.error.DriverException;
import org.conqat.engine.core.driver.error.EDriverExceptionType;
import org.conqat.engine.core.driver.error.ProcessorLayoutException;
import org.conqat.lib.commons.clone.CloneUtils;
import org.conqat.lib.commons.clone.DeepCloneException;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.reflect.ClassType;
import org.conqat.lib.commons.reflect.FormalParameter;
import org.conqat.lib.commons.reflect.GenericTypeResolver;
import org.conqat.lib.commons.reflect.ReflectionUtils;
import org.conqat.lib.commons.reflect.TypeConversionException;
import org.conqat.lib.commons.string.StringUtils;

/**
 * An attribute for a processor specification based on a method. This captures
 * the {@link AConQATAttribute} annotation.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 36645 $
 * @ConQAT.Rating GREEN Hash: 09BC9CB57D90194803524CA67325C3C8
 */
/* package */class MethodBasedProcessorSpecificationAttribute extends
		ProcessorSpecificationAttribute {

	/** The attribute's annotation. */
	private final AConQATAttribute annotation;

	/** The formal parameter corresponding to this attribute. */
	private final FormalParameter formalParameter;

	/** The default value of this attribute. */
	private final Object defaultValue;

	/**
	 * Create a new attribute for a processor specification.
	 * 
	 * @param formalParameter
	 *            the formal parameter corresponding to this attribute.
	 * @param resolver
	 *            the type resolver for the class to which this formal parameter
	 *            belongs to.
	 * @param parameter
	 *            the parameter this belongs to.
	 */
	/* package */MethodBasedProcessorSpecificationAttribute(
			FormalParameter formalParameter, GenericTypeResolver resolver,
			ProcessorSpecificationParameter parameter) throws DriverException {
		super(formalParameter.getAnnotation(AConQATAttribute.class).name(),
				new ClassType(resolver.resolveGenericType(formalParameter
						.getGenericType())), parameter);

		annotation = formalParameter.getAnnotation(AConQATAttribute.class);
		this.formalParameter = formalParameter;
		defaultValue = determineDefaultValue();

		ClassType outputType = parameter.getSpecification().getOutputs()[0]
				.getType();
		if (hasPipelineOutputs() && !getType().equals(outputType)) {
			throw new ProcessorLayoutException(
					EDriverExceptionType.INCOMPATIBLE_PIPELINE_TYPES,
					this + " is defined as pipeline attribute but its type ("
							+ getType() + ") does not match the output type '"
							+ outputType + "'.", this);
		}
	}

	/**
	 * Determines the object for the default value (or <code>null</code> if no
	 * default is given).
	 */
	private Object determineDefaultValue() throws ProcessorLayoutException {
		if (StringUtils.isEmpty(annotation.defaultValue())) {
			return null;
		}
		if (hasPipelineOutputs()) {
			throw new ProcessorLayoutException(
					EDriverExceptionType.PIPELINE_ATTRIBUTE_HAS_DEFAULT_VALUE,
					this
							+ " is defined as pipeline attribute but has default value '"
							+ annotation.defaultValue() + "'.", this);
		}
		try {
			return ReflectionUtils.convertString(annotation.defaultValue(),
					formalParameter.getType());
		} catch (TypeConversionException e) {
			throw new ProcessorLayoutException(
					EDriverExceptionType.ILLEGAL_DEFAULT_VALUE,
					"The default value '" + annotation.defaultValue() + "' at "
							+ this + " does not match the expected type '"
							+ formalParameter.getType().getName() + "'.", this);
		}
	}

	/** {@inheritDoc} */
	@Override
	public String getDoc() {
		return annotation.description();
	}

	/** {@inheritDoc} */
	@Override
	public List<SpecificationOutput> getPipelineOutputs() {
		if (!hasPipelineOutputs()) {
			return CollectionUtils.emptyList();
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		List<SpecificationOutput> result = (List) Arrays.asList(parameter
				.getSpecification().getOutputs());
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public boolean hasPipelineOutputs() {
		return formalParameter.getAnnotation(APipelineSource.class) != null;
	}

	/** {@inheritDoc} */
	@Override
	public Object getDefaultValue() throws ProcessorLayoutException {
		try {
			return CloneUtils.cloneAsDeepAsPossible(defaultValue);
		} catch (DeepCloneException e) {
			throw new ProcessorLayoutException(
					EDriverExceptionType.DEFAULT_VALUE_COULD_NOT_BE_CLONED,
					"The default value '" + defaultValue + "' of " + this
							+ " could not be cloned.", e, this);
		}
	}
}