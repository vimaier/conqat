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
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.conqat.engine.core.logging.IConQATLogger;
import org.conqat.engine.model_clones.detection.util.AugmentedModelGraph;
import org.conqat.engine.model_clones.detection.util.EDirection;
import org.conqat.engine.model_clones.detection.util.ICloneReporter;
import org.conqat.engine.model_clones.model.IDirectedEdge;
import org.conqat.engine.model_clones.model.INode;
import org.conqat.lib.commons.algo.MaxWeightMatching;
import org.conqat.lib.commons.collections.IdentityHashSet;
import org.conqat.lib.commons.collections.IdentityPairMap;
import org.conqat.lib.commons.collections.ImmutablePair;
import org.conqat.lib.commons.collections.ListMap;
import org.conqat.lib.commons.collections.PairList;

/**
 * Model clone detection based on a breath-first search (BFS) starting from two
 * nodes in parallel, which is guided by a similarity heuristic.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 36637 $
 * @ConQAT.Rating GREEN Hash: 0159BCF239FB4674EC7EFA599FEFDC1F
 */
public class PairDetector {

	/** The graph we are working on. */
	private final AugmentedModelGraph graph;

	/** Cache of pairs that have been visited so far. */
	private final IdentityPairMap<INode, PairInfo> pairInfo = new IdentityPairMap<INode, PairInfo>();

	/** The minimum size a clone should have to be reported. */
	private final int minSize;

	/** The minimum weight a clone should have to be reported. */
	private final int minWeight;

	/** Whether early exit strategy is used. */
	private final boolean earlyExit;

	/** The clone reporter. */
	private final ICloneReporter cloneReporter;

	/** The logger used. */
	private final IConQATLogger logger;

	/** Matcher used. */
	private final MaxWeightMatching<INode, INode> matcher = new MaxWeightMatching<INode, INode>();

	/** Weight provider based on {@link #pairInfo} */
	private final MaxWeightMatching.IWeightProvider<INode, INode> piWeightProvider = new MaxWeightMatching.IWeightProvider<INode, INode>() {
		@Override
		public double getConnectionWeight(INode node1, INode node2) {
			PairInfo info = pairInfo.get(node1, node2);
			if (info == null) {
				return 0;
			}
			return info.similarity;
		}
	};

	/** Constructor. */
	public PairDetector(AugmentedModelGraph graph, int minSize, int minWeight,
			boolean earlyExit, ICloneReporter cloneReporter,
			IConQATLogger logger) {
		this.graph = graph;
		this.minSize = minSize;
		this.minWeight = minWeight;
		this.cloneReporter = cloneReporter;
		this.earlyExit = earlyExit;
		this.logger = logger;
	}

	/**
	 * Run the main loop of the detection and report all clones found to the
	 * reporter.
	 */
	public void execute() {
		List<List<INode>> connectedComponents = extractConnectedComponents();

		int sumPairs = runWithinComponents(connectedComponents);
		sumPairs += runAcrossComponents(connectedComponents);

		logger.info("Overall processed " + sumPairs
				+ " pairs during pair detection.");
	}

	/** Extract the connected components of the graph. */
	private List<List<INode>> extractConnectedComponents() {
		List<List<INode>> connectedComponents = new ArrayList<List<INode>>();
		IdentityHashSet<INode> seen = new IdentityHashSet<INode>();

		int skippedComponents = 0;
		int numNodes = 0;
		for (INode n : graph.getNodes()) {
			if (seen.contains(n)) {
				continue;
			}
			IdentityHashSet<INode> currentCluster = new IdentityHashSet<INode>();
			currentCluster.add(n);
			runDFS(n, currentCluster);
			seen.addAll(currentCluster);

			// filter clutter
			if (currentCluster.size() >= minSize) {
				logger.debug("Found component of size " + currentCluster.size());
				numNodes += currentCluster.size();
				connectedComponents.add(new ArrayList<INode>(currentCluster));
			} else {
				++skippedComponents;
			}
		}
		logger.info("During connected component extraction: could skip "
				+ skippedComponents + " components, keeping "
				+ connectedComponents.size() + " (overall " + numNodes
				+ " nodes)");

		return connectedComponents;
	}

	/**
	 * Performs a DFS starting from the given node using both forward and
	 * backward edges. The nodes already visited are stored in the given set.
	 */
	private void runDFS(INode node, Set<INode> seen) {
		for (IDirectedEdge edge : graph.getEdgeClusters(node,
				EDirection.FORWARD).getValues()) {
			visitNode(seen, edge.getTargetNode());
		}

		for (IDirectedEdge edge : graph.getEdgeClusters(node,
				EDirection.BACKWARD).getValues()) {
			visitNode(seen, edge.getSourceNode());
		}
	}

	/** Visit node during DFS and continue DFS, if not has not yet been seen */
	private void visitNode(Set<INode> seen, INode other) {
		if (!seen.contains(other)) {
			seen.add(other);
			runDFS(other, seen);
		}
	}

