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
package org.conqat.engine.core.driver.specification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.conqat.engine.core.driver.declaration.DeclarationAttribute;
import org.conqat.engine.core.driver.declaration.DeclarationParameter;
import org.conqat.engine.core.driver.declaration.IDeclaration;
import org.conqat.engine.core.driver.error.BlockFileException;
import org.conqat.engine.core.driver.error.EDriverExceptionType;
import org.conqat.engine.core.driver.util.IInputReferencable;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Class for checking the acyclicity of the declaration graph (declarations with
 * references) of a block specification.
 * <p>
 * In addition, an order for the declarations is found, such that each
 * declaration references only declarations which are earlier in the list (this
 * is known as a topological sort in graph theory). This order guarantees, that
 * when the instances of these declarations are executed in this order, the
 * accessed results have already been calculated. The algorithm used runs in
 * linear time in both the number of declarations and references.
 * <p>
 * As this is only used for the initialization of the block specification it has
 * package visibility.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 7A6D11D0A4C3FDEDEC48A84D08AAAB0F
 */
/* package */class TopSorter {

	/** The block specification to work on. */
	private final BlockSpecification blockSpecification;

	/** The nodes used for sorting. */
	private final Map<IDeclaration, TopSorterDeclarationNode> nodeMap = new HashMap<IDeclaration, TopSorterDeclarationNode>();

	/** Create new top sorter. */
	/* package */TopSorter(BlockSpecification blockSpec) {
		blockSpecification = blockSpec;
		init();
	}

	/** Construct the declaration graph. */
	private void init() {
		for (IDeclaration declaration : blockSpecification.getDeclarationList()) {
			TopSorterDeclarationNode node = new TopSorterDeclarationNode(
					declaration);
			nodeMap.put(declaration, node);
		}

		for (TopSorterDeclarationNode node : CollectionUtils.sort(nodeMap
				.values())) {
			for (DeclarationParameter parameter : node.getDeclaration()
					.getParameters()) {
				for (DeclarationAttribute attribute : parameter.getAttributes()) {

					// we ignore links to BlockSpecAttributes here as these are
					// not relevant for sorting
					if (attribute.isReference()) {
						IInputReferencable referenced = attribute
								.getReference();

						if (referenced.asDeclarationOutput() != null) {
							TopSorterDeclarationNode referencedNode = nodeMap
									.get(referenced.asDeclarationOutput()
											.getDeclaration());
							node.addEdgeTo(referencedNode);
						}
					}
				}
			}
		}
	}

	/**
	 * Perform the topologic sort on the nodes of the declaration graph.
	 * 
	 * @return a valid ordering of the declarations as defined in the
	 *         description of this class.
	 * @throws BlockFileException
	 *             if the declaration graph is cyclic.
	 */
	public List<IDeclaration> sort() throws BlockFileException {
		ArrayList<IDeclaration> result = new ArrayList<IDeclaration>();

		// we need a set we can insert and extract from in constant time (so
		// many other classes could be used instead)
		Stack<TopSorterDeclarationNode> nodesWithoutOutgoingEdges = new Stack<TopSorterDeclarationNode>();

		// find all nodes without outgoing edges
		for (TopSorterDeclarationNode node : CollectionUtils.sort(nodeMap
				.values())) {
			if (node.getOutgoingCount() == 0) {
				nodesWithoutOutgoingEdges.push(node);
			}
		}

		// remove nodes without outgoing edges and put them on the result list
		// (as these will no reference any not-yet-calculated results).
		while (!nodesWithoutOutgoingEdges.isEmpty()) {
			TopSorterDeclarationNode node = nodesWithoutOutgoingEdges.pop();
			result.add(node.getDeclaration());
			node.eliminate(nodesWithoutOutgoingEdges);
		}

		// if we missed some nodes there has to be a cycle.
		if (result.size() < nodeMap.size()) {
			throw new BlockFileException(
					EDriverExceptionType.CYCLIC_DEPENDENCIES, "Cycle: "
							+ StringUtils.concat(getCycle(), " => "),
					blockSpecification);
		}

		return result;
	}

	/**
	 * If the top sort fails this returns one of the cycles found. We know from
	 * the algorithm, that now all remaining nodes do have outgoing edges, so we
	 * can just follow outgoing edges infinitely often. As there is only a
	 * finite number of nodes we must reach one node more than once and thus
	 * found our cycle. We only have to be careful to avoid nodes whose
	 * outgoingCount is 0, as these are "deleted" (not actually, but they are
	 * interpreted this way). Clearly this algorithm also works in linear time.
	 */
	private List<IDeclaration> getCycle() {
		TopSorterDeclarationNode node = findStartNodeForCycle();

		// linked hash map stores order of addition
		LinkedHashSet<IDeclaration> cycle = new LinkedHashSet<IDeclaration>();

		// traverse until start node, every node must have an outgoing node as
		// it is part of a cycle
		do {
			cycle.add(node.getDeclaration());
			node = node.getNextNodeInCycle();
		} while (!cycle.contains(node.getDeclaration()));

		return new ArrayList<IDeclaration>(cycle);
	}

	/**
	 * Returns a suitable start node for a cycle as desribed for
	 * {@link #getCycle()}.
	 */
	private TopSorterDeclarationNode findStartNodeForCycle() {
		for (TopSorterDeclarationNode node : nodeMap.values()) {
			if (node.getOutgoingCount() > 0) {
				return node;
			}
		}
		throw new IllegalStateException(
				"This method should only be called after topological sort failed!");
	}

}