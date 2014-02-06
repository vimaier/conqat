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

import org.conqat.lib.commons.assessment.Assessment;
import org.conqat.lib.commons.assessment.ETrafficLightColor;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * This processor creates an assessment based on a boolean value.
 * 
 * @author Benjamin Hummel
 * @author $Author: hummelb $
 * @version $Rev: 37013 $
 * @ConQAT.Rating GREEN Hash: 7C52B2F7A7C9B208D8F8CA5C59BC24EC
 * 
 */
@AConQATProcessor(description = "This processor creates an assessment based "
		+ "on a boolean value stored in a key. The resulting assessment for "
		+ "a node is GREEN if the value is true, RED otherwise. "
		+ "Default is to assess all nodes.")
public class BooleanAssessor extends LocalAssessorBase<Boolean> {

	/** {@inheritDoc} */
	@Override
	protected Assessment assessValue(Boolean value) {
		if (value.booleanValue()) {
			return new Assessment(ETrafficLightColor.GREEN);
		}
		return new Assessment(ETrafficLightColor.RED);
	}

}