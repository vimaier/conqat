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

import java.util.Collection;

import org.conqat.engine.core.core.AConQATProcessor;

/**
 * This processor creates an assessment based on the length of a list stored in
 * a key. For this value ranges with assigned colors can be specified. The
 * resulting assessment for a node is the color of the first range containing
 * the value or a default color of no range contained the value.
 * 
 * @author Tilman Seifert
 * @author $Author: hummelb $
 * @version $Rev: 37013 $
 * @ConQAT.Rating GREEN Hash: 7A021E9BC9F3D5E52DE453382E9CE1CD
 * 
 */
@AConQATProcessor(description = "This processor creates an assessment based on "
		+ "the size of a collection stored in a key. For this value ranges with assigned "
		+ "colors can be specified. The resulting assessment for a node is the color "
		+ "of the first range containing the value or a default color if no range "
		+ "contains the value. Default is to assess all nodes.")
public class ListLengthAssessor extends RangeBasedAssessorBase<Collection<?>> {

	/** {@inheritDoc} */
	@Override
	protected double obtainDouble(Collection<?> value) {
		return value.size();
	}
}