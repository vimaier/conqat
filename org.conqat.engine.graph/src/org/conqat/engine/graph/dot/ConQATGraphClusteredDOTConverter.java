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
package org.conqat.engine.graph.dot;

import static org.conqat.lib.commons.string.StringUtils.CR;
import org.conqat.engine.graph.nodes.ConQATGraph;
import org.conqat.engine.graph.nodes.ConQATGraphInnerNode;
import org.conqat.engine.graph.nodes.ConQATVertex;

/**
 * A class for converting a {@link ConQATGraph} to input suitable for the DOT
 * program using a clustered representation.
 * 
 * @author Florian Deissenboeck
 * @author Tilman Seifert
 * @author Benjamin Hummel
 * @author $Author: deissenb $
 * 
 * @version $Rev: 35147 $
 * @ConQAT.Rating GREEN Hash: D6DB636B25EF57544F396BCE489CC182
 */
public class ConQATGraphClusteredDOTConverter extends ConQATGraphDOTConverter {

	/** Counter for generating unique cluster names. */
	private int clusterID = 0;

	/** Use DFS for generating clusters. */
	@Override
	protected String createVertexDescription(ConQATGraph graph) {
		return createInnerNodeDescription(graph);
	}

	/** Creates the clustered description for a node and its children. */
	protected String createInnerNodeDescription(ConQATGraphInnerNode node) {
		StringBuilder result = new StringBuilder();

		for (ConQATGraphInnerNode inner : node.getInnerNodes()) {
			result.append("subgraph cluster_" + clusterID++ + " {" + CR
					+ "  label = \"" + makeLabel(inner) + "\";" + CR
					+ "  fontname = \"Helvetica\";" + CR);
			result.append(createInnerNodeDescription(inner));
			result.append("}" + CR);
		}

		for (ConQATVertex vertex : node.getChildVertices()) {
			result.append(createVertex(vertex));
		}

		return result.toString();
	}
}