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
package org.conqat.engine.code_clones.result.annotation;

import org.conqat.engine.code_clones.core.Clone;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.lib.commons.assessment.Assessment;
import org.conqat.lib.commons.assessment.ETrafficLightColor;
import org.conqat.lib.commons.collections.UnmodifiableList;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: juergens $
 * @version $Rev: 34670 $
 * @ConQAT.Rating GREEN Hash: DA1CC33C51725506C18BA53190674D5B
 */
@AConQATProcessor(description = "Rates elements with clones red, elements without clones green.")
public class CloneAssessmentAnnotator extends CloneAnnotatorBase {

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Key for clone assessment", type = "org.conqat.lib.commons.assessment.Assessment")
	public static final String CLONE_ASSESSMENT_KEY = "Clone Assessment";

	/** Makes clone assessment key visible */
	@Override
	protected String[] getKeys() {
		return new String[] { CLONE_ASSESSMENT_KEY };
	}

	/** Assesses elements based on their clones */
	@Override
	protected void annotateClones(ITextElement element,
			UnmodifiableList<Clone> clonesList) {
		Assessment assessment;
		if (clonesList.isEmpty()) {
			assessment = new Assessment(ETrafficLightColor.GREEN);
		} else {
			assessment = new Assessment(ETrafficLightColor.RED);
		}

		element.setValue(CLONE_ASSESSMENT_KEY, assessment);
	}

}