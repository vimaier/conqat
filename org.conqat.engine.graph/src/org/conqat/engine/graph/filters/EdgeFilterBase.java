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
package org.conqat.engine.graph.filters;

import java.util.HashSet;
import java.util.Set;

import org.conqat.engine.commons.ConQATPipelineProcessorBase;
import org.conqat.engine.graph.nodes.ConQATGraph;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;

/**
 * Base class for filters that remove edges from {@link ConQATGraph}s.
 * 
 * @author juergens
 * @author $Author: deissenb $
 * @version $Rev: 35147 $
 * @ConQAT.Rating GREEN Hash: 6F60D62248940009DFB5044146E1320E
 */
public abstract class EdgeFilterBase extends
		ConQATPipelineProcessorBase<ConQATGraph> {

	/**
	 * Applies the filter criteria provided by the template method
	 * {@link #isFiltered(DirectedSparseEdge)} to each edge in the graph.
	 */
	@Override
	protected void processInput(ConQATGraph graph) {
		// copy edge set as we remove edges
		Set<DirectedSparseEdge> edges = new HashSet<DirectedSparseEdge>(graph
				.getEdges());
		for (DirectedSparseEdge edge : edges) {
			if (isFiltered(edge)) {
				graph.getGraph().removeEdge(edge);
			}
		}
	}

	/**
	 * Template method that allows subclasses to provide filter criteria.
	 * <p>
	 * If this returns true, edge will be removed from the graph.
	 */
	protected abstract boolean isFiltered(DirectedSparseEdge edge);

}