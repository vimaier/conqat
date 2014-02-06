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
import org.conqat.engine.core.core.ConQATException;

/**
 * Interface that defines constraints on {@link CloneClass}es. Constraints can
 * be evaluated on a single clone class, independent of other {@link CloneClass}
 * es in the same detection result.
 * 
 * @author juergens
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 34021DB46ECFFEF67FB810D48EAC15D7
 */
public interface ICloneClassConstraint {

	/** Returns true, if constraint is satisfied, false if not */
	public boolean satisfied(CloneClass cloneClass) throws ConQATException;

}