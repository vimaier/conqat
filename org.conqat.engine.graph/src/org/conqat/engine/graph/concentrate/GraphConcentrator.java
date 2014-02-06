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
package org.conqat.engine.graph.concentrate;

import java.util.ArrayList;

import org.conqat.lib.commons.assessment.Assessment;
import org.conqat.lib.commons.collections.TwoDimHashMap;
import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.ConQATPipelineProcessorBase;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.graph.nodes.ConQATGraph;
import org.conqat.engine.graph.nodes.ConQATGraphInnerNode;
import org.conqat.engine.graph.nodes.ConQATVertex;
import org.conqat.engine.graph.nodes.DeepCloneCopyAction;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;

/**
 * This processor concentrates a graph, i.e. it collapses leaves and infers
 * edges for the collapsed graph.
 * 
 * @author Benjamin Hummel
 * @author $Author: deissenb $
 * @version $Rev: 35147 $
 * @ConQAT.Rating GREEN Hash: D9A952F89E9B24594A88691F425D54C1
 */
@AConQATProcessor(description = "This processor concentrates a ConQATGraph by folding all vertices "
		+ "of an inner node into a single vertex and combining edges accordingly. Optionally an "
		+ "assessment which is present at the edges can be combined for the new edge.")
public class GraphConcentrator extends ConQATPipelineProcessorBase<ConQATGraph> {

	/** Extension used to prolong the id. */
	private static final String EXT = "_";

	/** The key to read the assessment from. */
	private String assessmentKey = null;

	/** Storage for the new edges (with assessment). */
	private final TwoDimHashMap<String, String, Assessment> newEdgeAssessments = new TwoDimHashMap<String, String, Assessment>();

	/** Flag for loop filtering. */
	private boolean loopsFilter = true;

	/** Set the assessment key. */
	@AConQATParameter(name = "assessment", maxOccurrences = 1, description = ""
			+ "The key to read the assessment from. If not key is given, no assessment is created for the edges.")
	public void setAssessmentKey(
			@AConQATAttribute(name = ConQATParamDoc.READKEY_KEY_NAME, description = ConQATParamDoc.READKEY_KEY_DESC) String key) {
		assessmentKey = key;
	}

	/** Set the assessment key. */
	@AConQATParameter(name = "loops", maxOccurrences = 1, description = ""
			+ "Filter self-loops [true]")
	public void setLoopsFilter(
			@AConQATAttribute(name = "filter", description = "Enable/disable loop filter") boolean loopsFilter) {
		this.loopsFilter = loopsFilter;
	}

	/** {@inheritDoc} */
	@Override
	protected void processInput(ConQATGraph graph) throws ConQATException {
		calculateNewEdges(graph);
		discardEmptyInnerNodes(graph);
		createRepresentativeNodes(graph, graph);
		insertNewEdges(graph);

		getLogger().info(
				"Concentrated graph has " + graph.getVertices().size()
						+ " vertices and " + graph.getEdges().size()
						+ " edges.");
	}

	/** Calculates the new edges for the graph. */
	private void calculateNewEdges(ConQATGraph graph) {
		for (DirectedSparseEdge edge : graph.getEdges()) {
			ConQATVertex source = (ConQATVertex) edge.getSource();
			ConQATVertex dest = (ConQATVertex) edge.getDest();
			insertEdge(source.getParent().getId(), dest.getParent().getId(),
					edge);
		}
	}

	/**
	 * Inserts an edge between the given nodes (if it does not already exist)
	 * and handles merging the assessment (if required).
	 */
	private void insertEdge(String sourceID, String targetID,
			DirectedSparseEdge originalEdge) {

		if (loopsFilter && sourceID.equals(targetID)) {
			return;
		}

		// deal with assessment
		if (assessmentKey == null) {
			newEdgeAssessments.putValue(sourceID, targetID, null);
		} else {
			Assessment origAssessment = (Assessment) originalEdge
					.getUserDatum(assessmentKey);
			if (origAssessment == null) {
				origAssessment = new Assessment();
			}

			Assessment newAssessment = newEdgeAssessments.getValue(sourceID,
					targetID);
			if (newAssessment == null) {
				newEdgeAssessments.putValue(sourceID, targetID, origAssessment);
			} else {
				newAssessment.add(origAssessment);
			}
		}
	}

	/** Removes all empty inner nodes. */
	private void discardEmptyInnerNodes(ConQATGraphInnerNode node) {
		if (!node.hasChildren()) {
			node.remove();
		} else {
			for (ConQATGraphInnerNode child : new ArrayList<ConQATGraphInnerNode>(
					node.getInnerNodes())) {
				discardEmptyInnerNodes(child);
			}
		}
	}

	/** Creates the representative nodes for all graphs having vertices. */
	private void createRepresentativeNodes(ConQATGraphInnerNode node,
			ConQATGraph graph) throws ConQATException {
		// erase child vertices
		boolean createRep = !node.getChildVertices().isEmpty();
		for (ConQATVertex vertex : new ArrayList<ConQATVertex>(node
				.getChildVertices())) {
			vertex.remove();
		}

		// handle children
		for (ConQATGraphInnerNode child : new ArrayList<ConQATGraphInnerNode>(
				node.getInnerNodes())) {
			createRepresentativeNodes(child, graph);
		}

		// create representative if needed
		if (createRep) {
			ConQATGraphInnerNode parent = node.getParent();
			ConQATVertex vertex = null;
			if (node.hasChildren() || parent == null) {
				vertex = graph.createVertex(extendedName(node.getId()),
						extendedName(node.getName()), node);
			} else {
				node.remove();
				vertex = graph.createVertex(extendedName(node.getId()), node
						.getName(), parent);
			}

			NodeUtils.copyValues(NodeUtils.getDisplayList(graph), node, vertex);
		}
	}

	/** Inserts all new edges into the graph. */
	private void insertNewEdges(ConQATGraph graph) {
		for (String source : newEdgeAssessments.getFirstKeys()) {
			for (String target : newEdgeAssessments.getSecondKeys(source)) {
				DirectedSparseEdge edge = graph.addEdge(graph
						.getVertexByID(extendedName(source)), graph
						.getVertexByID(extendedName(target)));
				if (assessmentKey != null) {
					edge.addUserDatum(assessmentKey, newEdgeAssessments
							.getValue(source, target), DeepCloneCopyAction
							.getInstance());
				}
			}
		}
	}

	/** Create extended name for a vertex name */
	private static String extendedName(String name) {
		return name + EXT;
	}

}