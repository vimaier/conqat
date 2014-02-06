/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2011 the ConQAT Project                                   |
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
package org.conqat.engine.dotnet.spcop;

import org.conqat.lib.commons.assessment.ETrafficLightColor;

/**
 * Value set for attribute {@link ESPCopAttribute#Severity} of
 * {@link ESPCopElement#Notification} in SpCop result XMLs.
 * 
 * @author $Author: pfaller $
 * @version $Rev: 47078 $
 * @ConQAT.Rating YELLOW Hash: D1589CCEDCBD1ECBD27E0578438B8983
 */
public enum ESpCopSeverity {

	/** Value for warning */
	Warning(ETrafficLightColor.YELLOW),

	/** Value for critical warning */
	CriticalWarning(ETrafficLightColor.YELLOW),

	/** Value for error */
	Error(ETrafficLightColor.RED),

	/** Value for critical Error */
	CriticalError(ETrafficLightColor.RED);

	/**
	 * The assigned color for a severity
	 */
	private final ETrafficLightColor color;

	/**
	 * Constructor
	 */
	private ESpCopSeverity(ETrafficLightColor color) {
		this.color = color;
	}

	/** Gets color. */
	public ETrafficLightColor getColor() {
		return color;
	}
}
