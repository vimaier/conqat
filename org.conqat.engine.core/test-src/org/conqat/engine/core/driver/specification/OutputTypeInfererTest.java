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
import org.conqat.lib.commons.reflect.ClassType;

/**
 * Tests for {@link OutputTypeInferer}. As the {@link ClassType} class is tested
 * separately, we will not test too many complicated type combinations but
 * rather see if the correct inputs are used for inference.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 10F252593B982871D4427DEA13105DC0
 */
public class OutputTypeInfererTest extends InfererTestBase {

	/** Tests the most basic kind of output type inference. */
	public void testSimpleType() throws DriverException {
		spec = loadBlock("outtypeSimpleTypes");
		spec.initialize();

		assertOutputType("Object", new ClassType());
		assertOutputType("Integer", new ClassType(Integer.class));
		assertOutputType("String", new ClassType(String.class));
	}

	/** Tests output type inference when pipelining is included. */
	public void testPipelineType() throws DriverException {
		spec = loadBlock("outtypePipelineTypes");
		spec.initialize();

		assertOutputType("o1", new ClassType(Integer.class));
		assertOutputType("o2", new ClassType(Integer.class));
	}

	/** Tests output type inference when branching pipelining is included. */
	public void testBranchingType1() throws DriverException {
		spec = loadBlock("attrtypeComplexBranching");
		spec.initialize();

		assertOutputType("o1", new ClassType(Integer.class));
		assertOutputType("o2", new ClassType(Integer.class));
		assertOutputType("o3", new ClassType(Integer.class));
		assertOutputType("o4", new ClassType(Integer.class));
	}

	/** Tests output type inference when branching pipelining is included. */
	public void testBranchingType2() throws DriverException {
		spec = loadBlock("attrtypeBranchingFreezing");
		spec.initialize();
		assertOutputType("o1", new ClassType(String.class));
	}
}
