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

import java.util.HashSet;
import java.util.Set;

import org.conqat.engine.commons.ConQATPipelineProcessorBase;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.graph.nodes.ConQATGraph;
import org.conqat.engine.graph.nodes.ConQATVertex;
import edu.uci.ics.jung.algorithms.connectivity.BFSDistanceLabeler;

/**
 * This processor labels every vertex with the distance to a 'root set'.
 * 
 * @author Benjamin Hummel
 * @author Tilman Seifert
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 620130902D6049BD0A94CE23D6876B99
 */
@AConQATProcessor(description = "This processor labels every vertex of the "
		+ "graph with the distance to a 'root set'. Unreachable nodes are assigned a "
		+ "distance of -1.")
public class DistanceLabeler extends ConQATPipelineProcessorBase<ConQATGraph> {

	/** which elements to start with? */
	private final Set<String> rootNodeIDs = new HashSet<String>();

	/** Key used for writing. */
	@AConQATKey(description = "Distance to a given vertex or set of vertices", type = "java.lang.Integer")
	public static final String DIST_KEY = "distance";

	/** Add an element name to the set that is used for distance calculation. */
	@AConQATParameter(name = "root", minOccurrences = 1, description = "Adds an element to the root set")
	public void addElementName(
			@AConQATAttribute(name = "id", description = "The id of the node to add to the root set.")
			String element) {
		rootNodeIDs.add(element);
	}

	/** {@inheritDoc} */
	@Override
	protected void processInput(ConQATGraph graph) {
		NodeUtils.addToDisplayList(graph, DIST_KEY);
		Set<ConQATVertex> rootElements = new HashSet<ConQATVertex>();
		for (String id : rootNodeIDs) {
			ConQATVertex v = graph.getVertexByID(id);
			if (v != null) {
				rootElements.add(v);
			} else {
				getLogger().warn("No vertex with ID " + id + " found.");
			}
		}

		BFSDistanceLabeler labeler = new BFSDistanceLabeler();
		labeler.labelDistances(graph.getGraph(), rootElements);
		for (ConQATVertex v : graph.getVertices()) {
			int distance = labeler.getDistance(graph.getGraph(), v);
			v.setValue(DIST_KEY, distance);
		}
	}
}