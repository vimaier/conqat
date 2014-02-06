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
package org.conqat.engine.code_clones.core.constraint;

import org.conqat.engine.code_clones.core.CloneClass;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * {@ConQAT.Doc}
 * 
 * @author juergens
 * @author $Author: juergens $
 * @version $Rev: 34670 $
 * @ConQAT.Rating GREEN Hash: 83BB1FE2C65F33B9ABD9367E5F21DA17
 */
@AConQATProcessor(description = "Constraint that requires minimal and (optional) maximal clone class cardinalities")
public class CardinalityConstraint extends ConstraintBase {

	/** Constant used to denote infinity */
	public static final int INFINITY = -1;

	/** Maximal number of required clones */
	private int max = INFINITY;

	/** Minimal number of required clones */
	private int min;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "cardinality", minOccurrences = 1, maxOccurrences = 1, description = "Cardinality thresholds")
	public void setCardinality(
			@AConQATAttribute(name = "min", description = "Minimal number of clones") int min,
			@AConQATAttribute(name = "max", description = "Maximal number of clones. Default is infinity. Use "
					+ INFINITY + " for infinity.", defaultValue = "" + INFINITY) int max) {
		this.min = min;
		this.max = max;
	}

	/** {@inheritDoc} */
	@Override
	public boolean satisfied(CloneClass cloneClass) {
		int size = cloneClass.getClones().size();
		return size >= min && (max == INFINITY || size <= max);
	}

}