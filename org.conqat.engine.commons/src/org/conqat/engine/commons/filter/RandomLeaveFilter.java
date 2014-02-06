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
package org.conqat.engine.commons.filter;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.conqat.engine.commons.node.IRemovableConQATNode;
import org.conqat.engine.commons.sorting.NodeIdComparator;
import org.conqat.engine.commons.traversal.ETargetNodes;
import org.conqat.engine.commons.traversal.TraversalUtils;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.collections.IdentityHashSet;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 37013 $
 * @levd.rating GREEN Hash: EC5A060A2FBC0C5250E3E7BA1551E2E3
 */
@AConQATProcessor(description = "This filter randomly shuffles the leaf nodes and "
		+ "filters all but the specified number of nodes. This can be used, e.g., "
		+ "to create samples for manual reviews. Before shuffling the nodes are sorted"
		+ "according to their id. This ensures that if two complementary filters (one "
		+ "inverted and with the same fixed seed) are used to randomly split the files "
		+ "of a system into two disjoint parts works even in cases where the input for the "
		+ "filters are in different order.")
public class RandomLeaveFilter extends FilterBase<IRemovableConQATNode> {

	/** Leaves to be retained. */
	private final IdentityHashSet<IRemovableConQATNode> leavesToRetain = new IdentityHashSet<IRemovableConQATNode>();

	/** The seed for the random number generator used for shuffling. */
	private long seed = System.currentTimeMillis();

	/** Number of leaves to retain. */
	private int retainCount;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "retain-count", minOccurrences = 1, maxOccurrences = 1, description = "Number of leaves to retain.")
	public void setRetainCount(
			@AConQATAttribute(name = "value", description = "Number greater zero.") int retainCount)
			throws ConQATException {
		if (retainCount <= 0) {
			throw new ConQATException("Retain count must be greater zero.");
		}
		this.retainCount = retainCount;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "random-seed", maxOccurrences = 1, description = "Seed for the random number generator used for "
			+ "shuffling the leaves. If not specified the current time is used.")
	public void setRandomSeed(
			@AConQATAttribute(name = "value", description = "The initial seed") long seed) {
		this.seed = seed;
	}

	/** Prepare set {@link #leavesToRetain}. */
	@Override
	protected void preProcessInput(IRemovableConQATNode input) {
		getLogger().info("Seed used for random number generator: " + seed);
		List<IRemovableConQATNode> leaves = TraversalUtils
				.listLeavesDepthFirst(input);

		// sort before shuffling, so that 2 complementary filters (one inverted)
		// will yield disjoint sets even if input has different order
		Collections.sort(leaves, new NodeIdComparator());
		Collections.shuffle(leaves, new Random(seed));
		leavesToRetain.addAll(leaves.subList(0, Math.min(retainCount, leaves
				.size())));
	}

	/** {@inheritDoc} */
	@Override
	protected boolean isFiltered(IRemovableConQATNode node) {
		if (node.hasChildren()) {
			return false;
		}

		return !leavesToRetain.contains(node);
	}

	/** {@inheritDoc} */
	@Override
	protected ETargetNodes getTargetNodes() {
		return ETargetNodes.LEAVES;
	}
}