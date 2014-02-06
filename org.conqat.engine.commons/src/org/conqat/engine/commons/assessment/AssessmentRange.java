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

import java.awt.Color;

import org.conqat.lib.commons.math.Range;

/**
 * An assessment range is a {@link Range} with a color and a name.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 44615 $
 * @ConQAT.Rating GREEN Hash: FC684C31DA0517C285398079EB47C0B9
 */
public class AssessmentRange extends Range {

	/** Version for serialization. */
	private static final long serialVersionUID = 1;

	/** The color. */
	private final Color color;

	/** The name. */
	private final String name;

	/**
	 * Create range.
	 * 
	 * @param lower
	 *            the lower boundary
	 * @param lowerIsInclusive
	 *            a flag that indicates if the lower boundary is inclusive or
	 *            not.
	 * @param rangeDefinition
	 *            the range definition specifies the upper boundary, the color
	 *            and the name.
	 */
	public AssessmentRange(double lower, boolean lowerIsInclusive,
			AssessmentRangeDefinition rangeDefinition) {
		super(lower, lowerIsInclusive, rangeDefinition.getUpperBoundary(), true);
		this.color = rangeDefinition.getColor();
		this.name = rangeDefinition.getName();
	}

	/** Get the color associated with this range. */
	public Color getColor() {
		return color;
	}

	/** Get name. */
	public String getName() {
		return name;
	}

	/** The string representation includes the boundaries and the name. */
	@Override
	public String toString() {
		return name + " " + super.toString();
	}

}
