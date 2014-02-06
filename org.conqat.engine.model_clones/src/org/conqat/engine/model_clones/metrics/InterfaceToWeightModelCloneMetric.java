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
 * @author $Author: steidl $
 * @version $Rev: 43636 $
 * @ConQAT.Rating GREEN Hash: 69F1F375CBDC110E2991F666ED06570A
 */
@AConQATProcessor(description = "Calculates the maximal interface size to weight ratio over all instances "
		+ "of the clone group. The interface size is defined as the number of edges from the clones to its "
		+ "surroundings. If the weight of a clone is 0 it is assumed to be at least one. ")
public class InterfaceToWeightModelCloneMetric extends
		ModelCloneMetricProcessorBase {

	/**
	 * We use the key annotation here, so the key becomes visible in ConQATDoc
	 * and cq.edit.
	 */
	@AConQATKey(description = "The key used for storing the metric.", type = "java.lang.Double")
	public static final String KEY = "ifs/weight";

	/** The maximal ratio encountered so far. */
	private double maxRatio = 0;

	/** {@inheritDoc} */
	@Override
	protected void resetCounters() {
		maxRatio = 0;
	}

	/** {@inheritDoc} */
	@Override
	public void addCloneInstance(List<INode> nodes, List<IDirectedEdge> edges) {
		double ratio = (double)graph.getInterfaceSize(nodes)
				/ (double)Math.max(1, getNodesWeight(nodes));
		maxRatio = Math.max(maxRatio, ratio);
	}

	/** {@inheritDoc} */
	@Override
	public double calculateMetricValue() {
		return maxRatio;
	}

	/** {@inheritDoc} */
	@Override
	public String getName() {
		return KEY;
	}
}