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
package org.conqat.engine.code_clones.result.diff;

import org.conqat.engine.code_clones.detection.CloneDetectionResultElement;
import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;

/**
 * Base class for processors that compute differences between clone reports.
 * 
 * @author juergens
 * @author $Author: juergens $
 * @version $Rev: 34670 $
 * @ConQAT.Rating GREEN Hash: 7116D700E751FD659E1B9608E9C8489E
 */
public abstract class CloneDetectionResultDifferBase extends
		ConQATProcessorBase {

	/** Detection result that represents before state */
	protected CloneDetectionResultElement before;

	/** Detection result that represents after state */
	protected CloneDetectionResultElement after;

	/** ConQAT Parameter */
	@AConQATParameter(name = "detection-result-before", minOccurrences = 1, maxOccurrences = 1, description = ""
			+ "Clone detection results that represent before state")
	public void setDetectionResultBefore(
			@AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC) CloneDetectionResultElement before) {
		this.before = before;
	}

	/** ConQAT Parameter */
	@AConQATParameter(name = "detection-result-after", minOccurrences = 1, maxOccurrences = 1, description = ""
			+ "Clone detection results that represent after state")
	public void setDetectionResultAfter(
			@AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC) CloneDetectionResultElement after) {
		this.after = after;
	}

}