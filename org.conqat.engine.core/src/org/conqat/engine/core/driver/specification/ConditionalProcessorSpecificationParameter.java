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

import org.conqat.engine.core.core.IConQATProcessor;
import org.conqat.engine.core.driver.error.DriverException;
import org.conqat.engine.core.driver.util.Multiplicity;
import org.conqat.lib.commons.string.StringUtils;

/**
 * The {@link ProcessorSpecificationParameter} for the synthetic conditional
 * parameter.
 * 
 * @author $Author: juergens $
 * @version $Rev: 35194 $
 * @ConQAT.Rating GREEN Hash: 834A2F9CFC1BB07997FFB00290F83CB4
 */
/* package */class ConditionalProcessorSpecificationParameter extends
		ProcessorSpecificationParameter implements IConditionalParameter {

	/** Constructor. */
	/* package */ConditionalProcessorSpecificationParameter(
			ProcessorSpecification specification) throws DriverException {
		super(PARAMETER_NAME, specification);

		addAttribute(new ConditionalProcessorSpecificationAttribute(
				VALUE_ATTRIBUTE, this));
		addAttribute(new ConditionalProcessorSpecificationAttribute(
				INVERT_ATTRIBUTE, this));
	}

	/** {@inheritDoc} */
	@Override
	public void applyParameterToProcessor(IConQATProcessor processor,
			Object[] attributeValues) {
		// does nothing
	}

	/** {@inheritDoc} */
	@Override
	public Multiplicity getMultiplicity() {
		return new Multiplicity(1, 1);
	}

	/** {@inheritDoc} */
	@Override
	public String getDoc() {
		return StringUtils.EMPTY_STRING;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isSynthetic() {
		return true;
	}
}