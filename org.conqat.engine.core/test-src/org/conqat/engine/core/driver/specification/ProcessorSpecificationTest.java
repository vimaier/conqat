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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.conqat.engine.core.driver.error.EDriverExceptionType;
import org.conqat.engine.core.driver.specification.processors.AbstractGenericProcessorInstance;
import org.conqat.engine.core.driver.specification.processors.GenericProcessorBase;
import org.conqat.engine.core.driver.specification.processors.GenericProcessorInstance;
import org.conqat.engine.core.driver.specification.processors.ProcessorForSpecTestWithPipeline;
import org.conqat.engine.core.driver.specification.processors.ProcessorToTestSpec;
import org.conqat.engine.core.driver.specification.processors.ProcessorWithDuplicateParameterName;
import org.conqat.engine.core.driver.specification.processors.ProcessorWithIncompatiblePipeline;
import org.conqat.engine.core.driver.specification.processors.ProcessorWithoutAnnotation;
import org.conqat.engine.core.driver.specification.processors.ProcessorWithoutInterface;
import org.conqat.engine.core.driver.specification.processors.ProcessorWithoutParameterlessConstructor;
import org.conqat.engine.core.driver.specification.processors.ProcessorWithoutPublicConstructor;
import org.conqat.engine.core.driver.util.Multiplicity;
import org.conqat.engine.core.logging.IConQATLogger;
import org.conqat.lib.commons.reflect.ClassType;

/**
 * Tests for {@link ProcessorSpecification}.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: E91EA8B98D6BAE2962FF1E829EC02A17
 */
public class ProcessorSpecificationTest extends ProcessorSpecificationTestBase {

	/**
	 * Tests whether all processor details (parameters, outputs, documentation,
	 * etc.) are read correctly from the class.
	 */
	public void testSpecificationDetails() throws Exception {
		ProcessorSpecification spec = new ProcessorSpecification(
				ProcessorToTestSpec.class.getName());

		assertEquals("desc", spec.getDoc());
		assertEquals(ProcessorToTestSpec.class.getName(), spec.getName());

		assertEquals(1, spec.getOutputs().length);
		assertEquals("", spec.getOutputs()[0].getName());
		assertEquals(new ClassType(Object.class),
				spec.getOutputs()[0].getType());

		assertEquals(4, spec.getParameters().length);
		assertTrue(spec.getParameters()[0].isSynthetic());
		assertEquals(IConditionalParameter.PARAMETER_NAME,
				spec.getParameters()[0].getName());

		Set<String> paramNames = new HashSet<String>();
		for (ISpecificationParameter param : spec.getNonSyntheticParameters()) {
			paramNames.add(param.getName());
		}
		assertTrue(paramNames.contains("xset"));
		assertTrue(paramNames.contains("mult"));
		assertTrue(paramNames.contains("field_p"));

		ISpecificationParameter xset = spec.getParameter("xset");
		assertEquals("xset", xset.getName());
		assertEquals("xset_desc", xset.getDoc());
		assertEquals(new Multiplicity(0, Multiplicity.INFINITY),
				xset.getMultiplicity());
		assertEquals(2, xset.getAttributes().length);

		assertEquals("at_a", xset.getAttributes()[0].getName());
		assertEquals("at_a_desc", xset.getAttributes()[0].getDoc());
		assertEquals(0, xset.getAttributes()[0].getPipelineOutputs().size());
		assertEquals(null, xset.getAttributes()[0].getDefaultValue());
		assertEquals(new ClassType(int.class),
				xset.getAttributes()[0].getType());

		assertEquals("at_b", xset.getAttributes()[1].getName());
		assertEquals("at_b_desc", xset.getAttributes()[1].getDoc());
		assertEquals(0, xset.getAttributes()[1].getPipelineOutputs().size());
		assertEquals("test", xset.getAttributes()[1].getDefaultValue());
		assertEquals(new ClassType(String.class),
				xset.getAttributes()[1].getType());

		ISpecificationParameter mult = spec.getParameter("mult");
		assertEquals("mult", mult.getName());
		assertEquals("mult_desc", mult.getDoc());
		assertEquals(new Multiplicity(7, 42), mult.getMultiplicity());
		assertEquals(0, mult.getAttributes().length);

		ISpecificationParameter field = spec.getParameter("field_p");
		assertEquals("field_p", field.getName());
		assertEquals("field_d", field.getDoc());
		assertEquals(new Multiplicity(0, 1), field.getMultiplicity());
		assertEquals(1, field.getAttributes().length);

		SpecificationAttribute fieldAttr = field.getAttributes()[0];
		assertEquals("field_a", fieldAttr.getName());
		assertEquals("field_d", fieldAttr.getDoc());
		assertFalse(fieldAttr.hasPipelineOutputs());
		assertNull(fieldAttr.getDefaultValue());
		assertEquals(new ClassType(double.class), fieldAttr.getType());
	}

