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

import org.conqat.engine.core.core.AConQATProcessor;

/**
 * This processor calculates the difference between two numerical values.
 * 
 * @author Benjamin Hummel
 * @author $Author: hummelb $
 * @version $Rev: 36404 $
 * @ConQAT.Rating GREEN Hash: 2E346445C1917449E3AD60DF111AD93E
 */
@AConQATProcessor(description = "Calculates the difference between two numerical values."
		+ " The second argument is subtracted from the first. "
		+ "Default is to calculate on all nodes.")
public class DifferenceCalculator extends CalculatorBase {

	/** {@inheritDoc} */
	@Override
	protected double calculate(double arg1, double arg2) {
		return arg1 - arg2;
	}

}