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
package org.conqat.engine.commons.assessment;

import org.conqat.engine.core.core.AConQATProcessor;

/**
 * An assessor based on a single double value.
 * 
 * @author Benjamin Hummel
 * @author $Author: hummelb $
 * @version $Rev: 37013 $
 * @ConQAT.Rating GREEN Hash: 3BB3924831A0FB85756AF17D358E40EB
 */
@AConQATProcessor(description = "This processor creates an assessment based on a "
		+ "numeric value stored in a key. For this value ranges with assigned "
		+ "colors can be specified. The resulting assessment for a node is the color "
		+ "of the first range containing the value or a default color if no range "
		+ "contained the value. Default is to assess all nodes.")
public class DoubleAssessor extends RangeBasedAssessorBase<Number> {

	/** {@inheritDoc} */
	@Override
	protected double obtainDouble(Number value) {
		return value.doubleValue();
	}
}