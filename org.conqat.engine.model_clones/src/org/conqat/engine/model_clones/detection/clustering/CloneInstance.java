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
package org.conqat.engine.model_clones.detection.clustering;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.conqat.engine.model_clones.detection.util.AugmentedModelGraph;
import org.conqat.engine.model_clones.model.INode;

/**
 * A single element of a clone class, i.e. a subgraph of the model graph.
 * 
 * @author hummelb
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 * @levd.rating GREEN Hash: 2300AEF656234AFC49284473C59471BF
 */
/* package */class CloneInstance {

	/** The nodes. */
	private final List<INode> nodes;

	/** A sorted list of node indices used for equality testing. */
	private final int[] nodeIndices;

	/** The hash code. */
	private final int hashCode;

	/** Constructor. */
	public CloneInstance(List<INode> nodes, AugmentedModelGraph graph) {
		this.nodes = nodes;
		nodeIndices = new int[nodes.size()];
		for (int i = 0; i < nodeIndices.length; ++i) {
			nodeIndices[i] = graph.getNodeIndex(nodes.get(i));
		}
		Arrays.sort(nodeIndices);

		hashCode = Arrays.hashCode(nodeIndices) + 13 * nodeIndices.length;
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		return hashCode;
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CloneInstance)) {
			return false;
		}
		CloneInstance compareTo = (CloneInstance) obj;

		if (compareTo.hashCode != hashCode
				|| compareTo.nodes.size() != nodes.size()) {
			return false;
		}

		for (int i = 0; i < nodeIndices.length; ++i) {
			if (nodeIndices[i] != compareTo.nodeIndices[i]) {
				return false;
			}
		}

		return true;
	}

	/** Returns the nodes. */
	public List<INode> getNodes() {
		return nodes;
	}

	/** Applies the given permutation to the list of nodes. */
	public void applyPermutation(int[] permutation) {
		List<INode> oldList = new ArrayList<INode>(nodes);
		for (int i = 0; i < nodes.size(); ++i) {
			nodes.set(permutation[i], oldList.get(i));
		}
	}

	/**
	 * Returns a suitable permutation for transforming the nodes from the source
	 * list to those of the target list using the
	 * {@link #applyPermutation(int[])} method. The returned array contains at
	 * position i the index to which the i-th element of source should be
	 * placed.
	 * <p>
	 * Precondition: all nodes from the source list should be contained in the
	 * target list.
	 */
	public static int[] determinePermutation(List<INode> source,
			List<INode> target) {
		Map<INode, Integer> targetIndex = new IdentityHashMap<INode, Integer>();
		for (int i = 0; i < target.size(); ++i) {
			targetIndex.put(target.get(i), i);
		}

		int[] result = new int[source.size()];
		for (int i = 0; i < source.size(); ++i) {
			result[i] = targetIndex.get(source.get(i));
		}
		return result;
	}

	/** Returns whether the given CloneInstance is contained in this one. */
	public boolean contains(CloneInstance cloneInstance) {
		int i = 0;
		for (int index : cloneInstance.nodeIndices) {
			while (i < nodeIndices.length && nodeIndices[i] < index) {
				++i;
			}
			if (i >= nodeIndices.length || nodeIndices[i] != index) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Calculates an array which contains the indices of those nodes which
	 * should be taken from this clone instance's nodes list to form the given
	 * clone instance.
	 * <p>
	 * Precondition for extraction is that the clone instance to be extracted is
	 * actually a subset of the containing clone instance. (i.e. call
	 * {@link #contains(CloneInstance)}) as you will get a
	 * {@link NullPointerException} otherwise.
	 */
	public int[] extractSubCloneNodeIndices(CloneInstance subClone) {
		return determinePermutation(subClone.nodes, nodes);
	}

	/** Extract a CloneInstance from the nodes given by the extraction table. */
	public CloneInstance extractSubClone(int[] subCloneNodeIndices,
			AugmentedModelGraph graph) {
		List<INode> extractedNodes = new ArrayList<INode>();
		for (int i : subCloneNodeIndices) {
			extractedNodes.add(nodes.get(i));
		}
		return new CloneInstance(extractedNodes, graph);
	}
}