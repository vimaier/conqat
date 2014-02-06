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
 * @ConQAT.Rating GREEN Hash: 90808C700971E8D75EDCAD0C39AF2599
 */
@AConQATProcessor(description = "Constraint that is satisfied if not all contained clones are marked as covered.")
public class NotAllCoveredConstraint extends ConstraintBase {

	/** {@inheritDoc} */
	@Override
	public boolean satisfied(CloneClass cloneClass) {
		return !EBooleanStoredValue.COVERED.trueForAllClones(cloneClass);
	}

}