	/**
	 * Search for clones within one connected component.
	 * 
	 * @return the number of pairs processed.
	 */
	private int runWithinComponents(List<List<INode>> connectedComponents) {
		int sumPairs = 0;
		for (List<INode> component : connectedComponents) {
			List<ImmutablePair<INode, INode>> pairs = createPairsWithinComponent(component);
			if (!pairs.isEmpty()) {
				sumPairs += pairs.size();
				processPairs(pairs);
			}
		}
		return sumPairs;
	}

	/**
	 * Creates a list of possible starting candidates within a single connected
	 * component.
	 */
	private List<ImmutablePair<INode, INode>> createPairsWithinComponent(
			List<INode> component) {
		List<ImmutablePair<INode, INode>> pairs = new ArrayList<ImmutablePair<INode, INode>>();

		ListMap<Integer, INode> clustered = new ListMap<Integer, INode>();
		for (INode n : component) {
			clustered.add(graph.getNodeEq(n), n);
		}
		for (Integer key : clustered.getKeys()) {
			List<INode> list = clustered.getCollection(key);
			int size = list.size();
			for (int i = 0; i < size; ++i) {
				for (int j = i + 1; j < size; ++j) {
					pairs.add(new ImmutablePair<INode, INode>(list.get(i), list
							.get(j)));
				}
			}
		}
		return pairs;
	}

	/**
	 * Search for clones between all pairs of connected components.
	 * 
	 * @return the number of pairs processed.
	 */
	private int runAcrossComponents(List<List<INode>> connectedComponents) {
		int sumPairs = 0;
		int compSize = connectedComponents.size();
		for (int ci = 0; ci < compSize; ++ci) {
			ListMap<Integer, INode> clustered1 = new ListMap<Integer, INode>();
			for (INode n : connectedComponents.get(ci)) {
				clustered1.add(graph.getNodeEq(n), n);
			}
			for (int cj = ci + 1; cj < compSize; ++cj) {
				ListMap<Integer, INode> clustered2 = new ListMap<Integer, INode>();
				for (INode n : connectedComponents.get(cj)) {
					clustered2.add(graph.getNodeEq(n), n);
				}

				List<ImmutablePair<INode, INode>> pairs = createPairsAcrossComponents(
						clustered1, clustered2);
				if (!pairs.isEmpty()) {
					sumPairs += pairs.size();
					processPairs(pairs);
				}
			}
		}
		return sumPairs;
	}

	/**
	 * Creates a list of possible starting candidates between two connected
	 * components which are given pre-clustered by equivalence class index.
	 */
	private List<ImmutablePair<INode, INode>> createPairsAcrossComponents(
			ListMap<Integer, INode> clustered1,
			ListMap<Integer, INode> clustered2) {
		List<ImmutablePair<INode, INode>> pairs = new ArrayList<ImmutablePair<INode, INode>>();
		for (Integer key : clustered1.getKeys()) {
			List<INode> list1 = clustered1.getCollection(key);
			List<INode> list2 = clustered2.getCollection(key);
			if (list2 == null) {
				continue;
			}

			for (int i = 0; i < list1.size(); ++i) {
				for (int j = 0; j < list2.size(); ++j) {
					pairs.add(new ImmutablePair<INode, INode>(list1.get(i),
							list2.get(j)));
				}
			}
		}
		return pairs;
	}

	/**
	 * Perform clone detection on the given node pairs, i.e. using them as
	 * starting points.
	 */
	private void processPairs(List<ImmutablePair<INode, INode>> pairs) {
		pairInfo.clear();
		double[] similarity = SimilarityCalculator.calculateSimilarity(graph,
				pairs);
		int size = pairs.size();
		for (int i = 0; i < size; ++i) {
			pairInfo.put(pairs.get(i), new PairInfo(similarity[i]));
		}
		sortBySimilarity(pairs);

		for (ImmutablePair<INode, INode> pair : pairs) {
			if (!pairInfo.get(pair).visited) {
				new CloneBFS(pair.getFirst(), pair.getSecond()).runBFS();
			}
		}
	}

	/** Sort the given pairs of nodes by similarity. */
	private void sortBySimilarity(List<ImmutablePair<INode, INode>> pairs) {
		Collections.sort(pairs, new Comparator<ImmutablePair<INode, INode>>() {
			@Override
			public int compare(ImmutablePair<INode, INode> p1,
					ImmutablePair<INode, INode> p2) {
				double sim1 = pairInfo.get(p1).similarity;
				double sim2 = pairInfo.get(p2).similarity;
				return (int) (2. * Math.signum(sim2 - sim1));
			}
		});
	}

	/**
	 * This class implements a single breadth-first search (BFS) on the graph.
	 * We use this extra class, to keep the data structures clean.
	 */
	private class CloneBFS {

		/** The set of nodes which already have been visited. */
		private final Set<INode> seen = new IdentityHashSet<INode>();

