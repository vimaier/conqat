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
package org.conqat.engine.model_clones.metrics;

import java.util.List;

import org.conqat.engine.model_clones.detection.util.AugmentedModelGraph;
import org.conqat.engine.model_clones.detection.util.ICloneReporter;
import org.conqat.engine.model_clones.model.IDirectedEdge;
import org.conqat.engine.model_clones.model.INode;

/**
 * Interface for model clone metrics. This is to some extent similar to the
 * {@link ICloneReporter} interface to allow the calculation of these metrics in
 * parallel to the reporting process. The main difference is that less
 * information is provided in the "start" method to ease adaption to detection
 * algorithms which do not have full knowledge of the clone class at the start
 * of the reporting phase.
 * <p>
 * The typical lifecycle looks like this:
 * <ul>
 * <li>{@link #startCloneGroup(AugmentedModelGraph)}</li>
 * <li>one or more calls to {@link #addCloneInstance(List, List)}</li>
 * <li>{@link #calculateMetricValue()}</li>
 * <li>repeat these steps for all clone groups</li>
 * </ul>
 * 
 * @author hummelb
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 * @levd.rating GREEN Hash: E3997CA76F1D71ADB29E1275719672F8
 */
public interface IModelCloneMetric {

	/**
	 * Returns the name of the metric, which is used besides others to determine
	 * the key used for storing it.
	 */
	String getName();

	/**
	 * Indicates the start of a new clone group. This should reset all counters.
	 * The clone group is part of the given graph.
	 */
	void startCloneGroup(AugmentedModelGraph graph);

	/**
	 * Updates the metric with the given clone instance consisting of the given
	 * nodes and edges.
	 */
	void addCloneInstance(List<INode> nodes, List<IDirectedEdge> edges);

	/** Calculates the value of the metric for the clone group given so far. */
	double calculateMetricValue();
}