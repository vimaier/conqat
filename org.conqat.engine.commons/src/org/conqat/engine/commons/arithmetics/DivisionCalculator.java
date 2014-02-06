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

import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * This processor calculates the ratio between two numerical values.
 * 
 * @author Benjamin Hummel
 * @author $Author: hummelb $
 * @version $Rev: 36404 $
 * @ConQAT.Rating GREEN Hash: D76E4E2F75702A164594320D76B28633
 */
@AConQATProcessor(description = "Calculates the ratio between two numerical values. "
		+ "Default is to calculate on all nodes. The first argumment is divided "
		+ "by the second argument.")
public class DivisionCalculator extends CalculatorBase {

	/** Value that gets returned upon devision by zero */
	private Double divisionByZeroReturnValue = null;

	/** ConQAT Parameter */
	@AConQATParameter(name = "divisionByZero", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "Value that gets returned upon division by zero")
	public void setDivisionByZeroReturnValue(
			@AConQATAttribute(name = "return", description = "If this value is not set, the standard java division behaviour is exhibited")
			double divisionByZeroReturnValue) {
		this.divisionByZeroReturnValue = divisionByZeroReturnValue;
	}

	/**
	 * Calculates the ratio between two numerical values. Returns
	 * {@link Double#NEGATIVE_INFINITY} or {@link Double#POSITIVE_INFINITY} if
	 * the second argument is zero, {@link Double#NaN} if both arguments are
	 * zero.
	 */
	@Override
	protected double calculate(double arg1, double arg2) {
		if (arg2 == 0) {
			if (divisionByZeroReturnValue != null) {
				return divisionByZeroReturnValue;
			}

			if (arg1 == 0) {
				return Double.NaN;
			}
			if (arg1 < 0) {
				return Double.NEGATIVE_INFINITY;
			}
			return Double.POSITIVE_INFINITY;
		}
		
		return arg1 / arg2;
	}

}