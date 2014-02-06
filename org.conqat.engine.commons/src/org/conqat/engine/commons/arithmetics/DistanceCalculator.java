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
 * This processor calculates the distance between two numerical values.
 * 
 * @author Benjamin Hummel
 * @author $Author: hummelb $
 * @version $Rev: 36404 $
 * @ConQAT.Rating GREEN Hash: E72788ACC91C4CD5584F0118C6C1CB40
 */
@AConQATProcessor(description = ""
		+ "Calculates the distance between two numerical values, "
		+ "i.e. the absolute value of the difference. "
		+ "Default is to calculate on all nodes.")
public class DistanceCalculator extends CalculatorBase {

	/**
	 * Calculates the distance between two numerical values, i.e. the absolute
	 * value of the difference.
	 */
	@Override
	protected double calculate(double arg1, double arg2) {
		if (arg1 < arg2) {
			return arg2 - arg1;
		}
		return arg1 - arg2;
	}

}