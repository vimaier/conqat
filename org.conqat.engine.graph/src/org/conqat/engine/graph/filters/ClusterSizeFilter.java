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
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.graph.nodes.ConQATGraph;
import org.conqat.engine.graph.nodes.ConQATGraphInnerNode;

/**
 * This filter removes clusters with a smaller size than a given threshold.
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 69E13A8846B1E753D4EA0A0DD61AD82D
 */
@AConQATProcessor(description = "This filter removes all inner nodes with less vertices "
		+ "than the given threshold.")
public class ClusterSizeFilter extends ConQATPipelineProcessorBase<ConQATGraph> {

	/** Minimum size of a cluster to stay in the graph. */
	private int minSize;

	/** set graph */
	@AConQATParameter(name = "size", minOccurrences = 1, maxOccurrences = 1, description = "Minimum size of a cluster to stay in the graph.")
	public void setMinSize(
			@AConQATAttribute(name = "min", description = "Minimum size.")
			int minSize) {
		this.minSize = minSize;
	}

	/** {@inheritDoc} */
	@Override
	protected void processInput(ConQATGraph graph) {
		// copy the list as we remove
		for (ConQATGraphInnerNode child : new ArrayList<ConQATGraphInnerNode>(
				graph.getInnerNodes())) {
			checkNode(child);
		}
	}

	/** Checks the node and removes it if required. */
	private void checkNode(ConQATGraphInnerNode node) {
		for (ConQATGraphInnerNode child : new ArrayList<ConQATGraphInnerNode>(
				node.getInnerNodes())) {
			checkNode(child);
		}
		if (node.getInnerNodes().isEmpty()
				&& node.getChildVertices().size() < minSize) {
			node.remove();
		}
	}
}