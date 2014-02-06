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

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.model_clones.detection.util.ICloneReporter;
import org.conqat.engine.model_clones.label.CanonicalLabelCreator;
import org.conqat.engine.model_clones.label.GraphLabel;
import org.conqat.engine.model_clones.model.IDirectedEdge;
import org.conqat.engine.model_clones.model.INode;
import org.conqat.engine.resource.util.ResourceTraversalUtils;
import org.conqat.engine.simulink.clones.model.SimulinkDirectedEdge;
import org.conqat.engine.simulink.clones.model.SimulinkNode;
import org.conqat.engine.simulink.clones.normalize.ISimulinkNormalizer;
import org.conqat.engine.simulink.scope.ISimulinkElement;
import org.conqat.engine.simulink.scope.ISimulinkResource;
import org.conqat.lib.commons.collections.ListMap;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.simulink.model.SimulinkBlock;
import org.conqat.lib.simulink.model.SimulinkLine;
import org.conqat.lib.simulink.util.SimulinkUtils;

/**
 * {@ConQAT.Doc}
 * <p>
 * The overall algorithm works by comparing the contents of all subsystems with
 * each other. For this, a hash value for each subsystem based on its structural
 * contents is created in a recursive traversal. The hash is based on the
 * {@link CanonicalLabelCreator} and should be equal only for isomorphic graphs.
 * For the subsystem hashes, clone detection is easy, as all multiple hash
 * values correspond to a clone. Care has to be taken, however, to not also
 * report the subsystems contained in clones.
 * 
 * @author $Author: deissenb $
 * @version $Rev: 36793 $
 * @ConQAT.Rating GREEN Hash: 256340EF6327B39D1E4570C90EF7A780
 */
@AConQATProcessor(description = ""
		+ "A preprocessor which attempts to find duplicate subsystems and reports them as clones.")
public class DuplicateSubsystemPreprocessor extends SimulinkPreprocessorBase {

