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

import java.util.Date;

import org.conqat.engine.core.core.AConQATProcessor;

/**
 * Colors the provided nodes based on the stored date value, where each value is
 * assigned a color using the color gradient defined using single points which
 * are linearly interpolated.
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 5781F3B054F8D877E38022D9CFD2C7E7
 */
@AConQATProcessor(description = "Colors the provided nodes based on the stored date value, where "
		+ "each value is assigned a color using the color gradient defined using single points "
		+ "which are linearly interpolated. ")
public class DateGradientColorizer extends
		GradientColorizerBase<Date, Date> {

	/** {@inheritDoc} */
	@Override
	protected Date convertFromDouble(double d) {
		return new Date((long) d);
	}

	/** {@inheritDoc} */
	@Override
	protected double convertToDouble(Date e) {
		return e.getTime();
	}

	/** {@inheritDoc} */
	@Override
	protected Date doubleToInput(double d) {
		return new Date((long) d);
	}

	/** {@inheritDoc} */
	@Override
	protected double inputToDouble(Date i) {
		return i.getTime();
	}
}