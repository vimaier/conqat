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
package org.conqat.engine.simulink.clones.preprocess;

import java.util.List;
import java.util.Set;

import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.model_clones.detection.util.ICloneReporter;
import org.conqat.engine.model_clones.detection.util.SubgraphEnumerator;
import org.conqat.engine.model_clones.label.CanonicalLabelCreator;
import org.conqat.engine.model_clones.model.IDirectedEdge;
import org.conqat.engine.model_clones.model.INode;
import org.conqat.engine.simulink.clones.model.SimulinkModelGraph;
import org.conqat.engine.simulink.clones.model.SimulinkModelGraphCreator;
import org.conqat.engine.simulink.clones.model.SimulinkNode;
import org.conqat.engine.simulink.clones.normalize.ISimulinkNormalizer;
import org.conqat.engine.simulink.scope.ISimulinkResource;
import org.conqat.lib.commons.collections.IdentityHashSet;
import org.conqat.lib.commons.collections.ListMap;
import org.conqat.lib.commons.collections.PairList;
import org.conqat.lib.simulink.model.SimulinkBlock;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: deissenb $
 * @version $Rev: 36793 $
 * @ConQAT.Rating GREEN Hash: B46D521BA816450326404F25E93DD3AA
 */
@AConQATProcessor(description = ""
		+ "This preprocessor marks all nodes that are not part of at least one duplicated subgraph to be ignored during clone detection. "
		+ "The processor does not detect any clones, but can speed up the detection by reducing the size of the graph being searched.")
public class NonDuplicateSubgraphPreprocessor extends SimulinkPreprocessorBase {

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "subgraph", attribute = "size", optional = true, description = ""
			+ "The size of the subgraphs that are generated and checked. "
			+ "This should not be chosen too large, as then the performance is reduced instead of improved. "
			+ "The default value is 3.")
	public int subgraphSize = 3;

	/** {@inheritDoc} */
	@Override
	public void preprocess(ISimulinkResource model,
			ISimulinkNormalizer normalizer, Set<SimulinkBlock> ignoredBlocks,
			ICloneReporter reporter) throws ConQATException {

		// we measure time, as this is a strategy that is run in the context of
		// another processor.
		long start = System.currentTimeMillis();

		SimulinkModelGraph graph = SimulinkModelGraphCreator.createModelGraph(
				model, normalizer, ignoredBlocks);

		Set<INode> keep = determineKeptNodes(graph);
		int removed = 0;
		for (INode node : graph.getNodes()) {
			if (!keep.contains(node)) {
				++removed;
				ignoredBlocks.add(((SimulinkNode) node).getBlock());
			}
		}

		getLogger().info(
				"Could remove " + removed + " of " + graph.getNodes().size()
						+ " blocks.");
		getLogger().info(
				"Preprocessing took " + (System.currentTimeMillis() - start)
						/ 1000. + " seconds");
	}

	/**
	 * Calculates the set of nodes that should be kept as they occur in at least
	 * one duplicated subgraph.
	 */
	private Set<INode> determineKeptNodes(SimulinkModelGraph graph) {
		ListMap<String, List<INode>> nodesByLabel = getSubgraphsByLabel(graph);
		Set<INode> keep = new IdentityHashSet<INode>();
		for (String label : nodesByLabel.getKeys()) {
			List<List<INode>> list = nodesByLabel.getCollection(label);
			if (list != null && list.size() >= 2) {
				for (List<INode> nodes : list) {
					keep.addAll(nodes);
				}
			}
		}
		return keep;
	}

	/** Returns the subgraphs clustered by their canonical label. */
	private ListMap<String, List<INode>> getSubgraphsByLabel(
			SimulinkModelGraph graph) {
		ListMap<String, List<INode>> nodesByLabel = new ListMap<String, List<INode>>();

		PairList<List<INode>, List<IDirectedEdge>> subgraphs = SubgraphEnumerator
				.getConnectedSubGraphs(graph.getNodes(), graph.getEdges(),
						subgraphSize);
		getLogger().info("Extracted " + subgraphs.size() + " subgraphs");

		for (int i = 0; i < subgraphs.size(); ++i) {
			String label = CanonicalLabelCreator.getCanonicalLabel(
					subgraphs.getFirst(i), subgraphs.getSecond(i))
					.getTextualHash();
			nodesByLabel.add(label, subgraphs.getFirst(i));
		}
		return nodesByLabel;
	}
}