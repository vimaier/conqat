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

/**
 * An {@link AssessmentRangeDefinition} defines an assessment range through an
 * upper boundary (inclusive).
 * 
 * The name of the assessment range is required to address the range. However,
 * it may also be shown to the user.
 * 
 * An assessment range is associated with a color. This is mainly used for
 * visualization purposes. Hence, it is not 100% clean to store this here.
 * However, this greatly simplifies (and reduces redundancy) of visualizing
 * processors.
 * 
 * This class is immutable.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 02110893BE9119058669D675A1421453
 */
public class AssessmentRangeDefinition implements
		Comparable<AssessmentRangeDefinition> {

	/** The boundary. */
	private final double upperBoundary;

	/** The color. */
	private final Color color;

	/** The name. */
	private final String name;

	/** Constructor. */
	public AssessmentRangeDefinition(double upperBoundary, Color color, String name) {
		this.upperBoundary = upperBoundary;
		this.color = color;
		this.name = name;
	}

	/** Compares the boundaries. */
	@Override
	public int compareTo(AssessmentRangeDefinition other) {
		return Double.compare(upperBoundary, other.upperBoundary);
	}

	/** Get boundary. */
	public double getUpperBoundary() {
		return upperBoundary;
	}

	/** Get color. */
	public Color getColor() {
		return color;
	}

	/** Get name. */
	public String getName() {
		return name;
	}

	/** String representation includes boundary and name. */
	@Override
	public String toString() {
		return upperBoundary + ":" + name;
	}

}