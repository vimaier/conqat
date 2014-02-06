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
import org.conqat.engine.core.driver.util.Multiplicity;

/**
 * Tests for {@link ParameterMultiplicityInferer}.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 6EEB2E2D50AFB88818D1721FF36EC113
 */
public class ParameterMultiplicityInfererTest extends InfererTestBase {

	/** Test the case that a parameter is given too seldom. */
	public void testTooSeldom() {
		expectException("parammultTooSeldom",
				EDriverExceptionType.PARAMETER_OCCURS_NOT_OFTEN_ENOUGH);
	}

	/** Test the case that a parameter is given too often. */
	public void testTooOften() {
		expectException("parammultTooOften",
				EDriverExceptionType.PARAMETER_OCCURS_TOO_OFTEN);
	}

	/** This tests the multiplicity part of inference. */
	public void testMultiplicityInference() throws DriverException {
		spec = loadBlock("parammultMultiplicities");
		spec.initialize();

		assertParamMult("mAny", new Multiplicity(0, Multiplicity.INFINITY));
		assertParamMult("m01", new Multiplicity(0, 1));
		assertParamMult("m1", new Multiplicity(1, Multiplicity.INFINITY));
		assertParamMult("m38", new Multiplicity(3, 8));

		assertParamMult("m4", new Multiplicity(4, Multiplicity.INFINITY));
		assertParamMult("m37", new Multiplicity(3, 7));
		assertParamMult("m47", new Multiplicity(4, 7));
	}

	/** This tests the multiplicity part of inference for more complex cases. */
	public void testComplexMultiplicities() throws DriverException {
		spec = loadBlock("parammultComplexMultiplicities");
		spec.initialize();

		assertParamMult("mAny1", new Multiplicity(0, Multiplicity.INFINITY));
		assertParamMult("mAny2", new Multiplicity(0, Multiplicity.INFINITY));
		assertParamMult("m25", new Multiplicity(2, 5));

		assertParamMult("m24", new Multiplicity(2, 4));
		assertParamMult("m13", new Multiplicity(1, 3));

		assertParamMult("m11", new Multiplicity(1, 1));
	}

	/**
	 * This tests the multiplicity part of inference for the case of split
	 * parameters.
	 */
	public void testMultiplicitiesSplitCases() throws DriverException {
		spec = loadBlock("parammultSplitMultiplicities");
		spec.initialize();

		assertParamMult("mAny1", new Multiplicity(0, Multiplicity.INFINITY));
		assertParamMult("mAny2", new Multiplicity(0, Multiplicity.INFINITY));

		assertParamMult("m3-1", new Multiplicity(2, 2));
		assertParamMult("m3-2", new Multiplicity(2, 2));
	}

	/** Test the case of an empty infered multiplcity. */
	public void testInvalidMultiplicity() {
		expectException("parammultInvalidMult1",
				EDriverExceptionType.EMPTY_INFERED_PARAMETER_INTERVAL);
		expectException("parammultInvalidMult2",
				EDriverExceptionType.EMPTY_INFERED_PARAMETER_INTERVAL);
	}

	/**
	 * Test the case of a parameter which is given twice, each time referencing
	 * another input.
	 */
	public void testDuplicateParameterRefSameInput() {
		expectException("parammultDuplicateParamRef1",
				EDriverExceptionType.MULTIPLE_INPUT_REFERENCES);
		expectException("parammultDuplicateParamRef2",
				EDriverExceptionType.MULTIPLE_INPUT_REFERENCES);
	}
}