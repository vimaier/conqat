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
package org.conqat.engine.html_presentation.color;

import org.conqat.engine.core.core.AConQATProcessor;

/**
 * Assigns colors based on numerical keys using a gradient color scheme.
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 833D6BC099212239ACC0E047A2D7E907
 */
@AConQATProcessor(description = "Colors the provided nodes based on the stored numeric value, where "
		+ "each value is assigned a color using the color gradient defined using single points "
		+ "which are linearly interpolated. ")
public class NumberGradientColorizer extends
		GradientColorizerBase<Number, Double> {

	/** {@inheritDoc} */
	@Override
	protected Number convertFromDouble(double d) {
		return new Double(d);
	}

	/** {@inheritDoc} */
	@Override
	protected double convertToDouble(Number e) {
		return e.doubleValue();
	}

	/** {@inheritDoc} */
	@Override
	protected Double doubleToInput(double d) {
		return new Double(d);
	}

	/** {@inheritDoc} */
	@Override
	protected double inputToDouble(Double i) {
		return i.doubleValue();
	}
}