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
import java.util.List;

import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.reflect.ClassType;
import org.conqat.lib.commons.reflect.GenericTypeResolver;

/**
 * An attribute for a processor specification based on a method. This captures
 * the {@link AConQATFieldParameter} annotation.
 * 
 * @author hummelb
 * @author $Author: juergens $
 * @version $Rev: 35194 $
 * @ConQAT.Rating GREEN Hash: 5AFBDDDA595767B0ADCD44B4DE10AB04
 */
/* package */class FieldBasedProcessorSpecificationAttribute extends
		ProcessorSpecificationAttribute {

	/** The attribute's annotation. */
	private final AConQATFieldParameter annotation;

	/**
	 * Create a new attribute for a processor specification.
	 * 
	 * @param field
	 *            the field corresponding to this attribute.
	 * @param resolver
	 *            the type resolver for the class to which this formal parameter
	 *            belongs to.
	 * @param parameter
	 *            the parameter this belongs to.
	 */
	/* package */FieldBasedProcessorSpecificationAttribute(Field field,
			GenericTypeResolver resolver,
			ProcessorSpecificationParameter parameter) {
		super(field.getAnnotation(AConQATFieldParameter.class).attribute(),
				new ClassType(resolver.resolveGenericType(field
						.getGenericType())), parameter);

		annotation = field.getAnnotation(AConQATFieldParameter.class);
	}

	/** {@inheritDoc} */
	@Override
	public Object getDefaultValue() {
		// no default value
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public List<SpecificationOutput> getPipelineOutputs() {
		// no pipelines
		return CollectionUtils.emptyList();
	}

	/** {@inheritDoc} */
	@Override
	public boolean hasPipelineOutputs() {
		// no pipelines
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public String getDoc() {
		return annotation.description();
	}

}