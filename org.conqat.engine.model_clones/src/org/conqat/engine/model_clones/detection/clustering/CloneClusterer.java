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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.conqat.engine.core.logging.IConQATLogger;
import org.conqat.engine.model_clones.detection.pairs.PairDetector;
import org.conqat.engine.model_clones.detection.util.AugmentedModelGraph;
import org.conqat.engine.model_clones.detection.util.ICloneReporter;
import org.conqat.engine.model_clones.model.IDirectedEdge;
import org.conqat.engine.model_clones.model.INode;
import org.conqat.lib.commons.algo.UnionFind;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.collections.IdentityHashSet;
import org.conqat.lib.commons.collections.ListMap;

/**
 * This class is responsible for clustering clones (which are usually reported
 * as pairs) to entire clone classes. It acts as an {@link ICloneReporter} for a
 * detection reporting only pairs (such as the {@link PairDetector}) and
 * performs its clustering and own reporting on a call of
 * {@link #performClustering()}.
 * <p>
 * Clustering as implemented here is only based on nodes (ignoring edges) and as
 * such may result in clone classes having no commons edges.
 * <p>
 * The current implementation can only deal correctly with clone pairs, i.e. it
 * should only be used in conjunction with the {@link PairDetector}. To be more
 * generally applicable, the case which is currently marked with an
 * {@link CCSMAssert#fail(String)} call has to be implemented, which is slightly
 * complicated.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 35171 $
 * @ConQAT.Rating GREEN Hash: 8504093B5B8C33F3B6A895F7814ACDF7
 */
public class CloneClusterer implements ICloneReporter {

	/** The graph we are working on. */
	private final AugmentedModelGraph graph;

	/** The reporter used for delivering the clustered clones. */
	private final ICloneReporter cloneReporter;

	/** The logger used. */
	private final IConQATLogger logger;

	/** Union find structure used for clustering. */
	private final UnionFind unionFind = new UnionFind();

	/**
	 * Mapping from clone instances to entries storing the clone instance and
	 * its union find index.
	 */
	private final Map<CloneInstance, CloneInstanceEntry> cloneInstances = new HashMap<CloneInstance, CloneInstanceEntry>();

	/**
	 * The index in the union find structure the last clone instance of the
	 * current clone class was added to
	 */
	private int lastUnionFindIndex = -1;

	/** The permutation used for adding new entries. */
	private int[] currentNodePermutation = null;

	/** The backlog of clone elements not yet permuted. */
	private final List<CloneInstance> currentBacklog = new ArrayList<CloneInstance>();

	/** The number of reported clones (for statistics). */
	private int numReported = 0;

	/** Whether to remove overlaps using a greedy approach. */
	private final boolean removeOverlaps;

	/** Constructor. */
	public CloneClusterer(AugmentedModelGraph graph,
			ICloneReporter cloneReporter, IConQATLogger logger,
			boolean removeOverlaps) {
		this.graph = graph;
		this.cloneReporter = cloneReporter;
		this.logger = logger;
		this.removeOverlaps = removeOverlaps;
	}

