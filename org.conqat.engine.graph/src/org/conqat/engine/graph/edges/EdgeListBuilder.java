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
package org.conqat.engine.graph.edges;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.ListNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.graph.nodes.ConQATGraph;
import org.conqat.engine.graph.nodes.ConQATVertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;

/**
 * {@ConQAT.Doc}
 * 
 * @author juergens
 * @author $Author: deissenb $
 * @version $Rev: 35147 $
 * @ConQAT.Rating GREEN Hash: 0A231DA496CE095789B555A89E1F6186
 */
@AConQATProcessor(description = "Creates a list of nodes for the edges of a graph. List is "
		+ "rooted under a dummy root node so that list can be processed by standard "
		+ "ConQAT processors.")
public class EdgeListBuilder extends ConQATProcessorBase {

	/** Graph being worked on */
	private ConQATGraph graph;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "graph", minOccurrences = 1, maxOccurrences = 1, description = "Graph whose edges to be used.")
	public void setGraph(
			@AConQATAttribute(name = "ref", description = "Reference to graph generating processor.") ConQATGraph graph) {

		this.graph = graph;
	}

	/** {@inheritDoc} */
	@Override
	public IConQATNode process() {
		ListNode root = new ListNode();
		createEdgeNodes(root);
		return root;
	}

	/** Appends a node for each edge in the graph to root */
	private void createEdgeNodes(ListNode root) {
		Set<String> allKeys = new HashSet<String>();

		for (DirectedSparseEdge edge : graph.getEdges()) {
			// create node for edge
			ListNode edgeNode = new ListNode(createEdgeLabel(edge));
			root.addChild(edgeNode);

			// copy values from edge to node
			Iterator<?> keys = edge.getUserDatumKeyIterator();
			while (keys.hasNext()) {
				String key = keys.next().toString();
				allKeys.add(key);
				Object value = edge.getUserDatum(key);
				edgeNode.setValue(key, value);
			}
		}

		NodeUtils.addToDisplayList(root, allKeys);
	}

	/**
	 * Creates a label of the form <code>source -> destination</code> for an
	 * edge.
	 */
	private String createEdgeLabel(DirectedSparseEdge edge) {
		ConQATVertex source = (ConQATVertex) edge.getSource();
		ConQATVertex destination = (ConQATVertex) edge.getDest();
		return source.getId() + " -> " + destination.getId();
	}

}