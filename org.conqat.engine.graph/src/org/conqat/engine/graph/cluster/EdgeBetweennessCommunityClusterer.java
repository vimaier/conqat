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

import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import edu.uci.ics.jung.algorithms.cluster.EdgeBetweennessClusterer;
import edu.uci.ics.jung.algorithms.cluster.GraphClusterer;

/**
 * Clusterer based on edge betweenness.
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 4E5BF174FD533E2957BD927601A3B5F5
 */
@AConQATProcessor(description = "An algorithm for computing clusters (community "
		+ "structure) in graphs based on edge betweenness. [Note: The betweenness "
		+ "of an edge measures the extent to which that edge lies along shortest "
		+ "paths between all pairs of nodes.] Edges which are least central to "
		+ "communities are progressively removed until the communities have been "
		+ "adequately seperated. This algorithm works by iteratively following the "
		+ "2 step process: Compute edge betweenness for all edges in current graph. "
		+ " Remove edges with highest betweenness.")
public class EdgeBetweennessCommunityClusterer extends ClustererBase {

	/** The number of edges to be progressively removed from the graph. */
	private int numberOfEdges;

	/** Set the number of edges to be removed. */
	@AConQATParameter(name = "edges", minOccurrences = 1, maxOccurrences = 1, description = "The number of edges to be progressively removed from the graph.")
	public void setOutputKey(
			@AConQATAttribute(name = "number", description = "Number of edges")
			int numberOfEdges) {
		this.numberOfEdges = numberOfEdges;
	}

	/** {@inheritDoc} */
	@Override
	protected GraphClusterer obtainGraphClusterer() {
		return new EdgeBetweennessClusterer(numberOfEdges);
	}
}