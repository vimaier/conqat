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
import org.conqat.engine.code_clones.core.utils.EBooleanStoredValue;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * {@ConQAT.Doc}
 * 
 * @author Elmar Juergens
 * @author $Author: juergens $
 * @version $Rev: 34670 $
 * @ConQAT.Rating GREEN Hash: 15092208C37BFFA8E2E09CD2B90DF5EB
 */
@AConQATProcessor(description = "Constraint that is only satisfied if a clone class contains no ghosts.")
public class NoGhostConstraint extends ConstraintBase {

	/** {@inheritDoc} */
	@Override
	public boolean satisfied(CloneClass cloneClass) {
		return EBooleanStoredValue.GHOST.falseForAllClones(cloneClass);
	}

}