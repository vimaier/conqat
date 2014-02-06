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
package org.conqat.engine.commons.string;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.bool.ConditionBase;
import org.conqat.engine.commons.pattern.PatternList;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 37522 $
 * @ConQAT.Rating GREEN Hash: 645C50DED4BE5B5D3F9F64C3968C8DF2
 */
@AConQATProcessor(description = "Returns true if the given string matches at least on of the pattern.")
public class MatchesAnyCondition extends ConditionBase {

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = ConQATParamDoc.INPUT_NAME, attribute = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_DESC)
	public String input = null;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = ConQATParamDoc.PATTERN_LIST, attribute = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.PATTERN_LIST_DESC)
	public PatternList patterns = null;

	/** {@inheritDoc} */
	@Override
	protected boolean evaluateCondition() {
		return patterns.matchesAny(input);
	}
}
