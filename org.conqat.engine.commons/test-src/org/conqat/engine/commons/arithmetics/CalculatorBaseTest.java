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
package org.conqat.engine.commons.arithmetics;

import java.util.Collections;

import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.test.ConQATCommonsProcessorTestCaseBase;

/**
 * This class contains the tests for all subclasses of {@link CalculatorBase}.
 * They are collected here as most tests are that simple, that creating separate
 * classes would be too much overhead.
 * 
 * @author hummelb
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 * @levd.rating GREEN Hash: 820313ABC39DFECBA1A44E37D993DD7A
 */
public class CalculatorBaseTest extends ConQATCommonsProcessorTestCaseBase {

	/** Tests the {@link DifferenceCalculator}. */
	public void testDifferenceCalculator() throws Exception {
		testCalculatorBase(DifferenceCalculator.class, 7, 3, 4);
		testCalculatorBase(DifferenceCalculator.class, .5, 55, -54.5);
	}

	/** Tests the {@link DistanceCalculator}. */
	public void testDistanceCalculator() throws Exception {
		testCalculatorBase(DistanceCalculator.class, 7, 3, 4);
		testCalculatorBase(DistanceCalculator.class, .5, 55, 54.5);
	}

	/** Tests the {@link DivisionCalculator}. */
	public void testDivisionCalculator() throws Exception {
		testCalculatorBase(DivisionCalculator.class, 7, -2, -3.5);
		testCalculatorBase(DivisionCalculator.class, 15, .5, 30);
		testCalculatorBase(DivisionCalculator.class, 15, -0,
				Double.POSITIVE_INFINITY);
		testCalculatorBase(DivisionCalculator.class, -15, 0,
				Double.NEGATIVE_INFINITY);
		testCalculatorBase(DivisionCalculator.class, 0, 0, Double.NaN);

	}

	/** Tests the {@link ProductCalculator}. */
	public void testProductCalculator() throws Exception {
		testCalculatorBase(ProductCalculator.class, 7, -2, -14);
		testCalculatorBase(ProductCalculator.class, 15, .5, 7.5);
	}

	/** Tests the {@link SumCalculator}. */
	public void testSumCalculator() throws Exception {
		testCalculatorBase(SumCalculator.class, 7, -2, 5);
		testCalculatorBase(SumCalculator.class, 15, .5, 15.5);
	}

	/** Tests calculation for a */
	private void testCalculatorBase(
			Class<? extends CalculatorBase> processorClass, double a1,
			double a2, double result) throws Exception {
		Object input = parseCQDDL("listNode(root, (a1=", a1, ", a2=", a2, "), "
				+ "listNode(leaf, (a1=", a1, ", a2=", a2, ")), "
				+ "listNode(missing, (a1=4)) )");
		IConQATNode expected = (IConQATNode) parseCQDDL("listNode(root, (r=",
				result, "), " + "listNode(leaf, (r=", result, ")), "
						+ "listNode(missing, (r=nan())) )");

		IConQATNode received = (IConQATNode) executeProcessor(processorClass,
				"(keys=(arg1=a1, arg2=a2, result=r), input=(ref=", input, "))");

		assertSame("Should be pipeline processor!", input, received);
		assertNodesEqual(expected, received, Collections.singleton("r"));
	}
}