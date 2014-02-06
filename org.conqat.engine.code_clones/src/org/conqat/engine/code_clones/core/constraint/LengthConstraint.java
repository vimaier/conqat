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
import org.conqat.engine.core.core.ConQATException;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 37494 $
 * @ConQAT.Rating GREEN Hash: 2E205B5111BF902EDD6CD01E12C9632B
 */
@AConQATProcessor(description = "Constraint that requires minimal normalized clone length.")
public class LengthConstraint extends ConstraintBase {

	/** Minimal clone length */
	private int min;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "length", minOccurrences = 1, maxOccurrences = 1, description = "Length threshold")
	public void setMinLength(
			@AConQATAttribute(name = "min", description = "Minimal normalized length (inclusive).") int min)
			throws ConQATException {
		if (min <= 0) {
			throw new ConQATException("Minimal length must be positive");
		}
		this.min = min;
	}

	/** {@inheritDoc} */
	@Override
	public boolean satisfied(CloneClass cloneClass) {
		return cloneClass.getNormalizedLength() >= min;
	}
}