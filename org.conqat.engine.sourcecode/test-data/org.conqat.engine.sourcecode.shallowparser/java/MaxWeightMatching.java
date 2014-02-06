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
package org.conqat.lib.commons.algo;

import java.util.Arrays;
import java.util.List;

import org.conqat.lib.commons.collections.PairList;

/**
 * A class for calculating maximum weighted matching using an augmenting path
 * algorithm running in O(n^3*m), where n is the size of the smaller node set
 * and m the size of the larger one. In practice the running time is much less.
 * <p>
 * This class is not thread save!
 * 
 * @author hummelb
 * @author $Author: hummelb $
 * @version $Rev: 35946 $
 * @ConQAT.Rating GREEN Hash: 8AD1D19A6D49E4EC4F64E29FF65501BA
 * 
 * @param <N1>
 *            The first node type
 * @param <N2>
 *            The second node type
 */
public class MaxWeightMatching<N1, N2> {

	/**
	 * Flag indicating whether we are running in swapped mode. Swapped mode is
	 * needed as our algorithm requires the second set of nodes not to be
	 * smaller than the first set. If this is not the case, we just swap these
	 * sets, but we need this flag to adjust some parts of the code.
	 */
	private boolean swapped;

	/** Size of the first (or second if {@link #swapped}) node set. */
	private int size1;

	/** Size of the second (or first if {@link #swapped}) node set. */
	private int size2;

	/** The first node set. */
	private List<N1> nodes1;

	/** The second node set. */
	private List<N2> nodes2;

	/** The provider for the weights (i.e. weight matrix). */
	private IWeightProvider<N1, N2> weightProvider;

	/**
	 * This array stores for each node of the second set the index of the node
	 * from the first set, it is matched to (or -1 if is not in matching). If
	 * {@link #swapped}, first and second set change meaning.
	 */
	private int[] mate = new int[16];

	/**
	 * This is used while searching shortest path and stores the node index we
	 * came from.
	 */
	private int[] from = new int[16];

	/**
	 * This is used while searching shortest path and stores the distance (i.e.
	 * weight sum) to this node.
	 */
	private double[] dist = new double[16];

	/**
	 * Calculate the weighted bipartite matching.
	 * 
	 * @param matching
	 *            if this is non <code>null</code>, the matching (i.e. the pairs of nodes
	 *            matched onto each other) will be put into it.
	 * 
	 * @return the weight of the matching.
	 */
	public double calculateMatching(List<N1> nodes1, List<N2> nodes2,
			IWeightProvider<N1, N2> weightProvider, PairList<N1, N2> matching) {

		if (matching != null) {
			matching.clear();
		}

		if (nodes1.isEmpty() || nodes2.isEmpty()) {
			return 0;
		}

		init(nodes1, nodes2, weightProvider);
		prepareInternalArrays();

		for (int i = 0; i < size1; ++i) {
			augmentFrom(i);
		}

		double res = 0;
		for (int i = 0; i < size2; ++i) {
			if (mate[i] >= 0) {
				if (matching != null) {
					if (swapped) {
						matching.add(nodes1.get(i), nodes2.get(mate[i]));
					} else {
						matching.add(nodes1.get(mate[i]), nodes2.get(i));
					}
				}
				res += getWeight(mate[i], i);
			}
		}
		return res;
	}

	/**
	 * Initializes the data structures from the parameters to the
	 * {@link #calculateMatching(List, List, org.conqat.lib.commons.algo.MaxWeightMatching.IWeightProvider, PairList)}
	 * method.
	 */
	private void init(List<N1> nodes1, List<N2> nodes2,
			IWeightProvider<N1, N2> weightProvider) {
		if (nodes1.size() <= nodes2.size()) {
			size1 = nodes1.size();
			size2 = nodes2.size();
			swapped = false;
		} else {
			size1 = nodes2.size();
			size2 = nodes1.size();
			swapped = true;
		}
		this.nodes1 = nodes1;
		this.nodes2 = nodes2;
		this.weightProvider = weightProvider;
	}

	/** Make sure all internal arrays are large enough. */
	private void prepareInternalArrays() {
		if (size2 > mate.length) {
			int newSize = mate.length;
			while (newSize < size2) {
				newSize *= 2;
			}
			mate = new int[newSize];
			from = new int[newSize];
			dist = new double[newSize];
		}

		Arrays.fill(mate, 0, size2, -1);
	}

	/**
	 * Calculate shortest augmenting path and augment along it starting from the
	 * given node (index).
	 */
	private void augmentFrom(int u) {
		for (int i = 0; i < size2; ++i) {
			from[i] = -1;
			dist[i] = getWeight(u, i);
		}
		bellmanFord();
		int target = findBestUnmatchedTarget();
		augmentAlongPath(u, target);
	}

	/** Calculate the shortest path using Bellman-Ford algorithm. */
	private void bellmanFord() {
		boolean changed = true;
		while (changed) {
			changed = false;
			for (int i = 0; i < size2; ++i) {
				if (mate[i] < 0) {
					continue;
				}
				double w = getWeight(mate[i], i);
				for (int j = 0; j < size2; ++j) {
					if (i == j) {
						continue;
					}
					double newDist = dist[i] - w + getWeight(mate[i], j);
					if (newDist - 1e-15 > dist[j]) {
						dist[j] = newDist;
						from[j] = i;
						changed = true;
					}
				}
			}
		}
	}

	/** Find the best target which is not yet in the matching. */
	private int findBestUnmatchedTarget() {
		int target = -1;
		for (int i = 0; i < size2; ++i) {
			if (mate[i] < 0) {
				if (target < 0 || dist[i] > dist[target]) {
					target = i;
				}
			}
		}
		return target;
	}

	/** Augment along the given path to the target by adjusting the mate array. */
	private void augmentAlongPath(int u, int target) {
		while (from[target] >= 0) {
			mate[target] = mate[from[target]];
			target = from[target];
		}
		mate[target] = u;
	}

	/**
	 * Returns the weight between two nodes (=indices) handling swapping
	 * transparently.
	 */
	private double getWeight(int i1, int i2) {
		if (swapped) {
			return weightProvider.getConnectionWeight(nodes1.get(i2), nodes2
					.get(i1));
		}
		return weightProvider.getConnectionWeight(nodes1.get(i1), nodes2
				.get(i2));
	}

	/** A class providing the weight for a connection between two nodes. */
	public interface IWeightProvider<N1, N2> {

		/** Returns the weight of the connection between both nodes. */
		double getConnectionWeight(N1 node1, N2 node2);
	}
}