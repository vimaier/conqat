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
package org.conqat.engine.graph.nodes;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.ConQATPipelineProcessorBase;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.traversal.TraversalUtils;
import org.conqat.engine.commons.util.ValueCopier;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * Copies values from a source tree into a graph.
 * <p>
 * This class works similar to the {@link ValueCopier}, but copies values into
 * {@link ConQATGraph} nodes instead of arbitrary {@link IConQATNode}s.
 * 
 * @author Elmar Juergens
 * @author juergens
 * @author $Author: deissenb $
 * @version $Rev: 35147 $
 * @ConQAT.Rating GREEN Hash: 24923D1575B1F2B776D3F869BCA7B24B
 */
@AConQATProcessor(description = "Copies values from a source tree into a graph.")
public class GraphTargetingValueCopier extends
		ConQATPipelineProcessorBase<ConQATGraph> {

	/** Source tree from which values are read */
	private IConQATNode source;

	/** Key that holds target vertex id */
	private String targetNodeIdKey;

	/** Key that gets copied */
	private String copyKey;

	/** ConQAT Parameter */
	@AConQATParameter(name = "source", description = "ConQAT nodes from which values are read", minOccurrences = 1, maxOccurrences = 1)
	public void setInput(
			@AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC)
			IConQATNode source) {
		this.source = source;
	}

	/** ConQAT Parameter */
	@AConQATParameter(name = "target", description = "Key that holds the id of the node into which value gets copied", minOccurrences = 1, maxOccurrences = 1)
	public void setTargetNodeIdKey(
			@AConQATAttribute(name = "key", description = "Key that holds the id of the node into which value gets copied")
			String targetNodeIdKey) {
		this.targetNodeIdKey = targetNodeIdKey;
	}

	/** ConQAT Parameter */
	@AConQATParameter(name = "copy", description = "Key that gets copied from source node to target vertex", minOccurrences = 1, maxOccurrences = 1)
	public void setCopyKey(
			@AConQATAttribute(name = "key", description = "Key that gets copied from source node to target vertex")
			String copyKey) {
		this.copyKey = copyKey;
	}

	/** Copy values from source tree into graph */
	@Override
	protected void processInput(ConQATGraph graph) {
		NodeUtils.addToDisplayList(graph, copyKey);

		for (IConQATNode node : TraversalUtils.listAllDepthFirst(source)) {
			Object targetNodeId = node.getValue(targetNodeIdKey);
			if (targetNodeId != null) {
				ConQATVertex targetVertex = graph.getVertexByID(targetNodeId
						.toString());

				if (targetVertex == null) {
					getLogger()
							.warn("Vertex '" + targetNodeId + "' not found!");
				} else {
					targetVertex.setUserDatum(copyKey, node.getValue(copyKey),
							DeepCloneCopyAction.getInstance());
				}
			}
		}
	}
}