		/** The nodes in this clone. */
		private final PairList<INode, INode> nodes = new PairList<INode, INode>();

		/**
		 * The BFS queue of original nodes to be visited. This is in sync with
		 * {@link #cloneQueue}.
		 */
		private final Queue<INode> origQueue = new LinkedList<INode>();

		/**
		 * The BFS queue of cloned nodes to be visited. This is in sync with
		 * {@link #origQueue}.
		 */
		private final Queue<INode> cloneQueue = new LinkedList<INode>();

		/** The current weight of the clone. */
		private int currentWeight = 0;

		/** Create new BFS. */
		public CloneBFS(INode orig, INode clone) {
			enqueuePair(orig, clone);
		}

		/**
		 * Put a pair of nodes onto the BFS queue (this includes remembering the
		 * pair for the final clone).
		 */
		private void enqueuePair(INode orig, INode clone) {
			origQueue.add(orig);
			cloneQueue.add(clone);
			seen.add(orig);
			seen.add(clone);
			nodes.add(orig, clone);
			currentWeight += orig.getWeight();
		}

		/** Run/continue the current BFS. */
		public void runBFS() {
			while (!origQueue.isEmpty()) {
				INode orig = origQueue.poll();
				INode clone = cloneQueue.poll();

				if (earlyExit && pairInfo.get(orig, clone).visited) {
					return;
				}
				pairInfo.get(orig, clone).visited = true;

				traverse(orig, clone, EDirection.FORWARD);
				traverse(orig, clone, EDirection.BACKWARD);
			}

			// done with BFS, so report, if size constraints satisfied
			if (nodes.size() >= minSize && currentWeight >= minWeight) {

				List<INode> origNodes = nodes.extractFirstList();
				List<INode> cloneNodes = nodes.extractSecondList();

				@SuppressWarnings("unchecked")
				List<IDirectedEdge>[] edges = graph.calculateEquivalentEdges(
						origNodes, cloneNodes);

				cloneReporter.startModelCloneGroup(2, origNodes.size(),
						edges[0].size());
				cloneReporter.addModelCloneInstance(origNodes, edges[0]);
				cloneReporter.addModelCloneInstance(cloneNodes, edges[1]);
			}
		}

		/**
		 * Continues the search (traversal) from the given node pair along the
		 * given direction.
		 */
		private void traverse(INode orig, INode clone, EDirection direction) {
			ListMap<Integer, IDirectedEdge> origEdgeClusters = graph
					.getEdgeClusters(orig, direction);
			ListMap<Integer, IDirectedEdge> cloneEdgeClusters = graph
					.getEdgeClusters(clone, direction);

			List<INode> origNext = new ArrayList<INode>();
			List<INode> cloneNext = new ArrayList<INode>();

			for (Integer key : origEdgeClusters.getKeys()) {
				List<IDirectedEdge> origEdges = origEdgeClusters
						.getCollection(key);
				List<IDirectedEdge> cloneEdges = cloneEdgeClusters
						.getCollection(key);

				if (origEdges == null || cloneEdges == null) {
					continue;
				}

				if (extractUnseen(origEdges, origNext, direction) == 0
						|| extractUnseen(cloneEdges, cloneNext, direction) == 0) {
					continue;
				}

				PairList<INode, INode> pairs = new PairList<INode, INode>();
				matcher.calculateMatching(origNext, cloneNext,
						piWeightProvider, pairs);
				enqueuePairs(pairs);
			}
		}

		/**
		 * Extracts all nodes (in the given direction) from the given edges, but
		 * use each node only once and ignore those already {@link #seen}.
		 */
		private int extractUnseen(List<IDirectedEdge> edges,
				List<INode> targets, EDirection direction) {
			targets.clear();
			Set<INode> localSeen = new IdentityHashSet<INode>();
			for (IDirectedEdge edge : edges) {
				INode node;
				if (direction == EDirection.FORWARD) {
					node = edge.getTargetNode();
				} else {
					node = edge.getSourceNode();
				}
				if (!seen.contains(node) && !localSeen.contains(node)) {
					targets.add(node);
					localSeen.add(node);
				}
			}
			return targets.size();
		}

		/** Enqueue all pairs in the given list, but filter out invalid pairs. */
		private void enqueuePairs(PairList<INode, INode> pairs) {
			for (int i = 0; i < pairs.size(); ++i) {
				INode newOrig = pairs.getFirst(i);
				INode newClone = pairs.getSecond(i);

				if (seen.contains(newOrig) || seen.contains(newClone)
						|| newOrig == newClone) {
					// This can happen due to intersections in the pairs
					continue;
				}

				enqueuePair(newOrig, newClone);
			}
		}
	}

	/** Class used for storing basic information on node pairs. */
	public static class PairInfo {

		/** Whether the pair has been visited during the BFS. */
		public boolean visited = false;

		/** The similarity of the pair. */
		public final double similarity;

		/** Constructor. */
		public PairInfo(double similarity) {
			this.similarity = similarity;
		}
	}
}