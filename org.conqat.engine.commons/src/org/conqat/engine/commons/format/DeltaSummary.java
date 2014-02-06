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
package org.conqat.engine.commons.format;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.assessment.ETrafficLightColor;

/**
 * This class describes a delta summary, which consists of a value, an
 * additional baseline value, a corresponding formatter, and a color that is
 * used to indicate whether the difference between the baseline value and the
 * current value is positive, negative, or none of the two.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 41090 $
 * @ConQAT.Rating GREEN Hash: 767D88289618303498B7EC897AFF1F5F
 */
public class DeltaSummary extends Summary {

    /** An optional baseline value to which the actual value is compared. */
    private final double baselineValue;
    
    /** The color assigned to this summary. */
    private final ETrafficLightColor color;
    
    /** Constructs a new delta summary with the default formatter. */
    public DeltaSummary(double baselineValue, double value,
            ETrafficLightColor color) {
        this(baselineValue, value, EValueFormatter.DEFAULT, color);
    }

    /** Constructs a new delta summary with the given formatter. */
    public DeltaSummary(double baselineValue, double value,
            EValueFormatter formatter, ETrafficLightColor color) {
        super(value, formatter);
        this.baselineValue = baselineValue;
        this.color         = color;
    }
    
    /** Retrieves the color assigned to this summary. */
    public ETrafficLightColor getColor() {
        return color;
    }

    /** Retrieves the difference between the actual and the baseline value. */
    public double getDelta() {
        return getValue() - baselineValue;
    }
    
    /** Retrieves the actual value. */
    @Override
    public Double getValue() {
        return (Double)super.getValue();
    }
    
    /** 
     * Retrieves a string representation of this summary which is the difference
     * of the actual and the baseline value preceded by a - or +.
     */
    public String formatDelta() {
        String result;
        try {
            result = String.valueOf(formatter.format(getDelta()));
        } catch (ConQATException e) {
            result =  String.valueOf(getDelta());
        }
        if (getDelta() > 0) {
            result = "+" + result;
        }
        return result;
    }

}
