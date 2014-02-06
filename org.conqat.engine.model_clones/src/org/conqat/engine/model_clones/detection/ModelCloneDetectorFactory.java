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
package org.conqat.engine.model_clones.detection;

import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.logging.IConQATLogger;
import org.conqat.engine.model_clones.detection.clustering.CloneClusterer;
import org.conqat.engine.model_clones.detection.pairs.PairDetector;
import org.conqat.engine.model_clones.detection.util.AugmentedModelGraph;
import org.conqat.engine.model_clones.detection.util.ICloneReporter;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: deissenb $
 * @version $Rev: 36615 $
 * @ConQAT.Rating GREEN Hash: E896EB08D9BACEFC207A107D96572D67
 */
@AConQATProcessor(description = "This processor creates a model clone detector which works as "
		+ "described in the paper by Deissenboeck, Hummel, Juergens, Schaetz, Wagner, Girard, Teuchert: "
		+ "\"Clone Detection in Automotive Model-Based Development\".")
public class ModelCloneDetectorFactory extends ModelCloneDetectorFactoryBase {

	/** Flag indicating whether to use early exit strategy. */
	private boolean earlyExit = true;

	/** Flag indicating whether the inclusion analysis is performed. */
	private boolean inclusionAnalysis = false;

	/** Flag indicating whether overlaps in clones should be removed. */
	private boolean removeOverlaps = true;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "early", maxOccurrences = 1, description = ""
			+ "Whether to enable early exit strategy or not. This stragegy causes a single search "
			+ "to stop if one node pair has been seen before. It tends to speed up the search and "
			+ "avoids finding the same clones over and over again. However in some cases we might lose "
			+ "a relevant clone.")
	public void setEarlyExit(
			@AConQATAttribute(name = "exit", description = "Whether to exit early or not (default: true).") boolean earlyExit) {
		this.earlyExit = earlyExit;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "inclusion", maxOccurrences = 1, description = ""
			+ "Inclusion analysis can be used to find additional clones. Especially more occurrences "
			+ "of a clone can be found if this is active. However this is potentially costly.")
	public void setInclusionAnalysis(
			@AConQATAttribute(name = "analysis", description = "Whether to analyse or not (default: false).") boolean inclusionAnalysis) {
		this.inclusionAnalysis = inclusionAnalysis;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "overlaps", maxOccurrences = 1, description = ""
			+ "During clone clustering, clone classes containing overlapping clones can be build. "
			+ "If this flag is set to true, these overlaps are removed greedily by removing some clone classes.")
	public void setRemoveOverlaps(
			@AConQATAttribute(name = "remove", description = "Whether to remove them or not (default: true).") boolean removeOverlaps) {
		this.removeOverlaps = removeOverlaps;
	}

	/** {@inheritDoc} */
	@Override
	public void detect(AugmentedModelGraph modelGraph,
			ICloneReporter cloneReporter, IConQATLogger logger) {

		CloneClusterer clusterer = new CloneClusterer(modelGraph,
				cloneReporter, logger, removeOverlaps);

		new PairDetector(modelGraph, minSize, minWeight, earlyExit, clusterer,
				logger).execute();

		if (inclusionAnalysis) {
			clusterer.performInclusionAnalysis();
		}

		clusterer.performClustering();
	}
}