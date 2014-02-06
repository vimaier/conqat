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
package org.conqat.engine.commons.assessment;

import java.util.Set;

import org.conqat.lib.commons.clone.IDeepCloneable;

/**
 * This interface defines an object that stores assessment ranges.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 548468F6717490D4D477E96CB4BCE04C
 */
public interface IAssessmentRangesDefinition extends IDeepCloneable {

	/**
	 * Provided with extrema of a metric distribution, this returns the ranges
	 * specified by this range definition. The minValue must not be greater than
	 * the maxValue.
	 */
	Set<AssessmentRange> obtainRanges(double minValue, double maxValue);

	/**
	 * Returns true if this definition defines a range with the specified name.
	 * Note that this does not automatically imply that
	 * {@link #obtainRanges(double, double)} will return a range with this name,
	 * as the range could be excluded as no measurement data was found for it.
	 */
	boolean hasRangeDefinition(String rangeName);

	/** Obtain the range definition for the specified value. */
	AssessmentRangeDefinition obtainRangeDefinition(double value);
}
