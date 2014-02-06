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

/**
 * Tests for {@link DeclarationTypeChecker}.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 7B74E11EF18A80734C24671528BE2972
 */
public class DeclarationTypeCheckerTest extends InfererTestBase {

	/** Tests the most basic kind type mismatch. */
	public void testSimpleError() {
		expectException("decltypeSimpleError", EDriverExceptionType.TYPE_MISMATCH);
	}

	/** Tests type mismatch avoidance when pipelining is included. */
	public void testPipeline() throws DriverException {
		loadBlock("decltypePipelineCase").initialize();
	}
	
	/** Tests type mismatch avoidance when branching pipelining is included. */
	public void testBranchingPipeline() throws DriverException {
		loadBlock("decltypePipelineBranchingCase").initialize();
	}
	
	/** Tests the case of an unconnected optional pipeline attribute. */
	public void testOptionalPipeline() throws DriverException {
		loadBlock("decltypeOptionalPipelineCase").initialize();
	}
}
