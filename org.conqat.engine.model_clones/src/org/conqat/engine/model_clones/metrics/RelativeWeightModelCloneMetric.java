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

import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.model_clones.model.IDirectedEdge;
import org.conqat.engine.model_clones.model.INode;

/**
 * {@ConQAT.Doc}
 * 
 * @author hummelb
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 * @levd.rating GREEN Hash: 0899F58497EB7C405934D46421AFFC87
 */
@AConQATProcessor(description = "Calculates the relative weight of the clone group, "
		+ "which is defined as the average of weight/size  over all clone instances.")
public class RelativeWeightModelCloneMetric extends
		ModelCloneMetricProcessorBase {

	/**
	 * We use the key annotation here, so the key becomes visible in ConQATDoc
	 * and cq.edit.
	 */
	@AConQATKey(description = "The key used for storing the metric.", type = "java.lang.Double")
	public static final String KEY = "rel_weight";

	/** The sum of weight/size. */
	private double metricSum = 0;

	/** The number of occurrences. */
	private int occurrences = 0;

	/** {@inheritDoc} */
	@Override
	protected void resetCounters() {
		metricSum = 0;
		occurrences = 0;
	}

	/** {@inheritDoc} */
	@Override
	public void addCloneInstance(List<INode> nodes, List<IDirectedEdge> edges) {
		metricSum += getNodesWeight(nodes) / (double) nodes.size();
		occurrences += 1;
	}

	/** {@inheritDoc} */
	@Override
	public double calculateMetricValue() {
		return metricSum / occurrences;
	}

	/** {@inheritDoc} */
	@Override
	public String getName() {
		return KEY;
	}
}