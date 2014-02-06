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

import java.util.ArrayList;

import org.conqat.lib.commons.math.EAggregationStrategy;
import org.conqat.lib.commons.math.MathUtils;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.traversal.ETargetNodes;
import org.conqat.engine.commons.traversal.NodeTraversingProcessorBase;
import org.conqat.engine.commons.traversal.TraversalUtils;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;

/**
 * {@ConQAT.Doc}
 * 
 * @author Florian Deissenboeck
 * @author $Author: hummelb $
 * @version $Rev: 37013 $
 * @ConQAT.Rating GREEN Hash: 7A13556BAADCF7DD23A8DDCD53A46EEC
 */
@AConQATProcessor(description = "This processor provides a rank-based aggregation of metric values. For each"
		+ "specified metric (stored at a key), a percentile-based ranking is performed."
		+ "Example: Given are 100 values stored in the input nodes. For 3 ranks, the"
		+ "processor will determine the 33rd and the 66th percentile. Depending on the"
		+ "input value distribution, the bounds could eg. be 34 and 54. Based on this,"
		+ "it defines 3 buckets: ]-INFTY..34], ]34..54], ]54..INFTY[ where the first"
		+ "bucket has rank 0, the second rank 1 and the third rank 2. For each value it"
		+ "determines the bucket and stores the corresponding rank. E.g., value 15 is"
		+ "ranked 0, value 50 is ranked 1 and value 100 is ranked 2. In addition,"
		+ "metrics may be normalized (divided by) another metric. For example, metrics"
		+ "can be normalized by LOC to reduce the impact of file size. To aggregate the"
		+ "values of the different metrics, a strategy, e.g. summation can be defined."
		+ "The result is called the total rank. The influence of a metric on the total"
		+ "rank can be adjusted by assigning weights to the metrics. These weights are"
		+ "multiplied with the metric ranks before aggregating the total rank.")
public class MetricRanker extends NodeTraversingProcessorBase<IConQATNode> {

	/** Key to write. */
	private String writeKey;

	/** The list of metrics. */
	private final ArrayList<RankedMetric> metrics = new ArrayList<RankedMetric>();

	/** The number of ranks to use. */
	private int rankCount;

	/** The aggregation strategy used to calculate the total rank. */
	private EAggregationStrategy strategy = EAggregationStrategy.SUM;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "metric", minOccurrences = 1, description = "Add a metric for ranking.")
	public void addMetric(
			@AConQATAttribute(name = "key", description = "Key where metric is stored.") String key,
			@AConQATAttribute(name = "normalize", description = "Key to normalize metric with.", defaultValue = StringUtils.EMPTY_STRING) String normalizeKey,
			@AConQATAttribute(name = "weight", description = "Weight of this metric for total rank.", defaultValue = "1.0") double weight) {

		metrics.add(new RankedMetric(key, normalizeKey, weight));
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = ConQATParamDoc.WRITEKEY_NAME, minOccurrences = 1, maxOccurrences = 1, description = ""
			+ "The key for storing the total rank.")
	public void setWriteKey(
			@AConQATAttribute(name = ConQATParamDoc.WRITEKEY_KEY_NAME, description = ConQATParamDoc.WRITEKEY_KEY_DESC) String writeKey) {
		this.writeKey = writeKey;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "ranks", minOccurrences = 1, maxOccurrences = 1, description = ""
			+ "Specify number of ranks.")
	public void setRanks(
			@AConQATAttribute(name = "value", description = "Number of ranks "
					+ "(must be > 0)") int rankCount) throws ConQATException {
		if (rankCount < 1) {
			throw new ConQATException("Rank must be greater 0.");
		}
		this.rankCount = rankCount;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = ConQATParamDoc.AGG_STRATEGY_NAME, minOccurrences = 0, maxOccurrences = 1, description = ConQATParamDoc.AGG_STRATEGY_DESC
			+ " Default is summation.")
	public void setAggregationStrategy(
			@AConQATAttribute(name = ConQATParamDoc.STRATEGY_NAME, description = ConQATParamDoc.STRATEGY_DESC) EAggregationStrategy strategy) {
		this.strategy = strategy;
	}

	/** {@inheritDoc} */
	@Override
	protected ETargetNodes getTargetNodes() {
		return ETargetNodes.LEAVES;
	}

	/** Set up ranks. */
	@Override
	protected void setUp(IConQATNode root) throws ConQATException {
		int valueCount = TraversalUtils.countLeaves(root);

		if (valueCount < rankCount) {
			getLogger().warn(
					"You are using " + rankCount + " ranks for " + valueCount
							+ " values.");
		}

		// set up ranks for each metric
		for (RankedMetric metric : metrics) {
			metric.setUpRanks(root, rankCount);
			getLogger().debug(metric);
		}

		NodeUtils.addToDisplayList(root, writeKey);
	}

	/**
	 * Determine rank for each metric, calculate total rank and store it at
	 * node.
	 */
	@Override
	public void visit(IConQATNode node) throws ConQATException {
		ArrayList<Double> ranks = new ArrayList<Double>(metrics.size());

		for (RankedMetric metric : metrics) {
			ranks.add(metric.determineWeightedRank(node));
		}

		double totalRank = MathUtils.aggregate(ranks, strategy);
		node.setValue(writeKey, totalRank);
	}
}