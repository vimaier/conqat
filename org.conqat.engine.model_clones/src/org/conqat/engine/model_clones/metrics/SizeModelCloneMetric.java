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
 * @levd.rating GREEN Hash: 6C92DE54495BAE8924898AF28572D492
 */
@AConQATProcessor(description = "Calculates the size of the clone group, "
		+ "which is defined as the number of blocks of the largest clone instance.")
public class SizeModelCloneMetric extends ModelCloneMetricProcessorBase {

	/**
	 * We use the key annotation here, so the key becomes visible in ConQATDoc
	 * and cq.edit.
	 */
	@AConQATKey(description = "The key used for storing the metric.", type = "java.lang.Double")
	public static final String KEY = "size";

	/** The maximal size. */
	private int maxSize = 0;

	/** {@inheritDoc} */
	@Override
	protected void resetCounters() {
		maxSize = 0;
	}

	/** {@inheritDoc} */
	@Override
	public void addCloneInstance(List<INode> nodes, List<IDirectedEdge> edges) {
		maxSize = Math.max(maxSize, nodes.size());
	}

	/** {@inheritDoc} */
	@Override
	public double calculateMetricValue() {
		return maxSize;
	}

	/** {@inheritDoc} */
	@Override
	public String getName() {
		return KEY;
	}
}