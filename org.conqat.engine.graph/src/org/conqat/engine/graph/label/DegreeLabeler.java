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
package org.conqat.engine.graph.label;

import org.conqat.engine.commons.ConQATPipelineProcessorBase;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.graph.nodes.ConQATGraph;
import org.conqat.engine.graph.nodes.ConQATVertex;

/**
 * This processor labels every vertex with the of adjacent vertices
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 06BF6302CC89F9332761BA478540B6ED
 */
@AConQATProcessor(description = "This processor labels every vertex of the "
		+ "graph with the number of adjacent vertices. ")
public class DegreeLabeler extends ConQATPipelineProcessorBase<ConQATGraph> {

	/** Key used for writing. */
	@AConQATKey(description = "The degree of the node.", type = "java.lang.Integer")
	public static final String DEGREE_KEY = "degree";

	/** {@inheritDoc} */
	@Override
	protected void processInput(ConQATGraph graph) {
		NodeUtils.addToDisplayList(graph, DEGREE_KEY);
		for (ConQATVertex v : graph.getVertices()) {
			v.setValue(DEGREE_KEY, v.numNeighbors());
		}
	}
}