	/** Test whether pipeline information is extracted correctly. */
	public void testWithPipeline() throws Exception {
		ProcessorSpecification spec = new ProcessorSpecification(
				ProcessorForSpecTestWithPipeline.class.getName());

		assertEquals(new ClassType(Integer.class),
				spec.getOutputs()[0].getType());

		// must be 2: our parameter and the synthetic condition parameter
		assertEquals(2, spec.getParameters().length);
		assertTrue(spec.getParameters()[1].getName().equals("xset"));

		ISpecificationParameter xset = spec.getParameter("xset");
		assertEquals(2, xset.getAttributes().length);

		assertEquals(1, xset.getAttributes()[0].getPipelineOutputs().size());
		assertEquals(spec.getOutputs()[0], xset.getAttributes()[0]
				.getPipelineOutputs().get(0));

		assertEquals(0, xset.getAttributes()[1].getPipelineOutputs().size());
	}

	/** Test processor not implementing <code>IConQATProcessor</code> */
	public void testProcessorNotImplementingIConQATProcessor() {
		checkException(ProcessorWithoutInterface.class,
				EDriverExceptionType.PROCESSOR_CLASS_NOT_IMPLEMENTS_INTERFACE);
	}

	/** Test an unknown processor class. */
	public void testUnknownProcessorClass() {
		checkException("some.class.which.does.not.exists",
				EDriverExceptionType.PROCESSOR_CLASS_NOT_FOUND);
	}

	/** Test processor without annotation. */
	public void testProcessorWithoutAnnotation() {
		checkException(ProcessorWithoutAnnotation.class,
				EDriverExceptionType.PROCESSOR_CLASS_NOT_ANNOTATED);
	}

	/** Test generic processor. */
	public void testGenericProcessor() {
		checkException(GenericProcessorBase.class,
				EDriverExceptionType.GENERIC_PROCESSOR_CLASS);
	}

	/** Test processor without parameterless constructor. */
	public void testProcessorWithoutParameterlessConstructor() {
		checkException(
				ProcessorWithoutParameterlessConstructor.class,
				EDriverExceptionType.PROCESSOR_CLASS_WITHOUT_PARAMETERLESS_CONSTRUCTOR);
	}

	/** Test processor without public constructor. */
	public void testProcessorWithoutPublicConstructor() {
		checkException(ProcessorWithoutPublicConstructor.class,
				EDriverExceptionType.PROCESSOR_CLASS_WITHOUT_PUBLIC_CONSTRUCTOR);
	}

	/** Test processor with different types in the pipeline. */
	public void testProcessorWithTypeConflictInPipeline() {
		checkException(ProcessorWithIncompatiblePipeline.class,
				EDriverExceptionType.INCOMPATIBLE_PIPELINE_TYPES);
	}

	/** Test processor with duplicate name for parameter. */
	public void testProcessorWithDuplicateParameterName() {
		checkException(ProcessorWithDuplicateParameterName.class,
				EDriverExceptionType.DUPLICATE_PARAM_NAME);
	}

	/** Test a processor having a generic return value. */
	public void testGenericReturnValue() throws Exception {
		ProcessorSpecification spec = new ProcessorSpecification(
				GenericProcessorInstance.class.getName());

		assertEquals(new ClassType(Double.class),
				spec.getOutputs()[0].getType());
	}

	/**
	 * Test a processor having a generic return value in the context of an
	 * abstract base class. This documents bug #3271.
	 */
	public void testGenericReturnValueForAbstractBase() throws Exception {
		ProcessorSpecification spec = new ProcessorSpecification(
				AbstractGenericProcessorInstance.class.getName());

		assertEquals(new ClassType(IConQATLogger.class),
				spec.getOutputs()[0].getType());
	}

	/** Test a processor having a generic method parameter. */
	public void testGenericMethodParameter() throws Exception {
		ProcessorSpecification spec = new ProcessorSpecification(
				GenericProcessorInstance.class.getName());

		assertEquals(new ClassType(Double.class), spec.getParameter("param")
				.getAttributes()[0].getType());

		assertEquals(new ClassType(List.class), spec.getParameter("pls")
				.getAttributes()[0].getType());
		assertEquals(new ClassType(List.class), spec.getParameter("plq")
				.getAttributes()[0].getType());
		assertEquals(new ClassType(List.class), spec.getParameter("plx")
				.getAttributes()[0].getType());
	}

	/**
	 * Test a processor having a generic method parameter with an abstract base
	 * class.
	 */
	public void testGenericMethodParameterForAbstractBase() throws Exception {
		ProcessorSpecification spec = new ProcessorSpecification(
				AbstractGenericProcessorInstance.class.getName());

		assertEquals(new ClassType(IConQATLogger.class),
				spec.getParameter("param").getAttributes()[0].getType());
	}

	/** Test a processor having a generic exposed field. */
	public void testGenericField() throws Exception {
		ProcessorSpecification spec = new ProcessorSpecification(
				GenericProcessorInstance.class.getName());
		assertEquals(new ClassType(Double.class), spec.getParameter("field")
				.getAttributes()[0].getType());
	}

	/**
	 * Test a processor having a generic exposed field with an abstract base
	 * class.
	 */
	public void testGenericFieldForAbstractBase() throws Exception {
		ProcessorSpecification spec = new ProcessorSpecification(
				AbstractGenericProcessorInstance.class.getName());
		assertEquals(new ClassType(IConQATLogger.class),
				spec.getParameter("field").getAttributes()[0].getType());
	}
}