	/**
	 * Inclusion analysis checks if a clone element A is already part of a
	 * larger element B. In this case the clones of B contain clones of A. These
	 * contained clones are then also added to A. While this is a polynomial
	 * time process, this can be potentially slow, so this is optional.
	 * <p>
	 * However to use it, call this method before {@link #performClustering()}.
	 */
	public void performInclusionAnalysis() {

		logger.info("Before inclusion analysis: " + cloneInstances.size()
				+ " clone instances");

		// we sort the larger instances to the front
		List<CloneInstanceEntry> entries = new ArrayList<CloneInstanceEntry>(
				cloneInstances.values());
		Collections.sort(entries, new CloneSizeComparator());

		// Iterate over all instances and see if we can find them in some larger
		// clone instance
		Set<Long> alreadyMerged = new HashSet<Long>();
		for (int smallCloneIndex = 0; smallCloneIndex < entries.size(); ++smallCloneIndex) {
			CloneInstanceEntry smallCloneEntry = entries.get(smallCloneIndex);

			// we only want to iterate over larger clone instances, as only they
			// can contain the small instance. As we sorted them before, these
			// are at the front!
			for (int largeCloneIndex = 0; largeCloneIndex < smallCloneIndex; ++largeCloneIndex) {
				CloneInstanceEntry largeCloneEntry = entries
						.get(largeCloneIndex);

				// if the large one does not include the smaller one, we are
				// already done
				if (!largeCloneEntry.cloneInstance
						.contains(smallCloneEntry.cloneInstance)) {
					continue;
				}

				// if we merged these before, do not do so again.
				if (alreadyMerged.contains(buildPairCode(smallCloneEntry,
						largeCloneEntry))) {
					continue;
				}

				int[] subCloneNodeIndices = largeCloneEntry.cloneInstance
						.extractSubCloneNodeIndices(smallCloneEntry.cloneInstance);

				// find those entries connected to the largeInstanceEntry
				List<CloneInstanceEntry> connectedEntries = new ArrayList<CloneInstanceEntry>();
				int largeUnionFindIndex = unionFind
						.find(largeCloneEntry.unionFindIndex);
				for (CloneInstanceEntry entry : cloneInstances.values()) {
					if (entry != largeCloneEntry
							&& largeUnionFindIndex == unionFind
									.find(entry.unionFindIndex)) {
						connectedEntries.add(entry);
					}
				}

				// now cut the corresponding pieces from the connectedEntries
				// and see if we have to add them to our clone instances (and
				// connect then in the union find structure).
				for (CloneInstanceEntry entry : connectedEntries) {
					CloneInstance subClone = entry.cloneInstance
							.extractSubClone(subCloneNodeIndices, graph);
					if (!cloneInstances.containsKey(subClone)) {
						int newIndex = unionFind.addElement();
						cloneInstances.put(subClone, new CloneInstanceEntry(
								subClone, newIndex));
						unionFind.union(newIndex,
								smallCloneEntry.unionFindIndex);
					}
				}

				// remember that we already merged these entries. As they are of
				// different size, this can not be stored in the union find
				// structure
				alreadyMerged.add(buildPairCode(smallCloneEntry,
						largeCloneEntry));
			}
		}

		logger.info("After inclusion analysis: " + cloneInstances.size()
				+ " elements");
	}

	/**
	 * Build the a unique code for the given pair, which is just a 64 bit value
	 * consisting of the indices of the union find equivalence class of both
	 * instance entries. The upper 32 bit contain the smaller index, which helps
	 * in making the code the same regardless of the order of parameters.
	 * <p>
	 * This is just a compact representation of pairs of
	 * {@link CloneInstanceEntry}s.
	 */
	private long buildPairCode(CloneInstanceEntry cee1, CloneInstanceEntry cee2) {
		long code1 = unionFind.find(cee1.unionFindIndex);
		long code2 = unionFind.find(cee2.unionFindIndex);
		if (code1 < code2) {
			code1 <<= 32;
		} else {
			code2 <<= 32;
		}
		return code1 | code2;
	}

	/** Runs the clustering algorithms and sends all clones to the reporter. */
	public void performClustering() {

		logger.info("Running clustering on " + numReported
				+ " reported clones.");

		// go through all unified clusters and join them, by creating a mapping
		// from the index of the union find equivalence class to the clone
		// instance.
		ListMap<Integer, CloneInstance> clustered = new ListMap<Integer, CloneInstance>();
		for (CloneInstanceEntry entry : cloneInstances.values()) {
			clustered.add(unionFind.find(entry.unionFindIndex),
					entry.cloneInstance);
		}

		// variables for collecting statistics
		int count = 0;
		int sumSize = 0;
		int sumNodes = 0;

		// iterate over clusters and report
		for (Integer key : clustered.getKeys()) {
			List<CloneInstance> cloneCluster = clustered.getCollection(key);
			if (removeOverlaps) {
				cloneCluster = extractNonOverlapping(cloneCluster);
			}

			// determine the edges to be reported
			@SuppressWarnings("unchecked")
			List<INode>[] nodes = new List[cloneCluster.size()];
			for (int i = 0; i < nodes.length; ++i) {
				nodes[i] = cloneCluster.get(i).getNodes();
			}
			List<IDirectedEdge>[] edges = graph.calculateEquivalentEdges(nodes);

			// update statistics
			++count;
			sumSize += nodes.length;
			sumNodes += nodes[0].size();

			// perform actual reporting
			cloneReporter.startModelCloneGroup(nodes.length, nodes[0].size(),
					edges[0].size());
			for (int i = 0; i < nodes.length; ++i) {
				cloneReporter.addModelCloneInstance(nodes[i], edges[i]);
			}
		}

		logger.info("Clustering resulted in " + count + " clones.");
		if (count > 0) {
			logger.info("Average clone size is " + sumSize / (double) count);
			logger.info("Average clone nodes is " + sumNodes / (double) count);
		}
	}

