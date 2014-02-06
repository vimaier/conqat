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

import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.commons.ConQATPipelineProcessorBase;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.graph.algo.StrongConnectivity;
import org.conqat.engine.graph.nodes.ConQATGraph;
import org.conqat.engine.graph.nodes.ConQATGraphInnerNode;
import org.conqat.engine.graph.nodes.ConQATGraphUtils;
import org.conqat.engine.graph.nodes.ConQATVertex;

/**
 * Divide the graph into strongly connected components.
 * 
 * @author Florian Deissenboeck
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 8356F029E1AD1D75BC9738E9651F8E3A
 */
@AConQATProcessor(description = "Divides the graph into strongly connected components. "
		+ "Strongly connected components are the maximal sets of vertices such that in "
		+ "each such set there is a (directed) path between every two vertices.")
public class StronglyConnectedComponentClusterer extends
		ConQATPipelineProcessorBase<ConQATGraph> {

	/** {@inheritDoc} */
	@Override
	protected void processInput(ConQATGraph graph) throws ConQATException {

		ConQATGraphUtils.collapseHierarchy(graph);
		StrongConnectivity conn = new StrongConnectivity(graph);

		List<ConQATGraphInnerNode> clusterNodes = new ArrayList<ConQATGraphInnerNode>();
		for (int i = 0; i < conn.getNumComponents(); ++i) {
			clusterNodes.add(graph.createChildNode("Cluster " + i, "Cluster "
					+ i));
		}

		for (ConQATVertex vertex : graph.getVertices()) {
			int componentIndex = conn.getComponent(vertex);
			vertex.relocate(clusterNodes.get(componentIndex));
		}
	}
}