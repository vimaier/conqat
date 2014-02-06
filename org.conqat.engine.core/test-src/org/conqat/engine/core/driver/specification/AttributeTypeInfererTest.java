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

import java.util.List;

import org.conqat.engine.core.driver.error.DriverException;
import org.conqat.engine.core.driver.util.Multiplicity;
import org.conqat.lib.commons.reflect.ClassType;
import org.conqat.lib.commons.reflect.TypesNotMergableException;

/**
 * Tests for {@link AttributeTypeInferer}. As the {@link ClassType} class is
 * tested separately, we will not test too many complicated type combinations
 * but rather see if the correct inputs are used for inference.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 1825B6B75BAD9139C177387AD83E3F26
 */
public class AttributeTypeInfererTest extends InfererTestBase {

	/**
	 * This tests the case where each input attribute has at most one
	 * referencing processor attribute.
	 */
	public void testSimpleTypes() throws DriverException {
		spec = loadBlock("attrtypeSimpleTypes");
		spec.initialize();

		assertParamMult("p", new Multiplicity(0, Multiplicity.INFINITY));
		assertAttrTypes("p", new ClassType(Integer.class), new ClassType(
				int.class), new ClassType(Object.class), new ClassType(),
				new ClassType(String.class), new ClassType(Number.class));
	}

	/**
	 * This tests the case where each input attribute has at multiple
	 * referencing processor attributes.
	 */
	public void testComplexTypes() throws DriverException,
			TypesNotMergableException {
		spec = loadBlock("attrtypeComplexTypes");
		spec.initialize();

		assertParamMult("p", new Multiplicity(0, Multiplicity.INFINITY));
		assertAttrTypes("p", new ClassType(Integer.class), new ClassType(
				String.class, List.class));
	}

	/** This tests the inference for the condition input. */
	public void testConditionInput() throws DriverException {
		spec = loadBlock("attrtypeCondition");
		spec.initialize();

		assertParamMult("p", new Multiplicity(1, 1));
		assertAttrTypes("p", new ClassType(Boolean.class));
	}

	/** Test very simple cases including pipelining. */
	public void testSimplePipelines() throws DriverException {
		spec = loadBlock("attrtypeObjectPipeline");
		spec.initialize();
		assertParamMult("p", new Multiplicity(1, 1));
		assertAttrTypes("p", new ClassType());
		assertAttrPipelines("p", new String[] { "o1", "o2" });

		spec = loadBlock("attrtypeIntegerPipeline");
		spec.initialize();
		assertParamMult("p", new Multiplicity(1, 1));
		assertAttrTypes("p", new ClassType(Integer.class));
		assertAttrPipelines("p", new String[] { "o1", "o2" });

		spec = loadBlock("attrtypeNumberPipeline");
		spec.initialize();
		assertParamMult("p", new Multiplicity(1, 1));
		assertAttrTypes("p", new ClassType(Number.class));
		assertAttrPipelines("p", new String[] { "o1", "o2" });

		spec = loadBlock("attrtypeStringPipeline");
		spec.initialize();
		assertParamMult("p", new Multiplicity(1, 1));
		assertAttrTypes("p", new ClassType(String.class));
		assertAttrPipelines("p", new String[] { "o1", "o3" });
	}

	/**
	 * Test more complex cases including pipelining. The tests are based on the
	 * blocks tested in {@link #testSimplePipelines()}, so if those fail, these
	 * will not work
	 */
	public void testComplexPipelines() throws DriverException {
		spec = loadBlock("attrtypeComplexPipeline");
		spec.initialize();
		assertParamMult("p", new Multiplicity(1, 1));
		assertAttrTypes("p", new ClassType(Object.class), new ClassType(
				Integer.class), new ClassType(Integer.class), new ClassType(
				Integer.class));
		assertAttrPipelines("p", new String[] { "o1" }, new String[0],
				new String[0], new String[] { "o2", "o3", "o3" });
	}

	/**
	 * Tests the case of branching pipelines, i.e. including processors with
	 * more than one pipeline parameter/attribute.
	 */
	public void testBranchingPipeline() throws DriverException {
		spec = loadBlock("attrtypeObjectBranching");
		spec.initialize();
		assertParamMult("p1", new Multiplicity(1, 1));
		assertAttrTypes("p1", new ClassType());
		assertAttrPipelines("p1", new String[] { "o1", "o2", "o3" });
		assertParamMult("p2", new Multiplicity(7, 42));
		assertAttrTypes("p2", new ClassType());
		assertAttrPipelines("p2", new String[] { "o1", "o2" });

		spec = loadBlock("attrtypeNumberBranching");
		spec.initialize();
		assertParamMult("p1", new Multiplicity(1, 1));
		assertAttrTypes("p1", new ClassType(Number.class));
		assertAttrPipelines("p1", new String[] { "o1", "o2", "o3" });
		assertParamMult("p2", new Multiplicity(7, 42));
		assertAttrTypes("p2", new ClassType(Number.class));
		assertAttrPipelines("p2", new String[] { "o1", "o2" });
	}

	/**
	 * Tests the case of branching pipelines, i.e. including processors with
	 * more than one pipeline parameter/attribute.
	 */
	public void testComplexBranchingPipeline() throws DriverException {
		spec = loadBlock("attrtypeComplexBranching");
		spec.initialize();

		assertParamMult("p1", new Multiplicity(1, 1));
		assertParamMult("p2", new Multiplicity(7, 42));
		assertParamMult("p3", new Multiplicity(7, 42));

		assertAttrTypes("p1", new ClassType(Integer.class));
		assertAttrTypes("p2", new ClassType(Integer.class));
		assertAttrTypes("p3", new ClassType(Integer.class));

		assertAttrPipelines("p1", new String[] { "o1", "o2", "o3", "o4" });
		assertAttrPipelines("p2", new String[] { "o1", "o2", "o3" });
		assertAttrPipelines("p3", new String[] { "o2", "o3" });
	}

	/**
	 * Tests the combination of branching pipelines with "freezing", i.e. the
	 * case where one pipeline input gets a known type input, disabling further
	 * pipeline inference.
	 */
	public void testBranchingFreezing() throws DriverException {
		spec = loadBlock("attrtypeBranchingFreezing");
		spec.initialize();

		assertParamMult("p1", new Multiplicity(7, 42));
		assertAttrTypes("p1", new ClassType(String.class));
		assertAttrPipelines("p1", new String[] {});
	}
}
