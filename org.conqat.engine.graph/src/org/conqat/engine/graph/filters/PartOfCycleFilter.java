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
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.graph.algo.StrongConnectivity;
import org.conqat.engine.graph.nodes.ConQATGraph;
import org.conqat.engine.graph.nodes.ConQATVertex;

/**
 * This processor filters any nodes which are part of at least one cycle.
 * 
 * @author Benjamin Hummel
 * @author Tilman Seifert
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: BFC2CA8E8D155528DDA09D9CD9757185
 */
@AConQATProcessor(description = "This processor removes any vertices which are part of "
		+ "at least one (directed) cycle.")
public class PartOfCycleFilter extends ConQATPipelineProcessorBase<ConQATGraph> {

	/** {@inheritDoc} */
	@Override
	protected void processInput(ConQATGraph graph) {
		StrongConnectivity scc = new StrongConnectivity(graph);
		int[] componentSizes = scc.getComponentSizes();

		// copy the list as we remove vertices
		for (ConQATVertex vertex : new ArrayList<ConQATVertex>(graph
				.getVertices())) {
			if (componentSizes[scc.getComponent(vertex)] > 1) {
				vertex.remove();
			}
		}
	}
}