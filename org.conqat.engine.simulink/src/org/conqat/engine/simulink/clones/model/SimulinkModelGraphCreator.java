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
package org.conqat.engine.simulink.clones.model;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

import org.conqat.engine.resource.util.ResourceTraversalUtils;

import org.conqat.lib.commons.visitor.IVisitor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.simulink.clones.normalize.ISimulinkNormalizer;
import org.conqat.engine.simulink.scope.ISimulinkElement;
import org.conqat.engine.simulink.scope.ISimulinkResource;
import org.conqat.lib.simulink.model.SimulinkBlock;
import org.conqat.lib.simulink.model.SimulinkLine;
import org.conqat.lib.simulink.util.SimulinkUtils;

/**
 * Class used to create a {@link SimulinkModelGraph} from a
 * {@link ISimulinkElement}. This is not a processor, but instead called by a
 * processor directly.
 * 
 * @author $Author:hummelb $
 * @version $Rev: 35176 $
 * @ConQAT.Rating GREEN Hash: 5FAA91E234F0CAA6A913201A3BDBE4E5
 */
public class SimulinkModelGraphCreator implements
		IVisitor<SimulinkBlock, ConQATException> {

	/** The input being processed. */
	private final ISimulinkResource input;

	/** The normalizer. */
	private final ISimulinkNormalizer normalizer;

	/** The result. */
	private final SimulinkModelGraph result = new SimulinkModelGraph();

	/** Lookup from blocks to nodes. */
	private final Map<SimulinkBlock, SimulinkNode> blockMap = new IdentityHashMap<SimulinkBlock, SimulinkNode>();

	/** Set of ignored blocks. */
	private final Set<SimulinkBlock> ignoreSet;

	/**
	 * Constructor.
	 * 
	 * @param ignoreSet
	 *            all blocks contained in this set will be excluded from the
	 *            graph constructed.
	 */
	private SimulinkModelGraphCreator(ISimulinkResource rootNode,
			ISimulinkNormalizer normalizer, Set<SimulinkBlock> ignoreSet) {
		input = rootNode;
		this.normalizer = normalizer;
		this.ignoreSet = ignoreSet;
	}

	/** Create and return the model graph. */
	private SimulinkModelGraph create() throws ConQATException {
		for (ISimulinkElement element : ResourceTraversalUtils.listElements(
				input, ISimulinkElement.class)) {
			SimulinkUtils.visitDepthFirst(element.getModel(), this);
		}

		for (SimulinkBlock block : blockMap.keySet()) {
			convertEdges(block);
		}
		return result;
	}

	/**
	 * Converts the given block to a {@link SimulinkNode} and registers it with
	 * {@link #blockMap}.
	 */
	@Override
	public void visit(SimulinkBlock block) throws ConQATException {
		// only convert leaf blocks
		if (block.hasSubBlocks() || ignoreSet.contains(block)) {
			return;
		}

		SimulinkNode node = new SimulinkNode(block, normalizer
				.normalizeBlock(block), normalizer.determineWeight(block));
		blockMap.put(block, node);
		result.addNode(node);
	}

	/** Convert the edges of the given block. */
	private void convertEdges(SimulinkBlock block) throws ConQATException {
		// we are using only outgoing lines to avoid duplication here
		for (SimulinkLine line : block.getOutLines()) {
			SimulinkBlock sourceBlock = line.getSrcPort().getBlock();
			SimulinkBlock targetBlock = line.getDstPort().getBlock();

			if (ignoreSet.contains(sourceBlock)
					|| ignoreSet.contains(targetBlock)) {
				continue;
			}

			SimulinkNode sourceNode = blockMap.get(sourceBlock);
			SimulinkNode targetNode = blockMap.get(targetBlock);

			if (sourceNode == null || targetNode == null) {
				throw new ConQATException("Incomplete line: " + line);
			}
			result.addEdge(new SimulinkDirectedEdge(line, normalizer
					.normalizeLine(line), sourceNode, targetNode));
		}
	}

	/**
	 * Create the model graph from the given Simulink root node using the
	 * provided normalizations.
	 * 
	 * @param ignoreSet
	 *            all blocks contained in this set will be excluded from the
	 *            graph constructed.
	 */
	public static SimulinkModelGraph createModelGraph(
			ISimulinkResource rootNode, ISimulinkNormalizer normalizer,
			Set<SimulinkBlock> ignoreSet) throws ConQATException {
		return new SimulinkModelGraphCreator(rootNode, normalizer, ignoreSet)
				.create();
	}
}