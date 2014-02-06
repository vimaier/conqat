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

import java.util.Collection;

import org.conqat.lib.commons.clone.IDeepCloneable;
import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.model_clones.detection.util.AugmentedModelGraph;
import org.conqat.engine.model_clones.model.INode;

/**
 * Base class for processors which calculate metrics. All of these processors
 * are constructed to implement {@link IModelCloneMetric} themselves and just
 * return themselves.
 * 
 * @author hummelb
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 * @levd.rating GREEN Hash: 6308E3CD17B89A7AB3BFD056D66973D4
 */
public abstract class ModelCloneMetricProcessorBase extends ConQATProcessorBase
		implements IModelCloneMetric, IDeepCloneable {

	/** The model graph we are currently working on. */
	protected AugmentedModelGraph graph;

	/** {@inheritDoc} */
	@Override
	public void startCloneGroup(AugmentedModelGraph graph) {
		this.graph = graph;
		resetCounters();
	}

	/**
	 * Template methods for resetting any counters used in metric calculation.
	 * This is called from {@link #startCloneGroup(AugmentedModelGraph)}.
	 */
	protected abstract void resetCounters();

	/** Just returns <code>this</code> */
	@Override
	public IModelCloneMetric process() {
		return this;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Returns <code>this</code> as metric calculators are immutable.
	 */
	@Override
	public IDeepCloneable deepClone() {
		return this;
	}

	/** Returns the summed weight of several nodes. */
	protected static int getNodesWeight(Collection<INode> nodes) {
		int weight = 0;
		for (INode node : nodes) {
			weight += node.getWeight();
		}
		return weight;
	}
}