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
package org.conqat.engine.graph.cluster;

import java.util.Set;

import org.conqat.engine.commons.ConQATPipelineProcessorBase;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.graph.nodes.ConQATGraph;
import org.conqat.engine.graph.nodes.ConQATGraphInnerNode;
import org.conqat.engine.graph.nodes.ConQATGraphUtils;
import org.conqat.engine.graph.nodes.ConQATVertex;
import edu.uci.ics.jung.algorithms.cluster.ClusterSet;
import edu.uci.ics.jung.algorithms.cluster.GraphClusterer;

/**
 * Base class for clustering processors based on JUNG clusterers.
 * 
 * @author Tilman Seifert
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 7E88C752FD20AE61D48BC3EA86841D2B
 */
public abstract class ClustererBase extends
		ConQATPipelineProcessorBase<ConQATGraph> {

	/** {@inheritDoc} */
	@Override
	protected void processInput(ConQATGraph graph) throws ConQATException {
		ClusterSet clusters = obtainGraphClusterer().extract(graph.getGraph());
		ConQATGraphUtils.collapseHierarchy(graph);

		for (int i = 0; i < clusters.size(); i++) {
			Set<?> cluster = clusters.getCluster(i);
			ConQATGraphInnerNode clusterNode = graph.createChildNode("Cluster "
					+ i, "Cluster " + i);
			for (Object o : cluster) {
				ConQATVertex v = (ConQATVertex) o;
				if (v.getParent() != graph) {
					getLogger().warn(
							"Node " + v.getId()
									+ " is in more than one cluster. "
									+ "Using only first cluster!");
				} else {
					v.relocate(clusterNode);
				}
			}
		}
	}

	/** Template method for clusterer to be used. */
	protected abstract GraphClusterer obtainGraphClusterer();
}