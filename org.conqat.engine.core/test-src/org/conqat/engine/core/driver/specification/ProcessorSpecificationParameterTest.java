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

import org.conqat.engine.core.driver.error.DriverException;
import org.conqat.engine.core.driver.error.EDriverExceptionType;
import org.conqat.engine.core.driver.specification.processors.ProcessorWithDuplicateAttributeName;
import org.conqat.engine.core.driver.specification.processors.ProcessorWithEmptyParameterInterval;
import org.conqat.engine.core.driver.specification.processors.ProcessorWithOptionalPipelineMultiplicity;
import org.conqat.engine.core.driver.specification.processors.ProcessorWithUnannotatedParameter;
import org.conqat.engine.core.driver.specification.processors.ProcessorWithValidPipelineMultiplicity;

/**
 * Tests for {@link ProcessorSpecificationParameter}.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 79F94884B52D770C08C8D07869575074
 */
public class ProcessorSpecificationParameterTest extends
		ProcessorSpecificationTestBase {

	/** Test processor with a method where a parameter annotation is missing. */
	public void testParameterNotAnnotated() {
		checkException(ProcessorWithUnannotatedParameter.class,
				EDriverExceptionType.FORMAL_PARAMETER_NOT_ANNOTATED);
	}

	/** Test processor with parameter having invalid parameter interval. */
	public void testEmptyParameterInterval() {
		checkException(ProcessorWithEmptyParameterInterval.class,
				EDriverExceptionType.EMPTY_PARAMETER_INTERVAL);
	}

	/** Test processor with pipeline parameter and wrong multiplicity. */
	public void testPipelineParameterMultiplicity() throws DriverException {

		// both should be valid and throw no exceptions
		new ProcessorSpecification(
				ProcessorWithValidPipelineMultiplicity.class.getName());
		new ProcessorSpecification(
				ProcessorWithOptionalPipelineMultiplicity.class.getName());
	}

	/** Test processor with duplicate name for attribute. */
	public void testProcessorWithDuplicateAttributeName() {
		checkException(ProcessorWithDuplicateAttributeName.class,
				EDriverExceptionType.DUPLICATE_ATTRIBUTE_NAME);
	}

}