	/** Whether cloned subsystems should be removed. */
	private boolean removeClonedSubsystems = true;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "subsystems", maxOccurrences = 1, description = "Determines what happens with subsystems found.")
	public void setRemoveClonedSubsystems(
			@AConQATAttribute(name = "remove", description = "If this is set to true, duplicated subsystems are removed, which speeds up later clone detection. "
					+ "However, any clones to the removed subsystems will then be lost. Default is true.") boolean removeClonedSubsystems) {
		this.removeClonedSubsystems = removeClonedSubsystems;
	}

	/** {@inheritDoc} */
	@Override
	public void preprocess(ISimulinkResource input,
			ISimulinkNormalizer normalizer, Set<SimulinkBlock> ignoredBlocks,
			ICloneReporter reporter) throws ConQATException {

		Map<SimulinkBlock, GraphLabel> subsystemToLabel = buildSubsystemToLabelMapping(
				input, normalizer, ignoredBlocks);
		ListMap<GraphLabel, SimulinkBlock> labelToSubsystems = invertLabelMap(subsystemToLabel);

		for (GraphLabel key : labelToSubsystems.getKeys()) {
			List<SimulinkBlock> list = labelToSubsystems.getCollection(key);
			if (list == null || list.size() < 2) {
				continue;
			}

			GraphLabel parentLabel = subsystemToLabel.get(list.get(0)
					.getParent());
			List<SimulinkBlock> parentList = labelToSubsystems
					.getCollection(parentLabel);
			if (parentList != null && parentList.size() >= list.size()) {
				// no need to report as parent will be reported already
				continue;
			}

			reportClones(list, subsystemToLabel, reporter);
		}

		if (removeClonedSubsystems) {
			markClonesAsIgnored(labelToSubsystems, ignoredBlocks);
		}
	}

	/** Builds the mapping from subsystems to graph labels. */
	private Map<SimulinkBlock, GraphLabel> buildSubsystemToLabelMapping(
			ISimulinkResource root, ISimulinkNormalizer normalizer,
			Set<SimulinkBlock> ignoredBlocks) throws ConQATException {
		Map<SimulinkBlock, GraphLabel> subsystemToLabel = new IdentityHashMap<SimulinkBlock, GraphLabel>();
		for (ISimulinkElement element : ResourceTraversalUtils.listElements(
				root, ISimulinkElement.class)) {
			calculateSubsystemLabels(element.getModel(), normalizer,
					subsystemToLabel, ignoredBlocks);
		}
		return subsystemToLabel;
	}

	/** Inverts the subsystem to label map. */
	private ListMap<GraphLabel, SimulinkBlock> invertLabelMap(
			Map<SimulinkBlock, GraphLabel> subsystemToLabel) {
		ListMap<GraphLabel, SimulinkBlock> labelToSubsystems = new ListMap<GraphLabel, SimulinkBlock>();
		for (Entry<SimulinkBlock, GraphLabel> entry : subsystemToLabel
				.entrySet()) {
			labelToSubsystems.add(entry.getValue(), entry.getKey());
		}
		return labelToSubsystems;
	}

	/**
	 * Calculates canonical labels for all subsystems and stores them in the map
	 * provided. The string hash for the subsystem is returned (or null if the
	 * block has no children).
	 */
	private String calculateSubsystemLabels(SimulinkBlock block,
			ISimulinkNormalizer normalizer,
			Map<SimulinkBlock, GraphLabel> subsystemToLabel,
			Set<SimulinkBlock> ignoredBlocks) throws ConQATException {
		if (!block.hasSubBlocks()) {
			// return value not used in this case
			return null;
		}

		Map<SimulinkBlock, SimulinkNode> blockMap = new IdentityHashMap<SimulinkBlock, SimulinkNode>();

		for (SimulinkBlock child : block.getSubBlocks()) {
			if (ignoredBlocks.contains(child)) {
				continue;
			}

			SimulinkNode node;
			if (child.hasSubBlocks()) {
				node = new SimulinkNode(child, calculateSubsystemLabels(child,
						normalizer, subsystemToLabel, ignoredBlocks), 1);
			} else {
				node = new SimulinkNode(child,
						normalizer.normalizeBlock(child), 1);
			}
			blockMap.put(child, node);
		}

		GraphLabel label = CanonicalLabelCreator.getCanonicalLabel(
				blockMap.values(), buildEdges(blockMap, normalizer));
		subsystemToLabel.put(block, label);
		return label.getTextualHash();
	}

	/** Build the edges list for the given set of nodes. */
	private List<SimulinkDirectedEdge> buildEdges(
			Map<SimulinkBlock, SimulinkNode> blockMap,
			ISimulinkNormalizer normalizer) throws ConQATException {
		List<SimulinkDirectedEdge> edges = new ArrayList<SimulinkDirectedEdge>();
		for (SimulinkBlock block : blockMap.keySet()) {
			for (SimulinkLine line : block.getOutLines()) {
				SimulinkNode sourceNode = blockMap.get(line.getSrcPort()
						.getBlock());
				SimulinkNode targetNode = blockMap.get(line.getDstPort()
						.getBlock());

				if (sourceNode == null || targetNode == null) {
					// just ignore lines leaving the subsystem
					continue;
				}
				edges.add(new SimulinkDirectedEdge(line, normalizer
						.normalizeLine(line), sourceNode, targetNode));
			}
		}
		return edges;
	}

	/** Reports a list of clones. */
	private void reportClones(List<SimulinkBlock> list,
			Map<SimulinkBlock, GraphLabel> subsystemToLabel,
			ICloneReporter reporter) {
		List<List<INode>> nodeLists = new ArrayList<List<INode>>();
		List<List<IDirectedEdge>> edgeLists = new ArrayList<List<IDirectedEdge>>();
		for (SimulinkBlock subsystem : list) {
			List<INode> orderedNodes = extractOrderedNodes(subsystem,
					subsystemToLabel);
			nodeLists.add(orderedNodes);
			edgeLists.add(computeEdges(orderedNodes));
		}

		reporter.startModelCloneGroup(list.size(), nodeLists.get(0).size(),
				edgeLists.get(0).size());
		for (int i = 0; i < list.size(); ++i) {
			reporter.addModelCloneInstance(nodeLists.get(i), edgeLists.get(i));
		}
	}

	/**
	 * Extracts all nodes stored (recursively) under the given subsystem.
	 * Respects the order determined by the graph labels.
	 */
	private List<INode> extractOrderedNodes(SimulinkBlock subsystem,
			Map<SimulinkBlock, GraphLabel> subsystemToLabel) {
		List<INode> result = new ArrayList<INode>();

		GraphLabel label = subsystemToLabel.get(subsystem);
		for (INode node : label.getNodes()) {
			SimulinkBlock block = ((SimulinkNode) node).getBlock();
			if (block.hasSubBlocks()) {
				result.addAll(extractOrderedNodes(block, subsystemToLabel));
			} else {
				result.add(node);
			}
		}
		return result;
	}

	/** Computes the list of all edges for the given list of nodes. */
	private List<IDirectedEdge> computeEdges(List<INode> nodes) {
		List<IDirectedEdge> result = new ArrayList<IDirectedEdge>();
		Map<SimulinkBlock, SimulinkNode> blocks = new IdentityHashMap<SimulinkBlock, SimulinkNode>();
		for (INode node : nodes) {
			blocks.put(((SimulinkNode) node).getBlock(), (SimulinkNode) node);
		}

		for (SimulinkBlock block : blocks.keySet()) {
			for (SimulinkLine line : block.getOutLines()) {
				SimulinkNode source = blocks.get(line.getSrcPort().getBlock());
				SimulinkNode target = blocks.get(line.getDstPort().getBlock());
				if (source != null && target != null) {
					// We do not need valid labels for reporting
					String edgeLabel = StringUtils.EMPTY_STRING;
					result.add(new SimulinkDirectedEdge(line, edgeLabel,
							source, target));
				}
			}
		}

		return result;
	}

	/** Remove all clones from the model by adding them to the ignore list. */
	private void markClonesAsIgnored(
			ListMap<GraphLabel, SimulinkBlock> labelToSubsystems,
			Set<SimulinkBlock> ignoredBlocks) {
		for (GraphLabel key : labelToSubsystems.getKeys()) {
			List<SimulinkBlock> list = labelToSubsystems.getCollection(key);
			if (list != null && list.size() >= 2) {
				for (SimulinkBlock block : list) {
					ignoredBlocks.addAll(SimulinkUtils
							.listBlocksDepthFirst(block));
				}
			}
		}
	}
}