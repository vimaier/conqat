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

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.regions.RegionMarkerBase;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.resource.ITokenResource;

/**
 * Base class for processors that mark repetitive regions.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 37519 $
 * @ConQAT.Rating GREEN Hash: 2D66522251FBB2A3906D894184ACC5E4
 */
public abstract class RepetitiveRegionMarkerBase
		extends
		RegionMarkerBase<ITokenResource, ITokenElement, RepetitiveRegionMarkerStrategyBase> {

	/** Common documentation */
	protected static final String DOC = "Works only for languages for which a "
			+ "TokenOracleFactory is available. Just as holds for other region "
			+ "markers, region information can then be used to steer normalization. Regions "
			+ "of repetitive code can e.g. be normalized more conservatively.";

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

	/** {@inheritDoc} */
	@Override
	protected void setStrategyParameters(
			RepetitiveRegionMarkerStrategyBase strategy) {
		strategy.setRepetitionParameters(repetitionParameters);
	}

	/** {@inheritDoc} */
	@Override
	protected Class<ITokenElement> getElementClass() {
		return ITokenElement.class;
	}

}