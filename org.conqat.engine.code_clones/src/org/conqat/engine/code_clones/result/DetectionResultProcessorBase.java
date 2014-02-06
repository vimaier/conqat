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
package org.conqat.engine.code_clones.result;

import java.util.List;

import org.conqat.engine.code_clones.core.CloneClass;
import org.conqat.engine.code_clones.detection.CloneDetectionResultElement;
import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;

/**
 * Base class for processors that work on {@link CloneDetectionResultElement}s.
 * 
 * @author juergens
 * @author $Author: juergens $
 * @version $Rev: 34670 $
 * @ConQAT.Rating GREEN Hash: E6082725EE30DA71D5CD68376B081726
 */
public abstract class DetectionResultProcessorBase extends ConQATProcessorBase {

	/** Input {@link CloneDetectionResultElement} */
	protected CloneDetectionResultElement detectionResult;

	/** ConQAT Parameter */
	@AConQATParameter(name = "detection-result", description = ConQATParamDoc.INPUT_DESC, minOccurrences = 1, maxOccurrences = 1)
	public void setDetectionResult(
			@AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC) CloneDetectionResultElement detectionResult) {
		this.detectionResult = detectionResult;
	}

	/**
	 * Create a detection result based on the original detection result and a
	 * list of {@link CloneClass}es.
	 */
	protected CloneDetectionResultElement detectionResultForCloneClasses(
			List<CloneClass> pairClasses) {
		return new CloneDetectionResultElement(detectionResult.getSystemDate(),
				detectionResult.getRoot(), pairClasses,
				detectionResult.getUnits());
	}

}