	/**
	 * Extracts non-overlapping clone elements from the given list in a greedy
	 * fashion.
	 */
	private List<CloneInstance> extractNonOverlapping(
			List<CloneInstance> elements) {
		List<CloneInstance> result = new ArrayList<CloneInstance>();
		Set<INode> nodesInClone = new IdentityHashSet<INode>();
		for (CloneInstance ce : elements) {
			boolean containsAny = false;
			for (INode n : ce.getNodes()) {
				if (nodesInClone.contains(n)) {
					containsAny = true;
					break;
				}
			}
			if (!containsAny) {
				nodesInClone.addAll(ce.getNodes());
				result.add(ce);
			}
		}
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public void startModelCloneGroup(int numClones, int numNodes, int numEdges) {
		lastUnionFindIndex = -1;
		currentNodePermutation = null;
		currentBacklog.clear();
		++numReported;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This method inserts the clone into the table of {@link #cloneInstances}.
	 * As we want all node lists of clones in the same clone class to be in the
	 * same order, we have to deal with permutations.
	 */
	@Override
	public void addModelCloneInstance(List<INode> nodes,
			List<IDirectedEdge> edges) {
		CloneInstance cloneInstance = new CloneInstance(nodes, graph);

		CloneInstanceEntry entry = cloneInstances.get(cloneInstance);
		if (entry == null) {
			// if this node set is not yet know, create a new clone instance
			// entry from it
			entry = new CloneInstanceEntry(cloneInstance,
					unionFind.addElement());
			cloneInstances.put(cloneInstance, entry);

			// if we have a current permutation, just apply it, otherwise store
			// it in the backlog so we can apply the permutation as soon as we
			// find one
			if (currentNodePermutation != null) {
				cloneInstance.applyPermutation(currentNodePermutation);
			} else {
				currentBacklog.add(cloneInstance);
			}
		} else if (currentNodePermutation == null) {
			// if a clone with the same node set exists, but we do not yet have
			// a permutation, now we have one.
			currentNodePermutation = CloneInstance.determinePermutation(
					cloneInstance.getNodes(), entry.cloneInstance.getNodes());

			// and to be sure, we should apply it to our backlog
			for (CloneInstance element : currentBacklog) {
				element.applyPermutation(currentNodePermutation);
			}
			currentBacklog.clear();
		} else {
			// if the entry exists and we also already have a permutation, we
			// have a problem as we might have to reorder an existing clone
			// class

			logger.error("Had clone pair which required reordering in current "
					+ "cluster. This case is not yet implemented and may "
					+ "lead to strange behavior in the output.");
		}

		// merge with previous clone in report sequence (if not first clone)
		if (lastUnionFindIndex >= 0) {
			unionFind.union(entry.unionFindIndex, lastUnionFindIndex);
		}
		lastUnionFindIndex = entry.unionFindIndex;
	}

	/** Class used for storing clone instances. */
	private static class CloneInstanceEntry {

		/** The contained clone instance. */
		public final CloneInstance cloneInstance;

		/** The index in {@link #unionFindIndex}. */
		public final int unionFindIndex;

		/** Constructor. */
		public CloneInstanceEntry(CloneInstance cloneInstance,
				int unionFindIndex) {
			this.cloneInstance = cloneInstance;
			this.unionFindIndex = unionFindIndex;
		}
	}

	/**
	 * Comparator for sorting the {@link CloneInstanceEntry}s with many nodes to
	 * the front.
	 */
	private static class CloneSizeComparator implements
			Comparator<CloneInstanceEntry> {

		/** {@inheritDoc} */
		@Override
		public int compare(CloneInstanceEntry cee1, CloneInstanceEntry cee2) {
			int size1 = cee1.cloneInstance.getNodes().size();
			int size2 = cee2.cloneInstance.getNodes().size();
			return size2 - size1;
		}
	}
}