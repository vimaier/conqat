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
 * @levd.rating GREEN Hash: 2CD542A5C6CE5C711C77254A06229FD9
 */
@AConQATProcessor(description = "Calculates the weight of the clone group, "
		+ "which is defined as the sum of weights of blocks of the heaviest clone instance.")
public class WeightModelCloneMetric extends ModelCloneMetricProcessorBase {

	/**
	 * We use the key annotation here, so the key becomes visible in ConQATDoc
	 * and cq.edit.
	 */
	@AConQATKey(description = "The key used for storing the metric.", type = "java.lang.Double")
	public static final String KEY = "weight";

	/** The maximal weight. */
	private int maxWeight = 0;

	/** {@inheritDoc} */
	@Override
	protected void resetCounters() {
		maxWeight = 0;
	}

	/** {@inheritDoc} */
	@Override
	public void addCloneInstance(List<INode> nodes, List<IDirectedEdge> edges) {
		maxWeight = Math.max(maxWeight, getNodesWeight(nodes));
	}

	/** {@inheritDoc} */
	@Override
	public double calculateMetricValue() {
		return maxWeight;
	}

	/** {@inheritDoc} */
	@Override
	public String getName() {
		return KEY;
	}
}