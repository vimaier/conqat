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

import org.conqat.engine.core.driver.error.EDriverExceptionType;
import org.conqat.engine.core.driver.specification.processors.ProcessorWithDefaultPipeline;
import org.conqat.engine.core.driver.specification.processors.ProcessorWithIllegalDefault;

/**
 * Tests for {@link ProcessorSpecificationAttribute}.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 387EA7AAD1A4EF687D3440E8F3F211FA
 */
public class ProcessorSpecificationAttributeTest extends
		ProcessorSpecificationTestBase {

	/** Test processor with a pipeline attribute having a default value. */
	public void testPipelineAttributeHasDefaultValue() {
		checkException(ProcessorWithDefaultPipeline.class,
				EDriverExceptionType.PIPELINE_ATTRIBUTE_HAS_DEFAULT_VALUE);
	}

	/** Test processor an invalid default value. */
	public void testIllegalDefaultValue() {
		checkException(ProcessorWithIllegalDefault.class,
				EDriverExceptionType.ILLEGAL_DEFAULT_VALUE);
	}
}