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

import java.util.ArrayList;

import org.conqat.engine.commons.ConQATPipelineProcessorBase;
import org.conqat.engine.graph.nodes.ConQATGraph;
import org.conqat.engine.graph.nodes.ConQATVertex;

/**
 * Base class for filters that remove vertices.
 * 
 * @author juergens
 * @author $Author: deissenb $
 * @version $Rev: 35147 $
 * @ConQAT.Rating GREEN Hash: 04336E92683E932342DBC150485B6F4E
 */
public abstract class VertexFilterBase extends
		ConQATPipelineProcessorBase<ConQATGraph> {

	/**
	 * Applies the filter criteria provided by the template method
	 * {@link #isFiltered(ConQATVertex)} to each edge in the graph.
	 */
	@Override
	protected void processInput(ConQATGraph graph) {
		// copy the list as we remove vertices
		for (ConQATVertex vertex : new ArrayList<ConQATVertex>(graph
				.getVertices())) {
			if (isFiltered(vertex)) {
				vertex.remove();
			}
		}
	}

	/**
	 * Template method that allows subclasses to provide filter criteria.
	 * <p>
	 * If this returns true, vertex will be removed from the graph.
	 */
	protected abstract boolean isFiltered(ConQATVertex vertex);

}