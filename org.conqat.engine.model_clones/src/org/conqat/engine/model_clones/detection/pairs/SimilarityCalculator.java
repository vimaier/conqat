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
package org.conqat.engine.model_clones.detection.pairs;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.conqat.engine.model_clones.detection.util.AugmentedModelGraph;
import org.conqat.engine.model_clones.detection.util.EDirection;
import org.conqat.engine.model_clones.model.IDirectedEdge;
import org.conqat.engine.model_clones.model.INode;
import org.conqat.lib.commons.algo.MaxWeightMatching;
import org.conqat.lib.commons.collections.IdentityHashSet;
import org.conqat.lib.commons.collections.IdentityPairMap;
import org.conqat.lib.commons.collections.ImmutablePair;
import org.conqat.lib.commons.collections.ListMap;
import org.conqat.lib.commons.collections.PairList;

/**
 * This class calculates the similarity value as described in the paper by
 * Deissenboeck, Hummel, Juergens, Schaetz, Wagner, Girard, Teuchert: "Clone
 * Detection in Automotive Model-Based Development".
 * 
 * @author $Author: hummelb $
 * @version $Rev: 36637 $
 * @ConQAT.Rating GREEN Hash: F67D93D06493B1F0A57803A0FC22AAB7
 */
public class SimilarityCalculator {

	/** The graph. */
	private final AugmentedModelGraph graph;

	/** The weights used. */
	private final MaxWeightMatching.IWeightProvider<INode, INode> weightProvider;

	/** Class used for matching calculation. */
	private final MaxWeightMatching<INode, INode> matcher = new MaxWeightMatching<INode, INode>();

	/** Constructor. */
	private SimilarityCalculator(AugmentedModelGraph graph,
			MaxWeightMatching.IWeightProvider<INode, INode> weightProvider) {
		this.graph = graph;
		this.weightProvider = weightProvider;
	}

	/** Calculates the similarity of two nodes. */
	private double calculate(INode node1, INode node2) {
		if (graph.getNodeEq(node1) != graph.getNodeEq(node2)) {
			return 0;
		}

		double similarity = calculateSimilaritySum(node1, node2,
				EDirection.FORWARD)
				+ calculateSimilaritySum(node1, node2, EDirection.BACKWARD);

		if (graph.getNumEdges(node1) > 0) {
			similarity /= Math.max(graph.getNumEdges(node1),
					graph.getNumEdges(node2));
		}

		return similarity;
	}

	/** Returns the sum of matching the edges in the given direction. */
	private double calculateSimilaritySum(INode node1, INode node2,
			EDirection direction) {
		double similarity = 0;

		ListMap<Integer, IDirectedEdge> edgeClusters1 = graph.getEdgeClusters(
				node1, direction);
		ListMap<Integer, IDirectedEdge> edgeClusters2 = graph.getEdgeClusters(
				node2, direction);

		List<INode> oppositeNodes1 = new ArrayList<INode>();
		List<INode> oppositeNodes2 = new ArrayList<INode>();

		for (Integer key : edgeClusters1.getKeys()) {
			List<IDirectedEdge> edges1 = edgeClusters1.getCollection(key);
			List<IDirectedEdge> edges2 = edgeClusters2.getCollection(key);

			if (edges1 == null || edges2 == null) {
				continue;
			}

			if (extractOppositeNodes(edges1, oppositeNodes1, direction) == 0
					|| extractOppositeNodes(edges2, oppositeNodes2, direction) == 0) {
				continue;
			}

			PairList<INode, INode> mapping = new PairList<INode, INode>();
			matcher.calculateMatching(oppositeNodes1, oppositeNodes2,
					weightProvider, mapping);
			for (int i = 0; i < mapping.size(); ++i) {
				similarity += weightProvider.getConnectionWeight(
						mapping.getFirst(i), mapping.getSecond(i));
			}
		}
		return similarity;
	}

	/**
	 * Puts a list of all target nodes of the given edges into the targets list.
	 * Each target is stored only once (even if it occurs multiple times).
	 */
	private int extractOppositeNodes(List<IDirectedEdge> edges,
			List<INode> opposoteNodes, EDirection direction) {
		opposoteNodes.clear();
		Set<INode> localSeen = new IdentityHashSet<INode>();
		for (IDirectedEdge edge : edges) {
			INode node;
			if (direction == EDirection.FORWARD) {
				node = edge.getTargetNode();
			} else {
				node = edge.getSourceNode();
			}
			if (!localSeen.contains(node)) {
				opposoteNodes.add(node);
				localSeen.add(node);
			}
		}
		return opposoteNodes.size();
	}

	/**
	 * Calculates the similarity values for a list of node pairs. The given
	 * pairs must be complete in the sense that all connected pairs must be
	 * included in the list.
	 */
	public static double[] calculateSimilarity(AugmentedModelGraph graph,
			List<ImmutablePair<INode, INode>> pairs) {
		int size = pairs.size();
		double[] similarity = new double[size];

		IdentityPairMap<INode, Double> base = new IdentityPairMap<INode, Double>();
		IdentityPairMap<INode, Double> second = new IdentityPairMap<INode, Double>();

		for (int i = 0; i < size; ++i) {
			similarity[i] = .5;
			base.put(pairs.get(i), 1.);
		}

		final int numIter = 5;
		double factor = .25;
		for (int iter = 1; iter <= numIter; ++iter, factor *= .5) {
			final IdentityPairMap<INode, Double> finalBase = base;
			SimilarityCalculator so = new SimilarityCalculator(graph,
					new BaseWeightProvider(finalBase));

			for (int i = 0; i < size; ++i) {
				ImmutablePair<INode, INode> p = pairs.get(i);
				double value = so.calculate(p.getFirst(), p.getSecond());
				second.put(p, value);
				similarity[i] += factor * value;
			}

			base = second;
			second = new IdentityPairMap<INode, Double>();
		}

		return similarity;
	}

	/** A weight provider which is based on given weights. */
	private static final class BaseWeightProvider implements
			MaxWeightMatching.IWeightProvider<INode, INode> {

		/** The base weights. */
		private final IdentityPairMap<INode, Double> base;

		/** Constructor. */
		private BaseWeightProvider(IdentityPairMap<INode, Double> base) {
			this.base = base;
		}

		/** {@inheritDoc} */
		@Override
		public double getConnectionWeight(INode node1, INode node2) {
			Double d = base.get(node1, node2);
			if (d == null) {
				return 0;
			}
			return d;
		}
	}
}