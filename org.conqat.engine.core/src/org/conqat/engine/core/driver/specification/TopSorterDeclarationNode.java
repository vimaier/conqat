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
import java.util.Collection;

import org.conqat.engine.core.driver.declaration.IDeclaration;

/**
 * Node class for the declaration graph used in the {@link TopSorter} class.
 * 
 * @author Florian Deissenboeck
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 736083979C0529790DB2D2D2CCF149BC
 */
/* package */class TopSorterDeclarationNode implements
		Comparable<TopSorterDeclarationNode> {

	/** The declaration corresponding to this node. */
	private final IDeclaration declaration;

	/** Incoming edges. */
	private final Collection<TopSorterDeclarationNode> incoming = new ArrayList<TopSorterDeclarationNode>();

	/** Outgoing edges. */
	private final Collection<TopSorterDeclarationNode> outgoing = new ArrayList<TopSorterDeclarationNode>();

	/**
	 * Counts the number of outgoing edges. Later we just decrement this instead
	 * of actually removeing the edge from the list (which would be expensive).
	 */
	private int outgoingCount = 0;

	/** Create new node. */
	/* package */TopSorterDeclarationNode(IDeclaration declaration) {
		this.declaration = declaration;
	}

	/** Inserts an edge to another node. */
	public void addEdgeTo(TopSorterDeclarationNode otherNode) {
		outgoing.add(otherNode);
		++outgoingCount;
		otherNode.incoming.add(this);
	}

	/** Returns the declaration belonging to this node. */
	public IDeclaration getDeclaration() {
		return declaration;
	}

	/** String represenation is equal to the processors string represenation. */
	@Override
	public String toString() {
		return declaration.toString();
	}

	/**
	 * Returns the next node in the cycle. This should only be used if now all
	 * remaining nodes do have outgoing edges.
	 * 
	 * @return the next outgoing node which itself has outgoing nodes.
	 */
	/* package */TopSorterDeclarationNode getNextNodeInCycle() {
		for (TopSorterDeclarationNode node : outgoing) {
			if (node.getOutgoingCount() > 0) {
				return node;
			}
		}
		throw new IllegalStateException(
				"This may only be called if there is a cycle for sure!");
	}

	/** Returns the current number of outgoing edges of this node. */
	public int getOutgoingCount() {
		return outgoingCount;
	}

	/**
	 * Eliminated this node. This also removes all incoming edges of this node,
	 * which is done by decreasing the outgoingCount for referenced nodes. If a
	 * referencedNode has no more outgoing edges then, it is added to the
	 * provided collection.
	 */
	public void eliminate(
			Collection<TopSorterDeclarationNode> nodesWithoutOutgoingEdges) {
		for (TopSorterDeclarationNode node : incoming) {
			node.outgoingCount -= 1;
			if (node.outgoingCount == 0) {
				nodesWithoutOutgoingEdges.add(node);
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	public int compareTo(TopSorterDeclarationNode node) {
		return getDeclaration().getName().compareTo(
				node.getDeclaration().getName());
	}
}