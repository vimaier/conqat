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
import org.conqat.engine.graph.nodes.ConQATGraph;
import org.conqat.engine.graph.nodes.ConQATGraphInnerNode;

/**
 * This filter removes inner nodes which are not needed anymore as they carry no
 * vertices.
 * 
 * @author Benjamin Hummel
 * @author $Author: deissenb $
 * @version $Rev: 35147 $
 * @ConQAT.Rating GREEN Hash: 8C4862E5FB0E375AA19C9A7F09607292
 */
@AConQATProcessor(description = "This filter removes inner nodes having no children.")
public class EmptyInnerNodeFilter extends
		ConQATPipelineProcessorBase<ConQATGraph> {

	/** {@inheritDoc} */
	@Override
	protected void processInput(ConQATGraph graph) {
		for (ConQATGraphInnerNode child : new ArrayList<ConQATGraphInnerNode>(
				graph.getInnerNodes())) {
			checkNode(child);
		}
	}

	/** Checks the node and removes it if required. */
	private void checkNode(ConQATGraphInnerNode node) {
		// copy the list as we remove
		for (ConQATGraphInnerNode child : new ArrayList<ConQATGraphInnerNode>(
				node.getInnerNodes())) {
			checkNode(child);
		}
		if (!node.hasChildren()) {
			node.remove();
		}
	}
}