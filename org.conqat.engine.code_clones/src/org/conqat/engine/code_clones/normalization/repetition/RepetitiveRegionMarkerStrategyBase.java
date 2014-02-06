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
package org.conqat.engine.code_clones.normalization.repetition;

import java.io.Serializable;
import java.util.List;

import org.conqat.engine.code_clones.core.Unit;
import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.sourcecode.analysis.SourceCodeElementRegionMarkerStrategyBase;
import org.conqat.lib.commons.region.RegionSet;

/**
 * Base class for strategies that mark repetitive regions.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 41159 $
 * @ConQAT.Rating GREEN Hash: 193708C3C92C136E335B82627121F57D
 */
public abstract class RepetitiveRegionMarkerStrategyBase extends
		SourceCodeElementRegionMarkerStrategyBase implements Serializable {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	/** Repetition detection parameters */
	protected RepetitionParameters repetitionParameters = null;

	/** ConQAT Parameter */
	@AConQATParameter(name = "min", description = "Minimal size of repetition", minOccurrences = 1, maxOccurrences = 1)
	public void setMinLength(
			@AConQATAttribute(name = ConQATParamDoc.REPETITION_MIN_LENGTH_NAME, description = ConQATParamDoc.REPETITION_MIN_LENGTH_DESC) int minLength,
			@AConQATAttribute(name = ConQATParamDoc.REPETITION_MIN_INSTANCES_NAME, description = ConQATParamDoc.REPETITION_MIN_INSTANCES_DESC) int minMotifInstances,
			@AConQATAttribute(name = ConQATParamDoc.REPETITION_MIN_MOTIF_LENGTH_NAME, description = ConQATParamDoc.REPETITION_MIN_MOTIF_LENGTH_DESC) int minMotifLength,
			@AConQATAttribute(name = ConQATParamDoc.REPETITION_MAX_MOTIF_LENGTH_NAME, description = ConQATParamDoc.REPETITION_MAX_MOTIF_LENGTH_DESC) int maxMotifLength)
			throws ConQATException {
		repetitionParameters = new RepetitionParameters(minLength,
				minMotifLength, maxMotifLength, minMotifInstances);
	}

	/** Sets {@link RepetitionParameters} for programmatic use of the strategy. */
	/* package */void setRepetitionParameters(
			RepetitionParameters repetitionParameters) {
		this.repetitionParameters = repetitionParameters;
	}

	/** Run detector and mark regions */
	protected <U extends Unit> void markRegions(RegionSet result,
			RepetitionFinder<U> detector) {
		List<Repetition<U>> repetitions = detector.findRepetitions(
				repetitionParameters.getMinMotifLength(),
				repetitionParameters.getMaxMotifLength());

		for (Repetition<U> repetition : repetitions) {
			result.add(RepetitionUtils.regionFor(repetition));
		}
	}
}