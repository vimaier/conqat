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
package org.conqat.engine.commons.statistics;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.math.stat.descriptive.rank.Percentile;

import org.conqat.lib.commons.assertion.CCSMPre;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.traversal.TraversalUtils;
import org.conqat.engine.core.core.ConQATException;

/**
 * This class is used to rank metric values based on percentiles. To use this
 * class, instantiate it with the key that defines the metric, a weight and (if
 * needed) a second key that defines a normalization metric. The normalization
 * keys allows, for example, to normalize file-length-correlated metrics like "#
 * of issues" with the lines of code of a file.
 * 
 * To initialize the metric, call {@link #setUpRanks(IConQATNode, int)}. This
 * determines the upper bounds for each rank using percentiles. Subsequent calls
 * to {@link #determineWeightedRank(IConQATNode)} return the weighted rank of
 * the metric.
 * 
 * @author deissenb
 * @author $Author: hummelb $
 * @version $Rev: 37013 $
 * @ConQAT.Rating GREEN Hash: 941BC933F489319AEC522F35C098D01B
 */
/* package */class RankedMetric {

	/**
	 * Percentile instance used to calculate percentiles. This is defined here
	 * so multiple metrics can share the same instance.
	 */
	private final static Percentile PERCENTILE = new Percentile();

	/** Key the metric is stored at. */
	private final String metricReadKey;

	/**
	 * Key for normalization metric. If this is <code>null</code>, no
	 * normalization is performed.
	 */
	private final String normalizeReadKey;

	/**
	 * Key to store metric. If metric is not normalized, this is equal to
	 * {@link #metricReadKey}, otherwise it is "{@link #metricReadKey}/{@link #normalizeReadKey}".
	 */
	private final String metricWriteKey;

	/**
	 * Key used to store the rank of this metric. This is
	 * {@link #metricWriteKey}+"-Rank".
	 */
	private final String rankWriteKey;

	/** Weight of this metric. */
	private final double weight;

	/**
	 * Array that holds the upper limits for each rank, that is, the highest
	 * metric value that still fits into this rank.
	 */
	private double[] rankUpperLimits;

	/**
	 * Create new metric.
	 * 
	 * @param key
	 *            key the metric is stored at.
	 * @param normalizeKey
	 *            key for normalization metric. This is <code>null</code> if
	 *            no normalization is performed.
	 * @param weight
	 *            weight of this metric.
	 */
	public RankedMetric(String key, String normalizeKey, double weight) {
		this.metricReadKey = key;

		if (StringUtils.isEmpty(normalizeKey)) {
			this.normalizeReadKey = null;
			metricWriteKey = key;
		} else {
			this.normalizeReadKey = normalizeKey;
			metricWriteKey = key + "/" + normalizeKey;
		}

		rankWriteKey = metricWriteKey + "-Rank";
		this.weight = weight;
	}

	/**
	 * This method calculates the upper limits for each rank and and adds
	 * {@link #metricWriteKey} as well as {@link #rankWriteKey} to the display
	 * list of the root.
	 * 
	 * @param root
	 *            root node
	 * @param rankCount
	 *            the number of ranks to use
	 * @throws ConQATException
	 *             if a value is non-numeric
	 */
	public void setUpRanks(IConQATNode root, int rankCount)
			throws ConQATException {

		// if the metric is not normalized the metricWriteKey is equal to
		// metricReadKey. However, NodeUtils.addToDisplayList() ensures that
		// this exists only once in the display list
		NodeUtils.addToDisplayList(root, metricWriteKey);
		NodeUtils.addToDisplayList(root, rankWriteKey);

		rankUpperLimits = new double[rankCount - 1];

		// Array of all leaf values
		double[] values = getValues(root);

		double interval = 100.0 / rankCount;
		for (int i = 0; i < rankUpperLimits.length; i++) {
			double n = interval * (i + 1);
			rankUpperLimits[i] = PERCENTILE.evaluate(values, n);
		}
	}

	/**
	 * This determines all root values and, if necessary, takes care of
	 * normalization. The resulting array is sorted.
	 * 
	 * @throws ConQATException
	 *             if a non-numeric value was encountered.
	 */
	private double[] getValues(IConQATNode root) throws ConQATException {
		double[] values;
		if (normalizeReadKey != null) {
			values = getNormalizedLeaveValues(root);
		} else {
			values = TraversalUtils.getLeaveValues(root, metricReadKey);
		}
		Arrays.sort(values);
		return values;
	}

	/**
	 * For each leaf value determine value(metricReadKey)/value(normalizeKey).
	 * This also stores the normalized value for each leave at
	 * {@link #metricWriteKey}.
	 * 
	 * @param root
	 *            the root node.
	 * @return an array of normalized leave values.
	 * @throws ConQATException
	 *             if a metric value stored at a leaf node is non-numeric
	 */
	private double[] getNormalizedLeaveValues(IConQATNode root)
			throws ConQATException {
		List<IConQATNode> nodes = TraversalUtils.listLeavesDepthFirst(root);
		double[] values = new double[nodes.size()];

		for (int i = 0; i < nodes.size(); i++) {
			IConQATNode node = nodes.get(i);
			values[i] = getValue(node);
			node.setValue(metricWriteKey, values[i]);
		}

		return values;
	}

	/**
	 * Get value at node. If {@link #normalizeReadKey} is <code>null</code>,
	 * this returns value(metricReadKey). Otherwise this returns
	 * value(metricReadKey)/value(normalizeKey). If value(metricReadKey) is
	 * zero, this returns zero without evaluating the division. In the typical
	 * case of an empty file, this prevents division by zero exception as the
	 * value(metricReadKey) and value(normalizeKey) would both be zero. If, for
	 * some reason, value(metricReadKey) is unequal zero but value(normalizeKey)
	 * is zero, an exception is thrown. We do not go on with NaN values as the
	 * rest of the ranking algorithm would create unpredictable results for
	 * NaNs.
	 * 
	 * @param node
	 *            the node to get value from.
	 * @return the (potentially normalized) value
	 * @throws ConQATException
	 *             if one of the values is not numeric or a division by zero was
	 *             encountered.
	 */
	private double getValue(IConQATNode node) throws ConQATException {
		double value = NodeUtils.getDoubleValue(node, metricReadKey);

		if (normalizeReadKey == null) {
			return value;
		}

		// prevents division by zero for empty files
		if (value == 0) {
			return 0;
		}

		double normValue = NodeUtils.getDoubleValue(node, normalizeReadKey);

		if (normValue == 0) {
			throw new ConQATException("Division by zero at node "
					+ node.getId() + " for " + value + "/0");
		}

		return value / normValue;
	}

	/**
	 * For a node this reads the metric value and determines the weighted rank.
	 * This also stores the rank at key {@link #rankWriteKey}.
	 * 
	 * {@link #setUpRanks(IConQATNode, int)} must be called before this method
	 * can be used.
	 * 
	 * @return the rank of the metric at this node is a value >= 0 and <
	 *         rankCount
	 * @throws ConQATException
	 *             if a value is non-numeric
	 * @see #setUpRanks(IConQATNode, int)
	 */
	public double determineWeightedRank(IConQATNode node)
			throws ConQATException {
		CCSMPre.isFalse(rankUpperLimits == null,
				"Ranks must be set up before calling this method.");

		double value = getValue(node);

		int rank = Arrays.binarySearch(rankUpperLimits, value);
		if (rank < 0) {
			// value was not found in limits array
			rank = -(rank + 1);
		} else if (rank > 0) {
			// value was found in limits array

			// it may happen that the upper limits of multiple ranks are equal.
			// For example 6-ranking the values 0,1,1,1,3,8,50 results in the
			// following rank limits: 0.3333333333333335, 1.0, 1.0,
			// 4.66666666666667, 36.00000000000005, infty. Now a binary search
			// for 1.0 may return either of the two ranks, the binary search
			// function makes no guarantees for which one it returns. Hence, we
			// iterate towards the beginning of the array to always return the
			// first value (the lowest rank).
			for (int i = rank - 1; i >= 0; i--) {
				if (rankUpperLimits[i] == rankUpperLimits[rank]) {
					rank = i;
				}
			}
		}

		double result = weight * rank;
		node.setValue(rankWriteKey, result);

		return result;
	}

	/** This creates a string with the rank limits for the metric. */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Rank limits for metric ");

		builder.append(metricWriteKey);

		builder.append(": ");
		for (int i = 0; i < rankUpperLimits.length; i++) {
			builder.append(rankUpperLimits[i]);
			builder.append(", ");
		}
		builder.append("infty");
		return builder.toString();
	}

}